package com.zynpath.backend.ads.model;

/**
 * Record of Google AdMob Server-Side Verification (SSV) callback validation.
 *
 * Implements Prompt 29 Section 27:
 * - Records SSV callback transactions, signatures, and verification timestamps.
 */
public record RewardVerificationRecord(
    String transactionId,
    String keyId,
    String signature,
    String customData,
    String userId,
    long verifiedAtMs,
    boolean isValid,
    String failureReason
) {}
