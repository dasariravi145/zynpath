package com.zynpath.game.core.puzzle.model

/**
 * Domain-level contract representing an authoritatively validated puzzle completion.
 * Only a result validated by the puzzle engine may update persistent player progress.
 */
data class ValidatedCompletionResult(
    val puzzleId: String = "",
    val levelId: Int,
    val worldId: Int,
    val isValidated: Boolean = true,
    val elapsedTimeMs: Long,
    val moveCount: Int,
    val hintCount: Int,
    val undoCount: Int = 0,
    val resetCount: Int = 0,
    val completedAt: Long = System.currentTimeMillis(),
    val puzzleSeed: Long = 0L,
    val puzzleVersion: Int = 1,
    val totalCellsCovered: Int = 0
) {
    val undoResetCount: Int get() = undoCount + resetCount

    init {
        require(isValidated) { "Only validated puzzle completions can be persisted" }
        require(levelId in 1..WorldConfiguration.TOTAL_LEVELS) {
            "Level ID must be within 1..${WorldConfiguration.TOTAL_LEVELS}, was $levelId"
        }
        require(worldId in 1..WorldConfiguration.TOTAL_WORLDS) {
            "World ID must be within 1..${WorldConfiguration.TOTAL_WORLDS}, was $worldId"
        }
        require(elapsedTimeMs > 0) { "Elapsed time must be positive, was $elapsedTimeMs ms" }
        require(moveCount > 0) { "Move count must be positive, was $moveCount" }
        require(hintCount >= 0) { "Hint count cannot be negative, was $hintCount" }
        require(undoCount >= 0) { "Undo count cannot be negative, was $undoCount" }
        require(resetCount >= 0) { "Reset count cannot be negative, was $resetCount" }
        require(completedAt > 0) { "Completion timestamp must be positive, was $completedAt" }
    }
}

/**
 * Configurable star rating calculation policy.
 *
 * Implements Phase C:
 * - Star 1: Valid puzzle completion.
 * - Star 2: Valid completion without using a hint (hintCount <= 0).
 * - Star 3: Valid completion without hints (hintCount <= 0) and with at most one Undo/Reset action (undoResetCount <= 1).
 */
object StarRatingPolicy {
    fun calculateStars(
        hintCount: Int,
        undoResetCount: Int = 0,
        timeTakenMs: Long = 0L
    ): Int {
        return when {
            hintCount > 0 -> 1
            undoResetCount > 1 -> 2
            else -> 3
        }
    }
}
