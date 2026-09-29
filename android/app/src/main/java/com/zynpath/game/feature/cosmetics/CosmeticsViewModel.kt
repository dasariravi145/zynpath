package com.zynpath.game.feature.cosmetics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.cosmetics.catalog.CosmeticCatalog
import com.zynpath.game.core.cosmetics.model.CosmeticCategory
import com.zynpath.game.core.cosmetics.model.CosmeticItem
import com.zynpath.game.core.cosmetics.model.EquippedCosmetics
import com.zynpath.game.core.cosmetics.repository.CosmeticsRepository
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.player.AvatarCatalog
import com.zynpath.game.core.player.PlayerProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel managing cosmetic catalog state, live preview manipulation,
 * and authoritative equipment actions.
 *
 * Implements Prompt 28 Sections 11, 12, 17, 26, 33, 34, 35, 36, 40 & 41.
 */
@HiltViewModel
class CosmeticsViewModel @Inject constructor(
    private val cosmeticsRepository: CosmeticsRepository,
    private val preferencesRepository: PreferencesRepository,
    private val profileRepository: PlayerProfileRepository
) : ViewModel() {

    private data class LocalUiState(
        val category: CosmeticCategory = CosmeticCategory.THEME,
        val previewOverride: EquippedCosmetics? = null,
        val selectedItem: CosmeticItem? = null,
        val snackbar: String? = null
    )

    private val _selectedCategory = MutableStateFlow(CosmeticCategory.THEME)
    private val _previewCosmetics = MutableStateFlow<EquippedCosmetics?>(null)
    private val _selectedPreviewItem = MutableStateFlow<CosmeticItem?>(null)
    private val _isEquipping = MutableStateFlow(false)
    private val _snackbarMessage = MutableStateFlow<String?>(null)

    private val _localUiState = combine(
        _selectedCategory,
        _previewCosmetics,
        _selectedPreviewItem,
        _snackbarMessage
    ) { category, previewOverride, selectedItem, snackbar ->
        LocalUiState(category, previewOverride, selectedItem, snackbar)
    }

    val uiState: StateFlow<CosmeticsUiState> = combine(
        cosmeticsRepository.catalogWithDecisionsFlow,
        cosmeticsRepository.equippedCosmeticsFlow,
        preferencesRepository.userPreferencesFlow,
        profileRepository.observeProfile(),
        _localUiState
    ) { catalogWithDecisions, equipped, prefs, profile, local ->
        val activePreview = local.previewOverride ?: equipped
        val currentAvatar = AvatarCatalog.getAvatar(profile.avatarId)

        val resolvedSelectedItem = local.selectedItem ?: run {
            // Default preview item to the equipped item in the current category
            val equippedId = when (local.category) {
                CosmeticCategory.THEME -> activePreview.themeId
                CosmeticCategory.PATH_EFFECT -> activePreview.pathEffectId
                CosmeticCategory.AVATAR_FRAME -> activePreview.avatarFrameId
            }
            CosmeticCatalog.findById(equippedId) ?: CosmeticCatalog.getFallback(local.category)
        }

        CosmeticsUiState(
            selectedCategory = local.category,
            itemsWithDecisions = catalogWithDecisions,
            equippedCosmetics = equipped,
            previewCosmetics = activePreview,
            selectedPreviewItem = resolvedSelectedItem,
            isReducedMotion = prefs.isReducedMotion,
            currentAvatar = currentAvatar,
            isEquipping = _isEquipping.value,
            snackbarMessage = local.snackbar
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CosmeticsUiState()
    )

    fun selectCategory(category: CosmeticCategory) {
        _selectedCategory.value = category
        val currentEquipped = uiState.value.equippedCosmetics
        val equippedId = when (category) {
            CosmeticCategory.THEME -> currentEquipped.themeId
            CosmeticCategory.PATH_EFFECT -> currentEquipped.pathEffectId
            CosmeticCategory.AVATAR_FRAME -> currentEquipped.avatarFrameId
        }
        _selectedPreviewItem.value = CosmeticCatalog.findById(equippedId)
    }

    fun previewItem(item: CosmeticItem) {
        _selectedPreviewItem.value = item
        val current = _previewCosmetics.value ?: uiState.value.equippedCosmetics
        _previewCosmetics.value = when (item.category) {
            CosmeticCategory.THEME -> current.copy(themeId = item.id)
            CosmeticCategory.PATH_EFFECT -> current.copy(pathEffectId = item.id)
            CosmeticCategory.AVATAR_FRAME -> current.copy(avatarFrameId = item.id)
        }
    }

    fun equipSelected() {
        val item = _selectedPreviewItem.value ?: return
        viewModelScope.launch {
            _isEquipping.value = true
            val success = cosmeticsRepository.equipCosmetic(item.id)
            _isEquipping.value = false
            if (success) {
                _snackbarMessage.value = "Equipped ${item.name} successfully."
            } else {
                _snackbarMessage.value = "Active Premium subscription required to equip ${item.name}."
            }
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            cosmeticsRepository.resetToDefaults()
            _previewCosmetics.value = EquippedCosmetics.default()
            _selectedPreviewItem.value = CosmeticCatalog.getFallback(_selectedCategory.value)
            _snackbarMessage.value = "Appearance reset to default theme and styles."
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}
