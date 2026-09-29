package com.zynpath.backend.notification.controller;

import com.zynpath.backend.auth.model.PlayerSession;
import com.zynpath.backend.auth.service.SessionSecurityService;
import com.zynpath.backend.notification.model.NotificationDto;
import com.zynpath.backend.notification.model.NotificationPreference;
import com.zynpath.backend.notification.model.PlayerNotification;
import com.zynpath.backend.notification.service.NotificationService;
import com.zynpath.backend.security.annotation.RequireAccess;
import com.zynpath.backend.security.audit.SecurityAuditLogger;
import com.zynpath.backend.security.audit.SecurityEventType;
import com.zynpath.backend.security.model.EndpointAccessTier;
import com.zynpath.backend.security.ratelimit.RateLimitPolicy;
import com.zynpath.backend.security.ratelimit.RateLimited;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller for player notification management, read states, preferences, and push token registration.
 *
 * Implements Prompt 31 Section 54 and Prompt 36 Sections 18, 50, 71:
 * - Secure authenticated endpoints matching Zynpath standard headers.
 * - Resource-level isolation of notification history.
 * - Bounded pagination and rate limiting.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequireAccess(EndpointAccessTier.AUTHENTICATED)
public class NotificationController {

    private final NotificationService notificationService;
    private final SessionSecurityService sessionSecurityService;
    private final SecurityAuditLogger auditLogger;

    public NotificationController(
            NotificationService notificationService,
            SessionSecurityService sessionSecurityService,
            SecurityAuditLogger auditLogger
    ) {
        this.notificationService = notificationService;
        this.sessionSecurityService = sessionSecurityService;
        this.auditLogger = auditLogger;
    }

    private String authenticatePlayer(String authHeader) {
        PlayerSession session = sessionSecurityService.validateSession(authHeader);
        return session.playerId();
    }

    @GetMapping
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<NotificationDto.NotificationSummaryResponse> getNotifications(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        String playerId = authenticatePlayer(authHeader);
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        int safeOffset = Math.max(offset, 0);
        return ResponseEntity.ok(notificationService.getNotifications(playerId, safeLimit, safeOffset));
    }

    @GetMapping("/unread-count")
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<Map<String, Integer>> getUnreadCount(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String playerId = authenticatePlayer(authHeader);
        int count = notificationService.getUnreadCount(playerId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @PutMapping("/{id}/read")
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<NotificationDto.NotificationItemDto> markAsRead(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String id
    ) {
        String playerId = authenticatePlayer(authHeader);
        PlayerNotification updated = notificationService.markAsRead(id, playerId);
        return ResponseEntity.ok(NotificationDto.NotificationItemDto.fromDomain(updated));
    }

    @PutMapping("/read-all")
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<Map<String, Integer>> markAllAsRead(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String playerId = authenticatePlayer(authHeader);
        int markedCount = notificationService.markAllAsRead(playerId);
        return ResponseEntity.ok(Map.of("markedReadCount", markedCount));
    }

    @GetMapping("/preferences")
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<NotificationDto.NotificationPreferenceDto> getPreferences(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String playerId = authenticatePlayer(authHeader);
        NotificationPreference pref = notificationService.getPreferences(playerId);
        return ResponseEntity.ok(NotificationDto.NotificationPreferenceDto.fromDomain(pref));
    }

    @PutMapping("/preferences")
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<NotificationDto.NotificationPreferenceDto> updatePreferences(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody NotificationDto.NotificationPreferenceDto request
    ) {
        String playerId = authenticatePlayer(authHeader);
        NotificationPreference updated = notificationService.updatePreferences(playerId, request);
        return ResponseEntity.ok(NotificationDto.NotificationPreferenceDto.fromDomain(updated));
    }

    @PostMapping("/push-token")
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<Map<String, String>> registerPushToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody NotificationDto.RegisterPushTokenRequest request
    ) {
        String playerId = authenticatePlayer(authHeader);
        notificationService.registerDeviceToken(playerId, request.deviceToken(), request.platform());
        auditLogger.logEvent(SecurityEventType.PUSH_TOKEN_REGISTERED, playerId,
                Map.of("platform", request.platform()));
        return ResponseEntity.ok(Map.of("status", "REGISTERED"));
    }

    @DeleteMapping("/push-token")
    @RateLimited(RateLimitPolicy.DEFAULT_API)
    public ResponseEntity<Map<String, String>> unregisterPushToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody NotificationDto.UnregisterPushTokenRequest request
    ) {
        String playerId = authenticatePlayer(authHeader);
        notificationService.unregisterDeviceToken(playerId, request.deviceToken());
        return ResponseEntity.ok(Map.of("status", "UNREGISTERED"));
    }
}
