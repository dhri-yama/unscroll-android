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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.ui.components.CyberPanel
import com.unscroll.app.ui.components.CyberSectionLabel
import com.unscroll.app.ui.components.glitchShimmer
import com.unscroll.app.ui.components.glitchFrame
import com.unscroll.app.ui.components.glitchJitter
import com.unscroll.app.ui.components.glitchSlices
import com.unscroll.app.ui.components.glitchedText
import com.unscroll.app.ui.theme.CyberBlack
import com.unscroll.app.ui.theme.CyberLine
import com.unscroll.app.ui.theme.CyberMuted
import com.unscroll.app.ui.theme.CyberPanel
import com.unscroll.app.ui.theme.CyberSoft
import com.unscroll.app.ui.theme.CyberWhite

val INTERVAL_OPTIONS = listOf(1, 5, 10, 15, 20, 30)

@Composable
fun CadenceSetupStep(
    selectedInterval: Int,
    onSelectInterval: (Int) -> Unit,
    modifier: Modifier = Modifier,
    showHeader: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        if (showHeader) {
            CyberSectionLabel(text = "CADENCE // INTERRUPTION RATE")
            Spacer(modifier = Modifier.padding(top = 16.dp))
            Text(
                text = "Choose the threshold.",
                style = MaterialTheme.typography.headlineLarge,
                color = CyberWhite
            )
            Spacer(modifier = Modifier.padding(top = 10.dp))
            Text(
                text = "The terminal will interrupt after the selected window of continuous scroll.",
                style = MaterialTheme.typography.bodyMedium,
                color = CyberMuted
            )
            Spacer(modifier = Modifier.padding(top = 28.dp))
        } else {
            CyberSectionLabel(text = "CADENCE // INTERRUPTION RATE")
            Spacer(modifier = Modifier.padding(top = 12.dp))
            Text(
                text = "Select a trigger window.",
                style = MaterialTheme.typography.titleLarge,
                color = CyberWhite
            )
            Spacer(modifier = Modifier.padding(top = 18.dp))
        }
        CyberPanel(modifier = Modifier.fillMaxWidth(), contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                INTERVAL_OPTIONS.forEach { interval ->
                    val isSelected = interval == selectedInterval
                    val frame = glitchFrame(0x2A01 + interval * 17, gain = if (isSelected) 1.25f else 0.5f)
                    val label = interval.toString()
                    val displayed = remember(frame.step, frame.intensity, label, isSelected) {
                        if (isSelected) glitchedText(label, frame, strength = 0.5f) else label
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(82.dp)
                            .background(if (isSelected) CyberWhite else CyberBlack, RoundedCornerShape(0.dp))
                            .border(1.dp, if (isSelected) CyberWhite else CyberLine, RoundedCornerShape(0.dp))
                            .glitchShimmer(
                                frame = frame,
                                activeColor = if (isSelected) CyberWhite else CyberSoft,
                                idleColor = if (isSelected) CyberWhite else CyberSoft,
                                minAlpha = 0.4f,
                                maxShiftDp = if (isSelected) 2.8f else 1.2f,
                                verticalShiftDp = 0.6f
                            )
                            .clickable { onSelectInterval(interval) }
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = displayed,
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                            color = if (isSelected) CyberBlack else CyberSoft,
                            modifier = Modifier.clearAndSetSemantics { contentDescription = label }
                        )
                        Text(
                            text = "MIN",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) CyberBlack else CyberMuted
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Text(
            text = "CURRENT // ${selectedInterval.toString().padStart(2, '0')} MINUTES",
            style = MaterialTheme.typography.labelSmall,
            color = CyberMuted
        )
    }
}
