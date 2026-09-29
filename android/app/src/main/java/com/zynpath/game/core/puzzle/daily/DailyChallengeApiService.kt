package com.zynpath.game.core.puzzle.daily

import com.zynpath.game.core.network.NetworkResult

/**
 * Network API contract for Daily Challenge online verification,
 * canonical puzzle delivery, competitive attempts, and daily leaderboards.
 *
 * Implements Prompt 25 Sections 15, 17, 28, 36, 40, 52 & 53.
 */
interface DailyChallengeApiService {

    suspend fun getChallenge(dateKey: String? = null): NetworkResult<DailyChallengeDefinition>

    suspend fun startOfficialAttempt(
        sessionToken: String,
        dateKey: String? = null
    ): NetworkResult<DailyChallengeAttempt>

    suspend fun getActiveAttempt(
        sessionToken: String,
        dateKey: String? = null
    ): NetworkResult<DailyChallengeAttempt?>

    suspend fun submitCompletion(
        sessionToken: String,
        attemptId: String,
        challengeId: String,
        puzzleFingerprint: String,
        pathCoordinates: List<String>,
        clientElapsedMs: Long? = null
    ): NetworkResult<DailyChallengeOnlineResult>

    suspend fun getPersonalResult(
        sessionToken: String,
        dateKey: String? = null
    ): NetworkResult<DailyChallengeOnlineResult?>

    suspend fun getDailyLeaderboard(
        sessionToken: String?,
        dateKey: String? = null,
        page: Int = 0,
        pageSize: Int = 20
    ): NetworkResult<DailyLeaderboardResponse>

    suspend fun syncProvisional(
        sessionToken: String,
        challengeId: String,
        dateKey: String,
        fingerprint: String,
        solveTimeMs: Long,
        completedAt: Long,
        pathCoordinates: List<String>? = null,
        movesCount: Int = 0
    ): NetworkResult<DailyChallengeOnlineResult>
}
