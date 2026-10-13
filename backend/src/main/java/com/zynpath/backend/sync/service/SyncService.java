package com.zynpath.backend.sync.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyProvisionalSyncRequest;
import com.zynpath.backend.daily.service.DailyChallengeService;
import com.zynpath.backend.persistence.port.SyncPersistencePort;
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
import java.util.List;

/**
 * Service orchestrating offline-first synchronization, idempotent reconciliation,
 * and conflict resolution across game progress domains.
 *
 * Implements Prompt 35 Sections 6, 7, 11, 12, 20, 21, 22, 29, 39, 51, 54 & 56:
 * - Idempotent operation deduplication using stable client operation IDs.
 * - Non-destructive Solo progress merging (completions never lost, personal best preserved).
 * - Offline Daily Challenge reconciliation into provisional participation without corrupting competitive leaderboards.
 * - Partial batch tolerance ensuring individual operations succeed or fail independently.
 *
 * Delegates state persistence to SyncPersistencePort (supporting both PostgreSQL JPA and In-Memory storage).
 */
@Service
public class SyncService {

    private static final Logger log = LoggerFactory.getLogger(SyncService.class);

    private final DailyChallengeService dailyChallengeService;
    private final ObjectMapper objectMapper;
    private final SyncPersistencePort syncPersistencePort;

    public SyncService(
            DailyChallengeService dailyChallengeService,
            ObjectMapper objectMapper,
            SyncPersistencePort syncPersistencePort
    ) {
        this.dailyChallengeService = dailyChallengeService;
        this.objectMapper = objectMapper;
        this.syncPersistencePort = syncPersistencePort;
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
        SyncOperationResultDto cached = syncPersistencePort.getCachedOperation(op.operationId());
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
                    syncPersistencePort.recordOperation(playerId, success, op.operationType());
                    return success;
                }
                case "DAILY_CHALLENGE_SUBMISSION" -> {
                    reconcileDailyChallenge(playerId, op);
                    SyncOperationResultDto success = new SyncOperationResultDto(op.operationId(), "SUCCESS", null, now);
                    syncPersistencePort.recordOperation(playerId, success, op.operationType());
                    return success;
                }
                case "PREFERENCE_UPDATE" -> {
                    // Preferences acknowledged
                    SyncOperationResultDto success = new SyncOperationResultDto(op.operationId(), "SUCCESS", null, now);
                    syncPersistencePort.recordOperation(playerId, success, op.operationType());
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

        // Validate canonical level boundaries (6 worlds * 300 levels = 1,800 levels)
        if (levelId < 1 || levelId > 1800) {
            throw new IllegalArgumentException("Level ID " + levelId + " out of canonical range 1..1800");
        }

        LevelProgressDto progress = new LevelProgressDto(
                levelId,
                worldId,
                stars,
                bestTimeMs,
                movesCount,
                isCompleted,
                bestHintCount,
                completedAt
        );

        syncPersistencePort.saveOrUpdateLevelProgress(playerId, progress);
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
        return syncPersistencePort.getPlayerProgressList(playerId);
    }
}
