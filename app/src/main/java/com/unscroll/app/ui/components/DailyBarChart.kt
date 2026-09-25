package com.unscroll.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unscroll.app.domain.model.DailyOverlayStats
import com.unscroll.app.domain.model.OverlayResponse
import com.unscroll.app.ui.theme.CyberCyan
import com.unscroll.app.ui.theme.CyberDim
import com.unscroll.app.ui.theme.CyberLine
import com.unscroll.app.ui.theme.CyberMagenta
import com.unscroll.app.ui.theme.CyberMuted
import com.unscroll.app.ui.theme.CyberSoft
import com.unscroll.app.ui.theme.CyberWhite
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ChartDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())

/** Accent paired with each overlay control so charts match the glitch palette. */
fun accentForResponse(response: OverlayResponse): Color = when (response) {
    OverlayResponse.SKIP -> CyberCyan
    OverlayResponse.LOCK -> CyberMagenta
}

fun labelForResponse(response: OverlayResponse): String = when (response) {
    OverlayResponse.SKIP -> "PRIMARY // SKIP"
    OverlayResponse.LOCK -> "SECONDARY // LOCK"
}

/**
 * Hand-rolled daily bar chart. Values are plotted oldest-first, one bar per day,
 * with a floor of 1 on the axis maximum so a day of zeroes cannot divide by zero.
 */
@Composable
fun DailyBarChart(
    title: String,
    series: List<DailyOverlayStats>,
    response: OverlayResponse,
    modifier: Modifier = Modifier,
    chartHeight: Int = 116,
    glitchSeed: Int = 0x3C1A
) {
    val accent = accentForResponse(response)
    val values = series.map { it.countFor(response) }
    val total = values.sum()
    val maxValue = values.maxOrNull() ?: 0
    val frame = glitchFrame(glitchSeed, gain = 0.8f)
    val hasData = total > 0

    CyberPanel(modifier = modifier.fillMaxWidth(), glitchSeed = glitchSeed) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CyberSectionLabel(text = title, glitchSeed = glitchSeed)
            Text(
                text = total.toString().padStart(2, '0'),
                style = MaterialTheme.typography.titleLarge,
                color = if (hasData) accent else CyberDim,
                modifier = Modifier.glitchShimmer(
                    frame = glitchFrame(glitchSeed + 1, gain = 1.1f),
                    minAlpha = 0.45f,
                    maxShiftDp = 2.2f,
                    verticalShiftDp = 0.6f
                )
            )
        }

        Spacer(modifier = Modifier.padding(top = 12.dp))

        if (series.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "NO DATA",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberDim
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(chartHeight.dp)) {
                    val gridLines = 3
                    val plot = computeChartPlot(size.width, size.height)
                    val rects = computeBarRects(values, plot)

                    repeat(gridLines + 1) { index ->
                        val y = plot.plotTop + plot.plotHeight * index / gridLines
                        drawLine(
                            color = if (index == gridLines) CyberLine else CyberLine.copy(alpha = 0.45f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1f
                        )
                    }

                    rects.forEach { rect ->
                        if (rect.height > 0f) {
                            drawRect(
                                color = if (rect.value == maxValue) accent else accent.copy(alpha = 0.55f),
                                topLeft = Offset(rect.left, rect.top),
                                size = Size(rect.width, rect.height)
                            )
                            drawRect(
                                color = CyberWhite.copy(alpha = 0.5f),
                                topLeft = Offset(rect.left, rect.top),
                                size = Size(rect.width, 1f)
                            )
                        } else {
                            drawRect(
                                color = CyberLine.copy(alpha = 0.7f),
                                topLeft = Offset(rect.left, plot.plotBottom - 1f),
                                size = Size(rect.width, 1f)
                            )
                        }

                        val isNewest = rect.index == rects.lastIndex
                        if (isNewest && frame.isActive && rect.height > 0f) {
                            drawRect(
                                color = CyberWhite.copy(alpha = 0.35f * frame.intensity),
                                topLeft = Offset(rect.left, rect.top),
                                size = Size(rect.width, rect.height)
                            )
                        }
                    }

                    drawLine(
                        color = CyberSoft.copy(alpha = if (frame.isActive) 0.9f else 0.5f),
                        start = Offset(0f, plot.plotBottom),
                        end = Offset(size.width, plot.plotBottom),
                        strokeWidth = 1.5f
                    )

                    sparseTickIndexes(series.size).forEach { index ->
                        val x = rects[index].left + rects[index].width / 2f
                        drawLine(
                            color = CyberLine,
                            start = Offset(x, plot.plotBottom + 2f),
                            end = Offset(x, plot.plotBottom + 6f),
                            strokeWidth = 1f
                        )
                    }
                }

                if (series.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = (chartHeight * (1f - LABEL_BAND_FRACTION) + 2f).dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        sparseTickIndexes(series.size).forEach { index ->
                            Text(
                                text = formatEpochDay(series[index].epochDay),
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberMuted,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(52.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.padding(top = 10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "PEAK ${maxValue.toString().padStart(2, '0')}",
                style = MaterialTheme.typography.labelSmall,
                color = CyberMuted
            )
            Text(
                text = rangeCaption(series),
                style = MaterialTheme.typography.labelSmall,
                color = CyberMuted
            )
        }
    }
}

/** First, middle and last bar only, so 90 bars never collide. */
private fun sparseTickIndexes(size: Int): List<Int> = when {
    size <= 0 -> emptyList()
    size <= 2 -> List(size) { it }
    else -> listOf(0, size / 2, size - 1)
}

private fun formatEpochDay(epochDay: Long): String =
    runCatching { LocalDate.ofEpochDay(epochDay).format(ChartDateFormatter) }
        .getOrDefault("")

private fun rangeCaption(series: List<DailyOverlayStats>): String {
    val first = series.firstOrNull() ?: return ""
    val last = series.lastOrNull() ?: return ""
    return "${formatEpochDay(first.epochDay)} — ${formatEpochDay(last.epochDay)}"
}
