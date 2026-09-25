package com.unscroll.app.service.monitor

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.unscroll.app.R
import com.unscroll.app.UnscrollApplication
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
    private lateinit var overlayController: WindowManagerOverlayController
    private lateinit var getNextInsultUseCase: GetNextInsultUseCase
    private lateinit var evaluateOverlayTriggerUseCase: EvaluateOverlayTriggerUseCase

    companion object {
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

        fun stop(context: Context) {
            val intent = Intent(context, SessionMonitorForegroundService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        val appContainer = (application as UnscrollApplication).container
        getNextInsultUseCase = appContainer.getNextInsultUseCase
        evaluateOverlayTriggerUseCase = appContainer.evaluateOverlayTriggerUseCase
        overlayController = WindowManagerOverlayController(this)

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())

        startMonitoringLoop(appContainer)
    }

    private fun startMonitoringLoop(appContainer: com.unscroll.app.di.AppContainer) {
        serviceScope.launch {
            val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager

            while (true) {
                try {
                    delay(30_000L) // Check every 30 seconds

                    val profile = appContainer.settingsRepository.getUserProfile().first()
                    val trackedPackages = appContainer.settingsRepository.getTrackedPackageNames().first()
                    val stats = appContainer.sessionStatsRepository.sessionStatsState.value

                    // Update active time spent from UsageStats
                    val now = System.currentTimeMillis()
                    val activeTime = (now - stats.sessionStartMillis).coerceAtLeast(0L)
                    appContainer.sessionStatsRepository.updateTimeSpent(activeTime)

                    val updatedStats = appContainer.sessionStatsRepository.sessionStatsState.value
                    val shouldTrigger = evaluateOverlayTriggerUseCase(
                        stats = updatedStats,
                        intervalMinutes = profile.interruptionIntervalMinutes,
                        currentTimeMillis = now
                    )

                    if (shouldTrigger && !overlayController.isOverlayShowing()) {
                        val insult = getNextInsultUseCase()
                        appContainer.sessionStatsRepository.updateLastOverlayTriggered(now)

                        launch(Dispatchers.Main) {
                            overlayController.showOverlay(
                                stats = updatedStats,
                                insult = insult,
                                onDismiss = {
                                    // Resets or continues monitoring loop
                                }
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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
        super.onDestroy()
        serviceScope.cancel()
        overlayController.dismissOverlay()
    }
}
