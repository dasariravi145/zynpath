package com.zynpath.backend.persistence.repository;

import com.zynpath.backend.persistence.entity.WalletTransactionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for WalletTransactionEntity.
 * Enforces transaction uniqueness via idempotency key lookup and supplies audit histories.
 */
@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransactionEntity, String> {

    Optional<WalletTransactionEntity> findByIdempotencyKey(String idempotencyKey);

    boolean existsByIdempotencyKey(String idempotencyKey);

    boolean existsByPlayerIdAndType(String playerId, String type);

    List<WalletTransactionEntity> findByPlayerIdOrderByCreatedAtMsDesc(String playerId, Pageable pageable);

    List<WalletTransactionEntity> findByPlayerId(String playerId);

    void deleteByPlayerId(String playerId);
}
