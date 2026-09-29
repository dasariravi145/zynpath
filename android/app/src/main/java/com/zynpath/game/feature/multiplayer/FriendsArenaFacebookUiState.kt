package com.zynpath.game.feature.multiplayer

import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.auth.model.AuthSession
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.player.PlayerProfile

/**
 * Authoritative invitation states for Facebook friends.
 *
 * Implements Prompt 17 Task 7:
 * - NOT_INVITED: Available for invitation.
 * - SENDING: Dispatching invitation payload.
 * - INVITED: Dispatched successfully through backend.
 * - ACCEPTED: Friend confirmed room entry.
 * - DECLINED: Friend declined invitation.
 * - EXPIRED: Room or invitation lease window expired.
 * - FAILED: Delivery error or unreachable.
 */
enum class FacebookInvitationStatus {
    NOT_INVITED,
    SENDING,
    INVITED,
    ACCEPTED,
    DECLINED,
    EXPIRED,
    FAILED
}

/**
 * Eligible discoverable friend model complying with Meta's platform restrictions.
 *
 * Implements Prompt 17 Task 4 & Task 5:
 * - Uses stable provider/app-scoped identifiers (ASID).
 * - Only displays real data returned from an authorized integration.
 * - Omits unauthorized provider tokens, private emails, fake online statuses, or invented ranks.
 */
data class FacebookFriendItem(
    val appScopedId: String,
    val displayName: String,
    val pictureUrl: String? = null,
    val invitationStatus: FacebookInvitationStatus = FacebookInvitationStatus.NOT_INVITED
)

/**
 * 4 distinct authentication & Facebook connection eligibility states.
 *
 * Implements Prompt 17 Task 3:
 * - GUEST: Unauthenticated guest; sign-in prompt displayed, progress preserved.
 * - GOOGLE_WITHOUT_FACEBOOK: Google authenticated session; Facebook connection required for friend discovery.
 * - FACEBOOK_CONNECTED: Facebook account authorized and verified.
 * - NOT_CONFIGURED: Missing developer keys or client configuration.
 */
enum class FacebookFriendsEligibility {
    GUEST,
    GOOGLE_WITHOUT_FACEBOOK,
    FACEBOOK_CONNECTED,
    NOT_CONFIGURED
}

/**
 * Complete UI state for the Facebook Friends Discovery & Invitation screen (Prompt 17/24).
 */
data class FacebookFriendsUiState(
    val roomId: String? = null,
    val roomCode: String? = null,
    val currentOccupancy: Int = 1,
    val maxRoomCapacity: Int = 5,
    val authState: AuthState = AuthState.GUEST,
    val session: AuthSession? = null,
    val playerProfile: PlayerProfile? = null,
    val isFacebookConfigured: Boolean = false,
    val eligibleFriends: List<FacebookFriendItem> = emptyList(),
    val searchQuery: String = "",
    val selectedFriendIds: Set<String> = emptySet(),
    val isSendingInvitations: Boolean = false,
    val isLoading: Boolean = false,
    val userNoticeTitle: String? = null,
    val userNoticeDetails: String? = null,
    val isReducedMotion: Boolean = false
) {
    /**
     * Authoritative eligibility classification.
     */
    val eligibility: FacebookFriendsEligibility
        get() = when {
            session == null || authState == AuthState.GUEST || (playerProfile?.isGuest == true && session == null) -> {
                FacebookFriendsEligibility.GUEST
            }
            !isFacebookConfigured -> {
                FacebookFriendsEligibility.NOT_CONFIGURED
            }
            session.provider == AuthProvider.FACEBOOK && !session.isExpired -> {
                FacebookFriendsEligibility.FACEBOOK_CONNECTED
            }
            session.provider == AuthProvider.GOOGLE && !session.isExpired -> {
                FacebookFriendsEligibility.GOOGLE_WITHOUT_FACEBOOK
            }
            else -> {
                FacebookFriendsEligibility.GUEST
            }
        }

    /**
     * Whether the active player is an unauthenticated guest.
     */
    val isGuest: Boolean
        get() = eligibility == FacebookFriendsEligibility.GUEST

    /**
     * Available invitation capacity strictly calculated from authoritative room state (Task 5).
     * Room rules: Max 5 total players, including host.
     * Example: If host is alone in room, capacity is 5 - 1 = 4 additional players.
     */
    val availableCapacity: Int
        get() = (maxRoomCapacity - currentOccupancy).coerceIn(0, 4)

    /**
     * Number of currently selected friends.
     */
    val selectedCount: Int
        get() = selectedFriendIds.size

    /**
     * Whether more friends can be selected without exceeding room capacity.
     */
    val canSelectMore: Boolean
        get() = selectedCount < availableCapacity

    /**
     * Whether the INVITE SELECTED action can be executed.
     */
    val canSubmitInvitations: Boolean
        get() = selectedCount in 1..availableCapacity &&
                !isSendingInvitations &&
                eligibility == FacebookFriendsEligibility.FACEBOOK_CONNECTED

    /**
     * Filtered list of eligible friends matching the search query.
     */
    val filteredFriends: List<FacebookFriendItem>
        get() {
            val query = searchQuery.trim().lowercase()
            return if (query.isBlank()) {
                eligibleFriends
            } else {
                eligibleFriends.filter { it.displayName.lowercase().contains(query) }
            }
        }
}
