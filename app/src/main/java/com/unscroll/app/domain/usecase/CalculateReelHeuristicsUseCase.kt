package com.unscroll.app.domain.usecase

class CalculateReelHeuristicsUseCase {
    private var lastReelTimestamp: Long = 0L

    fun isReelAdvance(deltaPx: Long, screenHeightPx: Int, timestampMillis: Long): Boolean {
        if (screenHeightPx <= 0) return false
        val thresholdPx = (screenHeightPx * 0.40f).toLong()
        val isSignificantSwipe = deltaPx >= thresholdPx
        val isCooldownPassed = (timestampMillis - lastReelTimestamp) >= 400L

        if (isSignificantSwipe && isCooldownPassed) {
            lastReelTimestamp = timestampMillis
            return true
        }
        return false
    }
}
