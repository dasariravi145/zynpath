package com.zynpath.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Authoritative JPA entity for multiplayer generated puzzles.
 * Maps to table: multiplayer_generated_puzzles
 */
@Entity
@Table(name = "multiplayer_generated_puzzles")
public class GeneratedPuzzleJpaEntity {

    @Id
    @Column(name = "puzzle_id", length = 64, nullable = false)
    private String puzzleId;

    @Column(name = "generator_version", nullable = false)
    private int generatorVersion;

    @Column(name = "fingerprint", length = 128, nullable = false, unique = true)
    private String fingerprint;

    @Column(name = "difficulty_tier", length = 32, nullable = false)
    private String difficultyTier;

    @Column(name = "game_mode", length = 32, nullable = false)
    private String gameMode;

    @Column(name = "grid_rows", nullable = false)
    private int gridRows;

    @Column(name = "grid_cols", nullable = false)
    private int gridCols;

    @Column(name = "required_cells_json", columnDefinition = "TEXT", nullable = false)
    private String requiredCellsJson;

    @Column(name = "checkpoints_json", columnDefinition = "TEXT", nullable = false)
    private String checkpointsJson;

    @Column(name = "blocked_edges_json", columnDefinition = "TEXT", nullable = false)
    private String blockedEdgesJson;

    @Column(name = "canonical_solution_json", columnDefinition = "TEXT", nullable = false)
    private String canonicalSolutionJson;

    @Column(name = "cognitive_score", nullable = false)
    private double cognitiveScore;

    @Column(name = "is_validated", nullable = false)
    private boolean validated;

    @Column(name = "inventory_status", length = 32, nullable = false)
    private String inventoryStatus;

    @Column(name = "created_at", nullable = false)
    private long createdAt;

    public GeneratedPuzzleJpaEntity() {
    }

    public GeneratedPuzzleJpaEntity(
            String puzzleId,
            int generatorVersion,
            String fingerprint,
            String difficultyTier,
            String gameMode,
            int gridRows,
            int gridCols,
            String requiredCellsJson,
            String checkpointsJson,
            String blockedEdgesJson,
            String canonicalSolutionJson,
            double cognitiveScore,
            boolean validated,
            String inventoryStatus,
            long createdAt
    ) {
        this.puzzleId = puzzleId;
        this.generatorVersion = generatorVersion;
        this.fingerprint = fingerprint;
        this.difficultyTier = difficultyTier;
        this.gameMode = gameMode;
        this.gridRows = gridRows;
        this.gridCols = gridCols;
        this.requiredCellsJson = requiredCellsJson;
        this.checkpointsJson = checkpointsJson;
        this.blockedEdgesJson = blockedEdgesJson;
        this.canonicalSolutionJson = canonicalSolutionJson;
        this.cognitiveScore = cognitiveScore;
        this.validated = validated;
        this.inventoryStatus = inventoryStatus;
        this.createdAt = createdAt;
    }

    public String getPuzzleId() {
        return puzzleId;
    }

    public void setPuzzleId(String puzzleId) {
        this.puzzleId = puzzleId;
    }

    public int getGeneratorVersion() {
        return generatorVersion;
    }

    public void setGeneratorVersion(int generatorVersion) {
        this.generatorVersion = generatorVersion;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getDifficultyTier() {
        return difficultyTier;
    }

    public void setDifficultyTier(String difficultyTier) {
        this.difficultyTier = difficultyTier;
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public int getGridRows() {
        return gridRows;
    }

    public void setGridRows(int gridRows) {
        this.gridRows = gridRows;
    }

    public int getGridCols() {
        return gridCols;
    }

    public void setGridCols(int gridCols) {
        this.gridCols = gridCols;
    }

    public String getRequiredCellsJson() {
        return requiredCellsJson;
    }

    public void setRequiredCellsJson(String requiredCellsJson) {
        this.requiredCellsJson = requiredCellsJson;
    }

    public String getCheckpointsJson() {
        return checkpointsJson;
    }

    public void setCheckpointsJson(String checkpointsJson) {
        this.checkpointsJson = checkpointsJson;
    }

    public String getBlockedEdgesJson() {
        return blockedEdgesJson;
    }

    public void setBlockedEdgesJson(String blockedEdgesJson) {
        this.blockedEdgesJson = blockedEdgesJson;
    }

    public String getCanonicalSolutionJson() {
        return canonicalSolutionJson;
    }

    public void setCanonicalSolutionJson(String canonicalSolutionJson) {
        this.canonicalSolutionJson = canonicalSolutionJson;
    }

    public double getCognitiveScore() {
        return cognitiveScore;
    }

    public void setCognitiveScore(double cognitiveScore) {
        this.cognitiveScore = cognitiveScore;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public String getInventoryStatus() {
        return inventoryStatus;
    }

    public void setInventoryStatus(String inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
