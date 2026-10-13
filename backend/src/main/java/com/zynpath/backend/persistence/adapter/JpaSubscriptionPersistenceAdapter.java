package com.zynpath.backend.persistence.adapter;

import com.zynpath.backend.persistence.entity.SubscriptionEntitlementEntity;
import com.zynpath.backend.persistence.port.SubscriptionPersistencePort;
import com.zynpath.backend.persistence.repository.SubscriptionEntitlementRepository;
import com.zynpath.backend.subscription.model.EntitlementStatus;
import com.zynpath.backend.subscription.model.SubscriptionEntitlement;
import com.zynpath.backend.subscription.model.SubscriptionTier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * PostgreSQL JPA adapter for SubscriptionPersistencePort.
 * Provides durable storage for Google Play subscription entitlements and purchase token hashes.
 * Active when zynpath.persistence.mode=jpa.
 */
@Repository
@ConditionalOnProperty(name = "zynpath.persistence.mode", havingValue = "jpa")
public class JpaSubscriptionPersistenceAdapter implements SubscriptionPersistencePort {

    private static final Logger log = LoggerFactory.getLogger(JpaSubscriptionPersistenceAdapter.class);

    private final SubscriptionEntitlementRepository entitlementRepository;

    public JpaSubscriptionPersistenceAdapter(SubscriptionEntitlementRepository entitlementRepository) {
        this.entitlementRepository = entitlementRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionEntitlement getEntitlement(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            return SubscriptionEntitlement.free("anonymous");
        }

        return entitlementRepository.findById(accountId)
                .map(this::toDomain)
                .orElse(null);
    }

    @Override
    @Transactional
    public void saveEntitlement(SubscriptionEntitlement entitlement) {
        SubscriptionEntitlementEntity entity = new SubscriptionEntitlementEntity(
                entitlement.accountId(),
                entitlement.status().name(),
                entitlement.productId(),
                entitlement.basePlanId(),
                entitlement.currentPeriodEndMs(),
                entitlement.lastVerifiedAtMs(),
                entitlement.isAutoRenewing(),
                entitlement.purchaseTokenHash()
        );
        entitlementRepository.save(entity);
        log.info("PostgreSQL JPA: Saved subscription entitlement for accountId={}, status={}, expires={}",
                entitlement.accountId(), entitlement.status(), entitlement.currentPeriodEndMs());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> getAccountByTokenHash(String tokenHash) {
        return entitlementRepository.findByPurchaseTokenHash(tokenHash)
                .map(SubscriptionEntitlementEntity::getAccountId);
    }

    @Override
    @Transactional
    public void bindTokenHash(String tokenHash, String accountId) {
        entitlementRepository.findById(accountId).ifPresent(entity -> {
            entity.setPurchaseTokenHash(tokenHash);
            entitlementRepository.save(entity);
        });
    }

    @Override
    @Transactional
    public void deleteEntitlement(String accountId) {
        entitlementRepository.deleteByAccountId(accountId);
        log.info("PostgreSQL JPA: Deleted subscription entitlement for accountId={}", accountId);
    }

    private SubscriptionEntitlement toDomain(SubscriptionEntitlementEntity entity) {
        EntitlementStatus status;
        try {
            status = EntitlementStatus.valueOf(entity.getStatus());
        } catch (IllegalArgumentException e) {
            status = EntitlementStatus.FREE;
        }

        SubscriptionTier tier;
        if (status == EntitlementStatus.FREE || status == EntitlementStatus.EXPIRED) {
            tier = SubscriptionTier.FREE;
        } else if (entity.getBasePlanId() != null && entity.getBasePlanId().toLowerCase().contains("six-month")) {
            tier = SubscriptionTier.PREMIUM_SIX_MONTH;
        } else {
            tier = SubscriptionTier.PREMIUM_MONTHLY;
        }

        return new SubscriptionEntitlement(
                entity.getAccountId(),
                tier,
                status,
                entity.getProductId(),
                entity.getBasePlanId(),
                entity.getCurrentPeriodEndMs(),
                entity.getLastVerifiedAtMs(),
                entity.getPurchaseTokenHash(),
                "GOOGLE_PLAY",
                entity.isAutoRenewing()
        );
    }
}
