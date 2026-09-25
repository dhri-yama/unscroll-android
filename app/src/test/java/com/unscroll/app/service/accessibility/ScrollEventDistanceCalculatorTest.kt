package com.unscroll.app.service.accessibility

import org.junit.Assert.assertEquals
import org.junit.Test

class ScrollEventDistanceCalculatorTest {

    private val calculator = ScrollEventDistanceCalculator(estimatedItemHeightPx = 40L)

    @Test
    fun usesReportedScrollDelta() {
        val delta = calculator.calculate(
            ScrollEventSnapshot(
                sourceKey = "feed",
                scrollY = 0,
                scrollDeltaY = -180,
                currentItemIndex = -1,
                fromIndex = -1
            )
        )

        assertEquals(180L, delta)
    }

    @Test
    fun fallsBackToAbsoluteScrollPosition() {
        calculator.calculate(
            ScrollEventSnapshot("feed", 100, 0, -1, -1)
        )

        val delta = calculator.calculate(
            ScrollEventSnapshot("feed", 260, 0, -1, -1)
        )

        assertEquals(160L, delta)
    }

    @Test
    fun estimatesMovementWhenOnlyItemIndexChanges() {
        calculator.calculate(
            ScrollEventSnapshot("feed", 0, 0, 0, 0)
        )

        val delta = calculator.calculate(
            ScrollEventSnapshot("feed", 0, 0, 1, 1)
        )

        assertEquals(40L, delta)
    }

    @Test
    fun keepsSourcesIndependent() {
        calculator.calculate(
            ScrollEventSnapshot("first", 0, 0, -1, -1)
        )
        calculator.calculate(
            ScrollEventSnapshot("second", 500, 0, -1, -1)
        )

        val firstDelta = calculator.calculate(
            ScrollEventSnapshot("first", 50, 0, -1, -1)
        )
        val secondDelta = calculator.calculate(
            ScrollEventSnapshot("second", 525, 0, -1, -1)
        )

        assertEquals(50L, firstDelta)
        assertEquals(25L, secondDelta)
    }
}
