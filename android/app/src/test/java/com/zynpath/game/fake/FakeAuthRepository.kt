package com.zynpath.game.fake

import android.content.Context
import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.auth.model.AuthResult
import com.zynpath.game.core.auth.model.AuthSession
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.auth.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAuthRepository(
    initialState: AuthState = AuthState.GUEST,
    initialSession: AuthSession? = null
) : AuthRepository {

    private val _authState = MutableStateFlow(initialState)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentSession = MutableStateFlow(initialSession)
    override val currentSession: StateFlow<AuthSession?> = _currentSession.asStateFlow()

    fun setAuthState(state: AuthState, session: AuthSession? = null) {
        _authState.value = state
        _currentSession.value = session
    }

    override fun isProviderConfigured(provider: AuthProvider): Boolean = true

    override suspend fun signInWithGoogle(context: Context): AuthResult {
        val session = AuthSession(
            playerId = "player_google_1",
            publicZynpathId = "ZYN-GOOG",
            displayName = "Google Player",
            accountType = "REGISTERED",
            provider = AuthProvider.GOOGLE,
            expiresAt = System.currentTimeMillis() + 86400000L
        )
        setAuthState(AuthState.AUTHENTICATED, session)
        return AuthResult.Success(session)
    }

    override suspend fun signInWithFacebook(context: Context): AuthResult {
        val session = AuthSession(
            playerId = "player_fb_1",
            publicZynpathId = "ZYN-FB",
            displayName = "Facebook Player",
            accountType = "REGISTERED",
            provider = AuthProvider.FACEBOOK,
            expiresAt = System.currentTimeMillis() + 86400000L
        )
        setAuthState(AuthState.AUTHENTICATED, session)
        return AuthResult.Success(session)
    }

    override suspend fun linkGuestWithGoogle(context: Context): AuthResult = signInWithGoogle(context)

    override suspend fun linkGuestWithFacebook(context: Context): AuthResult = signInWithFacebook(context)

    override suspend fun restoreSession(): Boolean {
        return _currentSession.value != null
    }

    override suspend fun refreshSession(): Boolean {
        return _currentSession.value != null
    }

    override suspend fun signOut() {
        setAuthState(AuthState.GUEST, null)
    }
}
