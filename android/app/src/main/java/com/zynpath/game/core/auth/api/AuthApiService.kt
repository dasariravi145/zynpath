package com.zynpath.game.core.auth.api

import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.network.NetworkResult

/**
 * Contract for remote authentication and account linking API calls.
 *
 * Implements Prompt 18 Section 26 & 27.
 */
interface AuthApiService {
    fun getBaseUrl(): String

    suspend fun exchangeToken(
        provider: AuthProvider,
        providerToken: String,
        guestUuid: String?
    ): NetworkResult<AuthResponseDto>

    suspend fun linkAccount(
        provider: AuthProvider,
        providerToken: String,
        guestUuid: String,
        displayName: String?
    ): NetworkResult<AuthResponseDto>

    suspend fun getCurrentPlayer(sessionToken: String): NetworkResult<AuthResponseDto>

    suspend fun refreshSession(sessionToken: String): NetworkResult<AuthResponseDto>

    suspend fun signOut(sessionToken: String): NetworkResult<Unit>
}
