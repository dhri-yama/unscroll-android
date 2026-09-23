package com.unscroll.app.domain.model

data class UserProfile(
    val userName: String = "",
    val isPersonalized: Boolean = false,
    val interruptionIntervalMinutes: Int = 10
)
