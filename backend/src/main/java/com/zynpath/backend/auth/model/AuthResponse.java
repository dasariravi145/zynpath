package com.zynpath.backend.auth.model;

/**
 * Standard authentication response returned after credential exchange, linking, or profile fetch.
 *
 * Implements Prompt 18 Section 14, 24 & 26.
 */
public record AuthResponse(
    String sessionToken,
    String playerId,
    String publicZynpathId,
    String displayName,
    String accountType,
    long expiresAt
) {}
