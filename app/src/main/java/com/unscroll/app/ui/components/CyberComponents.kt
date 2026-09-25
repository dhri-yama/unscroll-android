package com.unscroll.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unscroll.app.ui.theme.CyberBlack
import com.unscroll.app.ui.theme.CyberDim
import com.unscroll.app.ui.theme.CyberLine
import com.unscroll.app.ui.theme.CyberMuted
import com.unscroll.app.ui.theme.CyberPanel
import com.unscroll.app.ui.theme.CyberSoft
import com.unscroll.app.ui.theme.CyberSurface
import com.unscroll.app.ui.theme.CyberWhite

private val TerminalShape = RoundedCornerShape(0.dp)

@Composable
fun CyberPanel(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .background(CyberPanel, TerminalShape)
            .border(1.dp, CyberLine, TerminalShape)
            .drawBehind {
                val corner = 14.dp.toPx()
                val stroke = 1.dp.toPx()
                drawLine(CyberWhite, androidx.compose.ui.geometry.Offset(0f, corner), androidx.compose.ui.geometry.Offset(0f, 0f), stroke)
                drawLine(CyberWhite, androidx.compose.ui.geometry.Offset(0f, 0f), androidx.compose.ui.geometry.Offset(corner, 0f), stroke)
                drawLine(CyberWhite, androidx.compose.ui.geometry.Offset(size.width - corner, size.height), androidx.compose.ui.geometry.Offset(size.width, size.height), stroke)
                drawLine(CyberWhite, androidx.compose.ui.geometry.Offset(size.width, size.height), androidx.compose.ui.geometry.Offset(size.width, size.height - corner), stroke)
            }
            .padding(contentPadding)
    ) {
        content()
    }
}

@Composable
fun CyberSectionLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        style = MaterialTheme.typography.labelMedium,
        color = CyberMuted
    )
}

@Composable
fun CyberStatusChip(
    text: String,
    modifier: Modifier = Modifier,
    active: Boolean = true
) {
    Box(
        modifier = modifier
            .background(if (active) CyberWhite else Color.Transparent, TerminalShape)
            .border(1.dp, if (active) CyberWhite else CyberLine, TerminalShape)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = if (active) CyberBlack else CyberMuted
        )
    }
}

@Composable
fun CyberProgressRail(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(totalSteps) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(if (index <= currentStep) CyberWhite else CyberLine)
            )
        }
    }
}

@Composable
fun CyberTopBar(
    title: String,
    status: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = CyberWhite
        )
        CyberStatusChip(text = status, active = true)
    }
}

data class CyberNavDestination(
    val label: String,
    val icon: ImageVector
)

@Composable
fun CyberBottomNav(
    destinations: List<CyberNavDestination>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CyberSurface)
            .border(1.dp, CyberLine, TerminalShape),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        destinations.forEachIndexed { index, destination ->
            val selected = selectedIndex == index
            Column(
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 72.dp)
                    .background(if (selected) CyberWhite else Color.Transparent)
                    .clickable { onSelect(index) }
                    .semantics { role = Role.Tab }
                    .padding(vertical = 9.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = destination.icon,
                    contentDescription = destination.label,
                    tint = if (selected) CyberBlack else CyberMuted,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = destination.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selected) CyberBlack else CyberMuted,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun CyberCheckBox(
    checked: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .background(if (checked) CyberWhite else Color.Transparent, TerminalShape)
            .border(1.dp, if (checked) CyberWhite else CyberDim, TerminalShape),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = CyberBlack,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun CyberTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label.uppercase()) },
        placeholder = { Text(placeholder.uppercase(), color = CyberDim) },
        singleLine = singleLine,
        shape = TerminalShape,
        textStyle = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontSize = 16.sp,
            color = CyberWhite
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = CyberWhite,
            unfocusedTextColor = CyberWhite,
            focusedBorderColor = CyberWhite,
            unfocusedBorderColor = CyberLine,
            focusedLabelColor = CyberWhite,
            unfocusedLabelColor = CyberMuted,
            cursorColor = CyberWhite,
            focusedContainerColor = CyberSurface,
            unfocusedContainerColor = CyberSurface
        ),
        modifier = modifier
            .defaultMinSize(minHeight = 58.dp)
            .fillMaxWidth()
    )
}

@Composable
fun GlitchText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.displayLarge,
    color: Color = CyberWhite
) {
    val transition = rememberInfiniteTransition(label = "glitch")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glitch-phase"
    )
    Box(modifier = modifier) {
        Text(
            text = text,
            style = style,
            color = color.copy(alpha = 0.32f),
            modifier = Modifier
                .clearAndSetSemantics { }
                .graphicsLayer {
                    translationX = if (phase > 0.52f) 2f else -2f
                }
        )
        Text(text = text, style = style, color = color)
        if (phase > 0.76f) {
            Text(
                text = text,
                style = style,
                color = CyberWhite.copy(alpha = 0.7f),
                modifier = Modifier
                    .clearAndSetSemantics { }
                    .graphicsLayer {
                        translationX = -3f
                        translationY = 1f
                    }
            )
        }
    }
}

@Composable
fun CyberMetric(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    emphasis: Boolean = false
) {
    Column(modifier = modifier.padding(vertical = 4.dp)) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = if (emphasis) 42.sp else 24.sp,
                color = if (emphasis) CyberWhite else CyberSoft
            ),
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = CyberMuted
        )
    }
}

@Composable
fun CyberRule(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = CyberLine
    )
}
