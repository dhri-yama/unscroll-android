package com.unscroll.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
        }
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}
