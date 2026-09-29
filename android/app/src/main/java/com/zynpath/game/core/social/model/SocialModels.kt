package com.zynpath.game.core.social.model

/**
 * High-level presence states for Zynpath players.
 *
 * Implements Prompt 19 Section 29:
 * - ONLINE: Connected and active.
 * - AWAY: Connected or recently seen within lease window, but backgrounded.
 * - OFFLINE: Explicitly disconnected or lease expired.
 * - UNKNOWN: Fallback when status cannot be established reliably or caller is not permitted to see it.
 */
enum class PlayerPresenceState {
    ONLINE,
    AWAY,
    OFFLINE,
    UNKNOWN
}

/**
 * Relationship state between the current authenticated player and another player.
 *
 * Implements Prompt 19 Section 11.
 */
enum class FriendRelationshipStatus {
    NONE,
    OUTGOING_REQUEST,
    INCOMING_REQUEST,
    FRIENDS,
    BLOCKED,
    SELF
}

/**
 * Safe public profile returned from discovery and search.
 * Omits private account identifiers, email addresses, and auth tokens.
 *
 * Implements Prompt 19 Sections 7-10.
 */
data class PublicPlayerProfile(
    val publicZynpathId: String,
    val displayName: String,
    val avatarId: String,
    val relationshipStatus: FriendRelationshipStatus,
    val presenceState: PlayerPresenceState
)

/**
 * Accepted mutual friend representation.
 *
 * Implements Prompt 19 Section 19.
 */
data class FriendItem(
    val playerId: String,
    val publicZynpathId: String,
    val displayName: String,
    val avatarId: String,
    val presenceState: PlayerPresenceState,
    val friendsSince: Long
) {
    val friendPlayerId: String get() = playerId
}

/**
 * Pending incoming friend request awaiting accept or reject.
 *
 * Implements Prompt 19 Section 20.
 */
data class FriendRequestItem(
    val requestId: String,
    val senderPlayerId: String,
    val senderPublicZynpathId: String,
    val senderDisplayName: String,
    val senderAvatarId: String,
    val createdAt: Long
)

/**
 * Pending outgoing friend request awaiting recipient response or sender cancellation.
 *
 * Implements Prompt 19 Section 21.
 */
data class SentFriendRequestItem(
    val requestId: String,
    val recipientPlayerId: String,
    val recipientPublicZynpathId: String,
    val recipientDisplayName: String,
    val createdAt: Long
)

/**
 * Result outcome of a social action (send, accept, reject, cancel, remove, block).
 */
sealed interface SocialOperationResult {
    data class Success(val message: String) : SocialOperationResult
    data class Error(val code: String, val message: String) : SocialOperationResult
}
