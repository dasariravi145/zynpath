package com.zynpath.game.core.analytics.model

/**
 * Categorization of data sources supporting the Zynpath analytics engine.
 *
 * Implements Prompt 30 Section 10:
 * - Distinguishes on-device local progress from backend-verified competitive data.
 * - Prevents unverified local records from masquerading as authoritative results.
 */
enum class DataSourceType(val displayName: String, val isServerAuthoritative: Boolean) {
    /**
     * Canonical single-player puzzle completions on-device (Worlds 1–6).
     */
    LOCAL_SOLO("Local Solo Progress", false),

    /**
     * Local participation and completion records for Daily Challenges.
     */
    LOCAL_DAILY("Local Daily Challenge", false),

    /**
     * Server-verified Daily Challenge completions with authoritative timestamping.
     */
    SERVER_VERIFIED_DAILY("Server-Verified Daily Challenge", true),

    /**
     * Server-authoritative multiplayer match results (Quick Duel, Friend Duel, Mini League).
     */
    SERVER_COMPETITIVE("Server Competitive Matches", true)
}
