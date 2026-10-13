package com.zynpath.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.util.Objects;

/**
 * Authoritative JPA entity for durable player session persistence.
 * Maps to table: player_sessions
 */
@Entity
@Table(
        name = "player_sessions",
        indexes = {
                @Index(name = "idx_player_sessions_token_hash", columnList = "token_hash", unique = true),
                @Index(name = "idx_player_sessions_player_id", columnList = "player_id"),
                @Index(name = "idx_player_sessions_expires_at", columnList = "expires_at"),
                @Index(name = "idx_player_sessions_is_revoked", columnList = "is_revoked")
        }
)
public class PlayerSessionEntity {

    @Id
    @Column(name = "session_id", length = 64, nullable = false)
    private String sessionId;

    @Column(name = "token_hash", length = 64, nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "player_id", length = 64, nullable = false)
    private String playerId;

    @Column(name = "created_at", nullable = false)
    private long createdAt;

    @Column(name = "expires_at", nullable = false)
    private long expiresAt;

    @Column(name = "last_used_at", nullable = false)
    private long lastUsedAt;

    @Column(name = "is_revoked", nullable = false)
    private boolean isRevoked;

    @Column(name = "revoked_at")
    private Long revokedAt;

    @Column(name = "replaced_by_token_hash", length = 64)
    private String replacedByTokenHash;

    public PlayerSessionEntity() {
    }

    public PlayerSessionEntity(
            String sessionId,
            String tokenHash,
            String playerId,
            long createdAt,
            long expiresAt,
            long lastUsedAt,
            boolean isRevoked,
            Long revokedAt,
            String replacedByTokenHash
    ) {
        this.sessionId = sessionId;
        this.tokenHash = tokenHash;
        this.playerId = playerId;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.lastUsedAt = lastUsedAt;
        this.isRevoked = isRevoked;
        this.revokedAt = revokedAt;
        this.replacedByTokenHash = replacedByTokenHash;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public long getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(long lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public boolean isRevoked() {
        return isRevoked;
    }

    public void setRevoked(boolean revoked) {
        isRevoked = revoked;
    }

    public Long getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(Long revokedAt) {
        this.revokedAt = revokedAt;
    }

    public String getReplacedByTokenHash() {
        return replacedByTokenHash;
    }

    public void setReplacedByTokenHash(String replacedByTokenHash) {
        this.replacedByTokenHash = replacedByTokenHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerSessionEntity that = (PlayerSessionEntity) o;
        return Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId);
    }
}
