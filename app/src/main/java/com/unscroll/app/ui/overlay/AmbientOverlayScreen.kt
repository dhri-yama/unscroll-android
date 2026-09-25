package com.unscroll.app.ui.overlay

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.domain.model.Insult
import com.unscroll.app.domain.model.SessionStats
import com.unscroll.app.ui.components.AmbientBackground
import com.unscroll.app.ui.components.CyberButtonStyle
import com.unscroll.app.ui.components.CyberMetric
import com.unscroll.app.ui.components.CyberPanel
import com.unscroll.app.ui.components.CyberRule
import com.unscroll.app.ui.components.CyberSectionLabel
import com.unscroll.app.ui.components.CyberStatusChip
import com.unscroll.app.ui.components.CyberTopBar
import com.unscroll.app.ui.components.GlassmorphicPillButton
import com.unscroll.app.ui.components.GlitchText
import com.unscroll.app.ui.components.glitchShimmer
import com.unscroll.app.ui.components.glitchFrame
import com.unscroll.app.ui.components.glitchJitter
import com.unscroll.app.ui.theme.CyberBlack
import com.unscroll.app.ui.theme.CyberMuted
import com.unscroll.app.ui.theme.CyberSoft
import com.unscroll.app.ui.theme.CyberWhite
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

    val timerText = String.format(
        Locale.getDefault(),
        "%02d:%02d",
        countdownSeconds / 60,
        countdownSeconds % 60
    )
    val densityDpi = LocalContext.current.resources.displayMetrics.densityDpi
    val meters = stats.getScrollDistanceMeters(densityDpi)
    val distanceText = if (meters >= 1.0f) {
        String.format(Locale.getDefault(), "%.1fm", meters)
    } else {
        "${(meters * 100).toInt()}cm"
    }
    val activeMinutes = stats.totalTimeSpentMillis / (1000 * 60)

    AmbientBackground(
        modifier = modifier.clickable { onSkipClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 18.dp)
        ) {
            CyberTopBar(
                title = "UNSCROLL // INTERVENTION",
                status = "LOCKDOWN"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CyberSectionLabel(text = "SIGNAL INTERRUPTION // LOOP DETECTED")
                    Spacer(modifier = Modifier.padding(top = 18.dp))
                    GlitchText(
                        text = insult.rawTemplate,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 30.sp,
                            lineHeight = 36.sp,
                            textAlign = TextAlign.Center
                        ),
                        color = CyberWhite
                    )
                    Spacer(modifier = Modifier.padding(top = 18.dp))
                    Text(
                        text = "THE FEED IS LOOPING. THE CLOCK IS NOT PAUSED.",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberMuted,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.padding(top = 24.dp))
                    CyberPanel(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .glitchJitter(
                                        frame = glitchFrame(0x6E17, gain = 1.15f),
                                        maxShiftDp = 2.6f,
                                        verticalShiftDp = 0.8f
                                    )
                            ) {
                                CyberSectionLabel(text = "LOCKOUT TIMER", glitchSeed = 0x6E17)
                                Spacer(modifier = Modifier.padding(top = 8.dp))
                                Text(
                                    text = timerText,
                                    style = MaterialTheme.typography.displayMedium.copy(letterSpacing = 2.sp),
                                    color = CyberWhite,
                                    modifier = Modifier.glitchShimmer(
                                        frame = glitchFrame(0x6E18, gain = 1.3f),
                                        minAlpha = 0.3f,
                                        maxShiftDp = 3.4f,
                                        verticalShiftDp = 0.8f
                                    )
                                )
                            }
                            CyberStatusChip(text = "HOLD", active = true, glitchSeed = 0x6E19)
                        }
                    }
                    Spacer(modifier = Modifier.padding(top = 12.dp))
                    CyberPanel(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                    ) {
                        CyberSectionLabel(text = "SESSION TELEMETRY")
                        Spacer(modifier = Modifier.padding(top = 12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CyberMetric(
                                value = stats.estimatedReelsCount.toString().padStart(2, '0'),
                                label = "Reels",
                                modifier = Modifier.weight(1f),
                                glitchSeed = 0x71A1
                            )
                            CyberMetric(
                                value = distanceText,
                                label = "Distance",
                                modifier = Modifier.weight(1f),
                                glitchSeed = 0x72B2
                            )
                            CyberMetric(
                                value = "${activeMinutes}m",
                                label = "Elapsed",
                                modifier = Modifier.weight(1f),
                                glitchSeed = 0x73C3
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassmorphicPillButton(
                    text = "I'm a PUSSY",
                    onClick = onSkipClick,
                    modifier = Modifier.weight(1f),
                    iconPrefix = ">>",
                    style = CyberButtonStyle.Filled
                )
                GlassmorphicPillButton(
                    text = "I'm an ASS",
                    onClick = onLockScreenClick,
                    modifier = Modifier.weight(1f),
                    iconPrefix = "[L]",
                    style = CyberButtonStyle.Outlined
                )
            }
            Spacer(modifier = Modifier.padding(top = 14.dp))
            Text(
                text = "LOCAL TIME // $currentTimeString    //    TAP OUTSIDE CONTROLS TO ABORT",
                style = MaterialTheme.typography.labelSmall,
                color = CyberMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
