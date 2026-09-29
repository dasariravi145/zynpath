package com.zynpath.backend.notification.model;

/**
 * Mobile device push notification registration mapping.
 *
 * Implements Prompt 31 Section 17 & 18:
 * - Associates device tokens securely with account identity.
 * - Private to server, never exposed via public APIs.
 */
public record DevicePushRegistration(
        String registrationId,
        String playerId,
        String deviceToken,
        String platform,
        long registeredAt,
        long lastActiveAt
) {
    public DevicePushRegistration withLastActive(long timestamp) {
        return new DevicePushRegistration(
                registrationId,
                playerId,
                deviceToken,
                platform,
                registeredAt,
                timestamp
        );
    }
}
