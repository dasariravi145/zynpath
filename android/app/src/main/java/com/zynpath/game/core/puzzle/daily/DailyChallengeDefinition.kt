package com.zynpath.game.core.puzzle.daily

import com.zynpath.game.core.puzzle.model.PuzzleDefinition

/**
 * Immutable definition of a Daily Challenge instance.
 *
 * Implements Prompt 16 Section 8:
 * Uniquely and stably identified by:
 * - [challengeId]: Stable format `daily-YYYY-MM-DD-v{version}`
 * - [dateKey]: Canonical UTC calendar date in `YYYY-MM-DD`
 * - [challengeVersion]: Format / rule revision number
 * - [puzzleId]: Stable identifier of the assigned puzzle
 * - [puzzleVersion]: Revision of the puzzle definition
 * - [puzzleFingerprint]: Canonical SHA-256 fingerprint for tamper and match verification
 * - [scheduleVersion]: Version of the offline mapping schedule
 */
data class DailyChallengeDefinition(
    val challengeId: String,
    val dateKey: String,
    val challengeVersion: Int = 1,
    val puzzleId: String,
    val puzzleVersion: Int = 1,
    val puzzleFingerprint: String,
    val scheduleVersion: String = "1.0.0",
    val puzzleDefinition: PuzzleDefinition,
    val difficultyTier: String = "MEDIUM",
    val title: String = "Daily Challenge"
) {
    init {
        require(challengeId.isNotBlank()) { "Challenge ID must not be blank" }
        require(dateKey.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            "Date key must follow YYYY-MM-DD format: $dateKey"
        }
        require(puzzleId.isNotBlank()) { "Puzzle ID must not be blank" }
        require(puzzleFingerprint.isNotBlank()) { "Puzzle fingerprint must not be blank" }
    }

    val gridDimensions get() = puzzleDefinition.gridDimensions
    val totalRequiredCells get() = puzzleDefinition.totalRequiredCells
    val checkpointCount get() = puzzleDefinition.checkpoints.size
    val wallCount get() = puzzleDefinition.blockedEdges.size
}
