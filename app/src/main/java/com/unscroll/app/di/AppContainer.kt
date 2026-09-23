package com.unscroll.app.di

import android.content.Context
import com.unscroll.app.data.repository.DataStoreSettingsRepositoryImpl
import com.unscroll.app.data.repository.LocalInsultRepositoryImpl
import com.unscroll.app.data.repository.MemorySessionStatsRepositoryImpl
import com.unscroll.app.domain.repository.InsultRepository
import com.unscroll.app.domain.repository.SessionStatsRepository
import com.unscroll.app.domain.repository.SettingsRepository
import com.unscroll.app.domain.usecase.CalculateReelHeuristicsUseCase
import com.unscroll.app.domain.usecase.EvaluateOverlayTriggerUseCase
import com.unscroll.app.domain.usecase.GetNextInsultUseCase
import com.unscroll.app.domain.usecase.TrackScrollEventUseCase

class AppContainer(private val context: Context) {

    val settingsRepository: SettingsRepository by lazy {
        DataStoreSettingsRepositoryImpl(context.applicationContext)
    }

    val insultRepository: InsultRepository by lazy {
        LocalInsultRepositoryImpl(context.applicationContext)
    }

    val sessionStatsRepository: SessionStatsRepository by lazy {
        MemorySessionStatsRepositoryImpl()
    }

    val calculateReelHeuristicsUseCase: CalculateReelHeuristicsUseCase by lazy {
        CalculateReelHeuristicsUseCase()
    }

    val trackScrollEventUseCase: TrackScrollEventUseCase by lazy {
        TrackScrollEventUseCase(sessionStatsRepository, calculateReelHeuristicsUseCase)
    }

    val getNextInsultUseCase: GetNextInsultUseCase by lazy {
        GetNextInsultUseCase(insultRepository, settingsRepository)
    }

    val evaluateOverlayTriggerUseCase: EvaluateOverlayTriggerUseCase by lazy {
        EvaluateOverlayTriggerUseCase()
    }
}
