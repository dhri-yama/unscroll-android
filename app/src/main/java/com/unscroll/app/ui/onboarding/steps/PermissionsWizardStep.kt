package com.unscroll.app.ui.onboarding.steps

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.ui.components.GlassmorphicPillButton
import com.unscroll.app.ui.theme.GlassPillBackground
import com.unscroll.app.ui.theme.GlassPillBorder
import com.unscroll.app.ui.theme.TextPrimaryWhite
import com.unscroll.app.ui.theme.TextSecondaryMuted
import com.unscroll.app.ui.theme.TickerGold

@Composable
fun PermissionsWizardStep(
    isUsageGranted: Boolean,
    isOverlayGranted: Boolean,
    isA11yGranted: Boolean,
    onCheckPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Required Permissions",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryWhite,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Unscroll requires system permissions to monitor usage and display overlay interventions.",
            fontSize = 14.sp,
            color = TextSecondaryMuted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Card 1: Usage Access
        PermissionCard(
            title = "Usage Access",
            description = "Monitors active time spent on tracked apps",
            isGranted = isUsageGranted,
            onGrantClick = {
                context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Card 2: Draw Over Apps
        PermissionCard(
            title = "Display Over Other Apps",
            description = "Displays the full-screen ambient intervention overlay",
            isGranted = isOverlayGranted,
            onGrantClick = {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")
                )
                context.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Card 3: Accessibility
        PermissionCard(
            title = "Accessibility Tracking",
            description = "Interprets scroll gestures & Reels counts",
            isGranted = isA11yGranted,
            onGrantClick = {
                context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        GlassmorphicPillButton(
            text = "Refresh Status",
            onClick = onCheckPermissions
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
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(GlassPillBackground)
            .border(1.dp, if (isGranted) TickerGold else GlassPillBorder, shape)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryWhite
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = TextSecondaryMuted
            )
        }

        if (isGranted) {
            Text(
                text = "✓ Granted",
                color = TickerGold,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        } else {
            GlassmorphicPillButton(
                text = "Grant",
                onClick = onGrantClick
            )
        }
    }
}
