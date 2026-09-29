package com.zynpath.game.core.player

import com.zynpath.game.core.achievement.AchievementRegistry
import com.zynpath.game.core.database.dao.AchievementDao
import com.zynpath.game.core.database.dao.DailyChallengeDao
import com.zynpath.game.core.database.dao.LevelProgressDao
import com.zynpath.game.core.database.dao.PlayerProfileDao
import com.zynpath.game.core.database.entity.PlayerProfileEntity
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.puzzle.catalog.LevelCatalogRepository
import com.zynpath.game.core.puzzle.daily.DailyChallengeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository interface managing local player profile, guest identity, and derived statistics.
 *
 * Implements Prompt 17 Sections 6-16.
 */
interface PlayerProfileRepository {
    fun observeProfile(): Flow<PlayerProfile>
    fun observeStatistics(): Flow<PlayerStatistics>
    suspend fun getProfile(): PlayerProfile
    suspend fun updateDisplayName(name: String): DisplayNameValidationResult
    suspend fun updateAvatar(avatarId: String)
    suspend fun updateAccountLinking(accountType: AccountType, publicId: String?)
}

@Singleton
class PlayerProfileRepositoryImpl @Inject constructor(
    private val profileDao: PlayerProfileDao,
    private val levelProgressDao: LevelProgressDao,
    private val dailyChallengeDao: DailyChallengeDao,
    private val dailyChallengeRepository: DailyChallengeRepository,
    private val achievementDao: AchievementDao,
    private val levelCatalogRepository: LevelCatalogRepository,
    private val preferencesRepository: PreferencesRepository
) : PlayerProfileRepository {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            ensureProfileInitialized()
        }
    }

    private suspend fun ensureProfileInitialized(): PlayerProfileEntity {
        val existing = profileDao.getProfile()
        if (existing != null) {
            return existing
        }

        // Fetch or create stable guest ID
        val prefs = preferencesRepository.userPreferencesFlow.first()
        val stableId = prefs.guestUuid.ifBlank { UUID.randomUUID().toString() }

        val newProfile = PlayerProfileEntity(
            playerId = stableId,
            displayName = "Pathfinder",
            avatarId = AvatarCatalog.DEFAULT_AVATAR_ID,
            createdAt = System.currentTimeMillis(),
            lastActiveAt = System.currentTimeMillis(),
            accountType = AccountType.GUEST.name,
            publicZynpathId = null
        )
        profileDao.upsertProfile(newProfile)
        return newProfile
    }

    override fun observeProfile(): Flow<PlayerProfile> {
        return profileDao.observeProfile().map { entity ->
            val e = entity ?: ensureProfileInitialized()
            PlayerProfile(
                playerId = e.playerId,
                displayName = e.displayName,
                avatarId = e.avatarId,
                createdAt = e.createdAt,
                lastActiveAt = e.lastActiveAt,
                accountType = AccountType.fromString(e.accountType),
                publicZynpathId = e.publicZynpathId
            )
        }
    }

    override suspend fun getProfile(): PlayerProfile {
        val e = profileDao.getProfile() ?: ensureProfileInitialized()
        return PlayerProfile(
            playerId = e.playerId,
            displayName = e.displayName,
            avatarId = e.avatarId,
            createdAt = e.createdAt,
            lastActiveAt = e.lastActiveAt,
            accountType = AccountType.fromString(e.accountType),
            publicZynpathId = e.publicZynpathId
        )
    }

    override fun observeStatistics(): Flow<PlayerStatistics> {
        val totalCatalogLevels = levelCatalogRepository.getWorlds().sumOf { it.totalLevels }
        val totalAchievements = AchievementRegistry.ALL_ACHIEVEMENTS.size

        return combine(
            levelProgressDao.getCompletedLevelCount(),
            levelProgressDao.getTotalStarsEarned(),
            dailyChallengeDao.getCompletedCount(),
            dailyChallengeRepository.observeTodaySummary(),
            achievementDao.observeUnlockedCount()
        ) { soloCompleted, totalStars, dailyCompleted, dailySummary, unlockedAchievements ->
            PlayerStatistics(
                completedSoloLevels = soloCompleted,
                totalAvailableSoloLevels = totalCatalogLevels,
                totalStarsEarned = totalStars ?: 0,
                completedDailyChallenges = dailyCompleted,
                currentDailyStreak = dailySummary.currentStreak,
                bestDailyStreak = dailySummary.maxStreak,
                totalCompletedSolves = soloCompleted + dailyCompleted,
                unlockedAchievementsCount = unlockedAchievements,
                totalAchievementsCount = totalAchievements
            )
        }
    }

    override suspend fun updateDisplayName(name: String): DisplayNameValidationResult {
        val result = DisplayNameValidator.validate(name)
        if (result is DisplayNameValidationResult.Valid) {
            val current = getProfile()
            profileDao.updateDisplayName(
                playerId = current.playerId,
                displayName = result.normalizedName,
                timestamp = System.currentTimeMillis()
            )
        }
        return result
    }

    override suspend fun updateAvatar(avatarId: String) {
        val current = getProfile()
        profileDao.updateAvatar(
            playerId = current.playerId,
            avatarId = avatarId,
            timestamp = System.currentTimeMillis()
        )
    }

    override suspend fun updateAccountLinking(accountType: AccountType, publicId: String?) {
        val current = getProfile()
        profileDao.updateAccountLinking(
            playerId = current.playerId,
            accountType = accountType.name,
            publicId = publicId,
            timestamp = System.currentTimeMillis()
        )
    }
}
