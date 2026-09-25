package com.unscroll.app.ui.onboarding.steps

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unscroll.app.ui.components.CyberButtonStyle
import com.unscroll.app.ui.components.CyberPanel
import com.unscroll.app.ui.components.CyberSectionLabel
import com.unscroll.app.ui.components.CyberStatusChip
import com.unscroll.app.ui.components.GlassmorphicPillButton
import com.unscroll.app.ui.theme.CyberMuted
import com.unscroll.app.ui.theme.CyberWhite

@Composable
fun PermissionsWizardStep(
    isUsageGranted: Boolean,
    isOverlayGranted: Boolean,
    isA11yGranted: Boolean,
    onCheckPermissions: () -> Unit,
    modifier: Modifier = Modifier,
    showHeader: Boolean = true
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        if (showHeader) {
            CyberSectionLabel(text = "ACCESS // SYSTEM CHECK")
            Spacer(modifier = Modifier.padding(top = 16.dp))
            Text(
                text = "Grant the channels Unscroll needs to interrupt the loop.",
                style = MaterialTheme.typography.headlineLarge,
                color = CyberWhite
            )
            Spacer(modifier = Modifier.padding(top = 10.dp))
            Text(
                text = "Permissions stay local to this device. Unscroll uses them only for telemetry, intervention, and scroll interpretation.",
                style = MaterialTheme.typography.bodyMedium,
                color = CyberMuted
            )
            Spacer(modifier = Modifier.padding(top = 20.dp))
        } else {
            CyberSectionLabel(text = "ACCESS // SYSTEM CHECK")
            Spacer(modifier = Modifier.padding(top = 12.dp))
        }
        PermissionCard(
            title = "Usage access",
            description = "Measures time inside the selected targets.",
            isGranted = isUsageGranted,
            onGrantClick = { context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) }
        )
        Spacer(modifier = Modifier.padding(top = 8.dp))
        PermissionCard(
            title = "Display over apps",
            description = "Deploys the full-screen intervention terminal.",
            isGranted = isOverlayGranted,
            onGrantClick = {
                context.startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    )
                )
            }
        )
        Spacer(modifier = Modifier.padding(top = 8.dp))
        PermissionCard(
            title = "Accessibility tracking",
            description = "Interprets scroll gestures and estimated reel units.",
            isGranted = isA11yGranted,
            onGrantClick = { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        )
        Spacer(modifier = Modifier.padding(top = 18.dp))
        GlassmorphicPillButton(
            text = "Refresh system status",
            onClick = onCheckPermissions,
            iconPrefix = "[↻]",
            style = CyberButtonStyle.Outlined
        )
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    isGranted: Boolean,
    onGrantClick: () -> Unit
) {
    CyberPanel(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = CyberWhite
                )
                Spacer(modifier = Modifier.padding(top = 4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CyberMuted
                )
            }
            Spacer(modifier = Modifier.padding(horizontal = 10.dp))
            if (isGranted) {
                CyberStatusChip(text = "Granted", active = true)
            } else {
                GlassmorphicPillButton(
                    text = "Grant",
                    onClick = onGrantClick,
                    style = CyberButtonStyle.Outlined
                )
            }
        }
    }
}
