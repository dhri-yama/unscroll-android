package com.unscroll.app.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MemorySessionStatsRepositoryImplTest {

    @Test
    fun resetSessionClearsAllTelemetry() {
        val repository = MemorySessionStatsRepositoryImpl()
        repository.recordScroll(120L, isReelAdvance = true)
        repository.updateTimeSpent(12_345L)
        repository.updateLastOverlayTriggered(6_789L)

        repository.resetSession()

        val stats = repository.sessionStatsState.value
        assertTrue(stats.isActive)
        assertEquals(0L, stats.totalTimeSpentMillis)
        assertEquals(0L, stats.totalScrollsCount)
        assertEquals(0L, stats.totalScrollDistancePx)
        assertEquals(0L, stats.estimatedReelsCount)
        assertEquals(0L, stats.lastOverlayTriggeredMillis)
    }

    @Test
    fun endingSessionClearsTelemetryAndRejectsUpdates() {
        val repository = MemorySessionStatsRepositoryImpl()
        repository.recordScroll(120L, isReelAdvance = true)
        repository.updateTimeSpent(12_345L)

        repository.endSession()
        repository.recordScroll(50L, isReelAdvance = true)
        repository.updateTimeSpent(1_000L)
        repository.updateLastOverlayTriggered(2_000L)

        val stats = repository.sessionStatsState.value
        assertFalse(stats.isActive)
        assertEquals(0L, stats.totalTimeSpentMillis)
        assertEquals(0L, stats.totalScrollsCount)
        assertEquals(0L, stats.totalScrollDistancePx)
        assertEquals(0L, stats.estimatedReelsCount)
        assertEquals(0L, stats.lastOverlayTriggeredMillis)
    }

    @Test
    fun staleSessionUpdatesCannotPopulateTheNextSession() {
        val repository = MemorySessionStatsRepositoryImpl()
        val previousSessionId = repository.sessionStatsState.value.sessionId

        repository.endSession()
        repository.beginSession()
        repository.recordScroll(50L, isReelAdvance = true, sessionId = previousSessionId)
        repository.updateTimeSpent(1_000L, previousSessionId)
        repository.updateLastOverlayTriggered(2_000L, previousSessionId)

        val stats = repository.sessionStatsState.value
        assertTrue(stats.isActive)
        assertNotEquals(previousSessionId, stats.sessionId)
        assertEquals(0L, stats.totalTimeSpentMillis)
        assertEquals(0L, stats.totalScrollsCount)
        assertEquals(0L, stats.totalScrollDistancePx)
        assertEquals(0L, stats.estimatedReelsCount)
        assertEquals(0L, stats.lastOverlayTriggeredMillis)
    }

    @Test
    fun resettingAnInactiveSessionKeepsItInactive() {
        val repository = MemorySessionStatsRepositoryImpl()

        repository.endSession()
        repository.resetSession()

        val stats = repository.sessionStatsState.value
        assertFalse(stats.isActive)
        assertEquals(0L, stats.totalTimeSpentMillis)
        assertEquals(0L, stats.totalScrollsCount)
    }

    @Test
    fun beginningASessionAlwaysStartsFresh() {
        val repository = MemorySessionStatsRepositoryImpl()
        repository.recordScroll(50L, isReelAdvance = true)
        val previousSessionId = repository.sessionStatsState.value.sessionId

        repository.beginSession()

        val stats = repository.sessionStatsState.value
        assertTrue(stats.isActive)
        assertNotEquals(previousSessionId, stats.sessionId)
        assertEquals(0L, stats.totalTimeSpentMillis)
        assertEquals(0L, stats.totalScrollsCount)
        assertEquals(0L, stats.totalScrollDistancePx)
        assertEquals(0L, stats.estimatedReelsCount)
        assertEquals(0L, stats.lastOverlayTriggeredMillis)
    }
}
