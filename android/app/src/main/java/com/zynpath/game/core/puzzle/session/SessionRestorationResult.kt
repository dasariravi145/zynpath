package com.zynpath.game.core.puzzle.session

import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.engine.PuzzleGameState

/**
 * Structured outcome of attempting to restore a persistent gameplay session.
 *
 * Implements Prompt 13 Sections 13–16:
 * - Differentiates verified replay success from specific validation failure modes.
 * - Prevents launching or binding invalid or incompatible saved paths.
 */
sealed class SessionRestorationResult {
    data class Success(
        val snapshot: GameplaySessionSnapshot,
        val restoredGameState: PuzzleGameState,
        val restoredEngine: PuzzleEngine
    ) : SessionRestorationResult()

    data class Invalid(
        val snapshot: GameplaySessionSnapshot,
        val reason: RestorationFailureReason,
        val detailMessage: String
    ) : SessionRestorationResult()

    data class NotFound(
        val levelId: Int
    ) : SessionRestorationResult()
}

enum class RestorationFailureReason {
    LEVEL_ID_MISMATCH,
    PUZZLE_ID_MISMATCH,
    PUZZLE_VERSION_MISMATCH,
    UNSUPPORTED_SCHEMA_VERSION,
    SESSION_NOT_RESUMABLE,
    START_NOT_CHECKPOINT_ONE,
    ILLEGAL_MOVE_REPLAY,
    PATH_MISMATCH_AFTER_REPLAY,
    SESSION_OWNERSHIP_MISMATCH,
    CORRUPTED_PATH_DATA
}
