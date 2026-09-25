package com.unscroll.app.domain.usecase

import com.unscroll.app.domain.model.SessionStats

class EvaluateOverlayTriggerUseCase {
    operator fun invoke(
        stats: SessionStats,
        intervalMinutes: Int,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): Boolean {
        if (!stats.isActive || intervalMinutes <= 0) return false
        val intervalMillis = intervalMinutes * 60 * 1000L
        val timeSinceLastTrigger = currentTimeMillis - stats.lastOverlayTriggeredMillis
        val activeSessionDuration = currentTimeMillis - stats.sessionStartMillis
        
        return activeSessionDuration >= intervalMillis && timeSinceLastTrigger >= intervalMillis
    }
}
