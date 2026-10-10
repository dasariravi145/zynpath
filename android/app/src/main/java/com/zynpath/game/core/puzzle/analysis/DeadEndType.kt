package com.zynpath.game.core.puzzle.analysis

/**
 * Diagnostic classification of puzzle dead ends.
 *
 * Implements Master Redesign Prompt 2 Section 24:
 * Differentiates why a route cannot be completed legally or with full coverage.
 */
enum class DeadEndType {
    /** No unvisited orthogonal neighbors exist from the current head. */
    IMMEDIATE_DEAD_END,

    /** Current head is sealed inside a local region with unvisited cells remaining outside. */
    LOCAL_REGION_DEAD_END,

    /** Reached a checkpoint out of strict ascending order. */
    CHECKPOINT_ORDER_DEAD_END,

    /** Unvisited cells are disconnected into multiple disjoint components. */
    CONNECTIVITY_DEAD_END,

    /** One or more unvisited cells lack sufficient traversable degree in the remaining graph. */
    FUTURE_COVERAGE_DEAD_END,

    /** Reached the final checkpoint prematurely before 100% of cells were covered. */
    FINAL_COVERAGE_FAILURE
}
