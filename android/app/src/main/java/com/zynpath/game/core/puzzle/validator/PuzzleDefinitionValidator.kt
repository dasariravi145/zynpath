package com.zynpath.game.core.puzzle.validator

import com.zynpath.game.core.puzzle.model.PuzzleDefinition

/**
 * Structured validation result for puzzle definitions.
 */
sealed interface DefinitionValidationResult {
    val isValid: Boolean

    data object Valid : DefinitionValidationResult {
        override val isValid: Boolean get() = true
    }

    data class Invalid(val errors: List<DefinitionError>) : DefinitionValidationResult {
        override val isValid: Boolean get() = false
        val errorSummary: String
            get() = errors.joinToString("; ") { "${it.code}: ${it.message}" }
    }
}

/**
 * Granular error descriptor indicating why a puzzle definition is structurally malformed.
 */
data class DefinitionError(
    val code: DefinitionErrorCode,
    val message: String
)

enum class DefinitionErrorCode {
    INVALID_ID,
    INVALID_VERSION,
    INVALID_DIMENSIONS,
    EMPTY_REQUIRED_CELLS,
    OUT_OF_BOUNDS_REQUIRED_CELL,
    INSUFFICIENT_CHECKPOINTS,
    MISSING_START_CHECKPOINT,
    NON_CONTIGUOUS_CHECKPOINTS,
    DUPLICATE_CHECKPOINT_NUMBER,
    DUPLICATE_CHECKPOINT_POSITION,
    CHECKPOINT_OUT_OF_BOUNDS,
    CHECKPOINT_ON_EXCLUDED_CELL,
    IDENTICAL_START_AND_END,
    INSUFFICIENT_CELLS_FOR_CHECKPOINTS,
    WALL_OUT_OF_BOUNDS,
    WALL_ON_EXCLUDED_CELL,
    WALL_NON_ADJACENT
}

/**
 * Authoritative validator ensuring puzzle definitions adhere to structural integrity rules before execution or solving.
 */
object PuzzleDefinitionValidator {

    fun validate(definition: PuzzleDefinition): DefinitionValidationResult {
        val errors = mutableListOf<DefinitionError>()

        // 1. Puzzle ID and Version
        if (definition.puzzleId.isBlank()) {
            errors.add(DefinitionError(DefinitionErrorCode.INVALID_ID, "Puzzle ID cannot be blank"))
        }
        if (definition.puzzleVersion < 1) {
            errors.add(DefinitionError(DefinitionErrorCode.INVALID_VERSION, "Puzzle version must be >= 1"))
        }

        // 2. Dimensions
        val dims = definition.gridDimensions
        if (dims.rows <= 0 || dims.columns <= 0) {
            errors.add(DefinitionError(DefinitionErrorCode.INVALID_DIMENSIONS, "Dimensions must be strictly positive"))
        }

        // 3. Required Cells
        if (definition.requiredCells.isEmpty()) {
            errors.add(DefinitionError(DefinitionErrorCode.EMPTY_REQUIRED_CELLS, "Puzzle must have at least one required cell"))
        }
        for (cell in definition.requiredCells) {
            if (!dims.contains(cell)) {
                errors.add(
                    DefinitionError(
                        DefinitionErrorCode.OUT_OF_BOUNDS_REQUIRED_CELL,
                        "Required cell $cell is out of grid bounds $dims"
                    )
                )
            }
        }

        // 4. Checkpoints Count & Start
        if (definition.checkpoints.size < 2) {
            errors.add(
                DefinitionError(
                    DefinitionErrorCode.INSUFFICIENT_CHECKPOINTS,
                    "A puzzle must have at least 2 checkpoints (start #1 and final #N), found ${definition.checkpoints.size}"
                )
            )
        }

        val hasStart = definition.checkpoints.any { it.number == 1 }
        if (!hasStart) {
            errors.add(DefinitionError(DefinitionErrorCode.MISSING_START_CHECKPOINT, "Puzzle must include starting checkpoint #1"))
        }

        // Checkpoint numbers uniqueness and contiguity
        val numbers = definition.checkpoints.map { it.number }
        val distinctNumbers = numbers.toSet()
        if (distinctNumbers.size != numbers.size) {
            errors.add(DefinitionError(DefinitionErrorCode.DUPLICATE_CHECKPOINT_NUMBER, "Checkpoint numbers must be unique"))
        }

        val sortedNumbers = numbers.sorted()
        if (sortedNumbers.isNotEmpty() && sortedNumbers.first() == 1) {
            for (i in sortedNumbers.indices) {
                val expected = i + 1
                if (sortedNumbers[i] != expected) {
                    errors.add(
                        DefinitionError(
                            DefinitionErrorCode.NON_CONTIGUOUS_CHECKPOINTS,
                            "Checkpoint sequence must be contiguous 1..N with no gaps. Expected #$expected but got #${sortedNumbers[i]}"
                        )
                    )
                    break
                }
            }
        }

        // Checkpoint positions uniqueness & bounds
        val positions = definition.checkpoints.map { it.position }
        val distinctPositions = positions.toSet()
        if (distinctPositions.size != positions.size) {
            errors.add(DefinitionError(DefinitionErrorCode.DUPLICATE_CHECKPOINT_POSITION, "Two or more checkpoints occupy the same position"))
        }

        for (cp in definition.checkpoints) {
            if (!dims.contains(cp.position)) {
                errors.add(
                    DefinitionError(
                        DefinitionErrorCode.CHECKPOINT_OUT_OF_BOUNDS,
                        "Checkpoint $cp is out of bounds $dims"
                    )
                )
            } else if (!definition.requiredCells.contains(cp.position)) {
                errors.add(
                    DefinitionError(
                        DefinitionErrorCode.CHECKPOINT_ON_EXCLUDED_CELL,
                        "Checkpoint $cp is placed on an excluded cell"
                    )
                )
            }
        }

        // Distinct start and end
        if (definition.startCheckpoint != null && definition.finalCheckpoint != null) {
            if (definition.startCheckpoint?.position == definition.finalCheckpoint?.position) {
                errors.add(
                    DefinitionError(
                        DefinitionErrorCode.IDENTICAL_START_AND_END,
                        "Start checkpoint #1 and final checkpoint #${definition.maxCheckpointNumber} cannot occupy the same position"
                    )
                )
            }
        }

        // Cell count vs checkpoint count
        if (definition.requiredCells.size < definition.checkpoints.size) {
            errors.add(
                DefinitionError(
                    DefinitionErrorCode.INSUFFICIENT_CELLS_FOR_CHECKPOINTS,
                    "Total required cells (${definition.requiredCells.size}) cannot be fewer than checkpoints (${definition.checkpoints.size})"
                )
            )
        }

        // 5. Blocked Edges (Walls)
        for (wall in definition.blockedEdges) {
            if (!dims.contains(wall.first) || !dims.contains(wall.second)) {
                errors.add(DefinitionError(DefinitionErrorCode.WALL_OUT_OF_BOUNDS, "Wall $wall connects out-of-bounds cells"))
            } else {
                if (!definition.requiredCells.contains(wall.first) || !definition.requiredCells.contains(wall.second)) {
                    errors.add(
                        DefinitionError(
                            DefinitionErrorCode.WALL_ON_EXCLUDED_CELL,
                            "Wall $wall is placed on a non-required cell"
                        )
                    )
                }
                if (!wall.first.isOrthogonalNeighbor(wall.second)) {
                    errors.add(DefinitionError(DefinitionErrorCode.WALL_NON_ADJACENT, "Wall $wall connects non-adjacent cells"))
                }
            }
        }

        return if (errors.isEmpty()) DefinitionValidationResult.Valid else DefinitionValidationResult.Invalid(errors)
    }
}
