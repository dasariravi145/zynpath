package com.zynpath.backend.social.model;

/**
 * State of an individual friend request.
 *
 * Implements Prompt 19 Section 12.
 */
public enum FriendRequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    CANCELLED
}
