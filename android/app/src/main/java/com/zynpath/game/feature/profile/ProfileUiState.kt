package com.zynpath.game.feature.profile

import com.zynpath.game.core.player.AccountLinkingState
import com.zynpath.game.core.player.AvatarOption
import com.zynpath.game.core.player.DisplayNameValidationResult
import com.zynpath.game.core.player.PlayerProfile
import com.zynpath.game.core.player.PlayerStatistics

/**
 * UI State for the Player Profile screen.
 *
 * Implements Prompt 17 & Prompt 18 Section 19:
 * - Guest status & statistics
 * - Name editing & avatar selection
 * - Account linking with Google & Facebook
 * - Honest provider configuration indicators
 * - Account conflict handling
 */
data class ProfileUiState(
    val profile: PlayerProfile? = null,
    val statistics: PlayerStatistics = PlayerStatistics(),
    val currentAvatar: AvatarOption? = null,
    val isEditingName: Boolean = false,
    val nameInput: String = "",
    val nameValidationResult: DisplayNameValidationResult? = null,
    val isAvatarPickerOpen: Boolean = false,
    val showAccountLinkingDialog: Boolean = false,
    val accountLinkingState: AccountLinkingState = AccountLinkingState(),
    val isLinkingLoading: Boolean = false,
    val isGoogleAvailable: Boolean = false,
    val isFacebookAvailable: Boolean = false,
    val conflictDialogMessage: String? = null,
    val unconfiguredNotice: String? = null,
    val isSaving: Boolean = false,
    val snackbarMessage: String? = null,
    val competitiveStats: com.zynpath.game.core.multiplayer.model.CompetitiveStats? = null,
    val equippedAvatarFrameId: String = "frame_default_slate"
)
