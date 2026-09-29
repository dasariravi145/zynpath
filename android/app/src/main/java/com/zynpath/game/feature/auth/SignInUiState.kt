package com.zynpath.game.feature.auth

import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.auth.model.AuthSession
import com.zynpath.game.core.auth.model.AuthState

/**
 * UI state for the optional Sign-In screen.
 *
 * Implements Prompt 18 Sections 16, 18 & 38 and Prompt 03/24.
 */
data class SignInUiState(
    val authState: AuthState = AuthState.GUEST,
    val currentSession: AuthSession? = null,
    val isGoogleAvailable: Boolean = false,
    val isFacebookAvailable: Boolean = false,
    val isLoading: Boolean = false,
    val isReducedMotion: Boolean = false,
    val errorMessage: String? = null,
    val conflictDialogMessage: String? = null,
    val unconfiguredProviderNotice: String? = null,
    val activeProvider: AuthProvider? = null
)
