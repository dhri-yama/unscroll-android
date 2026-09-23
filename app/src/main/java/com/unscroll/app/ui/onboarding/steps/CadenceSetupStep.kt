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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.ui.theme.AmbientDarkBackground
import com.unscroll.app.ui.theme.GlassPillBackground
import com.unscroll.app.ui.theme.GlassPillBorder
import com.unscroll.app.ui.theme.TextPrimaryWhite
import com.unscroll.app.ui.theme.TextSecondaryMuted
import com.unscroll.app.ui.theme.TickerGold

val INTERVAL_OPTIONS = listOf(5, 10, 15, 20, 30)

@Composable
fun CadenceSetupStep(
    selectedInterval: Int,
    onSelectInterval: (Int) -> Unit,
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
            text = "Interruption Cadence",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryWhite,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "How often should Unscroll interrupt your session with an intervention overlay?",
            fontSize = 15.sp,
            color = TextSecondaryMuted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            INTERVAL_OPTIONS.forEach { interval ->
                val isSelected = interval == selectedInterval
                val shape = RoundedCornerShape(14.dp)

                Column(
                    modifier = Modifier
                        .clip(shape)
                        .background(if (isSelected) TickerGold else GlassPillBackground)
                        .border(1.dp, GlassPillBorder, shape)
                        .clickable { onSelectInterval(interval) }
                        .padding(horizontal = 14.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${interval}m",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) AmbientDarkBackground else TextPrimaryWhite
                    )
                }
            }
        }
    }
}
