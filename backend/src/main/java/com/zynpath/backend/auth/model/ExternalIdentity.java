package com.zynpath.backend.auth.model;

/**
 * Association between an external identity provider subject and a Zynpath player account.
 *
 * Implements Prompt 18 Section 13:
 * - Provider and subject ID combination is unique
 * - Maps directly to an internal Zynpath player account
 */
public record ExternalIdentity(
    AuthProvider provider,
    String providerSubjectId,
    String playerId,
    long linkedAt
) {}
