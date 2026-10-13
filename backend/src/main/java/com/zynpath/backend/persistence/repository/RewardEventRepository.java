package com.zynpath.backend.persistence.repository;

import com.zynpath.backend.persistence.entity.RewardEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RewardEventRepository extends JpaRepository<RewardEventEntity, String> {
    Optional<RewardEventEntity> findByTransactionId(String transactionId);
    long countByPlayerIdAndGrantedAtMsGreaterThanEqual(String playerId, long sinceMs);
}
