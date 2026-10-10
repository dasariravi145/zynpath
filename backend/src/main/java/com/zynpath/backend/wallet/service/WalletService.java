package com.zynpath.backend.wallet.service;

import com.zynpath.backend.wallet.model.WalletBalance;
import com.zynpath.backend.wallet.model.WalletTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Authoritative virtual currency and wallet ledger service.
 * Enforces transaction deduplication via idempotency keys, maintains monotonic audit records,
 * and guarantees non-negative wallet balances.
 */
@Service
public class WalletService {

    private static final Logger log = LoggerFactory.getLogger(WalletService.class);
    private static final int WELCOME_COINS = 300;

    private final Map<String, WalletBalance> balances = new ConcurrentHashMap<>();
    private final Map<String, WalletTransaction> transactionsByIdempotencyKey = new ConcurrentHashMap<>();
    private final Map<String, List<WalletTransaction>> transactionsByPlayer = new ConcurrentHashMap<>();
    private final Map<String, ReentrantLock> playerLocks = new ConcurrentHashMap<>();

    private ReentrantLock getPlayerLock(String playerId) {
        return playerLocks.computeIfAbsent(playerId, k -> new ReentrantLock());
    }

    public WalletBalance getBalance(String playerId) {
        return balances.computeIfAbsent(playerId, id ->
                new WalletBalance(id, 0, 0, System.currentTimeMillis())
        );
    }

    public boolean ensureWelcomeReward(String playerId) {
        String idempotencyKey = "welcome_" + playerId;
        ReentrantLock lock = getPlayerLock(playerId);
        lock.lock();
        try {
            if (transactionsByIdempotencyKey.containsKey(idempotencyKey)) {
                return false;
            }
            // Check if player already received any welcome reward
            List<WalletTransaction> existing = transactionsByPlayer.getOrDefault(playerId, List.of());
            boolean alreadyHasWelcome = existing.stream().anyMatch(t -> "WELCOME_REWARD".equals(t.type()));
            if (alreadyHasWelcome) {
                return false;
            }

            WalletBalance current = getBalance(playerId);
            int newBalance = current.coinBalance() + WELCOME_COINS;
            long now = System.currentTimeMillis();

            WalletTransaction tx = new WalletTransaction(
                    "tx_" + UUID.randomUUID().toString().replace("-", ""),
                    playerId,
                    "WELCOME_REWARD",
                    WELCOME_COINS,
                    newBalance,
                    idempotencyKey,
                    "{\"note\":\"Genuine new player welcome gift\"}",
                    now
            );

            transactionsByIdempotencyKey.put(idempotencyKey, tx);
            transactionsByPlayer.computeIfAbsent(playerId, k -> new ArrayList<>()).add(0, tx);
            balances.put(playerId, new WalletBalance(playerId, newBalance, current.totalStars(), now));

            log.info("Welcome reward of {} coins granted to player {}", WELCOME_COINS, playerId);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public WalletBalance credit(String playerId, String type, int amount, String idempotencyKey, String metadataJson) {
        if (amount < 0) {
            throw new IllegalArgumentException("Credit amount must be non-negative");
        }

        ReentrantLock lock = getPlayerLock(playerId);
        lock.lock();
        try {
            WalletTransaction existing = transactionsByIdempotencyKey.get(idempotencyKey);
            if (existing != null) {
                return getBalance(playerId);
            }

            WalletBalance current = getBalance(playerId);
            int newBalance = current.coinBalance() + amount;
            long now = System.currentTimeMillis();

            WalletTransaction tx = new WalletTransaction(
                    "tx_" + UUID.randomUUID().toString().replace("-", ""),
                    playerId,
                    type,
                    amount,
                    newBalance,
                    idempotencyKey,
                    metadataJson != null ? metadataJson : "{}",
                    now
            );

            transactionsByIdempotencyKey.put(idempotencyKey, tx);
            transactionsByPlayer.computeIfAbsent(playerId, k -> new ArrayList<>()).add(0, tx);
            WalletBalance updated = new WalletBalance(playerId, newBalance, current.totalStars(), now);
            balances.put(playerId, updated);

            log.info("Credited {} coins to player {}. New balance: {}", amount, playerId, newBalance);
            return updated;
        } finally {
            lock.unlock();
        }
    }

    public WalletBalance debit(String playerId, String type, int amount, String idempotencyKey, String metadataJson) {
        if (amount < 0) {
            throw new IllegalArgumentException("Debit amount must be non-negative");
        }

        ReentrantLock lock = getPlayerLock(playerId);
        lock.lock();
        try {
            WalletTransaction existing = transactionsByIdempotencyKey.get(idempotencyKey);
            if (existing != null) {
                return getBalance(playerId);
            }

            WalletBalance current = getBalance(playerId);
            if (current.coinBalance() < amount) {
                throw new IllegalStateException("INSUFFICIENT_FUNDS: Current balance " + current.coinBalance() + " is less than required " + amount);
            }

            int newBalance = current.coinBalance() - amount;
            long now = System.currentTimeMillis();

            WalletTransaction tx = new WalletTransaction(
                    "tx_" + UUID.randomUUID().toString().replace("-", ""),
                    playerId,
                    type,
                    -amount,
                    newBalance,
                    idempotencyKey,
                    metadataJson != null ? metadataJson : "{}",
                    now
            );

            transactionsByIdempotencyKey.put(idempotencyKey, tx);
            transactionsByPlayer.computeIfAbsent(playerId, k -> new ArrayList<>()).add(0, tx);
            WalletBalance updated = new WalletBalance(playerId, newBalance, current.totalStars(), now);
            balances.put(playerId, updated);

            log.info("Debited {} coins from player {}. New balance: {}", amount, playerId, newBalance);
            return updated;
        } finally {
            lock.unlock();
        }
    }

    public List<WalletTransaction> getRecentTransactions(String playerId, int limit) {
        List<WalletTransaction> list = transactionsByPlayer.getOrDefault(playerId, List.of());
        return list.stream().limit(Math.max(1, limit)).toList();
    }

    public WalletBalance reassignGuestTransactions(String oldPlayerId, String newPlayerId) {
        if (oldPlayerId.equals(newPlayerId)) {
            return getBalance(newPlayerId);
        }

        ReentrantLock oldLock = getPlayerLock(oldPlayerId);
        ReentrantLock newLock = getPlayerLock(newPlayerId);

        // Lock ordering by ID to prevent deadlocks
        ReentrantLock firstLock = oldPlayerId.compareTo(newPlayerId) < 0 ? oldLock : newLock;
        ReentrantLock secondLock = oldPlayerId.compareTo(newPlayerId) < 0 ? newLock : oldLock;

        firstLock.lock();
        secondLock.lock();
        try {
            WalletBalance oldBalance = getBalance(oldPlayerId);
            WalletBalance newBalance = getBalance(newPlayerId);

            int combinedCoins = oldBalance.coinBalance() + newBalance.coinBalance();
            int combinedStars = Math.max(oldBalance.totalStars(), newBalance.totalStars());
            long now = System.currentTimeMillis();

            WalletBalance updated = new WalletBalance(newPlayerId, combinedCoins, combinedStars, now);
            balances.put(newPlayerId, updated);
            balances.put(oldPlayerId, new WalletBalance(oldPlayerId, 0, 0, now));

            List<WalletTransaction> oldTxs = transactionsByPlayer.remove(oldPlayerId);
            if (oldTxs != null) {
                transactionsByPlayer.computeIfAbsent(newPlayerId, k -> new ArrayList<>()).addAll(oldTxs);
            }

            log.info("Reassigned wallet from {} to {}. Combined balance: {}", oldPlayerId, newPlayerId, combinedCoins);
            return updated;
        } finally {
            secondLock.unlock();
            firstLock.unlock();
        }
    }
}
