package com.zynpath.backend.ads.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Objects for the Ad Reward API.
 *
 * Implements Prompt 29 Section 47.
 */
public final class RewardDto {

    private RewardDto() {}

    public record VerifyRewardRequest(
        @NotBlank(message = "rewardEventId must not be blank")
        String rewardEventId,

        String transactionId,

        String adUnitId,

        @Min(value = 1, message = "amount must be at least 1")
        int amount,

        @NotNull(message = "rewardType must not be null")
        RewardType rewardType
    ) {}

    public record VerifyRewardResponse(
        RewardVerificationStatus status,
        boolean granted,
        String rewardEventId,
        int newRewardedCredits,
        int totalAvailableHints,
        String message
    ) {}

    public record PlayerHintBalanceResponse(
        int freeHintsRemaining,
        int rewardedCredits,
        int totalAvailable,
        boolean isPremium,
        int dailyRewardedAdsRemaining
    ) {}

    public record AdMobSsvCallbackParams(
        String adNetwork,
        String adUnit,
        String customData,
        String rewardAmount,
        String rewardItem,
        String timestamp,
        String transactionId,
        String userId,
        String signature,
        String keyId
    ) {}
}
