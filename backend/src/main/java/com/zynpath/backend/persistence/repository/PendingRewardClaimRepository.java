package com.zynpath.backend.persistence.repository;

import com.zynpath.backend.persistence.entity.PendingRewardClaimEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PendingRewardClaimRepository extends JpaRepository<PendingRewardClaimEntity, String> {
    Optional<PendingRewardClaimEntity> findByTransactionId(String transactionId);
    long countByPlayerIdAndCreatedAtMsGreaterThanEqual(String playerId, long sinceMs);
}
