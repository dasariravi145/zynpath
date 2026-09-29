package com.zynpath.game.core.puzzle.engine

import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult

/**
 * Result returned after evaluating a [PuzzleAction] against the engine's current state.
 */
sealed interface PuzzleEngineResult {
    val state: PuzzleGameState
    val newState: PuzzleGameState get() = state
    val isAccepted: Boolean

    /**
     * The action was accepted and a new [state] was produced.
     * If this action completed the puzzle, [completionEvent] will be non-null.
     */
    data class Accepted(
        override val state: PuzzleGameState,
        val action: PuzzleAction,
        val completionEvent: ValidatedCompletionResult? = null
    ) : PuzzleEngineResult {
        override val isAccepted: Boolean get() = true
    }

    /**
     * The action was rejected. The domain [state] remains unchanged.
     */
    data class Rejected(
        override val state: PuzzleGameState,
        val action: PuzzleAction,
        val reason: MoveRejectionReason,
        val message: String
    ) : PuzzleEngineResult {
        override val isAccepted: Boolean get() = false
    }
}
