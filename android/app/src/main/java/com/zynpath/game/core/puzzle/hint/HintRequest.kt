package com.zynpath.game.core.puzzle.hint

import com.zynpath.game.core.puzzle.engine.PuzzleGameState
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath

/**
 * Immutable hint query payload referencing authoritative engine state.
 *
 * Implements Prompt 14 Section 7:
 * - References puzzle ID, puzzle version, current [PuzzleGameState].
 * - References current ordered path and next required checkpoint.
 * - Enforces game mode restrictions (Section 24).
 * - Carries solver budgets and cancellation signals (Section 20).
 */
data class HintRequest(
    val puzzleId: String,
    val puzzleVersion: Int = 1,
    val definition: PuzzleDefinition,
    val gameState: PuzzleGameState,
    val currentOrderedPath: List<GridPosition> = gameState.currentPath.positions,
    val nextRequiredCheckpoint: Int = gameState.nextRequiredCheckpoint,
    val gameMode: GameMode = GameMode.SOLO,
    val configuration: HintConfiguration = HintConfiguration.DEFAULT
) {
    val currentPath: PuzzlePath get() = gameState.currentPath

    constructor(
        puzzleId: String,
        puzzleVersion: Int = 1,
        definition: PuzzleDefinition,
        currentPath: PuzzlePath,
        gameMode: GameMode = GameMode.SOLO,
        configuration: HintConfiguration = HintConfiguration.DEFAULT
    ) : this(
        puzzleId = puzzleId,
        puzzleVersion = puzzleVersion,
        definition = definition,
        gameState = PuzzleGameState(definition, currentPath = currentPath),
        currentOrderedPath = currentPath.positions,
        nextRequiredCheckpoint = 1,
        gameMode = gameMode,
        configuration = configuration
    )
}
