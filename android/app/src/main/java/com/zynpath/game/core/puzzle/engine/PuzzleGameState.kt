package com.zynpath.game.core.puzzle.engine

import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleBoardState
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult

/**
 * Immutable snapshot of active puzzle gameplay.
 * Decoupled completely from Android UI, timers, pixel coordinates, and network transport.
 */
data class PuzzleGameState(
    val puzzleDefinition: PuzzleDefinition,
    val currentPath: PuzzlePath = PuzzlePath.empty(),
    val nextRequiredCheckpoint: Int = 1,
    val visitedCheckpointCount: Int = 0,
    val coveredCellCount: Int = 0,
    val totalRequiredCells: Int = puzzleDefinition.totalRequiredCells,
    val currentEndpoint: GridPosition? = currentPath.currentHead,
    val moveCount: Int = 0,
    val gameStatus: GameStatus = GameStatus.NOT_STARTED,
    val lastRejection: MoveRejectionReason? = null,
    val completionResult: ValidatedCompletionResult? = null
) {
    val nextRequiredCheckpointNumber: Int get() = nextRequiredCheckpoint
    val currentPosition: GridPosition? get() = currentEndpoint
    val totalCells: Int get() = totalRequiredCells
    val isFullyCovered: Boolean get() = coveredCellCount == totalRequiredCells
    val highestCheckpointVisited: Int get() = visitedCheckpointCount

    val isCompleted: Boolean
        get() = gameStatus == GameStatus.COMPLETED

    val isNotStarted: Boolean
        get() = gameStatus == GameStatus.NOT_STARTED

    val isInProgress: Boolean
        get() = gameStatus == GameStatus.IN_PROGRESS

    val isPaused: Boolean
        get() = gameStatus == GameStatus.PAUSED

    /**
     * Progress ratio from 0.0 (no cells covered) to 1.0 (100% board coverage).
     */
    val coverageFraction: Float
        get() = if (totalRequiredCells > 0) {
            coveredCellCount.toFloat() / totalRequiredCells.toFloat()
        } else {
            0f
        }

    /**
     * Checks if [position] is currently on the active path.
     */
    fun isCellCovered(position: GridPosition): Boolean = currentPath.contains(position)

    /**
     * Bridges this domain state to the presentation [PuzzleBoardState] for Compose Canvas rendering.
     */
    fun toBoardState(hintedCoordinate: GridPosition? = null): PuzzleBoardState {
        return PuzzleBoardState(
            rowCount = puzzleDefinition.gridDimensions.rows,
            columnCount = puzzleDefinition.gridDimensions.columns,
            checkpoints = puzzleDefinition.checkpointMap,
            walls = puzzleDefinition.blockedEdges,
            path = currentPath.positions,
            nextRequiredCheckpoint = nextRequiredCheckpoint,
            hintedCoordinate = hintedCoordinate
        )
    }

    companion object {
        /**
         * Factory creating the initial unplayed state for a given [definition].
         */
        fun initial(definition: PuzzleDefinition): PuzzleGameState {
            return PuzzleGameState(
                puzzleDefinition = definition,
                currentPath = PuzzlePath.empty(),
                nextRequiredCheckpoint = 1,
                visitedCheckpointCount = 0,
                coveredCellCount = 0,
                totalRequiredCells = definition.totalRequiredCells,
                currentEndpoint = null,
                moveCount = 0,
                gameStatus = GameStatus.NOT_STARTED,
                lastRejection = null,
                completionResult = null
            )
        }
    }
}
