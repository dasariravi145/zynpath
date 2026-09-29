package com.zynpath.backend.social.model;

/**
 * Safe, minimal public profile representation returned for player search and discovery.
 *
 * Implements Prompt 19 Sections 7, 9 & 10:
 * - Exposes only Public Zynpath ID, display name, avatar, and relationship status.
 * - Never returns email, provider subject ID, internal UUID, or session tokens.
 */
public record PublicPlayerProfile(
    String publicZynpathId,
    String displayName,
    String avatarId,
    FriendRelationshipStatus relationshipStatus,
    PlayerPresenceState presenceState
) {}
