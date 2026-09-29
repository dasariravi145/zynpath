package com.zynpath.game.core.account.model

enum class ProfileVisibility {
    PUBLIC,
    FRIENDS_ONLY,
    PRIVATE;

    companion object {
        fun fromString(value: String): ProfileVisibility {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: PUBLIC
        }
    }
}
