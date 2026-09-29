package com.zynpath.game.core.achievement

import com.zynpath.game.core.database.dao.AchievementDao
import com.zynpath.game.core.database.dao.DailyChallengeDao
import com.zynpath.game.core.database.dao.LevelProgressDao
import com.zynpath.game.core.database.entity.AchievementEntity
import com.zynpath.game.core.puzzle.daily.DailyChallengeStreakCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository interface managing local achievement state and evaluations.
 *
 * Implements Prompt 17 Section 17-23 & Prompt 41.
 */
interface AchievementRepository {
    fun observeAchievements(category: AchievementCategory = AchievementCategory.ALL): Flow<List<AchievementProgress>>
    fun observeUnlockedCount(): Flow<Int>
    val unlockedEvents: SharedFlow<AchievementDefinition>
    suspend fun evaluateAll()
    suspend fun evaluateCompetitiveAchievements(stats: com.zynpath.game.core.multiplayer.model.CompetitiveStats)
    suspend fun evaluateDailyAchievements(isServerValidated: Boolean, isLeaderboardEligible: Boolean)
}

@Singleton
class AchievementRepositoryImpl @Inject constructor(
    private val achievementDao: AchievementDao,
    private val levelProgressDao: LevelProgressDao,
    private val dailyChallengeDao: DailyChallengeDao
) : AchievementRepository {

    private val _unlockedEvents = MutableSharedFlow<AchievementDefinition>(extraBufferCapacity = 10)
    override val unlockedEvents: SharedFlow<AchievementDefinition> = _unlockedEvents.asSharedFlow()

    init {
        // Seed default achievements if absent
        CoroutineScope(Dispatchers.IO).launch {
            seedDefaults()
            evaluateAll()
        }
    }

    private suspend fun seedDefaults() {
        val initialEntities = AchievementRegistry.ALL_ACHIEVEMENTS.map { def ->
            AchievementEntity(
                achievementId = def.id,
                targetProgress = def.targetValue,
                currentProgress = 0,
                isUnlocked = false
            )
        }
        achievementDao.insertAll(initialEntities)
        for (def in AchievementRegistry.ALL_ACHIEVEMENTS) {
            achievementDao.updateTargetProgress(def.id, def.targetValue)
        }
    }

    override fun observeAchievements(category: AchievementCategory): Flow<List<AchievementProgress>> {
        return achievementDao.observeAllAchievements().map { entities ->
            val entityMap = entities.associateBy { it.achievementId }
            AchievementRegistry.ALL_ACHIEVEMENTS
                .filter { category == AchievementCategory.ALL || it.category == category }
                .map { def ->
                    val entity = entityMap[def.id]
                    AchievementProgress(
                        definition = def,
                        currentProgress = entity?.currentProgress ?: 0,
                        isUnlocked = entity?.isUnlocked ?: false,
                        unlockedAt = entity?.unlockedAt
                    )
                }
        }
    }

    override fun observeUnlockedCount(): Flow<Int> {
        return achievementDao.observeUnlockedCount()
    }

    override suspend fun evaluateAll() {
        // Fetch authoritative raw progress data
        val allLevelProgress = levelProgressDao.getAllProgressList()
        val completedLevels = allLevelProgress.filter { it.isCompleted }
        val distinctCompletedLevelIds = completedLevels.map { it.levelId }.toSet()
        val distinctCompletedCount = distinctCompletedLevelIds.size

        // Solo level progression milestones
        checkMilestone(AchievementRegistry.SOLO_FIRST_STEP.id, distinctCompletedCount, 1)
        checkMilestone(AchievementRegistry.SOLO_APPRENTICE.id, distinctCompletedCount, 5)
        checkMilestone(AchievementRegistry.SOLO_JOURNEYMAN.id, distinctCompletedCount, 15)
        checkMilestone(AchievementRegistry.SOLO_HALF_CENTURY.id, distinctCompletedCount, 50)
        checkMilestone(AchievementRegistry.SOLO_CENTURY.id, distinctCompletedCount, 100)
        checkMilestone(AchievementRegistry.SOLO_DOUBLE_CENTURY.id, distinctCompletedCount, 200)
        checkMilestone(AchievementRegistry.SOLO_CAMPAIGN_MASTER.id, distinctCompletedCount, 300)

        // World completion milestones (canonical ranges)
        // World 1: 1..20 (20 levels)
        val w1Count = distinctCompletedLevelIds.count { it in 1..20 }
        checkMilestone(AchievementRegistry.WORLD_ONE_PIONEER.id, w1Count, 20)

        // World 2: 21..50 (30 levels)
        val w2Count = distinctCompletedLevelIds.count { it in 21..50 }
        checkMilestone(AchievementRegistry.WORLD_TWO_EXPLORER.id, w2Count, 30)

        // World 3: 51..100 (50 levels)
        val w3Count = distinctCompletedLevelIds.count { it in 51..100 }
        checkMilestone(AchievementRegistry.WORLD_THREE_WALL_BREAKER.id, w3Count, 50)

        // World 4: 101..150 (50 levels)
        val w4Count = distinctCompletedLevelIds.count { it in 101..150 }
        checkMilestone(AchievementRegistry.WORLD_FOUR_NAVIGATOR.id, w4Count, 50)

        // World 5: 151..200 (50 levels)
        val w5Count = distinctCompletedLevelIds.count { it in 151..200 }
        checkMilestone(AchievementRegistry.WORLD_FIVE_MASTERMIND.id, w5Count, 50)

        // World 6: 201..300 (100 levels)
        val w6Count = distinctCompletedLevelIds.count { it in 201..300 }
        checkMilestone(AchievementRegistry.WORLD_SIX_GRANDMASTER.id, w6Count, 100)

        // Hint-free completions
        val hasNoHintCompletion = completedLevels.any { it.bestHintCount == 0 }
        checkMilestone(AchievementRegistry.PURE_INTELLECT.id, if (hasNoHintCompletion) 1 else 0, 1)

        // Speed solve under 30s (30,000 ms)
        val hasSpeedSolve = completedLevels.any { it.bestTimeMs in 1..30000 }
        checkMilestone(AchievementRegistry.SPEED_DEMON.id, if (hasSpeedSolve) 1 else 0, 1)

        // Daily challenges - authoritative UTC streak calculation
        val completedDailies = dailyChallengeDao.getAllCompletedList()
        val completedDailyCount = completedDailies.size
        val completedDateKeys = completedDailies.map { it.dateKey }.toSet()
        val streakResult = DailyChallengeStreakCalculator.calculateStreak(
            completedDateKeys = completedDateKeys,
            referenceUtcDate = LocalDate.now(ZoneOffset.UTC)
        )
        val maxDailyStreak = streakResult.maxStreak

        checkMilestone(AchievementRegistry.DAILY_FIRST_DAWN.id, completedDailyCount, 1)
        checkMilestone(AchievementRegistry.DAILY_THREE_STREAK.id, maxDailyStreak, 3)
        checkMilestone(AchievementRegistry.DAILY_SEVEN_STREAK.id, maxDailyStreak, 7)

        val hasServerValidated = completedDailies.any { it.verificationStatus == "VERIFIED" }
        if (hasServerValidated) {
            checkMilestone(AchievementRegistry.DAILY_SERVER_VALIDATED.id, 1, 1)
        }
        val hasLeaderboardRanked = completedDailies.any { it.isLeaderboardEligible }
        if (hasLeaderboardRanked) {
            checkMilestone(AchievementRegistry.DAILY_LEADERBOARD_RANKED.id, 1, 1)
        }
    }

    override suspend fun evaluateCompetitiveAchievements(stats: com.zynpath.game.core.multiplayer.model.CompetitiveStats) {
        checkMilestone(AchievementRegistry.COMPETITIVE_QUICK_DUEL_COMPLETE.id, stats.quickDuelMatches, 1)
        checkMilestone(AchievementRegistry.COMPETITIVE_QUICK_DUEL_WIN.id, stats.quickDuelWins, 1)
        checkMilestone(AchievementRegistry.COMPETITIVE_FRIEND_DUEL_COMPLETE.id, stats.friendDuelMatches, 1)
        checkMilestone(AchievementRegistry.COMPETITIVE_MINI_LEAGUE_PARTICIPATION.id, stats.miniLeagueParticipations, 1)
        checkMilestone(AchievementRegistry.COMPETITIVE_MINI_LEAGUE_WIN.id, stats.miniLeagueFirstPlaceFinishes, 1)
    }

    override suspend fun evaluateDailyAchievements(isServerValidated: Boolean, isLeaderboardEligible: Boolean) {
        if (isServerValidated) {
            checkMilestone(AchievementRegistry.DAILY_SERVER_VALIDATED.id, 1, 1)
        }
        if (isLeaderboardEligible) {
            checkMilestone(AchievementRegistry.DAILY_LEADERBOARD_RANKED.id, 1, 1)
        }
    }

    private suspend fun checkMilestone(id: String, progress: Int, target: Int) {
        val cappedProgress = progress.coerceAtMost(target)
        achievementDao.updateProgress(id, cappedProgress)

        if (progress >= target) {
            val rowsUpdated = achievementDao.unlockAchievement(id, System.currentTimeMillis())
            if (rowsUpdated > 0) {
                // Newly unlocked! Emit event
                AchievementRegistry.getById(id)?.let { def ->
                    _unlockedEvents.tryEmit(def)
                }
            }
        }
    }
}
