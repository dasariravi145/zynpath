package com.zynpath.backend.subscription.model;

/**
 * Zynpath premium subscription tiers.
 * Defined per Section 24: Monthly (₹99) and 6-Month (₹499) passes.
 * Premium strictly grants cosmetic themes and removes ads; no competitive hints or gameplay advantages are permitted.
 */
public enum SubscriptionTier {
    FREE,
    PREMIUM_MONTHLY,    // ₹99/mo via Google Play Billing
    PREMIUM_SIX_MONTH   // ₹499/6mo via Google Play Billing
}
