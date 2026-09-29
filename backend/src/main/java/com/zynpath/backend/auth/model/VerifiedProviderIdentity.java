package com.zynpath.backend.auth.model;

/**
 * Result of validating an external provider credential token.
 *
 * Implements Prompt 18 Section 11:
 * - Issuer or provider validated
 * - Audience / client ID validated
 * - Expiration and subject identifier verified
 */
public record VerifiedProviderIdentity(
    AuthProvider provider,
    String subjectId,
    String displayName,
    String email,
    boolean emailVerified
) {}
