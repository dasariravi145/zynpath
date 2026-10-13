package com.zynpath.backend.persistence.repository;

import com.zynpath.backend.persistence.entity.PlayerHintBalanceEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerHintBalanceRepository extends JpaRepository<PlayerHintBalanceEntity, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM PlayerHintBalanceEntity b WHERE b.playerId = :playerId")
    Optional<PlayerHintBalanceEntity> findByPlayerIdWithLock(@Param("playerId") String playerId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "INSERT INTO player_hint_balances (player_id, free_hints_remaining, rewarded_credits, total_available, last_updated_ms) " +
                   "VALUES (:playerId, 3, 0, 3, :nowMs) " +
                   "ON CONFLICT (player_id) DO NOTHING", nativeQuery = true)
    void insertDefaultBalanceIfNotExists(@Param("playerId") String playerId, @Param("nowMs") long nowMs);
}
