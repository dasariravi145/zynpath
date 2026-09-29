package com.zynpath.game.core.player

/**
 * Immutable player profile domain model.
 *
 * Implements Prompt 17 Section 9:
 * - Internal player ID (stable local UUID)
 * - Display name
 * - Avatar selection
 * - Creation and activity timestamps
 * - Account type
 * - Optional future public Zynpath ID
 */
data class PlayerProfile(
    val playerId: String,
    val displayName: String,
    val avatarId: String,
    val createdAt: Long,
    val lastActiveAt: Long,
    val accountType: AccountType = AccountType.GUEST,
    val publicZynpathId: String? = null
) {
    val isGuest: Boolean
        get() = accountType == AccountType.GUEST

    val formattedPublicId: String
        get() = publicZynpathId ?: "Unassigned (Offline Guest)"

    val shortGuestTag: String
        get() = if (playerId.length >= 8) {
            "ZYN-" + playerId.substring(0, 4).uppercase()
        } else {
            "ZYN-GUEST"
        }
}
