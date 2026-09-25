package com.unscroll.app.domain.repository

import com.unscroll.app.domain.model.SessionStats
import kotlinx.coroutines.flow.StateFlow

interface SessionStatsRepository {
    val sessionStatsState: StateFlow<SessionStats>
    fun recordScroll(deltaPx: Long, isReelAdvance: Boolean, sessionId: Long? = null)
    fun updateTimeSpent(activeTimeMillis: Long, sessionId: Long? = null)
    fun updateLastOverlayTriggered(timestampMillis: Long, sessionId: Long? = null)
    fun resetSession()
    fun beginSession()
    fun endSession()
}
