package com.zynpath.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(
    name = "reward_events",
    indexes = {
        @Index(name = "idx_reward_player_id", columnList = "player_id"),
        @Index(name = "idx_reward_transaction_id", columnList = "transaction_id")
    }
)
public class RewardEventEntity {

    @Id
    @Column(name = "reward_event_id", length = 128, nullable = false)
    private String rewardEventId;

    @Column(name = "player_id", length = 64, nullable = false)
    private String playerId;

    @Column(name = "transaction_id", length = 128)
    private String transactionId;

    @Column(name = "reward_type", length = 32, nullable = false)
    private String rewardType;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "ad_unit_id", length = 128)
    private String adUnitId;

    @Column(name = "verification_status", length = 32, nullable = false)
    private String verificationStatus;

    @Column(name = "granted_at_ms", nullable = false)
    private long grantedAtMs;

    @Column(name = "consumed_at_ms")
    private Long consumedAtMs;

    public RewardEventEntity() {}

    public RewardEventEntity(
        String rewardEventId,
        String playerId,
        String transactionId,
        String rewardType,
        int amount,
        String adUnitId,
        String verificationStatus,
        long grantedAtMs,
        Long consumedAtMs
    ) {
        this.rewardEventId = rewardEventId;
        this.playerId = playerId;
        this.transactionId = transactionId;
        this.rewardType = rewardType;
        this.amount = amount;
        this.adUnitId = adUnitId;
        this.verificationStatus = verificationStatus;
        this.grantedAtMs = grantedAtMs;
        this.consumedAtMs = consumedAtMs;
    }

    public String getRewardEventId() { return rewardEventId; }
    public void setRewardEventId(String rewardEventId) { this.rewardEventId = rewardEventId; }

    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getRewardType() { return rewardType; }
    public void setRewardType(String rewardType) { this.rewardType = rewardType; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public String getAdUnitId() { return adUnitId; }
    public void setAdUnitId(String adUnitId) { this.adUnitId = adUnitId; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

    public long getGrantedAtMs() { return grantedAtMs; }
    public void setGrantedAtMs(long grantedAtMs) { this.grantedAtMs = grantedAtMs; }

    public Long getConsumedAtMs() { return consumedAtMs; }
    public void setConsumedAtMs(Long consumedAtMs) { this.consumedAtMs = consumedAtMs; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RewardEventEntity that = (RewardEventEntity) o;
        return Objects.equals(rewardEventId, that.rewardEventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rewardEventId);
    }
}
