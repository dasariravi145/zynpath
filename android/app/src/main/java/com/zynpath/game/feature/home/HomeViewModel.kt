package com.zynpath.game.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.database.repository.ProgressRepository
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.puzzle.daily.DailyChallengeAvailability
import com.zynpath.game.core.puzzle.daily.DailyChallengeClock
import com.zynpath.game.core.puzzle.daily.DailyChallengeRepository
import com.zynpath.game.core.puzzle.daily.SystemDailyChallengeClock
import com.zynpath.game.core.player.PlayerProfileRepository
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import com.zynpath.game.core.sync.connectivity.NetworkConnectivityMonitor
import com.zynpath.game.core.sync.coordinator.SyncCoordinator
import com.zynpath.game.core.sync.model.SyncStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResumableSessionInfo(
    val worldId: Int,
    val levelId: Int,
    val moveCount: Int,
    val elapsedActiveTimeMs: Long
)

data class HomeUiState(
    val displayName: String = "Pathfinder",
    val guestTag: String = "ZYN-GUEST",
    val avatarId: String = "avatar_compass",
    val isGuest: Boolean = true,
    val completedLevelsCount: Int = 0,
    val totalStars: Int = 0,
    val isPremium: Boolean = false,
    val isOfflineReady: Boolean = true,
    val isOnline: Boolean = true,
    val syncStatus: SyncStatus = SyncStatus(),
    val nextPlayableWorldId: Int = 1,
    val nextPlayableLevelId: Int = 1,
    val currentWorldName: String = "World 1 • Learn the Path",
    val currentWorldProgressText: String = "0/20 Solved",
    val isAllLevelsCompleted: Boolean = false,
    val dailyChallengeDate: String = "",
    val dailyChallengeStatusText: String = "Available",
    val dailyChallengeGrid: String = "5×5",
    val isDailyCompleted: Boolean = false,
    val dailySolveTime: String? = null,
    val dailyResetCountdown: String = "",
    val dailyStreak: Int = 0,
    val unreadNotificationCount: Int = 0,
    val resumableSession: ResumableSessionInfo? = null,
    val userNoticeMessage: String? = null,
    val achievementsUnlockedCount: Int = 0,
    val achievementsTotalCount: Int = 25,
    val nextGoalTitle: String = "World 1 • Learn the Path",
    val nextGoalDescription: String = "Complete the next level to progress through World 1",
    val nextGoalProgressFraction: Float = 0f,
    val isReducedMotion: Boolean = false,
    val coinBalance: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    preferencesRepository: PreferencesRepository,
    progressRepository: ProgressRepository,
    dailyChallengeRepository: DailyChallengeRepository? = null,
    playerProfileRepository: PlayerProfileRepository? = null,
    clock: DailyChallengeClock? = null,
    notificationRepository: com.zynpath.game.core.notification.repository.NotificationRepository? = null,
    private val sessionRepository: com.zynpath.game.core.database.repository.GameplaySessionRepository? = null,
    private val networkMonitor: NetworkConnectivityMonitor? = null,
    private val syncCoordinator: SyncCoordinator? = null,
    achievementRepository: com.zynpath.game.core.achievement.AchievementRepository? = null,
    val walletRepository: com.zynpath.game.core.economy.WalletRepository? = null,
    val coinRewardedAdManager: com.zynpath.game.core.ads.CoinRewardedAdManager? = null
) : ViewModel() {

    // Test & backward-compatible constructor
    constructor(
        preferencesRepository: PreferencesRepository,
        progressRepository: ProgressRepository
    ) : this(
        preferencesRepository,
        progressRepository,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    )

    private val effectiveClock = clock ?: SystemDailyChallengeClock()

    private val _resumableSession = MutableStateFlow<ResumableSessionInfo?>(null)
    private val _userNoticeMessage = MutableStateFlow<String?>(null)

    init {
        refreshResumableSession()
        viewModelScope.launch {
            walletRepository?.ensureWelcomeGiftGranted()
        }
    }

    fun refreshResumableSession() {
        viewModelScope.launch {
            val session = sessionRepository?.getLatestResumableSession()
            if (session != null && session.isResumable && session.path.isNotEmpty()) {
                _resumableSession.value = ResumableSessionInfo(
                    worldId = session.worldId,
                    levelId = session.levelId,
                    moveCount = session.moveCount,
                    elapsedActiveTimeMs = session.elapsedActiveTimeMs
                )
            } else {
                _resumableSession.value = null
            }
        }
    }

    fun onOnlineModeSelected(isOnlineRequired: Boolean, onNavigate: () -> Unit) {
        val currentlyOnline = networkMonitor?.isConnected?.value ?: true
        if (isOnlineRequired && !currentlyOnline) {
            _userNoticeMessage.value = "Multiplayer duels require an active internet connection. Offline Solo and Daily Challenge are ready to play anytime."
        } else {
            onNavigate()
        }
    }

    fun clearUserNotice() {
        _userNoticeMessage.value = null
    }

    private val progressSummaryFlow = combine(
        preferencesRepository.userPreferencesFlow,
        progressRepository.observeAllProgress()
    ) { preferences, allProgress ->
        val completedLevelIds = allProgress.filter { it.isCompleted }.map { it.levelId }.toSet()
        val totalStars = allProgress.sumOf { it.stars }
        val completedCount = completedLevelIds.size

        val nextLevelId = WorldConfiguration.getNextPlayableLevel(completedLevelIds)
        val nextWorld = WorldConfiguration.getWorldForLevel(nextLevelId)
        val completedInCurrentWorld = completedLevelIds.count { it in nextWorld.levelRange }
        val isAllCompleted = completedCount >= WorldConfiguration.TOTAL_LEVELS

        val progressText = "$completedInCurrentWorld/${nextWorld.totalLevels} Solved"
        val worldNameText = "World ${nextWorld.worldId} • ${nextWorld.name}"

        ProgressionSummary(
            isPremium = preferences.isPremium,
            isReducedMotion = preferences.isReducedMotion,
            guestUuid = preferences.guestUuid,
            completedCount = completedCount,
            totalStars = totalStars,
            nextWorldId = nextWorld.worldId,
            nextLevelId = nextLevelId,
            worldName = worldNameText,
            worldProgressText = progressText,
            isAllCompleted = isAllCompleted,
            completedInCurrentWorld = completedInCurrentWorld,
            currentWorldTotalLevels = nextWorld.totalLevels
        )
    }

    val uiState: StateFlow<HomeUiState> = combine(
        progressSummaryFlow,
        dailyChallengeRepository?.observeTodaySummary() ?: flowOf(com.zynpath.game.core.puzzle.daily.DailyChallengeSummary.empty(effectiveClock.currentUtcDateKey())),
        playerProfileRepository?.observeProfile() ?: flowOf(com.zynpath.game.core.player.PlayerProfile("local_player", "Pathfinder", "avatar_compass", 0L, 0L)),
        notificationRepository?.unreadCount ?: flowOf(0),
        _resumableSession,
        networkMonitor?.isConnected ?: flowOf(true),
        syncCoordinator?.syncStatus ?: flowOf(SyncStatus()),
        _userNoticeMessage,
        achievementRepository?.observeAchievements() ?: flowOf(emptyList<com.zynpath.game.core.achievement.AchievementProgress>()),
        walletRepository?.balanceFlow ?: flowOf(0)
    ) { args ->
        val prog = args[0] as ProgressionSummary
        val dailySummary = args[1] as com.zynpath.game.core.puzzle.daily.DailyChallengeSummary
        val profile = args[2] as com.zynpath.game.core.player.PlayerProfile
        val unreadNotifs = args[3] as Int
        val resumable = args[4] as ResumableSessionInfo?
        val isOnline = args[5] as Boolean
        val syncStatus = args[6] as SyncStatus
        val notice = args[7] as String?
        val achievements = args[8] as List<com.zynpath.game.core.achievement.AchievementProgress>
        val coinBalance = args[9] as Int

        val unlockedAchievements = achievements.count { it.isUnlocked }
        val totalAchievements = achievements.size.takeIf { it > 0 } ?: com.zynpath.game.core.achievement.AchievementRegistry.ALL_ACHIEVEMENTS.size

        // Next goal discovery: highlight world completion goal or upcoming achievement milestone
        val candidateAchievement = achievements.filter { !it.isUnlocked && it.currentProgress > 0 }
            .maxByOrNull { it.progressFraction }

        val (goalTitle, goalDesc, goalFraction) = if (candidateAchievement != null) {
            Triple(
                candidateAchievement.definition.title,
                "${candidateAchievement.definition.description} (${candidateAchievement.formattedProgress})",
                candidateAchievement.progressFraction
            )
        } else if (!prog.isAllCompleted) {
            val frac = if (prog.currentWorldTotalLevels > 0) {
                prog.completedInCurrentWorld.toFloat() / prog.currentWorldTotalLevels
            } else 0f
            Triple(
                prog.worldName,
                "${prog.worldProgressText} • Level ${WorldConfiguration.toLocalLevel(prog.nextLevelId)} ready",
                frac
            )
        } else {
            Triple(
                "Campaign Legend",
                "All 300 levels completed! Daily challenges await.",
                1f
            )
        }

        val millisUntilReset = dailySummary.millisUntilReset
        val hours = (millisUntilReset / (1000 * 60 * 60))
        val minutes = (millisUntilReset / (1000 * 60)) % 60
        val resetCountdown = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"

        val isCompleted = dailySummary.availability == DailyChallengeAvailability.COMPLETED
        val solveTimeFormatted = dailySummary.entity?.solveTimeMs?.takeIf { it > 0 }?.let { ms ->
            val m = (ms / 1000) / 60
            val s = (ms / 1000) % 60
            String.format("%02d:%02d", m, s)
        }

        val statusText = when (dailySummary.availability) {
            DailyChallengeAvailability.COMPLETED -> "Completed ($solveTimeFormatted)"
            DailyChallengeAvailability.IN_PROGRESS -> "In Progress"
            else -> "Ready to Play"
        }

        val guestTag = if (profile.isGuest && prog.guestUuid.isNotBlank()) {
            if (prog.guestUuid.length >= 4) "ZYN-${prog.guestUuid.substring(0, 4).uppercase()}" else profile.shortGuestTag
        } else {
            profile.shortGuestTag
        }

        HomeUiState(
            displayName = profile.displayName,
            guestTag = guestTag,
            avatarId = profile.avatarId,
            isGuest = profile.isGuest,
            completedLevelsCount = prog.completedCount,
            totalStars = prog.totalStars,
            coinBalance = coinBalance,
            isPremium = prog.isPremium,
            isOfflineReady = true,
            isOnline = isOnline,
            syncStatus = syncStatus,
            nextPlayableWorldId = prog.nextWorldId,
            nextPlayableLevelId = prog.nextLevelId,
            currentWorldName = prog.worldName,
            currentWorldProgressText = prog.worldProgressText,
            isAllLevelsCompleted = prog.isAllCompleted,
            dailyChallengeDate = dailySummary.definition.dateKey,
            dailyChallengeStatusText = statusText,
            dailyChallengeGrid = "${dailySummary.definition.gridDimensions.rows}×${dailySummary.definition.gridDimensions.columns}",
            isDailyCompleted = isCompleted,
            dailySolveTime = solveTimeFormatted,
            dailyResetCountdown = resetCountdown,
            dailyStreak = dailySummary.currentStreak,
            unreadNotificationCount = unreadNotifs,
            resumableSession = resumable,
            userNoticeMessage = notice,
            achievementsUnlockedCount = unlockedAchievements,
            achievementsTotalCount = totalAchievements,
            nextGoalTitle = goalTitle,
            nextGoalDescription = goalDesc,
            nextGoalProgressFraction = goalFraction,
            isReducedMotion = prog.isReducedMotion
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(
            dailyChallengeDate = effectiveClock.currentUtcDateKey()
        )
    )

    private data class ProgressionSummary(
        val isPremium: Boolean,
        val isReducedMotion: Boolean,
        val guestUuid: String,
        val completedCount: Int,
        val totalStars: Int,
        val nextWorldId: Int,
        val nextLevelId: Int,
        val worldName: String,
        val worldProgressText: String,
        val isAllCompleted: Boolean,
        val completedInCurrentWorld: Int,
        val currentWorldTotalLevels: Int
    )
}
