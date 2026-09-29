package com.zynpath.game.core.ads.model

/**
 * Local reward record ensuring idempotency and tracking grant status.
 *
 * Implements Prompt 29 Section 26 & 30.
 */
data class RewardEvent(
    val rewardEventId: String,
    val transactionId: String?,
    val rewardType: String = "SOLO_HINT",
    val amount: Int = 1,
    val adUnitId: String,
    val verificationStatus: RewardVerificationStatus,
    val grantedAtMs: Long = System.currentTimeMillis()
)
