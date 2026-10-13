package com.zynpath.backend.ads.model;

/**
 * Command containing verified parameters for an atomic rewarded hint credit grant.
 */
public record RewardGrantCommand(
    String playerId,
    String claimId,
    String transactionId,
    String adUnitId,
    Integer levelId,
    String keyId,
    String signature,
    String customData,
    String rawQueryString,
    int maxDailyRewards,
    int maxStoredCredits,
    int hintsToGrant,
    long timestampMs
) {}
