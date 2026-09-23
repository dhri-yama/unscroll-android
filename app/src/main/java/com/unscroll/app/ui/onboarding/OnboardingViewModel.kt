package com.unscroll.app.ui.onboarding

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.unscroll.app.domain.repository.SettingsRepository
import com.unscroll.app.service.monitor.SessionMonitorForegroundService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val currentStep: Int = 0,
    val userName: String = "",
    val selectedPackages: Set<String> = setOf(
        "com.instagram.android",
        "com.google.android.youtube",
        "com.zhiliaoapp.musically"
    ),
    val interruptionIntervalMinutes: Int = 10,
    val isUsagePermissionGranted: Boolean = false,
    val isOverlayPermissionGranted: Boolean = false,
    val isAccessibilityPermissionGranted: Boolean = false
)

class OnboardingViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = settingsRepository.getUserProfile().first()
            val packages = settingsRepository.getTrackedPackageNames().first()
            _uiState.value = _uiState.value.copy(
                userName = profile.userName,
                interruptionIntervalMinutes = profile.interruptionIntervalMinutes,
                selectedPackages = packages
            )
        }
    }

    fun setStep(step: Int) {
        _uiState.value = _uiState.value.copy(currentStep = step.coerceIn(0, 4))
    }

    fun updateUserName(name: String) {
        _uiState.value = _uiState.value.copy(userName = name)
    }

    fun togglePackage(packageName: String) {
        val current = _uiState.value.selectedPackages
        val updated = if (current.contains(packageName)) current - packageName else current + packageName
        _uiState.value = _uiState.value.copy(selectedPackages = updated)
    }

    fun updateInterval(minutes: Int) {
        _uiState.value = _uiState.value.copy(interruptionIntervalMinutes = minutes)
    }

    fun checkPermissions(context: Context) {
        val hasUsage = checkUsageStatsPermission(context)
        val hasOverlay = Settings.canDrawOverlays(context)
        val hasA11y = checkAccessibilityPermission(context)

        _uiState.value = _uiState.value.copy(
            isUsagePermissionGranted = hasUsage,
            isOverlayPermissionGranted = hasOverlay,
            isAccessibilityPermissionGranted = hasA11y
        )
    }

    fun completeOnboarding(context: Context) {
        viewModelScope.launch {
            val state = _uiState.value
            settingsRepository.updateUserName(state.userName)
            settingsRepository.setTrackedPackageNames(state.selectedPackages)
            settingsRepository.updateInterruptionInterval(state.interruptionIntervalMinutes)
            settingsRepository.setOnboardingCompleted(true)

            SessionMonitorForegroundService.start(context)
        }
    }

    private fun checkUsageStatsPermission(context: Context): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as? android.app.AppOpsManager ?: return false
        val mode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(
                android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOpsManager.checkOpNoThrow(
                android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        }
        return mode == android.app.AppOpsManager.MODE_ALLOWED
    }

    private fun checkAccessibilityPermission(context: Context): Boolean {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? android.view.accessibility.AccessibilityManager ?: return false
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabledServices.contains(context.packageName)
    }

    class Factory(private val settingsRepository: SettingsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OnboardingViewModel(settingsRepository) as T
        }
    }
}
