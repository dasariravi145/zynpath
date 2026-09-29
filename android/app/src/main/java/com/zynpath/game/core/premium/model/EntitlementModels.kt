package com.zynpath.game.core.premium.model

/**
 * Domain models for authoritative subscription entitlement and feature access gating.
 *
 * Implements Prompt 26 Sections 23, 24, 34, 36 & 40.
 */

enum class EntitlementStatus {
    FREE,
    ACTIVE,
    IN_GRACE_PERIOD,
    ON_HOLD,
    PAUSED,
    CANCELLED_BUT_ACTIVE,
    EXPIRED,
    REVOKED,
    UNKNOWN;

    val isEntitled: Boolean
        get() = this == ACTIVE || this == IN_GRACE_PERIOD || this == CANCELLED_BUT_ACTIVE

    val displayLabel: String
        get() = when (this) {
            FREE -> "Free"
            ACTIVE -> "Active Premium"
            IN_GRACE_PERIOD -> "Grace Period"
            ON_HOLD -> "Payment On Hold"
            PAUSED -> "Subscription Paused"
            CANCELLED_BUT_ACTIVE -> "Active (Cancelled)"
            EXPIRED -> "Subscription Expired"
            REVOKED -> "Subscription Revoked"
            UNKNOWN -> "Status Unknown"
        }
}

enum class PremiumFeatureKey {
    AD_FREE,
    UNLIMITED_SOLO_HINTS,
    PREMIUM_SOLO_PACKS,
    PREMIUM_THEMES,
    PREMIUM_PATH_EFFECTS,
    PREMIUM_AVATAR_FRAMES,
    ADVANCED_PERSONAL_STATS
}

data class PremiumEntitlement(
    val accountId: String? = null,
    val status: EntitlementStatus = EntitlementStatus.FREE,
    val productId: String? = null,
    val basePlanId: String? = null,
    val currentPeriodEndMs: Long = 0L,
    val lastVerifiedAtMs: Long = 0L,
    val isAutoRenewing: Boolean = false,
    val isCachedOffline: Boolean = false,
    val unlockedFeatureKeys: List<String> = emptyList()
) {
    val isPremiumActive: Boolean
        get() = status.isEntitled && (currentPeriodEndMs <= 0 || System.currentTimeMillis() <= currentPeriodEndMs)

    companion object {
        fun free(accountId: String? = null): PremiumEntitlement = PremiumEntitlement(
            accountId = accountId,
            status = EntitlementStatus.FREE,
            currentPeriodEndMs = 0L,
            lastVerifiedAtMs = System.currentTimeMillis()
        )
    }
}
