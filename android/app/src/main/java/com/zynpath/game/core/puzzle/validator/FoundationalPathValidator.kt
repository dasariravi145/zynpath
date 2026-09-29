package com.zynpath.game.core.puzzle.validator

import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath

/**
 * Result of validating an ordered path against a puzzle definition.
 */
sealed interface PathValidationResult {
    val isWin: Boolean get() = false

    /**
     * The path represents a valid, completed victory:
     * 1. Started at checkpoint 1.
     * 2. Visited all checkpoints in strict ascending order.
     * 3. Moved orthogonally with no wall collisions or diagonal steps.
     * 4. Covered 100% of required cells without revisiting any cell.
     * 5. Terminated at the final checkpoint.
     */
    data class ValidVictory(
        val totalCellsCovered: Int,
        val pathLength: Int
    ) : PathValidationResult {
        override val isWin: Boolean get() = true
    }

    /**
     * Path is structurally legal so far, but incomplete.
     * CRITICAL: Reaching the final checkpoint with uncovered cells is INCOMPLETE, NOT VICTORY.
     */
    data class Incomplete(
        val reason: IncompleteReason,
        val message: String,
        val coveredCount: Int,
        val totalRequired: Int
    ) : PathValidationResult

    /**
     * Path violates one of the non-negotiable puzzle rules.
     */
    data class Violation(
        val reason: ViolationReason,
        val message: String,
        val stepIndex: Int,
        val position: GridPosition?
    ) : PathValidationResult
}

enum class IncompleteReason {
    /** Path is active and legal, but has not yet reached the final checkpoint. */
    UNFINISHED_PATH,

    /**
     * Path reached the final checkpoint, but failed to cover 100% of required cells.
     * This is strictly NOT a victory.
     */
    INCOMPLETE_COVERAGE_AT_FINAL_CHECKPOINT
}

enum class ViolationReason {
    EMPTY_PATH,
    INVALID_START_CELL,
    OUT_OF_BOUNDS_STEP,
    EXCLUDED_CELL_STEP,
    NON_ORTHOGONAL_STEP,
    WALL_COLLISION,
    REVISITED_CELL,
    WRONG_CHECKPOINT_ORDER,
    PREMATURE_FINAL_CHECKPOINT,
    WRONG_FINAL_ENDPOINT
}

/**
 * Authoritative validator for continuous number-path puzzle routes.
 */
object FoundationalPathValidator {

    fun validate(definition: PuzzleDefinition, path: PuzzlePath): PathValidationResult {
        if (path.isEmpty) {
            return PathValidationResult.Violation(
                reason = ViolationReason.EMPTY_PATH,
                message = "Path cannot be empty",
                stepIndex = 0,
                position = null
            )
        }

        val startCp = definition.startCheckpoint
        if (startCp == null || path.startPosition != startCp.position) {
            return PathValidationResult.Violation(
                reason = ViolationReason.INVALID_START_CELL,
                message = "Path must begin at checkpoint #1 (${startCp?.position}), but started at ${path.startPosition}",
                stepIndex = 0,
                position = path.startPosition
            )
        }

        val visitedCells = HashSet<GridPosition>(path.size)
        var highestCheckpointVisited = 1 // Already at start checkpoint #1
        val finalCpNumber = definition.maxCheckpointNumber

        for (i in path.positions.indices) {
            val curr = path.positions[i]

            // 1. Grid boundary check
            if (!definition.gridDimensions.contains(curr)) {
                return PathValidationResult.Violation(
                    reason = ViolationReason.OUT_OF_BOUNDS_STEP,
                    message = "Step $i ($curr) is outside grid dimensions ${definition.gridDimensions}",
                    stepIndex = i,
                    position = curr
                )
            }

            // 2. Playable/required cell check
            if (!definition.requiredCells.contains(curr)) {
                return PathValidationResult.Violation(
                    reason = ViolationReason.EXCLUDED_CELL_STEP,
                    message = "Step $i ($curr) is an excluded/non-playable cell",
                    stepIndex = i,
                    position = curr
                )
            }

            // 3. Simple path: No cell revisitation
            if (!visitedCells.add(curr)) {
                return PathValidationResult.Violation(
                    reason = ViolationReason.REVISITED_CELL,
                    message = "Cell $curr was already visited earlier in the path (step $i)",
                    stepIndex = i,
                    position = curr
                )
            }

            // 4. Orthogonal movement & wall collisions
            if (i > 0) {
                val prev = path.positions[i - 1]
                if (!prev.isOrthogonalNeighbor(curr)) {
                    return PathValidationResult.Violation(
                        reason = ViolationReason.NON_ORTHOGONAL_STEP,
                        message = "Movement from $prev to $curr is non-orthogonal or non-adjacent (diagonal/jump step $i)",
                        stepIndex = i,
                        position = curr
                    )
                }

                if (definition.graph.isBlocked(prev, curr)) {
                    return PathValidationResult.Violation(
                        reason = ViolationReason.WALL_COLLISION,
                        message = "Movement from $prev to $curr crosses a blocked edge/wall",
                        stepIndex = i,
                        position = curr
                    )
                }
            }

            // 5. Checkpoint order validation
            val cpNumber = definition.getCheckpointAt(curr)
            if (cpNumber != null && i > 0) {
                val expectedNext = highestCheckpointVisited + 1
                if (cpNumber != expectedNext) {
                    return PathValidationResult.Violation(
                        reason = ViolationReason.WRONG_CHECKPOINT_ORDER,
                        message = "Visited checkpoint #$cpNumber out of order at step $i. Expected next checkpoint #$expectedNext",
                        stepIndex = i,
                        position = curr
                    )
                }

                // If this is the final checkpoint, it must not be visited before the end of the path
                if (cpNumber == finalCpNumber && i != path.positions.lastIndex) {
                    return PathValidationResult.Violation(
                        reason = ViolationReason.PREMATURE_FINAL_CHECKPOINT,
                        message = "Entered final checkpoint #$cpNumber at step $i before path termination",
                        stepIndex = i,
                        position = curr
                    )
                }

                highestCheckpointVisited = cpNumber
            }
        }

        // 6. Final endpoint check
        val finalCp = definition.finalCheckpoint
        val lastPos = path.currentHead

        if (lastPos != finalCp?.position) {
            return PathValidationResult.Incomplete(
                reason = IncompleteReason.UNFINISHED_PATH,
                message = "Path ended at $lastPos, but final checkpoint is #$finalCpNumber at ${finalCp?.position}",
                coveredCount = visitedCells.size,
                totalRequired = definition.totalRequiredCells
            )
        }

        // 7. Full coverage check
        val totalRequired = definition.totalRequiredCells
        val coveredCount = visitedCells.size

        if (coveredCount < totalRequired) {
            val missingCount = totalRequired - coveredCount
            return PathValidationResult.Incomplete(
                reason = IncompleteReason.INCOMPLETE_COVERAGE_AT_FINAL_CHECKPOINT,
                message = "Reaching final checkpoint #$finalCpNumber with $missingCount required cells uncovered is NOT victory ($coveredCount/$totalRequired covered)",
                coveredCount = coveredCount,
                totalRequired = totalRequired
            )
        }

        // 8. Victory confirmed
        return PathValidationResult.ValidVictory(
            totalCellsCovered = coveredCount,
            pathLength = path.size
        )
    }
}
