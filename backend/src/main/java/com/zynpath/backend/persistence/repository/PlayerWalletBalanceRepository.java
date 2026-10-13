package com.zynpath.backend.persistence.repository;

import com.zynpath.backend.persistence.entity.PlayerWalletBalanceEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for PlayerWalletBalanceEntity.
 * Supports row-level pessimistic write locking for atomic credit/debit operations.
 */
@Repository
public interface PlayerWalletBalanceRepository extends JpaRepository<PlayerWalletBalanceEntity, String> {

    /**
     * Acquires an authoritative SELECT FOR UPDATE row lock on the player's wallet balance.
     * Prevents race conditions during concurrent credits, debits, or multiplayer settlements.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM PlayerWalletBalanceEntity b WHERE b.playerId = :playerId")
    Optional<PlayerWalletBalanceEntity> findByIdForUpdate(@Param("playerId") String playerId);
}
