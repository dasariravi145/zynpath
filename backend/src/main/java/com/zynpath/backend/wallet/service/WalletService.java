package com.zynpath.backend.wallet.service;

import com.zynpath.backend.persistence.port.WalletPersistencePort;
import com.zynpath.backend.wallet.model.WalletBalance;
import com.zynpath.backend.wallet.model.WalletTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Authoritative virtual currency and wallet ledger service.
 * Enforces transaction deduplication via idempotency keys, maintains monotonic audit records,
 * and guarantees non-negative wallet balances.
 *
 * Delegates persistence operations to WalletPersistencePort (supporting both PostgreSQL JPA
 * and In-Memory storage adapters).
 */
@Service
public class WalletService {

    private static final Logger log = LoggerFactory.getLogger(WalletService.class);
    private static final int WELCOME_COINS = 300;

    private final WalletPersistencePort walletPersistencePort;

    public WalletService() {
        this(new com.zynpath.backend.persistence.adapter.InMemoryWalletPersistenceAdapter());
    }

    @org.springframework.beans.factory.annotation.Autowired
    public WalletService(WalletPersistencePort walletPersistencePort) {
        this.walletPersistencePort = walletPersistencePort;
    }

    public WalletBalance getBalance(String playerId) {
        return walletPersistencePort.getBalance(playerId);
    }

    public boolean ensureWelcomeReward(String playerId) {
        return walletPersistencePort.ensureWelcomeReward(playerId, WELCOME_COINS);
    }

    public WalletBalance credit(String playerId, String type, int amount, String idempotencyKey, String metadataJson) {
        return walletPersistencePort.credit(playerId, type, amount, idempotencyKey, metadataJson);
    }

    public WalletBalance debit(String playerId, String type, int amount, String idempotencyKey, String metadataJson) {
        return walletPersistencePort.debit(playerId, type, amount, idempotencyKey, metadataJson);
    }

    public List<WalletTransaction> getRecentTransactions(String playerId, int limit) {
        return walletPersistencePort.getRecentTransactions(playerId, limit);
    }

    public WalletBalance reassignGuestTransactions(String oldPlayerId, String newPlayerId) {
        return walletPersistencePort.reassignGuestTransactions(oldPlayerId, newPlayerId);
    }
}
