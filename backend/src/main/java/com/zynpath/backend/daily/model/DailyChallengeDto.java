package com.zynpath.backend.daily.model;

import java.util.List;

/**
 * Data Transfer Objects for official Daily Challenge publication, competitive attempts,
 * authoritative validation, personal results, and daily leaderboards.
 *
 * Implements Prompt 25 Sections 8, 13, 16, 17, 31, 35, 36, 40 & 52.
 */
public class DailyChallengeDto {

    public enum DailyAttemptStatus {
        CREATED,
        ACTIVE,
        COMPLETION_PENDING_VALIDATION,
        VALID_COMPLETION,
        INVALID_SUBMISSION,
        EXPIRED,
        ABANDONED,
        FINALIZED
    }

    public enum DailyVerificationStatus {
        LOCAL_COMPLETION,
        PROVISIONAL,
        SERVER_VALIDATED,
        LEADERBOARD_ELIGIBLE,
        NOT_ELIGIBLE
    }

    public record DailyCheckpointSpec(int number, int row, int col) {}

    public record DailyBlockedEdgeSpec(int row1, int col1, int row2, int col2) {}

    public record DailyChallengeDefinitionDto(
            String challengeId,
            String dateKey,
            int challengeVersion,
            String scheduleVersion,
            String puzzleId,
            int puzzleVersion,
            String puzzleFingerprint,
            int gridRows,
            int gridCols,
            List<DailyCheckpointSpec> checkpoints,
            List<DailyBlockedEdgeSpec> blockedEdges,
            String difficultyTier,
            String title,
            long serverTimeMs,
            long submissionDeadlineMs,
            boolean isPublished
    ) {}

    public record DailyChallengeAttemptDto(
            String attemptId,
            String playerId,
            String challengeId,
            String dateKey,
            String puzzleFingerprint,
            long startedAt,
            long expiresAt,
            DailyAttemptStatus status
    ) {}

    public record DailyCompletionClaimDto(
            String attemptId,
            String challengeId,
            String puzzleFingerprint,
            List<String> pathCoordinates,
            Long clientElapsedMs
    ) {}

    public record DailyChallengeResultDto(
            String resultId,
            String attemptId,
            String playerId,
            String publicZynpathId,
            String displayName,
            String avatarId,
            String challengeId,
            String dateKey,
            String puzzleFingerprint,
            long solveTimeMs,
            long completedAt,
            DailyVerificationStatus verificationStatus,
            boolean isLeaderboardEligible,
            Integer rank
    ) {}

    public record DailyLeaderboardEntryDto(
            int rank,
            String publicZynpathId,
            String displayName,
            String avatarId,
            long solveTimeMs,
            long completedAt,
            DailyVerificationStatus verificationStatus
    ) {}

    public record DailyLeaderboardResponse(
            String challengeId,
            String dateKey,
            String puzzleFingerprint,
            List<DailyLeaderboardEntryDto> entries,
            DailyLeaderboardEntryDto playerEntry,
            int totalEntries,
            int page,
            int pageSize
    ) {}

    public record DailyProvisionalSyncRequest(
            String challengeId,
            String dateKey,
            String puzzleFingerprint,
            long solveTimeMs,
            long completedAt,
            List<String> pathCoordinates,
            int movesCount
    ) {}

    public record DailyStartAttemptRequest(
            String dateKey
    ) {}
}
