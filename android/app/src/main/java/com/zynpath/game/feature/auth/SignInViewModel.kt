package com.zynpath.game.feature.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.auth.model.AuthResult
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.auth.repository.AuthRepository
import com.zynpath.game.core.datastore.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private data class SignInTransientState(
    val errorMessage: String? = null,
    val conflictDialogMessage: String? = null,
    val unconfiguredProviderNotice: String? = null,
    val activeProvider: AuthProvider? = null
)

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _transientState = MutableStateFlow(SignInTransientState())

    val uiState: StateFlow<SignInUiState> = combine(
        authRepository.authState,
        authRepository.currentSession,
        preferencesRepository.userPreferencesFlow,
        _transientState
    ) { state, session, prefs, transient ->
        SignInUiState(
            authState = state,
            currentSession = session,
            isGoogleAvailable = authRepository.isProviderConfigured(AuthProvider.GOOGLE),
            isFacebookAvailable = authRepository.isProviderConfigured(AuthProvider.FACEBOOK),
            isLoading = state == AuthState.SIGNING_IN || state == AuthState.LINKING,
            isReducedMotion = prefs.isReducedMotion,
            errorMessage = transient.errorMessage,
            conflictDialogMessage = transient.conflictDialogMessage,
            unconfiguredProviderNotice = transient.unconfiguredProviderNotice,
            activeProvider = transient.activeProvider
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SignInUiState(
            isGoogleAvailable = authRepository.isProviderConfigured(AuthProvider.GOOGLE),
            isFacebookAvailable = authRepository.isProviderConfigured(AuthProvider.FACEBOOK)
        )
    )

    fun signInWithGoogle(context: Context) {
        _transientState.update { it.copy(activeProvider = AuthProvider.GOOGLE, errorMessage = null, unconfiguredProviderNotice = null) }
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(context)
            handleResult(result)
        }
    }

    fun signInWithFacebook(context: Context) {
        _transientState.update { it.copy(activeProvider = AuthProvider.FACEBOOK, errorMessage = null, unconfiguredProviderNotice = null) }
        viewModelScope.launch {
            val result = authRepository.signInWithFacebook(context)
            handleResult(result)
        }
    }

    fun linkWithGoogle(context: Context) {
        _transientState.update { it.copy(activeProvider = AuthProvider.GOOGLE, errorMessage = null, unconfiguredProviderNotice = null) }
        viewModelScope.launch {
            val result = authRepository.linkGuestWithGoogle(context)
            handleResult(result)
        }
    }

    fun linkWithFacebook(context: Context) {
        _transientState.update { it.copy(activeProvider = AuthProvider.FACEBOOK, errorMessage = null, unconfiguredProviderNotice = null) }
        viewModelScope.launch {
            val result = authRepository.linkGuestWithFacebook(context)
            handleResult(result)
        }
    }

    private fun handleResult(result: AuthResult) {
        when (result) {
            is AuthResult.Success -> {
                _transientState.update { it.copy(errorMessage = null, conflictDialogMessage = null, unconfiguredProviderNotice = null) }
            }
            is AuthResult.Conflict -> {
                _transientState.update {
                    it.copy(
                        conflictDialogMessage = result.message,
                        errorMessage = null
                    )
                }
            }
            is AuthResult.ProviderNotConfigured -> {
                _transientState.update {
                    it.copy(
                        unconfiguredProviderNotice = "[${result.provider.displayName} Pending] " + result.message
                    )
                }
            }
            is AuthResult.Error -> {
                _transientState.update {
                    it.copy(
                        errorMessage = result.message
                    )
                }
            }
            is AuthResult.Cancelled -> {
                _transientState.update { it.copy(errorMessage = null) }
            }
        }
    }

    fun clearError() {
        _transientState.update { it.copy(errorMessage = null) }
    }

    fun dismissConflictDialog() {
        _transientState.update { it.copy(conflictDialogMessage = null) }
    }

    fun dismissUnconfiguredNotice() {
        _transientState.update { it.copy(unconfiguredProviderNotice = null) }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}
