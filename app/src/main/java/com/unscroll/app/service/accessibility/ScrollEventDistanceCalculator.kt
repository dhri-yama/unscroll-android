package com.unscroll.app.service.accessibility

import kotlin.math.abs

internal data class ScrollEventSnapshot(
    val sourceKey: String,
    val scrollY: Int,
    val scrollDeltaY: Int,
    val currentItemIndex: Int,
    val fromIndex: Int
)

internal class ScrollEventDistanceCalculator(
    estimatedItemHeightPx: Long
) {
    private val itemHeightPx = estimatedItemHeightPx.coerceAtLeast(1L)
    private val previousPositions = mutableMapOf<String, PreviousPosition>()

    fun calculate(snapshot: ScrollEventSnapshot): Long {
        val previous = previousPositions[snapshot.sourceKey]
        val currentItemIndex = snapshot.currentItemIndex.takeIf { it >= 0 }
        val fromIndex = snapshot.fromIndex.takeIf { it >= 0 }
        val deltaPx = when {
            snapshot.scrollDeltaY != 0 && snapshot.scrollDeltaY != UNDEFINED_SCROLL_DELTA ->
                abs(snapshot.scrollDeltaY.toLong())
            previous != null && previous.scrollY != snapshot.scrollY ->
                abs(snapshot.scrollY.toLong() - previous.scrollY)
            previous?.currentItemIndex != null && currentItemIndex != null ->
                abs(currentItemIndex.toLong() - previous.currentItemIndex) * itemHeightPx
            previous?.fromIndex != null && fromIndex != null ->
                abs(fromIndex.toLong() - previous.fromIndex) * itemHeightPx
            else -> 0L
        }

        previousPositions[snapshot.sourceKey] = PreviousPosition(
            scrollY = snapshot.scrollY,
            currentItemIndex = currentItemIndex,
            fromIndex = fromIndex
        )
        return deltaPx
    }

    fun clear() {
        previousPositions.clear()
    }

    private data class PreviousPosition(
        val scrollY: Int,
        val currentItemIndex: Int?,
        val fromIndex: Int?
    )

    private companion object {
        const val UNDEFINED_SCROLL_DELTA = -1
    }
}
