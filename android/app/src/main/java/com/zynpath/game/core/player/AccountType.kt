package com.zynpath.game.core.player

/**
 * Account types supporting guest-first play and future authentication states.
 *
 * Implements Prompt 17 Section 27.
 */
enum class AccountType {
    GUEST,
    LINKING,
    LINKED,
    LINK_FAILED;

    companion object {
        fun fromString(value: String): AccountType {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: GUEST
        }
    }
}
