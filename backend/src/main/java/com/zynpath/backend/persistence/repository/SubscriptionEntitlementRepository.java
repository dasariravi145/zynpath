package com.zynpath.backend.persistence.repository;

import com.zynpath.backend.persistence.entity.SubscriptionEntitlementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for SubscriptionEntitlementEntity.
 * Authoritative storage for Google Play subscription entitlements and purchase token hashes.
 */
@Repository
public interface SubscriptionEntitlementRepository extends JpaRepository<SubscriptionEntitlementEntity, String> {

    Optional<SubscriptionEntitlementEntity> findByPurchaseTokenHash(String purchaseTokenHash);

    void deleteByAccountId(String accountId);
}
