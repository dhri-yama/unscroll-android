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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
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
import com.unscroll.app.ui.theme.TerminalShape

@Composable
fun CyberPanel(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    glitchSeed: Int = 0x51AB,
    content: @Composable () -> Unit
) {
    val frame = glitchFrame(glitchSeed, gain = 0.85f)
    Column(
        modifier = modifier
            .background(CyberPanel, TerminalShape)
            .border(1.dp, CyberLine, TerminalShape)
            .glitchEdgeFlicker(
                frame = frame,
                idleColor = CyberWhite,
                tintColor = CyberWhite
            )
            .glitchShimmer(
                frame = frame,
                maxShiftDp = 1.6f,
                verticalShiftDp = 0.8f
            )
            .padding(contentPadding)
    ) {
        content()
    }
}

@Composable
fun CyberSectionLabel(
    text: String,
    modifier: Modifier = Modifier,
    glitchSeed: Int = 0x2C0D
) {
    val frame = glitchFrame(glitchSeed, gain = 0.7f)
    val source = text.uppercase()
    val displayed = remember(frame.step, frame.intensity, source) {
        glitchedText(source, frame, strength = 0.3f)
    }
    Text(
        text = displayed,
        modifier = modifier
            .clearAndSetSemantics { contentDescription = source }
            .glitchShimmer(
                frame = frame,
                minAlpha = 0.25f
            ),
        style = MaterialTheme.typography.labelMedium,
        color = CyberMuted
    )
}

@Composable
fun CyberStatusChip(
    text: String,
    modifier: Modifier = Modifier,
    active: Boolean = true,
    glitchSeed: Int = 0xC41F
) {
    val frame = glitchFrame(glitchSeed, gain = 1.15f)
    val source = text.uppercase()
    val displayed = remember(frame.step, frame.intensity, source) {
        glitchedText(source, frame, strength = 0.4f)
    }
    Box(
        modifier = modifier
            .background(if (active) CyberWhite else Color.Transparent, TerminalShape)
            .border(
                width = 1.dp,
                color = if (frame.isActive && active) CyberSoft else if (active) CyberWhite else CyberLine,
                shape = TerminalShape
            )
            .glitchJitter(frame = frame, maxShiftDp = 2.2f, verticalShiftDp = 0.6f)
            .glitchSlices(frame = frame, strength = 0.4f)
            .clearAndSetSemantics { contentDescription = source }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = displayed,
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
    val frame = glitchFrame(0x1D3A, gain = 0.9f)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(totalSteps) { index ->
            val lit = index <= currentStep
            val segmentFrame = glitchFrame(0x1D3A + index * 41, gain = if (lit) 1.1f else 0.4f)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(
                        color = when {
                            !lit -> CyberLine
                            segmentFrame.isActive -> CyberSoft
                            else -> CyberWhite
                        },
                        shape = TerminalShape
                    )
                    .glitchJitter(
                        frame = segmentFrame,
                        maxShiftDp = 1.6f,
                        verticalShiftDp = 0f
                    )
                    .glitchShimmer(
                        frame = segmentFrame,
                        minAlpha = 0.45f
                    )
            )
        }
    }
}

@Composable
fun CyberTopBar(
    title: String,
    status: String,
    modifier: Modifier = Modifier,
    glitchSeed: Int = 0x70BB
) {
    val frame = glitchFrame(glitchSeed, gain = 1f)
    val label = title.uppercase()
    val displayed = remember(frame.step, frame.intensity, label) {
        glitchedText(label, frame, strength = 0.28f)
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = displayed,
            modifier = Modifier
                .clearAndSetSemantics { contentDescription = label }
                .glitchJitter(frame = frame, maxShiftDp = 2f, verticalShiftDp = 0.6f)
                .glitchShimmer(
                    frame = frame,
                    minAlpha = 0.3f
                ),
            style = MaterialTheme.typography.labelLarge,
            color = CyberWhite
        )
        CyberStatusChip(text = status, active = true, glitchSeed = glitchSeed + 3)
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
    val frame = glitchFrame(0x4A11, gain = 0.6f)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CyberSurface)
            .border(1.dp, CyberLine, TerminalShape)
            .glitchJitter(frame = frame, maxShiftDp = 1.4f, verticalShiftDp = 0f),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        destinations.forEachIndexed { index, destination ->
            val selected = selectedIndex == index
            val itemFrame = glitchFrame(0x4A11 + index * 97, gain = if (selected) 1.2f else 0.5f)
            val label = destination.label.uppercase()
            val displayed = remember(itemFrame.step, itemFrame.intensity, label, selected) {
                if (selected) glitchedText(label, itemFrame, strength = 0.45f) else label
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 72.dp)
                    .background(if (selected) CyberWhite else Color.Transparent)
                    .glitchShimmer(
                        frame = itemFrame,
                        minAlpha = 0.5f
                    )
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
                    modifier = Modifier
                        .size(20.dp)
                        .glitchJitter(frame = itemFrame, maxShiftDp = 1.6f, verticalShiftDp = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = displayed,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selected) CyberBlack else CyberMuted,
                    maxLines = 1,
                    modifier = Modifier.clearAndSetSemantics { contentDescription = destination.label }
                )
            }
        }
    }
}

@Composable
fun CyberCheckBox(
    checked: Boolean,
    modifier: Modifier = Modifier,
    glitchSeed: Int = 0x9A31
) {
    val frame = glitchFrame(glitchSeed, gain = 1.1f)
    Box(
        modifier = modifier
            .size(24.dp)
            .background(if (checked) CyberWhite else Color.Transparent, TerminalShape)
            .border(
                width = 1.dp,
                color = if (checked) CyberWhite else CyberDim,
                shape = TerminalShape
            )
            .glitchShimmer(
                frame = frame,
                minAlpha = 0.2f,
                maxShiftDp = 1.8f,
                verticalShiftDp = 0.6f
            ),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = CyberBlack,
                modifier = Modifier
                    .size(16.dp)
                    .glitchShimmer(
                        frame = frame,
                        minAlpha = 0.2f
                    )
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
    color: Color = CyberWhite,
    glitchSeed: Int = 0x61A5,
    scramble: Boolean = true
) {
    val frame = glitchFrame(glitchSeed, gain = 1.2f)
    val displayed = remember(frame.step, frame.intensity, text, scramble) {
        if (scramble) glitchedText(text, frame, strength = 0.42f) else text
    }
    val shiftPx = if (frame.isActive) {
        GlitchNoise.range(frame.seed, frame.step, -5f, 5f, salt = 41)
    } else {
        0f
    }
    val sliceTear = frame.isActive && frame.intensity > 0.45f
    val lead = frame.accentFor(swap = false)
    val counter = frame.counterAccentFor(swap = false)
    val splitAlpha = if (frame.isActive) (0.35f + 0.45f * frame.intensity).coerceAtMost(0.85f) else 0f

    Box(
        modifier = modifier
            .glitchJitter(frame = frame, maxShiftDp = 1.2f, verticalShiftDp = 0.4f)
    ) {
        if (frame.isActive) {
            Text(
                text = displayed,
                style = style,
                color = lead.copy(alpha = splitAlpha),
                modifier = Modifier
                    .clearAndSetSemantics { }
                    .graphicsLayer {
                        translationX = shiftPx - 2.5f
                        translationY = 0.8f
                    }
            )
        } else {
            Text(
                text = displayed,
                style = style,
                color = color.copy(alpha = 0.30f),
                modifier = Modifier
                    .clearAndSetSemantics { }
                    .graphicsLayer {
                        translationX = shiftPx - 2.5f
                        translationY = 0.8f
                    }
            )
        }
        Text(
            text = displayed,
            style = style,
            color = color,
            modifier = Modifier
                .clearAndSetSemantics { contentDescription = text }
                .graphicsLayer {
                    translationX = 0f
                }
        )
        if (frame.isActive) {
            Text(
                text = displayed,
                style = style,
                color = counter.copy(alpha = splitAlpha),
                modifier = Modifier
                    .clearAndSetSemantics { }
                    .graphicsLayer {
                        translationX = shiftPx + 3.2f
                        translationY = -0.9f
                    }
            )
        }
        if (sliceTear) {
            val density = LocalDensity.current
            val tearHeightPx = with(density) { 7f.dp.toPx() }
            val tearTop = with(density) { 2f.dp.toPx() }
            Text(
                text = displayed,
                style = style,
                color = color.copy(alpha = 0.75f),
                modifier = Modifier
                    .clearAndSetSemantics { }
                    .graphicsLayer {
                        translationX = shiftPx * 2.2f - 6f
                    }
                    .drawWithContent {
                        clipRect(
                            left = 0f,
                            top = tearTop,
                            right = size.width,
                            bottom = tearTop + tearHeightPx
                        ) {
                            this@drawWithContent.drawContent()
                        }
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
    emphasis: Boolean = false,
    glitchSeed: Int = 0x2F0B
) {
    val frame = glitchFrame(glitchSeed, gain = if (emphasis) 1.2f else 0.9f)
    val displayed = remember(frame.step, frame.intensity, value) {
        glitchedText(value, frame, strength = 0.4f)
    }
    Column(
        modifier = modifier
            .padding(vertical = 4.dp)
            .glitchShimmer(
                frame = frame,
                minAlpha = 0.3f,
                maxShiftDp = 2.4f,
                verticalShiftDp = 0.6f
            )
    ) {
        Text(
            text = displayed,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = if (emphasis) 42.sp else 24.sp,
                color = if (emphasis) CyberWhite else CyberSoft
            ),
            fontWeight = FontWeight.Black,
            modifier = Modifier.clearAndSetSemantics { contentDescription = value }
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = CyberMuted,
            modifier = Modifier
                .clearAndSetSemantics { }
                .glitchJitter(frame = frame, maxShiftDp = 1.2f, verticalShiftDp = 0.4f)
        )
    }
}

@Composable
fun CyberRule(modifier: Modifier = Modifier) {
    val frame = glitchFrame(0x77C3, gain = 0.8f)
    HorizontalDivider(
        modifier = modifier.glitchSlices(frame = frame, strength = 0.4f),
        thickness = 1.dp,
        color = if (frame.isActive) CyberSoft else CyberLine
    )
}
