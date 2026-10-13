package com.zynpath.backend.persistence.port;

import com.zynpath.backend.wallet.model.WalletBalance;
import com.zynpath.backend.wallet.model.WalletTransaction;

import java.util.List;

/**
 * Storage-agnostic persistence port for player wallets and immutable transaction ledgers.
 * Decouples domain logic from persistence technology (PostgreSQL JPA vs In-Memory).
 */
public interface WalletPersistencePort {

    WalletBalance getBalance(String playerId);

    boolean ensureWelcomeReward(String playerId, int welcomeCoins);

    WalletBalance credit(String playerId, String type, int amount, String idempotencyKey, String metadataJson);

    WalletBalance debit(String playerId, String type, int amount, String idempotencyKey, String metadataJson);

    List<WalletTransaction> getRecentTransactions(String playerId, int limit);

    WalletBalance reassignGuestTransactions(String oldPlayerId, String newPlayerId);
}
