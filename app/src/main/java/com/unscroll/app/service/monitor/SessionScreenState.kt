package com.unscroll.app.service.monitor

import com.unscroll.app.domain.model.SessionStats
import com.unscroll.app.domain.repository.SessionStatsRepository

internal data class DeviceScreenState(
    val isInteractive: Boolean,
    val isDeviceLocked: Boolean
) {
    val isUnlocked: Boolean = isInteractive && !isDeviceLocked
}

internal sealed interface SessionScreenAction {
    data object None : SessionScreenAction
    data object Begin : SessionScreenAction
    data object End : SessionScreenAction
}

internal fun SessionStatsRepository.resolveSessionScreenAction(
    screenState: DeviceScreenState
): SessionScreenAction {
    val stats = sessionStatsState.value
    return when {
        screenState.isUnlocked && !stats.isActive -> SessionScreenAction.Begin
        screenState.isUnlocked.not() && stats.isActive -> SessionScreenAction.End
        else -> SessionScreenAction.None
    }
}
