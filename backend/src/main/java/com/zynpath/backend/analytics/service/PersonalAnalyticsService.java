package com.zynpath.backend.analytics.service;

import com.zynpath.backend.analytics.model.PersonalAnalyticsDto.DailyResultItemDto;
import com.zynpath.backend.analytics.model.PersonalAnalyticsDto.PersonalAnalyticsReportDto;
import com.zynpath.backend.analytics.model.PersonalAnalyticsDto.ServerDailyChallengeSummaryDto;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyChallengeResultDto;
import com.zynpath.backend.daily.service.DailyChallengeService;
import com.zynpath.backend.multiplayer.model.CompetitiveDto.CompetitiveStatsDto;
import com.zynpath.backend.multiplayer.service.CompetitiveService;
import com.zynpath.backend.subscription.model.SubscriptionTier;
import com.zynpath.backend.subscription.service.EntitlementService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Service that aggregates verified server-authoritative personal analytics.
 * Adheres strictly to recorded gameplay with no fabricated metrics.
 */
@Service
public class PersonalAnalyticsService {

    private final PlayerAccountService playerAccountService;
    private final CompetitiveService competitiveService;
    private final DailyChallengeService dailyChallengeService;
    private final EntitlementService entitlementService;

    public PersonalAnalyticsService(
            PlayerAccountService playerAccountService,
            CompetitiveService competitiveService,
            DailyChallengeService dailyChallengeService,
            EntitlementService entitlementService
    ) {
        this.playerAccountService = playerAccountService;
        this.competitiveService = competitiveService;
        this.dailyChallengeService = dailyChallengeService;
        this.entitlementService = entitlementService;
    }

    public PersonalAnalyticsReportDto getPersonalAnalytics(String playerId) {
        PlayerAccount account = playerAccountService.findById(playerId).orElse(null);
        String publicZynpathId = account != null ? account.publicZynpathId() : "ZYN-XXXX-0000";
        String displayName = account != null ? account.displayName() : "Pathfinder";

        SubscriptionTier tier = entitlementService != null ? entitlementService.getEntitlement(playerId) : SubscriptionTier.FREE;
        boolean isAdvancedUnlocked = tier != null && tier != SubscriptionTier.FREE;

        CompetitiveStatsDto compStats = competitiveService.getCompetitiveStats(playerId);

        List<DailyChallengeResultDto> dailyResults = dailyChallengeService.getPlayerDailyResults(playerId);
        ServerDailyChallengeSummaryDto dailySummary = buildDailySummary(dailyResults);

        return new PersonalAnalyticsReportDto(
                playerId,
                publicZynpathId,
                displayName,
                tier != null ? tier : SubscriptionTier.FREE,
                isAdvancedUnlocked,
                compStats,
                dailySummary,
                System.currentTimeMillis()
        );
    }

    private ServerDailyChallengeSummaryDto buildDailySummary(List<DailyChallengeResultDto> results) {
        if (results == null || results.isEmpty()) {
            return new ServerDailyChallengeSummaryDto(0, 0, null, null, Collections.emptyList());
        }

        int totalVerified = results.size();
        int eligibleCount = 0;
        long fastestTime = Long.MAX_VALUE;
        long totalTime = 0L;
        int timedCount = 0;

        for (DailyChallengeResultDto r : results) {
            if (r.isLeaderboardEligible()) {
                eligibleCount++;
            }
            if (r.solveTimeMs() > 0L) {
                if (r.solveTimeMs() < fastestTime) {
                    fastestTime = r.solveTimeMs();
                }
                totalTime += r.solveTimeMs();
                timedCount++;
            }
        }

        Long fastest = (fastestTime != Long.MAX_VALUE) ? fastestTime : null;
        Long average = (timedCount > 0) ? (totalTime / timedCount) : null;

        List<DailyResultItemDto> recent = results.stream()
                .limit(30)
                .map(r -> new DailyResultItemDto(
                        r.dateKey(),
                        r.solveTimeMs(),
                        r.completedAt(),
                        r.isLeaderboardEligible(),
                        r.rank()
                ))
                .toList();

        return new ServerDailyChallengeSummaryDto(totalVerified, eligibleCount, fastest, average, recent);
    }
}
