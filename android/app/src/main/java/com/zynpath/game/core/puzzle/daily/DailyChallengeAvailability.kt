package com.zynpath.game.core.puzzle.daily

/**
 * Authoritative availability states for a Daily Challenge.
 *
 * Implements Prompt 16 Section 14:
 * Clearly distinguishes active, completed, in-progress, expired, and asset states
 * without conflating missing assets with expired challenges.
 */
enum class DailyChallengeAvailability {
    /** The challenge is open for today and has not yet been started. */
    AVAILABLE,

    /** The challenge was completed with a validated engine victory. */
    COMPLETED,

    /** The challenge has an active in-progress attempt for today. */
    IN_PROGRESS,

    /** The challenge date is in the future according to the authoritative UTC clock. */
    NOT_YET_AVAILABLE,

    /** The challenge UTC date window has passed without completion. */
    EXPIRED,

    /** The puzzle asset mapped for this challenge date is missing from offline packaging. */
    ASSET_UNAVAILABLE,

    /** The puzzle asset was found but failed cryptographic or structural verification. */
    ASSET_INVALID;

    /**
     * True if the challenge can be played or resumed right now.
     */
    val isPlayable: Boolean
        get() = this == AVAILABLE || this == IN_PROGRESS
}
