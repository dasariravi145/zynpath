package com.zynpath.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Authoritative JPA entity for multiplayer match puzzle assignments.
 * Maps to table: multiplayer_puzzle_assignments (V10 migration)
 */
@Entity
@Table(name = "multiplayer_puzzle_assignments")
public class MultiplayerPuzzleAssignmentJpaEntity {

    @Id
    @Column(name = "assignment_id", length = 64, nullable = false)
    private String assignmentId;

    @Column(name = "puzzle_id", length = 64, nullable = false)
    private String puzzleId;

    @Column(name = "match_id", length = 64, nullable = false, unique = true)
    private String matchId;

    @Column(name = "game_mode", length = 32, nullable = false)
    private String gameMode;

    @Column(name = "difficulty_tier", length = 32, nullable = false)
    private String difficultyTier;

    @Column(name = "assigned_at", nullable = false)
    private long assignedAt;

    @Column(name = "participant_ids_json", columnDefinition = "TEXT")
    private String participantIdsJson;

    public MultiplayerPuzzleAssignmentJpaEntity() {
    }

    public MultiplayerPuzzleAssignmentJpaEntity(
            String assignmentId,
            String puzzleId,
            String matchId,
            String gameMode,
            String difficultyTier,
            long assignedAt,
            String participantIdsJson
    ) {
        this.assignmentId = assignmentId;
        this.puzzleId = puzzleId;
        this.matchId = matchId;
        this.gameMode = gameMode;
        this.difficultyTier = difficultyTier;
        this.assignedAt = assignedAt;
        this.participantIdsJson = participantIdsJson;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getPuzzleId() {
        return puzzleId;
    }

    public void setPuzzleId(String puzzleId) {
        this.puzzleId = puzzleId;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public String getDifficultyTier() {
        return difficultyTier;
    }

    public void setDifficultyTier(String difficultyTier) {
        this.difficultyTier = difficultyTier;
    }

    public long getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(long assignedAt) {
        this.assignedAt = assignedAt;
    }

    public String getParticipantIdsJson() {
        return participantIdsJson;
    }

    public void setParticipantIdsJson(String participantIdsJson) {
        this.participantIdsJson = participantIdsJson;
    }
}
