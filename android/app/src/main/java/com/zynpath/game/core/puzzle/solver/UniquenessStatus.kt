package com.zynpath.game.core.puzzle.solver

/**
 * Authoritative classification of solution uniqueness for a puzzle definition.
 *
 * Adheres strictly to Prompt 8 Section 19:
 * - [UNIQUE]: Exactly one solution after exhaustive search.
 * - [NON_UNIQUE]: Two or more distinct solutions discovered.
 * - [UNSOLVABLE]: Zero solutions after exhaustive search.
 * - [UNKNOWN]: One solution found without exhaustive search, or search limit reached before proof.
 */
enum class UniquenessStatus {
    /**
     * Exhaustive search proved that exactly one valid full-coverage solution exists.
     */
    UNIQUE,

    /**
     * Search discovered two or more distinct valid solutions, proving the puzzle is non-unique.
     */
    NON_UNIQUE,

    /**
     * Exhaustive search proved that no valid full-coverage solution exists.
     */
    UNSOLVABLE,

    /**
     * Uniqueness cannot be proved because search was not exhaustive (e.g. maxSolutions was 1,
     * search hit a node/time limit, or search was cancelled).
     */
    UNKNOWN
}
