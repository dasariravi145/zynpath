package com.zynpath.backend.ads.model;

/**
 * Player hint credit balance separating standard free allowance from earned rewarded credits.
 *
 * Implements Prompt 29 Section 18:
 * - Distinguishes free daily allowance from rewarded ad hint credits.
 * - Does not overwrite standard allowance when adding rewards.
 */
public record PlayerHintCreditBalance(
    String playerId,
    int freeHintsRemaining,
    int rewardedCredits,
    int totalAvailable,
    long lastUpdatedMs
) {
    public static PlayerHintCreditBalance defaultBalance(String playerId) {
        return new PlayerHintCreditBalance(playerId, 3, 0, 3, System.currentTimeMillis());
    }

    public PlayerHintCreditBalance withAddedCredits(int credits) {
        int newRewarded = this.rewardedCredits + credits;
        return new PlayerHintCreditBalance(
            this.playerId,
            this.freeHintsRemaining,
            newRewarded,
            this.freeHintsRemaining + newRewarded,
            System.currentTimeMillis()
        );
    }
}
