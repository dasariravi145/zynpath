package com.zynpath.game.feature.multiplayer

import com.zynpath.game.core.auth.model.AuthSession
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.player.PlayerProfile

/**
 * Supported error types for Friends Arena room joining.
 *
 * Implements Prompt 16 Task 3:
 * - INVALID_CODE: Code format malformed or empty
 * - ROOM_NOT_FOUND: Code does not match any active server room
 * - ROOM_FULL: Room has reached maximum capacity (5 players)
 * - ROOM_CLOSED: Room has been terminated or expired
 * - UNAUTHORIZED: Guest or unauthenticated player
 * - CONNECTION_FAILURE: Network disruption
 * - ALREADY_JOINED: Player is already a member of this room
 * - SERVICE_PENDING: Backend room service scheduled for upcoming prompts
 */
enum class FriendsArenaJoinErrorType {
    INVALID_CODE,
    ROOM_NOT_FOUND,
    ROOM_FULL,
    ROOM_CLOSED,
    UNAUTHORIZED,
    CONNECTION_FAILURE,
    ALREADY_JOINED,
    SERVICE_PENDING
}

/**
 * Structured join failure model with user-facing message.
 */
data class FriendsArenaJoinError(
    val type: FriendsArenaJoinErrorType,
    val message: String
)

/**
 * Typed internal invitation destination and payload model.
 *
 * Implements Prompt 16 Task 4:
 * - Does not invent arbitrary public production domains.
 * - Encapsulates validated room code and invitation origin.
 */
data class FriendsArenaInvitationPayload(
    val roomCode: String,
    val source: String? = null,
    val inviterDisplayName: String? = null,
    val receivedAt: Long = System.currentTimeMillis()
)

/**
 * UI State for the Friends Arena Join Room Screen (Prompt 16/24).
 */
data class FriendsArenaJoinUiState(
    val roomCodeInput: String = "",
    val authState: AuthState = AuthState.GUEST,
    val session: AuthSession? = null,
    val playerProfile: PlayerProfile? = null,
    val isJoining: Boolean = false,
    val pendingInvitation: FriendsArenaInvitationPayload? = null,
    val joinError: FriendsArenaJoinError? = null,
    val userNoticeTitle: String? = null,
    val userNoticeDetails: String? = null,
    val isReducedMotion: Boolean = false
) {
    /**
     * Whether the active user is an unauthenticated guest.
     */
    val isGuest: Boolean
        get() = session == null || authState == AuthState.GUEST || (playerProfile?.isGuest == true && session == null)

    /**
     * Display name for the active player.
     */
    val displayName: String
        get() = session?.displayName ?: playerProfile?.displayName ?: "Guest Explorer"

    /**
     * Public Zynpath identifier.
     */
    val publicId: String
        get() = session?.publicZynpathId ?: playerProfile?.publicZynpathId ?: (playerProfile?.shortGuestTag ?: "ZYN-GUEST")

    /**
     * Avatar resource identifier.
     */
    val avatarId: String
        get() = playerProfile?.avatarId ?: "avatar_compass"

    /**
     * Cleaned uppercase code without whitespace.
     */
    val cleanedCode: String
        get() = roomCodeInput.trim().uppercase()

    /**
     * Standard room-code format validation.
     * Code must be nonblank, 4–8 alphanumeric characters (standard is 6 characters).
     */
    val isCodeValid: Boolean
        get() = cleanedCode.length in 4..8 && cleanedCode.all { it.isLetterOrDigit() }

    /**
     * Exact 6-character match conforming to standard Zynpath room-code length.
     */
    val isExactStandardLength: Boolean
        get() = cleanedCode.length == 6

    /**
     * Whether the Join Room button can be submitted.
     * Requires valid non-empty code, not currently joining, and authenticated account.
     */
    val canSubmit: Boolean
        get() = !isJoining && !isGuest && isCodeValid

    /**
     * Whether an invitation payload is currently pending sign-in resolution.
     */
    val hasPendingInvitation: Boolean
        get() = pendingInvitation != null
}
