package com.zynpath.backend.account.model;

/**
 * Result outcome of an account deletion workflow.
 *
 * Implements Prompt 32 Sections 36, 44-51:
 * - Clear deletion status confirmation
 * - Explicit notice that Google Play subscriptions must be managed in the Play Store
 */
public record AccountDeletionResultDto(
    String status,
    String playerId,
    long deletedAt,
    String message,
    String subscriptionNotice
) {}
