package com.zynpath.backend.social.model;

/**
 * Status of the relationship between two players.
 *
 * Implements Prompt 19 Section 11.
 */
public enum FriendRelationshipStatus {
    NONE,
    OUTGOING_REQUEST,
    INCOMING_REQUEST,
    FRIENDS,
    BLOCKED,
    SELF
}
