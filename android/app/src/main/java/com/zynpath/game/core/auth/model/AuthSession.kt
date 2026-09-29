package com.zynpath.game.core.auth.model

/**
 * Immutable authenticated session representation.
 *
 * Implements Prompt 18 Section 14 & 15:
 * - Session token is kept confidential in secure storage and not displayed in UI state.
 * - Holds authoritative backend identity attributes.
 */
data class AuthSession(
    val playerId: String,
    val publicZynpathId: String,
    val displayName: String,
    val accountType: String,
    val provider: AuthProvider,
    val expiresAt: Long
) {
    val isExpired: Boolean
        get() = System.currentTimeMillis() > expiresAt
}
