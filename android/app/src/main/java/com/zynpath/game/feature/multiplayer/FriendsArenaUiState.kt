package com.zynpath.game.feature.multiplayer

import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.auth.model.AuthSession
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.player.PlayerProfile

/**
 * UI State for the Friends Arena Entry & Eligibility screen.
 *
 * Implements Prompt 14/24:
 * - Total room capacity: 1–5 players (including the host).
 * - Single player room creation / waiting allowed.
 * - Match start condition: Minimum 2 connected players.
 * - Supports 4 distinct authentication & Facebook eligibility states:
 *   A. Guest player
 *   B. Google-authenticated player without Facebook connection
 *   C. Facebook-connected player
 *   D. Missing/pending Facebook configuration
 * - Strict adherence to Meta platform privacy rules (no simulated friends).
 */
data class FriendsArenaUiState(
    val authState: AuthState = AuthState.GUEST,
    val session: AuthSession? = null,
    val playerProfile: PlayerProfile? = null,
    val isFacebookConfigured: Boolean = false,
    val isGoogleConfigured: Boolean = false,
    val isReducedMotion: Boolean = false,
    val isLoading: Boolean = false,
    val userNotice: String? = null
) {
    /**
     * Whether the current user is an unauthenticated guest.
     */
    val isGuest: Boolean
        get() = session == null || authState == AuthState.GUEST || (playerProfile?.isGuest == true && session == null)

    /**
     * Whether the current session is explicitly authenticated via Facebook.
     * Google Login must NOT be treated as Facebook authorization.
     */
    val isFacebookConnected: Boolean
        get() = session != null && session.provider == AuthProvider.FACEBOOK && !session.isExpired

    /**
     * Whether the current session is authenticated via Google (without Facebook friend discovery auth).
     */
    val isGoogleAuthenticated: Boolean
        get() = session != null && session.provider == AuthProvider.GOOGLE && !session.isExpired

    /**
     * General verified session state.
     */
    val isAuthenticated: Boolean
        get() = session != null && authState == AuthState.AUTHENTICATED && !session.isExpired

    /**
     * Current display name for the active player.
     */
    val displayName: String
        get() = session?.displayName ?: playerProfile?.displayName ?: "Guest Explorer"

    /**
     * Current public identifier or short guest tag.
     */
    val publicId: String
        get() = session?.publicZynpathId
            ?: playerProfile?.publicZynpathId
            ?: (playerProfile?.shortGuestTag ?: "ZYN-GUEST")

    /**
     * Avatar illustration resource identifier.
     */
    val avatarId: String
        get() = playerProfile?.avatarId ?: "avatar_compass"

    /**
     * Authoritative room limits.
     */
    val maxRoomCapacity: Int = 5
    val minPlayersToStart: Int = 2
}
