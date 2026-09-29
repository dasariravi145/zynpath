package com.zynpath.game.core.puzzle.daily

/**
 * Representation of a discrete player attempt at a Daily Challenge.
 *
 * Implements Prompt 16 Section 21 & Prompt 25 Section 10:
 * Tied to a specific challenge and puzzle revision; attempts across different
 * dates are never merged or conflated.
 */
data class DailyChallengeAttempt(
    val attemptId: String,
    val playerId: String = "",
    val challengeId: String = "",
    val dateKey: String = "",
    val puzzleFingerprint: String = "",
    val startedAt: Long = 0L,
    val expiresAt: Long = 0L,
    val status: String = "ACTIVE",
    val puzzleId: String = "",
    val puzzleVersion: Int = 1,
    val isCompleted: Boolean = false,
    val activeElapsedDurationMs: Long = 0L,
    val movesCount: Int = 0,
    val completedAt: Long? = null
)
