package com.zynpath.backend.persistence.repository;

import com.zynpath.backend.persistence.entity.MultiplayerPuzzleAssignmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for MultiplayerPuzzleAssignmentJpaEntity.
 */
@Repository
public interface MultiplayerPuzzleAssignmentJpaRepository extends JpaRepository<MultiplayerPuzzleAssignmentJpaEntity, String> {

    Optional<MultiplayerPuzzleAssignmentJpaEntity> findByMatchId(String matchId);

    List<MultiplayerPuzzleAssignmentJpaEntity> findByPuzzleId(String puzzleId);
}
