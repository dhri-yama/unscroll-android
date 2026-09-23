package com.unscroll.app.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.unscroll.app.domain.model.SessionStats
import com.unscroll.app.domain.model.UserProfile
import com.unscroll.app.domain.repository.SessionStatsRepository
import com.unscroll.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val settingsRepository: SettingsRepository,
    private val sessionStatsRepository: SessionStatsRepository
) : ViewModel() {

    val isOnboardingCompleted: StateFlow<Boolean> = settingsRepository.isOnboardingCompleted()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val userProfile: StateFlow<UserProfile> = settingsRepository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val trackedPackages: StateFlow<Set<String>> = settingsRepository.getTrackedPackageNames()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())


    val sessionStats: StateFlow<SessionStats> = sessionStatsRepository.sessionStatsState

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
        private val sessionStatsRepository: SessionStatsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(settingsRepository, sessionStatsRepository) as T
        }
    }
}
