package com.zynpath.backend.wallet.model;

/**
 * Authoritative wallet balance for a player.
 */
public record WalletBalance(
        String playerId,
        int coinBalance,
        int totalStars,
        long updatedAtMs
) {}
