package com.zynpath.backend.persistence.adapter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynpath.backend.multiplayer.generator.CompetitiveDifficultyTier;
import com.zynpath.backend.multiplayer.generator.GeneratedPuzzle;
import com.zynpath.backend.multiplayer.generator.MultiplayerPuzzlePersistencePort;
import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.model.PuzzleAssignment;
import com.zynpath.backend.persistence.entity.GeneratedPuzzleJpaEntity;
import com.zynpath.backend.persistence.repository.GeneratedPuzzleJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * PostgreSQL JPA persistence adapter for MultiplayerPuzzlePersistencePort.
 * Active when zynpath.persistence.mode=jpa.
 */
@Repository
@ConditionalOnProperty(name = "zynpath.persistence.mode", havingValue = "jpa")
public class JpaMultiplayerPuzzlePersistenceAdapter implements MultiplayerPuzzlePersistencePort {

    private static final Logger log = LoggerFactory.getLogger(JpaMultiplayerPuzzlePersistenceAdapter.class);

    private final GeneratedPuzzleJpaRepository puzzleRepository;
    private final com.zynpath.backend.persistence.repository.MultiplayerPuzzleAssignmentJpaRepository assignmentRepository;
    private final com.zynpath.backend.persistence.repository.MultiplayerPlayerPuzzleHistoryJpaRepository historyRepository;
    private final ObjectMapper objectMapper;

    public JpaMultiplayerPuzzlePersistenceAdapter(
            GeneratedPuzzleJpaRepository puzzleRepository,
            com.zynpath.backend.persistence.repository.MultiplayerPuzzleAssignmentJpaRepository assignmentRepository,
            com.zynpath.backend.persistence.repository.MultiplayerPlayerPuzzleHistoryJpaRepository historyRepository,
            ObjectMapper objectMapper
    ) {
        this.puzzleRepository = puzzleRepository;
        this.assignmentRepository = assignmentRepository;
        this.historyRepository = historyRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void saveGeneratedPuzzle(GeneratedPuzzle puzzle) {
        if (puzzle == null) return;
        try {
            String reqCellsJson = objectMapper.writeValueAsString(puzzle.requiredCells());
            String cpsJson = objectMapper.writeValueAsString(puzzle.checkpoints());
            String wallsJson = objectMapper.writeValueAsString(puzzle.blockedEdges());
            String solJson = objectMapper.writeValueAsString(puzzle.canonicalSolution());

            GeneratedPuzzleJpaEntity entity = new GeneratedPuzzleJpaEntity(
                    puzzle.puzzleId(),
                    puzzle.generatorVersion(),
                    puzzle.fingerprint(),
                    puzzle.difficultyTier().name(),
                    puzzle.gameMode().name(),
                    puzzle.gridRows(),
                    puzzle.gridCols(),
                    reqCellsJson,
                    cpsJson,
                    wallsJson,
                    solJson,
                    puzzle.cognitiveScore(),
                    true,
                    "AVAILABLE",
                    System.currentTimeMillis()
            );

            puzzleRepository.save(entity);
            log.info("Persisted dynamic puzzle to PostgreSQL: id={}, tier={}, mode={}, fp={}",
                    puzzle.puzzleId(), puzzle.difficultyTier(), puzzle.gameMode(), puzzle.fingerprint());
        } catch (Exception e) {
            log.error("Failed to serialize and save GeneratedPuzzle: id={}", puzzle.puzzleId(), e);
            throw new RuntimeException("Database error saving generated puzzle", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GeneratedPuzzle> findPuzzleById(String puzzleId) {
        return puzzleRepository.findById(puzzleId).map(this::toDomainPuzzle);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GeneratedPuzzle> findPuzzleByFingerprint(String fingerprint) {
        return puzzleRepository.findByFingerprint(fingerprint).map(this::toDomainPuzzle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GeneratedPuzzle> findAvailablePuzzles(GameMode mode, CompetitiveDifficultyTier tier, int limit) {
        List<GeneratedPuzzleJpaEntity> entities = puzzleRepository.findAvailablePuzzles(
                mode.name(), tier.name(), PageRequest.of(0, limit)
        );
        List<GeneratedPuzzle> result = new ArrayList<>(entities.size());
        for (GeneratedPuzzleJpaEntity entity : entities) {
            GeneratedPuzzle p = toDomainPuzzle(entity);
            if (p != null) {
                result.add(p);
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    @Transactional
    public void recordAssignment(
            String assignmentId,
            String puzzleId,
            String matchId,
            GameMode mode,
            CompetitiveDifficultyTier tier,
            List<String> participantIds
    ) {
        long now = System.currentTimeMillis();
        // 1. Update puzzle inventory status if present
        GeneratedPuzzleJpaEntity puzzleEntity = puzzleRepository.findById(puzzleId).orElse(null);
        if (puzzleEntity != null) {
            puzzleEntity.setInventoryStatus("ASSIGNED");
            puzzleRepository.save(puzzleEntity);
        }

        // 2. Persist match-to-puzzle assignment record
        try {
            String pidsJson = (participantIds != null) ? objectMapper.writeValueAsString(participantIds) : "[]";
            com.zynpath.backend.persistence.entity.MultiplayerPuzzleAssignmentJpaEntity assignmentEntity =
                    new com.zynpath.backend.persistence.entity.MultiplayerPuzzleAssignmentJpaEntity(
                            assignmentId,
                            puzzleId,
                            matchId,
                            mode.name(),
                            tier.name(),
                            now,
                            pidsJson
                    );
            assignmentRepository.save(assignmentEntity);
        } catch (Exception e) {
            log.error("Failed to record assignment in database: assignmentId={}, matchId={}", assignmentId, matchId, e);
        }

        // 3. Persist player recent history for repeat suppression
        if (participantIds != null && !participantIds.isEmpty()) {
            String fp = (puzzleEntity != null) ? puzzleEntity.getFingerprint() : puzzleId;
            for (String pid : participantIds) {
                com.zynpath.backend.persistence.entity.MultiplayerPlayerPuzzleHistoryJpaEntity hist =
                        new com.zynpath.backend.persistence.entity.MultiplayerPlayerPuzzleHistoryJpaEntity(
                                pid,
                                puzzleId,
                                fp,
                                mode.name(),
                                now
                        );
                historyRepository.save(hist);
            }
        }

        log.info("Recorded puzzle assignment in JPA: matchId={}, puzzleId={}, mode={}, tier={}",
                matchId, puzzleId, mode, tier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getRecentPuzzleFingerprintsForPlayer(String playerId, int limit) {
        if (playerId == null || playerId.isBlank()) {
            return List.of();
        }
        return historyRepository.findRecentFingerprints(playerId, PageRequest.of(0, limit));
    }

    @Override
    @Transactional(readOnly = true)
    public long countAvailablePuzzles(GameMode mode, CompetitiveDifficultyTier tier) {
        return puzzleRepository.countAvailablePuzzles(mode.name(), tier.name());
    }

    private GeneratedPuzzle toDomainPuzzle(GeneratedPuzzleJpaEntity entity) {
        try {
            List<String> reqCells = objectMapper.readValue(entity.getRequiredCellsJson(), new TypeReference<>() {});
            List<PuzzleAssignment.CheckpointSpec> cps = objectMapper.readValue(entity.getCheckpointsJson(), new TypeReference<>() {});
            List<PuzzleAssignment.BlockedEdgeSpec> walls = objectMapper.readValue(entity.getBlockedEdgesJson(), new TypeReference<>() {});
            List<String> sol = objectMapper.readValue(entity.getCanonicalSolutionJson(), new TypeReference<>() {});

            CompetitiveDifficultyTier tier = CompetitiveDifficultyTier.fromString(entity.getDifficultyTier());
            GameMode mode = GameMode.valueOf(entity.getGameMode());

            return new GeneratedPuzzle(
                    entity.getPuzzleId(),
                    entity.getGeneratorVersion(),
                    entity.getFingerprint(),
                    tier,
                    mode,
                    entity.getGridRows(),
                    entity.getGridCols(),
                    reqCells,
                    cps,
                    walls,
                    sol,
                    entity.getCognitiveScore(),
                    null,
                    0L,
                    0L
            );
        } catch (Exception e) {
            log.error("Failed to deserialize GeneratedPuzzleJpaEntity: id={}", entity.getPuzzleId(), e);
            return null;
        }
    }
}
