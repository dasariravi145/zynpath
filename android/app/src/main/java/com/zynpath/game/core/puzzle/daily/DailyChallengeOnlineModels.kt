package com.zynpath.game.core.puzzle.daily

/**
 * Domain and network models for Daily Challenge online verification,
 * shared puzzles, competitive attempts, and daily leaderboards.
 *
 * Implements Prompt 25 Sections 8, 13, 17, 31, 35, 36, 40 & 42.
 */

enum class DailyVerificationStatus {
    LOCAL_COMPLETION,
    PROVISIONAL,
    SERVER_VALIDATED,
    LEADERBOARD_ELIGIBLE,
    NOT_ELIGIBLE;

    val displayLabel: String
        get() = when (this) {
            LOCAL_COMPLETION -> "Local Completion"
            PROVISIONAL -> "Provisional"
            SERVER_VALIDATED -> "Server-Validated"
            LEADERBOARD_ELIGIBLE -> "Leaderboard-Eligible"
            NOT_ELIGIBLE -> "Not Eligible"
        }

    companion object {
        val OFFLINE_PROVISIONAL = PROVISIONAL
        val SERVER_VERIFIED = SERVER_VALIDATED
    }
}

data class DailyLeaderboardEntry(
    val rank: Int,
    val publicZynpathId: String,
    val displayName: String,
    val avatarId: String,
    val solveTimeMs: Long,
    val completedAt: Long,
    val verificationStatus: DailyVerificationStatus
)

data class DailyLeaderboardResponse(
    val challengeId: String,
    val dateKey: String,
    val puzzleFingerprint: String,
    val entries: List<DailyLeaderboardEntry>,
    val playerEntry: DailyLeaderboardEntry?,
    val totalEntries: Int,
    val page: Int,
    val pageSize: Int
)

data class DailyChallengeOnlineResult(
    val resultId: String,
    val attemptId: String,
    val playerId: String,
    val publicZynpathId: String,
    val displayName: String,
    val avatarId: String,
    val challengeId: String,
    val dateKey: String,
    val puzzleFingerprint: String,
    val solveTimeMs: Long,
    val completedAt: Long,
    val verificationStatus: DailyVerificationStatus,
    val isLeaderboardEligible: Boolean,
    val rank: Int?
)
