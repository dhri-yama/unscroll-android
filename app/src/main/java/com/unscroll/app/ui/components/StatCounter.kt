package com.unscroll.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun StatCounter(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    CyberMetric(
        value = value,
        label = label,
        modifier = modifier
    )
}
