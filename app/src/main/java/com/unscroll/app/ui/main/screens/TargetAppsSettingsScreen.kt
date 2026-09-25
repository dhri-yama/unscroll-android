package com.unscroll.app.ui.main.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unscroll.app.ui.components.CyberSectionLabel
import com.unscroll.app.ui.onboarding.steps.TargetAppPickerStep
import com.unscroll.app.ui.theme.CyberMuted
import com.unscroll.app.ui.theme.CyberWhite

@Composable
fun TargetAppsSettingsScreen(
    selectedPackages: Set<String>,
    onTogglePackage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        CyberSectionLabel(text = "CONFIG // TARGET NODES")
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Text(
            text = "Choose the apps under watch.",
            style = MaterialTheme.typography.headlineMedium,
            color = CyberWhite
        )
        Spacer(modifier = Modifier.padding(top = 6.dp))
        Text(
            text = "The observer only activates inside armed targets.",
            style = MaterialTheme.typography.bodyMedium,
            color = CyberMuted
        )
        Spacer(modifier = Modifier.padding(top = 12.dp))
        TargetAppPickerStep(
            selectedPackages = selectedPackages,
            onTogglePackage = onTogglePackage,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            showHeader = false
        )
    }
}
