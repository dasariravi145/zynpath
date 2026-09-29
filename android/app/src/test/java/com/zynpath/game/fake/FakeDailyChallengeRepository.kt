package com.zynpath.game.fake

import com.zynpath.game.core.database.entity.DailyChallengeEntity
import com.zynpath.game.core.puzzle.daily.DailyChallengeApiService
import com.zynpath.game.core.puzzle.daily.DailyChallengeAttempt
import com.zynpath.game.core.puzzle.daily.DailyChallengeAvailability
import com.zynpath.game.core.puzzle.daily.DailyChallengeClock
import com.zynpath.game.core.puzzle.daily.DailyChallengeDefinition
import com.zynpath.game.core.puzzle.daily.DailyChallengeOnlineResult
import com.zynpath.game.core.puzzle.daily.DailyChallengeRepository
import com.zynpath.game.core.puzzle.daily.DailyChallengeSchedule
import com.zynpath.game.core.puzzle.daily.DailyChallengeSummary
import com.zynpath.game.core.puzzle.daily.DailyLeaderboardResponse
import com.zynpath.game.core.puzzle.daily.DailyVerificationStatus
import com.zynpath.game.core.puzzle.daily.SystemDailyChallengeClock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeDailyChallengeRepository(
    private val clock: DailyChallengeClock = SystemDailyChallengeClock(),
    private val schedule: DailyChallengeSchedule = DailyChallengeSchedule()
) : DailyChallengeRepository {

    private val entities = mutableMapOf<String, DailyChallengeEntity>()
    private val _entitiesFlow = MutableStateFlow<List<DailyChallengeEntity>>(emptyList())
    private val _currentStreak = MutableStateFlow(0)
    private val _maxStreak = MutableStateFlow(0)

    private fun todayKey(): String = clock.currentUtcDateKey()

    override fun getTodayChallenge(): DailyChallengeDefinition = schedule.resolveChallenge(todayKey())

    override fun getChallengeForDate(dateKey: String): DailyChallengeDefinition = schedule.resolveChallenge(dateKey)

    override fun observeTodaySummary(): Flow<DailyChallengeSummary> {
        return _entitiesFlow.map {
            val key = todayKey()
            val entity = entities[key]
            val def = getTodayChallenge()
            val avail = when {
                entity?.isCompleted == true -> DailyChallengeAvailability.COMPLETED
                (entity?.attemptCount ?: 0) > 0 -> DailyChallengeAvailability.IN_PROGRESS
                else -> DailyChallengeAvailability.AVAILABLE
            }
            DailyChallengeSummary(
                definition = def,
                availability = avail,
                entity = entity,
                currentStreak = _currentStreak.value,
                maxStreak = _maxStreak.value,
                millisUntilReset = clock.millisUntilNextReset()
            )
        }
    }

    override fun observeTodayAvailability(): Flow<DailyChallengeAvailability> {
        return observeTodaySummary().map { it.availability }
    }

    override fun observeCurrentStreak(): Flow<Int> = _currentStreak.asStateFlow()

    override fun observeMaxStreak(): Flow<Int> = _maxStreak.asStateFlow()

    override fun observeCompletedChallenges(): Flow<List<DailyChallengeEntity>> = _entitiesFlow.asStateFlow()

    override suspend fun recordAttemptStarted(challenge: DailyChallengeDefinition) {
        val existing = entities[challenge.dateKey]
        if (existing == null) {
            val entity = DailyChallengeEntity(
                dateKey = challenge.dateKey,
                challengeId = challenge.challengeId,
                attemptCount = 1,
                isCompleted = false
            )
            entities[challenge.dateKey] = entity
            _entitiesFlow.value = entities.values.toList()
        }
    }

    override suspend fun recordCompletion(
        challenge: DailyChallengeDefinition,
        solveTimeMs: Long,
        movesCount: Int,
        verificationStatus: DailyVerificationStatus,
        serverAttemptId: String?,
        isLeaderboardEligible: Boolean
    ): Boolean {
        val entity = DailyChallengeEntity(
            dateKey = challenge.dateKey,
            challengeId = challenge.challengeId,
            attemptCount = 1,
            isCompleted = true,
            solveTimeMs = solveTimeMs,
            movesCount = movesCount,
            verificationStatus = verificationStatus.name,
            completedAt = System.currentTimeMillis()
        )
        entities[challenge.dateKey] = entity
        _entitiesFlow.value = entities.values.toList()
        _currentStreak.value += 1
        if (_currentStreak.value > _maxStreak.value) {
            _maxStreak.value = _currentStreak.value
        }
        return true
    }

    override suspend fun getOfficialChallengeOnline(dateKey: String?): Result<DailyChallengeDefinition> {
        val key = dateKey ?: todayKey()
        return Result.success(getChallengeForDate(key))
    }

    override suspend fun startOfficialAttempt(dateKey: String?): Result<DailyChallengeAttempt> {
        val key = dateKey ?: todayKey()
        return Result.success(
            DailyChallengeAttempt(
                attemptId = "attempt_$key",
                dateKey = key,
                challengeId = "daily-$key",
                startedAt = System.currentTimeMillis(),
                expiresAt = System.currentTimeMillis() + 3600000L
            )
        )
    }

    override suspend fun getActiveAttempt(dateKey: String?): Result<DailyChallengeAttempt?> = Result.success(null)

    override suspend fun submitOfficialCompletion(
        attemptId: String,
        challenge: DailyChallengeDefinition,
        pathCoordinates: List<String>,
        clientElapsedMs: Long?
    ): Result<DailyChallengeOnlineResult> {
        return Result.success(
            DailyChallengeOnlineResult(
                resultId = "res_$attemptId",
                attemptId = attemptId,
                playerId = "p_1",
                publicZynpathId = "ZYN-1",
                displayName = "Player",
                avatarId = "av_1",
                challengeId = challenge.challengeId,
                dateKey = challenge.dateKey,
                puzzleFingerprint = challenge.puzzleFingerprint,
                solveTimeMs = clientElapsedMs ?: 15000L,
                completedAt = System.currentTimeMillis(),
                verificationStatus = DailyVerificationStatus.SERVER_VERIFIED,
                isLeaderboardEligible = true,
                rank = 1
            )
        )
    }

    override suspend fun getPersonalResultOnline(dateKey: String?): Result<DailyChallengeOnlineResult?> {
        val key = dateKey ?: todayKey()
        val entity = entities[key] ?: return Result.success(null)
        return Result.success(
            DailyChallengeOnlineResult(
                resultId = "res_${entity.challengeId}",
                attemptId = entity.serverAttemptId ?: "att_local",
                playerId = "p_1",
                publicZynpathId = "ZYN-1",
                displayName = "Player",
                avatarId = "av_1",
                challengeId = entity.challengeId,
                dateKey = key,
                puzzleFingerprint = entity.puzzleFingerprint,
                solveTimeMs = entity.solveTimeMs,
                completedAt = entity.completedAt,
                verificationStatus = DailyVerificationStatus.valueOf(entity.verificationStatus),
                isLeaderboardEligible = entity.isLeaderboardEligible,
                rank = 1
            )
        )
    }

    override suspend fun getDailyLeaderboard(dateKey: String?, page: Int, pageSize: Int): Result<DailyLeaderboardResponse> {
        val key = dateKey ?: todayKey()
        return Result.success(
            DailyLeaderboardResponse(
                challengeId = "challenge_$key",
                dateKey = key,
                puzzleFingerprint = "fp_$key",
                entries = emptyList(),
                playerEntry = null,
                totalEntries = 0,
                page = page,
                pageSize = pageSize
            )
        )
    }

    override suspend fun syncProvisional(
        challenge: DailyChallengeDefinition,
        solveTimeMs: Long,
        completedAt: Long,
        path: List<String>?,
        movesCount: Int
    ): Result<DailyChallengeOnlineResult> {
        return Result.success(
            DailyChallengeOnlineResult(
                resultId = "res_1",
                attemptId = "att_1",
                playerId = "p_1",
                publicZynpathId = "ZYN-1",
                displayName = "Player",
                avatarId = "av_1",
                challengeId = challenge.dateKey,
                dateKey = challenge.dateKey,
                puzzleFingerprint = challenge.puzzleFingerprint,
                solveTimeMs = solveTimeMs,
                completedAt = completedAt,
                verificationStatus = DailyVerificationStatus.OFFLINE_PROVISIONAL,
                isLeaderboardEligible = true,
                rank = null
            )
        )
    }
}
