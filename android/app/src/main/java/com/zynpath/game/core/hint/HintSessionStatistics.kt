package com.zynpath.game.core.hint

/**
 * In-memory telemetry capturing hint query statistics for active gameplay.
 *
 * Implements Prompt 14 Section 33:
 * Tracks hints requested, successfully delivered, recovery hints, and hints used in attempt.
 */
data class HintSessionStatistics(
    var hintsRequested: Int = 0,
    var hintsDelivered: Int = 0,
    var recoveryHintsDelivered: Int = 0,
    var hintsUsedInAttempt: Int = 0
) {
    fun recordRequest() {
        hintsRequested++
    }

    fun recordDelivery(isRecovery: Boolean) {
        hintsDelivered++
        if (isRecovery) {
            recoveryHintsDelivered++
        }
        hintsUsedInAttempt++
    }

    fun reset() {
        hintsRequested = 0
        hintsDelivered = 0
        recoveryHintsDelivered = 0
        hintsUsedInAttempt = 0
    }
}
