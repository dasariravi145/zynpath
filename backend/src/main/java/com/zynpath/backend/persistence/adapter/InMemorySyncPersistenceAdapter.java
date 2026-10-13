package com.zynpath.backend.persistence.adapter;

import com.zynpath.backend.persistence.port.SyncPersistencePort;
import com.zynpath.backend.sync.model.SyncDto.LevelProgressDto;
import com.zynpath.backend.sync.model.SyncDto.SyncOperationResultDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory adapter for SyncPersistencePort using ConcurrentHashMap.
 * Retained for fast offline development and testing.
 */
@Repository
@ConditionalOnProperty(name = "zynpath.persistence.mode", havingValue = "in-memory", matchIfMissing = true)
public class InMemorySyncPersistenceAdapter implements SyncPersistencePort {

    private final Map<String, SyncOperationResultDto> processedOperations = new ConcurrentHashMap<>();
    private final Map<String, Map<Integer, LevelProgressDto>> playerLevelProgress = new ConcurrentHashMap<>();

    @Override
    public SyncOperationResultDto getCachedOperation(String operationId) {
        return processedOperations.get(operationId);
    }

    @Override
    public void recordOperation(String playerId, SyncOperationResultDto result, String operationType) {
        processedOperations.put(result.operationId(), result);
    }

    @Override
    public void saveOrUpdateLevelProgress(String playerId, LevelProgressDto progress) {
        Map<Integer, LevelProgressDto> progressMap = playerLevelProgress.computeIfAbsent(
                playerId,
                k -> new ConcurrentHashMap<>()
        );

        synchronized (progressMap) {
            LevelProgressDto existing = progressMap.get(progress.levelId());
            if (existing == null) {
                progressMap.put(progress.levelId(), progress);
            } else {
                boolean mergedCompleted = existing.isCompleted() || progress.isCompleted();
                int mergedStars = Math.max(existing.stars(), progress.stars());

                long mergedTime = (existing.bestTimeMs() <= 0L)
                        ? progress.bestTimeMs()
                        : (progress.bestTimeMs() <= 0L ? existing.bestTimeMs() : Math.min(existing.bestTimeMs(), progress.bestTimeMs()));

                int mergedMoves = (existing.movesCount() <= 0)
                        ? progress.movesCount()
                        : (progress.movesCount() <= 0 ? existing.movesCount() : Math.min(existing.movesCount(), progress.movesCount()));

                int mergedHints = Math.min(existing.bestHintCount(), progress.bestHintCount());

                Long mergedCompletedAt = existing.completedAt() != null
                        ? (progress.completedAt() != null ? Math.min(existing.completedAt(), progress.completedAt()) : existing.completedAt())
                        : progress.completedAt();

                progressMap.put(
                        progress.levelId(),
                        new LevelProgressDto(
                                progress.levelId(),
                                progress.worldId(),
                                mergedStars,
                                mergedTime,
                                mergedMoves,
                                mergedCompleted,
                                mergedHints,
                                mergedCompletedAt
                        )
                );
            }
        }
    }

    @Override
    public List<LevelProgressDto> getPlayerProgressList(String playerId) {
        Map<Integer, LevelProgressDto> map = playerLevelProgress.get(playerId);
        if (map == null || map.isEmpty()) {
            return Collections.emptyList();
        }
        List<LevelProgressDto> list = new ArrayList<>(map.values());
        list.sort(Comparator.comparingInt(LevelProgressDto::levelId));
        return list;
    }
}
