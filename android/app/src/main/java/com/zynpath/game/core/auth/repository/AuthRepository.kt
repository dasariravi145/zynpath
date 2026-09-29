package com.zynpath.game.core.auth.repository

import android.content.Context
import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.auth.model.AuthResult
import com.zynpath.game.core.auth.model.AuthSession
import com.zynpath.game.core.auth.model.AuthState
import kotlinx.coroutines.flow.StateFlow

/**
 * Authoritative boundary managing authentication state, session persistence, provider exchanges,
 * and safe guest account linking.
 *
 * Implements Prompt 18 Sections 6, 16, 17, 20, 21, 22, 28 & 29.
 */
interface AuthRepository {
    val authState: StateFlow<AuthState>
    val currentSession: StateFlow<AuthSession?>

    fun isProviderConfigured(provider: AuthProvider): Boolean

    suspend fun signInWithGoogle(context: Context): AuthResult

    suspend fun signInWithFacebook(context: Context): AuthResult

    suspend fun linkGuestWithGoogle(context: Context): AuthResult

    suspend fun linkGuestWithFacebook(context: Context): AuthResult

    suspend fun restoreSession(): Boolean

    suspend fun refreshSession(): Boolean

    suspend fun signOut()
}
