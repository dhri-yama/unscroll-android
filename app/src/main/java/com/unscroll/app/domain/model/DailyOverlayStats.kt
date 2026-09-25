package com.unscroll.app.domain.model

/**
 * Overlay response counts for a single calendar day.
 *
 * [epochDay] is [java.time.LocalDate.toEpochDay] so entries sort and range-query
 * as plain numbers, and so the value can be used directly as part of a
 * DataStore preference key.
 */
data class DailyOverlayStats(
    val epochDay: Long,
    val skipCount: Int = 0,
    val lockCount: Int = 0
) {
    val totalCount: Int get() = skipCount + lockCount

    fun countFor(response: OverlayResponse): Int = when (response) {
        OverlayResponse.SKIP -> skipCount
        OverlayResponse.LOCK -> lockCount
    }

    fun plus(response: OverlayResponse): DailyOverlayStats = when (response) {
        OverlayResponse.SKIP -> copy(skipCount = skipCount + 1)
        OverlayResponse.LOCK -> copy(lockCount = lockCount + 1)
    }
}
