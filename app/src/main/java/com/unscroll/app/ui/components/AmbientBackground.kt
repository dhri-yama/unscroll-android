package com.unscroll.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.unscroll.app.ui.theme.CyberBlack
import com.unscroll.app.ui.theme.CyberLine
import com.unscroll.app.ui.theme.CyberWhite

@Composable
fun AmbientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "terminal-grid")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(7200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "grid-phase"
    )
    val frame = glitchFrame(0x8A31, gain = 1.1f)

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(CyberBlack)
            val gridStep = 32.dp.toPx()
            val verticalOffset = phase * gridStep
            var x = -gridStep + verticalOffset
            while (x < size.width + gridStep) {
                drawLine(
                    color = CyberLine.copy(alpha = 0.32f),
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1f,
                    cap = StrokeCap.Square
                )
                x += gridStep
            }
            val horizontalOffset = phase * gridStep * 0.45f
            var y = -gridStep + horizontalOffset
            while (y < size.height + gridStep) {
                drawLine(
                    color = CyberLine.copy(alpha = 0.24f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f,
                    cap = StrokeCap.Square
                )
                y += gridStep
            }
            var scanY = 14.dp.toPx()
            while (scanY < size.height) {
                drawLine(
                    color = Color.White.copy(alpha = 0.035f),
                    start = Offset(0f, scanY),
                    end = Offset(size.width, scanY),
                    strokeWidth = 1f
                )
                scanY += 14.dp.toPx()
            }
            val signalY = (phase * size.height * 1.4f - size.height * 0.2f).coerceIn(0f, size.height)
            drawRect(
                color = CyberWhite.copy(alpha = 0.09f),
                topLeft = Offset(0f, signalY),
                size = Size(size.width, 1.dp.toPx())
            )
            drawRect(
                color = CyberWhite.copy(alpha = 0.18f),
                topLeft = Offset(0f, 0f),
                size = Size(size.width, 1.dp.toPx())
            )

            if (frame.isActive) {
                val intensity = frame.intensity
                val accent = frame.accentFor(swap = false)
                val accentAlt = frame.counterAccentFor(swap = false)
                val bandCount = (1 + (intensity * 4f).toInt()).coerceIn(1, 5)
                repeat(bandCount) { index ->
                    val salt = 3 + index * 13
                    val placement = GlitchNoise.sample(frame.seed, frame.step, salt = salt)
                    val bandHeight = (6f + 30f * GlitchNoise.sample(frame.seed, frame.step, salt = salt + 1)).dp.toPx()
                    val top = (size.height * placement).coerceIn(0f, (size.height - 1f).coerceAtLeast(0f))
                    val bottom = (top + bandHeight).coerceAtMost(size.height)
                    if (bottom - top < 1f) return@repeat

                    val bandTint = if (index % 2 == 0) accent else accentAlt
                    drawRect(
                        color = bandTint.copy(alpha = 0.03f + 0.07f * intensity),
                        topLeft = Offset(0f, top),
                        size = Size(size.width, bottom - top)
                    )

                    val shift = GlitchNoise.range(frame.seed, frame.step, -26f, 26f, salt = salt + 2).dp.toPx()
                    val segments = 2 + index % 3
                    repeat(segments) { segment ->
                        val segmentPlacement = GlitchNoise.sample(frame.seed, frame.step, salt = salt + 3 + segment)
                        val segmentWidth = (size.width * (0.08f + 0.3f * segmentPlacement))
                        val segmentX = (size.width * segmentPlacement - segmentWidth / 2f)
                            .coerceIn(0f, (size.width - segmentWidth).coerceAtLeast(0f))
                        drawRect(
                            color = bandTint.copy(alpha = 0.05f + 0.11f * intensity),
                            topLeft = Offset(segmentX + shift, top),
                            size = Size(segmentWidth, (bottom - top).coerceAtLeast(1f))
                        )
                    }

                    drawLine(
                        color = bandTint.copy(alpha = 0.3f + 0.5f * intensity),
                        start = Offset(0f, top),
                        end = Offset(size.width, top),
                        strokeWidth = 1f
                    )
                }

                if (intensity > 0.55f) {
                    val tearShift = GlitchNoise.range(frame.seed, frame.step, -14f, 14f, salt = 41).dp.toPx()
                    val tearY = GlitchNoise.sample(frame.seed, frame.step, salt = 42) * size.height
                    drawRect(
                        color = Color.Black.copy(alpha = 0.55f),
                        topLeft = Offset(tearShift, tearY),
                        size = Size(size.width, 3.dp.toPx())
                    )
                    drawLine(
                        color = accent.copy(alpha = 0.7f),
                        start = Offset(tearShift, tearY),
                        end = Offset(tearShift + size.width, tearY),
                        strokeWidth = 1f
                    )
                }
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}
