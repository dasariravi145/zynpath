package com.zynpath.backend.persistence.repository;

import com.zynpath.backend.persistence.entity.PlayerSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for PlayerSessionEntity.
 */
@Repository
public interface PlayerSessionRepository extends JpaRepository<PlayerSessionEntity, String> {

    Optional<PlayerSessionEntity> findByTokenHash(String tokenHash);

    List<PlayerSessionEntity> findByPlayerId(String playerId);

    @Modifying
    @Query("UPDATE PlayerSessionEntity s SET s.isRevoked = true, s.revokedAt = :revokedAt WHERE s.playerId = :playerId AND s.isRevoked = false")
    int revokeAllForPlayer(@Param("playerId") String playerId, @Param("revokedAt") long revokedAt);

    @Modifying
    @Query("DELETE FROM PlayerSessionEntity s WHERE s.expiresAt < :cutoff")
    int purgeExpiredSessions(@Param("cutoff") long cutoff);
}
