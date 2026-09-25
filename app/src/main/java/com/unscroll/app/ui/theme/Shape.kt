package com.unscroll.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * Shared corner token. The interface is intentionally square, so this stays at
 * zero radius; declared once here so panels, buttons and chart chrome cannot
 * drift apart.
 */
val TerminalShape = RoundedCornerShape(0.dp)
