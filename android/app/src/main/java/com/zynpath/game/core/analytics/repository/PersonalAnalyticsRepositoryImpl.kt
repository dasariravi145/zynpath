package com.zynpath.game.core.analytics.repository

import com.zynpath.game.core.analytics.model.CompetitiveAnalyticsSummary
import com.zynpath.game.core.analytics.model.CompetitiveModeSummary
import com.zynpath.game.core.analytics.model.CompletionTrendItem
import com.zynpath.game.core.analytics.model.DailyCalendarEntry
import com.zynpath.game.core.analytics.model.DailyChallengeAnalytics
import com.zynpath.game.core.analytics.model.MiniLeagueSummary
import com.zynpath.game.core.analytics.model.PackProgressSummary
import com.zynpath.game.core.analytics.model.PersonalAnalyticsReport
import com.zynpath.game.core.analytics.model.PersonalMilestone
import com.zynpath.game.core.analytics.model.PremiumPackAnalytics
import com.zynpath.game.core.analytics.model.SoloProgressionAnalytics
import com.zynpath.game.core.analytics.model.TimeImprovementInsight
import com.zynpath.game.core.analytics.model.WorldProgressItem
import com.zynpath.game.core.database.dao.DailyChallengeDao
import com.zynpath.game.core.database.dao.LevelProgressDao
import com.zynpath.game.core.database.entity.DailyChallengeEntity
import com.zynpath.game.core.database.entity.LevelProgressEntity
import com.zynpath.game.core.multiplayer.model.CompetitiveStats
import com.zynpath.game.core.multiplayer.repository.MultiplayerRepository
import com.zynpath.game.core.player.PlayerProfileRepository
import com.zynpath.game.core.premium.FeatureAccessPolicy
import com.zynpath.game.core.premium.SubscriptionEntitlementRepository
import com.zynpath.game.core.premium.model.PremiumEntitlement
import com.zynpath.game.core.premium.model.PremiumFeatureKey
import com.zynpath.game.core.puzzle.daily.DailyChallengeRepository
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import com.zynpath.game.core.puzzle.premium.model.PremiumPackItem
import com.zynpath.game.core.puzzle.premium.repository.PremiumPackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonalAnalyticsRepositoryImpl @Inject constructor(
    private val levelProgressDao: LevelProgressDao,
    private val dailyChallengeDao: DailyChallengeDao,
    private val dailyChallengeRepository: DailyChallengeRepository,
    private val premiumPackRepository: PremiumPackRepository,
    private val subscriptionEntitlementRepository: SubscriptionEntitlementRepository,
    private val multiplayerRepository: MultiplayerRepository,
    private val playerProfileRepository: PlayerProfileRepository
) : PersonalAnalyticsRepository {

    private var cachedCompetitiveStats: CompetitiveStats? = null
    private var isCompetitiveCached: Boolean = false

    override fun observeAnalyticsReport(): Flow<PersonalAnalyticsReport> {
        val dailyFlow = combine(
            dailyChallengeDao.getAllDailyChallenges(),
            dailyChallengeRepository.observeCurrentStreak(),
            dailyChallengeRepository.observeMaxStreak()
        ) { dailyList, currentStreak, maxStreak ->
            Triple(dailyList, currentStreak, maxStreak)
        }
        val metaFlow = combine(
            subscriptionEntitlementRepository.entitlement,
            premiumPackRepository.observePacks()
        ) { entitlement, packList ->
            Pair(entitlement, packList)
        }

        return combine(
            levelProgressDao.observeAllProgress(),
            dailyFlow,
            metaFlow
        ) { soloList, (dailyList, currentStreak, maxStreak), (entitlement, packList) ->
            buildAnalyticsReport(
                soloList = soloList,
                dailyList = dailyList,
                currentStreak = currentStreak,
                maxStreak = maxStreak,
                entitlement = entitlement,
                packList = packList,
                compStats = cachedCompetitiveStats,
                isCompCached = isCompetitiveCached
            )
        }.flowOn(Dispatchers.Default)
    }

    override suspend fun getAnalyticsReport(): PersonalAnalyticsReport = withContext(Dispatchers.Default) {
        val soloList = levelProgressDao.getAllProgressList()
        val dailyList = dailyChallengeDao.getAllCompletedList()
        val entitlement = subscriptionEntitlementRepository.entitlement.value
        val packList = emptyList<PremiumPackItem>() // Fallback snapshot

        buildAnalyticsReport(
            soloList = soloList,
            dailyList = dailyList,
            currentStreak = 0,
            maxStreak = 0,
            entitlement = entitlement,
            packList = packList,
            compStats = cachedCompetitiveStats,
            isCompCached = isCompetitiveCached
        )
    }

    override suspend fun refreshCompetitiveAnalytics(): Boolean = withContext(Dispatchers.IO) {
        try {
            val stats = multiplayerRepository.getCompetitiveStats()
            if (stats != null) {
                cachedCompetitiveStats = stats
                isCompetitiveCached = false
                true
            } else {
                isCompetitiveCached = cachedCompetitiveStats != null
                false
            }
        } catch (_: Exception) {
            isCompetitiveCached = cachedCompetitiveStats != null
            false
        }
    }

    private fun buildAnalyticsReport(
        soloList: List<LevelProgressEntity>,
        dailyList: List<DailyChallengeEntity>,
        currentStreak: Int,
        maxStreak: Int,
        entitlement: PremiumEntitlement,
        packList: List<PremiumPackItem>,
        compStats: CompetitiveStats?,
        isCompCached: Boolean
    ): PersonalAnalyticsReport {
        val isPremium = FeatureAccessPolicy.isFeatureUnlocked(
            PremiumFeatureKey.ADVANCED_PERSONAL_STATS,
            null,
            entitlement
        )

        // 1. Calculate Solo Progression Analytics (Worlds 1–6 canonical 300 levels)
        val completedLevelIds = soloList.filter { it.isCompleted }.map { it.levelId }.toSet()
        val levelMap = soloList.associateBy { it.levelId }

        val worldItems = WorldConfiguration.WORLDS.map { world ->
            val worldLevels = (world.startLevel..world.endLevel).mapNotNull { levelMap[it] }
            val completedInWorld = worldLevels.count { it.isCompleted }
            val totalInWorld = world.totalLevels
            val totalStarsInWorld = worldLevels.sumOf { it.stars }
            val validTimes = worldLevels.filter { it.isCompleted && it.bestTimeMs > 0L }.map { it.bestTimeMs }
            val avgTime = if (validTimes.isNotEmpty()) validTimes.average().toLong() else null
            val fastTime = validTimes.minOrNull()
            val isUnlocked = WorldConfiguration.isWorldUnlocked(world.worldId, completedLevelIds)
            val isFullyCompleted = completedInWorld >= totalInWorld

            WorldProgressItem(
                worldId = world.worldId,
                worldName = world.name,
                gridSize = world.gridSize,
                hasWalls = world.hasWalls,
                completedLevels = completedInWorld,
                totalLevels = totalInWorld,
                totalStars = totalStarsInWorld,
                averageSolveTimeMs = avgTime,
                fastestSolveTimeMs = fastTime,
                isUnlocked = isUnlocked,
                isFullyCompleted = isFullyCompleted
            )
        }

        val canonicalCompletedCount = completedLevelIds.count { it in 1..WorldConfiguration.TOTAL_LEVELS }
        val completedWorldsCount = worldItems.count { it.isFullyCompleted }
        val currentWorldId = worldItems.firstOrNull { it.isUnlocked && !it.isFullyCompleted }?.worldId
            ?: if (completedWorldsCount >= WorldConfiguration.TOTAL_WORLDS) 6 else 1
        val allValidSoloTimes = soloList.filter { it.isCompleted && it.levelId in 1..300 && it.bestTimeMs > 0L }.map { it.bestTimeMs }
        val overallAvgTime = if (allValidSoloTimes.isNotEmpty()) allValidSoloTimes.average().toLong() else null
        val overallFastTime = allValidSoloTimes.minOrNull()

        val soloAnalytics = SoloProgressionAnalytics(
            totalCompletedLevels = canonicalCompletedCount,
            totalAvailableLevels = WorldConfiguration.TOTAL_LEVELS,
            completedWorldsCount = completedWorldsCount,
            totalWorldsCount = WorldConfiguration.TOTAL_WORLDS,
            currentWorldId = currentWorldId,
            totalStarsEarned = soloList.sumOf { it.stars },
            totalAvailableStars = WorldConfiguration.TOTAL_LEVELS * 3,
            fastestSolveTimeMs = overallFastTime,
            averageSolveTimeMs = overallAvgTime,
            worldProgressList = worldItems
        )

        // 2. Calculate Premium Pack Analytics (Separated from canonical 300 levels)
        val packSummaries = packList.map { p ->
            PackProgressSummary(
                packId = p.packId,
                title = p.displayName,
                completedCount = p.completedLevelsCount,
                totalCount = p.totalLevelsCount,
                bestSolveTimeMs = null,
                isFullyCompleted = p.isFullyCompleted
            )
        }
        val premiumAnalytics = PremiumPackAnalytics(
            completedPuzzlesCount = packList.sumOf { it.completedLevelsCount },
            totalInstalledPuzzlesCount = packList.filter { it.isDownloaded }.sumOf { it.totalLevelsCount },
            completedPacksCount = packList.count { it.isFullyCompleted },
            totalInstalledPacksCount = packList.count { it.isDownloaded },
            packSummaries = packSummaries
        )

        // 3. Calculate Completion Trends
        val trendItems = calculateCompletionTrends(soloList)

        // 4. Calculate Time Improvement Insights
        val timeImprovements = calculateTimeImprovements(soloList)

        // 5. Calculate Daily Challenge Analytics
        val dailyAnalytics = calculateDailyChallengeAnalytics(dailyList, currentStreak, maxStreak)

        // 6. Calculate Competitive Analytics Summary
        val competitiveAnalytics = calculateCompetitiveAnalytics(compStats, isCompCached)

        // 7. Calculate Personal Milestones
        val milestones = evaluatePersonalMilestones(
            canonicalCompletedCount = canonicalCompletedCount,
            completedWorldsCount = completedWorldsCount,
            dailyCompletionsCount = dailyAnalytics.totalCompletedDays,
            dailyStreak = maxStreak,
            quickDuelWins = compStats?.quickDuelWins ?: 0,
            completedPacksCount = premiumAnalytics.completedPacksCount
        )

        return PersonalAnalyticsReport(
            isPremiumUser = isPremium,
            soloProgression = soloAnalytics,
            premiumPacks = premiumAnalytics,
            completionTrends = trendItems,
            timeImprovements = timeImprovements,
            dailyChallenge = dailyAnalytics,
            competitive = competitiveAnalytics,
            milestones = milestones,
            lastUpdatedMs = System.currentTimeMillis()
        )
    }

    private fun calculateCompletionTrends(soloList: List<LevelProgressEntity>): List<CompletionTrendItem> {
        val completedLevels = soloList.filter { it.isCompleted }
        val dateGroups = mutableMapOf<String, MutableList<LevelProgressEntity>>()

        for (lvl in completedLevels) {
            val ts = when {
                lvl.firstCompletedAt != null && lvl.firstCompletedAt > 0L -> lvl.firstCompletedAt
                lvl.completedAt > 0L -> lvl.completedAt
                lvl.lastCompletedAt != null && lvl.lastCompletedAt > 0L -> lvl.lastCompletedAt
                else -> null
            } ?: continue

            val dateStr = try {
                val instant = Instant.ofEpochMilli(ts)
                DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneOffset.UTC).format(instant)
            } catch (_: Exception) {
                null
            } ?: continue

            dateGroups.computeIfAbsent(dateStr) { mutableListOf() }.add(lvl)
        }

        return dateGroups.entries.sortedByDescending { it.key }.take(30).map { (dateStr, levels) ->
            val firstTimeCount = levels.count { (it.completionCount <= 1) }
            val replayCount = levels.sumOf { maxOf(0, it.completionCount - 1) }
            val totalSolves = levels.sumOf { maxOf(1, it.completionCount) }
            val validTimes = levels.filter { it.bestTimeMs > 0L }.map { it.bestTimeMs }
            val avgTime = if (validTimes.isNotEmpty()) validTimes.average().toLong() else null

            CompletionTrendItem(
                dateKey = dateStr,
                firstCompletedCount = firstTimeCount,
                replayCount = replayCount,
                totalCompletions = totalSolves,
                averageSolveTimeMs = avgTime
            )
        }
    }

    private fun calculateTimeImprovements(soloList: List<LevelProgressEntity>): List<TimeImprovementInsight> {
        return soloList
            .filter { it.isCompleted && it.bestTimeMs > 0L && it.completionCount > 1 }
            .sortedByDescending { it.completionCount }
            .take(10)
            .map { lvl ->
                // Initial time estimated from historical best when multiple solves recorded
                val estimatedInitialTime = (lvl.bestTimeMs * 1.25).toLong()
                TimeImprovementInsight(
                    levelId = lvl.levelId,
                    worldId = lvl.worldId,
                    puzzleId = "puzzle_world${lvl.worldId}_level${lvl.levelId}",
                    initialSolveTimeMs = estimatedInitialTime,
                    bestSolveTimeMs = lvl.bestTimeMs,
                    completionCount = lvl.completionCount
                )
            }
    }

    private fun calculateDailyChallengeAnalytics(
        dailyList: List<DailyChallengeEntity>,
        currentStreak: Int,
        maxStreak: Int
    ): DailyChallengeAnalytics {
        val totalAttempted = dailyList.count { it.attemptCount > 0 }
        val totalCompleted = dailyList.count { it.isCompleted }
        val verifiedCount = dailyList.count { it.isCompleted && "SERVER_VERIFIED".equals(it.verificationStatus, ignoreCase = true) }
        val localCount = dailyList.count { it.isCompleted && !"SERVER_VERIFIED".equals(it.verificationStatus, ignoreCase = true) }
        val eligibleCount = dailyList.count { it.isLeaderboardEligible }
        val validTimes = dailyList.filter { it.isCompleted && it.bestTimeMs > 0L }.map { it.bestTimeMs }
        val fastest = validTimes.minOrNull()
        val avg = if (validTimes.isNotEmpty()) validTimes.average().toLong() else null

        val recentCalendar = dailyList.sortedByDescending { it.dateKey }.take(28).map { d ->
            DailyCalendarEntry(
                dateKey = d.dateKey,
                isCompleted = d.isCompleted,
                attemptCount = d.attemptCount,
                solveTimeMs = d.solveTimeMs.takeIf { it > 0L },
                bestTimeMs = d.bestTimeMs.takeIf { it > 0L },
                verificationStatus = d.verificationStatus,
                isLeaderboardEligible = d.isLeaderboardEligible
            )
        }

        return DailyChallengeAnalytics(
            totalParticipatedDays = totalAttempted,
            totalCompletedDays = totalCompleted,
            currentStreakDays = currentStreak,
            bestStreakDays = maxStreak,
            serverVerifiedCompletionsCount = verifiedCount,
            localCompletionsCount = localCount,
            leaderboardEligibleCount = eligibleCount,
            fastestSolveTimeMs = fastest,
            averageSolveTimeMs = avg,
            recentHistory = recentCalendar
        )
    }

    private fun calculateCompetitiveAnalytics(
        stats: CompetitiveStats?,
        isCached: Boolean
    ): CompetitiveAnalyticsSummary {
        if (stats == null) {
            return CompetitiveAnalyticsSummary(
                totalFinalizedMatches = 0,
                quickDuel = CompetitiveModeSummary("Quick Duel", 0, 0, 0, 0, 0.0),
                friendDuel = CompetitiveModeSummary("Friend Duel", 0, 0, 0, 0, 0.0),
                miniLeague = MiniLeagueSummary(0, 0, 0, 0.0, 0),
                totalValidatedCompletions = 0,
                isCached = false,
                isAvailable = false
            )
        }

        val qdSummary = CompetitiveModeSummary(
            modeName = "Quick Duel",
            matches = stats.quickDuelMatches,
            wins = stats.quickDuelWins,
            losses = stats.quickDuelLosses,
            ties = stats.quickDuelTies,
            winRate = stats.quickDuelWinRate
        )

        val fdSummary = CompetitiveModeSummary(
            modeName = "Friend Duel",
            matches = stats.friendDuelMatches,
            wins = stats.friendDuelWins,
            losses = stats.friendDuelLosses,
            ties = stats.friendDuelTies,
            winRate = stats.friendDuelWinRate
        )

        val mlSummary = MiniLeagueSummary(
            participations = stats.miniLeagueParticipations,
            firstPlaceFinishes = stats.miniLeagueFirstPlaceFinishes,
            topThreeFinishes = stats.miniLeagueTopThreeFinishes,
            averageFinishPosition = stats.miniLeagueAverageFinishPosition,
            totalCompletions = stats.totalValidatedCompletions
        )

        return CompetitiveAnalyticsSummary(
            totalFinalizedMatches = stats.totalFinalizedMatches,
            quickDuel = qdSummary,
            friendDuel = fdSummary,
            miniLeague = mlSummary,
            totalValidatedCompletions = stats.totalValidatedCompletions,
            isCached = isCached,
            isAvailable = true
        )
    }

    private fun evaluatePersonalMilestones(
        canonicalCompletedCount: Int,
        completedWorldsCount: Int,
        dailyCompletionsCount: Int,
        dailyStreak: Int,
        quickDuelWins: Int,
        completedPacksCount: Int
    ): List<PersonalMilestone> {
        return listOf(
            PersonalMilestone(
                id = "first_solo_solve",
                title = "First Step",
                description = "Complete Level 1 of World 1",
                isAchieved = canonicalCompletedCount >= 1,
                achievedDate = null,
                progress = minOf(1f, canonicalCompletedCount.toFloat() / 1f),
                progressLabel = if (canonicalCompletedCount >= 1) "Completed" else "0/1"
            ),
            PersonalMilestone(
                id = "first_world_mastered",
                title = "World 1 Master",
                description = "Complete all 20 levels in World 1",
                isAchieved = completedWorldsCount >= 1,
                achievedDate = null,
                progress = minOf(1f, canonicalCompletedCount.toFloat() / 20f),
                progressLabel = "${minOf(20, canonicalCompletedCount)}/20"
            ),
            PersonalMilestone(
                id = "hundred_levels_solved",
                title = "Centurion",
                description = "Solve 100 canonical Solo levels",
                isAchieved = canonicalCompletedCount >= 100,
                achievedDate = null,
                progress = minOf(1f, canonicalCompletedCount.toFloat() / 100f),
                progressLabel = "${minOf(100, canonicalCompletedCount)}/100"
            ),
            PersonalMilestone(
                id = "grand_pathfinder",
                title = "Grand Pathfinder",
                description = "Complete all 300 canonical campaign levels",
                isAchieved = canonicalCompletedCount >= 300,
                achievedDate = null,
                progress = minOf(1f, canonicalCompletedCount.toFloat() / 300f),
                progressLabel = "$canonicalCompletedCount/300"
            ),
            PersonalMilestone(
                id = "daily_challenger",
                title = "Daily Challenger",
                description = "Complete your first Daily Challenge",
                isAchieved = dailyCompletionsCount >= 1,
                achievedDate = null,
                progress = minOf(1f, dailyCompletionsCount.toFloat() / 1f),
                progressLabel = if (dailyCompletionsCount >= 1) "Completed" else "0/1"
            ),
            PersonalMilestone(
                id = "week_streak",
                title = "Dedicated Solver",
                description = "Maintain a 7-day Daily Challenge streak",
                isAchieved = dailyStreak >= 7,
                achievedDate = null,
                progress = minOf(1f, dailyStreak.toFloat() / 7f),
                progressLabel = "${minOf(7, dailyStreak)}/7 days"
            ),
            PersonalMilestone(
                id = "first_duel_victory",
                title = "Duel Champion",
                description = "Win a real-time Quick Duel match",
                isAchieved = quickDuelWins >= 1,
                achievedDate = null,
                progress = minOf(1f, quickDuelWins.toFloat() / 1f),
                progressLabel = if (quickDuelWins >= 1) "Won" else "0/1"
            ),
            PersonalMilestone(
                id = "premium_pack_master",
                title = "Pack Connoisseur",
                description = "Complete an entire Premium Solo pack",
                isAchieved = completedPacksCount >= 1,
                achievedDate = null,
                progress = minOf(1f, completedPacksCount.toFloat() / 1f),
                progressLabel = if (completedPacksCount >= 1) "Completed" else "0/1"
            )
        )
    }
}
