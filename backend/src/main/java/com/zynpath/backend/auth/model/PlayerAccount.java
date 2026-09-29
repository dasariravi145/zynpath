package com.zynpath.backend.auth.model;

/**
 * Authoritative backend player account.
 *
 * Implements Prompt 18 Section 12 & 24:
 * - Internal stable player identifier (distinct from provider subject ID)
 * - Globally unique public Zynpath ID suitable for friend discovery
 * - Account type and registration timestamps
 */
public record PlayerAccount(
    String playerId,
    String publicZynpathId,
    String displayName,
    String accountType,
    long createdAt,
    long lastActiveAt
) {}
