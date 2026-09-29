package com.zynpath.backend.notification.service;

import java.util.Map;

/**
 * Gateway interface for remote push notification delivery.
 *
 * Implements Prompt 31 Section 15 & 16:
 * - Abstracts the push provider boundary.
 * - Handles configuration availability check and delivery execution.
 */
public interface PushNotificationGateway {

    enum PushDeliveryStatus {
        DELIVERED,
        BLOCKED_BY_CONFIGURATION,
        FAILED,
        TOKEN_UNREGISTERED
    }

    record PushResult(
            PushDeliveryStatus status,
            String messageId,
            String errorMessage
    ) {
        public static PushResult success(String messageId) {
            return new PushResult(PushDeliveryStatus.DELIVERED, messageId, null);
        }

        public static PushResult blockedByConfiguration(String reason) {
            return new PushResult(PushDeliveryStatus.BLOCKED_BY_CONFIGURATION, null, reason);
        }

        public static PushResult failed(String error) {
            return new PushResult(PushDeliveryStatus.FAILED, null, error);
        }
    }

    /**
     * Attempts to send a push notification to a specific device token.
     */
    PushResult sendPush(
            String deviceToken,
            String title,
            String body,
            Map<String, String> dataPayload
    );

    /**
     * Checks if the push delivery provider is fully configured with credentials.
     */
    boolean isConfigured();
}
