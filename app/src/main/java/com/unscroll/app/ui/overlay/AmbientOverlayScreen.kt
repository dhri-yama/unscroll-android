package com.unscroll.app.ui.overlay

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.domain.model.Insult
import com.unscroll.app.domain.model.SessionStats
import com.unscroll.app.ui.components.AmbientBackground
import com.unscroll.app.ui.components.GlassmorphicPillButton
import com.unscroll.app.ui.theme.TextPrimaryWhite
import com.unscroll.app.ui.theme.TextSecondaryMuted
import com.unscroll.app.ui.theme.TickerGold
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AmbientOverlayScreen(
    stats: SessionStats,
    insult: Insult,
    onSkipClick: () -> Unit,
    onLockScreenClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var countdownSeconds by remember { mutableIntStateOf(30) }
    val currentTimeString = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    LaunchedEffect(Unit) {
        while (countdownSeconds > 0) {
            delay(1000L)
            countdownSeconds--
        }
    }

    val minutesFormatted = String.format(Locale.getDefault(), "%02d", countdownSeconds / 60)
    val secondsFormatted = String.format(Locale.getDefault(), "%02d", countdownSeconds % 60)
    val timerText = "$minutesFormatted:$secondsFormatted"

    val meters = stats.getScrollDistanceMeters()
    val distanceText = if (meters >= 1.0f) {
        String.format(Locale.getDefault(), "%.1fm", meters)
    } else {
        "${(meters * 100).toInt()}cm"
    }

    AmbientBackground(
        modifier = modifier.clickable { onSkipClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Status Bar
            Text(
                text = "Current time is $currentTimeString",
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondaryMuted,
                modifier = Modifier.padding(top = 24.dp)
            )

            // Center Insult & Stats Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = insult.rawTemplate,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryWhite,
                    textAlign = TextAlign.Center,
                    lineHeight = 44.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Focus on a distant point until it's time to resume\n${stats.estimatedReelsCount} Reels scrolled ($distanceText total distance)",
                    fontSize = 17.sp,
                    color = TextSecondaryMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    thickness = 1.dp,
                    color = TextSecondaryMuted.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Big Digital Ticker
                Text(
                    text = timerText,
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Bold,
                    color = TickerGold,
                    letterSpacing = 2.sp
                )
            }

            // Bottom Action Pill Buttons & Secondary Hint
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GlassmorphicPillButton(
                        text = "Skip",
                        iconPrefix = ">>",
                        onClick = onSkipClick
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    GlassmorphicPillButton(
                        text = "Lock Screen",
                        iconPrefix = "🔒",
                        onClick = onLockScreenClick
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tap anywhere or Skip to resume",
                    fontSize = 13.sp,
                    color = TextSecondaryMuted.copy(alpha = 0.7f)
                )
            }
        }
    }
}
