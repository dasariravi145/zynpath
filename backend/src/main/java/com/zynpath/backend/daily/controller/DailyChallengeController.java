package com.zynpath.backend.daily.controller;

import com.zynpath.backend.auth.model.PlayerSession;
import com.zynpath.backend.auth.service.SessionSecurityService;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyChallengeAttemptDto;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyChallengeDefinitionDto;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyChallengeResultDto;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyCompletionClaimDto;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyLeaderboardResponse;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyProvisionalSyncRequest;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyStartAttemptRequest;
import com.zynpath.backend.daily.service.DailyChallengeService;
import com.zynpath.backend.security.annotation.RequireAccess;
import com.zynpath.backend.security.audit.SecurityAuditLogger;
import com.zynpath.backend.security.audit.SecurityEventType;
import com.zynpath.backend.security.model.EndpointAccessTier;
import com.zynpath.backend.security.ratelimit.RateLimitPolicy;
import com.zynpath.backend.security.ratelimit.RateLimited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Daily Challenge online verification, shared puzzles,
 * competitive attempts, and daily leaderboards.
 *
 * Implements Prompt 25 Sections 15, 17, 28, 36, 40, 52 & 53 and Prompt 36 Sections 42, 43:
 * - Server-authoritative daily puzzle delivery and attempt tracking.
 * - Rate-limited attempt starts and solve submissions.
 * - Bounded leaderboard pagination preventing resource exhaustion.
 */
@RestController
@RequestMapping("/api/v1/daily")
public class DailyChallengeController {

    private static final Logger log = LoggerFactory.getLogger(DailyChallengeController.class);

    private final DailyChallengeService dailyChallengeService;
    private final SessionSecurityService sessionSecurityService;
    private final SecurityAuditLogger auditLogger;

    public DailyChallengeController(
            DailyChallengeService dailyChallengeService,
            SessionSecurityService sessionSecurityService,
            SecurityAuditLogger auditLogger
    ) {
        this.dailyChallengeService = dailyChallengeService;
        this.sessionSecurityService = sessionSecurityService;
        this.auditLogger = auditLogger;
    }

    /**
     * Retrieve the canonical daily challenge definition for today or a specific UTC date.
     */
    @GetMapping("/challenge")
    @RequireAccess(EndpointAccessTier.PUBLIC)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<DailyChallengeDefinitionDto> getChallenge(
            @RequestParam(value = "date", required = false) String dateKey
    ) {
        DailyChallengeDefinitionDto definition = dailyChallengeService.getOfficialChallenge(dateKey);
        return ResponseEntity.ok(definition);
    }

    /**
     * Start an official server-authoritative competitive attempt.
     */
    @PostMapping("/attempt/start")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.SUBMISSIONS)
    public ResponseEntity<DailyChallengeAttemptDto> startAttempt(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody(required = false) DailyStartAttemptRequest request
    ) {
        String playerId = authenticate(authHeader);
        String dateKey = request != null ? request.dateKey() : null;
        DailyChallengeAttemptDto attempt = dailyChallengeService.startOfficialAttempt(playerId, dateKey);
        return ResponseEntity.ok(attempt);
    }

    /**
     * Retrieve the active official attempt for resumption or reconnection.
     */
    @GetMapping("/attempt/active")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<DailyChallengeAttemptDto> getActiveAttempt(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "date", required = false) String dateKey
    ) {
        String playerId = authenticate(authHeader);
        DailyChallengeAttemptDto attempt = dailyChallengeService.getActiveAttempt(playerId, dateKey);
        if (attempt == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(attempt);
    }

    /**
     * Submit a completed puzzle path for independent server-side verification and ranking.
     */
    @PostMapping("/attempt/submit")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.SUBMISSIONS)
    public ResponseEntity<DailyChallengeResultDto> submitCompletion(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody DailyCompletionClaimDto claim
    ) {
        String playerId = authenticate(authHeader);
        DailyChallengeResultDto result = dailyChallengeService.submitCompletion(playerId, claim);
        auditLogger.logEvent(SecurityEventType.PUZZLE_SOLVE_SUBMITTED, playerId,
                java.util.Map.of("challengeId", claim.challengeId(), "solveTimeMs", String.valueOf(result.solveTimeMs())));
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieve personal daily challenge result for the specified date.
     */
    @GetMapping("/result")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<DailyChallengeResultDto> getPersonalResult(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "date", required = false) String dateKey
    ) {
        String playerId = authenticate(authHeader);
        DailyChallengeResultDto result = dailyChallengeService.getPersonalResult(playerId, dateKey);
        if (result == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieve the paginated daily leaderboard for an official challenge date.
     */
    @GetMapping("/leaderboard")
    @RequireAccess(EndpointAccessTier.PUBLIC)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<DailyLeaderboardResponse> getLeaderboard(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "date", required = false) String dateKey,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize
    ) {
        String playerId = null;
        if (authHeader != null && !authHeader.isBlank()) {
            try {
                playerId = authenticate(authHeader);
            } catch (Exception ignored) {
                // Anonymous viewer allowed
            }
        }

        // Bounded pagination: maximum 100 entries per page
        int safePage = Math.max(0, page);
        int safePageSize = Math.min(Math.max(1, pageSize), 100);

        DailyLeaderboardResponse response = dailyChallengeService.getDailyLeaderboard(dateKey, safePage, safePageSize, playerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Synchronize provisional offline completion metadata without polluting the timed competitive leaderboard.
     */
    @PostMapping("/sync-provisional")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.SUBMISSIONS)
    public ResponseEntity<DailyChallengeResultDto> syncProvisional(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody DailyProvisionalSyncRequest request
    ) {
        String playerId = authenticate(authHeader);
        DailyChallengeResultDto result = dailyChallengeService.syncProvisional(playerId, request);
        return ResponseEntity.ok(result);
    }

    private String authenticate(String authHeader) {
        PlayerSession session = sessionSecurityService.validateSession(authHeader);
        return session.playerId();
    }
}
