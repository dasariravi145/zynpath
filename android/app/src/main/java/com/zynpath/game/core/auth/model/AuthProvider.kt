package com.zynpath.game.core.auth.model

/**
 * Supported external identity providers.
 *
 * Implements Prompt 18 Section 7.
 */
enum class AuthProvider(val displayName: String) {
    GOOGLE("Google"),
    FACEBOOK("Facebook")
}
