package com.unscroll.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.unscroll.app.ui.theme.AmbientDarkBackground
import com.unscroll.app.ui.theme.AmbientGlowGold
import com.unscroll.app.ui.theme.AmbientGlowOlive

@Composable
fun AmbientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AmbientGlowOlive,
                        AmbientGlowGold.copy(alpha = 0.6f),
                        AmbientDarkBackground
                    ),
                    center = Offset(700f, 600f),
                    radius = 1200f
                )
            )
    ) {
        content()
    }
}
