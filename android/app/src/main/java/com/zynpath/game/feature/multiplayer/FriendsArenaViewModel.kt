package com.zynpath.game.feature.multiplayer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.auth.model.AuthResult
import com.zynpath.game.core.auth.repository.AuthRepository
import com.zynpath.game.core.datastore.PreferencesRepository
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
 * ViewModel managing authoritative authentication, Facebook connection eligibility,
 * and entry actions for Friends Arena (Prompt 14/24).
 */
@HiltViewModel
class FriendsArenaViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val playerProfileRepository: PlayerProfileRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private data class TransientState(
        val isLoading: Boolean = false,
        val userNotice: String? = null
    )

    private val _isActionLoading = MutableStateFlow(false)
    private val _userNoticeMessage = MutableStateFlow<String?>(null)

    private val _transientState = combine(
        _isActionLoading,
        _userNoticeMessage
    ) { loading, notice ->
        TransientState(loading, notice)
    }

    val uiState: StateFlow<FriendsArenaUiState> = combine(
        authRepository.authState,
        authRepository.currentSession,
        playerProfileRepository.observeProfile(),
        preferencesRepository.userPreferencesFlow,
        _transientState
    ) { authState, session, profile, prefs, transient ->
        val isFacebookConfigured = authRepository.isProviderConfigured(AuthProvider.FACEBOOK)
        val isGoogleConfigured = authRepository.isProviderConfigured(AuthProvider.GOOGLE)

        FriendsArenaUiState(
            authState = authState,
            session = session,
            playerProfile = profile,
            isFacebookConfigured = isFacebookConfigured,
            isGoogleConfigured = isGoogleConfigured,
            isReducedMotion = prefs.isReducedMotion,
            isLoading = transient.isLoading,
            userNotice = transient.userNotice
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FriendsArenaUiState(
            isFacebookConfigured = authRepository.isProviderConfigured(AuthProvider.FACEBOOK),
            isGoogleConfigured = authRepository.isProviderConfigured(AuthProvider.GOOGLE)
        )
    )

    /**
     * Connect or link Facebook explicitly for Facebook friend discovery.
     * Google authentication does NOT grant or infer Facebook friend authorization.
     */
    fun onConnectFacebook(context: Context) {
        if (!authRepository.isProviderConfigured(AuthProvider.FACEBOOK)) {
            _userNoticeMessage.value = "Facebook developer configuration is currently pending in this environment. Room Code and invitation link matchmaking remain fully accessible."
            return
        }

        viewModelScope.launch {
            _isActionLoading.value = true
            val currentSession = authRepository.currentSession.value

            val result = if (currentSession != null) {
                // Link existing authenticated profile with Facebook
                authRepository.linkGuestWithFacebook(context)
            } else {
                // Sign in directly with Facebook
                authRepository.signInWithFacebook(context)
            }
            _isActionLoading.value = false

            when (result) {
                is AuthResult.Success -> {
                    _userNoticeMessage.value = "Facebook successfully connected! Friend discovery will be available in the upcoming multiplayer release."
                }
                is AuthResult.Cancelled -> {
                    // User dismissed the auth dialog; no error needed
                }
                is AuthResult.Error -> {
                    _userNoticeMessage.value = result.message
                }
                is AuthResult.ProviderNotConfigured -> {
                    _userNoticeMessage.value = "Facebook configuration is pending setup in this environment."
                }
                is AuthResult.Conflict -> {
                    _userNoticeMessage.value = "This Facebook account is already linked to another Zynpath profile."
                }
            }
        }
    }

    /**
     * Displays an honest status dialog when interacting with actions scheduled for later prompts.
     */
    fun showActionNotice(actionTitle: String, details: String) {
        _userNoticeMessage.value = "$actionTitle\n\n$details"
    }

    /**
     * Clears user alert notices.
     */
    fun clearUserNotice() {
        _userNoticeMessage.value = null
    }
}
