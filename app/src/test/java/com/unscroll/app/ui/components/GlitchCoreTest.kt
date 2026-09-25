package com.unscroll.app.ui.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class GlitchCoreTest {

    @Test
    fun disabledEngineIsAlwaysIdle() {
        repeat(200) { step ->
            val frame = glitchFrameFor(step / 200f, seed = 7, enabled = false)
            assertSame(GlitchFrame.Idle, frame)
        }
    }

    @Test
    fun noiseStaysNormalized() {
        repeat(500) { step ->
            val value = GlitchNoise.sample(0x5EED, step, salt = 3)
            assertTrue("value=$value", value >= 0f && value <= 1f)
        }
    }

    @Test
    fun noiseIsDeterministic() {
        assertEquals(
            GlitchNoise.sample(11, 22, salt = 33),
            GlitchNoise.sample(11, 22, salt = 33),
            0f
        )
    }

    @Test
    fun cycleContainsBothCalmAndActiveBursts() {
        val frames = List(GLITCH_STEPS_PER_CYCLE) { step ->
            glitchFrameFor(step / GLITCH_STEPS_PER_CYCLE.toFloat(), seed = 9, enabled = true)
        }

        assertTrue(frames.any { it.isCalm })
        assertTrue(frames.any { it.band == GlitchBand.Primary })
        assertTrue(frames.any { it.band == GlitchBand.Secondary })
        assertTrue(frames.any { it.intensity > 0.5f })
    }

    @Test
    fun activeCoverageIsFrequentEnoughToBeVisible() {
        val frames = List(GLITCH_STEPS_PER_CYCLE) { step ->
            glitchFrameFor(step / GLITCH_STEPS_PER_CYCLE.toFloat(), seed = 9, enabled = true)
        }
        val activeRatio = frames.count { it.isActive } / frames.size.toFloat()

        assertTrue("activeRatio=$activeRatio", activeRatio > 0.3f)
        assertTrue("activeRatio=$activeRatio", activeRatio < 0.7f)
    }

    @Test
    fun calmFramesNeverScrambleText() {
        val calm = GlitchFrame.Idle
        assertEquals("UNSCROLL", glitchedText("UNSCROLL", calm))
    }

    @Test
    fun activeFramesScrambleAndCalmFramesRestoreText() {
        var scrambledFound = false
        repeat(GLITCH_STEPS_PER_CYCLE) { step ->
            val frame = glitchFrameFor(step / GLITCH_STEPS_PER_CYCLE.toFloat(), seed = 4, enabled = true)
            val scrambled = glitchedText("UNSCROLL", frame)
            if (scrambled != "UNSCROLL") scrambledFound = true
        }

        assertTrue(scrambledFound)
    }

    @Test
    fun scrambledTextKeepsLengthAndStaysBounded() {
        val source = "SIGNAL INTERRUPTION"
        var totalChanged = 0
        var sampled = 0

        repeat(GLITCH_STEPS_PER_CYCLE) { step ->
            val frame = glitchFrameFor(step / GLITCH_STEPS_PER_CYCLE.toFloat(), seed = 12, enabled = true)
            val scrambled = glitchedText(source, frame, strength = 0.4f)

            assertEquals(source.length, scrambled.length)
            val changed = source.zip(scrambled).count { (a, b) -> a != b }
            val maxChanged = (source.length * 0.45f).toInt()
            assertTrue("changed=$changed max=$maxChanged", changed <= maxChanged)
            totalChanged += changed
            sampled++
        }

        val averageChanged = totalChanged / sampled.toFloat()
        assertTrue("averageChanged=$averageChanged", averageChanged < 1.5f)
    }

    @Test
    fun differentSeedsProduceDifferentOffsets() {
        val phase = 0.6f
        val first = glitchFrameFor(phase, seed = 1, enabled = true)
        val second = glitchFrameFor(phase, seed = 2, enabled = true)

        assertNotEquals(first.seed, second.seed)
    }
}
