package com.zynpath.backend.sync.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyProvisionalSyncRequest;
import com.zynpath.backend.daily.service.DailyChallengeService;
import com.zynpath.backend.sync.model.SyncDto.BatchSyncRequest;
import com.zynpath.backend.sync.model.SyncDto.BatchSyncResponse;
import com.zynpath.backend.sync.model.SyncDto.LevelProgressDto;
import com.zynpath.backend.sync.model.SyncDto.SyncOperationDto;
import com.zynpath.backend.sync.model.SyncDto.SyncOperationResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service orchestrating offline-first synchronization, idempotent reconciliation,
 * and conflict resolution across game progress domains.
 *
 * Implements Prompt 35 Sections 6, 7, 11, 12, 20, 21, 22, 29, 39, 51, 54 & 56:
 * - Idempotent operation deduplication using stable client operation IDs.
 * - Non-destructive Solo progress merging (completions never lost, personal best preserved).
 * - Offline Daily Challenge reconciliation into provisional participation without corrupting competitive leaderboards.
 * - Partial batch tolerance ensuring individual operations succeed or fail independently.
 */
@Service
public class SyncService {

    private static final Logger log = LoggerFactory.getLogger(SyncService.class);

    private final DailyChallengeService dailyChallengeService;
    private final ObjectMapper objectMapper;

    // Idempotent operation cache: operationId -> previous result
    private final Map<String, SyncOperationResultDto> processedOperations = new ConcurrentHashMap<>();

    // Authoritative remote level progress: playerId -> (levelId -> LevelProgressDto)
    private final Map<String, Map<Integer, LevelProgressDto>> playerLevelProgress = new ConcurrentHashMap<>();

    public SyncService(
            DailyChallengeService dailyChallengeService,
            ObjectMapper objectMapper
    ) {
        this.dailyChallengeService = dailyChallengeService;
        this.objectMapper = objectMapper;
    }

    /**
     * Process an incoming batch of synchronization operations idempotently.
     */
    public BatchSyncResponse processBatch(String authenticatedPlayerId, BatchSyncRequest request) {
        if (request == null || request.operations() == null) {
            return new BatchSyncResponse(Collections.emptyList(), Collections.emptyList(), System.currentTimeMillis());
        }

        String targetPlayerId = (authenticatedPlayerId != null && !authenticatedPlayerId.isBlank())
                ? authenticatedPlayerId
                : (request.playerId() != null ? request.playerId() : "guest");

        List<SyncOperationResultDto> operationResults = new ArrayList<>();

        for (SyncOperationDto op : request.operations()) {
            SyncOperationResultDto result = processSingleOperation(targetPlayerId, op);
            operationResults.add(result);
        }

        List<LevelProgressDto> currentProgress = getPlayerProgressList(targetPlayerId);

        return new BatchSyncResponse(
                operationResults,
                currentProgress,
                System.currentTimeMillis()
        );
    }

    private SyncOperationResultDto processSingleOperation(String playerId, SyncOperationDto op) {
        if (op.operationId() == null || op.operationId().isBlank()) {
            return new SyncOperationResultDto(
                    "unknown",
                    "REJECTED",
                    "Missing operationId",
                    System.currentTimeMillis()
            );
        }

        // Section 11: Idempotency check. Repeated delivery of previously acknowledged operation
        SyncOperationResultDto cached = processedOperations.get(op.operationId());
        if (cached != null) {
            log.info("Idempotent deduplication for operationId={}", op.operationId());
            return new SyncOperationResultDto(
                    op.operationId(),
                    "IGNORED_DUPLICATE",
                    "Operation previously processed and acknowledged",
                    cached.serverTimestamp()
            );
        }

        long now = System.currentTimeMillis();

        try {
            switch (op.operationType()) {
                case "SOLO_LEVEL_COMPLETION" -> {
                    reconcileSoloCompletion(playerId, op);
                    SyncOperationResultDto success = new SyncOperationResultDto(op.operationId(), "SUCCESS", null, now);
                    processedOperations.put(op.operationId(), success);
                    return success;
                }
                case "DAILY_CHALLENGE_SUBMISSION" -> {
                    reconcileDailyChallenge(playerId, op);
                    SyncOperationResultDto success = new SyncOperationResultDto(op.operationId(), "SUCCESS", null, now);
                    processedOperations.put(op.operationId(), success);
                    return success;
                }
                case "PREFERENCE_UPDATE" -> {
                    // Preferences acknowledged
                    SyncOperationResultDto success = new SyncOperationResultDto(op.operationId(), "SUCCESS", null, now);
                    processedOperations.put(op.operationId(), success);
                    return success;
                }
                default -> {
                    log.warn("Unknown operationType={} for operationId={}", op.operationType(), op.operationId());
                    return new SyncOperationResultDto(
                            op.operationId(),
                            "REJECTED",
                            "Unsupported operation type: " + op.operationType(),
                            now
                    );
                }
            }
        } catch (Exception e) {
            log.error("Failed to process operationId=" + op.operationId(), e);
            return new SyncOperationResultDto(
                    op.operationId(),
                    "REJECTED",
                    "Error processing payload: " + e.getMessage(),
                    now
            );
        }
    }

    /**
     * Reconcile Solo level completion (Prompt 35 Sections 20, 21, 22).
     */
    private void reconcileSoloCompletion(String playerId, SyncOperationDto op) throws Exception {
        JsonNode node = objectMapper.readTree(op.payloadJson());
        int levelId = node.path("levelId").asInt();
        int worldId = node.path("worldId").asInt();
        int stars = node.path("stars").asInt();
        long bestTimeMs = node.path("bestTimeMs").asLong();
        int movesCount = node.path("movesCount").asInt();
        boolean isCompleted = node.path("isCompleted").asBoolean(true);
        int bestHintCount = node.path("bestHintCount").asInt();
        Long completedAt = node.has("completedAt") && !node.get("completedAt").isNull()
                ? node.get("completedAt").asLong()
                : System.currentTimeMillis();

        // Validate canonical level boundaries (Prompt 35 Section 23: 300 levels, 6 worlds)
        if (levelId < 1 || levelId > 300) {
            throw new IllegalArgumentException("Level ID " + levelId + " out of canonical range 1..300");
        }

        Map<Integer, LevelProgressDto> progressMap = playerLevelProgress.computeIfAbsent(
                playerId,
                k -> new ConcurrentHashMap<>()
        );

        synchronized (progressMap) {
            LevelProgressDto existing = progressMap.get(levelId);
            if (existing == null) {
                progressMap.put(
                        levelId,
                        new LevelProgressDto(
                                levelId,
                                worldId,
                                stars,
                                bestTimeMs,
                                movesCount,
                                isCompleted,
                                bestHintCount,
                                completedAt
                        )
                );
            } else {
                // Non-destructive personal-best and completion merge
                boolean mergedCompleted = existing.isCompleted() || isCompleted;
                int mergedStars = Math.max(existing.stars(), stars);

                // Best time: non-zero minimum
                long mergedTime = (existing.bestTimeMs() <= 0L)
                        ? bestTimeMs
                        : (bestTimeMs <= 0L ? existing.bestTimeMs() : Math.min(existing.bestTimeMs(), bestTimeMs));

                // Moves count: non-zero minimum
                int mergedMoves = (existing.movesCount() <= 0)
                        ? movesCount
                        : (movesCount <= 0 ? existing.movesCount() : Math.min(existing.movesCount(), movesCount));

                // Best hint count: minimum hints used
                int mergedHints = Math.min(existing.bestHintCount(), bestHintCount);

                // Preserve earliest completion timestamp
                Long mergedCompletedAt = existing.completedAt() != null
                        ? (completedAt != null ? Math.min(existing.completedAt(), completedAt) : existing.completedAt())
                        : completedAt;

                progressMap.put(
                        levelId,
                        new LevelProgressDto(
                                levelId,
                                worldId,
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

    /**
     * Reconcile Daily Challenge offline participation (Prompt 35 Sections 28, 29, 30).
     */
    private void reconcileDailyChallenge(String playerId, SyncOperationDto op) throws Exception {
        JsonNode node = objectMapper.readTree(op.payloadJson());
        String challengeId = node.path("challengeId").asText("");
        String dateKey = node.path("dateKey").asText("");
        long solveTimeMs = node.path("solveTimeMs").asLong();
        long completedAt = node.path("completedAt").asLong(System.currentTimeMillis());
        int movesCount = node.path("movesCount").asInt();
        String fingerprint = node.path("fingerprint").asText("");

        List<String> pathCoords = new ArrayList<>();
        if (node.has("pathCoordinates") && node.get("pathCoordinates").isArray()) {
            for (JsonNode coord : node.get("pathCoordinates")) {
                pathCoords.add(coord.asText());
            }
        }

        DailyProvisionalSyncRequest provRequest = new DailyProvisionalSyncRequest(
                challengeId,
                dateKey,
                fingerprint,
                solveTimeMs,
                completedAt,
                pathCoords,
                movesCount
        );

        dailyChallengeService.syncProvisional(playerId, provRequest);
    }

    /**
     * Fetch authoritative level progress for a player.
     */
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
