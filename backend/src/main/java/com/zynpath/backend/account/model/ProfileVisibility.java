package com.zynpath.backend.account.model;

/**
 * Public profile visibility tier.
 *
 * Implements Prompt 32 Section 17:
 * - PUBLIC: Profile and public stats visible to all players.
 * - FRIENDS_ONLY: Profile and stats visible only to accepted friends.
 * - PRIVATE: Profile hidden from public searches and leaderboards.
 */
public enum ProfileVisibility {
    PUBLIC,
    FRIENDS_ONLY,
    PRIVATE
}
