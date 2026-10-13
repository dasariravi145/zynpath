package com.zynpath.backend.persistence.adapter;

import com.zynpath.backend.persistence.entity.PlayerWalletBalanceEntity;
import com.zynpath.backend.persistence.entity.WalletTransactionEntity;
import com.zynpath.backend.persistence.port.WalletPersistencePort;
import com.zynpath.backend.persistence.repository.PlayerWalletBalanceRepository;
import com.zynpath.backend.persistence.repository.WalletTransactionRepository;
import com.zynpath.backend.wallet.model.WalletBalance;
import com.zynpath.backend.wallet.model.WalletTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * PostgreSQL JPA adapter for WalletPersistencePort.
 * Provides ACID transactions, pessimistic write locking (SELECT ... FOR UPDATE),
 * idempotent transaction deduplication, and database-enforced non-negative balance checks.
 * Active when zynpath.persistence.mode=jpa.
 */
@Repository
@ConditionalOnProperty(name = "zynpath.persistence.mode", havingValue = "jpa")
public class JpaWalletPersistenceAdapter implements WalletPersistencePort {

    private static final Logger log = LoggerFactory.getLogger(JpaWalletPersistenceAdapter.class);

    private final PlayerWalletBalanceRepository balanceRepository;
    private final WalletTransactionRepository transactionRepository;

    public JpaWalletPersistenceAdapter(
            PlayerWalletBalanceRepository balanceRepository,
            WalletTransactionRepository transactionRepository
    ) {
        this.balanceRepository = balanceRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public WalletBalance getBalance(String playerId) {
        return balanceRepository.findById(playerId)
                .map(this::toDomainBalance)
                .orElseGet(() -> new WalletBalance(playerId, 0, 0, System.currentTimeMillis()));
    }

    @Override
    @Transactional
    public boolean ensureWelcomeReward(String playerId, int welcomeCoins) {
        String idempotencyKey = "welcome_" + playerId;

        // 1. Check idempotency key first
        if (transactionRepository.existsByIdempotencyKey(idempotencyKey)) {
            return false;
        }

        // 2. Check if player already received any welcome reward
        if (transactionRepository.existsByPlayerIdAndType(playerId, "WELCOME_REWARD")) {
            return false;
        }

        // 3. Acquire pessimistic lock and credit
        credit(playerId, "WELCOME_REWARD", welcomeCoins, idempotencyKey, "{\"note\":\"Genuine new player welcome gift\"}");
        log.info("PostgreSQL JPA: Welcome reward of {} coins granted to player {}", welcomeCoins, playerId);
        return true;
    }

    @Override
    @Transactional
    public WalletBalance credit(String playerId, String type, int amount, String idempotencyKey, String metadataJson) {
        if (amount < 0) {
            throw new IllegalArgumentException("Credit amount must be non-negative");
        }

        // Idempotent deduplication check
        Optional<WalletTransactionEntity> existingTx = transactionRepository.findByIdempotencyKey(idempotencyKey);
        if (existingTx.isPresent()) {
            log.info("PostgreSQL JPA: Idempotent deduplication for credit tx with key={}", idempotencyKey);
            return getBalance(playerId);
        }

        // Acquire pessimistic row lock on the player's balance
        long now = System.currentTimeMillis();
        PlayerWalletBalanceEntity balanceEntity = balanceRepository.findByIdForUpdate(playerId)
                .orElseGet(() -> new PlayerWalletBalanceEntity(playerId, 0, 0, now));

        int newBalance = balanceEntity.getCoinBalance() + amount;
        balanceEntity.setCoinBalance(newBalance);
        balanceEntity.setUpdatedAtMs(now);
        balanceRepository.save(balanceEntity);

        WalletTransactionEntity tx = new WalletTransactionEntity(
                "tx_" + UUID.randomUUID().toString().replace("-", ""),
                playerId,
                type,
                amount,
                newBalance,
                idempotencyKey,
                metadataJson != null ? metadataJson : "{}",
                now
        );
        transactionRepository.save(tx);

        log.info("PostgreSQL JPA: Credited {} coins to player {}. New balance: {}", amount, playerId, newBalance);
        return toDomainBalance(balanceEntity);
    }

    @Override
    @Transactional
    public WalletBalance debit(String playerId, String type, int amount, String idempotencyKey, String metadataJson) {
        if (amount < 0) {
            throw new IllegalArgumentException("Debit amount must be non-negative");
        }

        // Idempotent deduplication check
        Optional<WalletTransactionEntity> existingTx = transactionRepository.findByIdempotencyKey(idempotencyKey);
        if (existingTx.isPresent()) {
            log.info("PostgreSQL JPA: Idempotent deduplication for debit tx with key={}", idempotencyKey);
            return getBalance(playerId);
        }

        // Acquire pessimistic row lock
        long now = System.currentTimeMillis();
        PlayerWalletBalanceEntity balanceEntity = balanceRepository.findByIdForUpdate(playerId)
                .orElseGet(() -> new PlayerWalletBalanceEntity(playerId, 0, 0, now));

        if (balanceEntity.getCoinBalance() < amount) {
            throw new IllegalStateException("INSUFFICIENT_FUNDS: Current balance "
                    + balanceEntity.getCoinBalance() + " is less than required " + amount);
        }

        int newBalance = balanceEntity.getCoinBalance() - amount;
        balanceEntity.setCoinBalance(newBalance);
        balanceEntity.setUpdatedAtMs(now);
        balanceRepository.save(balanceEntity);

        WalletTransactionEntity tx = new WalletTransactionEntity(
                "tx_" + UUID.randomUUID().toString().replace("-", ""),
                playerId,
                type,
                -amount,
                newBalance,
                idempotencyKey,
                metadataJson != null ? metadataJson : "{}",
                now
        );
        transactionRepository.save(tx);

        log.info("PostgreSQL JPA: Debited {} coins from player {}. New balance: {}", amount, playerId, newBalance);
        return toDomainBalance(balanceEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletTransaction> getRecentTransactions(String playerId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, Math.max(1, limit));
        List<WalletTransactionEntity> entities = transactionRepository.findByPlayerIdOrderByCreatedAtMsDesc(playerId, pageRequest);
        return entities.stream().map(this::toDomainTransaction).toList();
    }

    @Override
    @Transactional
    public WalletBalance reassignGuestTransactions(String oldPlayerId, String newPlayerId) {
        if (oldPlayerId.equals(newPlayerId)) {
            return getBalance(newPlayerId);
        }

        // Lock rows in deterministic alphabetical order to avoid deadlocks
        String firstId = oldPlayerId.compareTo(newPlayerId) < 0 ? oldPlayerId : newPlayerId;
        String secondId = oldPlayerId.compareTo(newPlayerId) < 0 ? newPlayerId : oldPlayerId;

        balanceRepository.findByIdForUpdate(firstId);
        balanceRepository.findByIdForUpdate(secondId);

        long now = System.currentTimeMillis();
        PlayerWalletBalanceEntity oldEntity = balanceRepository.findById(oldPlayerId)
                .orElseGet(() -> new PlayerWalletBalanceEntity(oldPlayerId, 0, 0, now));
        PlayerWalletBalanceEntity newEntity = balanceRepository.findById(newPlayerId)
                .orElseGet(() -> new PlayerWalletBalanceEntity(newPlayerId, 0, 0, now));

        int combinedCoins = oldEntity.getCoinBalance() + newEntity.getCoinBalance();
        int combinedStars = Math.max(oldEntity.getTotalStars(), newEntity.getTotalStars());

        newEntity.setCoinBalance(combinedCoins);
        newEntity.setTotalStars(combinedStars);
        newEntity.setUpdatedAtMs(now);
        balanceRepository.save(newEntity);

        oldEntity.setCoinBalance(0);
        oldEntity.setUpdatedAtMs(now);
        balanceRepository.save(oldEntity);

        // Reassign transaction history
        List<WalletTransactionEntity> oldTransactions = transactionRepository.findByPlayerId(oldPlayerId);
        for (WalletTransactionEntity tx : oldTransactions) {
            tx.setPlayerId(newPlayerId);
        }
        transactionRepository.saveAll(oldTransactions);

        log.info("PostgreSQL JPA: Reassigned wallet from {} to {}. Combined balance: {}", oldPlayerId, newPlayerId, combinedCoins);
        return toDomainBalance(newEntity);
    }

    private WalletBalance toDomainBalance(PlayerWalletBalanceEntity entity) {
        return new WalletBalance(
                entity.getPlayerId(),
                entity.getCoinBalance(),
                entity.getTotalStars(),
                entity.getUpdatedAtMs()
        );
    }

    private WalletTransaction toDomainTransaction(WalletTransactionEntity entity) {
        return new WalletTransaction(
                entity.getTransactionId(),
                entity.getPlayerId(),
                entity.getType(),
                entity.getAmount(),
                entity.getBalanceAfter(),
                entity.getIdempotencyKey(),
                entity.getMetadataJson(),
                entity.getCreatedAtMs()
        );
    }
}
