package com.unscroll.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.ui.theme.GlassPillBackground
import com.unscroll.app.ui.theme.GlassPillBorder
import com.unscroll.app.ui.theme.TextPrimaryWhite

@Composable
fun GlassmorphicPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconPrefix: String? = null
) {
    val shape = RoundedCornerShape(28.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(GlassPillBackground)
            .border(1.dp, GlassPillBorder, shape)
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!iconPrefix.isNullOrBlank()) {
            Text(
                text = iconPrefix,
                color = TextPrimaryWhite,
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        Text(
            text = text,
            color = TextPrimaryWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
