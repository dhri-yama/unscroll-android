package com.unscroll.app.service.overlay

import com.unscroll.app.domain.model.Insult
import com.unscroll.app.domain.model.OverlayResponse
import com.unscroll.app.domain.model.SessionStats

interface OverlayController {
    /**
     * @param onDismiss invoked with the control the operator used to leave the
     * overlay. Both controls dismiss; the response is reported so callers can
     * record which one was used.
     */
    fun showOverlay(
        stats: SessionStats,
        insult: Insult,
        onDismiss: (OverlayResponse) -> Unit
    )
    fun dismissOverlay()
    fun isOverlayShowing(): Boolean
}
