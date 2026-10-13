package com.zynpath.backend.persistence.repository;

import com.zynpath.backend.persistence.entity.PlayerLevelProgressEntity;
import com.zynpath.backend.persistence.entity.PlayerLevelProgressId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for PlayerLevelProgressEntity.
 * Provides durable query and persistence for 1,800 Solo levels.
 */
@Repository
public interface PlayerLevelProgressRepository extends JpaRepository<PlayerLevelProgressEntity, PlayerLevelProgressId> {

    List<PlayerLevelProgressEntity> findByPlayerIdOrderByLevelIdAsc(String playerId);

    List<PlayerLevelProgressEntity> findByPlayerIdAndWorldIdOrderByLevelIdAsc(String playerId, int worldId);

    void deleteByPlayerId(String playerId);
}
