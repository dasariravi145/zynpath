package com.zynpath.game.core.puzzle.hint

import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzlePath

/**
 * Sealed hierarchy of structured hint analysis results.
 *
 * Implements Prompt 14 Section 8:
 * Returns rich, strongly typed data for each outcome rather than nullable coordinates.
 */
sealed class HintResult {
    abstract val type: HintType

    /**
     * A verified next legal move that extends the player's current path into at least one complete full-coverage solution.
     */
    data class NextMove(
        val nextMove: GridPosition,
        val fullSolution: PuzzlePath,
        val totalSolutionLength: Int,
        val isCached: Boolean = false
    ) : HintResult() {
        override val type: HintType = HintType.NEXT_MOVE
        val nextCoordinate: GridPosition get() = nextMove
    }

    /**
     * Dead-end recovery guidance when exhaustive search proves no full continuation exists from current path.
     */
    data class RecoveryRequired(
        val recommendedRollbackIndex: Int,
        val recommendedRollbackPosition: GridPosition,
        val stepsToRetract: Int,
        val explanation: String,
        val verifiedPrefix: List<GridPosition>
    ) : HintResult() {
        override val type: HintType = HintType.RECOVERY_REQUIRED
    }

    /**
     * Returned when puzzle is already completed.
     */
    data class AlreadyCompleted(
        val message: String = "Puzzle is already completed"
    ) : HintResult() {
        override val type: HintType = HintType.ALREADY_COMPLETED
    }

    /**
     * Search budget or time limit exhausted without proving a solution or dead end.
     */
    data class SearchInconclusive(
        val message: String,
        val nodesExplored: Long,
        val elapsedMs: Long
    ) : HintResult() {
        override val type: HintType = HintType.SEARCH_INCONCLUSIVE
    }

    /**
     * Search was cancelled cooperatively.
     */
    data class Cancelled(
        val message: String = "Hint search was cancelled"
    ) : HintResult() {
        override val type: HintType = HintType.CANCELLED
    }

    /**
     * Request had mismatched or invalid game state.
     */
    data class InvalidState(
        val reason: String
    ) : HintResult() {
        override val type: HintType = HintType.INVALID_STATE
    }

    /**
     * Hints unavailable in this mode (e.g. competitive duel).
     */
    data class HintNotAvailable(
        val reason: String
    ) : HintResult() {
        override val type: HintType = HintType.HINT_NOT_AVAILABLE
    }

    /**
     * Free hint allowance exhausted.
     */
    data class UsageLimitReached(
        val remainingAllowance: Int = 0,
        val message: String = "No free hints remaining"
    ) : HintResult() {
        override val type: HintType = HintType.USAGE_LIMIT_REACHED
    }
}
