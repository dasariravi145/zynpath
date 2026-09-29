package com.zynpath.backend.subscription.controller;

import com.zynpath.backend.auth.model.AuthClaims;
import com.zynpath.backend.auth.service.SessionSecurityService;
import com.zynpath.backend.subscription.model.SubscriptionDto.SubscriptionEntitlementDto;
import com.zynpath.backend.subscription.model.SubscriptionDto.SubscriptionRestoreRequest;
import com.zynpath.backend.subscription.model.SubscriptionDto.SubscriptionRestoreResponse;
import com.zynpath.backend.subscription.model.SubscriptionDto.SubscriptionVerificationRequest;
import com.zynpath.backend.subscription.model.SubscriptionDto.SubscriptionVerificationResponse;
import com.zynpath.backend.subscription.model.SubscriptionEntitlement;
import com.zynpath.backend.subscription.service.SubscriptionService;
import com.zynpath.backend.security.annotation.RequireAccess;
import com.zynpath.backend.security.audit.SecurityAuditLogger;
import com.zynpath.backend.security.audit.SecurityEventType;
import com.zynpath.backend.security.integrity.BillingIntegrityGuard;
import com.zynpath.backend.security.model.EndpointAccessTier;
import com.zynpath.backend.security.ratelimit.RateLimitPolicy;
import com.zynpath.backend.security.ratelimit.RateLimited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for subscription entitlement verification and queries.
 *
 * Implements Prompt 26 Sections 18, 20, 26, 29 & 50 and Prompt 36 Sections 44, 45, 46:
 * - Endpoints protected by bearer session authorization.
 * - Enforces account binding and prevents cross-account token hijacking.
 * - BillingIntegrityGuard prevents purchase token duplication across distinct player accounts.
 */
@RestController
@RequestMapping("/api/v1/subscription")
public class SubscriptionController {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionController.class);

    private final SubscriptionService subscriptionService;
    private final SessionSecurityService sessionSecurityService;
    private final BillingIntegrityGuard billingIntegrityGuard;
    private final SecurityAuditLogger auditLogger;

    public SubscriptionController(
            SubscriptionService subscriptionService,
            SessionSecurityService sessionSecurityService,
            BillingIntegrityGuard billingIntegrityGuard,
            SecurityAuditLogger auditLogger
    ) {
        this.subscriptionService = subscriptionService;
        this.sessionSecurityService = sessionSecurityService;
        this.billingIntegrityGuard = billingIntegrityGuard;
        this.auditLogger = auditLogger;
    }

    /**
     * Retrieve the current player's authoritative subscription entitlement.
     */
    /**
     * Retrieve the current player's authoritative subscription entitlement.
     */
    @GetMapping("/entitlement")
    @RequireAccess(EndpointAccessTier.PUBLIC)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<SubscriptionEntitlementDto> getEntitlement(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        AuthClaims claims = sessionSecurityService.verifyBearerToken(authHeader);
        String accountId = claims.isAuthenticated() ? claims.playerId() : "anonymous";

        SubscriptionEntitlement entitlement = subscriptionService.getEntitlementForAccount(accountId);
        return ResponseEntity.ok(subscriptionService.toDto(entitlement));
    }

    /**
     * Submit a Google Play purchase token for authoritative backend verification.
     */
    @PostMapping("/verify")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.SENSITIVE)
    public ResponseEntity<SubscriptionVerificationResponse> verifyPurchase(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody SubscriptionVerificationRequest request
    ) {
        AuthClaims claims = sessionSecurityService.verifyBearerToken(authHeader);
        if (!claims.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new SubscriptionVerificationResponse(
                            false,
                            "UNAUTHENTICATED",
                            "Authentication required to verify and link subscription purchase",
                            "NOT_CONFIGURED",
                            subscriptionService.toDto(SubscriptionEntitlement.free("anonymous"))
                    )
            );
        }

        // Anti-abuse: ensure purchase token is uniquely bound to this player account
        billingIntegrityGuard.assertPurchaseTokenOwnership(claims.playerId(), request.purchaseToken());

        SubscriptionVerificationResponse response = subscriptionService.verifyPurchase(claims.playerId(), request);
        auditLogger.logEvent(SecurityEventType.ENTITLEMENT_VERIFIED, claims.playerId(),
                java.util.Map.of("productId", request.productId(), "verified", String.valueOf(response.verified())));
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh the current player's subscription status against Google Play records.
     */
    @PostMapping("/refresh")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<SubscriptionEntitlementDto> refreshSubscription(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        AuthClaims claims = sessionSecurityService.verifyBearerToken(authHeader);
        if (!claims.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    subscriptionService.toDto(SubscriptionEntitlement.free("anonymous"))
            );
        }

        SubscriptionEntitlement entitlement = subscriptionService.getEntitlementForAccount(claims.playerId());
        return ResponseEntity.ok(subscriptionService.toDto(entitlement));
    }

    /**
     * Restore existing purchases associated with the authenticated account.
     */
    @PostMapping("/restore")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.SENSITIVE)
    public ResponseEntity<SubscriptionRestoreResponse> restorePurchases(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody SubscriptionRestoreRequest request
    ) {
        AuthClaims claims = sessionSecurityService.verifyBearerToken(authHeader);
        if (!claims.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new SubscriptionRestoreResponse(
                            false,
                            "Authentication required to restore purchases",
                            subscriptionService.toDto(SubscriptionEntitlement.free("anonymous"))
                    )
            );
        }

        SubscriptionRestoreResponse response = subscriptionService.restorePurchases(claims.playerId(), request);
        auditLogger.logEvent(SecurityEventType.ENTITLEMENT_VERIFIED, claims.playerId(),
                java.util.Map.of("action", "RESTORE", "restored", String.valueOf(response.restored())));
        return ResponseEntity.ok(response);
    }
}
