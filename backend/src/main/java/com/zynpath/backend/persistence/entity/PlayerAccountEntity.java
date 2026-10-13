package com.zynpath.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.util.Objects;

/**
 * Authoritative JPA entity for core player account identity.
 * Maps to table: player_accounts
 */
@Entity
@Table(
        name = "player_accounts",
        indexes = {
                @Index(name = "idx_player_accounts_public_id", columnList = "public_zynpath_id", unique = true),
                @Index(name = "idx_player_accounts_created_at", columnList = "created_at")
        }
)
public class PlayerAccountEntity {

    @Id
    @Column(name = "player_id", length = 64, nullable = false)
    private String playerId;

    @Column(name = "public_zynpath_id", length = 32, nullable = false, unique = true)
    private String publicZynpathId;

    @Column(name = "display_name", length = 64, nullable = false)
    private String displayName;

    @Column(name = "account_type", length = 32, nullable = false)
    private String accountType;

    @Column(name = "created_at", nullable = false)
    private long createdAt;

    @Column(name = "last_login_at", nullable = false)
    private long lastLoginAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    public PlayerAccountEntity() {
    }

    public PlayerAccountEntity(
            String playerId,
            String publicZynpathId,
            String displayName,
            String accountType,
            long createdAt,
            long lastLoginAt,
            boolean isDeleted
    ) {
        this.playerId = playerId;
        this.publicZynpathId = publicZynpathId;
        this.displayName = displayName;
        this.accountType = accountType;
        this.createdAt = createdAt;
        this.lastLoginAt = lastLoginAt;
        this.isDeleted = isDeleted;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPublicZynpathId() {
        return publicZynpathId;
    }

    public void setPublicZynpathId(String publicZynpathId) {
        this.publicZynpathId = publicZynpathId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(long lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerAccountEntity that = (PlayerAccountEntity) o;
        return Objects.equals(playerId, that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }
}
