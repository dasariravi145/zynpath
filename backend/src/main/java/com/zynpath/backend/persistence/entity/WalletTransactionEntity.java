package com.zynpath.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.util.Objects;

/**
 * Authoritative immutable audit ledger record for currency debit/credit operations.
 * Maps to table: wallet_transactions
 */
@Entity
@Table(
        name = "wallet_transactions",
        indexes = {
                @Index(name = "idx_wallet_tx_player_id", columnList = "player_id"),
                @Index(name = "idx_wallet_tx_created", columnList = "created_at_ms"),
                @Index(name = "idx_wallet_tx_idempotency", columnList = "idempotency_key", unique = true)
        }
)
public class WalletTransactionEntity {

    @Id
    @Column(name = "transaction_id", length = 64, nullable = false)
    private String transactionId;

    @Column(name = "player_id", length = 64, nullable = false)
    private String playerId;

    @Column(name = "type", length = 32, nullable = false)
    private String type;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "balance_after", nullable = false)
    private int balanceAfter;

    @Column(name = "idempotency_key", length = 128, nullable = false, unique = true)
    private String idempotencyKey;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    @Column(name = "created_at_ms", nullable = false)
    private long createdAtMs;

    public WalletTransactionEntity() {
    }

    public WalletTransactionEntity(
            String transactionId,
            String playerId,
            String type,
            int amount,
            int balanceAfter,
            String idempotencyKey,
            String metadataJson,
            long createdAtMs
    ) {
        this.transactionId = transactionId;
        this.playerId = playerId;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.idempotencyKey = idempotencyKey;
        this.metadataJson = metadataJson;
        this.createdAtMs = createdAtMs;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(int balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }

    public long getCreatedAtMs() {
        return createdAtMs;
    }

    public void setCreatedAtMs(long createdAtMs) {
        this.createdAtMs = createdAtMs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WalletTransactionEntity that = (WalletTransactionEntity) o;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }
}
