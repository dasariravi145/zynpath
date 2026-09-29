package com.zynpath.game.core.ads.model

/**
 * Verification state of an ad reward event.
 *
 * Implements Prompt 29 Section 31.
 */
enum class RewardVerificationStatus {
    LOCAL_CONFIRMED,
    SERVER_VERIFIED,
    PENDING_VERIFICATION,
    REJECTED
}
