package com.zynpath.game.core.puzzle.engine

import com.zynpath.game.core.puzzle.model.GridPosition

/**
 * Explicit, deterministic actions dispatched to the puzzle engine.
 */
sealed interface PuzzleAction {
    /**
     * Initializes the path at [position].
     * Must be numbered checkpoint #1.
     */
    data class StartPath(val position: GridPosition) : PuzzleAction

    /**
     * Attempts to extend the continuous path to [position].
     * Must be an orthogonal, unblocked, unvisited, legal neighbor.
     * Note: If [position] matches the immediate predecessor in the path,
     * the engine treats this as intuitive single-step drag-backtracking.
     */
    data class ExtendPath(val position: GridPosition) : PuzzleAction

    /**
     * Retracts the path back to an already-visited [position], discarding all subsequent steps.
     * Restores checkpoint and coverage counters accordingly.
     */
    data class BacktrackTo(val position: GridPosition) : PuzzleAction

    /**
     * Backtracks by exactly one cell (undoing the most recent move).
     */
    data object BacktrackOne : PuzzleAction

    /**
     * Resets the entire path back to NOT_STARTED. Checkpoint 1 becomes required again.
     */
    data object ResetPath : PuzzleAction

    /**
     * Pauses the active game session.
     */
    data object PauseGame : PuzzleAction

    /**
     * Resumes a paused game session.
     */
    data object ResumeGame : PuzzleAction

    /**
     * Undo alias for BacktrackOne.
     */
    data object Undo : PuzzleAction

    /**
     * Reset alias for ResetPath.
     */
    data object Reset : PuzzleAction

    companion object {
        val UndoLastMove: PuzzleAction get() = BacktrackOne
        val ResetPuzzle: PuzzleAction get() = ResetPath
    }
}
