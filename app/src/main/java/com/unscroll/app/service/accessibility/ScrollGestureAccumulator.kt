package com.unscroll.app.service.accessibility

internal data class ScrollGesture(
    val distancePx: Long,
    val lastEventTimestampMillis: Long
)

internal class ScrollGestureAccumulator(
    idleTimeMillis: Long
) {
    private val idleTime = idleTimeMillis.coerceAtLeast(1L)
    private val pendingGestures = mutableMapOf<String, PendingGesture>()

    fun add(
        sourceKey: String,
        distancePx: Long,
        timestampMillis: Long
    ): ScrollGesture? {
        if (distancePx <= 0L) return null

        var completedGesture: ScrollGesture? = null
        val current = pendingGestures[sourceKey]
        if (current != null && timestampMillis - current.lastEventTimestampMillis > idleTime) {
            completedGesture = pendingGestures.remove(sourceKey)?.toGesture()
        }

        val target = pendingGestures.getOrPut(sourceKey) {
            PendingGesture(lastEventTimestampMillis = timestampMillis)
        }
        target.distancePx += distancePx
        target.lastEventTimestampMillis = timestampMillis
        return completedGesture
    }

    fun complete(sourceKey: String): ScrollGesture? {
        return pendingGestures.remove(sourceKey)?.toGesture()
    }

    fun completeAll(): List<ScrollGesture> {
        return pendingGestures.values.map { it.toGesture() }.also { pendingGestures.clear() }
    }

    private fun PendingGesture.toGesture(): ScrollGesture {
        return ScrollGesture(
            distancePx = distancePx,
            lastEventTimestampMillis = lastEventTimestampMillis
        )
    }

    private data class PendingGesture(
        var distancePx: Long = 0L,
        var lastEventTimestampMillis: Long
    )
}
