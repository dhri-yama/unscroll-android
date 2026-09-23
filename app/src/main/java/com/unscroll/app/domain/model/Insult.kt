package com.unscroll.app.domain.model

data class Insult(
    val id: Int,
    val rawTemplate: String
) {
    fun formatForUser(userName: String?): String {
        val nameToUse = userName?.trim().takeIf { !it.isNullOrBlank() } ?: "Hey scroll-addict"
        return rawTemplate.replace("{name}", nameToUse)
    }
}
