package com.zynpath.game.core.puzzle.catalog

/**
 * Authoritative availability status of a catalog level.
 *
 * Implements Prompt 11 Section 21:
 * Clearly distinguishes progression gates (locked vs. completed) from asset readiness
 * (available vs. asset unavailable vs. invalid asset).
 */
enum class LevelAvailability {
    /** The level has not yet been unlocked by progression (previous level or world incomplete). */
    LOCKED,

    /** The level is unlocked by progression, and its verified puzzle asset is ready to play. */
    UNLOCKED_AND_AVAILABLE,

    /** The level has already been successfully solved and verified by the player. */
    COMPLETED,

    /** The level is unlocked by progression, but no packaged puzzle asset exists yet (content pending). */
    ASSET_UNAVAILABLE,

    /** The level asset was found but failed integrity checks or structural validation. */
    ASSET_INVALID;

    val isPlayable: Boolean
        get() = this == UNLOCKED_AND_AVAILABLE || this == COMPLETED
}
