package com.unscroll.app.service.overlay

import com.unscroll.app.domain.model.Insult
import com.unscroll.app.domain.model.SessionStats

interface OverlayController {
    fun showOverlay(stats: SessionStats, insult: Insult, onDismiss: () -> Unit)
    fun dismissOverlay()
    fun isOverlayShowing(): Boolean
}
