package com.zynpath.backend.notification.model;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * Data Transfer Objects for the Notification REST API.
 */
public final class NotificationDto {

    private NotificationDto() {}

    public record NotificationItemDto(
            String id,
            String eventType,
            String title,
            String message,
            String relatedResourceId,
            String actionDestination,
            long createdAt,
            Long expiresAt,
            boolean isRead,
            boolean isExpired
    ) {
        public static NotificationItemDto fromDomain(PlayerNotification n) {
            return new NotificationItemDto(
                    n.id(),
                    n.eventType().name(),
                    n.title(),
                    n.message(),
                    n.relatedResourceId(),
                    n.actionDestination(),
                    n.createdAt(),
                    n.expiresAt(),
                    n.isRead(),
                    n.isExpired()
            );
        }
    }

    public record NotificationSummaryResponse(
            List<NotificationItemDto> notifications,
            int unreadCount,
            int totalCount
    ) {}

    public record NotificationPreferenceDto(
            boolean friendAlertsEnabled,
            boolean multiplayerAlertsEnabled,
            boolean dailyReminderEnabled,
            int dailyReminderHour,
            int dailyReminderMinute
    ) {
        public static NotificationPreferenceDto fromDomain(NotificationPreference p) {
            return new NotificationPreferenceDto(
                    p.friendAlertsEnabled(),
                    p.multiplayerAlertsEnabled(),
                    p.dailyReminderEnabled(),
                    p.dailyReminderHour(),
                    p.dailyReminderMinute()
            );
        }
    }

    public record RegisterPushTokenRequest(
            @NotBlank(message = "Device token is required")
            String deviceToken,
            String platform
    ) {}

    public record UnregisterPushTokenRequest(
            @NotBlank(message = "Device token is required")
            String deviceToken
    ) {}
}
