package com.zynpath.backend.ads.controller;

import com.zynpath.backend.ads.model.RewardDto.AdMobSsvCallbackParams;
import com.zynpath.backend.ads.model.RewardDto.PlayerHintBalanceResponse;
import com.zynpath.backend.ads.model.RewardDto.VerifyRewardRequest;
import com.zynpath.backend.ads.model.RewardDto.VerifyRewardResponse;
import com.zynpath.backend.ads.model.RewardVerificationStatus;
import com.zynpath.backend.ads.service.AdRewardService;
import com.zynpath.backend.auth.model.AuthClaims;
import com.zynpath.backend.auth.service.SessionSecurityService;
import com.zynpath.backend.security.annotation.RequireAccess;
import com.zynpath.backend.security.audit.SecurityAuditLogger;
import com.zynpath.backend.security.audit.SecurityEventType;
import com.zynpath.backend.security.integrity.BillingIntegrityGuard;
import com.zynpath.backend.security.model.EndpointAccessTier;
import com.zynpath.backend.security.ratelimit.RateLimitPolicy;
import com.zynpath.backend.security.ratelimit.RateLimited;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for rewarded ad verification, balance lookups, and AdMob SSV webhooks.
 *
 * Implements Prompt 29 Sections 27, 47 & 50 and Prompt 36 Sections 48, 49:
 * - Deduplicates reward transaction grants via BillingIntegrityGuard.
 * - Enforces rate limiting on reward claim submissions.
 */
@RestController
@RequestMapping("/api/v1/ads")
public class AdRewardController {

    private final AdRewardService adRewardService;
    private final SessionSecurityService sessionSecurityService;
    private final BillingIntegrityGuard billingIntegrityGuard;
    private final SecurityAuditLogger auditLogger;

    public AdRewardController(
        AdRewardService adRewardService,
        SessionSecurityService sessionSecurityService,
        BillingIntegrityGuard billingIntegrityGuard,
        SecurityAuditLogger auditLogger
    ) {
        this.adRewardService = adRewardService;
        this.sessionSecurityService = sessionSecurityService;
        this.billingIntegrityGuard = billingIntegrityGuard;
        this.auditLogger = auditLogger;
    }

    /**
     * Submit an earned rewarded ad completion for authoritative verification and credit grant.
     */
    @PostMapping("/reward/verify")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.SUBMISSIONS)
    public ResponseEntity<VerifyRewardResponse> verifyReward(
        @RequestHeader(value = "Authorization", required = false) String authHeader,
        @Valid @RequestBody VerifyRewardRequest request
    ) {
        AuthClaims claims = sessionSecurityService.verifyBearerToken(authHeader);
        String playerId = claims.isAuthenticated() ? claims.playerId() : null;

        if (playerId == null || playerId.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new VerifyRewardResponse(
                    RewardVerificationStatus.REJECTED,
                    false,
                    request.rewardEventId(),
                    0,
                    0,
                    "Authentication required to verify and grant rewarded hints"
                )
            );
        }

        // Anti-abuse: ensure reward event ID is not replayed
        billingIntegrityGuard.assertRewardTransactionUnique(request.rewardEventId());

        VerifyRewardResponse response = adRewardService.verifyReward(playerId, request);
        if (!response.granted() && response.status() == RewardVerificationStatus.REJECTED) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (response.granted()) {
            auditLogger.logEvent(SecurityEventType.REWARD_GRANTED, playerId,
                    java.util.Map.of("rewardEventId", request.rewardEventId(), "newRewardedCredits", String.valueOf(response.newRewardedCredits())));
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve the player's active hint credit balance.
     */
    @GetMapping("/reward/balance")
    @RequireAccess(EndpointAccessTier.PUBLIC)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<PlayerHintBalanceResponse> getBalance(
        @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        AuthClaims claims = sessionSecurityService.verifyBearerToken(authHeader);
        String playerId = claims.isAuthenticated() ? claims.playerId() : "guest";
        return ResponseEntity.ok(adRewardService.getBalance(playerId));
    }

    /**
     * Webhook endpoint for Google AdMob Server-Side Verification (SSV) callbacks.
     */
    @GetMapping("/ssv-callback")
    @RequireAccess(EndpointAccessTier.PUBLIC)
    @RateLimited(RateLimitPolicy.SUBMISSIONS)
    public ResponseEntity<String> handleSsvCallback(
        @RequestParam(value = "ad_network", required = false) String adNetwork,
        @RequestParam(value = "ad_unit", required = false) String adUnit,
        @RequestParam(value = "custom_data", required = false) String customData,
        @RequestParam(value = "reward_amount", required = false) String rewardAmount,
        @RequestParam(value = "reward_item", required = false) String rewardItem,
        @RequestParam(value = "timestamp", required = false) String timestamp,
        @RequestParam(value = "transaction_id", required = false) String transactionId,
        @RequestParam(value = "user_id", required = false) String userId,
        @RequestParam(value = "signature", required = false) String signature,
        @RequestParam(value = "key_id", required = false) String keyId
    ) {
        AdMobSsvCallbackParams params = new AdMobSsvCallbackParams(
            adNetwork,
            adUnit,
            customData,
            rewardAmount,
            rewardItem,
            timestamp,
            transactionId,
            userId,
            signature,
            keyId
        );

        boolean success = adRewardService.handleSsvCallback(params);
        if (success) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID_TRANSACTION");
        }
    }
}
