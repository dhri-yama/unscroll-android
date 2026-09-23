package com.unscroll.app.ui.main.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.service.monitor.SessionMonitorForegroundService
import com.unscroll.app.ui.components.GlassmorphicPillButton
import com.unscroll.app.ui.onboarding.steps.PermissionsWizardStep
import com.unscroll.app.ui.theme.TextPrimaryWhite

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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Service Controls & Status",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryWhite
        )
        Spacer(modifier = Modifier.height(16.dp))

        PermissionsWizardStep(
            isUsageGranted = isUsageGranted,
            isOverlayGranted = isOverlayGranted,
            isA11yGranted = isA11yGranted,
            onCheckPermissions = onRefreshPermissions
        )

        Spacer(modifier = Modifier.height(16.dp))

        GlassmorphicPillButton(
            text = "Restart Monitoring Service",
            onClick = {
                SessionMonitorForegroundService.stop(context)
                SessionMonitorForegroundService.start(context)
            }
        )
    }
}
