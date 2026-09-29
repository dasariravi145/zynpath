package com.zynpath.game.fake

import com.zynpath.game.core.player.AccountType
import com.zynpath.game.core.player.DisplayNameValidationResult
import com.zynpath.game.core.player.PlayerProfile
import com.zynpath.game.core.player.PlayerProfileRepository
import com.zynpath.game.core.player.PlayerStatistics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakePlayerProfileRepository(
    initialProfile: PlayerProfile? = PlayerProfile(
        playerId = "guest_123",
        displayName = "Guest Solver",
        avatarId = "avatar_compass",
        createdAt = System.currentTimeMillis(),
        lastActiveAt = System.currentTimeMillis(),
        accountType = AccountType.GUEST,
        publicZynpathId = "ZYN-GUEST"
    )
) : PlayerProfileRepository {

    private val _profileFlow = MutableStateFlow(
        initialProfile ?: PlayerProfile(
            playerId = "guest_123",
            displayName = "Guest Solver",
            avatarId = "avatar_compass",
            createdAt = System.currentTimeMillis(),
            lastActiveAt = System.currentTimeMillis()
        )
    )

    private val _statsFlow = MutableStateFlow(
        PlayerStatistics(
            completedSoloLevels = 5,
            totalAvailableSoloLevels = 300,
            totalStarsEarned = 15,
            completedDailyChallenges = 2,
            currentDailyStreak = 2,
            bestDailyStreak = 2
        )
    )

    override fun observeProfile(): Flow<PlayerProfile> = _profileFlow.asStateFlow()

    override fun observeStatistics(): Flow<PlayerStatistics> = _statsFlow.asStateFlow()

    override suspend fun getProfile(): PlayerProfile = _profileFlow.value

    override suspend fun updateDisplayName(name: String): DisplayNameValidationResult {
        _profileFlow.value = _profileFlow.value.copy(displayName = name)
        return DisplayNameValidationResult.Valid(name)
    }

    override suspend fun updateAvatar(avatarId: String) {
        _profileFlow.value = _profileFlow.value.copy(avatarId = avatarId)
    }

    override suspend fun updateAccountLinking(accountType: AccountType, publicId: String?) {
        _profileFlow.value = _profileFlow.value.copy(
            accountType = accountType,
            publicZynpathId = publicId
        )
    }
}
