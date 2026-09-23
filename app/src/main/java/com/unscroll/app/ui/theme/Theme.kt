package com.unscroll.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = TickerGold,
    secondary = TextSecondaryMuted,
    background = AmbientDarkBackground,
    surface = GlassPillBackground,
    onBackground = TextPrimaryWhite,
    onSurface = TextPrimaryWhite
)

@Composable
fun UnscrollTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
