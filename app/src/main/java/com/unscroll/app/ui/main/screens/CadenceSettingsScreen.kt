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
import com.unscroll.app.ui.onboarding.steps.CadenceSetupStep
import com.unscroll.app.ui.theme.CyberMuted
import com.unscroll.app.ui.theme.CyberWhite

@Composable
fun CadenceSettingsScreen(
    selectedInterval: Int,
    onSelectInterval: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        CyberSectionLabel(text = "CONFIG // INTERRUPTION RATE")
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Text(
            text = "Set the loop threshold.",
            style = MaterialTheme.typography.headlineMedium,
            color = CyberWhite
        )
        Spacer(modifier = Modifier.padding(top = 6.dp))
        Text(
            text = "Choose how long the system permits a continuous scroll before intervening.",
            style = MaterialTheme.typography.bodyMedium,
            color = CyberMuted
        )
        Spacer(modifier = Modifier.padding(top = 12.dp))
        CadenceSetupStep(
            selectedInterval = selectedInterval,
            onSelectInterval = onSelectInterval,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            showHeader = false
        )
    }
}
