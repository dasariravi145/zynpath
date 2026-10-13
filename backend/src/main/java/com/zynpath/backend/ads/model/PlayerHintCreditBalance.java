package com.zynpath.backend.ads.model;

/**
 * Player hint credit balance separating standard free allowance from earned rewarded credits.
 *
 * Implements Prompt 29 Section 18 & Step 17.22 Economy Hardening:
 * - freeHintsRemaining: Default baseline allowance.
 * - rewardedCredits: Cumulative lifetime count of server-verified rewarded hints awarded to this account.
 *   Because hint consumption is managed locally/offline-first by the client per level (supporting offline gameplay),
 *   rewardedCredits is not decremented by the server and must not be treated as a spendable wallet balance.
 * - totalAvailable: freeHintsRemaining + rewardedCredits (calculated with overflow-safe arithmetic).
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
        int newRewarded = (this.rewardedCredits > Integer.MAX_VALUE - credits)
            ? Integer.MAX_VALUE
            : this.rewardedCredits + credits;
        int newTotal = (this.freeHintsRemaining > Integer.MAX_VALUE - newRewarded)
            ? Integer.MAX_VALUE
            : this.freeHintsRemaining + newRewarded;
        return new PlayerHintCreditBalance(
            this.playerId,
            this.freeHintsRemaining,
            newRewarded,
            newTotal,
            System.currentTimeMillis()
        );
    }
}
