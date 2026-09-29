package com.zynpath.backend.cosmetics.controller;

import com.zynpath.backend.auth.model.AuthClaims;
import com.zynpath.backend.auth.service.SessionSecurityService;
import com.zynpath.backend.cosmetics.model.CosmeticDto.CosmeticCatalogResponse;
import com.zynpath.backend.cosmetics.model.CosmeticDto.EquippedCosmeticsDto;
import com.zynpath.backend.cosmetics.model.CosmeticDto.PublicPlayerCosmeticsDto;
import com.zynpath.backend.cosmetics.model.CosmeticDto.UpdateEquippedCosmeticsRequest;
import com.zynpath.backend.cosmetics.model.CosmeticDto.UpdateEquippedCosmeticsResponse;
import com.zynpath.backend.cosmetics.service.CosmeticService;
import com.zynpath.backend.security.annotation.RequireAccess;
import com.zynpath.backend.security.model.EndpointAccessTier;
import com.zynpath.backend.security.ratelimit.RateLimitPolicy;
import com.zynpath.backend.security.ratelimit.RateLimited;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for cosmetic catalog retrieval, equipment synchronization,
 * and public cosmetic profile metadata.
 *
 * Implements Prompt 28 Sections 31, 32, 51 & 54 and Prompt 36 Sections 18, 50:
 * - Public cosmetic catalog and profiles.
 * - Authenticated cosmetic equipment updates.
 */
@RestController
@RequestMapping("/api/v1/cosmetics")
public class CosmeticController {

    private final CosmeticService cosmeticService;
    private final SessionSecurityService sessionSecurityService;

    public CosmeticController(
            CosmeticService cosmeticService,
            SessionSecurityService sessionSecurityService
    ) {
        this.cosmeticService = cosmeticService;
        this.sessionSecurityService = sessionSecurityService;
    }

    /**
     * Retrieve the active cosmetic catalog and version metadata.
     */
    @GetMapping("/catalog")
    @RequireAccess(EndpointAccessTier.PUBLIC)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<CosmeticCatalogResponse> getCatalog() {
        return ResponseEntity.ok(cosmeticService.getCatalog());
    }

    /**
     * Retrieve the authenticated player's equipped cosmetics.
     */
    @GetMapping("/equipped")
    @RequireAccess(EndpointAccessTier.PUBLIC)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<EquippedCosmeticsDto> getEquipped(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        AuthClaims claims = sessionSecurityService.verifyBearerToken(authHeader);
        String playerId = claims.isAuthenticated() ? claims.playerId() : "anonymous";
        return ResponseEntity.ok(cosmeticService.getEquippedCosmetics(playerId));
    }

    /**
     * Update the authenticated player's equipped cosmetics with server entitlement validation.
     */
    @PostMapping("/equipped")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<UpdateEquippedCosmeticsResponse> updateEquipped(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody UpdateEquippedCosmeticsRequest request
    ) {
        AuthClaims claims = sessionSecurityService.verifyBearerToken(authHeader);
        if (!claims.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new UpdateEquippedCosmeticsResponse(false, "UNAUTHENTICATED", "Authentication required to equip cosmetics", null)
            );
        }

        UpdateEquippedCosmeticsResponse response = cosmeticService.updateEquippedCosmetics(claims.playerId(), request);
        if (!response.success()) {
            if ("ENTITLEMENT_REQUIRED".equals(response.errorCode())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve public cosmetic metadata for another player.
     * Exposes strictly cosmetic IDs and ZERO billing or payment information.
     */
    @GetMapping("/public/{playerId}")
    @RequireAccess(EndpointAccessTier.PUBLIC)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<PublicPlayerCosmeticsDto> getPublicCosmetics(
            @PathVariable("playerId") String playerId
    ) {
        return ResponseEntity.ok(cosmeticService.getPublicPlayerCosmetics(playerId));
    }
}
