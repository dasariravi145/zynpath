package com.zynpath.game.core.puzzle.daily

import com.zynpath.game.core.database.entity.DailyChallengeEntity
import kotlinx.coroutines.flow.Flow

/**
 * High-level presentation summary of today's Daily Challenge status.
 */
data class DailyChallengeSummary(
    val definition: DailyChallengeDefinition,
    val availability: DailyChallengeAvailability,
    val entity: DailyChallengeEntity?,
    val millisUntilReset: Long,
    val currentStreak: Int,
    val maxStreak: Int
) {
    companion object {
        fun empty(dateKey: String): DailyChallengeSummary = DailyChallengeSummary(
            definition = DailyChallengeDefinition(
                challengeId = "daily-$dateKey-v1",
                dateKey = dateKey,
                challengeVersion = 1,
                puzzleId = "puzzle-$dateKey",
                puzzleVersion = 1,
                puzzleFingerprint = "fingerprint-$dateKey",
                scheduleVersion = "1.0.0",
                puzzleDefinition = com.zynpath.game.core.puzzle.catalog.PackagedPuzzles.LEVEL_1
            ),
            availability = DailyChallengeAvailability.AVAILABLE,
            entity = null,
            millisUntilReset = 86_400_000L,
            currentStreak = 0,
            maxStreak = 0
        )
    }
}

/**
 * Authoritative repository contract for accessing Daily Challenge schedules,
 * user participation status, streaks, and completion persistence.
 *
 * Implements Prompt 16 Sections 6, 7, 27, 28, 29 & 30.
 */
interface DailyChallengeRepository {

    /**
     * Resolves the authoritative [DailyChallengeDefinition] for today's UTC calendar date.
     */
    fun getTodayChallenge(): DailyChallengeDefinition

    /**
     * Resolves the [DailyChallengeDefinition] for an arbitrary UTC date key.
     */
    fun getChallengeForDate(dateKey: String): DailyChallengeDefinition

    /**
     * Observes the active status for today's challenge.
     */
    fun observeTodaySummary(): Flow<DailyChallengeSummary>

    /**
     * Observes availability state of today's challenge.
     */
    fun observeTodayAvailability(): Flow<DailyChallengeAvailability>

    /**
     * Observes current consecutive daily streak.
     */
    fun observeCurrentStreak(): Flow<Int>

    /**
     * Observes all-time maximum daily streak.
     */
    fun observeMaxStreak(): Flow<Int>

    /**
     * Observes all completed challenges in history.
     */
    fun observeCompletedChallenges(): Flow<List<DailyChallengeEntity>>

    /**
     * Records that an attempt has been initiated for the challenge.
     */
    suspend fun recordAttemptStarted(challenge: DailyChallengeDefinition)

    /**
     * Transactionally and idempotently records a validated engine victory.
     */
    suspend fun recordCompletion(
        challenge: DailyChallengeDefinition,
        solveTimeMs: Long,
        movesCount: Int,
        verificationStatus: DailyVerificationStatus = DailyVerificationStatus.LOCAL_COMPLETION,
        serverAttemptId: String? = null,
        isLeaderboardEligible: Boolean = false
    ): Boolean

    /**
     * Retrieves the official canonical Daily Challenge from the backend.
     */
    suspend fun getOfficialChallengeOnline(dateKey: String? = null): Result<DailyChallengeDefinition>

    /**
     * Initiates an official server-authoritative competitive attempt.
     */
    suspend fun startOfficialAttempt(dateKey: String? = null): Result<DailyChallengeAttempt>

    /**
     * Retrieves any active unexpired official attempt from the backend.
     */
    suspend fun getActiveAttempt(dateKey: String? = null): Result<DailyChallengeAttempt?>

    /**
     * Submits a completed path to the backend for independent server validation and leaderboard placement.
     */
    suspend fun submitOfficialCompletion(
        attemptId: String,
        challenge: DailyChallengeDefinition,
        pathCoordinates: List<String>,
        clientElapsedMs: Long? = null
    ): Result<DailyChallengeOnlineResult>

    /**
     * Retrieves personal verified result from the backend.
     */
    suspend fun getPersonalResultOnline(dateKey: String? = null): Result<DailyChallengeOnlineResult?>

    /**
     * Retrieves the official paginated Daily Leaderboard.
     */
    suspend fun getDailyLeaderboard(
        dateKey: String? = null,
        page: Int = 0,
        pageSize: Int = 20
    ): Result<DailyLeaderboardResponse>

    /**
     * Synchronizes a local provisional offline completion without placing untrusted times on the timed leaderboard.
     */
    suspend fun syncProvisional(
        challenge: DailyChallengeDefinition,
        solveTimeMs: Long,
        completedAt: Long,
        path: List<String>? = null,
        movesCount: Int = 0
    ): Result<DailyChallengeOnlineResult>
}

