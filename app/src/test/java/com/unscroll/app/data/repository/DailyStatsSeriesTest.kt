package com.unscroll.app.data.repository

import com.unscroll.app.domain.model.DailyOverlayStats
import com.unscroll.app.domain.model.OverlayResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DailyStatsSeriesTest {

    private val today: Long = LocalDate.of(2026, 3, 15).toEpochDay()

    @Test
    fun epochDayRangeIsDenseAscendingAndEndsToday() {
        val range = epochDayRange(today, 7)

        assertEquals(7, range.size)
        assertEquals(today - 6L, range.first())
        assertEquals(today, range.last())
        assertEquals(range.sorted(), range)
        assertEquals(range.distinct().size, range.size)
    }

    @Test
    fun singleDayRangeIsJustToday() {
        assertEquals(listOf(today), epochDayRange(today, 1))
    }

    @Test
    fun rangeIsClampedToSupportedBounds() {
        assertEquals(MIN_RANGE_DAYS, sanitizeRangeDays(0))
        assertEquals(MIN_RANGE_DAYS, sanitizeRangeDays(-5))
        assertEquals(MAX_RANGE_DAYS, sanitizeRangeDays(MAX_RANGE_DAYS + 1))
        assertEquals(MAX_RANGE_DAYS, sanitizeRangeDays(9_999))
    }

    @Test
    fun buildDailySeriesFillsMissingDaysWithZeroes() {
        val series = buildDailySeries(
            sparse = mapOf(
                today to DailyOverlayStats(epochDay = today, skipCount = 3, lockCount = 1)
            ),
            endEpochDay = today,
            rangeDays = 7
        )

        assertEquals(7, series.size)
        assertEquals(1, series.count { it.totalCount > 0 })
        assertEquals(3, series.last().skipCount)
        assertEquals(1, series.last().lockCount)
        series.dropLast(1).forEach { day ->
            assertEquals(0, day.skipCount)
            assertEquals(0, day.lockCount)
        }
    }

    @Test
    fun buildDailySeriesDropsDaysOutsideTheRange() {
        val series = buildDailySeries(
            sparse = mapOf(
                today to DailyOverlayStats(epochDay = today, skipCount = 1),
                today - 40L to DailyOverlayStats(epochDay = today - 40L, skipCount = 9),
                today + 5L to DailyOverlayStats(epochDay = today + 5L, skipCount = 9)
            ),
            endEpochDay = today,
            rangeDays = 7
        )

        assertEquals(7, series.size)
        assertEquals(1, series.sumOf { it.skipCount })
        assertTrue(series.none { it.epochDay > today })
    }

    @Test
    fun buildDailySeriesWithNoDataIsAllZeroes() {
        val series = buildDailySeries(sparse = emptyMap(), endEpochDay = today, rangeDays = 30)

        assertEquals(30, series.size)
        assertTrue(series.all { it.totalCount == 0 })
    }

    @Test
    fun totalForCountsOnlyTheRequestedResponse() {
        val series = listOf(
            DailyOverlayStats(epochDay = today - 1, skipCount = 2, lockCount = 5),
            DailyOverlayStats(epochDay = today, skipCount = 3, lockCount = 1)
        )

        assertEquals(5, totalFor(series, OverlayResponse.SKIP))
        assertEquals(6, totalFor(series, OverlayResponse.LOCK))
    }

    @Test
    fun plusIncrementsOnlyTheTargetedCounter() {
        val base = DailyOverlayStats(epochDay = today, skipCount = 4, lockCount = 7)

        val afterSkip = base.plus(OverlayResponse.SKIP)
        assertEquals(5, afterSkip.skipCount)
        assertEquals(7, afterSkip.lockCount)

        val afterLock = base.plus(OverlayResponse.LOCK)
        assertEquals(4, afterLock.skipCount)
        assertEquals(8, afterLock.lockCount)
    }

    @Test
    fun countForReadsTheMatchingCounter() {
        val day = DailyOverlayStats(epochDay = today, skipCount = 8, lockCount = 2)

        assertEquals(8, day.countFor(OverlayResponse.SKIP))
        assertEquals(2, day.countFor(OverlayResponse.LOCK))
    }

    @Test
    fun staleEpochDaysKeepsOnlyDaysBeforeTheCutoff() {
        val known = listOf(today - 400L, today - 30L, today, today + 1L)

        val stale = staleEpochDays(known, cutoffEpochDay = today - 365L)

        assertEquals(listOf(today - 400L), stale)
    }

    @Test
    fun retentionCutoffSitsTheConfiguredDistanceInThePast() {
        val cutoff = retentionCutoff(LocalDate.of(2026, 3, 15))

        assertEquals(LocalDate.of(2026, 3, 15).minusDays(RETENTION_DAYS).toEpochDay(), cutoff)
        assertTrue(cutoff < today)
    }

    @Test
    fun retentionCoversTheWidestSelectableRange() {
        assertTrue(RETENTION_DAYS >= MAX_RANGE_DAYS.toLong())
    }
}
