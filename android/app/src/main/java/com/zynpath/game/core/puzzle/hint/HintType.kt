package com.zynpath.game.core.puzzle.hint

/**
 * Authoritative outcome classification for hint queries.
 *
 * Implements Prompt 14 Section 8:
 * Avoids representing every outcome as a nullable GridPosition and provides
 * structured taxonomy for UI presentation and entitlement tracking.
 */
enum class HintType {
    /**
     * A verified, legal forward move that belongs to at least one complete full-coverage solution.
     */
    NEXT_MOVE,

    /**
     * The player has reached an uncompletable dead end; rollback to a verified earlier prefix is required.
     */
    RECOVERY_REQUIRED,

    /**
     * The puzzle has already reached a validated 100% full-coverage completion state.
     */
    ALREADY_COMPLETED,

    /**
     * The solver search limit or time budget was exhausted without proving a valid continuation or dead end.
     */
    SEARCH_INCONCLUSIVE,

    /**
     * Search was cancelled cooperatively (e.g. state changed or user navigated away).
     */
    CANCELLED,

    /**
     * Provided request parameters or partial path violated puzzle topology invariants.
     */
    INVALID_STATE,

    /**
     * Hints are disabled for this game mode (e.g. competitive duel) or level type.
     */
    HINT_NOT_AVAILABLE,

    /**
     * Free hint allowance has been exhausted for this non-premium user.
     */
    USAGE_LIMIT_REACHED
}
