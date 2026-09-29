package com.zynpath.backend.subscription.model;

/**
 * Authoritative subscription entitlement statuses.
 * Implements Prompt 26 Sections 23, 24 & 34.
 */
public enum EntitlementStatus {
    ACTIVE,
    IN_GRACE_PERIOD,
    ON_HOLD,
    PAUSED,
    CANCELLED_BUT_ACTIVE,
    EXPIRED,
    REVOKED,
    FREE,
    UNKNOWN;

    /**
     * Returns true if the status grants active premium access.
     * Note: CANCELLED_BUT_ACTIVE still retains paid access until period end.
     */
    public boolean isEntitled() {
        return this == ACTIVE || this == IN_GRACE_PERIOD || this == CANCELLED_BUT_ACTIVE;
    }
}
