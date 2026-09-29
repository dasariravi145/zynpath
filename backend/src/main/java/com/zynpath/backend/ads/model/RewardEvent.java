package com.zynpath.backend.ads.model;

/**
 * Immutable record representing an individual ad reward event.
 *
 * Implements Prompt 29 Section 30:
 * - Tracks unique event ID, transaction ID, verification status, and timestamps.
 * - Idempotent: Repeated submissions of the same transactionId are rejected.
 */
public record RewardEvent(
    String rewardEventId,
    String playerId,
    String transactionId,
    RewardType rewardType,
    int amount,
    String adUnitId,
    RewardVerificationStatus verificationStatus,
    long grantedAtMs,
    Long consumedAtMs
) {
    public boolean isGranted() {
        return verificationStatus == RewardVerificationStatus.SERVER_VERIFIED ||
               verificationStatus == RewardVerificationStatus.LOCAL_CONFIRMED;
    }
}
