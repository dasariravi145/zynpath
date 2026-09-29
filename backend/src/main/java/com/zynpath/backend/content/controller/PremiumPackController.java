package com.zynpath.backend.content.controller;

import com.zynpath.backend.auth.model.AuthClaims;
import com.zynpath.backend.auth.service.SessionSecurityService;
import com.zynpath.backend.content.model.PremiumContentDto.PremiumPackDownloadResponse;
import com.zynpath.backend.content.model.PremiumContentDto.PremiumPackManifestDto;
import com.zynpath.backend.content.service.PremiumPackContentService;
import com.zynpath.backend.security.annotation.RequireAccess;
import com.zynpath.backend.security.audit.SecurityAuditLogger;
import com.zynpath.backend.security.audit.SecurityEventType;
import com.zynpath.backend.security.model.EndpointAccessTier;
import com.zynpath.backend.security.ratelimit.RateLimitPolicy;
import com.zynpath.backend.security.ratelimit.RateLimited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for Premium puzzle-pack catalog queries, manifests, and secure content downloads.
 *
 * Implements Prompt 27 and Prompt 36 Sections 47, 50:
 * - Server-authoritative entitlement verification.
 * - Content security (no leaked credentials or tokens).
 * - Rate-limited pack downloads.
 */
@RestController
@RequestMapping("/api/v1/content/packs")
public class PremiumPackController {

    private static final Logger log = LoggerFactory.getLogger(PremiumPackController.class);

    private final PremiumPackContentService contentService;
    private final SessionSecurityService sessionSecurityService;
    private final SecurityAuditLogger auditLogger;

    public PremiumPackController(
            PremiumPackContentService contentService,
            SessionSecurityService sessionSecurityService,
            SecurityAuditLogger auditLogger
    ) {
        this.contentService = contentService;
        this.sessionSecurityService = sessionSecurityService;
        this.auditLogger = auditLogger;
    }

    /**
     * Retrieves the catalog of all available and upcoming Premium packs.
     */
    @GetMapping
    @RequireAccess(EndpointAccessTier.PUBLIC)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<List<PremiumPackManifestDto>> getCatalog() {
        return ResponseEntity.ok(contentService.getCatalog());
    }

    /**
     * Retrieves the manifest for a specific pack.
     */
    @GetMapping("/{packId}/manifest")
    @RequireAccess(EndpointAccessTier.PUBLIC)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<PremiumPackManifestDto> getManifest(@PathVariable String packId) {
        return contentService.getManifest(packId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Downloads the verified puzzle collection for a pack.
     * Enforces active subscription entitlement on the server.
     */
    @GetMapping("/{packId}/download")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.SENSITIVE)
    public ResponseEntity<?> downloadPack(
            @PathVariable String packId,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        AuthClaims claims = sessionSecurityService.verifyBearerToken(authHeader);
        if (!claims.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required to download premium content");
        }

        var optResponse = contentService.downloadPack(claims.playerId(), packId);
        if (optResponse.isEmpty()) {
            // Check if pack exists to distinguish 404 from 403
            var manifestOpt = contentService.getManifest(packId);
            if (manifestOpt.isEmpty() || manifestOpt.get().puzzleCount() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Pack not found or not currently available for download");
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("ENTITLEMENT_REQUIRED: Active Premium subscription required to download this pack");
        }

        auditLogger.logEvent(SecurityEventType.ENTITLEMENT_VERIFIED, claims.playerId(),
                java.util.Map.of("packId", packId, "downloadGranted", "true"));
        return ResponseEntity.ok(optResponse.get());
    }
}
