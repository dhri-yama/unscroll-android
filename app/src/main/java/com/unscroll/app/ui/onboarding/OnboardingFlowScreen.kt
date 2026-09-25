package com.unscroll.app.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.unscroll.app.ui.components.AmbientBackground
import com.unscroll.app.ui.components.CyberButtonStyle
import com.unscroll.app.ui.components.CyberProgressRail
import com.unscroll.app.ui.components.CyberSectionLabel
import com.unscroll.app.ui.components.GlassmorphicPillButton
import com.unscroll.app.ui.components.glitchFrame
import com.unscroll.app.ui.components.glitchJitter
import com.unscroll.app.ui.components.glitchSlices
import com.unscroll.app.ui.onboarding.steps.CadenceSetupStep
import com.unscroll.app.ui.onboarding.steps.NamePersonalizationStep
import com.unscroll.app.ui.onboarding.steps.PermissionsWizardStep
import com.unscroll.app.ui.onboarding.steps.TargetAppPickerStep
import com.unscroll.app.ui.onboarding.steps.WelcomeStep

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
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            val stepFrame = glitchFrame(0x4D11 + uiState.currentStep * 131, gain = 1.1f)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CyberSectionLabel(text = "UNSCROLL // INIT SEQUENCE")
                CyberSectionLabel(
                    text = "0${uiState.currentStep + 1} / 05",
                    glitchSeed = 0x4D12 + uiState.currentStep * 131
                )
            }
            Spacer(modifier = Modifier.padding(top = 12.dp))
            CyberProgressRail(
                currentStep = uiState.currentStep,
                totalSteps = 5
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .glitchSlices(frame = stepFrame, strength = 0.45f)
                    .glitchJitter(frame = stepFrame, maxShiftDp = 1.4f, verticalShiftDp = 0.6f),
                contentAlignment = Alignment.TopCenter
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
                    else -> WelcomeStep()
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (uiState.currentStep > 0) {
                    GlassmorphicPillButton(
                        text = "Back",
                        onClick = { viewModel.setStep(uiState.currentStep - 1) },
                        style = CyberButtonStyle.Outlined
                    )
                } else {
                    Spacer(modifier = Modifier)
                }
                if (uiState.currentStep < 4) {
                    GlassmorphicPillButton(
                        text = "Next",
                        onClick = { viewModel.setStep(uiState.currentStep + 1) },
                        iconPrefix = ">",
                        style = CyberButtonStyle.Filled
                    )
                } else {
                    GlassmorphicPillButton(
                        text = "Initiate reckoning",
                        onClick = {
                            viewModel.completeOnboarding(context)
                            onOnboardingFinished()
                        },
                        iconPrefix = "[+]",
                        style = CyberButtonStyle.Filled
                    )
                }
            }
        }
    }
}
