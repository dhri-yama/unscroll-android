package com.unscroll.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.unscroll.app.ui.components.GlitchEngine

private val DarkColorScheme = darkColorScheme(
    primary = CyberWhite,
    onPrimary = CyberBlack,
    secondary = CyberMuted,
    onSecondary = CyberBlack,
    tertiary = CyberSoft,
    onTertiary = CyberBlack,
    background = CyberBlack,
    onBackground = CyberWhite,
    surface = CyberSurface,
    onSurface = CyberWhite,
    surfaceVariant = CyberPanel,
    onSurfaceVariant = CyberMuted,
    outline = CyberLine,
    outlineVariant = CyberDim,
    error = CyberWhite,
    onError = CyberBlack
)

@Composable
fun UnscrollTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography
    ) {
        GlitchEngine(content = content)
    }
}
