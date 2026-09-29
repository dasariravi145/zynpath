package com.zynpath.game.core.auth.model

/**
 * Explicit application authentication states.
 *
 * Implements Prompt 18 Section 16:
 * - GUEST: Unauthenticated guest using local progress
 * - SIGNING_IN: Provider sign-in in progress
 * - AUTHENTICATED: Backend verified online session
 * - SESSION_EXPIRED: Prior session expired or revoked
 * - LINKING: Guest account linking in progress
 * - LINK_FAILED: Account linking failed; guest progress intact
 * - SIGN_IN_FAILED: Provider sign-in or verification failed
 */
enum class AuthState {
    GUEST,
    SIGNING_IN,
    AUTHENTICATED,
    SESSION_EXPIRED,
    LINKING,
    LINK_FAILED,
    SIGN_IN_FAILED
}
