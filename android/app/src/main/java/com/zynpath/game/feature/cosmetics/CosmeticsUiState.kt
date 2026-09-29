package com.zynpath.game.feature.cosmetics

import com.zynpath.game.core.cosmetics.model.CosmeticAccessDecision
import com.zynpath.game.core.cosmetics.model.CosmeticCategory
import com.zynpath.game.core.cosmetics.model.CosmeticItem
import com.zynpath.game.core.cosmetics.model.EquippedCosmetics
import com.zynpath.game.core.player.AvatarCatalog
import com.zynpath.game.core.player.AvatarOption

/**
 * UI State for the Cosmetic Customization Screen.
 *
 * Implements Prompt 28 Sections 12, 17, 26, 33, 34 & 35:
 * - Granular categories: THEME, PATH_EFFECT, AVATAR_FRAME.
 * - Clear separation of currently equipped vs live previewed cosmetics.
 * - Access decision status (AVAILABLE, LOCKED, UNAVAILABLE, UNKNOWN).
 */
data class CosmeticsUiState(
    val selectedCategory: CosmeticCategory = CosmeticCategory.THEME,
    val itemsWithDecisions: List<Pair<CosmeticItem, CosmeticAccessDecision>> = emptyList(),
    val equippedCosmetics: EquippedCosmetics = EquippedCosmetics.default(),
    val previewCosmetics: EquippedCosmetics = EquippedCosmetics.default(),
    val selectedPreviewItem: CosmeticItem? = null,
    val isReducedMotion: Boolean = false,
    val currentAvatar: AvatarOption = AvatarCatalog.AVATARS.first(),
    val isEquipping: Boolean = false,
    val snackbarMessage: String? = null
) {
    val currentCategoryItems: List<Pair<CosmeticItem, CosmeticAccessDecision>>
        get() = itemsWithDecisions.filter { it.first.category == selectedCategory }

    val isPreviewEquipped: Boolean
        get() = when (selectedCategory) {
            CosmeticCategory.THEME -> previewCosmetics.themeId == equippedCosmetics.themeId
            CosmeticCategory.PATH_EFFECT -> previewCosmetics.pathEffectId == equippedCosmetics.pathEffectId
            CosmeticCategory.AVATAR_FRAME -> previewCosmetics.avatarFrameId == equippedCosmetics.avatarFrameId
        }

    val selectedDecision: CosmeticAccessDecision
        get() = selectedPreviewItem?.let { item ->
            itemsWithDecisions.firstOrNull { it.first.id == item.id }?.second
        } ?: CosmeticAccessDecision.AVAILABLE
}
