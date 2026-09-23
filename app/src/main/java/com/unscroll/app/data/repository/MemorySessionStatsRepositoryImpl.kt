package com.unscroll.app.data.repository

import com.unscroll.app.domain.model.SessionStats
import com.unscroll.app.domain.repository.SessionStatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MemorySessionStatsRepositoryImpl : SessionStatsRepository {

    private val _sessionStatsState = MutableStateFlow(SessionStats())
    override val sessionStatsState: StateFlow<SessionStats> = _sessionStatsState.asStateFlow()

    override fun recordScroll(deltaPx: Long, isReelAdvance: Boolean) {
        if (deltaPx <= 0) return
        _sessionStatsState.update { current ->
            current.copy(
                totalScrollsCount = current.totalScrollsCount + 1,
                totalScrollDistancePx = current.totalScrollDistancePx + deltaPx,
                estimatedReelsCount = if (isReelAdvance) current.estimatedReelsCount + 1 else current.estimatedReelsCount
            )
        }
    }

    override fun updateTimeSpent(activeTimeMillis: Long) {
        _sessionStatsState.update { current ->
            current.copy(totalTimeSpentMillis = activeTimeMillis)
        }
    }

    override fun updateLastOverlayTriggered(timestampMillis: Long) {
        _sessionStatsState.update { current ->
            current.copy(lastOverlayTriggeredMillis = timestampMillis)
        }
    }

    override fun resetSession() {
        _sessionStatsState.value = SessionStats(sessionStartMillis = System.currentTimeMillis())
    }
}
