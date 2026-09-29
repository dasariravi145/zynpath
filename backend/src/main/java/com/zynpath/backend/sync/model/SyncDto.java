package com.zynpath.backend.sync.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * Data Transfer Objects for offline-first synchronization, idempotency, and progress exchange.
 *
 * Implements Prompt 35 Sections 9, 11, 12, 18, 53 & 54:
 * - Versioned synchronization payloads.
 * - Idempotent operation status.
 * - Non-destructive level progress exchange.
 */
public final class SyncDto {

    private SyncDto() {}

    public record SyncOperationDto(
            String operationId,
            String operationType,
            String resourceIdentity,
            int payloadVersion,
            String payloadJson,
            long clientTimestamp
    ) {}

    public record BatchSyncRequest(
            String playerId,
            List<SyncOperationDto> operations
    ) {}

    public record SyncOperationResultDto(
            String operationId,
            String status, // "SUCCESS", "IGNORED_DUPLICATE", "CONFLICT", "REJECTED"
            @JsonInclude(JsonInclude.Include.NON_NULL)
            String message,
            long serverTimestamp
    ) {}

    public record LevelProgressDto(
            int levelId,
            int worldId,
            int stars,
            long bestTimeMs,
            int movesCount,
            boolean isCompleted,
            int bestHintCount,
            @JsonInclude(JsonInclude.Include.NON_NULL)
            Long completedAt
    ) {}

    public record BatchSyncResponse(
            List<SyncOperationResultDto> results,
            List<LevelProgressDto> latestProgress,
            long serverTimestamp
    ) {}

    public record ProgressResponse(
            List<LevelProgressDto> progress,
            long serverTimestamp
    ) {}
}
