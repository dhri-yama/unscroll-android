package com.unscroll.app.ui.components

/**
 * Chart geometry, kept separate from the Canvas so the layout invariants can be
 * asserted in unit tests rather than eyeballed on a device.
 *
 * Coordinate contract: the x-axis baseline sits at the *bottom* of the plot
 * region, bars grow upward from it, and no bar may exceed the plot region or
 * the canvas width.
 */

/** Fraction of the canvas reserved at the bottom for the date axis labels. */
internal const val LABEL_BAND_FRACTION = 0.22f

internal const val BAR_WIDTH_FRACTION = 0.62f
internal const val MIN_BAR_HEIGHT_PX = 2f

internal data class ChartPlot(
    val width: Float,
    val height: Float,
    val plotTop: Float,
    val plotBottom: Float
) {
    val plotHeight: Float get() = plotBottom - plotTop
}

internal fun computeChartPlot(
    width: Float,
    height: Float,
    labelBandFraction: Float = LABEL_BAND_FRACTION
): ChartPlot {
    val fraction = labelBandFraction.coerceIn(0f, 0.6f)
    val labelBand = (height * fraction).coerceAtLeast(0f)
    val plotBottom = (height - labelBand).coerceIn(0f, height)
    return ChartPlot(
        width = width,
        height = height,
        plotTop = 0f,
        plotBottom = plotBottom
    )
}

internal data class BarRect(
    val index: Int,
    val value: Int,
    val left: Float,
    val top: Float,
    val width: Float,
    val height: Float
) {
    val right: Float get() = left + width
    val bottom: Float get() = top + height
}

/**
 * Lays out one bar per value, anchored to [ChartPlot.plotBottom]. Values are
 * scaled against the largest value in the series, with a floor of 1 so an
 * all-zero series cannot divide by zero.
 */
internal fun computeBarRects(
    values: List<Int>,
    plot: ChartPlot,
    barWidthFraction: Float = BAR_WIDTH_FRACTION
): List<BarRect> {
    if (values.isEmpty() || plot.width <= 0f || plot.plotHeight <= 0f) return emptyList()

    val slot = plot.width / values.size
    val fraction = barWidthFraction.coerceIn(0.05f, 1f)
    val barWidth = (slot * fraction).coerceAtMost(slot)
    val gap = (slot - barWidth) / 2f
    val peak = (values.maxOrNull() ?: 0).coerceAtLeast(1)

    return values.mapIndexed { index, rawValue ->
        val value = rawValue.coerceAtLeast(0)
        val scaled = plot.plotHeight * (value.toFloat() / peak)
        val barHeight = if (value <= 0) {
            0f
        } else {
            scaled.coerceIn(MIN_BAR_HEIGHT_PX, plot.plotHeight)
        }
        BarRect(
            index = index,
            value = value,
            left = index * slot + gap,
            top = plot.plotBottom - barHeight,
            width = barWidth,
            height = barHeight
        )
    }
}
