package com.zynpath.game.core.auth.api

/**
 * Data transfer objects for backend authentication exchange and linking endpoints.
 *
 * Implements Prompt 18 Section 26 & 27.
 */
data class AuthExchangeRequestDto(
    val provider: String,
    val providerToken: String,
    val guestUuid: String?
)

data class AuthLinkRequestDto(
    val provider: String,
    val providerToken: String,
    val guestUuid: String,
    val displayName: String?
)

data class AuthResponseDto(
    val sessionToken: String,
    val playerId: String,
    val publicZynpathId: String,
    val displayName: String,
    val accountType: String,
    val expiresAt: Long
)

data class AuthErrorDto(
    val errorCode: String,
    val message: String,
    val timestamp: Long
)
