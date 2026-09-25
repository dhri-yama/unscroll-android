package com.unscroll.app.ui.components

import android.animation.ValueAnimator
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import kotlin.math.floor

internal const val GLITCH_CYCLE_MILLIS = 2_600
internal const val GLITCH_STEPS_PER_CYCLE = 52

internal enum class GlitchBand {
    Calm,
    Primary,
    PrimaryDecay,
    Secondary,
    SecondaryDecay
}

internal data class GlitchFrame(
    val step: Int,
    val band: GlitchBand,
    val intensity: Float,
    val seed: Int
) {
    val isCalm: Boolean get() = band == GlitchBand.Calm
    val isActive: Boolean get() = intensity > 0.02f

    companion object {
        val Idle = GlitchFrame(
            step = 0,
            band = GlitchBand.Calm,
            intensity = 0f,
            seed = 0
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

internal val LocalGlitchFrame = compositionLocalOf { GlitchFrame.Idle }

internal fun glitchFrameFor(phase: Float, seed: Int, enabled: Boolean): GlitchFrame {
    if (!enabled) return GlitchFrame.Idle
    val wrapped = phase - floor(phase)
    val step = (wrapped * GLITCH_STEPS_PER_CYCLE).toInt()
    val band = when (wrapped) {
        in 0f..0.56f -> GlitchBand.Calm
        in 0.56f..0.71f -> GlitchBand.Primary
        in 0.71f..0.81f -> GlitchBand.PrimaryDecay
        in 0.81f..0.90f -> GlitchBand.Secondary
        else -> GlitchBand.SecondaryDecay
    }
    val base = when (band) {
        GlitchBand.Calm -> 0f
        GlitchBand.Primary -> 1f
        GlitchBand.PrimaryDecay -> 0.45f
        GlitchBand.Secondary -> 0.8f
        GlitchBand.SecondaryDecay -> 0.28f
    }
    val flicker = if (band == GlitchBand.Calm) {
        0f
    } else {
        0.3f + 0.7f * GlitchNoise.sample(seed, step, salt = 7)
    }
    return GlitchFrame(
        step = step,
        band = band,
        intensity = (base * flicker).coerceIn(0f, 1f),
        seed = seed * 131 + band.ordinal
    )
}

@Composable
internal fun GlitchEngine(content: @Composable () -> Unit) {
    if (!ValueAnimator.areAnimatorsEnabled()) {
        CompositionLocalProvider(LocalGlitchFrame provides GlitchFrame.Idle) {
            content()
        }
        return
    }
    val transition = rememberInfiniteTransition(label = "glitch-engine")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(GLITCH_CYCLE_MILLIS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glitch-phase"
    )
    val frame = remember(phase) { glitchFrameFor(phase, seed = 0x5EED, enabled = true) }
    CompositionLocalProvider(LocalGlitchFrame provides frame) {
        content()
    }
}

@Composable
internal fun glitchFrame(seed: Int, gain: Float = 1f): GlitchFrame {
    val global = LocalGlitchFrame.current
    if (global.isCalm) return global
    val intensity = (global.intensity * gain).coerceIn(0f, 1f)
    return if (intensity <= 0.02f) {
        global.copy(intensity = 0f)
    } else {
        global.copy(intensity = intensity, seed = global.seed * 31 + seed)
    }
}

internal fun Modifier.glitchJitter(
    frame: GlitchFrame,
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
    frame: GlitchFrame,
    activeColor: Color,
    idleColor: Color,
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
    frame: GlitchFrame,
    strength: Float = 1f
): Modifier {
    if (!frame.isActive || frame.intensity < 0.35f) return this
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
        }
    }
}

@Composable
internal fun Modifier.glitchFlicker(
    frame: GlitchFrame,
    activeColor: Color,
    idleColor: Color,
    minAlpha: Float = 0.15f
): Modifier {
    val flicker = if (!frame.isActive) {
        1f
    } else {
        (0.15f + 0.85f * GlitchNoise.sample(frame.seed, frame.step, salt = 21))
            .coerceAtLeast(minAlpha)
    }
    return graphicsLayer { alpha = flicker }
}

@Composable
internal fun Modifier.glitchEdgeFlicker(
    frame: GlitchFrame,
    activeColor: Color,
    idleColor: Color,
    tintColor: Color
): Modifier {
    val color = when {
        !frame.isActive -> idleColor
        frame.intensity > 0.55f -> activeColor
        else -> idleColor.copy(alpha = 0.6f)
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
    frame: GlitchFrame,
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
