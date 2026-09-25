package com.unscroll.app.ui.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChartGeometryTest {

    private val width = 900f
    private val height = 116f

    private fun plot() = computeChartPlot(width, height)

    @Test
    fun baselineSitsAtTheBottomOfThePlotRegionNotTheCentre() {
        val plot = plot()

        assertEquals(0f, plot.plotTop, 0.001f)
        assertEquals(height * (1f - LABEL_BAND_FRACTION), plot.plotBottom, 0.001f)
        // The axis must be in the lower portion of the box, clear of the middle.
        assertTrue("baseline was ${plot.plotBottom} of $height", plot.plotBottom > height * 0.6f)
        assertTrue("label band should not swallow the chart", plot.plotHeight > height * 0.5f)
    }

    @Test
    fun labelBandIsReservedAtTheBottom() {
        val plot = plot()

        assertTrue(plot.plotBottom < height)
        assertTrue(plot.plotBottom + (height * LABEL_BAND_FRACTION) <= height + 0.001f)
    }

    @Test
    fun fullHeightBarTouchesTheTopAndNeverLeavesTheCanvas() {
        val plot = plot()
        val rects = computeBarRects(listOf(0, 0, 10, 0), plot)
        val tallest = rects.maxBy { it.height }

        assertEquals(10, tallest.value)
        assertEquals(plot.plotTop, tallest.top, 0.001f)
        assertEquals(plot.plotHeight, tallest.height, 0.001f)
        assertTrue("bar top must stay inside the canvas", tallest.top >= 0f)
    }

    @Test
    fun everyBarStaysWithinTheCanvasBounds() {
        val plot = plot()
        val values = listOf(0, 1, 7, 3, 0, 12, 0, 5, 9, 0, 2, 0, 0, 4, 11, 1)
        val rects = computeBarRects(values, plot)

        assertEquals(values.size, rects.size)
        rects.forEach { rect ->
            assertTrue("left < 0 for index ${rect.index}", rect.left >= 0f)
            assertTrue("right ${rect.right} > $width for index ${rect.index}", rect.right <= width + 0.001f)
            assertTrue("top ${rect.top} < 0 for index ${rect.index}", rect.top >= -0.001f)
            assertTrue("height negative for index ${rect.index}", rect.height >= 0f)
        }
    }

    @Test
    fun everyBarAnchorsToTheBaseline() {
        val plot = plot()
        val rects = computeBarRects(listOf(0, 3, 9, 1, 6), plot)

        rects.forEach { rect ->
            assertEquals("bar ${rect.index} not anchored to axis", plot.plotBottom, rect.bottom, 0.001f)
        }
    }

    @Test
    fun zeroValuesProduceNoBarButStayInSequence() {
        val plot = plot()
        val rects = computeBarRects(listOf(0, 0, 0), plot)

        assertEquals(3, rects.size)
        rects.forEach { rect ->
            assertEquals(0, rect.value)
            assertEquals(0f, rect.height, 0.001f)
        }
    }

    @Test
    fun allZeroSeriesDoesNotDivideByZero() {
        val plot = plot()
        val rects = computeBarRects(listOf(0, 0, 0, 0), plot)

        assertEquals(4, rects.size)
        assertTrue(rects.all { it.height == 0f })
    }

    @Test
    fun singleBarSeriesSpansThePlotHeight() {
        val plot = plot()
        val rect = computeBarRects(listOf(6), plot).single()

        assertEquals(plot.plotHeight, rect.height, 0.001f)
        assertEquals(0f, rect.top, 0.001f)
    }

    @Test
    fun narrowSeriesKeepsABarWidthFloor() {
        val plot = computeChartPlot(4f, height)
        val rects = computeBarRects(listOf(1, 2, 3, 4, 5, 6, 7, 8), plot)

        rects.forEach { rect ->
            assertTrue("bar too thin to see", rect.width > 0f)
            assertTrue("bar wider than its slot", rect.right <= 4f + 0.001f)
        }
    }

    @Test
    fun barsNeverOverlap() {
        val plot = plot()
        val rects = computeBarRects(List(30) { it % 5 }, plot)

        rects.zipWithNext { a, b ->
            assertTrue("bar ${a.index} overlaps bar ${b.index}", a.right <= b.left + 0.001f)
        }
    }

    @Test
    fun degenerateSizesProduceNoBars() {
        assertTrue(computeBarRects(listOf(1, 2), computeChartPlot(0f, height)).isEmpty())
        assertTrue(computeBarRects(listOf(1, 2), computeChartPlot(width, 0f)).isEmpty())
        assertTrue(computeBarRects(emptyList(), plot()).isEmpty())
    }

    @Test
    fun labelBandFractionIsClampedToASaneRange() {
        val tooBig = computeChartPlot(width, height, labelBandFraction = 5f)
        val negative = computeChartPlot(width, height, labelBandFraction = -1f)

        // Clamped to a 0.6 band, so the plot always keeps 40% of the height.
        assertEquals(height * 0.4f, tooBig.plotBottom, 0.001f)
        assertTrue(tooBig.plotHeight > 0f)
        assertEquals(height, negative.plotBottom, 0.001f)
    }

    @Test
    fun worksAtTheWidestSelectableRange() {
        val plot = plot()
        val rects = computeBarRects(List(90) { it % 7 }, plot)

        assertEquals(90, rects.size)
        assertTrue(rects.all { it.right <= width + 0.001f && it.top >= -0.001f })
    }
}
