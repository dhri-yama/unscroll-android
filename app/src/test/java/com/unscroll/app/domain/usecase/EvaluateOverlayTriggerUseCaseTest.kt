package com.unscroll.app.domain.usecase

import com.unscroll.app.domain.model.SessionStats
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EvaluateOverlayTriggerUseCaseTest {

    private val useCase = EvaluateOverlayTriggerUseCase()
    private val sessionStartMillis = 1_000_000L

    @Test
    fun triggersAtOneMinute() {
        val stats = SessionStats(sessionStartMillis = sessionStartMillis)

        assertFalse(useCase(stats, intervalMinutes = 1, currentTimeMillis = sessionStartMillis + 59_999L))
        assertTrue(useCase(stats, intervalMinutes = 1, currentTimeMillis = sessionStartMillis + 60_000L))
    }

    @Test
    fun doesNotRetriggerBeforeTheNextOneMinuteWindow() {
        val stats = SessionStats(
            sessionStartMillis = sessionStartMillis,
            lastOverlayTriggeredMillis = sessionStartMillis + 60_000L
        )

        assertFalse(useCase(stats, intervalMinutes = 1, currentTimeMillis = sessionStartMillis + 119_999L))
        assertTrue(useCase(stats, intervalMinutes = 1, currentTimeMillis = sessionStartMillis + 120_000L))
    }

    @Test
    fun rejectsNonPositiveIntervals() {
        val stats = SessionStats(sessionStartMillis = sessionStartMillis)

        assertFalse(useCase(stats, intervalMinutes = 0, currentTimeMillis = sessionStartMillis + 60_000L))
    }
}
