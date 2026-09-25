package com.unscroll.app.domain.model

data class SessionStats(
    val sessionStartMillis: Long = System.currentTimeMillis(),
    val totalTimeSpentMillis: Long = 0L,
    val totalScrollsCount: Long = 0L,
    val totalScrollDistancePx: Long = 0L,
    val estimatedReelsCount: Long = 0L,
    val lastOverlayTriggeredMillis: Long = 0L,
    val isActive: Boolean = true,
    val sessionId: Long = 0L
) {
    fun getScrollDistanceMeters(densityDpi: Int = 420): Float {
        if (densityDpi <= 0) return 0f
        val inches = totalScrollDistancePx.toFloat() / densityDpi.toFloat()
        return inches * 0.0254f
    }
}
