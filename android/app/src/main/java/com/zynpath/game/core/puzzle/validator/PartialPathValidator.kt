package com.zynpath.game.core.puzzle.validator

import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath

/**
 * Result of validating a partial gameplay path against a [PuzzleDefinition].
 */
sealed class PartialPathValidationResult {
    data class Valid(
        val nextRequiredCheckpoint: Int,
        val remainingRequiredCells: Int,
        val isPrematureFinalCheckpoint: Boolean
    ) : PartialPathValidationResult()

    data class Invalid(val reason: String) : PartialPathValidationResult()
}

/**
 * Authoritative validator for partial gameplay paths before solver or hint execution.
 *
 * Implements Prompt 14 Section 12:
 * - Verifies the path begins at Checkpoint #1.
 * - Verifies consecutive cells are orthogonally adjacent.
 * - Verifies no blocked edge (wall) is crossed.
 * - Verifies no cell is repeated (simple self-avoiding path).
 * - Verifies checkpoints are visited in strictly ascending sequence ($1 \to 2 \dots \to K$).
 * - Detects premature final checkpoint entry when remaining required cells $> 0$.
 */
object PartialPathValidator {

    fun validate(definition: PuzzleDefinition, path: PuzzlePath): PartialPathValidationResult {
        if (path.isEmpty) {
            return PartialPathValidationResult.Invalid("Path cannot be empty")
        }

        val positions = path.positions
        val startCheckpoint = definition.checkpoints.find { it.number == 1 }
            ?: return PartialPathValidationResult.Invalid("Puzzle definition missing start checkpoint #1")
        val finalCheckpoint = definition.checkpoints.maxByOrNull { it.number }
            ?: return PartialPathValidationResult.Invalid("Puzzle definition missing final checkpoint")

        // 1. Verify origin is Checkpoint #1
        if (positions.first() != startCheckpoint.position) {
            return PartialPathValidationResult.Invalid(
                "Path must begin at checkpoint #1 (${startCheckpoint.position}), but began at ${positions.first()}"
            )
        }

        // 2. Track visited cells for duplicate detection & boundary validation
        val visited = HashSet<GridPosition>(positions.size)
        var nextExpectedCheckpoint = 2

        for (i in positions.indices) {
            val current = positions[i]

            // Boundary and required cell verification
            if (!definition.gridDimensions.contains(current)) {
                return PartialPathValidationResult.Invalid("Position $current is out of grid bounds")
            }
            if (!definition.isRequired(current)) {
                return PartialPathValidationResult.Invalid("Position $current is an excluded cell")
            }

            // Simple path (no cycles or revisits)
            if (!visited.add(current)) {
                return PartialPathValidationResult.Invalid("Path revisits cell at $current")
            }

            // Step adjacency and wall collision check
            if (i > 0) {
                val previous = positions[i - 1]
                if (!previous.isOrthogonalNeighbor(current)) {
                    return PartialPathValidationResult.Invalid(
                        "Non-orthogonal step between $previous and $current"
                    )
                }
                if (definition.graph.isBlocked(previous, current)) {
                    return PartialPathValidationResult.Invalid(
                        "Step between $previous and $current crosses a blocked wall edge"
                    )
                }
            }

            // Checkpoint sequence ordering
            val checkpointNumber = definition.getCheckpointAt(current)
            if (checkpointNumber != null && checkpointNumber > 1) {
                if (checkpointNumber != nextExpectedCheckpoint) {
                    return PartialPathValidationResult.Invalid(
                        "Checkpoints visited out of sequence: encountered #$checkpointNumber at $current, expected #$nextExpectedCheckpoint"
                    )
                }
                nextExpectedCheckpoint++
            }
        }

        val remainingRequired = definition.totalRequiredCells - positions.size
        val isPrematureFinal = positions.last() == finalCheckpoint.position && remainingRequired > 0

        return PartialPathValidationResult.Valid(
            nextRequiredCheckpoint = nextExpectedCheckpoint,
            remainingRequiredCells = remainingRequired,
            isPrematureFinalCheckpoint = isPrematureFinal
        )
    }
}
