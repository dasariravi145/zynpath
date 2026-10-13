package com.zynpath.backend.persistence.repository;

import com.zynpath.backend.persistence.entity.SyncOperationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for SyncOperationEntity.
 * Durable log for client synchronization operations and idempotency receipts.
 */
@Repository
public interface SyncOperationRepository extends JpaRepository<SyncOperationEntity, String> {

    List<SyncOperationEntity> findByPlayerIdOrderByServerTimestampMsDesc(String playerId);

    void deleteByPlayerId(String playerId);
}
