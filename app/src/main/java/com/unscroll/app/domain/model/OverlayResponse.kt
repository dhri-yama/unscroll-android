package com.unscroll.app.domain.model

/**
 * Which control the operator used to leave an intervention overlay.
 *
 * Both responses dismiss the overlay; they are recorded separately so daily
 * history can distinguish giving in from the primary control versus the
 * secondary one.
 */
enum class OverlayResponse {
    SKIP,
    LOCK
}
