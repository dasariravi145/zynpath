package com.zynpath.backend.analytics.model;

import com.zynpath.backend.daily.model.DailyChallengeDto.DailyChallengeResultDto;
import com.zynpath.backend.multiplayer.model.CompetitiveDto.CompetitiveStatsDto;
import com.zynpath.backend.subscription.model.SubscriptionTier;

import java.util.List;

/**
 * Data transfer objects for server-authoritative personal analytics.
 * Implements Prompt 30 Section 56.
 */
public final class PersonalAnalyticsDto {

    private PersonalAnalyticsDto() {}

    public record DailyResultItemDto(
            String dateKey,
            long solveTimeMs,
            long completedAt,
            boolean isLeaderboardEligible,
            Integer rank
    ) {}

    public record ServerDailyChallengeSummaryDto(
            int totalVerifiedCompletions,
            int eligibleLeaderboardCompletions,
            Long fastestSolveMs,
            Long averageSolveMs,
            List<DailyResultItemDto> recentVerifiedResults
    ) {}

    public record PersonalAnalyticsReportDto(
            String playerId,
            String publicZynpathId,
            String displayName,
            SubscriptionTier subscriptionTier,
            boolean isAdvancedAnalyticsUnlocked,
            CompetitiveStatsDto competitiveStats,
            ServerDailyChallengeSummaryDto dailyChallengeStats,
            long generatedAt
    ) {}
}
