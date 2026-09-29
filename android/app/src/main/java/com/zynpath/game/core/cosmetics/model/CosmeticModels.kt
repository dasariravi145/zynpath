package com.zynpath.game.core.cosmetics.model

import com.zynpath.game.core.premium.model.PremiumFeatureKey

/**
 * Primary categories of cosmetic personalization in Zynpath.
 *
 * Implements Prompt 28 Sections 5 & 6.
 * Cosmetics are optional visual personalization and must NEVER modify
 * puzzle rules, puzzle difficulty, scoring, timing, or competitive outcomes.
 */
enum class CosmeticCategory(val displayName: String) {
    THEME("Themes"),
    PATH_EFFECT("Path Effects"),
    AVATAR_FRAME("Avatar Frames")
}

/**
 * Access availability classification for catalog cosmetics.
 *
 * Implements Prompt 28 Section 8.
 */
enum class CosmeticAccessStatus {
    FREE,
    PREMIUM,
    UNAVAILABLE,
    COMING_SOON
}

/**
 * Authoritative access decision for a cosmetic item given the player's entitlement.
 *
 * Implements Prompt 28 Sections 11 & 12.
 */
enum class CosmeticAccessDecision {
    AVAILABLE,
    LOCKED,
    UNAVAILABLE,
    UNKNOWN;

    val canEquip: Boolean get() = this == AVAILABLE
}

/**
 * Core definition of a cosmetic item.
 *
 * Implements Prompt 28 Section 7.
 */
data class CosmeticItem(
    val id: String,
    val category: CosmeticCategory,
    val name: String,
    val description: String,
    val accessStatus: CosmeticAccessStatus,
    val requiredFeatureKey: PremiumFeatureKey?,
    val isDefault: Boolean = false,
    val version: Int = 1
)

/**
 * Player's currently equipped cosmetic configuration.
 *
 * Implements Prompt 28 Sections 9 & 35:
 * Exactly one item per category can be equipped at a time.
 */
data class EquippedCosmetics(
    val themeId: String = DEFAULT_THEME_ID,
    val pathEffectId: String = DEFAULT_PATH_EFFECT_ID,
    val avatarFrameId: String = DEFAULT_AVATAR_FRAME_ID
) {
    companion object {
        const val DEFAULT_THEME_ID = "theme_classic_midnight"
        const val DEFAULT_PATH_EFFECT_ID = "path_solid_glow"
        const val DEFAULT_AVATAR_FRAME_ID = "frame_default_slate"

        fun default(): EquippedCosmetics = EquippedCosmetics(
            themeId = DEFAULT_THEME_ID,
            pathEffectId = DEFAULT_PATH_EFFECT_ID,
            avatarFrameId = DEFAULT_AVATAR_FRAME_ID
        )
    }
}
