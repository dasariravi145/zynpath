package com.zynpath.backend.auth.controller;

import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.auth.model.AuthLinkRequest;
import com.zynpath.backend.auth.model.AuthResponse;
import com.zynpath.backend.auth.model.AuthTokenExchangeRequest;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.model.PlayerSession;
import com.zynpath.backend.auth.model.VerifiedProviderIdentity;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.auth.service.SessionSecurityService;
import com.zynpath.backend.auth.service.TokenVerificationService;
import com.zynpath.backend.security.annotation.RequireAccess;
import com.zynpath.backend.security.audit.SecurityAuditLogger;
import com.zynpath.backend.security.audit.SecurityEventType;
import com.zynpath.backend.security.model.EndpointAccessTier;
import com.zynpath.backend.security.ratelimit.RateLimitPolicy;
import com.zynpath.backend.security.ratelimit.RateLimited;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller for Zynpath authentication, credential exchange, guest linking, and session management.
 *
 * Implements Prompt 18 Section 26 and Prompt 36 Sections 9, 10, 11, 12, 14, 15:
 * - /api/v1/auth/exchange: Authenticate via Google or Facebook token
 * - /api/v1/auth/link: Link guest account to external provider identity
 * - /api/v1/auth/me: Retrieve current authenticated player account
 * - /api/v1/auth/signout: Revoke active session token
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final TokenVerificationService tokenVerificationService;
    private final PlayerAccountService playerAccountService;
    private final SessionSecurityService sessionSecurityService;
    private final com.zynpath.backend.notification.service.NotificationService notificationService;
    private final SecurityAuditLogger auditLogger;

    public AuthController(
            TokenVerificationService tokenVerificationService,
            PlayerAccountService playerAccountService,
            SessionSecurityService sessionSecurityService,
            @org.springframework.context.annotation.Lazy com.zynpath.backend.notification.service.NotificationService notificationService,
            SecurityAuditLogger auditLogger
    ) {
        this.tokenVerificationService = tokenVerificationService;
        this.playerAccountService = playerAccountService;
        this.sessionSecurityService = sessionSecurityService;
        this.notificationService = notificationService;
        this.auditLogger = auditLogger;
    }

    /**
     * Exchanges a verified Google or Facebook provider token for a Zynpath application session.
     */
    @PostMapping("/exchange")
    @RequireAccess(EndpointAccessTier.PUBLIC)
    @RateLimited(RateLimitPolicy.AUTH)
    public ResponseEntity<AuthResponse> exchangeToken(@Valid @RequestBody AuthTokenExchangeRequest request) {
        log.info("Processing auth exchange for provider={}", request.provider());

        // 1. Backend provider verification
        VerifiedProviderIdentity verifiedIdentity = tokenVerificationService.verifyProviderToken(
                request.provider(),
                request.providerToken()
        );

        // 2. Resolve or create player account
        PlayerAccount account = playerAccountService.getOrCreateForExternalIdentity(
                verifiedIdentity,
                request.guestUuid()
        );

        // 3. Issue application session token
        PlayerSession session = sessionSecurityService.createSession(account.playerId());

        auditLogger.logEvent(SecurityEventType.LOGIN_SUCCESS, account.playerId(),
                Map.of("provider", request.provider().name(), "accountType", String.valueOf(account.accountType())));

        AuthResponse response = new AuthResponse(
                session.sessionToken(),
                account.playerId(),
                account.publicZynpathId(),
                account.displayName(),
                account.accountType(),
                session.expiresAt()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Links an existing guest identity to a third-party provider identity.
     * Preserves guest progress and fails gracefully with conflict error if the provider is already linked.
     */
    @PostMapping("/link")
    @RequireAccess(EndpointAccessTier.PUBLIC)
    @RateLimited(RateLimitPolicy.AUTH)
    public ResponseEntity<AuthResponse> linkAccount(@Valid @RequestBody AuthLinkRequest request) {
        log.info("Processing account link for provider={}, guestUuid={}", request.provider(), request.guestUuid());

        // 1. Backend provider verification
        VerifiedProviderIdentity verifiedIdentity = tokenVerificationService.verifyProviderToken(
                request.provider(),
                request.providerToken()
        );

        // 2. Link account with uniqueness check
        PlayerAccount account = playerAccountService.linkGuestAccount(
                verifiedIdentity,
                request.guestUuid(),
                request.displayName()
        );

        // 3. Issue application session token
        PlayerSession session = sessionSecurityService.createSession(account.playerId());

        auditLogger.logEvent(SecurityEventType.ACCOUNT_LINKED, account.playerId(),
                Map.of("provider", request.provider().name(), "guestUuid", String.valueOf(request.guestUuid())));

        AuthResponse response = new AuthResponse(
                session.sessionToken(),
                account.playerId(),
                account.publicZynpathId(),
                account.displayName(),
                account.accountType(),
                session.expiresAt()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the profile information for the current session.
     */
    @GetMapping("/me")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<AuthResponse> getCurrentPlayer(@RequestHeader("Authorization") String authHeader) {
        PlayerSession session = sessionSecurityService.validateSession(authHeader);
        PlayerAccount account = playerAccountService.findById(session.playerId())
                .orElseThrow(() -> AuthException.sessionExpired("Player account not found"));

        AuthResponse response = new AuthResponse(
                session.sessionToken(),
                account.playerId(),
                account.publicZynpathId(),
                account.displayName(),
                account.accountType(),
                session.expiresAt()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Refreshes an existing session token, rotating and extending expiration.
     * Implements Prompt 38 Section 30.
     */
    @PostMapping("/refresh")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    @RateLimited(RateLimitPolicy.AUTH)
    public ResponseEntity<AuthResponse> refreshSession(@RequestHeader("Authorization") String authHeader) {
        PlayerSession newSession = sessionSecurityService.refreshSession(authHeader);
        PlayerAccount account = playerAccountService.findById(newSession.playerId())
                .orElseThrow(() -> AuthException.sessionExpired("Player account not found"));

        auditLogger.logEvent(SecurityEventType.LOGIN_SUCCESS, account.playerId(),
                Map.of("action", "SESSION_REFRESHED", "accountType", String.valueOf(account.accountType())));

        AuthResponse response = new AuthResponse(
                newSession.sessionToken(),
                account.playerId(),
                account.publicZynpathId(),
                account.displayName(),
                account.accountType(),
                newSession.expiresAt()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Revokes the current session upon sign-out.
     */
    @PostMapping("/signout")
    @RequireAccess(EndpointAccessTier.AUTHENTICATED)
    public ResponseEntity<Map<String, String>> signOut(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && !authHeader.isBlank()) {
            try {
                PlayerSession session = sessionSecurityService.validateSession(authHeader);
                if (session != null) {
                    notificationService.cleanupAccountNotifications(session.playerId());
                    auditLogger.logEvent(SecurityEventType.LOGOUT, session.playerId(), Map.of());
                }
            } catch (Exception ignored) {}
            sessionSecurityService.revokeSession(authHeader);
        }
        return ResponseEntity.ok(Map.of("status", "SIGNED_OUT"));
    }
}
