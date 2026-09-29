package com.zynpath.backend.social.model;

/**
 * Summary view of an incoming or outgoing friend request.
 *
 * Implements Prompt 19 Sections 20 & 21.
 */
public record FriendRequestSummary(
    String requestId,
    String otherPlayerId,
    String otherPublicZynpathId,
    String otherDisplayName,
    String otherAvatarId,
    long createdAt
) {}
