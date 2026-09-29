package com.zynpath.backend.account.controller;

import com.zynpath.backend.account.model.AccountDeletionResultDto;
import com.zynpath.backend.account.model.AccountSettingsDto;
import com.zynpath.backend.account.model.DataExportDto;
import com.zynpath.backend.account.model.PlayerPrivacySettings;
import com.zynpath.backend.account.service.AccountManagementService;
import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.PlayerSession;
import com.zynpath.backend.auth.service.SessionSecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import com.zynpath.backend.security.annotation.RequireAccess;
import com.zynpath.backend.security.context.SecurityContext;
import com.zynpath.backend.security.model.EndpointAccessTier;
import com.zynpath.backend.security.ratelimit.RateLimitPolicy;
import com.zynpath.backend.security.ratelimit.RateLimited;

/**
 * REST controller for unified account settings, privacy preferences, provider linking,
 * data export, and self-service account deletion.
 *
 * Implements Prompt 32 Sections 5, 16-20, 24, 27-29, 39-51, 56, 57 & Prompt 36 Section 7, 8, 17, 18, 50, 69, 70.
 */
@RestController
@RequestMapping("/api/v1/account")
@RequireAccess(EndpointAccessTier.AUTHENTICATED)
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    private final AccountManagementService accountManagementService;
    private final SessionSecurityService sessionSecurityService;

    public AccountController(
            AccountManagementService accountManagementService,
            SessionSecurityService sessionSecurityService
    ) {
        this.accountManagementService = accountManagementService;
        this.sessionSecurityService = sessionSecurityService;
    }

    /**
     * Retrieves aggregated account details, privacy settings, and linked providers.
     */
    @GetMapping("/settings")
    public ResponseEntity<AccountSettingsDto> getSettings(
            @RequestHeader("Authorization") String authHeader
    ) {
        String playerId = authenticate(authHeader);
        AccountSettingsDto settings = accountManagementService.getAccountSettings(playerId);
        return ResponseEntity.ok(settings);
    }

    /**
     * Updates privacy settings for the calling player.
     */
    @PutMapping("/privacy")
    public ResponseEntity<PlayerPrivacySettings> updatePrivacy(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PlayerPrivacySettings newSettings
    ) {
        String playerId = authenticate(authHeader);
        PlayerPrivacySettings updated = accountManagementService.updatePrivacySettings(playerId, newSettings);
        return ResponseEntity.ok(updated);
    }

    /**
     * Lists all authentication providers linked to the account.
     */
    @GetMapping("/providers")
    public ResponseEntity<List<AuthProvider>> getLinkedProviders(
            @RequestHeader("Authorization") String authHeader
    ) {
        String playerId = authenticate(authHeader);
        List<AuthProvider> providers = accountManagementService.getLinkedProviders(playerId);
        return ResponseEntity.ok(providers);
    }

    /**
     * Unlinks an authentication provider from the account.
     * Rejects request if it is the only remaining sign-in method.
     */
    @DeleteMapping("/providers/{provider}")
    public ResponseEntity<List<AuthProvider>> unlinkProvider(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("provider") String providerStr
    ) {
        String playerId = authenticate(authHeader);
        AuthProvider provider = AuthProvider.valueOf(providerStr.toUpperCase());
        accountManagementService.unlinkProvider(playerId, provider);
        List<AuthProvider> remaining = accountManagementService.getLinkedProviders(playerId);
        return ResponseEntity.ok(remaining);
    }

    /**
     * Generates a portable, schema-versioned data export ("Request My Data").
     */
    @PostMapping("/export")
    @RateLimited(RateLimitPolicy.SENSITIVE)
    public ResponseEntity<DataExportDto> exportData(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String playerId = authenticate(authHeader);
        DataExportDto export = accountManagementService.exportAccountData(playerId);
        return ResponseEntity.ok(export);
    }

    /**
     * Authoritative account deletion.
     * Irreversibly purges personal data, social links, notifications, and sessions.
     */
    @org.springframework.web.bind.annotation.RequestMapping(
            value = "/delete",
            method = {org.springframework.web.bind.annotation.RequestMethod.POST, org.springframework.web.bind.annotation.RequestMethod.DELETE}
    )
    @RateLimited(RateLimitPolicy.SENSITIVE)
    public ResponseEntity<AccountDeletionResultDto> deleteAccount(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String playerId = authenticate(authHeader);
        AccountDeletionResultDto result = accountManagementService.deleteAccount(playerId);
        return ResponseEntity.ok(result);
    }

    private String authenticate(String authHeader) {
        if (SecurityContext.isAuthenticated()) {
            return SecurityContext.requirePlayerId();
        }
        PlayerSession session = sessionSecurityService.validateSession(authHeader);
        return session.playerId();
    }
}
