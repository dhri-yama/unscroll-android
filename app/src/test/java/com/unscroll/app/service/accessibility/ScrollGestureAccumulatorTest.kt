package com.unscroll.app.service.accessibility

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ScrollGestureAccumulatorTest {

    @Test
    fun groupsEventsUntilTheSourceGoesIdle() {
        val accumulator = ScrollGestureAccumulator(idleTimeMillis = 450L)

        assertNull(accumulator.add("feed", 20L, 100L))
        assertNull(accumulator.add("feed", 30L, 200L))
        assertNull(accumulator.add("feed", 10L, 300L))

        val gesture = accumulator.complete("feed")

        assertNotNull(gesture)
        assertEquals(60L, gesture?.distancePx)
        assertEquals(300L, gesture?.lastEventTimestampMillis)
    }

    @Test
    fun completesThePreviousGestureWhenAnIdleGapArrives() {
        val accumulator = ScrollGestureAccumulator(idleTimeMillis = 450L)

        accumulator.add("feed", 20L, 100L)
        val completedGesture = accumulator.add("feed", 30L, 700L)

        assertNotNull(completedGesture)
        assertEquals(20L, completedGesture?.distancePx)
        assertEquals(30L, accumulator.complete("feed")?.distancePx)
    }

    @Test
    fun completesAllPendingGestures() {
        val accumulator = ScrollGestureAccumulator(idleTimeMillis = 450L)

        accumulator.add("first", 10L, 100L)
        accumulator.add("second", 20L, 100L)

        val gestures = accumulator.completeAll()

        assertEquals(2, gestures.size)
        assertEquals(30L, gestures.sumOf { it.distancePx })
        assertNull(accumulator.complete("first"))
    }

    @Test
    fun separatesGesturesAcrossSessions() {
        val accumulator = ScrollGestureAccumulator(idleTimeMillis = 450L)

        accumulator.add("feed", 10L, 100L, sessionId = 1L)
        val completedGesture = accumulator.add("feed", 20L, 200L, sessionId = 2L)
        val currentGesture = accumulator.complete("feed", sessionId = 2L)

        assertNotNull(completedGesture)
        assertEquals(1L, completedGesture?.sessionId)
        assertNotNull(currentGesture)
        assertEquals(2L, currentGesture?.sessionId)
        assertEquals(20L, currentGesture?.distancePx)
    }
}
