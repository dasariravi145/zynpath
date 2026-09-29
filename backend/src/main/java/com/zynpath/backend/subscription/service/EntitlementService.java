package com.zynpath.backend.subscription.service;

import com.zynpath.backend.subscription.model.SubscriptionTier;

/**
 * Service contract for verifying user premium entitlement.
 */
public interface EntitlementService {
    SubscriptionTier getEntitlement(String playerId);
}
