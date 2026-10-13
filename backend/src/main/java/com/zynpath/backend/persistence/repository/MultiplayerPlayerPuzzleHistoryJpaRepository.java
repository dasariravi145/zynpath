package com.zynpath.backend.persistence.repository;

import com.zynpath.backend.persistence.entity.MultiplayerPlayerPuzzleHistoryJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for MultiplayerPlayerPuzzleHistoryJpaEntity.
 */
@Repository
public interface MultiplayerPlayerPuzzleHistoryJpaRepository extends JpaRepository<MultiplayerPlayerPuzzleHistoryJpaEntity, Long> {

    @Query("SELECT h.fingerprint FROM MultiplayerPlayerPuzzleHistoryJpaEntity h " +
           "WHERE h.playerId = :playerId ORDER BY h.assignedAt DESC")
    List<String> findRecentFingerprints(
            @Param("playerId") String playerId,
            Pageable pageable
    );
}
