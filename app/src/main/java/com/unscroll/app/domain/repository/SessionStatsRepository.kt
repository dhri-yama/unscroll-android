package com.unscroll.app.domain.repository

import com.unscroll.app.domain.model.SessionStats
import kotlinx.coroutines.flow.StateFlow

interface SessionStatsRepository {
    val sessionStatsState: StateFlow<SessionStats>
    fun recordScroll(deltaPx: Long, isReelAdvance: Boolean)
    fun updateTimeSpent(activeTimeMillis: Long)
    fun updateLastOverlayTriggered(timestampMillis: Long)
    fun resetSession()
}
