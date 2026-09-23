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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.ui.onboarding.steps.TargetAppPickerStep
import com.unscroll.app.ui.theme.TextPrimaryWhite

@Composable
fun TargetAppsSettingsScreen(
    selectedPackages: Set<String>,
    onTogglePackage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tracked Doomscrolling Apps",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryWhite
        )
        Spacer(modifier = Modifier.height(16.dp))

        TargetAppPickerStep(
            selectedPackages = selectedPackages,
            onTogglePackage = onTogglePackage
        )
    }
}
