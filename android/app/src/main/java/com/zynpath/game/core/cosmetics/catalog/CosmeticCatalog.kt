package com.zynpath.game.core.cosmetics.catalog

import com.zynpath.game.core.cosmetics.model.CosmeticAccessStatus
import com.zynpath.game.core.cosmetics.model.CosmeticCategory
import com.zynpath.game.core.cosmetics.model.CosmeticItem
import com.zynpath.game.core.cosmetics.model.EquippedCosmetics
import com.zynpath.game.core.premium.model.PremiumFeatureKey

/**
 * Authoritative client-side cosmetic catalog for Zynpath.
 *
 * Implements Prompt 28 Sections 7, 8, 9, 14, 15, 20, 21, 28, 29:
 * - Free defaults available unconditionally.
 * - Premium items tied to authoritative PremiumFeatureKey.
 * - Distinct, curated themes, path effects, and avatar frames.
 */
object CosmeticCatalog {

    // ==========================================
    // THEMES
    // ==========================================
    val THEME_CLASSIC_MIDNIGHT = CosmeticItem(
        id = EquippedCosmetics.DEFAULT_THEME_ID,
        category = CosmeticCategory.THEME,
        name = "Classic Midnight",
        description = "Signature Zynpath navy blue palette with soothing mint accents and cyan trail.",
        accessStatus = CosmeticAccessStatus.FREE,
        requiredFeatureKey = null,
        isDefault = true
    )

    val THEME_PURE_DARK = CosmeticItem(
        id = "theme_pure_dark",
        category = CosmeticCategory.THEME,
        name = "Pure Dark OLED",
        description = "Deep OLED true black background designed for maximum contrast and battery efficiency.",
        accessStatus = CosmeticAccessStatus.FREE,
        requiredFeatureKey = null
    )

    val THEME_CYBER_NEON = CosmeticItem(
        id = "theme_cyber_neon",
        category = CosmeticCategory.THEME,
        name = "Cyber Neon",
        description = "Vibrant synthwave aesthetic with electric magenta accents and neon cyan aura.",
        accessStatus = CosmeticAccessStatus.PREMIUM,
        requiredFeatureKey = PremiumFeatureKey.PREMIUM_THEMES
    )

    val THEME_EMERALD_FOREST = CosmeticItem(
        id = "theme_emerald_forest",
        category = CosmeticCategory.THEME,
        name = "Emerald Forest",
        description = "Deep botanical greens with jade checkpoints and warm golden highlights.",
        accessStatus = CosmeticAccessStatus.PREMIUM,
        requiredFeatureKey = PremiumFeatureKey.PREMIUM_THEMES
    )

    val THEME_SOLAR_AMBER = CosmeticItem(
        id = "theme_solar_amber",
        category = CosmeticCategory.THEME,
        name = "Solar Amber",
        description = "Obsidian charcoal canvas paired with radiant sun-gold paths and amber accents.",
        accessStatus = CosmeticAccessStatus.PREMIUM,
        requiredFeatureKey = PremiumFeatureKey.PREMIUM_THEMES
    )

    val THEME_ARCTIC_FROST = CosmeticItem(
        id = "theme_arctic_frost",
        category = CosmeticCategory.THEME,
        name = "Arctic Frost",
        description = "Glacial blue tones and polar shimmer. Available in future updates.",
        accessStatus = CosmeticAccessStatus.COMING_SOON,
        requiredFeatureKey = PremiumFeatureKey.PREMIUM_THEMES
    )

    // ==========================================
    // PATH EFFECTS
    // ==========================================
    val PATH_SOLID_GLOW = CosmeticItem(
        id = EquippedCosmetics.DEFAULT_PATH_EFFECT_ID,
        category = CosmeticCategory.PATH_EFFECT,
        name = "Solid Glow",
        description = "Crisp, responsive dual-layer solid path with a soft ambient edge glow.",
        accessStatus = CosmeticAccessStatus.FREE,
        requiredFeatureKey = null,
        isDefault = true
    )

    val PATH_CYAN_PULSE = CosmeticItem(
        id = "path_cyan_pulse",
        category = CosmeticCategory.PATH_EFFECT,
        name = "Cyan Energy Pulse",
        description = "Harmonic pulse of electrical energy traveling along your connected trail.",
        accessStatus = CosmeticAccessStatus.PREMIUM,
        requiredFeatureKey = PremiumFeatureKey.PREMIUM_PATH_EFFECTS
    )

    val PATH_GOLD_SHIMMER = CosmeticItem(
        id = "path_gold_shimmer",
        category = CosmeticCategory.PATH_EFFECT,
        name = "Golden Shimmer",
        description = "Prestigious gold dual-gradient path with subtle sparkling core highlights.",
        accessStatus = CosmeticAccessStatus.PREMIUM,
        requiredFeatureKey = PremiumFeatureKey.PREMIUM_PATH_EFFECTS
    )

    val PATH_EMBER_TRAIL = CosmeticItem(
        id = "path_ember_trail",
        category = CosmeticCategory.PATH_EFFECT,
        name = "Ember Trail",
        description = "Warm blazing gradient radiating heat from the path head back to checkpoint 1.",
        accessStatus = CosmeticAccessStatus.PREMIUM,
        requiredFeatureKey = PremiumFeatureKey.PREMIUM_PATH_EFFECTS
    )

    val PATH_STARLIGHT = CosmeticItem(
        id = "path_starlight",
        category = CosmeticCategory.PATH_EFFECT,
        name = "Starlight Stream",
        description = "Subtle cosmic particles drifting along the path. Coming in a future update.",
        accessStatus = CosmeticAccessStatus.COMING_SOON,
        requiredFeatureKey = PremiumFeatureKey.PREMIUM_PATH_EFFECTS
    )

    // ==========================================
    // AVATAR FRAMES
    // ==========================================
    val FRAME_DEFAULT_SLATE = CosmeticItem(
        id = EquippedCosmetics.DEFAULT_AVATAR_FRAME_ID,
        category = CosmeticCategory.AVATAR_FRAME,
        name = "Slate Ring",
        description = "Clean, understated circular border that frames your avatar without distraction.",
        accessStatus = CosmeticAccessStatus.FREE,
        requiredFeatureKey = null,
        isDefault = true
    )

    val FRAME_SILVER_CREST = CosmeticItem(
        id = "frame_silver_crest",
        category = CosmeticCategory.AVATAR_FRAME,
        name = "Silver Crest",
        description = "Polished silver border accented with four cardinal directional markers.",
        accessStatus = CosmeticAccessStatus.FREE,
        requiredFeatureKey = null
    )

    val FRAME_GOLD_ACCENT = CosmeticItem(
        id = "frame_gold_accent",
        category = CosmeticCategory.AVATAR_FRAME,
        name = "Royal Gold Crown",
        description = "Prestigious gold double-ring border adorned with a diamond crown accent.",
        accessStatus = CosmeticAccessStatus.PREMIUM,
        requiredFeatureKey = PremiumFeatureKey.PREMIUM_AVATAR_FRAMES
    )

    val FRAME_NEON_RING = CosmeticItem(
        id = "frame_neon_ring",
        category = CosmeticCategory.AVATAR_FRAME,
        name = "Cyber Ring",
        description = "Glowing cyberpunk frame with dual concentric neon lines and electric pulse.",
        accessStatus = CosmeticAccessStatus.PREMIUM,
        requiredFeatureKey = PremiumFeatureKey.PREMIUM_AVATAR_FRAMES
    )

    val FRAME_EMERALD_GEOMETRIC = CosmeticItem(
        id = "frame_emerald_geometric",
        category = CosmeticCategory.AVATAR_FRAME,
        name = "Emerald Facet",
        description = "Faceted geometric border crafted with luminous emerald nodes and gold pins.",
        accessStatus = CosmeticAccessStatus.PREMIUM,
        requiredFeatureKey = PremiumFeatureKey.PREMIUM_AVATAR_FRAMES
    )

    val FRAME_COSMIC_AURORA = CosmeticItem(
        id = "frame_cosmic_aurora",
        category = CosmeticCategory.AVATAR_FRAME,
        name = "Cosmic Aurora",
        description = "Iridescent celestial border. Coming in a future update.",
        accessStatus = CosmeticAccessStatus.COMING_SOON,
        requiredFeatureKey = PremiumFeatureKey.PREMIUM_AVATAR_FRAMES
    )

    val ALL_ITEMS: List<CosmeticItem> = listOf(
        // Themes
        THEME_CLASSIC_MIDNIGHT,
        THEME_PURE_DARK,
        THEME_CYBER_NEON,
        THEME_EMERALD_FOREST,
        THEME_SOLAR_AMBER,
        THEME_ARCTIC_FROST,

        // Path Effects
        PATH_SOLID_GLOW,
        PATH_CYAN_PULSE,
        PATH_GOLD_SHIMMER,
        PATH_EMBER_TRAIL,
        PATH_STARLIGHT,

        // Avatar Frames
        FRAME_DEFAULT_SLATE,
        FRAME_SILVER_CREST,
        FRAME_GOLD_ACCENT,
        FRAME_NEON_RING,
        FRAME_EMERALD_GEOMETRIC,
        FRAME_COSMIC_AURORA
    )

    fun getItemsByCategory(category: CosmeticCategory): List<CosmeticItem> {
        return ALL_ITEMS.filter { it.category == category }
    }

    fun findById(id: String): CosmeticItem? {
        return ALL_ITEMS.firstOrNull { it.id == id }
    }

    fun getFallback(category: CosmeticCategory): CosmeticItem {
        return when (category) {
            CosmeticCategory.THEME -> THEME_CLASSIC_MIDNIGHT
            CosmeticCategory.PATH_EFFECT -> PATH_SOLID_GLOW
            CosmeticCategory.AVATAR_FRAME -> FRAME_DEFAULT_SLATE
        }
    }
}
