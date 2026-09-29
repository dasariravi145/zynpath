package com.zynpath.backend.social.model;

/**
 * Observed server-side presence states for online players.
 *
 * Implements Prompt 19 Section 29:
 * - ONLINE: Active connection or foreground heartbeat within lease window
 * - AWAY: Application paused / backgrounded
 * - OFFLINE: Disconnected or lease expired
 * - UNKNOWN: Connection or status cannot be verified
 */
public enum PlayerPresenceState {
    ONLINE,
    AWAY,
    OFFLINE,
    UNKNOWN
}
