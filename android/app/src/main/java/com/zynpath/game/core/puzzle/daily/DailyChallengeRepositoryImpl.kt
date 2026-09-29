package com.zynpath.game.core.puzzle.daily

import com.zynpath.game.core.auth.storage.SecureTokenStorage
import com.zynpath.game.core.database.dao.DailyChallengeDao
import com.zynpath.game.core.database.entity.DailyChallengeEntity
import com.zynpath.game.core.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyChallengeRepositoryImpl @Inject constructor(
    private val dao: DailyChallengeDao,
    private val schedule: DailyChallengeSchedule,
    private val clock: DailyChallengeClock,
    private val apiService: DailyChallengeApiService,
    private val tokenStorage: SecureTokenStorage,
    private val syncOperationDao: com.zynpath.game.core.database.dao.SyncOperationDao? = null
) : DailyChallengeRepository {

    override fun getTodayChallenge(): DailyChallengeDefinition {
        val todayKey = clock.currentUtcDateKey()
        return schedule.resolveChallenge(todayKey)
    }

    override fun getChallengeForDate(dateKey: String): DailyChallengeDefinition {
        return schedule.resolveChallenge(dateKey)
    }

    override fun observeTodaySummary(): Flow<DailyChallengeSummary> {
        val todayKey = clock.currentUtcDateKey()
        val definition = getTodayChallenge()

        return combine(
            dao.getDailyChallenge(todayKey),
            dao.getAllCompletedDateKeys()
        ) { entity, completedKeys ->
            val streak = DailyChallengeStreakCalculator.calculateStreak(
                completedDateKeys = completedKeys.toSet(),
                referenceUtcDate = clock.currentUtcDate()
            )

            val availability = when {
                entity?.isCompleted == true -> DailyChallengeAvailability.COMPLETED
                (entity?.attemptCount ?: 0) > 0 -> DailyChallengeAvailability.IN_PROGRESS
                else -> DailyChallengeAvailability.AVAILABLE
            }

            DailyChallengeSummary(
                definition = definition,
                availability = availability,
                entity = entity,
                millisUntilReset = clock.millisUntilNextReset(),
                currentStreak = streak.currentStreak,
                maxStreak = streak.maxStreak
            )
        }
    }

    override fun observeTodayAvailability(): Flow<DailyChallengeAvailability> {
        return observeTodaySummary().map { it.availability }
    }

    override fun observeCurrentStreak(): Flow<Int> {
        return dao.getAllCompletedDateKeys().map { completedKeys ->
            DailyChallengeStreakCalculator.calculateStreak(
                completedDateKeys = completedKeys.toSet(),
                referenceUtcDate = clock.currentUtcDate()
            ).currentStreak
        }
    }

    override fun observeMaxStreak(): Flow<Int> {
        return dao.getAllCompletedDateKeys().map { completedKeys ->
            DailyChallengeStreakCalculator.calculateStreak(
                completedDateKeys = completedKeys.toSet(),
                referenceUtcDate = clock.currentUtcDate()
            ).maxStreak
        }
    }

    override fun observeCompletedChallenges(): Flow<List<DailyChallengeEntity>> {
        return dao.getAllCompletedChallenges()
    }

    override suspend fun recordAttemptStarted(challenge: DailyChallengeDefinition) {
        val now = clock.currentUtcInstant().toEpochMilli()
        val existing = dao.getDailyChallengeSync(challenge.dateKey)
        if (existing == null) {
            val initial = DailyChallengeEntity(
                dateKey = challenge.dateKey,
                challengeId = challenge.challengeId,
                challengeVersion = challenge.challengeVersion,
                puzzleId = challenge.puzzleId,
                puzzleVersion = challenge.puzzleVersion,
                puzzleFingerprint = challenge.puzzleFingerprint,
                scheduleVersion = challenge.scheduleVersion,
                seed = challenge.puzzleDefinition.seed ?: 0L,
                gridSize = challenge.puzzleDefinition.gridDimensions.rows,
                isCompleted = false,
                solveTimeMs = 0L,
                completedAt = 0L,
                attemptCount = 1,
                bestTimeMs = 0L,
                firstAttemptAt = now,
                lastAttemptAt = now,
                movesCount = 0,
                verificationStatus = DailyVerificationStatus.LOCAL_COMPLETION.name,
                serverAttemptId = null,
                isLeaderboardEligible = false
            )
            dao.upsertDailyChallenge(initial)
        } else {
            dao.incrementAttemptCount(challenge.dateKey, now)
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
        val now = clock.currentUtcInstant().toEpochMilli()
        dao.recordCompletion(
            dateKey = challenge.dateKey,
            challengeId = challenge.challengeId,
            puzzleId = challenge.puzzleId,
            puzzleVersion = challenge.puzzleVersion,
            puzzleFingerprint = challenge.puzzleFingerprint,
            scheduleVersion = challenge.scheduleVersion,
            gridSize = challenge.puzzleDefinition.gridDimensions.rows,
            solveTimeMs = solveTimeMs,
            completedAt = now,
            movesCount = movesCount,
            verificationStatus = verificationStatus.name,
            serverAttemptId = serverAttemptId,
            isLeaderboardEligible = isLeaderboardEligible
        )

        // Prompt 35 & 38: Persist durable pending sync operation with deduplication for Daily Challenge reconciliation
        try {
            syncOperationDao?.let { sDao ->
                val owner = tokenStorage.getSessionMetadata()?.playerId ?: "guest"
                val resourceId = "daily_${challenge.dateKey}"
                val payload = org.json.JSONObject().apply {
                    put("dateKey", challenge.dateKey)
                    put("challengeId", challenge.challengeId)
                    put("fingerprint", challenge.puzzleFingerprint)
                    put("solveTimeMs", solveTimeMs)
                    put("movesCount", movesCount)
                    put("completedAt", now)
                }

                val existingPending = sDao.getPendingOperationForResource(owner, resourceId)
                if (existingPending != null) {
                    sDao.updateOperation(
                        existingPending.copy(
                            payloadJson = payload.toString(),
                            createdAt = now
                        )
                    )
                } else {
                    val op = com.zynpath.game.core.sync.model.SyncOperationEntity(
                        operationId = java.util.UUID.randomUUID().toString(),
                        ownerIdentity = owner,
                        operationType = com.zynpath.game.core.sync.model.SyncOperationEntity.TYPE_DAILY_CHALLENGE,
                        payloadVersion = 1,
                        resourceIdentity = resourceId,
                        payloadJson = payload.toString(),
                        createdAt = now,
                        status = com.zynpath.game.core.sync.model.SyncOperationEntity.STATUS_PENDING
                    )
                    sDao.insertOperation(op)
                }
            }
        } catch (_: Exception) {}

        return true
    }

    override suspend fun getOfficialChallengeOnline(dateKey: String?): Result<DailyChallengeDefinition> {
        return when (val res = apiService.getChallenge(dateKey)) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(Exception(res.message))
            is NetworkResult.Exception -> Result.failure(res.throwable)
        }
    }

    override suspend fun startOfficialAttempt(dateKey: String?): Result<DailyChallengeAttempt> {
        val token = tokenStorage.getSessionToken()
            ?: return Result.failure(IllegalStateException("UNAUTHENTICATED: Authentication required for official competitive attempts"))

        return when (val res = apiService.startOfficialAttempt(token, dateKey)) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(Exception(res.message))
            is NetworkResult.Exception -> Result.failure(res.throwable)
        }
    }

    override suspend fun getActiveAttempt(dateKey: String?): Result<DailyChallengeAttempt?> {
        val token = tokenStorage.getSessionToken()
            ?: return Result.success(null)

        return when (val res = apiService.getActiveAttempt(token, dateKey)) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(Exception(res.message))
            is NetworkResult.Exception -> Result.failure(res.throwable)
        }
    }

    override suspend fun submitOfficialCompletion(
        attemptId: String,
        challenge: DailyChallengeDefinition,
        pathCoordinates: List<String>,
        clientElapsedMs: Long?
    ): Result<DailyChallengeOnlineResult> {
        val token = tokenStorage.getSessionToken()
            ?: return Result.failure(IllegalStateException("UNAUTHENTICATED: Session token required to submit completion"))

        return when (val res = apiService.submitCompletion(
            sessionToken = token,
            attemptId = attemptId,
            challengeId = challenge.challengeId,
            puzzleFingerprint = challenge.puzzleFingerprint,
            pathCoordinates = pathCoordinates,
            clientElapsedMs = clientElapsedMs
        )) {
            is NetworkResult.Success -> {
                // Update local Room entity with authoritative server validation outcome
                dao.updateVerificationStatus(
                    dateKey = challenge.dateKey,
                    verificationStatus = res.data.verificationStatus.name,
                    serverAttemptId = attemptId,
                    isLeaderboardEligible = res.data.isLeaderboardEligible
                )
                Result.success(res.data)
            }
            is NetworkResult.Error -> Result.failure(Exception(res.message))
            is NetworkResult.Exception -> Result.failure(res.throwable)
        }
    }

    override suspend fun getPersonalResultOnline(dateKey: String?): Result<DailyChallengeOnlineResult?> {
        val token = tokenStorage.getSessionToken()
            ?: return Result.success(null)

        return when (val res = apiService.getPersonalResult(token, dateKey)) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(Exception(res.message))
            is NetworkResult.Exception -> Result.failure(res.throwable)
        }
    }

    override suspend fun getDailyLeaderboard(
        dateKey: String?,
        page: Int,
        pageSize: Int
    ): Result<DailyLeaderboardResponse> {
        val token = tokenStorage.getSessionToken()

        return when (val res = apiService.getDailyLeaderboard(token, dateKey, page, pageSize)) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(Exception(res.message))
            is NetworkResult.Exception -> Result.failure(res.throwable)
        }
    }

    override suspend fun syncProvisional(
        challenge: DailyChallengeDefinition,
        solveTimeMs: Long,
        completedAt: Long,
        path: List<String>?,
        movesCount: Int
    ): Result<DailyChallengeOnlineResult> {
        val token = tokenStorage.getSessionToken()
            ?: return Result.failure(IllegalStateException("UNAUTHENTICATED: Session token required for sync"))

        return when (val res = apiService.syncProvisional(
            sessionToken = token,
            challengeId = challenge.challengeId,
            dateKey = challenge.dateKey,
            fingerprint = challenge.puzzleFingerprint,
            solveTimeMs = solveTimeMs,
            completedAt = completedAt,
            pathCoordinates = path,
            movesCount = movesCount
        )) {
            is NetworkResult.Success -> Result.success(res.data)
            is NetworkResult.Error -> Result.failure(Exception(res.message))
            is NetworkResult.Exception -> Result.failure(res.throwable)
        }
    }
}
