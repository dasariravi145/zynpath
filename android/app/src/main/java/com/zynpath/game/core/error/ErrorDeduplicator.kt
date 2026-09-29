package com.zynpath.game.core.error

import java.util.concurrent.ConcurrentHashMap

/**
 * Deduplicates error presentation to prevent repeated identical snackbars or dialogs.
 *
 * Implements Prompt 38 Section 71:
 * - Suppresses identical error messages within a sliding window (default 3 seconds).
 * - Avoids notification spam caused by rapid recompositions, repeated network callbacks,
 *   worker retries, or lifecycle restoration.
 */
class ErrorDeduplicator(
    private val suppressionWindowMs: Long = 3000L
) {
    private val lastSeenTimestamp = ConcurrentHashMap<String, Long>()

    /**
     * Returns true if the message should be presented, false if it should be suppressed.
     */
    fun shouldPresent(error: ZynpathError, nowMs: Long = System.currentTimeMillis()): Boolean {
        if (error.isSilent) return false
        val key = "${error.category.name}:${error.userMessage}"
        val lastSeen = lastSeenTimestamp[key]
        if (lastSeen != null && (nowMs - lastSeen) < suppressionWindowMs) {
            return false
        }
        lastSeenTimestamp[key] = nowMs
        pruneOldEntries(nowMs)
        return true
    }

    /**
     * Clears all recorded suppressions.
     */
    fun clear() {
        lastSeenTimestamp.clear()
    }

    private fun pruneOldEntries(nowMs: Long) {
        if (lastSeenTimestamp.size > 50) {
            val threshold = nowMs - (suppressionWindowMs * 2)
            lastSeenTimestamp.entries.removeIf { it.value < threshold }
        }
    }
}
