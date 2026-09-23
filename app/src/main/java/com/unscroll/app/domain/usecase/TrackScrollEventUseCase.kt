package com.unscroll.app.domain.usecase

import com.unscroll.app.domain.repository.SessionStatsRepository

class TrackScrollEventUseCase(
    private val sessionStatsRepository: SessionStatsRepository,
    private val calculateReelHeuristicsUseCase: CalculateReelHeuristicsUseCase
) {
    operator fun invoke(deltaPx: Long, screenHeightPx: Int, timestampMillis: Long = System.currentTimeMillis()) {
        if (deltaPx <= 0) return
        val isReel = calculateReelHeuristicsUseCase.isReelAdvance(deltaPx, screenHeightPx, timestampMillis)
        sessionStatsRepository.recordScroll(deltaPx, isReel)
    }
}
