package com.unscroll.app.service.accessibility

import android.accessibilityservice.AccessibilityService
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import com.unscroll.app.UnscrollApplication
import com.unscroll.app.domain.usecase.TrackScrollEventUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ScrollTrackerAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var trackedPackages: Set<String> = emptySet()
    private var screenHeightPx: Int = 2340

    private lateinit var trackScrollEventUseCase: TrackScrollEventUseCase

    override fun onCreate() {
        super.onCreate()
        val appContainer = (application as UnscrollApplication).container
        trackScrollEventUseCase = appContainer.trackScrollEventUseCase

        val displayMetrics = DisplayMetrics()
        val windowManager = getSystemService(WINDOW_SERVICE) as? WindowManager
        @Suppress("DEPRECATION")
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        screenHeightPx = displayMetrics.heightPixels

        serviceScope.launch {
            appContainer.settingsRepository.getTrackedPackageNames().collect { packages ->
                trackedPackages = packages
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_VIEW_SCROLLED) return

        val packageName = event.packageName?.toString() ?: return
        if (!trackedPackages.contains(packageName)) return

        val deltaY = Math.abs(event.scrollY - event.fromIndex)
        val estimatedDeltaPx = if (deltaY > 0) deltaY.toLong() else (screenHeightPx * 0.42f).toLong()

        trackScrollEventUseCase(
            deltaPx = estimatedDeltaPx,
            screenHeightPx = screenHeightPx,
            timestampMillis = System.currentTimeMillis()
        )
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
