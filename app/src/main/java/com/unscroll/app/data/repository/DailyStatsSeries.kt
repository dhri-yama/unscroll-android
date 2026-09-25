package com.unscroll.app.data.repository

import com.unscroll.app.domain.model.DailyOverlayStats
import com.unscroll.app.domain.model.OverlayResponse
import java.time.LocalDate

internal const val MIN_RANGE_DAYS = 1
internal const val MAX_RANGE_DAYS = 90

/**
 * Days of history kept on disk. Deliberately longer than [MAX_RANGE_DAYS] so the
 * range picker can be widened later without losing data.
 */
internal const val RETENTION_DAYS = 365L

internal fun sanitizeRangeDays(rangeDays: Int): Int =
    rangeDays.coerceIn(MIN_RANGE_DAYS, MAX_RANGE_DAYS)

/** Epoch days for the [rangeDays] days ending at [endEpochDay], oldest first. */
internal fun epochDayRange(endEpochDay: Long, rangeDays: Int): List<Long> {
    val days = sanitizeRangeDays(rangeDays)
    val firstEpochDay = endEpochDay - (days - 1L)
    return List(days) { index -> firstEpochDay + index }
}

/**
 * Expands a sparse map of recorded days into a dense ascending series covering
 * the trailing range, so charts plot a zero bar for days with no activity rather
 * than collapsing them and misrepresenting the timeline.
 */
internal fun buildDailySeries(
    sparse: Map<Long, DailyOverlayStats>,
    endEpochDay: Long,
    rangeDays: Int
): List<DailyOverlayStats> = epochDayRange(endEpochDay, rangeDays).map { epochDay ->
    sparse[epochDay] ?: DailyOverlayStats(epochDay = epochDay)
}

internal fun totalFor(series: List<DailyOverlayStats>, response: OverlayResponse): Int =
    series.sumOf { it.countFor(response) }

internal fun retentionCutoff(today: LocalDate): Long =
    today.minusDays(RETENTION_DAYS).toEpochDay()

/** Epoch days in [knownEpochDays] that fall before [cutoffEpochDay]. */
internal fun staleEpochDays(knownEpochDays: Collection<Long>, cutoffEpochDay: Long): List<Long> =
    knownEpochDays.filter { it < cutoffEpochDay }
