package com.zynpath.backend.subscription.model;

import java.util.List;

/**
 * Data Transfer Objects for subscription purchase verification and entitlement queries.
 *
 * Implements Prompt 26 Sections 18, 23, 26, 29 & 40.
 */
public final class SubscriptionDto {

    private SubscriptionDto() {}

    public record SubscriptionEntitlementDto(
            String accountId,
            String tier,
            String status,
            boolean isPremiumActive,
            String productId,
            String basePlanId,
            long currentPeriodEndMs,
            long lastVerifiedAtMs,
            boolean isAutoRenewing,
            List<String> unlockedFeatureKeys
    ) {}

    public record SubscriptionVerificationRequest(
            String purchaseToken,
            String productId,
            String basePlanId,
            String orderId,
            String packageName
    ) {}

    public record SubscriptionVerificationResponse(
            boolean verified,
            String status,
            String message,
            String configurationStatus,
            SubscriptionEntitlementDto entitlement
    ) {}

    public record SubscriptionRestoreRequest(
            List<String> purchaseTokens,
            String packageName
    ) {}

    public record SubscriptionRestoreResponse(
            boolean restored,
            String message,
            SubscriptionEntitlementDto entitlement
    ) {}
}
