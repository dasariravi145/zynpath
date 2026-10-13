package com.zynpath.backend.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for player level progress: (playerId, levelId).
 */
public class PlayerLevelProgressId implements Serializable {

    private String playerId;
    private int levelId;

    public PlayerLevelProgressId() {
    }

    public PlayerLevelProgressId(String playerId, int levelId) {
        this.playerId = playerId;
        this.levelId = levelId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerLevelProgressId that = (PlayerLevelProgressId) o;
        return levelId == that.levelId && Objects.equals(playerId, that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, levelId);
    }
}
