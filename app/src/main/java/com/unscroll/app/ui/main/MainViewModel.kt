package com.unscroll.app.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.unscroll.app.domain.model.DailyOverlayStats
import com.unscroll.app.domain.model.SessionStats
import com.unscroll.app.domain.model.UserProfile
import com.unscroll.app.domain.repository.DailyStatsRepository
import com.unscroll.app.domain.repository.SessionStatsRepository
import com.unscroll.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val settingsRepository: SettingsRepository,
    private val sessionStatsRepository: SessionStatsRepository,
    private val dailyStatsRepository: DailyStatsRepository
) : ViewModel() {

    private val _selectedRangeDays = MutableStateFlow(DEFAULT_RANGE_DAYS)

    val selectedRangeDays: StateFlow<Int> = _selectedRangeDays.asStateFlow()

    val isOnboardingCompleted: StateFlow<Boolean> = settingsRepository.isOnboardingCompleted()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val userProfile: StateFlow<UserProfile> = settingsRepository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val trackedPackages: StateFlow<Set<String>> = settingsRepository.getTrackedPackageNames()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val sessionStats: StateFlow<SessionStats> = sessionStatsRepository.sessionStatsState

    /**
     * Re-subscribes whenever the range selector changes, so switching between
     * 7/30/90 days re-queries storage instead of filtering a cached window.
     */
    val dailyStats: StateFlow<List<DailyOverlayStats>> = _selectedRangeDays
        .flatMapLatest { rangeDays -> dailyStatsRepository.observeDailyStats(rangeDays) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectRange(rangeDays: Int) {
        _selectedRangeDays.value = rangeDays
    }

    fun updateUserName(name: String) {
        viewModelScope.launch {
            settingsRepository.updateUserName(name)
        }
    }

    fun togglePackage(packageName: String) {
        viewModelScope.launch {
            val current = trackedPackages.value
            if (current.contains(packageName)) {
                settingsRepository.removeTrackedPackage(packageName)
            } else {
                settingsRepository.addTrackedPackage(packageName)
            }
        }
    }

    fun updateInterval(minutes: Int) {
        viewModelScope.launch {
            settingsRepository.updateInterruptionInterval(minutes)
        }
    }

    fun resetSessionStats() {
        sessionStatsRepository.resetSession()
    }

    class Factory(
        private val settingsRepository: SettingsRepository,
        private val sessionStatsRepository: SessionStatsRepository,
        private val dailyStatsRepository: DailyStatsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(
                settingsRepository,
                sessionStatsRepository,
                dailyStatsRepository
            ) as T
        }
    }

    companion object {
        const val DEFAULT_RANGE_DAYS = 7
    }
}
