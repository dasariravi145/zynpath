package com.zynpath.backend.social.model;

/**
 * Item in an authenticated player's friends list.
 *
 * Implements Prompt 19 Section 19.
 */
public record FriendSummary(
    String playerId,
    String publicZynpathId,
    String displayName,
    String avatarId,
    PlayerPresenceState presenceState,
    long friendsSince
) {}
