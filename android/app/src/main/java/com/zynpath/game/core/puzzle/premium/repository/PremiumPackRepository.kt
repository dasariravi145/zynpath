package com.zynpath.game.core.puzzle.premium.repository

import com.zynpath.game.core.database.entity.PremiumPackProgressEntity
import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.player.PlayerProfileRepository
import com.zynpath.game.core.premium.FeatureAccessPolicy
import com.zynpath.game.core.premium.SubscriptionEntitlementRepository
import com.zynpath.game.core.premium.model.PremiumFeatureKey
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.premium.content.VerifiedPremiumPacks
import com.zynpath.game.core.puzzle.premium.model.PackAccessStatus
import com.zynpath.game.core.puzzle.premium.model.PackPublicationStatus
import com.zynpath.game.core.puzzle.premium.model.PremiumPackDefinition
import com.zynpath.game.core.puzzle.premium.model.PremiumPackItem
import com.zynpath.game.core.puzzle.premium.model.PremiumPackManifest
import com.zynpath.game.core.puzzle.premium.network.PremiumPackApiService
import com.zynpath.game.core.puzzle.premium.storage.PremiumPackStorageManager
import com.zynpath.game.core.auth.storage.SecureTokenStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Authoritative boundary for Premium Solo puzzle pack catalog, entitlement gating, and content delivery.
 *
 * Implements Prompt 27:
 * - Section 8, 9, 10, 11: Reusable catalog-driven pack architecture and versioning.
 * - Section 18, 19, 20: Entitlement-aware access decisions via [FeatureAccessPolicy].
 * - Section 25, 26, 27: Bounded offline access, expiration behavior, and resubscription.
 * - Section 39, 41: Reuses existing Solo engine; strictly isolated from competitive modes.
 */
interface PremiumPackRepository {
    fun observePacks(): Flow<List<PremiumPackItem>>
    fun observePackLevelProgress(packId: String): Flow<List<PremiumPackProgressEntity>>
    suspend fun getPackItem(packId: String): PremiumPackItem?
    suspend fun getPackManifest(packId: String): PremiumPackManifest?
    suspend fun getPackDefinition(packId: String): PremiumPackDefinition?
    suspend fun getPuzzle(packId: String, levelIndex: Int): Result<PuzzleDefinition>
    suspend fun downloadPack(packId: String): Result<Unit>
    suspend fun deletePack(packId: String, isGameplayActive: Boolean): Result<Unit>
    suspend fun recordLevelCompleted(
        packId: String,
        levelIndex: Int,
        puzzleId: String,
        puzzleVersion: Int,
        solveTimeMs: Long
    ): Result<Unit>
}

@Singleton
class PremiumPackRepositoryImpl @Inject constructor(
    private val storageManager: PremiumPackStorageManager,
    private val progressRepository: PremiumPackProgressRepository,
    private val entitlementRepository: SubscriptionEntitlementRepository,
    private val profileRepository: PlayerProfileRepository,
    private val apiService: PremiumPackApiService,
    private val tokenStorage: SecureTokenStorage
) : PremiumPackRepository {

    override fun observePacks(): Flow<List<PremiumPackItem>> {
        return profileRepository.observeProfile().flatMapLatest { profile ->
            val playerId = profile.playerId
            entitlementRepository.entitlement.flatMapLatest { entitlement ->
                // Combine with progress for each known pack
                val manifests = VerifiedPremiumPacks.ALL_MANIFESTS
                val flows = manifests.map { manifest ->
                    progressRepository.observeCompletedCount(playerId, manifest.packId)
                }

                combine(flows) { counts ->
                    manifests.mapIndexed { index, manifest ->
                        val completedCount = counts.getOrElse(index) { 0 }
                        val isFeatureUnlocked = FeatureAccessPolicy.isFeatureUnlocked(
                            featureKey = PremiumFeatureKey.PREMIUM_SOLO_PACKS,
                            gameMode = null,
                            entitlement = entitlement
                        )

                        val pubStatus = if (manifest.packId == "pack_grandmaster_7x7") {
                            PackPublicationStatus.COMING_SOON
                        } else {
                            PackPublicationStatus.PREMIUM
                        }

                        val accessStatus = when (pubStatus) {
                            PackPublicationStatus.COMING_SOON -> PackAccessStatus.UNAVAILABLE
                            PackPublicationStatus.UNAVAILABLE -> PackAccessStatus.UNAVAILABLE
                            PackPublicationStatus.FREE -> PackAccessStatus.ALLOWED
                            PackPublicationStatus.PREMIUM -> {
                                if (isFeatureUnlocked) {
                                    PackAccessStatus.ALLOWED
                                } else {
                                    PackAccessStatus.LOCKED
                                }
                            }
                        }

                        val isInstalled = storageManager.isPackInstalled(manifest.packId)

                        PremiumPackItem(
                            packId = manifest.packId,
                            packVersion = manifest.packVersion,
                            displayName = manifest.displayName,
                            description = manifest.description,
                            difficulty = manifest.difficulty,
                            puzzleCount = manifest.puzzleCount,
                            publicationStatus = pubStatus,
                            accessStatus = accessStatus,
                            isInstalled = isInstalled,
                            isDownloading = false,
                            downloadProgress = if (isInstalled) 1.0f else 0.0f,
                            completedPuzzlesCount = completedCount,
                            totalPuzzlesCount = manifest.puzzleCount
                        )
                    }
                }
            }
        }
    }

    override fun observePackLevelProgress(packId: String): Flow<List<PremiumPackProgressEntity>> {
        return profileRepository.observeProfile().flatMapLatest { profile ->
            progressRepository.observePackProgress(profile.playerId, packId)
        }
    }

    override suspend fun getPackItem(packId: String): PremiumPackItem? {
        val manifest = getPackManifest(packId) ?: return null
        val profile = profileRepository.getProfile()
        val entitlement = entitlementRepository.entitlement.value
        val isFeatureUnlocked = FeatureAccessPolicy.isFeatureUnlocked(
            featureKey = PremiumFeatureKey.PREMIUM_SOLO_PACKS,
            gameMode = null,
            entitlement = entitlement
        )

        val pubStatus = if (manifest.packId == "pack_grandmaster_7x7") {
            PackPublicationStatus.COMING_SOON
        } else {
            PackPublicationStatus.PREMIUM
        }

        val accessStatus = when (pubStatus) {
            PackPublicationStatus.COMING_SOON -> PackAccessStatus.UNAVAILABLE
            PackPublicationStatus.UNAVAILABLE -> PackAccessStatus.UNAVAILABLE
            PackPublicationStatus.FREE -> PackAccessStatus.ALLOWED
            PackPublicationStatus.PREMIUM -> if (isFeatureUnlocked) PackAccessStatus.ALLOWED else PackAccessStatus.LOCKED
        }

        val isInstalled = storageManager.isPackInstalled(manifest.packId)
        val progress = progressRepository.getLevelProgress(profile.playerId, packId, 1)

        return PremiumPackItem(
            packId = manifest.packId,
            packVersion = manifest.packVersion,
            displayName = manifest.displayName,
            description = manifest.description,
            difficulty = manifest.difficulty,
            puzzleCount = manifest.puzzleCount,
            publicationStatus = pubStatus,
            accessStatus = accessStatus,
            isInstalled = isInstalled,
            isDownloading = false,
            downloadProgress = if (isInstalled) 1.0f else 0.0f,
            completedPuzzlesCount = if (progress?.isCompleted == true) 1 else 0,
            totalPuzzlesCount = manifest.puzzleCount
        )
    }

    override suspend fun getPackManifest(packId: String): PremiumPackManifest? {
        return storageManager.loadPackDefinition(packId)?.manifest
            ?: VerifiedPremiumPacks.getManifest(packId)
    }

    override suspend fun getPackDefinition(packId: String): PremiumPackDefinition? {
        return storageManager.loadPackDefinition(packId)
            ?: VerifiedPremiumPacks.getPack(packId)
    }

    override suspend fun getPuzzle(packId: String, levelIndex: Int): Result<PuzzleDefinition> {
        val entitlement = entitlementRepository.entitlement.value
        val isFeatureUnlocked = FeatureAccessPolicy.isFeatureUnlocked(
            featureKey = PremiumFeatureKey.PREMIUM_SOLO_PACKS,
            gameMode = null,
            entitlement = entitlement
        )

        val manifest = getPackManifest(packId)
            ?: return Result.failure(IllegalArgumentException("Pack $packId not found"))

        if (manifest.packId == "pack_grandmaster_7x7") {
            return Result.failure(IllegalStateException("Pack is coming soon"))
        }

        if (!isFeatureUnlocked) {
            return Result.failure(IllegalStateException("Active Premium subscription required to access $packId"))
        }

        val packDef = getPackDefinition(packId)
            ?: return Result.failure(IllegalStateException("Pack $packId content is not installed or available"))

        if (levelIndex < 1 || levelIndex > packDef.puzzles.size) {
            return Result.failure(IllegalArgumentException("Invalid level index $levelIndex for pack $packId (size: ${packDef.puzzles.size})"))
        }

        val puzzle = packDef.puzzles[levelIndex - 1]
        return Result.success(puzzle)
    }

    override suspend fun downloadPack(packId: String): Result<Unit> {
        val entitlement = entitlementRepository.entitlement.value
        val isFeatureUnlocked = FeatureAccessPolicy.isFeatureUnlocked(
            featureKey = PremiumFeatureKey.PREMIUM_SOLO_PACKS,
            gameMode = null,
            entitlement = entitlement
        )

        if (!isFeatureUnlocked) {
            return Result.failure(IllegalStateException("ENTITLEMENT_REQUIRED: Active Premium subscription required to download pack $packId"))
        }

        // Check if we have a bundled verified pack available
        val bundled = VerifiedPremiumPacks.getPack(packId)
        if (bundled != null) {
            val installResult = storageManager.validateAndInstallPack(bundled)
            if (installResult.isSuccess) {
                return Result.success(Unit)
            }
        }

        // Attempt remote download if token available
        val token = tokenStorage.getSessionToken()
        if (token != null) {
            when (val netResult = apiService.downloadPack(token, packId)) {
                is NetworkResult.Success -> {
                    return storageManager.validateAndInstallPack(netResult.data)
                }
                is NetworkResult.Error -> {
                    return Result.failure(IllegalStateException("Download failed: HTTP ${netResult.code} - ${netResult.message}"))
                }
                is NetworkResult.Exception -> {
                    return Result.failure(netResult.throwable)
                }
            }
        }

        return if (bundled != null) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalStateException("Pack $packId not found or cannot be downloaded"))
        }
    }

    override suspend fun deletePack(packId: String, isGameplayActive: Boolean): Result<Unit> {
        return storageManager.deletePack(packId, isGameplayActive)
    }

    override suspend fun recordLevelCompleted(
        packId: String,
        levelIndex: Int,
        puzzleId: String,
        puzzleVersion: Int,
        solveTimeMs: Long
    ): Result<Unit> {
        val profile = profileRepository.getProfile()
        progressRepository.recordCompletion(
            playerId = profile.playerId,
            packId = packId,
            levelIndex = levelIndex,
            puzzleId = puzzleId,
            puzzleVersion = puzzleVersion,
            solveTimeMs = solveTimeMs
        )
        return Result.success(Unit)
    }
}
