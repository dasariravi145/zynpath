package com.zynpath.backend.ads.model;

/**
 * Supported reward types earned via optional rewarded advertising.
 *
 * Implements Prompt 29 Section 6:
 * - Rewarded ads grant SOLO hints only.
 * - Never grants competitive advantages, time extensions, or leaderboard boosts.
 */
public enum RewardType {
    SOLO_HINT
}
