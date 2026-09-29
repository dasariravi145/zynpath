package com.zynpath.backend.subscription.model;

/**
 * Domain entity representing an account's verified subscription entitlement.
 *
 * Implements Prompt 26 Sections 20, 23 & 25:
 * - Binds verified subscriptions to the authenticated Zynpath accountId.
 * - Stores authoritative expiration, status, and sensitive token hash.
 */
public record SubscriptionEntitlement(
        String accountId,
        SubscriptionTier tier,
        EntitlementStatus status,
        String productId,
        String basePlanId,
        long currentPeriodEndMs,
        long lastVerifiedAtMs,
        String purchaseTokenHash,
        String source,
        boolean isAutoRenewing
) {
    /**
     * Evaluates whether premium features are currently active.
     */
    public boolean isActive() {
        if (status == null || !status.isEntitled()) {
            return false;
        }
        // Active if period end is unset (lifetime/managed) or has not yet passed
        return currentPeriodEndMs <= 0 || System.currentTimeMillis() <= currentPeriodEndMs;
    }

    public static SubscriptionEntitlement free(String accountId) {
        return new SubscriptionEntitlement(
                accountId,
                SubscriptionTier.FREE,
                EntitlementStatus.FREE,
                null,
                null,
                0L,
                System.currentTimeMillis(),
                null,
                "NONE",
                false
        );
    }
}
