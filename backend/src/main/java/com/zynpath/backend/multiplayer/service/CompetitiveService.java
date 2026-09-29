package com.zynpath.backend.multiplayer.service;

import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.multiplayer.model.CompetitiveDto;
import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.model.MatchParticipant;
import com.zynpath.backend.multiplayer.model.MatchResult;
import com.zynpath.backend.multiplayer.model.MatchSession;
import com.zynpath.backend.multiplayer.model.PuzzleAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Server-authoritative service for competitive match history, verified player statistics,
 * and leaderboard queries.
 *
 * Implements Prompt 24 Sections 5-21, 23-31, 40-42, 48:
 * - Backed strictly by real, finalized, server-validated match sessions.
 * - Idempotent result finalization event ingestion.
 * - Deterministic tie handling and stable bounded pagination.
 * - Safe privacy boundaries returning only public identities in leaderboards.
 * - Authorization validation on private match details.
 */
@Service
public class CompetitiveService {

    private static final Logger log = LoggerFactory.getLogger(CompetitiveService.class);

    private final PlayerAccountService playerAccountService;
    private final MatchSessionService matchSessionService;

    // Authoritative in-memory historical stores (thread-safe, ready for persistence layer)
    private final Map<String, MatchSession> finalizedSessions = new ConcurrentHashMap<>();
    private final Map<String, List<String>> playerMatchHistory = new ConcurrentHashMap<>();
    private final Set<String> finalizedMatchIds = ConcurrentHashMap.newKeySet();
    private final Map<String, String> playerIdByPublicId = new ConcurrentHashMap<>();

    // Section 60 & 62: Bounded in-memory leaderboard cache invalidated on match finalization
    public record CachedRankedEntry(int rank, String playerId, CompetitiveDto.LeaderboardEntryDto dto) {}
    private final Map<String, List<CachedRankedEntry>> leaderboardCache = new ConcurrentHashMap<>();

    public CompetitiveService(
            PlayerAccountService playerAccountService,
            @Lazy MatchSessionService matchSessionService
    ) {
        this.playerAccountService = playerAccountService;
        this.matchSessionService = matchSessionService;
    }

    /**
     * Records an authoritatively concluded match session idempotently.
     * Prevents duplicate increments if called repeatedly.
     */
    public synchronized void recordFinalizedMatch(MatchSession session) {
        if (session == null || session.getMatchId() == null) {
            return;
        }
        String matchId = session.getMatchId();

        // Section 9 & 41: Idempotency guarantee
        if (!finalizedMatchIds.add(matchId)) {
            log.debug("Match {} already finalized in competitive history, skipping duplicate record", matchId);
            return;
        }

        finalizedSessions.put(matchId, session);

        // Invalidate precomputed leaderboards so next query reflects the new match result
        leaderboardCache.clear();

        for (MatchParticipant participant : session.getParticipants().values()) {
            String pId = participant.getPlayerId();
            if (pId != null) {
                playerMatchHistory.computeIfAbsent(pId, k -> new CopyOnWriteArrayList<>()).add(0, matchId);
                if (participant.getPublicZynpathId() != null && !participant.getPublicZynpathId().isBlank()) {
                    playerIdByPublicId.put(participant.getPublicZynpathId(), pId);
                }
            }
        }

        log.info("Recorded finalized competitive match: id={}, mode={}, participants={}",
                matchId, session.getGameMode(), session.getParticipantCount());
    }

    /**
     * Retrieves paginated match history for an authenticated player.
     * Ordered newest finalized match first.
     */
    public CompetitiveDto.MatchHistoryResponse getMatchHistory(
            String playerId,
            GameMode modeFilter,
            int page,
            int pageSize
    ) {
        int boundedPage = Math.max(0, page);
        int boundedSize = Math.max(1, Math.min(pageSize, 50));

        List<String> matchIds = playerMatchHistory.getOrDefault(playerId, Collections.emptyList());
        List<CompetitiveDto.MatchHistoryItemDto> filteredItems = new ArrayList<>();

        for (String matchId : matchIds) {
            MatchSession session = finalizedSessions.get(matchId);
            if (session != null) {
                if (modeFilter == null || session.getGameMode() == modeFilter) {
                    filteredItems.add(mapToHistoryItem(session, playerId));
                }
            }
        }

        int totalItems = filteredItems.size();
        int fromIndex = boundedPage * boundedSize;
        List<CompetitiveDto.MatchHistoryItemDto> pageItems;

        if (fromIndex >= totalItems) {
            pageItems = Collections.emptyList();
        } else {
            int toIndex = Math.min(fromIndex + boundedSize, totalItems);
            pageItems = filteredItems.subList(fromIndex, toIndex);
        }

        boolean hasMore = (fromIndex + pageItems.size()) < totalItems;
        return new CompetitiveDto.MatchHistoryResponse(pageItems, boundedPage, boundedSize, totalItems, hasMore);
    }

    /**
     * Retrieves authorized match details.
     * Only participants of the match are authorized to view full details.
     */
    public CompetitiveDto.MatchDetailsDto getMatchDetails(String matchId, String requestingPlayerId) {
        MatchSession session = finalizedSessions.get(matchId);
        if (session == null) {
            try {
                session = matchSessionService.getSession(matchId);
            } catch (Exception ignored) {
                // Not in active sessions either
            }
        }

        if (session == null) {
            throw new AuthException("MATCH_NOT_FOUND", "Match not found: " + matchId, HttpStatus.NOT_FOUND);
        }

        if (!session.hasParticipant(requestingPlayerId)) {
            throw new AuthException("MATCH_ACCESS_DENIED", "You are not authorized to view details for this match", HttpStatus.FORBIDDEN);
        }

        PuzzleAssignment puzzle = session.getPuzzleAssignment();
        long startedAt = session.getStartedAt() != null ? session.getStartedAt() : session.getCreatedAt();
        long endedAt = session.getEndedAt() != null ? session.getEndedAt() : System.currentTimeMillis();
        long durationMs = Math.max(0, endedAt - startedAt);

        List<CompetitiveDto.MatchParticipantSummaryDto> participantSummaries = buildParticipantSummaries(session);
        CompetitiveDto.MatchParticipantSummaryDto myResult = findParticipantSummary(participantSummaries, requestingPlayerId);

        return new CompetitiveDto.MatchDetailsDto(
                session.getMatchId(),
                session.getGameMode(),
                puzzle != null ? puzzle.puzzleId() : "unknown",
                puzzle != null ? puzzle.puzzleVersion() : 1,
                puzzle != null ? puzzle.fingerprint() : "",
                puzzle != null ? puzzle.gridRows() : 5,
                puzzle != null ? puzzle.gridCols() : 5,
                startedAt,
                endedAt,
                durationMs,
                session.getState().name(),
                session.getParticipantCount(),
                participantSummaries,
                myResult
        );
    }

    /**
     * Calculates authoritative competitive statistics for the authenticated player.
     */
    public CompetitiveDto.CompetitiveStatsDto getCompetitiveStats(String playerId) {
        PlayerAccount account = playerAccountService.findById(playerId).orElse(null);
        String publicZynpathId = account != null ? account.publicZynpathId() : "";
        String displayName = account != null ? account.displayName() : "Player";

        List<String> matchIds = playerMatchHistory.getOrDefault(playerId, Collections.emptyList());

        int totalFinalized = 0;
        int qdMatches = 0, qdWins = 0, qdLosses = 0, qdTies = 0;
        int fdMatches = 0, fdWins = 0, fdLosses = 0, fdTies = 0;
        int mlMatches = 0, mlFirstPlaces = 0, mlTopThree = 0;
        int mlTotalFinishSum = 0;
        int totalCompletions = 0;

        Map<String, CompetitiveDto.PersonalBestDto> personalBests = new HashMap<>();

        for (String matchId : matchIds) {
            MatchSession session = finalizedSessions.get(matchId);
            if (session == null) continue;

            MatchParticipant p = session.getParticipant(playerId);
            if (p == null) continue;

            totalFinalized++;
            GameMode mode = session.getGameMode();

            // Find authoritative MatchResult
            MatchResult res = null;
            for (MatchResult r : session.getResults()) {
                if (r.playerId().equals(playerId)) {
                    res = r;
                    break;
                }
            }

            boolean isWinner = (res != null && res.isWinner()) || p.isWinner();
            boolean isCompleted = (res != null && res.completed()) || (p.getCompletedAt() != null);
            Long solveTime = (res != null && res.solveTimeMs() != null) ? res.solveTimeMs() : p.getSolveTimeMs();
            int finishOrder = (res != null) ? res.finishOrder() : p.getFinishOrder();
            String status = (res != null) ? res.resultStatus() : (isWinner ? "VICTORY" : "COMPLETED");

            if (isCompleted) {
                totalCompletions++;
                if (solveTime != null && solveTime > 0) {
                    updatePersonalBest(personalBests, mode, session.getPuzzleAssignment(), matchId, solveTime, session.getEndedAt());
                }
            }

            if (mode == GameMode.QUICK_DUEL) {
                qdMatches++;
                if (isWinner) {
                    qdWins++;
                } else if ("TIED".equalsIgnoreCase(status)) {
                    qdTies++;
                } else {
                    qdLosses++;
                }
            } else if (mode == GameMode.FRIEND_DUEL) {
                fdMatches++;
                if (isWinner) {
                    fdWins++;
                } else if ("TIED".equalsIgnoreCase(status)) {
                    fdTies++;
                } else {
                    fdLosses++;
                }
            } else if (mode == GameMode.MINI_LEAGUE) {
                mlMatches++;
                if (finishOrder == 1) {
                    mlFirstPlaces++;
                }
                if (finishOrder >= 1 && finishOrder <= 3) {
                    mlTopThree++;
                }
                if (finishOrder > 0) {
                    mlTotalFinishSum += finishOrder;
                }
            }
        }

        double qdWinRate = qdMatches > 0 ? ((double) qdWins / qdMatches) : 0.0;
        double fdWinRate = fdMatches > 0 ? ((double) fdWins / fdMatches) : 0.0;
        double mlAvgFinish = mlMatches > 0 ? ((double) mlTotalFinishSum / mlMatches) : 0.0;

        return new CompetitiveDto.CompetitiveStatsDto(
                playerId,
                publicZynpathId,
                displayName,
                totalFinalized,
                qdMatches,
                qdWins,
                qdLosses,
                qdTies,
                qdWinRate,
                fdMatches,
                fdWins,
                fdLosses,
                fdTies,
                fdWinRate,
                mlMatches,
                mlFirstPlaces,
                mlTopThree,
                mlAvgFinish,
                totalCompletions,
                personalBests
        );
    }

    /**
     * Retrieves limited public competitive stats for a friend or other public player.
     * Excludes private IDs, tokens, emails, or granular history.
     */
    public CompetitiveDto.PublicCompetitiveStatsDto getPublicCompetitiveStats(String publicZynpathId) {
        PlayerAccount account = playerAccountService.findByPublicId(publicZynpathId)
                .orElseThrow(() -> new AuthException("PLAYER_NOT_FOUND", "Player not found: " + publicZynpathId, HttpStatus.NOT_FOUND));

        CompetitiveDto.CompetitiveStatsDto fullStats = getCompetitiveStats(account.playerId());
        return new CompetitiveDto.PublicCompetitiveStatsDto(
                account.publicZynpathId(),
                account.displayName(),
                "avatar_default",
                fullStats.totalFinalizedMatches(),
                fullStats.quickDuelWins(),
                fullStats.friendDuelWins(),
                fullStats.miniLeagueFirstPlaceFinishes(),
                fullStats.totalValidatedCompletions()
        );
    }

    /**
     * Lists available leaderboard categories.
     */
    public List<CompetitiveDto.LeaderboardCategoryInfoDto> getLeaderboardCategories() {
        List<CompetitiveDto.LeaderboardCategoryInfoDto> list = new ArrayList<>();
        for (CompetitiveDto.LeaderboardCategory cat : CompetitiveDto.LeaderboardCategory.values()) {
            list.add(new CompetitiveDto.LeaderboardCategoryInfoDto(
                    cat.name(),
                    cat.getTitle(),
                    cat.getDescription()
            ));
        }
        return list;
    }

    /**
     * Queries authoritative leaderboard rankings for a category and period.
     * Guaranteed no fake placeholder players; strictly from finalized records.
     * Uses in-memory caching to eliminate redundant O(N) session scans and N account queries.
     */
    public CompetitiveDto.LeaderboardResponse getLeaderboard(
            CompetitiveDto.LeaderboardCategory category,
            CompetitiveDto.LeaderboardPeriod period,
            int page,
            int pageSize,
            String requestingPlayerId
    ) {
        int boundedPage = Math.max(0, page);
        int boundedSize = Math.max(1, Math.min(pageSize, 50));

        String cacheKey = category.name() + ":" + period.name();
        List<CachedRankedEntry> allRankedEntries = leaderboardCache.computeIfAbsent(cacheKey, k -> computeRankedEntries(category, period));

        Integer myRank = null;
        Long myMetricValue = null;

        for (CachedRankedEntry entry : allRankedEntries) {
            if (entry.playerId().equals(requestingPlayerId)) {
                myRank = entry.rank();
                myMetricValue = entry.dto().metricValue();
                break;
            }
        }

        int totalEntries = allRankedEntries.size();
        int fromIndex = boundedPage * boundedSize;
        List<CompetitiveDto.LeaderboardEntryDto> pagedEntries;

        if (fromIndex >= totalEntries) {
            pagedEntries = Collections.emptyList();
        } else {
            int toIndex = Math.min(fromIndex + boundedSize, totalEntries);
            pagedEntries = allRankedEntries.subList(fromIndex, toIndex).stream()
                    .map(CachedRankedEntry::dto)
                    .toList();
        }

        boolean hasMore = (fromIndex + pagedEntries.size()) < totalEntries;

        return new CompetitiveDto.LeaderboardResponse(
                category,
                period,
                pagedEntries,
                myRank,
                myMetricValue,
                boundedPage,
                boundedSize,
                totalEntries,
                hasMore
        );
    }

    private List<CachedRankedEntry> computeRankedEntries(
            CompetitiveDto.LeaderboardCategory category,
            CompetitiveDto.LeaderboardPeriod period
    ) {
        long periodStartMs = calculatePeriodStartMs(period);

        // Map: playerId -> metric count
        Map<String, Long> playerMetrics = new HashMap<>();
        Map<String, Long> earliestAchieved = new HashMap<>();

        for (MatchSession session : finalizedSessions.values()) {
            long endedAt = session.getEndedAt() != null ? session.getEndedAt() : session.getCreatedAt();
            if (endedAt < periodStartMs) {
                continue;
            }

            for (MatchResult result : session.getResults()) {
                String pId = result.playerId();
                if (pId == null) continue;

                boolean eligible = switch (category) {
                    case QUICK_DUEL_WINS -> (session.getGameMode() == GameMode.QUICK_DUEL && result.isWinner());
                    case MINI_LEAGUE_WINS -> (session.getGameMode() == GameMode.MINI_LEAGUE && result.finishOrder() == 1);
                    case TOTAL_COMPLETIONS -> result.completed();
                };

                if (eligible) {
                    playerMetrics.put(pId, playerMetrics.getOrDefault(pId, 0L) + 1L);
                    earliestAchieved.putIfAbsent(pId, endedAt);
                }
            }
        }

        // Build sorted list of entries
        List<InternalLeaderboardRecord> sortedList = new ArrayList<>();
        for (Map.Entry<String, Long> entry : playerMetrics.entrySet()) {
            String pId = entry.getKey();
            long value = entry.getValue();
            if (value > 0) {
                Optional<PlayerAccount> accOpt = playerAccountService.findById(pId);
                String publicId = accOpt.map(PlayerAccount::publicZynpathId).orElse("ZYN-" + pId.substring(0, 4));
                String name = accOpt.map(PlayerAccount::displayName).orElse("Player");
                long timestamp = earliestAchieved.getOrDefault(pId, 0L);
                sortedList.add(new InternalLeaderboardRecord(pId, publicId, name, "avatar_default", value, timestamp));
            }
        }

        // Sort descending by value, deterministic secondary sort by publicZynpathId
        sortedList.sort(Comparator
                .comparingLong(InternalLeaderboardRecord::metricValue).reversed()
                .thenComparing(InternalLeaderboardRecord::publicZynpathId));

        // Assign standard competition ranks (1, 2, 2, 4...)
        List<CachedRankedEntry> ranked = new ArrayList<>();
        int currentRank = 1;
        for (int i = 0; i < sortedList.size(); i++) {
            InternalLeaderboardRecord rec = sortedList.get(i);
            if (i > 0 && rec.metricValue < sortedList.get(i - 1).metricValue) {
                currentRank = i + 1;
            }

            String formatted = switch (category) {
                case QUICK_DUEL_WINS -> rec.metricValue + (rec.metricValue == 1 ? " Win" : " Wins");
                case MINI_LEAGUE_WINS -> rec.metricValue + (rec.metricValue == 1 ? " 1st Place" : " 1st Places");
                case TOTAL_COMPLETIONS -> rec.metricValue + (rec.metricValue == 1 ? " Solve" : " Solves");
            };

            CompetitiveDto.LeaderboardEntryDto dto = new CompetitiveDto.LeaderboardEntryDto(
                    currentRank,
                    rec.publicZynpathId,
                    rec.displayName,
                    rec.avatarId,
                    rec.metricValue,
                    formatted
            );
            ranked.add(new CachedRankedEntry(currentRank, rec.playerId, dto));
        }
        return ranked;
    }

    private long calculatePeriodStartMs(CompetitiveDto.LeaderboardPeriod period) {
        if (period == CompetitiveDto.LeaderboardPeriod.ALL_TIME) {
            return 0L;
        }

        ZonedDateTime nowUtc = ZonedDateTime.now(ZoneOffset.UTC);
        if (period == CompetitiveDto.LeaderboardPeriod.THIS_MONTH) {
            return nowUtc.withDayOfMonth(1).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        } else if (period == CompetitiveDto.LeaderboardPeriod.THIS_WEEK) {
            return nowUtc.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        }
        return 0L;
    }

    private void updatePersonalBest(
            Map<String, CompetitiveDto.PersonalBestDto> bests,
            GameMode mode,
            PuzzleAssignment puzzle,
            String matchId,
            long solveTimeMs,
            Long endedAt
    ) {
        String key = mode.name();
        CompetitiveDto.PersonalBestDto existing = bests.get(key);
        if (existing == null || solveTimeMs < existing.solveTimeMs()) {
            bests.put(key, new CompetitiveDto.PersonalBestDto(
                    mode,
                    puzzle != null ? puzzle.puzzleId() : "unknown",
                    matchId,
                    solveTimeMs,
                    endedAt != null ? endedAt : System.currentTimeMillis()
            ));
        }
    }

    private CompetitiveDto.MatchHistoryItemDto mapToHistoryItem(MatchSession session, String requestingPlayerId) {
        PuzzleAssignment puzzle = session.getPuzzleAssignment();
        long startedAt = session.getStartedAt() != null ? session.getStartedAt() : session.getCreatedAt();
        long endedAt = session.getEndedAt() != null ? session.getEndedAt() : System.currentTimeMillis();

        List<CompetitiveDto.MatchParticipantSummaryDto> participantSummaries = buildParticipantSummaries(session);
        CompetitiveDto.MatchParticipantSummaryDto myResult = findParticipantSummary(participantSummaries, requestingPlayerId);

        return new CompetitiveDto.MatchHistoryItemDto(
                session.getMatchId(),
                session.getGameMode(),
                puzzle != null ? puzzle.puzzleId() : "unknown",
                puzzle != null ? puzzle.puzzleVersion() : 1,
                puzzle != null ? puzzle.fingerprint() : "",
                puzzle != null ? puzzle.gridRows() : 5,
                puzzle != null ? puzzle.gridCols() : 5,
                startedAt,
                endedAt,
                session.getState().name(),
                session.getParticipantCount(),
                participantSummaries,
                myResult
        );
    }

    private List<CompetitiveDto.MatchParticipantSummaryDto> buildParticipantSummaries(MatchSession session) {
        List<CompetitiveDto.MatchParticipantSummaryDto> summaries = new ArrayList<>();
        Map<String, MatchResult> resultMap = new HashMap<>();
        for (MatchResult r : session.getResults()) {
            resultMap.put(r.playerId(), r);
        }

        for (MatchParticipant p : session.getParticipants().values()) {
            MatchResult r = resultMap.get(p.getPlayerId());
            boolean completed = (r != null) ? r.completed() : (p.getCompletedAt() != null);
            Long solveTime = (r != null) ? r.solveTimeMs() : p.getSolveTimeMs();
            int finishOrder = (r != null) ? r.finishOrder() : p.getFinishOrder();
            boolean isWinner = (r != null) ? r.isWinner() : p.isWinner();
            String outcome = (r != null) ? r.resultStatus() : (isWinner ? "VICTORY" : (completed ? "COMPLETED" : "UNFINISHED"));

            summaries.add(new CompetitiveDto.MatchParticipantSummaryDto(
                    p.getPlayerId(),
                    p.getPublicZynpathId(),
                    p.getDisplayName(),
                    p.getAvatarId(),
                    completed,
                    solveTime,
                    finishOrder,
                    isWinner,
                    outcome
            ));
        }

        // Sort by finishOrder
        summaries.sort(Comparator.comparingInt(CompetitiveDto.MatchParticipantSummaryDto::finishOrder));
        return summaries;
    }

    private CompetitiveDto.MatchParticipantSummaryDto findParticipantSummary(
            List<CompetitiveDto.MatchParticipantSummaryDto> summaries,
            String playerId
    ) {
        for (CompetitiveDto.MatchParticipantSummaryDto dto : summaries) {
            if (dto.playerId().equals(playerId)) {
                return dto;
            }
        }
        return null;
    }

    private record InternalLeaderboardRecord(
            String playerId,
            String publicZynpathId,
            String displayName,
            String avatarId,
            long metricValue,
            long earliestAchievedAt
    ) {}
}
