package com.unscroll.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.unscroll.app.ui.components.AmbientBackground
import com.unscroll.app.ui.components.GlassmorphicPillButton
import com.unscroll.app.ui.onboarding.steps.CadenceSetupStep
import com.unscroll.app.ui.onboarding.steps.NamePersonalizationStep
import com.unscroll.app.ui.onboarding.steps.PermissionsWizardStep
import com.unscroll.app.ui.onboarding.steps.TargetAppPickerStep
import com.unscroll.app.ui.onboarding.steps.WelcomeStep
import com.unscroll.app.ui.theme.GlassPillBackground
import com.unscroll.app.ui.theme.TickerGold

@Composable
fun OnboardingFlowScreen(
    viewModel: OnboardingViewModel,
    onOnboardingFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.checkPermissions(context)
    }

    AmbientBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Step Indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 36.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                (0..4).forEach { index ->
                    val isCurrent = index == uiState.currentStep
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isCurrent) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (isCurrent) TickerGold else GlassPillBackground)
                    )
                }
            }

            // Step Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (uiState.currentStep) {
                    0 -> WelcomeStep()
                    1 -> NamePersonalizationStep(
                        userName = uiState.userName,
                        onNameChange = viewModel::updateUserName
                    )
                    2 -> TargetAppPickerStep(
                        selectedPackages = uiState.selectedPackages,
                        onTogglePackage = viewModel::togglePackage
                    )
                    3 -> PermissionsWizardStep(
                        isUsageGranted = uiState.isUsagePermissionGranted,
                        isOverlayGranted = uiState.isOverlayPermissionGranted,
                        isA11yGranted = uiState.isAccessibilityPermissionGranted,
                        onCheckPermissions = { viewModel.checkPermissions(context) }
                    )
                    4 -> CadenceSetupStep(
                        selectedInterval = uiState.interruptionIntervalMinutes,
                        onSelectInterval = viewModel::updateInterval
                    )
                }
            }

            // Bottom Navigation Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (uiState.currentStep > 0) {
                    GlassmorphicPillButton(
                        text = "Back",
                        onClick = { viewModel.setStep(uiState.currentStep - 1) }
                    )
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (uiState.currentStep < 4) {
                    GlassmorphicPillButton(
                        text = "Next",
                        onClick = { viewModel.setStep(uiState.currentStep + 1) }
                    )
                } else {
                    GlassmorphicPillButton(
                        text = "Start Reckoning",
                        onClick = {
                            viewModel.completeOnboarding(context)
                            onOnboardingFinished()
                        }
                    )
                }
            }
        }
    }
}
