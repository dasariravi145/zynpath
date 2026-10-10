package com.zynpath.backend.wallet.model;

/**
 * Immutable ledger entry recording virtual currency credits, debits, and adjustments.
 */
public record WalletTransaction(
        String transactionId,
        String playerId,
        String type,
        int amount,
        int balanceAfter,
        String idempotencyKey,
        String metadataJson,
        long createdAtMs
) {}
