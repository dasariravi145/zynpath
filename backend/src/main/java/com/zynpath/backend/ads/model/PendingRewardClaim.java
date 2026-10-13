package com.zynpath.backend.ads.model;

/**
 * Model representing a pending or processed rewarded ad reward claim.
 * Enables exact matching between client reward requests and asynchronous Google AdMob SSV callbacks.
 */
public record PendingRewardClaim(
    String claimId,
    String playerId,
    String adUnitId,
    Integer levelId,
    String rewardType,
    RewardVerificationStatus status,
    String transactionId,
    long createdAtMs,
    Long verifiedAtMs,
    Long consumedAtMs,
    String failureReason
) {
    public boolean isVerified() {
        return status == RewardVerificationStatus.SERVER_VERIFIED;
    }

    public boolean isPending() {
        return status == RewardVerificationStatus.PENDING_VERIFICATION;
    }

    public PendingRewardClaim withVerified(String txnId, long verifiedAt) {
        return new PendingRewardClaim(
            this.claimId,
            this.playerId,
            this.adUnitId,
            this.levelId,
            this.rewardType,
            RewardVerificationStatus.SERVER_VERIFIED,
            txnId,
            this.createdAtMs,
            verifiedAt,
            this.consumedAtMs,
            null
        );
    }

    public PendingRewardClaim withRejected(String reason) {
        return new PendingRewardClaim(
            this.claimId,
            this.playerId,
            this.adUnitId,
            this.levelId,
            this.rewardType,
            RewardVerificationStatus.REJECTED,
            this.transactionId,
            this.createdAtMs,
            this.verifiedAtMs,
            this.consumedAtMs,
            reason
        );
    }
}
