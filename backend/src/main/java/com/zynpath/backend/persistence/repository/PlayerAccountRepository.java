package com.zynpath.backend.persistence.repository;

import com.zynpath.backend.persistence.entity.PlayerAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for PlayerAccountEntity.
 * Authoritative storage for core player accounts and Public Zynpath IDs.
 */
@Repository
public interface PlayerAccountRepository extends JpaRepository<PlayerAccountEntity, String> {

    Optional<PlayerAccountEntity> findByPublicZynpathId(String publicZynpathId);

    boolean existsByPublicZynpathId(String publicZynpathId);
}
