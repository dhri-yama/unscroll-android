package com.unscroll.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.ui.theme.CyberBlack
import com.unscroll.app.ui.theme.CyberLine
import com.unscroll.app.ui.theme.CyberMuted
import com.unscroll.app.ui.theme.CyberPanel
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
    style: CyberButtonStyle = CyberButtonStyle.Outlined
) {
    val shape = RoundedCornerShape(0.dp)
    val filled = style == CyberButtonStyle.Filled
    val quiet = style == CyberButtonStyle.Quiet
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
            .border(1.dp, borderColor, shape)
            .clickable { onClick() }
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
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        Text(
            text = text.uppercase(),
            color = contentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
        )
    }
}
