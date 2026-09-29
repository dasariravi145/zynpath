package com.zynpath.game.core.puzzle.engine

/**
 * Granular reasons explaining why an attempted move or action was rejected by the engine.
 * Ensures the UI and state machines can respond with precise feedback without corrupting domain state.
 */
enum class MoveRejectionReason {
    /** The player attempted to start a path from a cell that is not checkpoint 1. */
    START_MUST_BE_CHECKPOINT_ONE,

    /** The destination coordinate lies outside the grid boundaries. */
    OUT_OF_BOUNDS,

    /** The destination cell is marked as excluded / non-playable. */
    EXCLUDED_CELL,

    /** The move is non-adjacent (diagonal step or jump across multiple cells). */
    NON_ADJACENT,

    /** An edge barrier / wall obstructs traversal between the current head and the destination. */
    BLOCKED_BY_WALL,

    /** The destination cell is already occupied by the current path (self-intersection). */
    CELL_ALREADY_VISITED,

    /** Checkpoints must be connected in strictly ascending order (e.g. 1 -> 2 -> 3). */
    WRONG_CHECKPOINT_ORDER,

    /**
     * The player attempted to enter the final checkpoint before 100% of other required cells were covered.
     * Game rules strictly forbid early termination.
     */
    PREMATURE_FINAL_CHECKPOINT,

    /** The puzzle is already completed; further forward moves are frozen. */
    GAME_ALREADY_COMPLETED,

    /** The game is currently paused; actions cannot be processed until resumed. */
    GAME_PAUSED,

    /** The requested action is not valid in the current game state (e.g. invalid backtrack target). */
    INVALID_ACTION;

    companion object {
        val SELF_INTERSECTION: MoveRejectionReason get() = CELL_ALREADY_VISITED
        val WALL_COLLISION: MoveRejectionReason get() = BLOCKED_BY_WALL
        val FINAL_CHECKPOINT_PREMATURE: MoveRejectionReason get() = PREMATURE_FINAL_CHECKPOINT
    }
}
