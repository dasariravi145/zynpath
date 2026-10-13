package com.zynpath.backend.persistence.adapter;

import com.zynpath.backend.persistence.port.SubscriptionPersistencePort;
import com.zynpath.backend.subscription.model.SubscriptionEntitlement;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory adapter for SubscriptionPersistencePort using ConcurrentHashMap.
 * Retained for fast offline development and testing.
 */
@Repository
@ConditionalOnProperty(name = "zynpath.persistence.mode", havingValue = "in-memory", matchIfMissing = true)
public class InMemorySubscriptionPersistenceAdapter implements SubscriptionPersistencePort {

    private final Map<String, SubscriptionEntitlement> entitlementsByAccount = new ConcurrentHashMap<>();
    private final Map<String, String> accountByTokenHash = new ConcurrentHashMap<>();

    @Override
    public SubscriptionEntitlement getEntitlement(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            return SubscriptionEntitlement.free("anonymous");
        }
        return entitlementsByAccount.get(accountId);
    }

    @Override
    public void saveEntitlement(SubscriptionEntitlement entitlement) {
        entitlementsByAccount.put(entitlement.accountId(), entitlement);
        if (entitlement.purchaseTokenHash() != null && !entitlement.purchaseTokenHash().isBlank()) {
            accountByTokenHash.put(entitlement.purchaseTokenHash(), entitlement.accountId());
        }
    }

    @Override
    public Optional<String> getAccountByTokenHash(String tokenHash) {
        return Optional.ofNullable(accountByTokenHash.get(tokenHash));
    }

    @Override
    public void bindTokenHash(String tokenHash, String accountId) {
        accountByTokenHash.put(tokenHash, accountId);
    }

    @Override
    public void deleteEntitlement(String accountId) {
        SubscriptionEntitlement removed = entitlementsByAccount.remove(accountId);
        if (removed != null && removed.purchaseTokenHash() != null) {
            accountByTokenHash.remove(removed.purchaseTokenHash());
        }
    }
}
