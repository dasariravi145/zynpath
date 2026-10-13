package com.zynpath.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "player_hint_balances")
public class PlayerHintBalanceEntity {

    @Id
    @Column(name = "player_id", length = 64, nullable = false)
    private String playerId;

    @Column(name = "free_hints_remaining", nullable = false)
    private int freeHintsRemaining;

    @Column(name = "rewarded_credits", nullable = false)
    private int rewardedCredits;

    @Column(name = "total_available", nullable = false)
    private int totalAvailable;

    @Column(name = "last_updated_ms", nullable = false)
    private long lastUpdatedMs;

    public PlayerHintBalanceEntity() {}

    public PlayerHintBalanceEntity(String playerId, int freeHintsRemaining, int rewardedCredits, int totalAvailable, long lastUpdatedMs) {
        this.playerId = playerId;
        this.freeHintsRemaining = freeHintsRemaining;
        this.rewardedCredits = rewardedCredits;
        this.totalAvailable = totalAvailable;
        this.lastUpdatedMs = lastUpdatedMs;
    }

    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }

    public int getFreeHintsRemaining() { return freeHintsRemaining; }
    public void setFreeHintsRemaining(int freeHintsRemaining) { this.freeHintsRemaining = freeHintsRemaining; }

    public int getRewardedCredits() { return rewardedCredits; }
    public void setRewardedCredits(int rewardedCredits) { this.rewardedCredits = rewardedCredits; }

    public int getTotalAvailable() { return totalAvailable; }
    public void setTotalAvailable(int totalAvailable) { this.totalAvailable = totalAvailable; }

    public long getLastUpdatedMs() { return lastUpdatedMs; }
    public void setLastUpdatedMs(long lastUpdatedMs) { this.lastUpdatedMs = lastUpdatedMs; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerHintBalanceEntity that = (PlayerHintBalanceEntity) o;
        return Objects.equals(playerId, that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }
}
