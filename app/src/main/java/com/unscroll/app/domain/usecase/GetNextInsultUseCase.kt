package com.unscroll.app.domain.usecase

import com.unscroll.app.domain.model.Insult
import com.unscroll.app.domain.repository.InsultRepository
import com.unscroll.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first

class GetNextInsultUseCase(
    private val insultRepository: InsultRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(): Insult {
        val profile = settingsRepository.getUserProfile().first()
        val insult = insultRepository.getNextInsult(profile.userName)
        insultRepository.advanceInsultIndex()
        return insult
    }
}
