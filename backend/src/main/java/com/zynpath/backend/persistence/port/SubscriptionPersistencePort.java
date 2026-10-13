package com.zynpath.backend.persistence.port;

import com.zynpath.backend.subscription.model.SubscriptionEntitlement;

import java.util.Optional;

/**
 * Storage-agnostic persistence port for Google Play subscription entitlements.
 */
public interface SubscriptionPersistencePort {

    SubscriptionEntitlement getEntitlement(String accountId);

    void saveEntitlement(SubscriptionEntitlement entitlement);

    Optional<String> getAccountByTokenHash(String tokenHash);

    void bindTokenHash(String tokenHash, String accountId);

    void deleteEntitlement(String accountId);
}
