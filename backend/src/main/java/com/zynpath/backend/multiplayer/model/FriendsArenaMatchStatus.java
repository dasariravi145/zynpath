package com.zynpath.backend.multiplayer.model;

/**
 * Authoritative lifecycle states for a Friends Arena multiplayer match (Prompt 19).
 */
public enum FriendsArenaMatchStatus {
    COUNTDOWN,
    ACTIVE,
    COMPLETING,
    COMPLETED,
    CANCELLED
}
