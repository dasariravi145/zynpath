package com.zynpath.backend.social.model;

/**
 * Status of a multiplayer room invitation.
 *
 * Implements Prompt 19 Section 36 & 37.
 */
public enum InvitationStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    CANCELLED,
    EXPIRED,
    INVALIDATED
}
