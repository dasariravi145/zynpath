package com.zynpath.game.core.player

/**
 * State representing account linking boundaries for future Google/Facebook integration.
 *
 * Implements Prompt 17 Section 27-31.
 */
data class AccountLinkingState(
    val accountType: AccountType = AccountType.GUEST,
    val provider: String? = null,
    val publicZynpathId: String? = null,
    val isLinked: Boolean = false,
    val statusMessage: String = "Guest Account (Local Offline)"
)
