package com.unscroll.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.unscroll.app.domain.model.DailyOverlayStats
import com.unscroll.app.domain.model.OverlayResponse
import com.unscroll.app.domain.repository.DailyStatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private const val SKIP_PREFIX = "skip_"
private const val LOCK_PREFIX = "lock_"

private val Context.dailyStatsStore: DataStore<Preferences> by
    preferencesDataStore(name = "unscroll_daily_stats")

/**
 * Persists overlay response counts as one pair of integer preferences per day
 * (`skip_<epochDay>`, `lock_<epochDay>`).
 *
 * Kept in its own DataStore rather than sharing `unscroll_settings` so the
 * high-churn daily writes do not contend with low-churn settings on the same
 * file lock.
 */
class DataStoreDailyStatsRepository(
    private val context: Context
) : DailyStatsRepository {

    override fun observeDailyStats(rangeDays: Int): Flow<List<DailyOverlayStats>> {
        val sanitized = sanitizeRangeDays(rangeDays)
        return context.dailyStatsStore.data.map { preferences ->
            buildDailySeries(
                sparse = parseSparse(preferences),
                endEpochDay = LocalDate.now().toEpochDay(),
                rangeDays = sanitized
            )
        }
    }

    override fun observeResponseTotal(response: OverlayResponse, rangeDays: Int): Flow<Int> =
        observeDailyStats(rangeDays).map { series -> totalFor(series, response) }

    override suspend fun recordResponse(response: OverlayResponse, day: LocalDate) {
        val epochDay = day.toEpochDay()
        context.dailyStatsStore.edit { preferences ->
            val key = intPreferencesKey(prefixFor(response) + epochDay)
            preferences[key] = (preferences[key] ?: 0) + 1
            preferences.removeStaleDays(retentionCutoff(LocalDate.now()))
        }
    }

    override suspend fun clearAll() {
        context.dailyStatsStore.edit { preferences ->
            preferences.asMap().keys
                .filter { it.name.isDailyStatsKey() }
                .forEach { preferences.remove(intPreferencesKey(it.name)) }
        }
    }

    private fun prefixFor(response: OverlayResponse): String = when (response) {
        OverlayResponse.SKIP -> SKIP_PREFIX
        OverlayResponse.LOCK -> LOCK_PREFIX
    }

    private fun String.isDailyStatsKey(): Boolean =
        startsWith(SKIP_PREFIX) || startsWith(LOCK_PREFIX)

    private fun epochDayFromKeyName(name: String): Long? =
        name.substringAfter('_').toLongOrNull()

    private fun MutablePreferences.removeStaleDays(cutoffEpochDay: Long) {
        val stale = staleEpochDays(
            knownEpochDays = asMap().keys.mapNotNull { epochDayFromKeyName(it.name) },
            cutoffEpochDay = cutoffEpochDay
        )
        stale.forEach { epochDay ->
            remove(intPreferencesKey(SKIP_PREFIX + epochDay))
            remove(intPreferencesKey(LOCK_PREFIX + epochDay))
        }
    }

    private fun parseSparse(preferences: Preferences): Map<Long, DailyOverlayStats> {
        val skips = mutableMapOf<Long, Int>()
        val locks = mutableMapOf<Long, Int>()

        preferences.asMap().forEach { (key, value) ->
            val name = key.name
            if (!name.isDailyStatsKey()) return@forEach
            val count = value as? Int ?: return@forEach
            val epochDay = epochDayFromKeyName(name) ?: return@forEach
            if (name.startsWith(SKIP_PREFIX)) {
                skips[epochDay] = count
            } else {
                locks[epochDay] = count
            }
        }

        return (skips.keys + locks.keys).associateWith { epochDay ->
            DailyOverlayStats(
                epochDay = epochDay,
                skipCount = skips[epochDay] ?: 0,
                lockCount = locks[epochDay] ?: 0
            )
        }
    }
}
