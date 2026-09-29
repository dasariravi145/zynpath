package com.zynpath.backend.social.service;

import com.zynpath.backend.social.model.PlayerPresenceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages ephemeral, in-memory player presence and activity heartbeats.
 *
 * Implements Prompt 19 Sections 29-35:
 * - Ephemeral in-memory presence storage (no permanent DB writes for heartbeats).
 * - Automatic expiration lease (60s for ONLINE, 120s for AWAY -> OFFLINE).
 * - Server-observed presence authority.
 */
@Service
public class PresenceService {

    private static final Logger log = LoggerFactory.getLogger(PresenceService.class);

    private static final long ONLINE_LEASE_MS = 60_000L;
    private static final long AWAY_LEASE_MS = 120_000L;

    private record PresenceEntry(
        PlayerPresenceState state,
        long lastSeenTimestamp
    ) {}

    private final Map<String, PresenceEntry> playerPresenceMap = new ConcurrentHashMap<>();

    /**
     * Updates presence state from an authenticated client heartbeat or connection event.
     */
    public void recordHeartbeat(String playerId, PlayerPresenceState state) {
        if (playerId == null || playerId.isBlank()) return;
        playerPresenceMap.put(playerId, new PresenceEntry(state, System.currentTimeMillis()));
        log.debug("Recorded presence for playerId={}: {}", playerId, state);
    }

    /**
     * Explicitly marks a player offline (e.g., upon sign-out or disconnect).
     */
    public void markOffline(String playerId) {
        if (playerId != null) {
            playerPresenceMap.put(playerId, new PresenceEntry(PlayerPresenceState.OFFLINE, System.currentTimeMillis()));
            log.debug("Marked playerId={} as OFFLINE", playerId);
        }
    }

    /**
     * Resolves the authoritative presence state for a player, applying time-based lease expiry.
     */
    public PlayerPresenceState getPresence(String playerId) {
        if (playerId == null || playerId.isBlank()) {
            return PlayerPresenceState.UNKNOWN;
        }

        PresenceEntry entry = playerPresenceMap.get(playerId);
        if (entry == null) {
            return PlayerPresenceState.OFFLINE;
        }

        long elapsed = System.currentTimeMillis() - entry.lastSeenTimestamp();

        return switch (entry.state()) {
            case ONLINE -> {
                if (elapsed > ONLINE_LEASE_MS) {
                    yield elapsed > AWAY_LEASE_MS ? PlayerPresenceState.OFFLINE : PlayerPresenceState.AWAY;
                }
                yield PlayerPresenceState.ONLINE;
            }
            case AWAY -> elapsed > AWAY_LEASE_MS ? PlayerPresenceState.OFFLINE : PlayerPresenceState.AWAY;
            case OFFLINE -> PlayerPresenceState.OFFLINE;
            case UNKNOWN -> PlayerPresenceState.UNKNOWN;
        };
    }
}
