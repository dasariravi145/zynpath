package com.zynpath.backend.multiplayer.model;

import java.util.UUID;

/**
 * Standard versioned envelope for real-time WebSocket multiplayer messaging.
 *
 * Implements Prompt 20 Section 29 & 31:
 * - Versioned schema prevents protocol mismatch.
 * - Server timestamp & monotonic sequence number handle stale and out-of-order events.
 */
public record MultiplayerEventEnvelope(
    String eventId,
    MultiplayerEventType eventType,
    int schemaVersion,
    String matchId,
    long serverTimestamp,
    long sequenceNumber,
    Object payload
) {
    public static final int CURRENT_SCHEMA_VERSION = 1;

    public static MultiplayerEventEnvelope create(
            MultiplayerEventType eventType,
            String matchId,
            long sequenceNumber,
            Object payload
    ) {
        return new MultiplayerEventEnvelope(
                "evt_" + UUID.randomUUID().toString().substring(0, 12),
                eventType,
                CURRENT_SCHEMA_VERSION,
                matchId,
                System.currentTimeMillis(),
                sequenceNumber,
                payload
        );
    }
}
