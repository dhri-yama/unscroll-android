package com.unscroll.app.ui.main.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.unscroll.app.service.monitor.SessionMonitorForegroundService
import com.unscroll.app.ui.components.CyberButtonStyle
import com.unscroll.app.ui.components.CyberSectionLabel
import com.unscroll.app.ui.components.GlassmorphicPillButton
import com.unscroll.app.ui.onboarding.steps.PermissionsWizardStep
import com.unscroll.app.ui.theme.CyberMuted
import com.unscroll.app.ui.theme.CyberWhite

@Composable
fun PermissionsDashboardScreen(
    isUsageGranted: Boolean,
    isOverlayGranted: Boolean,
    isA11yGranted: Boolean,
    onRefreshPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        CyberSectionLabel(text = "CONFIG // SYSTEM CHANNELS")
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Text(
            text = "Keep the channels open.",
            style = MaterialTheme.typography.headlineMedium,
            color = CyberWhite
        )
        Spacer(modifier = Modifier.padding(top = 6.dp))
        Text(
            text = "Unscroll needs these local capabilities to observe, interpret, and interrupt the loop.",
            style = MaterialTheme.typography.bodyMedium,
            color = CyberMuted
        )
        Spacer(modifier = Modifier.padding(top = 12.dp))
        PermissionsWizardStep(
            isUsageGranted = isUsageGranted,
            isOverlayGranted = isOverlayGranted,
            isA11yGranted = isA11yGranted,
            onCheckPermissions = onRefreshPermissions,
            modifier = Modifier
                .weight(1f),
            showHeader = false
        )
        Spacer(modifier = Modifier.padding(top = 12.dp))
        GlassmorphicPillButton(
            text = "Restart monitoring service",
            onClick = {
                SessionMonitorForegroundService.stop(context)
                SessionMonitorForegroundService.start(context)
            },
            iconPrefix = "[R]",
            style = CyberButtonStyle.Outlined
        )
    }
}
