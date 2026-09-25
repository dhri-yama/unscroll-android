package com.unscroll.app.ui.components

import com.unscroll.app.ui.theme.CyberCyan
import com.unscroll.app.ui.theme.CyberMagenta
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GlitchCoreTest {

    private fun simulate(
        seed: Int,
        events: Int,
        onEvent: (id: Int, gap: Long, steps: Int, frames: List<GlitchEvent>) -> Unit
    ) {
        val random = Random(seed)
        var first = true
        var activeSteps = 0
        var totalSteps = 0
        repeat(events) { index ->
            val gap = nextGapMillis(random, first)
            first = false
            totalSteps += (gap / GLITCH_STEP_MILLIS).toInt()
            val steps = nextEventSteps(random)
            val eventRandom = Random(seed + index + 1)
            val frames = (0 until steps).map { step ->
                frameForEvent(index + 1, step, steps, eventRandom, enabled = true)
            }
            activeSteps += frames.count { it.isActive }
            onEvent(index + 1, gap, steps, frames)
        }
        assertTrue(activeSteps < totalSteps * 0.1f)
    }

    @Test
    fun disabledEngineIsAlwaysIdle() {
        val random = Random(1)
        repeat(200) { step ->
            assertSame(GlitchEvent.Idle, frameForEvent(1, step, 6, random, enabled = false))
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
    fun eventCoreIsActiveAndTapersOff() {
        val frames = List(GLITCH_MAX_EVENT_STEPS) { step ->
            frameForEvent(1, step, GLITCH_MAX_EVENT_STEPS, Random(1), enabled = true)
        }

        assertTrue(frames.any { it.isActive })
        assertTrue(frames.first().intensity >= frames.last().intensity)
        assertTrue(frames.all { it.intensity in 0f..1f })
    }

    @Test
    fun everyEventReachesStrongIntensity() {
        val random = Random(9)
        repeat(60) { id ->
            val steps = nextEventSteps(random)
            val eventRandom = Random(id + 1)
            val frames = (0 until steps).map { step ->
                frameForEvent(id + 1, step, steps, eventRandom, enabled = true)
            }
            val peak = frames.maxOf { it.intensity }
            assertTrue("id=$id peak=$peak", peak > 0.72f)
            assertTrue("id=$id", frames.first().isActive)
        }
    }

    @Test
    fun steadyStateGapsStayInRequestedRange() {
        val gaps = mutableListOf<Long>()
        val firstGap = mutableListOf<Long>()
        simulate(seed = 4242, events = 300) { id, gap, _, _ ->
            if (id == 1) firstGap += gap else gaps += gap
        }

        assertTrue("firstGap=$firstGap", firstGap.single() in 1_500L..GLITCH_FIRST_GAP_MAX_MILLIS)
        assertTrue("gaps=$gaps", gaps.all { it in GLITCH_MIN_GAP_MILLIS..GLITCH_MAX_GAP_MILLIS })
        val mean = gaps.average()
        assertTrue("mean=$mean", mean in 7_500.0..10_500.0)
    }

    @Test
    fun dutyCycleStaysLowSoUiIsMostlyCalm() {
        var activeSteps = 0
        var totalSteps = 0L
        val random = Random(77)
        var first = true
        repeat(200) {
            totalSteps += nextGapMillis(random, first)
            first = false
            val steps = nextEventSteps(random)
            val eventRandom = Random(it + 1)
            activeSteps += (0 until steps).count { step ->
                frameForEvent(it + 1, step, steps, eventRandom, enabled = true).isActive
            }
        }
        val duty = activeSteps * GLITCH_STEP_MILLIS / totalSteps.toFloat()
        assertTrue("duty=$duty", duty < 0.03f)
    }

    @Test
    fun timingIsNotPeriodicAcrossEvents() {
        val lengths = mutableSetOf<Int>()
        val gaps = mutableSetOf<Long>()
        val ids = mutableSetOf<Int>()
        simulate(seed = 31337, events = 120) { id, gap, steps, frames ->
            lengths += steps
            gaps += gap
            ids += frames.first().seed
        }

        assertTrue("lengths=${lengths.size}", lengths.size >= 4)
        assertTrue("gaps=${gaps.size}", gaps.size >= 20)
        assertTrue("ids=${ids.size}", ids.size >= 100)
    }

    @Test
    fun sameSeedReproducesTheSameSchedule() {
        fun gapsFor(seed: Int): List<Long> {
            val random = Random(seed)
            val out = mutableListOf<Long>()
            var first = true
            repeat(25) {
                out += nextGapMillis(random, first)
                first = false
                nextEventSteps(random)
                random.nextFloat()
            }
            return out
        }

        assertEquals(gapsFor(5), gapsFor(5))
    }

    @Test
    fun envelopePeaksThenDecays() {
        val shape = (0 until 8).map { envelopeFor(it, 8) }
        assertEquals(1f, shape.first(), 0.0001f)
        assertTrue("shape=$shape", shape.zipWithNext().all { (a, b) -> b <= a })
        assertEquals(0f, envelopeFor(8, 8), 0f)
        assertEquals(0f, envelopeFor(-1, 8), 0f)
    }

    @Test
    fun activeFramesAlwaysCarryColoredAccents() {
        val random = Random(2)
        var sawCyan = false
        var sawMagenta = false

        repeat(120) { id ->
            val steps = nextEventSteps(random)
            val eventRandom = Random(id + 1)
            (0 until steps).forEach { step ->
                val frame = frameForEvent(id + 1, step, steps, eventRandom, enabled = true)
                if (!frame.isActive) return@forEach
                val lead = frame.accentFor(swap = false)
                val counter = frame.counterAccentFor(swap = false)
                assertTrue(lead == CyberCyan || lead == CyberMagenta)
                assertTrue(counter == CyberCyan || counter == CyberMagenta)
                assertNotEquals(lead, counter)
                if (lead == CyberCyan) sawCyan = true else sawMagenta = true
            }
        }

        assertTrue("cyan=$sawCyan magenta=$sawMagenta", sawCyan && sawMagenta)
    }

    @Test
    fun perElementWindowsVaryWithinOneEvent() {
        val steps = GLITCH_MAX_EVENT_STEPS
        val global = frameForEvent(1, 1, steps, Random(1), enabled = true)
        val locals = (0 until 40).map { seed -> localFrameFor(global, seed) }
        val active = locals.filter { it.isActive }

        assertTrue("active=${active.size}", active.isNotEmpty())
        assertTrue("skipped=${locals.size - active.size}", locals.size != active.size)
        assertTrue(
            "intensities=${active.map { it.intensity }.distinct().size}",
            active.map { it.intensity }.distinct().size >= 5
        )
    }

    @Test
    fun calmFramesNeverScrambleText() {
        assertEquals("UNSCROLL", glitchedText("UNSCROLL", GlitchEvent.Idle))
    }

    @Test
    fun activeFramesScrambleAndCalmFramesRestoreText() {
        var scrambledFound = false
        repeat(40) { id ->
            val steps = nextEventSteps(Random(id))
            val eventRandom = Random(id + 1)
            (0 until steps).forEach { step ->
                val frame = frameForEvent(id + 1, step, steps, eventRandom, enabled = true)
                if (glitchedText("UNSCROLL", frame) != "UNSCROLL") scrambledFound = true
            }
        }

        assertTrue(scrambledFound)
    }

    @Test
    fun scrambledTextKeepsLengthAndStaysBounded() {
        val source = "SIGNAL INTERRUPTION"
        var totalChanged = 0
        var sampled = 0

        repeat(40) { id ->
            val steps = nextEventSteps(Random(id))
            val eventRandom = Random(id + 1)
            (0 until steps).forEach { step ->
                val frame = frameForEvent(id + 1, step, steps, eventRandom, enabled = true)
                val scrambled = glitchedText(source, frame, strength = 0.4f)

                assertEquals(source.length, scrambled.length)
                val changed = source.zip(scrambled).count { (a, b) -> a != b }
                val maxChanged = (source.length * 0.45f).toInt()
                assertTrue("changed=$changed max=$maxChanged", changed <= maxChanged)
                totalChanged += changed
                sampled++
            }
        }

        val averageChanged = totalChanged / sampled.toFloat()
        assertTrue("averageChanged=$averageChanged", averageChanged < 3.5f)
    }

    @Test
    fun differentSeedsProduceDifferentOffsets() {
        val steps = GLITCH_MAX_EVENT_STEPS
        val global = frameForEvent(1, 1, steps, Random(1), enabled = true)

        assertNotEquals(localFrameFor(global, 1).seed, localFrameFor(global, 2).seed)
    }
}
