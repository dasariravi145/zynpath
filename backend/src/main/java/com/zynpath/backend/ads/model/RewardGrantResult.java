package com.zynpath.backend.ads.model;

/**
 * Result of an atomic reward grant operation.
 */
public record RewardGrantResult(
    RewardGrantStatus status,
    PlayerHintCreditBalance updatedBalance,
    String failureReason
) {
    public boolean isSuccess() {
        return status == RewardGrantStatus.SUCCESS;
    }

    public static RewardGrantResult success(PlayerHintCreditBalance balance) {
        return new RewardGrantResult(RewardGrantStatus.SUCCESS, balance, null);
    }

    public static RewardGrantResult failure(RewardGrantStatus status, String reason) {
        return new RewardGrantResult(status, null, reason);
    }
}
