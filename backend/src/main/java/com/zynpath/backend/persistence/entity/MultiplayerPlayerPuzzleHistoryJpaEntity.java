package com.zynpath.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Authoritative JPA entity for player recent dynamic puzzle history.
 * Maps to table: multiplayer_player_puzzle_history (V10 migration)
 */
@Entity
@Table(name = "multiplayer_player_puzzle_history")
public class MultiplayerPlayerPuzzleHistoryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "player_id", length = 64, nullable = false)
    private String playerId;

    @Column(name = "puzzle_id", length = 64, nullable = false)
    private String puzzleId;

    @Column(name = "fingerprint", length = 128, nullable = false)
    private String fingerprint;

    @Column(name = "game_mode", length = 32, nullable = false)
    private String gameMode;

    @Column(name = "assigned_at", nullable = false)
    private long assignedAt;

    public MultiplayerPlayerPuzzleHistoryJpaEntity() {
    }

    public MultiplayerPlayerPuzzleHistoryJpaEntity(
            String playerId,
            String puzzleId,
            String fingerprint,
            String gameMode,
            long assignedAt
    ) {
        this.playerId = playerId;
        this.puzzleId = puzzleId;
        this.fingerprint = fingerprint;
        this.gameMode = gameMode;
        this.assignedAt = assignedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPuzzleId() {
        return puzzleId;
    }

    public void setPuzzleId(String puzzleId) {
        this.puzzleId = puzzleId;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public long getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(long assignedAt) {
        this.assignedAt = assignedAt;
    }
}
