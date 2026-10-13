package com.zynpath.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.util.Objects;

/**
 * Authoritative JPA entity for solo puzzle level progress.
 * Maps to table: player_level_progress
 */
@Entity
@Table(
        name = "player_level_progress",
        indexes = {
                @Index(name = "idx_progress_player", columnList = "player_id"),
                @Index(name = "idx_progress_world", columnList = "player_id, world_id"),
                @Index(name = "idx_progress_level", columnList = "level_id")
        }
)
@IdClass(PlayerLevelProgressId.class)
public class PlayerLevelProgressEntity {

    @Id
    @Column(name = "player_id", length = 64, nullable = false)
    private String playerId;

    @Id
    @Column(name = "level_id", nullable = false)
    private int levelId;

    @Column(name = "world_id", nullable = false)
    private int worldId;

    @Column(name = "stars", nullable = false)
    private int stars;

    @Column(name = "best_time_ms", nullable = false)
    private long bestTimeMs;

    @Column(name = "moves_count", nullable = false)
    private int movesCount;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted;

    @Column(name = "best_hint_count", nullable = false)
    private int bestHintCount;

    @Column(name = "completed_at_ms")
    private Long completedAtMs;

    @Column(name = "updated_at_ms", nullable = false)
    private long updatedAtMs;

    public PlayerLevelProgressEntity() {
    }

    public PlayerLevelProgressEntity(
            String playerId,
            int levelId,
            int worldId,
            int stars,
            long bestTimeMs,
            int movesCount,
            boolean isCompleted,
            int bestHintCount,
            Long completedAtMs,
            long updatedAtMs
    ) {
        this.playerId = playerId;
        this.levelId = levelId;
        this.worldId = worldId;
        this.stars = stars;
        this.bestTimeMs = bestTimeMs;
        this.movesCount = movesCount;
        this.isCompleted = isCompleted;
        this.bestHintCount = bestHintCount;
        this.completedAtMs = completedAtMs;
        this.updatedAtMs = updatedAtMs;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public int getWorldId() {
        return worldId;
    }

    public void setWorldId(int worldId) {
        this.worldId = worldId;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public long getBestTimeMs() {
        return bestTimeMs;
    }

    public void setBestTimeMs(long bestTimeMs) {
        this.bestTimeMs = bestTimeMs;
    }

    public int getMovesCount() {
        return movesCount;
    }

    public void setMovesCount(int movesCount) {
        this.movesCount = movesCount;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public int getBestHintCount() {
        return bestHintCount;
    }

    public void setBestHintCount(int bestHintCount) {
        this.bestHintCount = bestHintCount;
    }

    public Long getCompletedAtMs() {
        return completedAtMs;
    }

    public void setCompletedAtMs(Long completedAtMs) {
        this.completedAtMs = completedAtMs;
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
        PlayerLevelProgressEntity that = (PlayerLevelProgressEntity) o;
        return levelId == that.levelId && Objects.equals(playerId, that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, levelId);
    }
}
