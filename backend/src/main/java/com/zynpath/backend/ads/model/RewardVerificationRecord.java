package com.zynpath.backend.ads.model;

/**
 * Record of Google AdMob Server-Side Verification (SSV) callback validation.
 *
 * Implements Prompt 29 Section 27:
 * - Records SSV callback transactions, signatures, custom data, and verification timestamps.
 */
public record RewardVerificationRecord(
    String transactionId,
    String keyId,
    String signature,
    String customData,
    String userId,
    String claimId,
    String adUnitId,
    int rewardAmount,
    String rawQueryString,
    long verifiedAtMs,
    boolean isValid,
    String failureReason
) {
    public RewardVerificationRecord(
        String transactionId,
        String keyId,
        String signature,
        String customData,
        String userId,
        long verifiedAtMs,
        boolean isValid,
        String failureReason
    ) {
        this(transactionId, keyId, signature, customData, userId, null, null, 1, null, verifiedAtMs, isValid, failureReason);
    }
}
