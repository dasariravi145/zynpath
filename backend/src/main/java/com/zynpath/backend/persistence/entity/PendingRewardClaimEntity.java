package com.zynpath.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(
    name = "pending_reward_claims",
    indexes = {
        @Index(name = "idx_pending_claim_player", columnList = "player_id"),
        @Index(name = "idx_pending_claim_status", columnList = "status"),
        @Index(name = "idx_pending_claim_txn", columnList = "transaction_id")
    }
)
public class PendingRewardClaimEntity {

    @Id
    @Column(name = "claim_id", length = 128, nullable = false)
    private String claimId;

    @Column(name = "player_id", length = 64, nullable = false)
    private String playerId;

    @Column(name = "ad_unit_id", length = 128, nullable = false)
    private String adUnitId;

    @Column(name = "level_id")
    private Integer levelId;

    @Column(name = "reward_type", length = 32, nullable = false)
    private String rewardType;

    @Column(name = "status", length = 32, nullable = false)
    private String status;

    @Column(name = "transaction_id", length = 128)
    private String transactionId;

    @Column(name = "created_at_ms", nullable = false)
    private long createdAtMs;

    @Column(name = "verified_at_ms")
    private Long verifiedAtMs;

    @Column(name = "consumed_at_ms")
    private Long consumedAtMs;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    public PendingRewardClaimEntity() {}

    public PendingRewardClaimEntity(
        String claimId,
        String playerId,
        String adUnitId,
        Integer levelId,
        String rewardType,
        String status,
        String transactionId,
        long createdAtMs,
        Long verifiedAtMs,
        Long consumedAtMs,
        String failureReason
    ) {
        this.claimId = claimId;
        this.playerId = playerId;
        this.adUnitId = adUnitId;
        this.levelId = levelId;
        this.rewardType = rewardType;
        this.status = status;
        this.transactionId = transactionId;
        this.createdAtMs = createdAtMs;
        this.verifiedAtMs = verifiedAtMs;
        this.consumedAtMs = consumedAtMs;
        this.failureReason = failureReason;
    }

    public String getClaimId() { return claimId; }
    public void setClaimId(String claimId) { this.claimId = claimId; }

    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }

    public String getAdUnitId() { return adUnitId; }
    public void setAdUnitId(String adUnitId) { this.adUnitId = adUnitId; }

    public Integer getLevelId() { return levelId; }
    public void setLevelId(Integer levelId) { this.levelId = levelId; }

    public String getRewardType() { return rewardType; }
    public void setRewardType(String rewardType) { this.rewardType = rewardType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public long getCreatedAtMs() { return createdAtMs; }
    public void setCreatedAtMs(long createdAtMs) { this.createdAtMs = createdAtMs; }

    public Long getVerifiedAtMs() { return verifiedAtMs; }
    public void setVerifiedAtMs(Long verifiedAtMs) { this.verifiedAtMs = verifiedAtMs; }

    public Long getConsumedAtMs() { return consumedAtMs; }
    public void setConsumedAtMs(Long consumedAtMs) { this.consumedAtMs = consumedAtMs; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PendingRewardClaimEntity that = (PendingRewardClaimEntity) o;
        return Objects.equals(claimId, that.claimId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(claimId);
    }
}
