package com.zynpath.backend.persistence.repository;

import com.zynpath.backend.persistence.entity.GeneratedPuzzleJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for GeneratedPuzzleJpaEntity.
 */
@Repository
public interface GeneratedPuzzleJpaRepository extends JpaRepository<GeneratedPuzzleJpaEntity, String> {

    Optional<GeneratedPuzzleJpaEntity> findByFingerprint(String fingerprint);

    @Query("SELECT p FROM GeneratedPuzzleJpaEntity p WHERE " +
           "(p.gameMode = :gameMode OR p.gameMode = 'ANY') AND " +
           "p.difficultyTier = :tier AND " +
           "p.inventoryStatus = 'AVAILABLE' " +
           "ORDER BY p.createdAt ASC")
    List<GeneratedPuzzleJpaEntity> findAvailablePuzzles(
            @Param("gameMode") String gameMode,
            @Param("tier") String tier,
            Pageable pageable
    );

    @Query("SELECT COUNT(p) FROM GeneratedPuzzleJpaEntity p WHERE " +
           "(p.gameMode = :gameMode OR p.gameMode = 'ANY') AND " +
           "p.difficultyTier = :tier AND " +
           "p.inventoryStatus = 'AVAILABLE'")
    long countAvailablePuzzles(
            @Param("gameMode") String gameMode,
            @Param("tier") String tier
    );
}
