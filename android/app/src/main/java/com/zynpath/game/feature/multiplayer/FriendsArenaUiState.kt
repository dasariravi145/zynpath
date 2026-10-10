package com.zynpath.game.feature.multiplayer

import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.auth.model.AuthSession
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.player.PlayerProfile

import com.zynpath.game.core.social.model.FriendItem

/**
 * UI State for Friends Arena Entry & Compact Popups.
 *
 * Implements Google-Only Authentication & Compact Popup Flow:
 * - Total room capacity: 1–5 players (including the host).
 * - Single player room creation / waiting allowed (host can wait alone).
 * - Match start condition: Minimum 2 connected players.
 * - Google authentication gate for Friends Arena.
 * - Compact popup states: Connect Google, Create Room, Join Room, Friend Picker.
 */
data class FriendsArenaUiState(
    val authState: AuthState = AuthState.GUEST,
    val session: AuthSession? = null,
    val playerProfile: PlayerProfile? = null,
    val isFacebookConfigured: Boolean = false,
    val isGoogleConfigured: Boolean = false,
    val isReducedMotion: Boolean = false,
    val isLoading: Boolean = false,
    val userNotice: String? = null,
    val showGoogleConnectModal: Boolean = false,
    val isGoogleConnecting: Boolean = false,
    val googleConnectError: String? = null,
    val showCreateRoomModal: Boolean = false,
    val isCreatingRoom: Boolean = false,
    val createRoomError: String? = null,
    val selectedPlayerCount: Int = 3,
    val selectedFriendIds: Set<String> = emptySet(),
    val selectedPuzzleDescription: String = "Random Expert",
    val showFriendPickerModal: Boolean = false,
    val showJoinRoomModal: Boolean = false,
    val isJoiningRoom: Boolean = false,
    val joinRoomCode: String = "",
    val joinRoomError: String? = null,
    val eligibleFriends: List<FriendItem> = emptyList()
) {
    /**
     * Whether the current user is an unauthenticated guest.
     */
    val isGuest: Boolean
        get() = session == null || authState == AuthState.GUEST

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
