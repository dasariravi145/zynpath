package com.zynpath.game.core.puzzle.engine

import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult

/**
 * Result produced by the independent [CompletionValidator].
 */
sealed interface CompletionCheckResult {
    val isSuccess: Boolean get() = false

    data class Success(val completionResult: ValidatedCompletionResult) : CompletionCheckResult {
        override val isSuccess: Boolean get() = true
    }

    data class Failure(
        val reason: CompletionFailureReason,
        val message: String
    ) : CompletionCheckResult
}

enum class CompletionFailureReason {
    EMPTY_PATH,
    INVALID_START_CHECKPOINT,
    NON_ORTHOGONAL_STEP,
    WALL_COLLISION,
    REVISITED_CELL,
    INCOMPLETE_CELL_COVERAGE,
    WRONG_CHECKPOINT_ORDER,
    WRONG_FINAL_CHECKPOINT,
    INVALID_OR_EXCLUDED_CELL
}

/**
 * Dedicated, authoritative completion validator that independently verifies all 8 non-negotiable
 * conditions before awarding victory. Victory is never granted based solely on UI state,
 * checkpoint counters, or simple path length.
 */
object CompletionValidator {

    /**
     * Independently evaluates [path] against [definition].
     */
    fun validate(
        definition: PuzzleDefinition,
        path: PuzzlePath,
        elapsedTimeMs: Long = 1000L,
        moveCount: Int = path.size,
        hintCount: Int = 0,
        undoCount: Int = 0,
        resetCount: Int = 0,
        levelId: Int = 1,
        worldId: Int = 1
    ): CompletionCheckResult {
        // 1. Path must not be empty
        if (path.isEmpty) {
            return CompletionCheckResult.Failure(
                CompletionFailureReason.EMPTY_PATH,
                "Path is empty"
            )
        }

        // 2. Origin Rule: Path must start at checkpoint 1
        val startCp = definition.startCheckpoint
        if (startCp == null || path.startPosition != startCp.position) {
            return CompletionCheckResult.Failure(
                CompletionFailureReason.INVALID_START_CHECKPOINT,
                "Path must start at checkpoint #1 (${startCp?.position}), but started at ${path.startPosition}"
            )
        }

        val visited = HashSet<com.zynpath.game.core.puzzle.model.GridPosition>(path.size)
        var expectedCheckpoint = 1

        for (i in path.positions.indices) {
            val curr = path.positions[i]

            // 3. Coordinate validity & excluded cell check
            if (!definition.gridDimensions.contains(curr) || !definition.requiredCells.contains(curr)) {
                return CompletionCheckResult.Failure(
                    CompletionFailureReason.INVALID_OR_EXCLUDED_CELL,
                    "Step $i at $curr is outside grid or on an excluded cell"
                )
            }

            // 4. No revisit (simple path)
            if (!visited.add(curr)) {
                return CompletionCheckResult.Failure(
                    CompletionFailureReason.REVISITED_CELL,
                    "Cell $curr was revisited at step $i"
                )
            }

            // 5. Orthogonal movement & wall collisions
            if (i > 0) {
                val prev = path.positions[i - 1]
                if (!prev.isOrthogonalNeighbor(curr)) {
                    return CompletionCheckResult.Failure(
                        CompletionFailureReason.NON_ORTHOGONAL_STEP,
                        "Movement from $prev to $curr is non-orthogonal (step $i)"
                    )
                }

                if (definition.graph.isBlocked(prev, curr)) {
                    return CompletionCheckResult.Failure(
                        CompletionFailureReason.WALL_COLLISION,
                        "Movement from $prev to $curr crosses a wall (step $i)"
                    )
                }
            }

            // 6. Checkpoint order
            val cpNumber = definition.getCheckpointAt(curr)
            if (cpNumber != null) {
                if (cpNumber != expectedCheckpoint) {
                    return CompletionCheckResult.Failure(
                        CompletionFailureReason.WRONG_CHECKPOINT_ORDER,
                        "Visited checkpoint #$cpNumber at step $i out of order. Expected #$expectedCheckpoint"
                    )
                }
                expectedCheckpoint++
            }
        }

        // 7. Check that all checkpoints were visited
        val totalCheckpoints = definition.checkpoints.size
        if (expectedCheckpoint != totalCheckpoints + 1) {
            return CompletionCheckResult.Failure(
                CompletionFailureReason.WRONG_CHECKPOINT_ORDER,
                "Not all checkpoints were visited ($expectedCheckpoint - 1 visited out of $totalCheckpoints)"
            )
        }

        // 8. Terminal Condition: Ends at final checkpoint
        val finalCp = definition.finalCheckpoint
        if (finalCp == null || path.currentHead != finalCp.position) {
            return CompletionCheckResult.Failure(
                CompletionFailureReason.WRONG_FINAL_CHECKPOINT,
                "Path ended at ${path.currentHead}, but final checkpoint #${definition.maxCheckpointNumber} is at ${finalCp?.position}"
            )
        }

        // 9. Full Required Cell Coverage
        val totalRequired = definition.totalRequiredCells
        if (visited.size != totalRequired || visited != definition.requiredCells) {
            return CompletionCheckResult.Failure(
                CompletionFailureReason.INCOMPLETE_CELL_COVERAGE,
                "Path covered ${visited.size} of $totalRequired required cells. Incomplete board is NOT victory."
            )
        }

        // All 8 conditions satisfied!
        val completion = ValidatedCompletionResult(
            puzzleId = definition.puzzleId,
            levelId = levelId,
            worldId = worldId,
            isValidated = true,
            elapsedTimeMs = maxOf(1L, elapsedTimeMs),
            moveCount = maxOf(1, moveCount),
            hintCount = maxOf(0, hintCount),
            undoCount = maxOf(0, undoCount),
            resetCount = maxOf(0, resetCount),
            completedAt = System.currentTimeMillis(),
            puzzleSeed = definition.seed ?: 0L,
            puzzleVersion = definition.puzzleVersion,
            totalCellsCovered = visited.size
        )

        return CompletionCheckResult.Success(completion)
    }
}
