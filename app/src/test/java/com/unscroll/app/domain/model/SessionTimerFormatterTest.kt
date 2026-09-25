package com.unscroll.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class SessionTimerFormatterTest {

    @Test
    fun formatsSecondsBeforeTheFirstMinute() {
        assertEquals("0:00", formatElapsedTime(0L))
        assertEquals("0:01", formatElapsedTime(1_000L))
        assertEquals("0:59", formatElapsedTime(59_999L))
    }

    @Test
    fun rollsIntoMinutes() {
        assertEquals("1:00", formatElapsedTime(60_000L))
        assertEquals("12:34", formatElapsedTime(754_000L))
    }

    @Test
    fun formatsHours() {
        assertEquals("1:02:03", formatElapsedTime(3_723_000L))
    }

    @Test
    fun clampsNegativeValues() {
        assertEquals("0:00", formatElapsedTime(-1L))
    }
}
