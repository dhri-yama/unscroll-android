package com.unscroll.app.domain.repository

import com.unscroll.app.domain.model.DailyOverlayStats
import com.unscroll.app.domain.model.OverlayResponse
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface DailyStatsRepository {
    /**
     * Daily totals for the [rangeDays] days ending on the current day, oldest
     * first. Days with no recorded response are present with zero counts so
     * callers always receive a dense series to plot.
     */
    fun observeDailyStats(rangeDays: Int): Flow<List<DailyOverlayStats>>

    /** Total responses recorded for [response] within the trailing [rangeDays] days. */
    fun observeResponseTotal(response: OverlayResponse, rangeDays: Int): Flow<Int>

    suspend fun recordResponse(response: OverlayResponse, day: LocalDate = LocalDate.now())

    suspend fun clearAll()
}
