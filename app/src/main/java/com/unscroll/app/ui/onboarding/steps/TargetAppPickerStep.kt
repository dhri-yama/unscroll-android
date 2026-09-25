package com.unscroll.app.ui.onboarding.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unscroll.app.ui.components.CyberCheckBox
import com.unscroll.app.ui.components.CyberSectionLabel
import com.unscroll.app.ui.components.CyberStatusChip
import com.unscroll.app.ui.theme.CyberLine
import com.unscroll.app.ui.theme.CyberPanel
import com.unscroll.app.ui.theme.CyberMuted
import com.unscroll.app.ui.theme.CyberSoft
import com.unscroll.app.ui.theme.CyberSurface
import com.unscroll.app.ui.theme.CyberWhite

data class AppOption(val pkg: String, val name: String)

val DEFAULT_APPS = listOf(
    AppOption("com.instagram.android", "Instagram"),
    AppOption("com.google.android.youtube", "YouTube Shorts"),
    AppOption("com.zhiliaoapp.musically", "TikTok"),
    AppOption("com.reddit.frontpage", "Reddit"),
    AppOption("com.twitter.android", "X (Twitter)")
)

@Composable
fun TargetAppPickerStep(
    selectedPackages: Set<String>,
    onTogglePackage: (String) -> Unit,
    modifier: Modifier = Modifier,
    showHeader: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        if (showHeader) {
            CyberSectionLabel(text = "TARGETS // WATCHLIST")
            Spacer(modifier = Modifier.padding(top = 16.dp))
            Text(
                text = "Mark the apps you want Unscroll to watch.",
                style = MaterialTheme.typography.headlineLarge,
                color = CyberWhite
            )
            Spacer(modifier = Modifier.padding(top = 10.dp))
            Text(
                text = "Only selected surfaces enter the scroll telemetry loop.",
                style = MaterialTheme.typography.bodyMedium,
                color = CyberMuted
            )
            Spacer(modifier = Modifier.padding(top = 16.dp))
        } else {
            CyberSectionLabel(text = "TARGETS // WATCHLIST")
            Spacer(modifier = Modifier.padding(top = 12.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${selectedPackages.size.toString().padStart(2, '0')} NODES ARMED",
                style = MaterialTheme.typography.labelSmall,
                color = CyberMuted
            )
            CyberStatusChip(
                text = if (selectedPackages.isEmpty()) "STANDBY" else "LISTENING",
                active = selectedPackages.isNotEmpty()
            )
        }
        Spacer(modifier = Modifier.padding(top = 14.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(DEFAULT_APPS, key = { it.pkg }) { app ->
                val isSelected = selectedPackages.contains(app.pkg)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isSelected) CyberSurface else CyberPanel, RoundedCornerShape(0.dp))
                        .border(1.dp, if (isSelected) CyberWhite else CyberLine, RoundedCornerShape(0.dp))
                        .clickable { onTogglePackage(app.pkg) }
                        .padding(horizontal = 14.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = app.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = CyberWhite
                        )
                        Text(
                            text = app.pkg,
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberMuted
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CyberStatusChip(
                            text = if (isSelected) "ARMED" else "IDLE",
                            active = isSelected
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                        CyberCheckBox(checked = isSelected)
                    }
                }
            }
        }
    }
}
