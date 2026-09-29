package com.zynpath.backend.security.ratelimit;

/**
 * Rate limit tiers specifying request thresholds and sliding window intervals.
 *
 * Implements Prompt 36 Section 50, 51, 52:
 * - Differentiated policies based on endpoint abuse risk.
 */
public enum RateLimitPolicy {

    /** Provider token exchange, linking, and session refresh (prevent brute force / abuse). */
    AUTH(10, 60),

    /** Friend invitations, room creation, and presence updates. */
    INVITATIONS_AND_ROOMS(20, 60),

    /** Matchmaking queue enqueue / cancel. */
    MATCHMAKING(15, 60),

    /** Solution claims, daily challenge submissions, batch sync. */
    SUBMISSIONS(30, 60),

    /** Sensitive actions: Account deletion and data export. */
    SENSITIVE(3, 300),

    /** General read/write APIs. */
    DEFAULT_API(120, 60);

    private final int maxRequests;
    private final int windowSeconds;

    RateLimitPolicy(int maxRequests, int windowSeconds) {
        this.maxRequests = maxRequests;
        this.windowSeconds = windowSeconds;
    }

    public int getMaxRequests() {
        return maxRequests;
    }

    public int getWindowSeconds() {
        return windowSeconds;
    }
}
