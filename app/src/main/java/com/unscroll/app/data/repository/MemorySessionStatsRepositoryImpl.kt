package com.unscroll.app.data.repository

import com.unscroll.app.domain.model.SessionStats
import com.unscroll.app.domain.repository.SessionStatsRepository
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MemorySessionStatsRepositoryImpl : SessionStatsRepository {

    private val _sessionStatsState = MutableStateFlow(SessionStats())
    private val nextSessionId = AtomicLong(0L)

    override val sessionStatsState: StateFlow<SessionStats> = _sessionStatsState.asStateFlow()

    override fun recordScroll(deltaPx: Long, isReelAdvance: Boolean, sessionId: Long?) {
        if (deltaPx <= 0) return
        _sessionStatsState.update { current ->
            if (!current.isActive || (sessionId != null && sessionId != current.sessionId)) {
                current
            } else {
                current.copy(
                    totalScrollsCount = current.totalScrollsCount + 1,
                    totalScrollDistancePx = current.totalScrollDistancePx + deltaPx,
                    estimatedReelsCount = if (isReelAdvance) current.estimatedReelsCount + 1 else current.estimatedReelsCount
                )
            }
        }
    }

    override fun updateTimeSpent(activeTimeMillis: Long, sessionId: Long?) {
        _sessionStatsState.update { current ->
            if (!current.isActive || (sessionId != null && sessionId != current.sessionId)) {
                current
            } else {
                current.copy(totalTimeSpentMillis = activeTimeMillis.coerceAtLeast(0L))
            }
        }
    }

    override fun updateLastOverlayTriggered(timestampMillis: Long, sessionId: Long?) {
        _sessionStatsState.update { current ->
            if (!current.isActive || (sessionId != null && sessionId != current.sessionId)) {
                current
            } else {
                current.copy(lastOverlayTriggeredMillis = timestampMillis)
            }
        }
    }

    override fun resetSession() {
        _sessionStatsState.update { current -> createSession(current.isActive) }
    }

    override fun beginSession() {
        _sessionStatsState.value = createSession(isActive = true)
    }

    override fun endSession() {
        _sessionStatsState.update { current ->
            if (current.isActive) createSession(isActive = false) else current
        }
    }

    private fun createSession(isActive: Boolean): SessionStats {
        return SessionStats(
            sessionStartMillis = System.currentTimeMillis(),
            isActive = isActive,
            sessionId = nextSessionId.incrementAndGet()
        )
    }
}
