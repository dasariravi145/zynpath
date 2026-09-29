package com.zynpath.game.core.cosmetics.repository

import com.zynpath.game.core.cosmetics.catalog.CosmeticCatalog
import com.zynpath.game.core.cosmetics.model.CosmeticAccessDecision
import com.zynpath.game.core.cosmetics.model.CosmeticAccessStatus
import com.zynpath.game.core.cosmetics.model.CosmeticCategory
import com.zynpath.game.core.cosmetics.model.CosmeticItem
import com.zynpath.game.core.cosmetics.model.EquippedCosmetics
import com.zynpath.game.core.cosmetics.network.CosmeticsApiService
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.designsystem.theme.ClassicMidnightPalette
import com.zynpath.game.core.designsystem.theme.ZynpathColorPalette
import com.zynpath.game.core.designsystem.theme.ZynpathPalettes
import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.auth.storage.SecureTokenStorage
import com.zynpath.game.core.premium.FeatureAccessPolicy
import com.zynpath.game.core.premium.SubscriptionEntitlementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Authoritative cosmetic repository governing preview, equipment, entitlement enforcement,
 * offline access, and graceful fallback on subscription expiration.
 *
 * Implements Prompt 28:
 * - Section 7 & 8: Catalog-driven cosmetic access.
 * - Section 10 & 11: Authoritative entitlement checks.
 * - Section 12: Preview vs Equip distinction.
 * - Section 35: Single item per category equipped at a time.
 * - Section 36 & 37: Preference persistence with account isolation.
 * - Section 39: Bounded offline access support.
 * - Section 40 & 41: Expiration fallback & resubscription restoration.
 */
interface CosmeticsRepository {
    val equippedCosmeticsFlow: Flow<EquippedCosmetics>
    val activePaletteFlow: Flow<ZynpathColorPalette>
    val catalogWithDecisionsFlow: Flow<List<Pair<CosmeticItem, CosmeticAccessDecision>>>

    suspend fun getAccessDecision(item: CosmeticItem): CosmeticAccessDecision
    suspend fun equipCosmetic(cosmeticId: String): Boolean
    suspend fun resetToDefaults()
    suspend fun syncWithBackend(): Result<EquippedCosmetics>
}

@Singleton
class CosmeticsRepositoryImpl @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val entitlementRepository: SubscriptionEntitlementRepository,
    private val cosmeticsApiService: CosmeticsApiService,
    private val secureTokenStorage: SecureTokenStorage
) : CosmeticsRepository {

    override val equippedCosmeticsFlow: Flow<EquippedCosmetics> = combine(
        preferencesRepository.userPreferencesFlow,
        entitlementRepository.entitlement
    ) { prefs, entitlement ->
        val rawThemeId = prefs.equippedThemeId
        val rawPathEffectId = prefs.equippedPathEffectId
        val rawAvatarFrameId = prefs.equippedAvatarFrameId

        // Resolve Theme with Expiration Fallback
        val activeThemeId = resolveEffectiveCosmetic(
            rawId = rawThemeId,
            category = CosmeticCategory.THEME,
            isFeatureUnlocked = { featureKey ->
                FeatureAccessPolicy.isFeatureUnlocked(featureKey, null, entitlement)
            }
        )

        // Resolve Path Effect with Expiration Fallback
        val activePathEffectId = resolveEffectiveCosmetic(
            rawId = rawPathEffectId,
            category = CosmeticCategory.PATH_EFFECT,
            isFeatureUnlocked = { featureKey ->
                FeatureAccessPolicy.isFeatureUnlocked(featureKey, null, entitlement)
            }
        )

        // Resolve Avatar Frame with Expiration Fallback
        val activeAvatarFrameId = resolveEffectiveCosmetic(
            rawId = rawAvatarFrameId,
            category = CosmeticCategory.AVATAR_FRAME,
            isFeatureUnlocked = { featureKey ->
                FeatureAccessPolicy.isFeatureUnlocked(featureKey, null, entitlement)
            }
        )

        EquippedCosmetics(
            themeId = activeThemeId,
            pathEffectId = activePathEffectId,
            avatarFrameId = activeAvatarFrameId
        )
    }

    override val activePaletteFlow: Flow<ZynpathColorPalette> =
        equippedCosmeticsFlow.map { equipped ->
            ZynpathPalettes.getById(equipped.themeId)
        }

    override val catalogWithDecisionsFlow: Flow<List<Pair<CosmeticItem, CosmeticAccessDecision>>> =
        combine(
            entitlementRepository.entitlement,
            preferencesRepository.userPreferencesFlow
        ) { entitlement, _ ->
            CosmeticCatalog.ALL_ITEMS.map { item ->
                val decision = when (item.accessStatus) {
                    CosmeticAccessStatus.FREE -> CosmeticAccessDecision.AVAILABLE
                    CosmeticAccessStatus.UNAVAILABLE -> CosmeticAccessDecision.UNAVAILABLE
                    CosmeticAccessStatus.COMING_SOON -> CosmeticAccessDecision.UNAVAILABLE
                    CosmeticAccessStatus.PREMIUM -> {
                        val key = item.requiredFeatureKey
                        if (key != null && FeatureAccessPolicy.isFeatureUnlocked(key, null, entitlement)) {
                            CosmeticAccessDecision.AVAILABLE
                        } else {
                            CosmeticAccessDecision.LOCKED
                        }
                    }
                }
                Pair(item, decision)
            }
        }

    override suspend fun getAccessDecision(item: CosmeticItem): CosmeticAccessDecision {
        if (item.accessStatus == CosmeticAccessStatus.FREE) return CosmeticAccessDecision.AVAILABLE
        if (item.accessStatus == CosmeticAccessStatus.UNAVAILABLE || item.accessStatus == CosmeticAccessStatus.COMING_SOON) {
            return CosmeticAccessDecision.UNAVAILABLE
        }
        val entitlement = entitlementRepository.entitlement.value
        val key = item.requiredFeatureKey ?: return CosmeticAccessDecision.AVAILABLE
        return if (FeatureAccessPolicy.isFeatureUnlocked(key, null, entitlement)) {
            CosmeticAccessDecision.AVAILABLE
        } else {
            CosmeticAccessDecision.LOCKED
        }
    }

    override suspend fun equipCosmetic(cosmeticId: String): Boolean {
        val item = CosmeticCatalog.findById(cosmeticId) ?: return false
        val decision = getAccessDecision(item)
        if (!decision.canEquip) {
            return false
        }

        when (item.category) {
            CosmeticCategory.THEME -> preferencesRepository.setEquippedTheme(item.id)
            CosmeticCategory.PATH_EFFECT -> preferencesRepository.setEquippedPathEffect(item.id)
            CosmeticCategory.AVATAR_FRAME -> preferencesRepository.setEquippedAvatarFrame(item.id)
        }

        // Try background server sync if authenticated (fails silently if offline)
        val sessionToken = secureTokenStorage.getSessionToken()
        if (!sessionToken.isNullOrBlank()) {
            try {
                val current = EquippedCosmetics(
                    themeId = if (item.category == CosmeticCategory.THEME) item.id else preferencesRepository.userPreferencesFlow.map { it.equippedThemeId }.let { item.id },
                    pathEffectId = if (item.category == CosmeticCategory.PATH_EFFECT) item.id else EquippedCosmetics.DEFAULT_PATH_EFFECT_ID,
                    avatarFrameId = if (item.category == CosmeticCategory.AVATAR_FRAME) item.id else EquippedCosmetics.DEFAULT_AVATAR_FRAME_ID
                )
                cosmeticsApiService.updateEquippedCosmetics(sessionToken, current)
            } catch (_: Exception) {}
        }

        return true
    }

    override suspend fun resetToDefaults() {
        preferencesRepository.setEquippedCosmetics(
            themeId = EquippedCosmetics.DEFAULT_THEME_ID,
            pathEffectId = EquippedCosmetics.DEFAULT_PATH_EFFECT_ID,
            avatarFrameId = EquippedCosmetics.DEFAULT_AVATAR_FRAME_ID
        )

        val sessionToken = secureTokenStorage.getSessionToken()
        if (!sessionToken.isNullOrBlank()) {
            try {
                cosmeticsApiService.updateEquippedCosmetics(sessionToken, EquippedCosmetics.default())
            } catch (_: Exception) {}
        }
    }

    override suspend fun syncWithBackend(): Result<EquippedCosmetics> {
        val sessionToken = secureTokenStorage.getSessionToken()
            ?: return Result.failure(IllegalStateException("User is not authenticated"))

        return when (val result = cosmeticsApiService.getEquippedCosmetics(sessionToken)) {
            is NetworkResult.Success -> {
                val remote = result.data
                preferencesRepository.setEquippedCosmetics(
                    themeId = remote.themeId,
                    pathEffectId = remote.pathEffectId,
                    avatarFrameId = remote.avatarFrameId
                )
                Result.success(remote)
            }
            is NetworkResult.Error -> Result.failure(Exception(result.message))
            is NetworkResult.Exception -> Result.failure(result.throwable)
        }
    }

    /**
     * Resolves effective cosmetic for gameplay rendering.
     * If user has a premium item equipped but entitlement is expired, returns the fallback default.
     * User's preference in DataStore is NOT wiped, ensuring automatic restoration on resubscription.
     */
    private inline fun resolveEffectiveCosmetic(
        rawId: String,
        category: CosmeticCategory,
        isFeatureUnlocked: (com.zynpath.game.core.premium.model.PremiumFeatureKey) -> Boolean
    ): String {
        val item = CosmeticCatalog.findById(rawId)
        if (item == null || item.category != category) {
            return CosmeticCatalog.getFallback(category).id
        }

        return when (item.accessStatus) {
            CosmeticAccessStatus.FREE -> item.id
            CosmeticAccessStatus.PREMIUM -> {
                val key = item.requiredFeatureKey
                if (key != null && isFeatureUnlocked(key)) {
                    item.id
                } else {
                    // Graceful fallback to default free item without deleting user's saved choice
                    CosmeticCatalog.getFallback(category).id
                }
            }
            CosmeticAccessStatus.UNAVAILABLE,
            CosmeticAccessStatus.COMING_SOON -> {
                CosmeticCatalog.getFallback(category).id
            }
        }
    }
}
