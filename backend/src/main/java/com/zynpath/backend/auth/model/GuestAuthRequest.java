package com.zynpath.backend.auth.model;

/**
 * Payload requesting an opaque guest session for Quick Duel matchmaking.
 */
public record GuestAuthRequest(
        String guestUuid,
        String displayName
) {}
