package com.unscroll.app.ui.onboarding.steps

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.ui.theme.TextPrimaryWhite
import com.unscroll.app.ui.theme.TextSecondaryMuted
import com.unscroll.app.ui.theme.TickerGold

@Composable
fun WelcomeStep(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to",
            fontSize = 20.sp,
            color = TextSecondaryMuted
        )
        Text(
            text = "Unscroll",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = TickerGold
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Reclaim your time from endless reels and shorts.\n\nUnscroll tracks your doomscrolling habits and intervenes with personalized wake-up overlays before your time evaporates.",
            fontSize = 17.sp,
            color = TextPrimaryWhite,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}
