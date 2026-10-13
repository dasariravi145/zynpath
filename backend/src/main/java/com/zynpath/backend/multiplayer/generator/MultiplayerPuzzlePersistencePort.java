package com.zynpath.backend.multiplayer.generator;

import com.zynpath.backend.multiplayer.model.GameMode;

import java.util.List;
import java.util.Optional;

/**
 * Persistence port for multiplayer generated puzzles, inventory buffers, and assignment history.
 *
 * Implements Hexagonal architecture pattern:
 * - In-Memory adapter for dev and local testing (zynpath.persistence.mode=in-memory)
 * - JPA adapter for production PostgreSQL (zynpath.persistence.mode=jpa)
 */
public interface MultiplayerPuzzlePersistencePort {

    void saveGeneratedPuzzle(GeneratedPuzzle puzzle);

    Optional<GeneratedPuzzle> findPuzzleById(String puzzleId);

    Optional<GeneratedPuzzle> findPuzzleByFingerprint(String fingerprint);

    List<GeneratedPuzzle> findAvailablePuzzles(GameMode mode, CompetitiveDifficultyTier tier, int limit);

    void recordAssignment(
            String assignmentId,
            String puzzleId,
            String matchId,
            GameMode mode,
            CompetitiveDifficultyTier tier,
            List<String> participantIds
    );

    List<String> getRecentPuzzleFingerprintsForPlayer(String playerId, int limit);

    long countAvailablePuzzles(GameMode mode, CompetitiveDifficultyTier tier);
}
