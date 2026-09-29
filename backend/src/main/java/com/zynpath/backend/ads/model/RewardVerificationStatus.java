package com.zynpath.backend.ads.model;

/**
 * Verification state of an ad reward event.
 *
 * Implements Prompt 29 Section 31:
 * - LOCAL_CONFIRMED: Client-confirmed guest or offline reward within bounded limits.
 * - SERVER_VERIFIED: Server-authoritative or cryptographically verified reward.
 * - PENDING_VERIFICATION: Submitted for server-side verification, awaiting completion.
 * - REJECTED: Failed verification, duplicate transaction, or abuse limit exceeded.
 */
public enum RewardVerificationStatus {
    LOCAL_CONFIRMED,
    SERVER_VERIFIED,
    PENDING_VERIFICATION,
    REJECTED
}
