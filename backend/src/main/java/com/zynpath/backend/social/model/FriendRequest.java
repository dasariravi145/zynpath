package com.zynpath.backend.social.model;

/**
 * Record of a friend request sent between players.
 *
 * Implements Prompt 19 Section 12:
 * - Server-generated requestId
 * - Server-validated sender and recipient player IDs
 * - Timestamps and status
 */
public record FriendRequest(
    String requestId,
    String senderPlayerId,
    String recipientPlayerId,
    FriendRequestStatus status,
    long createdAt,
    long updatedAt
) {}
