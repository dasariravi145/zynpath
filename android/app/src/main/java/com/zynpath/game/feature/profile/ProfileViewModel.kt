package com.zynpath.game.feature.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.auth.model.AuthResult
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.auth.repository.AuthRepository
import com.zynpath.game.core.player.AccountLinkingState
import com.zynpath.game.core.player.AccountType
import com.zynpath.game.core.player.AvatarCatalog
import com.zynpath.game.core.player.DisplayNameValidationResult
import com.zynpath.game.core.player.DisplayNameValidator
import com.zynpath.game.core.player.PlayerProfileRepository
import com.zynpath.game.core.multiplayer.model.CompetitiveStats
import com.zynpath.game.core.multiplayer.repository.MultiplayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private data class ProfileEditorState(
    val isEditingName: Boolean = false,
    val nameInput: String = "",
    val nameValidationResult: DisplayNameValidationResult? = null,
    val isAvatarPickerOpen: Boolean = false,
    val showAccountLinkingDialog: Boolean = false,
    val conflictDialogMessage: String? = null,
    val unconfiguredNotice: String? = null,
    val snackbarMessage: String? = null
)

/**
 * ViewModel managing the Player Profile screen state, name edits, avatar selections,
 * and account linking with external providers.
 *
 * Implements Prompt 17, Prompt 18 & Prompt 24 Section 35:
 * - Profile editing and statistics
 * - Guest account linking with Google and Facebook
 * - Real server-authoritative competitive statistics integration
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: PlayerProfileRepository,
    private val authRepository: AuthRepository,
    private val multiplayerRepository: MultiplayerRepository,
    private val cosmeticsRepository: com.zynpath.game.core.cosmetics.repository.CosmeticsRepository
) : ViewModel() {

    private val _editorState = MutableStateFlow(ProfileEditorState())
    private val _competitiveStats = MutableStateFlow<CompetitiveStats?>(null)

    init {
        viewModelScope.launch {
            authRepository.authState.collect { state ->
                if (state == AuthState.AUTHENTICATED) {
                    _competitiveStats.value = multiplayerRepository.getCompetitiveStats()
                } else {
                    _competitiveStats.value = null
                }
            }
        }
    }

    private val _compEditorAndCosmetics = combine(
        _competitiveStats,
        _editorState,
        cosmeticsRepository.equippedCosmeticsFlow
    ) { comp, editor, cosmetics ->
        Triple(comp, editor, cosmetics)
    }

    val uiState: StateFlow<ProfileUiState> = combine(
        profileRepository.observeProfile(),
        profileRepository.observeStatistics(),
        authRepository.authState,
        authRepository.currentSession,
        _compEditorAndCosmetics
    ) { profile, stats, authState, session, compEditorAndCosmetics ->
        val (compStats, editor, equippedCosmetics) = compEditorAndCosmetics
        val isLinked = profile.accountType == AccountType.LINKED
        val providerName = session?.provider?.displayName ?: "External Provider"

        val linkingState = AccountLinkingState(
            accountType = profile.accountType,
            provider = if (isLinked) providerName else null,
            publicZynpathId = profile.publicZynpathId,
            isLinked = isLinked,
            statusMessage = if (isLinked) "Linked with $providerName" else "Guest Account (Local Offline)"
        )

        ProfileUiState(
            profile = profile,
            statistics = stats,
            currentAvatar = AvatarCatalog.getAvatar(profile.avatarId),
            isEditingName = editor.isEditingName,
            nameInput = editor.nameInput,
            nameValidationResult = editor.nameValidationResult,
            isAvatarPickerOpen = editor.isAvatarPickerOpen,
            showAccountLinkingDialog = editor.showAccountLinkingDialog,
            accountLinkingState = linkingState,
            isLinkingLoading = authState == AuthState.LINKING || authState == AuthState.SIGNING_IN,
            isGoogleAvailable = authRepository.isProviderConfigured(AuthProvider.GOOGLE),
            isFacebookAvailable = authRepository.isProviderConfigured(AuthProvider.FACEBOOK),
            conflictDialogMessage = editor.conflictDialogMessage,
            unconfiguredNotice = editor.unconfiguredNotice,
            snackbarMessage = editor.snackbarMessage,
            competitiveStats = compStats,
            equippedAvatarFrameId = equippedCosmetics.avatarFrameId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState(
            isGoogleAvailable = authRepository.isProviderConfigured(AuthProvider.GOOGLE),
            isFacebookAvailable = authRepository.isProviderConfigured(AuthProvider.FACEBOOK)
        )
    )

    fun startEditingName() {
        val currentName = uiState.value.profile?.displayName ?: "Pathfinder"
        _editorState.update {
            it.copy(
                isEditingName = true,
                nameInput = currentName,
                nameValidationResult = null
            )
        }
    }

    fun onNameInputChanged(newInput: String) {
        _editorState.update { current ->
            current.copy(
                nameInput = newInput,
                nameValidationResult = if (current.nameValidationResult != null) {
                    DisplayNameValidator.validate(newInput)
                } else null
            )
        }
    }

    fun saveName() {
        val input = _editorState.value.nameInput
        val result = DisplayNameValidator.validate(input)
        if (result !is DisplayNameValidationResult.Valid) {
            _editorState.update { it.copy(nameValidationResult = result) }
            return
        }

        viewModelScope.launch {
            val saveResult = profileRepository.updateDisplayName(input)
            if (saveResult is DisplayNameValidationResult.Valid) {
                _editorState.update {
                    it.copy(
                        isEditingName = false,
                        nameValidationResult = null,
                        snackbarMessage = "Display name updated"
                    )
                }
            } else {
                _editorState.update { it.copy(nameValidationResult = saveResult) }
            }
        }
    }

    fun cancelEditingName() {
        _editorState.update {
            it.copy(
                isEditingName = false,
                nameValidationResult = null
            )
        }
    }

    fun toggleAvatarPicker(open: Boolean) {
        _editorState.update { it.copy(isAvatarPickerOpen = open) }
    }

    fun selectAvatar(avatarId: String) {
        viewModelScope.launch {
            profileRepository.updateAvatar(avatarId)
            _editorState.update {
                it.copy(
                    isAvatarPickerOpen = false,
                    snackbarMessage = "Avatar updated"
                )
            }
        }
    }

    fun toggleAccountLinkingDialog(show: Boolean) {
        _editorState.update { it.copy(showAccountLinkingDialog = show) }
    }

    fun linkWithGoogle(context: Context) {
        viewModelScope.launch {
            val result = authRepository.linkGuestWithGoogle(context)
            handleLinkResult(result)
        }
    }

    fun linkWithFacebook(context: Context) {
        viewModelScope.launch {
            val result = authRepository.linkGuestWithFacebook(context)
            handleLinkResult(result)
        }
    }

    private fun handleLinkResult(result: AuthResult) {
        when (result) {
            is AuthResult.Success -> {
                _editorState.update {
                    it.copy(
                        showAccountLinkingDialog = false,
                        snackbarMessage = "Account linked successfully! Public ID: ${result.session.publicZynpathId}"
                    )
                }
            }
            is AuthResult.Conflict -> {
                _editorState.update {
                    it.copy(
                        conflictDialogMessage = result.message
                    )
                }
            }
            is AuthResult.ProviderNotConfigured -> {
                _editorState.update {
                    it.copy(
                        unconfiguredNotice = "[${result.provider.displayName} Pending] " + result.message
                    )
                }
            }
            is AuthResult.Error -> {
                _editorState.update {
                    it.copy(
                        snackbarMessage = "Linking failed: ${result.message}"
                    )
                }
            }
            is AuthResult.Cancelled -> {
                // User cancelled provider prompt, progress preserved
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _editorState.update {
                it.copy(
                    showAccountLinkingDialog = false,
                    snackbarMessage = "Signed out. Restored to offline guest mode."
                )
            }
        }
    }

    fun dismissConflictDialog() {
        _editorState.update { it.copy(conflictDialogMessage = null) }
    }

    fun dismissUnconfiguredNotice() {
        _editorState.update { it.copy(unconfiguredNotice = null) }
    }

    fun clearSnackbar() {
        _editorState.update { it.copy(snackbarMessage = null) }
    }
}
