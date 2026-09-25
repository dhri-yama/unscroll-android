package com.unscroll.app.service.accessibility

internal data class ScrollGesture(
    val distancePx: Long,
    val lastEventTimestampMillis: Long,
    val sessionId: Long
)

internal class ScrollGestureAccumulator(
    idleTimeMillis: Long
) {
    private val idleTime = idleTimeMillis.coerceAtLeast(1L)
    private val pendingGestures = mutableMapOf<String, PendingGesture>()

    fun add(
        sourceKey: String,
        distancePx: Long,
        timestampMillis: Long,
        sessionId: Long = 0L
    ): ScrollGesture? {
        if (distancePx <= 0L) return null

        var completedGesture: ScrollGesture? = null
        val current = pendingGestures[sourceKey]
        if (current != null && current.sessionId != sessionId) {
            completedGesture = pendingGestures.remove(sourceKey)?.toGesture()
        } else if (current != null && timestampMillis - current.lastEventTimestampMillis > idleTime) {
            completedGesture = pendingGestures.remove(sourceKey)?.toGesture()
        }

        val target = pendingGestures.getOrPut(sourceKey) {
            PendingGesture(sessionId = sessionId, lastEventTimestampMillis = timestampMillis)
        }
        target.distancePx += distancePx
        target.lastEventTimestampMillis = timestampMillis
        return completedGesture
    }

    fun complete(sourceKey: String, sessionId: Long? = null): ScrollGesture? {
        val pendingGesture = pendingGestures[sourceKey] ?: return null
        if (sessionId != null && pendingGesture.sessionId != sessionId) return null
        return pendingGestures.remove(sourceKey)?.toGesture()
    }

    fun completeAll(): List<ScrollGesture> {
        return pendingGestures.values.map { it.toGesture() }.also { pendingGestures.clear() }
    }

    fun clear() {
        pendingGestures.clear()
    }

    private fun PendingGesture.toGesture(): ScrollGesture {
        return ScrollGesture(
            distancePx = distancePx,
            lastEventTimestampMillis = lastEventTimestampMillis,
            sessionId = sessionId
        )
    }

    private data class PendingGesture(
        val sessionId: Long,
        var distancePx: Long = 0L,
        var lastEventTimestampMillis: Long
    )
}
