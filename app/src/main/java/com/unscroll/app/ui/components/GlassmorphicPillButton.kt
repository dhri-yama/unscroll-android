package com.unscroll.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.ui.theme.CyberBlack
import com.unscroll.app.ui.theme.CyberLine
import com.unscroll.app.ui.theme.CyberMuted
import com.unscroll.app.ui.theme.CyberPanel
import com.unscroll.app.ui.theme.CyberSoft
import com.unscroll.app.ui.theme.CyberWhite

enum class CyberButtonStyle {
    Filled,
    Outlined,
    Quiet
}

@Composable
fun GlassmorphicPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconPrefix: String? = null,
    style: CyberButtonStyle = CyberButtonStyle.Outlined,
    glitchSeed: Int = 0x3F7C
) {
    val shape = RoundedCornerShape(0.dp)
    val filled = style == CyberButtonStyle.Filled
    val quiet = style == CyberButtonStyle.Quiet
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val frame = glitchFrame(glitchSeed, gain = 1.15f)
    val label = text.uppercase()
    val displayed = remember(frame.step, frame.intensity, pressed, label) {
        when {
            pressed -> glitchedText(label, frame, strength = 0.5f)
            frame.isActive -> glitchedText(label, frame, strength = 0.22f)
            else -> label
        }
    }
    val background = when {
        filled -> CyberWhite
        quiet -> CyberPanel
        else -> androidx.compose.ui.graphics.Color.Transparent
    }
    val borderColor = when {
        filled -> CyberWhite
        quiet -> CyberLine
        else -> CyberWhite
    }
    val contentColor = if (filled) CyberBlack else CyberWhite

    Row(
        modifier = modifier
            .background(background, shape)
            .border(
                width = if (frame.isActive && !pressed) 2.dp else 1.dp,
                color = if (frame.isActive) CyberSoft else borderColor,
                shape = shape
            )
            .glitchShimmer(
                frame = if (pressed) frame.copy(intensity = 1f) else frame,
                activeColor = CyberWhite,
                idleColor = CyberWhite,
                minAlpha = 0.3f,
                maxShiftDp = if (pressed) 3.5f else 2.4f,
                verticalShiftDp = if (pressed) 1.4f else 0.8f
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .defaultMinSize(minHeight = 52.dp)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!iconPrefix.isNullOrBlank()) {
            Text(
                text = iconPrefix,
                color = contentColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .glitchJitter(frame = frame, maxShiftDp = 2f, verticalShiftDp = 0.6f)
            )
        }
        Text(
            text = displayed,
            color = contentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            modifier = Modifier.clearAndSetSemantics { contentDescription = text }
        )
    }
}
