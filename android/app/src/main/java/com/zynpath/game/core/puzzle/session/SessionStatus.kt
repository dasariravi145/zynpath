package com.zynpath.game.core.puzzle.session

/**
 * Authoritative lifecycle status of a gameplay session.
 *
 * Implements Prompt 13 Section 8:
 * - Distinguishes session status from pure engine completion state.
 */
enum class SessionStatus {
    NOT_STARTED,
    ACTIVE,
    PAUSED,
    COMPLETED,
    ABANDONED,
    RESTORATION_FAILED;

    companion object {
        fun fromString(value: String): SessionStatus {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: PAUSED
        }
    }
}
