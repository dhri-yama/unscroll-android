package com.unscroll.app.ui.main.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unscroll.app.ui.theme.CyberCyan
import com.unscroll.app.ui.theme.CyberLine
import com.unscroll.app.ui.theme.CyberMuted
import com.unscroll.app.ui.theme.CyberWhite
import com.unscroll.app.ui.theme.TerminalShape

/** Trailing-day windows offered by the chart date selector. */
val ChartRangeOptions = listOf(7, 30, 90)

@Composable
fun CyberRangeSelector(
    options: List<Int>,
    selectedRangeDays: Int,
    onSelectRange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = CyberCyan
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { days ->
            val isSelected = days == selectedRangeDays
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
                    .background(
                        if (isSelected) accent.copy(alpha = 0.18f) else Color.Transparent,
                        TerminalShape
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) accent else CyberLine,
                        shape = TerminalShape
                    )
                    .clickable { onSelectRange(days) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${days}D",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) CyberWhite else CyberMuted,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
