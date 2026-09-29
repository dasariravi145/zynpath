package com.zynpath.game.core.ads.model

/**
 * Explicit states of the rewarded ad lifecycle.
 *
 * Implements Prompt 29 Section 12:
 * - NOT_INITIALIZED: Ad SDK has not yet been initialized.
 * - CONSENT_REQUIRED: Consent must be gathered before requesting ads.
 * - LOADING: Ad request is in progress.
 * - READY: Rewarded ad is cached in memory and ready for presentation.
 * - SHOWING: Ad is actively presenting full-screen content.
 * - REWARD_EARNED: User completed ad requirement and earned the reward.
 * - DISMISSED_WITHOUT_REWARD: Ad was closed before completing reward requirement.
 * - UNAVAILABLE: No ad inventory available or offline.
 * - ERROR: Ad failed to load or failed to show.
 */
enum class AdState {
    NOT_INITIALIZED,
    CONSENT_REQUIRED,
    LOADING,
    READY,
    SHOWING,
    REWARD_EARNED,
    DISMISSED_WITHOUT_REWARD,
    UNAVAILABLE,
    ERROR
}
