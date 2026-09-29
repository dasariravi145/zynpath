package com.zynpath.backend.notification.service;

import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.notification.model.DevicePushRegistration;
import com.zynpath.backend.notification.model.NotificationDto;
import com.zynpath.backend.notification.model.NotificationEventType;
import com.zynpath.backend.notification.model.NotificationPreference;
import com.zynpath.backend.notification.model.PlayerNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Modular backend notification service.
 *
 * Implements Prompt 31 Section 21, 22, 23, 47, 50, 52:
 * - Event intake, recipient validation, preference checks.
 * - Idempotency and deduplication (rate limiting).
 * - Push notification dispatch and device token association.
 * - Account isolation on sign-out / switching.
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final long DEDUPLICATION_WINDOW_MS = 5 * 60 * 1000L; // 5 minutes

    private final PlayerAccountService playerAccountService;
    private final PushNotificationGateway pushGateway;
    private final com.zynpath.backend.social.service.SocialService socialService;

    // Concurrent thread-safe in-memory stores
    private final Map<String, PlayerNotification> notificationsById = new ConcurrentHashMap<>();
    private final Map<String, List<String>> notificationsByRecipient = new ConcurrentHashMap<>();
    private final Map<String, NotificationPreference> preferencesByPlayerId = new ConcurrentHashMap<>();
    private final Map<String, List<DevicePushRegistration>> pushRegistrationsByPlayerId = new ConcurrentHashMap<>();
    private final Map<String, Long> lastNotificationTimeBySenderRecipient = new ConcurrentHashMap<>();
    private final List<com.zynpath.backend.notification.model.NotificationDeliveryEvent> deliveryAuditEvents = new CopyOnWriteArrayList<>();

    public NotificationService(
            PlayerAccountService playerAccountService,
            PushNotificationGateway pushGateway,
            @org.springframework.context.annotation.Lazy com.zynpath.backend.social.service.SocialService socialService
    ) {
        this.playerAccountService = playerAccountService;
        this.pushGateway = pushGateway;
        this.socialService = socialService;
    }

    /**
     * Backward-compatible overload without explicit sender.
     */
    public PlayerNotification createNotification(
            String recipientPlayerId,
            NotificationEventType eventType,
            String title,
            String message,
            String relatedResourceId,
            String actionDestination,
            Long expiresAt
    ) {
        return createNotification(recipientPlayerId, eventType, title, message, relatedResourceId, actionDestination, expiresAt, null);
    }

    /**
     * Creates and dispatches an in-app and remote push notification if eligible,
     * enforcing privacy, blocked relationships, and rate limits.
     *
     * Implements Prompt 31 Section 21, 22, 23, 52, 53:
     * - Backend block enforcement prevents alerts from blocked users.
     * - Rate limits prevent notification spam.
     * - Idempotency suppresses duplicate alerts within deduplication window.
     */
    public PlayerNotification createNotification(
            String recipientPlayerId,
            NotificationEventType eventType,
            String title,
            String message,
            String relatedResourceId,
            String actionDestination,
            Long expiresAt,
            String senderPlayerId
    ) {
        if (recipientPlayerId == null || recipientPlayerId.isBlank()) {
            log.warn("Cannot create notification: invalid recipientPlayerId");
            return null;
        }

        // 1. Verify recipient account exists
        if (playerAccountService.findById(recipientPlayerId).isEmpty()) {
            log.debug("Recipient {} does not exist, suppressing notification", recipientPlayerId);
            recordDeliveryEvent(null, recipientPlayerId, "IN_APP", "SUPPRESSED_ACCOUNT_MISSING", "Recipient does not exist");
            return null;
        }

        // 2. Enforce block relationships on the backend (Section 53)
        if (senderPlayerId != null && socialService != null) {
            try {
                if (socialService.isBlocked(senderPlayerId, recipientPlayerId) || socialService.isBlocked(recipientPlayerId, senderPlayerId)) {
                    log.info("Notification {} from {} to {} suppressed due to block relationship", eventType, senderPlayerId, recipientPlayerId);
                    recordDeliveryEvent(null, recipientPlayerId, "IN_APP", "SUPPRESSED_BLOCKED", "Blocked player relationship");
                    return null;
                }
            } catch (Exception e) {
                log.warn("Failed checking block relationship between {} and {}: {}", senderPlayerId, recipientPlayerId, e.getMessage());
            }
        }

        // 3. Check user notification preferences (Section 50)
        NotificationPreference preference = getPreferences(recipientPlayerId);
        if (!isNotificationAllowedByPreference(preference, eventType)) {
            log.debug("Notification {} suppressed for {} due to user preference", eventType, recipientPlayerId);
            recordDeliveryEvent(null, recipientPlayerId, "IN_APP", "SUPPRESSED_PREFERENCE", "User disabled category " + eventType);
            return null;
        }

        long now = System.currentTimeMillis();

        // 4. Rate Limiting (Section 52): max 1 alert per 5 seconds per (sender, recipient, eventType)
        if (senderPlayerId != null) {
            String rateKey = senderPlayerId + ":" + recipientPlayerId + ":" + eventType;
            Long lastSent = lastNotificationTimeBySenderRecipient.get(rateKey);
            if (lastSent != null && (now - lastSent) < 5000L) {
                log.warn("Rate limiting notification {} from {} to {}", eventType, senderPlayerId, recipientPlayerId);
                recordDeliveryEvent(null, recipientPlayerId, "IN_APP", "SUPPRESSED_RATE_LIMIT", "Rate limit exceeded (5s window)");
                return null;
            }
            lastNotificationTimeBySenderRecipient.put(rateKey, now);
        }

        // 5. Deduplication (Section 23): Check if active unread notification exists for same resource
        synchronized (this) {
            List<String> userNotificationIds = notificationsByRecipient.getOrDefault(recipientPlayerId, List.of());
            for (String notifId : userNotificationIds) {
                PlayerNotification existing = notificationsById.get(notifId);
                if (existing != null && !existing.isRead() && !existing.isExpired()) {
                    boolean sameEvent = existing.eventType() == eventType;
                    boolean sameResource = relatedResourceId != null && relatedResourceId.equals(existing.relatedResourceId());
                    boolean withinWindow = (now - existing.createdAt()) < DEDUPLICATION_WINDOW_MS;

                    if (sameEvent && sameResource && withinWindow) {
                        log.debug("Suppressed duplicate notification {} for user {} resource {}", eventType, recipientPlayerId, relatedResourceId);
                        recordDeliveryEvent(existing.id(), recipientPlayerId, "IN_APP", "SUPPRESSED_DUPLICATE", "Deduplicated active notification");
                        return existing;
                    }
                }
            }

            // 6. Create and persist in-app notification
            String notificationId = "notif_" + UUID.randomUUID().toString().substring(0, 12);
            PlayerNotification notification = new PlayerNotification(
                    notificationId,
                    recipientPlayerId,
                    eventType,
                    title,
                    message,
                    relatedResourceId,
                    actionDestination,
                    now,
                    expiresAt,
                    false
            );

            notificationsById.put(notificationId, notification);
            notificationsByRecipient.computeIfAbsent(recipientPlayerId, k -> new CopyOnWriteArrayList<>()).add(notificationId);

            recordDeliveryEvent(notificationId, recipientPlayerId, "IN_APP", "DELIVERED", "Stored in-app notification");
            log.info("Created in-app notification: id={}, type={}, recipient={}", notificationId, eventType, recipientPlayerId);

            // 7. Dispatch remote push if registered devices exist
            dispatchPushForNotification(notification);

            return notification;
        }
    }

    private void recordDeliveryEvent(String notificationId, String recipientPlayerId, String channel, String status, String detail) {
        String eventId = "deliv_" + UUID.randomUUID().toString().substring(0, 10);
        deliveryAuditEvents.add(new com.zynpath.backend.notification.model.NotificationDeliveryEvent(
                eventId,
                notificationId,
                recipientPlayerId,
                channel,
                status,
                System.currentTimeMillis(),
                detail
        ));
    }

    private boolean isNotificationAllowedByPreference(NotificationPreference pref, NotificationEventType type) {
        return switch (type) {
            case FRIEND_REQUEST, FRIEND_REQUEST_ACCEPTED -> pref.friendAlertsEnabled();
            case FRIEND_DUEL_INVITATION, FRIEND_DUEL_INVITATION_ACCEPTED, FRIEND_DUEL_INVITATION_DECLINED,
                 MINI_LEAGUE_INVITATION, MINI_LEAGUE_READY, MATCH_STARTING, MATCH_RESULT -> pref.multiplayerAlertsEnabled();
            case DAILY_CHALLENGE_REMINDER -> pref.dailyReminderEnabled();
        };
    }

    private void dispatchPushForNotification(PlayerNotification notification) {
        List<DevicePushRegistration> devices = pushRegistrationsByPlayerId.get(notification.recipientPlayerId());
        if (devices == null || devices.isEmpty()) {
            log.debug("No registered push devices for player {}", notification.recipientPlayerId());
            return;
        }

        Map<String, String> dataPayload = Map.of(
                "notificationId", notification.id(),
                "eventType", notification.eventType().name(),
                "resourceId", notification.relatedResourceId() != null ? notification.relatedResourceId() : "",
                "destination", notification.actionDestination() != null ? notification.actionDestination() : ""
        );

        for (DevicePushRegistration device : devices) {
            pushGateway.sendPush(device.deviceToken(), notification.title(), notification.message(), dataPayload);
        }
    }

    public NotificationDto.NotificationSummaryResponse getNotifications(String playerId, int limit, int offset) {
        List<String> userNotificationIds = notificationsByRecipient.getOrDefault(playerId, List.of());
        List<PlayerNotification> active = new ArrayList<>();

        for (String id : userNotificationIds) {
            PlayerNotification notif = notificationsById.get(id);
            if (notif != null && !notif.isExpired()) {
                active.add(notif);
            }
        }

        // Sort descending by creation timestamp
        active.sort(Comparator.comparingLong(PlayerNotification::createdAt).reversed());

        int unreadCount = (int) active.stream().filter(n -> !n.isRead()).count();
        int totalCount = active.size();

        int start = Math.min(Math.max(offset, 0), totalCount);
        int end = Math.min(start + Math.max(limit, 1), totalCount);

        List<NotificationDto.NotificationItemDto> pagedItems = active.subList(start, end).stream()
                .map(NotificationDto.NotificationItemDto::fromDomain)
                .toList();

        return new NotificationDto.NotificationSummaryResponse(pagedItems, unreadCount, totalCount);
    }

    public int getUnreadCount(String playerId) {
        List<String> userNotificationIds = notificationsByRecipient.getOrDefault(playerId, List.of());
        int count = 0;
        for (String id : userNotificationIds) {
            PlayerNotification notif = notificationsById.get(id);
            if (notif != null && !notif.isRead() && !notif.isExpired()) {
                count++;
            }
        }
        return count;
    }

    public PlayerNotification markAsRead(String notificationId, String playerId) {
        PlayerNotification existing = notificationsById.get(notificationId);
        if (existing == null) {
            throw new AuthException("NOTIFICATION_NOT_FOUND", "Notification not found", HttpStatus.NOT_FOUND);
        }
        if (!existing.recipientPlayerId().equals(playerId)) {
            throw new AuthException("NOT_AUTHORIZED", "Not authorized to modify this notification", HttpStatus.FORBIDDEN);
        }

        PlayerNotification updated = existing.withRead(true);
        notificationsById.put(notificationId, updated);
        return updated;
    }

    public int markAllAsRead(String playerId) {
        List<String> userNotificationIds = notificationsByRecipient.getOrDefault(playerId, List.of());
        int count = 0;
        for (String id : userNotificationIds) {
            PlayerNotification existing = notificationsById.get(id);
            if (existing != null && !existing.isRead()) {
                notificationsById.put(id, existing.withRead(true));
                count++;
            }
        }
        return count;
    }

    public NotificationPreference getPreferences(String playerId) {
        return preferencesByPlayerId.computeIfAbsent(playerId, NotificationPreference::createDefault);
    }

    public NotificationPreference updatePreferences(String playerId, NotificationDto.NotificationPreferenceDto dto) {
        NotificationPreference updated = new NotificationPreference(
                playerId,
                dto.friendAlertsEnabled(),
                dto.multiplayerAlertsEnabled(),
                dto.dailyReminderEnabled(),
                dto.dailyReminderHour(),
                dto.dailyReminderMinute()
        );
        preferencesByPlayerId.put(playerId, updated);
        log.info("Updated notification preferences for player: {}", playerId);
        return updated;
    }

    public void registerDeviceToken(String playerId, String deviceToken, String platform) {
        if (deviceToken == null || deviceToken.isBlank()) return;

        List<DevicePushRegistration> devices = pushRegistrationsByPlayerId.computeIfAbsent(playerId, k -> new CopyOnWriteArrayList<>());
        long now = System.currentTimeMillis();

        // Check if token already registered for this user
        for (int i = 0; i < devices.size(); i++) {
            if (devices.get(i).deviceToken().equals(deviceToken)) {
                devices.set(i, devices.get(i).withLastActive(now));
                return;
            }
        }

        // Clean up token if associated with other accounts on this physical device
        for (Map.Entry<String, List<DevicePushRegistration>> entry : pushRegistrationsByPlayerId.entrySet()) {
            if (!entry.getKey().equals(playerId)) {
                entry.getValue().removeIf(reg -> reg.deviceToken().equals(deviceToken));
            }
        }

        String regId = "push_" + UUID.randomUUID().toString().substring(0, 12);
        devices.add(new DevicePushRegistration(
                regId,
                playerId,
                deviceToken,
                platform != null ? platform : "ANDROID",
                now,
                now
        ));
        log.info("Registered device push token for player: {}", playerId);
    }

    public void unregisterDeviceToken(String playerId, String deviceToken) {
        List<DevicePushRegistration> devices = pushRegistrationsByPlayerId.get(playerId);
        if (devices != null && deviceToken != null) {
            devices.removeIf(reg -> reg.deviceToken().equals(deviceToken));
            log.info("Unregistered device push token for player: {}", playerId);
        }
    }

    public void cleanupAccountNotifications(String playerId) {
        pushRegistrationsByPlayerId.remove(playerId);
        log.info("Cleaned up push registrations for player on logout/account switch: {}", playerId);
    }
}
