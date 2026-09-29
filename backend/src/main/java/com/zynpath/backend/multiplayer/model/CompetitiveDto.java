package com.zynpath.backend.multiplayer.model;

import java.util.List;
import java.util.Map;

/**
 * Data Transfer Objects for Competitive Progression, Match History, Player Statistics, and Leaderboards.
 *
 * Implements Prompt 24 Sections 6, 7, 10, 14, 15, 17, 21, 24, 29, 30.
 */
public final class CompetitiveDto {

    private CompetitiveDto() {}

    public enum LeaderboardCategory {
        QUICK_DUEL_WINS("Quick Duel Victories", "Most 1v1 Quick Duel wins"),
        MINI_LEAGUE_WINS("Mini League 1st Places", "Most 1st place finishes in Mini League"),
        TOTAL_COMPLETIONS("Total Completions", "Most multiplayer puzzles solved");

        private final String title;
        private final String description;

        LeaderboardCategory(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum LeaderboardPeriod {
        ALL_TIME("All Time"),
        THIS_MONTH("This Month"),
        THIS_WEEK("This Week");

        private final String displayName;

        LeaderboardPeriod(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public record MatchParticipantSummaryDto(
            String playerId,
            String publicZynpathId,
            String displayName,
            String avatarId,
            boolean completed,
            Long solveTimeMs,
            int finishOrder,
            boolean isWinner,
            String outcomeStatus
    ) {}

    public record MatchHistoryItemDto(
            String matchId,
            GameMode gameMode,
            String puzzleId,
            int puzzleVersion,
            String puzzleFingerprint,
            int gridRows,
            int gridCols,
            long startedAt,
            long endedAt,
            String matchStatus,
            int participantCount,
            List<MatchParticipantSummaryDto> participants,
            MatchParticipantSummaryDto myResult
    ) {}

    public record MatchHistoryResponse(
            List<MatchHistoryItemDto> items,
            int page,
            int pageSize,
            int totalItems,
            boolean hasMore
    ) {}

    public record MatchDetailsDto(
            String matchId,
            GameMode gameMode,
            String puzzleId,
            int puzzleVersion,
            String puzzleFingerprint,
            int gridRows,
            int gridCols,
            long startedAt,
            long endedAt,
            long durationMs,
            String matchStatus,
            int participantCount,
            List<MatchParticipantSummaryDto> participants,
            MatchParticipantSummaryDto myResult
    ) {}

    public record PersonalBestDto(
            GameMode gameMode,
            String puzzleId,
            String matchId,
            long solveTimeMs,
            long achievedAt
    ) {}

    public record CompetitiveStatsDto(
            String playerId,
            String publicZynpathId,
            String displayName,
            int totalFinalizedMatches,
            int quickDuelMatches,
            int quickDuelWins,
            int quickDuelLosses,
            int quickDuelTies,
            double quickDuelWinRate,
            int friendDuelMatches,
            int friendDuelWins,
            int friendDuelLosses,
            int friendDuelTies,
            double friendDuelWinRate,
            int miniLeagueParticipations,
            int miniLeagueFirstPlaceFinishes,
            int miniLeagueTopThreeFinishes,
            double miniLeagueAverageFinishPosition,
            int totalValidatedCompletions,
            Map<String, PersonalBestDto> personalBests
    ) {}

    public record PublicCompetitiveStatsDto(
            String publicZynpathId,
            String displayName,
            String avatarId,
            int totalFinalizedMatches,
            int quickDuelWins,
            int friendDuelWins,
            int miniLeagueWins,
            int totalCompletions
    ) {}

    public record LeaderboardCategoryInfoDto(
            String id,
            String title,
            String description
    ) {}

    public record LeaderboardEntryDto(
            int rank,
            String publicZynpathId,
            String displayName,
            String avatarId,
            long metricValue,
            String formattedValue
    ) {}

    public record LeaderboardResponse(
            LeaderboardCategory category,
            LeaderboardPeriod period,
            List<LeaderboardEntryDto> entries,
            Integer myRank,
            Long myMetricValue,
            int page,
            int pageSize,
            int totalEntries,
            boolean hasMore
    ) {}
}
