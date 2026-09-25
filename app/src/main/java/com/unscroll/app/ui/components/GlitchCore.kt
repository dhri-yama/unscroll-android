package com.unscroll.app.ui.components

import android.animation.ValueAnimator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.unscroll.app.ui.theme.CyberCyan
import com.unscroll.app.ui.theme.CyberMagenta
import kotlinx.coroutines.delay
import kotlin.random.Random

internal const val GLITCH_STEP_MILLIS = 40
internal const val GLITCH_MIN_GAP_MILLIS = 6_000L
internal const val GLITCH_MAX_GAP_MILLIS = 12_000L
internal const val GLITCH_FIRST_GAP_MAX_MILLIS = 3_000L
internal const val GLITCH_MIN_EVENT_STEPS = 3
internal const val GLITCH_MAX_EVENT_STEPS = 8
internal const val GLITCH_STUTTER_CHANCE = 0.10f
internal const val GLITCH_STUTTER_MAX_MILLIS = 900L

internal data class GlitchEvent(
    val eventId: Int,
    val step: Int,
    val steps: Int,
    val intensity: Float,
    val seed: Int,
    val leadIsCyan: Boolean
) {
    val isActive: Boolean get() = intensity > 0.02f

    fun accentFor(swap: Boolean): Color {
        val cyanLeads = leadIsCyan != swap
        return if (cyanLeads) CyberCyan else CyberMagenta
    }

    fun counterAccentFor(swap: Boolean): Color {
        val cyanLeads = leadIsCyan != swap
        return if (cyanLeads) CyberMagenta else CyberCyan
    }

    companion object {
        val Idle = GlitchEvent(
            eventId = 0,
            step = 0,
            steps = 0,
            intensity = 0f,
            seed = 0,
            leadIsCyan = true
        )
    }
}

internal object GlitchNoise {

    fun sample(seed: Int, step: Int, salt: Int = 0): Float {
        var h = seed * 0x27D4EB2D
        h = h xor (step + salt * 0x9E3779B1.toInt() + 0x165667B1)
        h *= 0x85EBCA6B.toInt()
        h = h xor (h ushr 13)
        h *= 0xC2B2AE35.toInt()
        h = h xor (h ushr 16)
        return ((h ushr 8) and 0xFFFF) / 65_535f
    }

    fun range(seed: Int, step: Int, min: Float, max: Float, salt: Int = 0): Float {
        return min + (max - min) * sample(seed, step, salt)
    }
}

internal fun envelopeFor(step: Int, steps: Int): Float {
    if (steps <= 0 || step < 0 || step >= steps) return 0f
    val progress = step / steps.toFloat()
    return when {
        progress < 0.25f -> 1f
        progress < 0.55f -> 1f - (progress - 0.25f) * 0.9f
        else -> {
            val decay = (progress - 0.55f) / 0.45f
            0.775f * (1f - decay) * (1f - decay)
        }
    }
}

internal fun nextGapMillis(random: Random, first: Boolean): Long {
    if (first) {
        return 1_500L + random.nextLong(0L, GLITCH_FIRST_GAP_MAX_MILLIS - 1_500L)
    }
    return GLITCH_MIN_GAP_MILLIS +
        random.nextLong(0L, GLITCH_MAX_GAP_MILLIS - GLITCH_MIN_GAP_MILLIS)
}

internal fun nextEventSteps(random: Random): Int =
    GLITCH_MIN_EVENT_STEPS + random.nextInt(GLITCH_MAX_EVENT_STEPS - GLITCH_MIN_EVENT_STEPS + 1)

internal fun frameForEvent(
    eventId: Int,
    step: Int,
    steps: Int,
    random: Random,
    enabled: Boolean
): GlitchEvent {
    if (!enabled || steps <= 0) return GlitchEvent.Idle
    val base = envelopeFor(step, steps)
    if (base <= 0f) return GlitchEvent.Idle
    val noise = GlitchNoise.sample(eventId * 7919, step, salt = 7)
    val flicker = if (step == 0) 0.78f + 0.22f * noise else 0.3f + 0.7f * noise
    return GlitchEvent(
        eventId = eventId,
        step = step,
        steps = steps,
        intensity = (base * flicker).coerceIn(0f, 1f),
        seed = eventId * 131 + 17,
        leadIsCyan = random.nextFloat() < 0.5f
    )
}

internal val LocalGlitchFrame = compositionLocalOf { GlitchEvent.Idle }

@Composable
internal fun GlitchEngine(content: @Composable () -> Unit) {
    if (!ValueAnimator.areAnimatorsEnabled()) {
        CompositionLocalProvider(LocalGlitchFrame provides GlitchEvent.Idle) {
            content()
        }
        return
    }
    val baseSeed = remember { Random.Default.nextInt() }
    var eventId by remember { mutableIntStateOf(0) }
    var frame by remember { mutableStateOf(GlitchEvent.Idle) }

    LaunchedEffect(baseSeed) {
        val random = Random(baseSeed)
        var currentId = 0
        var first = true
        while (true) {
            var gap = nextGapMillis(random, first)
            first = false
            while (gap > 0L) {
                val slice = minOf(gap, 250L)
                delay(slice)
                gap -= slice
            }
            currentId += 1
            eventId = currentId
            val steps = nextEventSteps(random)
            val eventRandom = Random(baseSeed + currentId)
            for (step in 0 until steps) {
                frame = frameForEvent(currentId, step, steps, eventRandom, enabled = true)
                delay(GLITCH_STEP_MILLIS.toLong())
            }
            frame = GlitchEvent.Idle
            if (random.nextFloat() < GLITCH_STUTTER_CHANCE) {
                delay(400L + random.nextLong(0L, GLITCH_STUTTER_MAX_MILLIS - 400L))
            }
        }
    }

    CompositionLocalProvider(LocalGlitchFrame provides frame) {
        content()
    }
}

internal fun localFrameFor(global: GlitchEvent, seed: Int, gain: Float = 1f): GlitchEvent {
    if (!global.isActive) return global
    val delaySteps = (GlitchNoise.sample(seed, global.eventId, salt = 91) * global.steps * 0.45f).toInt()
    val localStep = global.step - delaySteps
    if (localStep < 0) return GlitchEvent.Idle
    val localSteps = (global.steps * (0.55f + 0.75f * GlitchNoise.sample(seed, global.eventId, salt = 92)))
        .toInt()
        .coerceAtLeast(1)
    val local = envelopeFor(localStep, localSteps)
    if (local <= 0f) return GlitchEvent.Idle
    val texture = 0.3f + 0.7f * GlitchNoise.sample(global.seed xor seed, global.step, salt = 21)
    val intensity = (local * texture * gain).coerceIn(0f, 1f)
    if (intensity <= 0.02f) return GlitchEvent.Idle
    return global.copy(
        step = localStep,
        steps = localSteps,
        intensity = intensity,
        seed = global.seed * 31 + seed,
        leadIsCyan = global.leadIsCyan xor (GlitchNoise.sample(seed, global.eventId, salt = 93) < 0.5f)
    )
}

@Composable
internal fun glitchFrame(seed: Int, gain: Float = 1f): GlitchEvent {
    return localFrameFor(LocalGlitchFrame.current, seed, gain)
}

internal fun Modifier.glitchJitter(
    frame: GlitchEvent,
    maxShiftDp: Float = 3f,
    verticalShiftDp: Float = 1.2f
): Modifier {
    if (!frame.isActive) return this
    val x = GlitchNoise.range(frame.seed, frame.step, -maxShiftDp, maxShiftDp, salt = 1)
    val y = GlitchNoise.range(frame.seed, frame.step, -verticalShiftDp, verticalShiftDp, salt = 2)
    return graphicsLayer {
        translationX = x
        translationY = y
    }
}

@Composable
internal fun Modifier.glitchShimmer(
    frame: GlitchEvent,
    minAlpha: Float = 0.15f,
    maxShiftDp: Float = 0f,
    verticalShiftDp: Float = 0f
): Modifier {
    if (!frame.isActive) {
        return if (maxShiftDp > 0f || verticalShiftDp > 0f) {
            graphicsLayer { alpha = 1f }
        } else {
            this
        }
    }
    val alpha = (0.15f + 0.85f * GlitchNoise.sample(frame.seed, frame.step, salt = 21))
        .coerceAtLeast(minAlpha)
    val x = if (maxShiftDp > 0f) {
        GlitchNoise.range(frame.seed, frame.step, -maxShiftDp, maxShiftDp, salt = 1)
    } else {
        0f
    }
    val y = if (verticalShiftDp > 0f) {
        GlitchNoise.range(frame.seed, frame.step, -verticalShiftDp, verticalShiftDp, salt = 2)
    } else {
        0f
    }
    return graphicsLayer {
        this.alpha = alpha
        translationX = x
        translationY = y
    }
}

@Composable
internal fun Modifier.glitchSlices(
    frame: GlitchEvent,
    strength: Float = 1f
): Modifier {
    if (!frame.isActive || frame.intensity < 0.35f) return this
    val accent = frame.accentFor(swap = false)
    val counter = frame.counterAccentFor(swap = false)
    val density = LocalDensity.current
    val maxShiftPx = with(density) { (6f * strength).dp.toPx() }
    val bandHeightPx = with(density) { 8f.dp.toPx() }
    return drawWithContent {
        drawContent()
        val bands = 1 + (frame.intensity * 1.4f).toInt().coerceAtMost(1)
        repeat(bands) { index ->
            val salt = 3 + index * 11
            val placement = GlitchNoise.sample(frame.seed, frame.step, salt = salt)
            val halfHeight = bandHeightPx * (0.35f + GlitchNoise.sample(frame.seed, frame.step, salt = salt + 1))
            val top = (size.height * placement - halfHeight).coerceIn(0f, (size.height - 1f).coerceAtLeast(0f))
            val bottom = (top + halfHeight * 2f).coerceAtMost(size.height)
            if (bottom - top < 1f) return@repeat
            val shift = GlitchNoise.range(frame.seed, frame.step, -maxShiftPx, maxShiftPx, salt = salt + 2)
            if (kotlin.math.abs(shift) < 0.5f) return@repeat
            clipRect(left = 0f, top = top, right = size.width, bottom = bottom) {
                translate(shift, 0f) {
                    this@drawWithContent.drawContent()
                }
            }
            val tint = if (index % 2 == 0) accent else counter
            drawRect(
                color = tint.copy(alpha = 0.16f * frame.intensity),
                topLeft = Offset(0f, top),
                size = Size(size.width, bottom - top)
            )
        }
    }
}

@Composable
internal fun Modifier.glitchEdgeFlicker(
    frame: GlitchEvent,
    idleColor: Color,
    tintColor: Color
): Modifier {
    val color = if (!frame.isActive) {
        idleColor
    } else {
        frame.accentFor(swap = false)
    }
    val alpha = if (frame.isActive) {
        0.4f + 0.6f * GlitchNoise.sample(frame.seed, frame.step, salt = 23)
    } else {
        1f
    }
    return this
        .drawBehind {
            val corner = 14.dp.toPx()
            val stroke = 1.dp.toPx()
            drawLine(color, Offset(0f, corner), Offset(0f, 0f), stroke)
            drawLine(color, Offset(0f, 0f), Offset(corner, 0f), stroke)
            drawLine(color, Offset(size.width - corner, size.height), Offset(size.width, size.height), stroke)
            drawLine(color, Offset(size.width, size.height), Offset(size.width, size.height - corner), stroke)
            if (frame.isActive) {
                drawRect(
                    color = tintColor.copy(alpha = 0.1f * frame.intensity * alpha),
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, 1.dp.toPx())
                )
            }
        }
}

internal fun glitchedText(
    source: String,
    frame: GlitchEvent,
    strength: Float = 0.35f
): String {
    if (!frame.isActive || source.isEmpty()) return source
    val intensity = frame.intensity
    if (intensity < 0.12f) return source
    val chars = source.toCharArray()
    val replacements = (intensity * strength * chars.size).toInt()
        .coerceAtLeast(if (intensity > 0.4f) 1 else 0)
    repeat(replacements) { attempt ->
        val pickSalt = 31 + attempt * 5
        val index = (GlitchNoise.sample(frame.seed, frame.step, salt = pickSalt) * chars.size)
            .toInt()
            .coerceIn(0, chars.size - 1)
        val glyph = GLITCH_GLYPHS[
            (GlitchNoise.sample(frame.seed, frame.step, salt = pickSalt + 1) * GLITCH_GLYPHS.size)
                .toInt()
                .coerceIn(0, GLITCH_GLYPHS.size - 1)
        ]
        chars[index] = glyph
    }
    return String(chars)
}

private val GLITCH_GLYPHS = charArrayOf(
    '/', '\\', '|', '#', '@', '*', '+', '<', '>', '~', '$', '▓', '▒', '░', '∎'
)
