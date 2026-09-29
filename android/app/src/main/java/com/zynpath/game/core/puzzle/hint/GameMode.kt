package com.zynpath.game.core.puzzle.hint

/**
 * Enumeration of distinct gameplay game modes in Zynpath.
 *
 * Implements Prompt 14 Section 24: Competitive Fairness.
 * Solution-revealing hints are strictly disabled in competitive/ranked modes
 * (Quick Duel, Friend Duel, Mini League) to prevent unfair competitive advantage.
 */
enum class GameMode {
    SOLO,
    DAILY_CHALLENGE,
    QUICK_DUEL,
    FRIEND_DUEL,
    MINI_LEAGUE;

    /**
     * True if hints are permitted in this mode.
     * Competitive and ranked modes (Daily Challenge, Quick Duel, Friend Duel, Mini League)
     * strictly return false per Prompt 16 Section 20.
     */
    val allowsHints: Boolean
        get() = this == SOLO
}
