package com.zynpath.backend.notification.model;

/**
 * Audit record of a notification delivery outcome.
 *
 * Implements Prompt 31 Section 21 & 55:
 * - Records delivery outcomes across in-app, system, and remote push channels.
 * - Tracks suppression reasons (user preferences, blocked relationships, rate limits, deduplication).
 */
public record NotificationDeliveryEvent(
        String eventId,
        String notificationId,
        String recipientPlayerId,
        String channel,
        String status,
        long attemptedAt,
        String detail
) {}
