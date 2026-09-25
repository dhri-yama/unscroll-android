package com.unscroll.app.service.accessibility

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import com.unscroll.app.UnscrollApplication
import com.unscroll.app.domain.usecase.TrackScrollEventUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScrollTrackerAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
    private var trackedPackages: Set<String> = emptySet()
    private var screenHeightPx: Int = 2340

    private lateinit var trackScrollEventUseCase: TrackScrollEventUseCase
    private lateinit var distanceCalculator: ScrollEventDistanceCalculator
    private lateinit var gestureAccumulator: ScrollGestureAccumulator
    private val gestureFlushJobs = mutableMapOf<String, Job>()

    override fun onCreate() {
        super.onCreate()
        val appContainer = (application as UnscrollApplication).container
        trackScrollEventUseCase = appContainer.trackScrollEventUseCase

        val displayMetrics = DisplayMetrics()
        val windowManager = getSystemService(WINDOW_SERVICE) as? WindowManager
        @Suppress("DEPRECATION")
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        screenHeightPx = displayMetrics.heightPixels
        distanceCalculator = ScrollEventDistanceCalculator(
            estimatedItemHeightPx = (screenHeightPx * 0.40f).toLong()
        )
        gestureAccumulator = ScrollGestureAccumulator(GESTURE_IDLE_MS)

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

        val sourceIdentity = event.className?.toString()?.takeIf { it.isNotBlank() }
            ?: event.source?.hashCode()?.toString()
            ?: "scroller"
        val sourceKey = "$packageName:${event.windowId}:$sourceIdentity"
        val deltaPx = distanceCalculator.calculate(
            ScrollEventSnapshot(
                sourceKey = sourceKey,
                scrollY = event.scrollY,
                scrollDeltaY = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    event.scrollDeltaY
                } else {
                    0
                },
                currentItemIndex = event.currentItemIndex,
                fromIndex = event.fromIndex
            )
        )
        if (deltaPx <= 0L) return

        val eventTimestamp = event.eventTime.takeIf { it > 0L } ?: SystemClock.uptimeMillis()
        gestureFlushJobs.remove(sourceKey)?.cancel()
        gestureAccumulator.add(
            sourceKey = sourceKey,
            distancePx = deltaPx,
            timestampMillis = eventTimestamp
        )?.let { gesture -> recordGesture(gesture) }
        gestureFlushJobs[sourceKey] = serviceScope.launch {
            delay(GESTURE_IDLE_MS)
            completeGesture(sourceKey)
        }
    }

    private fun completeGesture(sourceKey: String) {
        gestureFlushJobs.remove(sourceKey)?.cancel()
        gestureAccumulator.complete(sourceKey)?.let { gesture -> recordGesture(gesture) }
    }

    private fun recordGesture(gesture: ScrollGesture) {
        trackScrollEventUseCase(
            deltaPx = gesture.distancePx,
            screenHeightPx = screenHeightPx,
            timestampMillis = gesture.lastEventTimestampMillis
        )
    }

    private fun flushPendingGestures() {
        gestureFlushJobs.values.forEach { it.cancel() }
        gestureFlushJobs.clear()
        if (::gestureAccumulator.isInitialized) {
            gestureAccumulator.completeAll().forEach { gesture -> recordGesture(gesture) }
        }
    }

    override fun onInterrupt() {
        flushPendingGestures()
        if (::distanceCalculator.isInitialized) {
            distanceCalculator.clear()
        }
    }

    override fun onDestroy() {
        flushPendingGestures()
        if (::distanceCalculator.isInitialized) {
            distanceCalculator.clear()
        }
        serviceScope.cancel()
        super.onDestroy()
    }

    private companion object {
        const val GESTURE_IDLE_MS = 450L
    }
}
