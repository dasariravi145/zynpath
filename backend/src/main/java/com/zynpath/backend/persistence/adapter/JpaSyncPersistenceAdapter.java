package com.zynpath.backend.persistence.adapter;

import com.zynpath.backend.persistence.entity.PlayerLevelProgressEntity;
import com.zynpath.backend.persistence.entity.PlayerLevelProgressId;
import com.zynpath.backend.persistence.entity.SyncOperationEntity;
import com.zynpath.backend.persistence.port.SyncPersistencePort;
import com.zynpath.backend.persistence.repository.PlayerLevelProgressRepository;
import com.zynpath.backend.persistence.repository.SyncOperationRepository;
import com.zynpath.backend.sync.model.SyncDto.LevelProgressDto;
import com.zynpath.backend.sync.model.SyncDto.SyncOperationResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * PostgreSQL JPA adapter for SyncPersistencePort.
 * Stores durable Solo level progression (up to 1,800 puzzles) and client sync operation receipts.
 * Active when zynpath.persistence.mode=jpa.
 */
@Repository
@ConditionalOnProperty(name = "zynpath.persistence.mode", havingValue = "jpa")
public class JpaSyncPersistenceAdapter implements SyncPersistencePort {

    private static final Logger log = LoggerFactory.getLogger(JpaSyncPersistenceAdapter.class);

    private final PlayerLevelProgressRepository levelProgressRepository;
    private final SyncOperationRepository syncOperationRepository;

    public JpaSyncPersistenceAdapter(
            PlayerLevelProgressRepository levelProgressRepository,
            SyncOperationRepository syncOperationRepository
    ) {
        this.levelProgressRepository = levelProgressRepository;
        this.syncOperationRepository = syncOperationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public SyncOperationResultDto getCachedOperation(String operationId) {
        return syncOperationRepository.findById(operationId)
                .map(entity -> new SyncOperationResultDto(
                        entity.getOperationId(),
                        entity.getStatus(),
                        entity.getMessage(),
                        entity.getServerTimestampMs()
                ))
                .orElse(null);
    }

    @Override
    @Transactional
    public void recordOperation(String playerId, SyncOperationResultDto result, String operationType) {
        SyncOperationEntity entity = new SyncOperationEntity(
                result.operationId(),
                playerId,
                operationType != null ? operationType : "UNKNOWN",
                result.status(),
                result.message(),
                result.serverTimestamp()
        );
        syncOperationRepository.save(entity);
    }

    @Override
    @Transactional
    public void saveOrUpdateLevelProgress(String playerId, LevelProgressDto progress) {
        PlayerLevelProgressId id = new PlayerLevelProgressId(playerId, progress.levelId());
        Optional<PlayerLevelProgressEntity> existingOpt = levelProgressRepository.findById(id);

        long now = System.currentTimeMillis();

        if (existingOpt.isEmpty()) {
            PlayerLevelProgressEntity newEntity = new PlayerLevelProgressEntity(
                    playerId,
                    progress.levelId(),
                    progress.worldId(),
                    progress.stars(),
                    progress.bestTimeMs(),
                    progress.movesCount(),
                    progress.isCompleted(),
                    progress.bestHintCount(),
                    progress.completedAt(),
                    now
            );
            levelProgressRepository.save(newEntity);
            log.info("PostgreSQL JPA: Created new level progress for player={}, level={}", playerId, progress.levelId());
        } else {
            PlayerLevelProgressEntity existing = existingOpt.get();

            // Non-destructive personal-best and completion merge
            boolean mergedCompleted = existing.isCompleted() || progress.isCompleted();
            int mergedStars = Math.max(existing.getStars(), progress.stars());

            long mergedTime = (existing.getBestTimeMs() <= 0L)
                    ? progress.bestTimeMs()
                    : (progress.bestTimeMs() <= 0L ? existing.getBestTimeMs() : Math.min(existing.getBestTimeMs(), progress.bestTimeMs()));

            int mergedMoves = (existing.getMovesCount() <= 0)
                    ? progress.movesCount()
                    : (progress.movesCount() <= 0 ? existing.getMovesCount() : Math.min(existing.getMovesCount(), progress.movesCount()));

            int mergedHints = Math.min(existing.getBestHintCount(), progress.bestHintCount());

            Long mergedCompletedAt = existing.getCompletedAtMs() != null
                    ? (progress.completedAt() != null ? Math.min(existing.getCompletedAtMs(), progress.completedAt()) : existing.getCompletedAtMs())
                    : progress.completedAt();

            existing.setWorldId(progress.worldId());
            existing.setStars(mergedStars);
            existing.setBestTimeMs(mergedTime);
            existing.setMovesCount(mergedMoves);
            existing.setCompleted(mergedCompleted);
            existing.setBestHintCount(mergedHints);
            existing.setCompletedAtMs(mergedCompletedAt);
            existing.setUpdatedAtMs(now);

            levelProgressRepository.save(existing);
            log.info("PostgreSQL JPA: Merged level progress for player={}, level={}", playerId, progress.levelId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<LevelProgressDto> getPlayerProgressList(String playerId) {
        List<PlayerLevelProgressEntity> list = levelProgressRepository.findByPlayerIdOrderByLevelIdAsc(playerId);
        return list.stream().map(this::toDto).toList();
    }

    private LevelProgressDto toDto(PlayerLevelProgressEntity entity) {
        return new LevelProgressDto(
                entity.getLevelId(),
                entity.getWorldId(),
                entity.getStars(),
                entity.getBestTimeMs(),
                entity.getMovesCount(),
                entity.isCompleted(),
                entity.getBestHintCount(),
                entity.getCompletedAtMs()
        );
    }
}
