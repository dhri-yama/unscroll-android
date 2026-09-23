package com.unscroll.app.domain.model

data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val activeTimeMillis: Long,
    val isTracked: Boolean = false
)
