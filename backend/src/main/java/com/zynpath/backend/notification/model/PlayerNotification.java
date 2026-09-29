package com.zynpath.backend.notification.model;

/**
 * Immutable in-app notification record.
 *
 * Implements Prompt 31 Section 8:
 * - Specific notification ID, recipient player ID, event type, resource ID, timestamps, read status.
 * - Excludes unnecessary private message content.
 */
public record PlayerNotification(
        String id,
        String recipientPlayerId,
        NotificationEventType eventType,
        String title,
        String message,
        String relatedResourceId,
        String actionDestination,
        long createdAt,
        Long expiresAt,
        boolean isRead
) {
    public boolean isExpired() {
        return expiresAt != null && System.currentTimeMillis() > expiresAt;
    }

    public PlayerNotification withRead(boolean read) {
        return new PlayerNotification(
                id,
                recipientPlayerId,
                eventType,
                title,
                message,
                relatedResourceId,
                actionDestination,
                createdAt,
                expiresAt,
                read
        );
    }
}
