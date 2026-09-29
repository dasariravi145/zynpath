package com.zynpath.game.core.billing

/**
 * Authoritative subscription identifiers, targets, and official Google Play constants.
 *
 * Implements Prompt 26 Sections 7, 8, 40 & 48.
 */
object SubscriptionConstants {

    /** Canonical Google Play Subscription Product ID */
    const val PRODUCT_ID_PREMIUM = "zynpath_premium"

    /** Base plan identifier for the 1-month recurring plan */
    const val BASE_PLAN_MONTHLY = "premium-monthly"

    /** Base plan identifier for the 6-month recurring plan */
    const val BASE_PLAN_SIX_MONTH = "premium-six-months"

    /** Planned commercial target price for Monthly pass (indicative; actual from Google Play) */
    const val PLANNED_PRICE_MONTHLY_INR = "₹99"

    /** Planned commercial target price for 6-Month pass (indicative; actual from Google Play) */
    const val PLANNED_PRICE_SIX_MONTH_INR = "₹499"

    /**
     * Constructs the official Google Play subscription management deep-link URL.
     * Section 48: Directs users to the official Play Store subscription center.
     */
    fun getManageSubscriptionUrl(packageName: String = "com.zynpath.game"): String {
        return "https://play.google.com/store/account/subscriptions?sku=$PRODUCT_ID_PREMIUM&package=$packageName"
    }

    /**
     * Fallback URL to Google Play Subscriptions overview.
     */
    const val GOOGLE_PLAY_SUBSCRIPTIONS_OVERVIEW_URL = "https://play.google.com/store/account/subscriptions"

    /**
     * Defined Premium features and benefits list for presentation.
     */
    val BENEFIT_DESCRIPTIONS = listOf(
        "Ad-free gameplay — Zero banner or interstitial ads",
        "Unlimited Solo hints — Clear stuck numbers anytime without limits",
        "Premium Solo puzzle packs — Exclusive curated master worlds",
        "Premium visual themes — Midnight, Neon & Forest Glass boards",
        "Custom path effects — Cyan pulse, Gold trail & Ember glow",
        "Profile customization — Exclusive avatar badges and frames",
        "Advanced personal statistics — Detailed solve speeds and breakdown"
    )
}
