package com.zynpath.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

/**
 * Authoritative JPA entity for player wallet balances.
 * Maps to table: player_wallet_balances
 */
@Entity
@Table(name = "player_wallet_balances")
public class PlayerWalletBalanceEntity {

    @Id
    @Column(name = "player_id", length = 64, nullable = false)
    private String playerId;

    @Column(name = "coin_balance", nullable = false)
    private int coinBalance;

    @Column(name = "total_stars", nullable = false)
    private int totalStars;

    @Column(name = "updated_at_ms", nullable = false)
    private long updatedAtMs;

    public PlayerWalletBalanceEntity() {
    }

    public PlayerWalletBalanceEntity(String playerId, int coinBalance, int totalStars, long updatedAtMs) {
        this.playerId = playerId;
        this.coinBalance = coinBalance;
        this.totalStars = totalStars;
        this.updatedAtMs = updatedAtMs;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getCoinBalance() {
        return coinBalance;
    }

    public void setCoinBalance(int coinBalance) {
        this.coinBalance = coinBalance;
    }

    public int getTotalStars() {
        return totalStars;
    }

    public void setTotalStars(int totalStars) {
        this.totalStars = totalStars;
    }

    public long getUpdatedAtMs() {
        return updatedAtMs;
    }

    public void setUpdatedAtMs(long updatedAtMs) {
        this.updatedAtMs = updatedAtMs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerWalletBalanceEntity that = (PlayerWalletBalanceEntity) o;
        return Objects.equals(playerId, that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }
}
