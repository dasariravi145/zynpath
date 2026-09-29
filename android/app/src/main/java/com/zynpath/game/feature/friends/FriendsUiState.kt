package com.zynpath.game.feature.friends

import com.zynpath.game.core.social.model.FriendItem
import com.zynpath.game.core.social.model.FriendRequestItem
import com.zynpath.game.core.social.model.PublicPlayerProfile
import com.zynpath.game.core.social.model.SentFriendRequestItem

enum class FriendsTab {
    FRIENDS,
    REQUESTS,
    FIND_PLAYER
}

/**
 * UI State for the interactive Friends screen.
 *
 * Implements Prompt 19 Sections 6, 19-23:
 * - Clear authentication status (guest vs signed in)
 * - Exact-ID player search results
 * - Accepted friends list with presence badges
 * - Incoming and outgoing request queues
 * - Tab selection and user feedback messages
 */
data class FriendsUiState(
    val isAuthenticated: Boolean = false,
    val myPublicId: String? = null,
    val myDisplayName: String? = null,
    val myAvatarId: String? = null,
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val searchResult: PublicPlayerProfile? = null,
    val searchError: String? = null,
    val friends: List<FriendItem> = emptyList(),
    val incomingRequests: List<FriendRequestItem> = emptyList(),
    val outgoingRequests: List<SentFriendRequestItem> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: String? = null,
    val selectedTab: FriendsTab = FriendsTab.FRIENDS,
    val pendingDeepLinkInviterId: String? = null
)
