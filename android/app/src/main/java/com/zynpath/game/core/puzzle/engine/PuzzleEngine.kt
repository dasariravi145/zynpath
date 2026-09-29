package com.zynpath.game.core.puzzle.engine

import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath

/**
 * Authoritative, interactive continuous number-path puzzle engine.
 *
 * Enforces non-negotiable puzzle rules:
 * 1. Path must begin at checkpoint 1.
 * 2. Checkpoints must be visited in strictly ascending numerical order (1 -> 2 -> ... -> N).
 * 3. Movements must be strictly orthogonal (no diagonals, no jumps).
 * 4. Walls / blocked edges cannot be crossed.
 * 5. Cells cannot be revisited during forward movement (simple path).
 * 6. The final checkpoint cannot be entered prematurely before all other required cells are covered.
 * 7. Victory requires BOTH 100% required cell coverage AND ascending checkpoint order ending at #N.
 *
 * Supports intuitive single-step drag backtracking, multi-cell retraction, and path resetting.
 * Completely decoupled from Android UI, Compose, Room, DataStore, and network clients.
 */
class PuzzleEngine(
    val definition: PuzzleDefinition,
    val levelId: Int = 1,
    val worldId: Int = 1,
    initialState: PuzzleGameState = PuzzleGameState.initial(definition)
) {
    constructor(
        definition: PuzzleDefinition,
        initialState: PuzzleGameState
    ) : this(definition, 1, 1, initialState)

    var currentState: PuzzleGameState = initialState
        private set

    /**
     * Dispatches an action to the engine, updating [currentState] if accepted.
     */
    fun process(action: PuzzleAction, elapsedTimeMs: Long = 1000L): PuzzleEngineResult {
        val result = reduce(currentState, action, elapsedTimeMs)
        if (result.isAccepted) {
            currentState = result.state
        }
        return result
    }

    /**
     * Pure state reduction function transitioning [state] given [action].
     */
    fun reduce(
        state: PuzzleGameState,
        action: PuzzleAction,
        elapsedTimeMs: Long = 1000L
    ): PuzzleEngineResult {
        return when (action) {
            is PuzzleAction.StartPath -> handleStartPath(state, action.position)
            is PuzzleAction.ExtendPath -> handleExtendPath(state, action.position, elapsedTimeMs)
            is PuzzleAction.BacktrackTo -> handleBacktrackTo(state, action.position)
            is PuzzleAction.BacktrackOne -> handleBacktrackOne(state)
            is PuzzleAction.ResetPath -> handleReset(state)
            is PuzzleAction.Undo -> handleBacktrackOne(state)
            is PuzzleAction.Reset -> handleReset(state)
            is PuzzleAction.PauseGame -> handlePause(state)
            is PuzzleAction.ResumeGame -> handleResume(state)
        }
    }

    fun processAction(action: PuzzleAction, elapsedTimeMs: Long = 0L): PuzzleEngineResult =
        process(action, elapsedTimeMs)


    private fun handleStartPath(state: PuzzleGameState, position: GridPosition): PuzzleEngineResult {
        val action = PuzzleAction.StartPath(position)

        if (state.isCompleted) {
            return PuzzleEngineResult.Rejected(
                state, action, MoveRejectionReason.GAME_ALREADY_COMPLETED,
                "Puzzle is already completed"
            )
        }
        if (state.isPaused) {
            return PuzzleEngineResult.Rejected(
                state, action, MoveRejectionReason.GAME_PAUSED,
                "Game is currently paused"
            )
        }

        val startCp = definition.startCheckpoint
        if (startCp == null || position != startCp.position) {
            return PuzzleEngineResult.Rejected(
                state, action, MoveRejectionReason.START_MUST_BE_CHECKPOINT_ONE,
                "First step must be starting checkpoint #1 at ${startCp?.position} (attempted $position)"
            )
        }

        val newPath = PuzzlePath.of(position)
        val newState = state.copy(
            currentPath = newPath,
            nextRequiredCheckpoint = 2,
            visitedCheckpointCount = 1,
            coveredCellCount = 1,
            currentEndpoint = position,
            moveCount = 0,
            gameStatus = GameStatus.IN_PROGRESS,
            lastRejection = null,
            completionResult = null
        )

        return PuzzleEngineResult.Accepted(newState, action)
    }

    private fun handleExtendPath(
        state: PuzzleGameState,
        position: GridPosition,
        elapsedTimeMs: Long
    ): PuzzleEngineResult {
        val action = PuzzleAction.ExtendPath(position)

        if (state.isCompleted) {
            return PuzzleEngineResult.Rejected(
                state, action, MoveRejectionReason.GAME_ALREADY_COMPLETED,
                "Puzzle is already completed"
            )
        }
        if (state.isPaused) {
            return PuzzleEngineResult.Rejected(
                state, action, MoveRejectionReason.GAME_PAUSED,
                "Game is currently paused"
            )
        }

        // If path is not yet started, treat ExtendPath to checkpoint 1 as StartPath
        if (state.isNotStarted || state.currentPath.isEmpty) {
            return handleStartPath(state, position)
        }

        val currentHead = state.currentPath.currentHead ?: return handleStartPath(state, position)

        // No-op if tapping/dragging on current endpoint
        if (position == currentHead) {
            return PuzzleEngineResult.Rejected(
                state, action, MoveRejectionReason.CELL_ALREADY_VISITED,
                "Position $position is already the current head of the path"
            )
        }

        // UX Backtracking: Dragging or stepping back to the immediately preceding cell retracts the path by 1 step
        val pathPositions = state.currentPath.positions
        if (pathPositions.size >= 2 && position == pathPositions[pathPositions.size - 2]) {
            return handleBacktrackTo(state, position)
        }

        // 1. Grid boundary check
        if (!definition.gridDimensions.contains(position)) {
            return PuzzleEngineResult.Rejected(
                state, action, MoveRejectionReason.OUT_OF_BOUNDS,
                "Position $position is outside grid dimensions ${definition.gridDimensions}"
            )
        }

        // 2. Playable/required cell check
        if (!definition.requiredCells.contains(position)) {
            return PuzzleEngineResult.Rejected(
                state, action, MoveRejectionReason.EXCLUDED_CELL,
                "Position $position is an excluded/non-playable cell"
            )
        }

        // 3. Orthogonal adjacency check (strictly no diagonals or multi-cell jumps)
        if (!currentHead.isOrthogonalNeighbor(position)) {
            return PuzzleEngineResult.Rejected(
                state, action, MoveRejectionReason.NON_ADJACENT,
                "Move from $currentHead to $position is non-orthogonal or non-adjacent"
            )
        }

        // 4. Blocked edge / wall collision check
        if (definition.graph.isBlocked(currentHead, position)) {
            return PuzzleEngineResult.Rejected(
                state, action, MoveRejectionReason.BLOCKED_BY_WALL,
                "Move from $currentHead to $position crosses a blocked edge/wall"
            )
        }

        // 5. Visited cell check (no self-intersection or revisitation)
        if (state.currentPath.contains(position)) {
            return PuzzleEngineResult.Rejected(
                state, action, MoveRejectionReason.CELL_ALREADY_VISITED,
                "Cell $position has already been covered by the path"
            )
        }

        // 6. Checkpoint validation
        val cpNumber = definition.getCheckpointAt(position)
        if (cpNumber != null) {
            // Checkpoint sequence order check: Must visit checkpoints in strictly ascending order
            if (cpNumber != state.nextRequiredCheckpoint) {
                return PuzzleEngineResult.Rejected(
                    state, action, MoveRejectionReason.WRONG_CHECKPOINT_ORDER,
                    "Visited checkpoint #$cpNumber out of order. Expected next checkpoint #${state.nextRequiredCheckpoint}"
                )
            }

            // Critical rule: Final checkpoint cannot be entered prematurely before all other cells are covered
            val isFinalCheckpoint = (cpNumber == definition.maxCheckpointNumber)
            if (isFinalCheckpoint) {
                val coverageAfterMove = state.coveredCellCount + 1
                if (coverageAfterMove < definition.totalRequiredCells) {
                    val remaining = definition.totalRequiredCells - coverageAfterMove
                    return PuzzleEngineResult.Rejected(
                        state, action, MoveRejectionReason.PREMATURE_FINAL_CHECKPOINT,
                        "Cannot enter final checkpoint #$cpNumber prematurely: $remaining required cells remain uncovered"
                    )
                }
            }
        }

        // Accept forward move!
        val newPath = state.currentPath.plus(position)
        val newMoveCount = state.moveCount + 1
        val newCoveredCount = state.coveredCellCount + 1
        val isCheckpointStep = (cpNumber != null)
        val newVisitedCpCount = if (isCheckpointStep) state.visitedCheckpointCount + 1 else state.visitedCheckpointCount
        val newNextCp = if (isCheckpointStep) state.nextRequiredCheckpoint + 1 else state.nextRequiredCheckpoint

        // Check if this move completes the puzzle
        val isFinalCp = (cpNumber == definition.maxCheckpointNumber)
        val isFullCoverage = (newCoveredCount == definition.totalRequiredCells)

        if (isFinalCp && isFullCoverage) {
            val completionCheck = CompletionValidator.validate(
                definition = definition,
                path = newPath,
                elapsedTimeMs = elapsedTimeMs,
                moveCount = newMoveCount,
                levelId = levelId,
                worldId = worldId
            )

            if (completionCheck is CompletionCheckResult.Success) {
                val completedState = state.copy(
                    currentPath = newPath,
                    nextRequiredCheckpoint = newNextCp,
                    visitedCheckpointCount = newVisitedCpCount,
                    coveredCellCount = newCoveredCount,
                    currentEndpoint = position,
                    moveCount = newMoveCount,
                    gameStatus = GameStatus.COMPLETED,
                    lastRejection = null,
                    completionResult = completionCheck.completionResult
                )
                return PuzzleEngineResult.Accepted(
                    state = completedState,
                    action = action,
                    completionEvent = completionCheck.completionResult
                )
            }
        }

        val inProgressState = state.copy(
            currentPath = newPath,
            nextRequiredCheckpoint = newNextCp,
            visitedCheckpointCount = newVisitedCpCount,
            coveredCellCount = newCoveredCount,
            currentEndpoint = position,
            moveCount = newMoveCount,
            gameStatus = GameStatus.IN_PROGRESS,
            lastRejection = null
        )

        return PuzzleEngineResult.Accepted(inProgressState, action)
    }

    private fun handleBacktrackTo(state: PuzzleGameState, position: GridPosition): PuzzleEngineResult {
        val action = PuzzleAction.BacktrackTo(position)

        if (state.isPaused) {
            return PuzzleEngineResult.Rejected(
                state, action, MoveRejectionReason.GAME_PAUSED,
                "Game is currently paused"
            )
        }
        if (state.currentPath.isEmpty) {
            return PuzzleEngineResult.Rejected(
                state, action, MoveRejectionReason.INVALID_ACTION,
                "Cannot backtrack when path is empty"
            )
        }
        if (!state.currentPath.contains(position)) {
            return PuzzleEngineResult.Rejected(
                state, action, MoveRejectionReason.INVALID_ACTION,
                "Position $position is not part of the active path"
            )
        }

        // If already at the position, no-op accepted
        if (position == state.currentPath.currentHead) {
            return PuzzleEngineResult.Accepted(state, action)
        }

        // Retract path back to target position
        val newPath = state.currentPath.retractTo(position)

        // Recalculate checkpoint progression along the remaining path
        val checkpointsInNewPath = newPath.positions.mapNotNull { definition.getCheckpointAt(it) }
        val newVisitedCpCount = checkpointsInNewPath.size
        val highestCp = checkpointsInNewPath.maxOrNull() ?: 1
        val newNextCp = highestCp + 1
        val newCoveredCount = newPath.coverageCount

        val newStatus = if (state.isCompleted) GameStatus.IN_PROGRESS else state.gameStatus

        val backtrackedState = state.copy(
            currentPath = newPath,
            nextRequiredCheckpoint = newNextCp,
            visitedCheckpointCount = newVisitedCpCount,
            coveredCellCount = newCoveredCount,
            currentEndpoint = position,
            gameStatus = newStatus,
            lastRejection = null,
            completionResult = null
        )

        return PuzzleEngineResult.Accepted(backtrackedState, action)
    }

    private fun handleBacktrackOne(state: PuzzleGameState): PuzzleEngineResult {
        val action = PuzzleAction.BacktrackOne

        if (state.isPaused) {
            return PuzzleEngineResult.Rejected(
                state, action, MoveRejectionReason.GAME_PAUSED,
                "Game is currently paused"
            )
        }
        if (state.currentPath.isEmpty) {
            return PuzzleEngineResult.Rejected(
                state, action, MoveRejectionReason.INVALID_ACTION,
                "Cannot backtrack when path is empty"
            )
        }
        // Backtracking at single start cell preserves the 1-cell starting state (Section 15)
        if (state.currentPath.size == 1) {
            return PuzzleEngineResult.Accepted(state, action)
        }

        val previousPos = state.currentPath.positions[state.currentPath.positions.size - 2]
        return handleBacktrackTo(state, previousPos)
    }

    private fun handleReset(state: PuzzleGameState): PuzzleEngineResult {
        val resetState = PuzzleGameState.initial(definition)
        return PuzzleEngineResult.Accepted(resetState, PuzzleAction.ResetPath)
    }

    private fun handlePause(state: PuzzleGameState): PuzzleEngineResult {
        return if (state.isInProgress) {
            val pausedState = state.copy(gameStatus = GameStatus.PAUSED)
            PuzzleEngineResult.Accepted(pausedState, PuzzleAction.PauseGame)
        } else {
            PuzzleEngineResult.Accepted(state, PuzzleAction.PauseGame)
        }
    }

    private fun handleResume(state: PuzzleGameState): PuzzleEngineResult {
        return if (state.isPaused) {
            val resumedState = state.copy(gameStatus = GameStatus.IN_PROGRESS)
            PuzzleEngineResult.Accepted(resumedState, PuzzleAction.ResumeGame)
        } else {
            PuzzleEngineResult.Accepted(state, PuzzleAction.ResumeGame)
        }
    }

    /**
     * Asserts that all state invariants hold (Section 22).
     * Useful in unit tests and automated verification.
     */
    fun assertInvariants(state: PuzzleGameState) {
        val path = state.currentPath
        if (path.isEmpty) {
            check(state.isNotStarted) { "Empty path must have status NOT_STARTED" }
            check(state.coveredCellCount == 0) { "Empty path must have 0 covered cells" }
            check(state.currentEndpoint == null) { "Empty path must have null endpoint" }
            return
        }

        // 1. Starts at checkpoint 1
        check(path.startPosition == definition.startCheckpoint?.position) {
            "Non-empty path must start at checkpoint 1"
        }

        // 2. All positions valid and within grid
        path.positions.forEach {
            check(definition.gridDimensions.contains(it)) { "Position $it must be within bounds" }
            check(definition.requiredCells.contains(it)) { "Position $it must be required" }
        }

        // 3. Orthogonal steps with no wall collisions
        for (i in 0 until path.size - 1) {
            val from = path.positions[i]
            val to = path.positions[i + 1]
            check(from.isOrthogonalNeighbor(to)) { "Step $from -> $to must be orthogonal" }
            check(!definition.graph.isBlocked(from, to)) { "Step $from -> $to must not cross a wall" }
        }

        // 4. Simple path: No duplicate positions
        check(!path.hasRevisitedCells) { "Path must not contain duplicate cells" }

        // 5. Endpoint matches last position
        check(state.currentEndpoint == path.currentHead) { "Endpoint must match last position" }

        // 6. Coverage count matches visited size
        check(state.coveredCellCount == path.coverageCount) { "Covered cell count must match path coverage" }

        // 7. Checkpoints visited in strict sequence
        val visitedCps = path.positions.mapNotNull { definition.getCheckpointAt(it) }
        for (i in visitedCps.indices) {
            check(visitedCps[i] == i + 1) { "Checkpoints must be in sequence 1..N, found ${visitedCps[i]} at index $i" }
        }

        // 8. Next required checkpoint
        val expectedNext = (visitedCps.maxOrNull() ?: 1) + 1
        check(state.nextRequiredCheckpoint == expectedNext) {
            "Expected next checkpoint $expectedNext, got ${state.nextRequiredCheckpoint}"
        }

        // 9. Completed implies full completion
        if (state.isCompleted) {
            check(state.coveredCellCount == definition.totalRequiredCells) { "Completed must have 100% coverage" }
            check(state.currentEndpoint == definition.finalCheckpoint?.position) { "Completed must end at final checkpoint" }
            check(state.completionResult != null) { "Completed must have non-null completionResult" }
        }
    }
}
