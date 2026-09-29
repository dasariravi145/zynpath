package com.zynpath.game.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.zynpath.game.core.database.entity.DailyChallengeEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for local daily challenge persistence.
 *
 * Implements Prompt 16 Sections 27, 28, 29 & 33.
 */
@Dao
interface DailyChallengeDao {

    @Query("SELECT * FROM daily_challenge WHERE dateKey = :dateKey LIMIT 1")
    fun getDailyChallenge(dateKey: String): Flow<DailyChallengeEntity?>

    @Query("SELECT * FROM daily_challenge WHERE dateKey = :dateKey LIMIT 1")
    suspend fun getDailyChallengeSync(dateKey: String): DailyChallengeEntity?

    @Query("SELECT * FROM daily_challenge ORDER BY dateKey DESC")
    fun getAllDailyChallenges(): Flow<List<DailyChallengeEntity>>

    @Query("SELECT * FROM daily_challenge WHERE isCompleted = 1 ORDER BY dateKey DESC")
    fun getAllCompletedChallenges(): Flow<List<DailyChallengeEntity>>

    @Query("SELECT * FROM daily_challenge WHERE isCompleted = 1 ORDER BY dateKey DESC")
    suspend fun getAllCompletedList(): List<DailyChallengeEntity>

    @Query("SELECT COUNT(*) FROM daily_challenge WHERE isCompleted = 1")
    fun getCompletedCount(): Flow<Int>

    @Query("SELECT dateKey FROM daily_challenge WHERE isCompleted = 1 ORDER BY dateKey DESC")
    fun getAllCompletedDateKeys(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyChallenge(challenge: DailyChallengeEntity)

    @Query("UPDATE daily_challenge SET attemptCount = attemptCount + 1, lastAttemptAt = :timestamp WHERE dateKey = :dateKey")
    suspend fun incrementAttemptCount(dateKey: String, timestamp: Long)

    @Transaction
    suspend fun recordCompletion(
        dateKey: String,
        challengeId: String,
        puzzleId: String,
        puzzleVersion: Int,
        puzzleFingerprint: String,
        scheduleVersion: String,
        gridSize: Int,
        solveTimeMs: Long,
        completedAt: Long,
        movesCount: Int,
        verificationStatus: String = "LOCAL_COMPLETION",
        serverAttemptId: String? = null,
        isLeaderboardEligible: Boolean = false
    ) {
        val existing = getDailyChallengeSync(dateKey)
        val currentBest = existing?.bestTimeMs?.takeIf { it > 0 }
        val newBestTime = if (currentBest != null) minOf(currentBest, solveTimeMs) else solveTimeMs
        val attempts = (existing?.attemptCount ?: 0) + 1
        val firstAttempt = existing?.firstAttemptAt?.takeIf { it > 0 } ?: completedAt

        val updated = DailyChallengeEntity(
            dateKey = dateKey,
            challengeId = challengeId,
            challengeVersion = existing?.challengeVersion ?: 1,
            puzzleId = puzzleId,
            puzzleVersion = puzzleVersion,
            puzzleFingerprint = puzzleFingerprint,
            scheduleVersion = scheduleVersion,
            seed = existing?.seed ?: 0L,
            gridSize = gridSize,
            isCompleted = true,
            solveTimeMs = solveTimeMs,
            completedAt = completedAt,
            attemptCount = attempts,
            bestTimeMs = newBestTime,
            firstAttemptAt = firstAttempt,
            lastAttemptAt = completedAt,
            movesCount = movesCount,
            verificationStatus = verificationStatus,
            serverAttemptId = serverAttemptId ?: existing?.serverAttemptId,
            isLeaderboardEligible = isLeaderboardEligible || (existing?.isLeaderboardEligible ?: false)
        )
        upsertDailyChallenge(updated)
    }

    @Query("UPDATE daily_challenge SET verificationStatus = :verificationStatus, serverAttemptId = :serverAttemptId, isLeaderboardEligible = :isLeaderboardEligible WHERE dateKey = :dateKey")
    suspend fun updateVerificationStatus(
        dateKey: String,
        verificationStatus: String,
        serverAttemptId: String?,
        isLeaderboardEligible: Boolean
    )
}

