package com.zynpath.backend.sync.controller;

import com.zynpath.backend.auth.model.AuthClaims;
import com.zynpath.backend.auth.service.SessionSecurityService;
import com.zynpath.backend.security.annotation.RequireAccess;
import com.zynpath.backend.security.audit.SecurityAuditLogger;
import com.zynpath.backend.security.audit.SecurityEventType;
import com.zynpath.backend.security.model.EndpointAccessTier;
import com.zynpath.backend.security.ratelimit.RateLimitPolicy;
import com.zynpath.backend.security.ratelimit.RateLimited;
import com.zynpath.backend.sync.model.SyncDto.BatchSyncRequest;
import com.zynpath.backend.sync.model.SyncDto.BatchSyncResponse;
import com.zynpath.backend.sync.model.SyncDto.LevelProgressDto;
import com.zynpath.backend.sync.model.SyncDto.ProgressResponse;
import com.zynpath.backend.sync.service.SyncService;
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

import java.util.List;

/**
 * REST Controller for offline-first progress synchronization, durable queue reconciliation,
 * and remote progress retrieval.
 *
 * Implements Prompt 35 Sections 7, 11, 12, 17, 54 & 55 and Prompt 36 Sections 18, 50, 72:
 * - Batched progress submission supporting at-least-once delivery and idempotency.
 * - Incremental remote progress fetching for authenticated and guest players.
 * - Rate-limited batch sync submissions.
 */
@RestController
@RequestMapping("/api/v1/sync")
public class SyncController {

    private static final Logger log = LoggerFactory.getLogger(SyncController.class);

    private final SyncService syncService;
    private final SessionSecurityService sessionSecurityService;
    private final SecurityAuditLogger auditLogger;

    public SyncController(
            SyncService syncService,
            SessionSecurityService sessionSecurityService,
            SecurityAuditLogger auditLogger
    ) {
        this.syncService = syncService;
        this.sessionSecurityService = sessionSecurityService;
        this.auditLogger = auditLogger;
    }

    /**
     * Submit a batch of durable client sync operations.
     */
    @PostMapping("/batch")
    @RequireAccess(EndpointAccessTier.PUBLIC)
    @RateLimited(RateLimitPolicy.SUBMISSIONS)
    public ResponseEntity<BatchSyncResponse> submitBatch(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody BatchSyncRequest request
    ) {
        String authenticatedPlayerId = resolvePlayerId(authHeader);
        String effectivePlayerId = authenticatedPlayerId != null ? authenticatedPlayerId : (request != null ? request.playerId() : "unknown");
        log.info("Processing sync batch for player={}, operationCount={}",
                effectivePlayerId,
                request != null && request.operations() != null ? request.operations().size() : 0);

        BatchSyncResponse response = syncService.processBatch(authenticatedPlayerId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve latest authoritative remote level progress for reconciliation.
     */
    @GetMapping("/progress")
    @RequireAccess(EndpointAccessTier.PUBLIC)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<ProgressResponse> getRemoteProgress(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam("playerId") String playerId
    ) {
        String effectivePlayerId = resolvePlayerId(authHeader);
        if (effectivePlayerId == null || effectivePlayerId.isBlank()) {
            effectivePlayerId = playerId;
        }

        List<LevelProgressDto> progress = syncService.getPlayerProgressList(effectivePlayerId);
        return ResponseEntity.ok(new ProgressResponse(progress, System.currentTimeMillis()));
    }

    private String resolvePlayerId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        AuthClaims claims = sessionSecurityService.verifyBearerToken(authHeader);
        return claims.isAuthenticated() ? claims.playerId() : null;
    }
}
