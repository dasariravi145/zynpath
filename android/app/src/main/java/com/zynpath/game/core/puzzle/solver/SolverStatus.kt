package com.zynpath.game.core.puzzle.solver

/**
 * Authoritative outcome classification of a puzzle solver execution.
 */
enum class SolverStatus {
    /**
     * At least one valid solution path was found.
     * When exhaustive search was performed with maxSolutions >= 2, this also confirms uniqueness.
     */
    SOLVED,

    /**
     * Exhaustive search proved that NO valid solution path exists for this puzzle definition.
     */
    UNSOLVABLE,

    /**
     * At least two distinct valid solution paths were found, proving the puzzle is non-unique.
     */
    MULTIPLE_SOLUTIONS,

    /**
     * Search was aborted due to exceeding node budget or time limit before exhaustive proof.
     * Does NOT imply unsolvability; solutions found prior to the limit are preserved.
     */
    SEARCH_LIMIT_REACHED,

    /**
     * Search was cooperatively cancelled via the cancellation signal.
     */
    CANCELLED,

    /**
     * The supplied PuzzleDefinition failed structural validation before search began.
     */
    INVALID_PUZZLE
}
