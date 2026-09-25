package com.unscroll.app.service.monitor

import android.app.KeyguardManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.unscroll.app.R
import com.unscroll.app.UnscrollApplication
import com.unscroll.app.di.AppContainer
import com.unscroll.app.domain.repository.DailyStatsRepository
import com.unscroll.app.domain.repository.SessionStatsRepository
import com.unscroll.app.domain.usecase.EvaluateOverlayTriggerUseCase
import com.unscroll.app.domain.usecase.GetNextInsultUseCase
import com.unscroll.app.service.overlay.WindowManagerOverlayController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SessionMonitorForegroundService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private lateinit var appContainer: AppContainer
    private lateinit var sessionStatsRepository: SessionStatsRepository
    private lateinit var overlayController: WindowManagerOverlayController
    private lateinit var getNextInsultUseCase: GetNextInsultUseCase
    private lateinit var evaluateOverlayTriggerUseCase: EvaluateOverlayTriggerUseCase
    private lateinit var dailyStatsRepository: DailyStatsRepository
    private var screenStateReceiver: BroadcastReceiver? = null

    companion object {
        private const val TAG = "SessionMonitor"
        private const val CHANNEL_ID = "unscroll_monitor_channel"
        private const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
            val intent = Intent(context, SessionMonitorForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun startSafely(context: Context) {
            try {
                start(context)
            } catch (error: Exception) {
                Log.w(TAG, "Unable to start monitoring service", error)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, SessionMonitorForegroundService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        appContainer = (application as UnscrollApplication).container
        sessionStatsRepository = appContainer.sessionStatsRepository
        getNextInsultUseCase = appContainer.getNextInsultUseCase
        evaluateOverlayTriggerUseCase = appContainer.evaluateOverlayTriggerUseCase
        dailyStatsRepository = appContainer.dailyStatsRepository
        overlayController = WindowManagerOverlayController(this)

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
        registerScreenStateReceiver()
        reconcileSessionWithScreenState()
        startMonitoringLoop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        reconcileSessionWithScreenState()
        return START_STICKY
    }

    private fun registerScreenStateReceiver() {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent?.action
                if (action == Intent.ACTION_SCREEN_OFF) {
                    lockSession()
                } else {
                    reconcileSessionWithScreenState()
                }
            }
        }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        screenStateReceiver = receiver
        ContextCompat.registerReceiver(this, receiver, filter, ContextCompat.RECEIVER_EXPORTED)
    }

    private fun reconcileSessionWithScreenState() {
        when (sessionStatsRepository.resolveSessionScreenAction(currentScreenState())) {
            SessionScreenAction.Begin -> beginSession()
            SessionScreenAction.End -> endSession()
            SessionScreenAction.None -> Unit
        }
    }

    private fun beginSession() {
        sessionStatsRepository.beginSession()
        appContainer.calculateReelHeuristicsUseCase.reset()
    }

    private fun endSession() {
        sessionStatsRepository.endSession()
        appContainer.calculateReelHeuristicsUseCase.reset()
        overlayController.dismissOverlay()
    }

    private fun lockSession() {
        endSession()
    }

    private fun currentScreenState(): DeviceScreenState {
        val powerManager = getSystemService(PowerManager::class.java)
        val keyguardManager = getSystemService(KeyguardManager::class.java)
        return DeviceScreenState(
            isInteractive = powerManager?.isInteractive == true,
            isDeviceLocked = keyguardManager?.isDeviceLocked == true
        )
    }

    private fun startMonitoringLoop() {
        serviceScope.launch {
            while (true) {
                try {
                    reconcileSessionWithScreenState()
                    val profile = appContainer.settingsRepository.getUserProfile().first()
                    val stats = sessionStatsRepository.sessionStatsState.value

                    if (stats.isActive) {
                        val now = System.currentTimeMillis()
                        val activeTime = (now - stats.sessionStartMillis).coerceAtLeast(0L)
                        sessionStatsRepository.updateTimeSpent(activeTime, stats.sessionId)

                        val updatedStats = sessionStatsRepository.sessionStatsState.value
                        if (updatedStats.isActive && updatedStats.sessionId == stats.sessionId) {
                            val shouldTrigger = evaluateOverlayTriggerUseCase(
                                stats = updatedStats,
                                intervalMinutes = profile.interruptionIntervalMinutes,
                                currentTimeMillis = now
                            )

                            if (shouldTrigger && !overlayController.isOverlayShowing()) {
                                val insult = getNextInsultUseCase()
                                sessionStatsRepository.updateLastOverlayTriggered(now, updatedStats.sessionId)

                                launch(Dispatchers.Main) {
                                    val latestStats = sessionStatsRepository.sessionStatsState.value
                                    if (latestStats.isActive &&
                                        latestStats.sessionId == updatedStats.sessionId &&
                                        !overlayController.isOverlayShowing()
                                    ) {
                                        overlayController.showOverlay(
                                            stats = updatedStats,
                                            insult = insult,
                                            onDismiss = { response ->
                                                serviceScope.launch {
                                                    dailyStatsRepository.recordResponse(response)
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(1_000L)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "UNSCROLL // SIGNAL WATCH",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors scroll telemetry and deploys intervention protocols"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("UNSCROLL // SIGNAL WATCH")
            .setContentText("Monitoring scroll loops // awaiting operator")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        screenStateReceiver?.let { receiver ->
            runCatching { unregisterReceiver(receiver) }
        }
        screenStateReceiver = null
        serviceScope.cancel()
        overlayController.dismissOverlay()
        super.onDestroy()
    }
}
