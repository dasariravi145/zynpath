package com.zynpath.backend.persistence.repository;

import com.zynpath.backend.persistence.entity.ExternalIdentityEntity;
import com.zynpath.backend.persistence.entity.ExternalIdentityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for ExternalIdentityEntity.
 * Authoritative mapping between OAuth/Play Games identities and internal player IDs.
 */
@Repository
public interface ExternalIdentityRepository extends JpaRepository<ExternalIdentityEntity, ExternalIdentityId> {

    Optional<ExternalIdentityEntity> findByProviderAndProviderSubjectId(String provider, String providerSubjectId);

    List<ExternalIdentityEntity> findByPlayerId(String playerId);

    void deleteByPlayerId(String playerId);

    void deleteByPlayerIdAndProvider(String playerId, String provider);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "INSERT INTO external_identities (provider, provider_subject_id, player_id, linked_at) " +
            "VALUES ('GUEST', :guestUuid, :playerId, :linkedAt) " +
            "ON CONFLICT (provider, provider_subject_id) DO NOTHING", nativeQuery = true)
    int insertGuestIdentityIfNotExists(@Param("guestUuid") String guestUuid,
                                       @Param("playerId") String playerId,
                                       @Param("linkedAt") long linkedAt);
}
