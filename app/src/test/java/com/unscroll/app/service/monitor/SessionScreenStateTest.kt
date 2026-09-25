package com.unscroll.app.service.monitor

import com.unscroll.app.data.repository.MemorySessionStatsRepositoryImpl
import org.junit.Assert.assertEquals
import org.junit.Test

class SessionScreenStateTest {

    private val unlocked = DeviceScreenState(isInteractive = true, isDeviceLocked = false)
    private val screenOff = DeviceScreenState(isInteractive = false, isDeviceLocked = false)
    private val locked = DeviceScreenState(isInteractive = true, isDeviceLocked = true)

    @Test
    fun unlockedRequiresInteractiveScreenAndNoKeyguard() {
        assertEquals(true, unlocked.isUnlocked)
        assertEquals(false, screenOff.isUnlocked)
        assertEquals(false, locked.isUnlocked)
    }

    @Test
    fun activeSessionStaysActiveWhileUnlocked() {
        val repository = MemorySessionStatsRepositoryImpl()

        assertEquals(
            SessionScreenAction.None,
            repository.resolveSessionScreenAction(unlocked)
        )
    }

    @Test
    fun inactiveSessionBeginsWhenDeviceBecomesUnlocked() {
        val repository = MemorySessionStatsRepositoryImpl()
        repository.endSession()

        assertEquals(
            SessionScreenAction.Begin,
            repository.resolveSessionScreenAction(unlocked)
        )
    }

    @Test
    fun activeSessionEndsWhenScreenTurnsOff() {
        val repository = MemorySessionStatsRepositoryImpl()

        assertEquals(
            SessionScreenAction.End,
            repository.resolveSessionScreenAction(screenOff)
        )
    }

    @Test
    fun activeSessionEndsWhileKeyguardIsLocked() {
        val repository = MemorySessionStatsRepositoryImpl()

        assertEquals(
            SessionScreenAction.End,
            repository.resolveSessionScreenAction(locked)
        )
    }

    @Test
    fun inactiveSessionStaysInactiveWhileLocked() {
        val repository = MemorySessionStatsRepositoryImpl()
        repository.endSession()

        assertEquals(
            SessionScreenAction.None,
            repository.resolveSessionScreenAction(locked)
        )
        assertEquals(
            SessionScreenAction.None,
            repository.resolveSessionScreenAction(screenOff)
        )
    }

    @Test
    fun repeatedReconcileDoesNotRestartAnActiveSession() {
        val repository = MemorySessionStatsRepositoryImpl()
        val sessionId = repository.sessionStatsState.value.sessionId

        repeat(5) {
            assertEquals(
                SessionScreenAction.None,
                repository.resolveSessionScreenAction(unlocked)
            )
        }

        assertEquals(sessionId, repository.sessionStatsState.value.sessionId)
    }

    @Test
    fun missedUnlockBroadcastIsRecoveredByPolling() {
        val repository = MemorySessionStatsRepositoryImpl()
        repository.endSession()

        assertEquals(
            SessionScreenAction.Begin,
            repository.resolveSessionScreenAction(unlocked)
        )

        repository.beginSession()
        val recoveredSessionId = repository.sessionStatsState.value.sessionId

        assertEquals(true, repository.sessionStatsState.value.isActive)
        assertEquals(
            SessionScreenAction.None,
            repository.resolveSessionScreenAction(unlocked)
        )
        assertEquals(recoveredSessionId, repository.sessionStatsState.value.sessionId)
    }
}
