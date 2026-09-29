package com.zynpath.game.core.puzzle

import com.zynpath.game.core.puzzle.fixtures.SamplePuzzleFixtures
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.validator.DefinitionErrorCode
import com.zynpath.game.core.puzzle.validator.DefinitionValidationResult
import com.zynpath.game.core.puzzle.validator.PuzzleDefinitionValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PuzzleDefinitionValidationTest {

    // =========================================================================
    // 1. Valid Definitions
    // =========================================================================

    @Test
    fun sampleFixtures_validPuzzles_passStructuralValidation() {
        val r4x4 = PuzzleDefinitionValidator.validate(SamplePuzzleFixtures.puzzle4x4Valid)
        assertTrue("4x4 valid fixture must pass validation", r4x4 is DefinitionValidationResult.Valid)

        val r5x5 = PuzzleDefinitionValidator.validate(SamplePuzzleFixtures.puzzle5x5Valid)
        assertTrue("5x5 valid fixture must pass validation", r5x5 is DefinitionValidationResult.Valid)

        val r5x5Walls = PuzzleDefinitionValidator.validate(SamplePuzzleFixtures.puzzle5x5WithWalls)
        assertTrue("5x5 with walls fixture must pass validation", r5x5Walls is DefinitionValidationResult.Valid)
    }

    // =========================================================================
    // 2. Checkpoint Validation Errors
    // =========================================================================

    @Test
    fun validate_missingStartCheckpoint_rejected() {
        val fixture = SamplePuzzleFixtures.createMissingStartCheckpointFixture()
        val result = PuzzleDefinitionValidator.validate(fixture)

        assertFalse(result.isValid)
        assertTrue(result is DefinitionValidationResult.Invalid)
        val errors = (result as DefinitionValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == DefinitionErrorCode.MISSING_START_CHECKPOINT })
    }

    @Test
    fun validate_duplicateCheckpointNumber_rejected() {
        val fixture = SamplePuzzleFixtures.createDuplicateCheckpointNumberFixture()
        val result = PuzzleDefinitionValidator.validate(fixture)

        assertFalse(result.isValid)
        val errors = (result as DefinitionValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == DefinitionErrorCode.DUPLICATE_CHECKPOINT_NUMBER })
    }

    @Test
    fun validate_duplicateCheckpointPosition_rejected() {
        val fixture = SamplePuzzleFixtures.createDuplicateCheckpointPositionFixture()
        val result = PuzzleDefinitionValidator.validate(fixture)

        assertFalse(result.isValid)
        val errors = (result as DefinitionValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == DefinitionErrorCode.DUPLICATE_CHECKPOINT_POSITION })
    }

    @Test
    fun validate_nonContiguousCheckpointNumbers_rejected() {
        val fixture = SamplePuzzleFixtures.createNonContiguousCheckpointsFixture()
        val result = PuzzleDefinitionValidator.validate(fixture)

        assertFalse(result.isValid)
        val errors = (result as DefinitionValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == DefinitionErrorCode.NON_CONTIGUOUS_CHECKPOINTS })
    }

    @Test
    fun validate_outOfBoundsCheckpoint_rejected() {
        val fixture = SamplePuzzleFixtures.createOutOfBoundsCheckpointFixture()
        val result = PuzzleDefinitionValidator.validate(fixture)

        assertFalse(result.isValid)
        val errors = (result as DefinitionValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == DefinitionErrorCode.CHECKPOINT_OUT_OF_BOUNDS })
    }

    @Test
    fun validate_insufficientCheckpoints_rejected() {
        val puzzle = PuzzleDefinition(
            puzzleId = "only_one_cp",
            gridDimensions = GridDimensions(4, 4),
            requiredCells = GridDimensions(4, 4).allPositions().toSet(),
            checkpoints = listOf(NumberedCheckpoint(1, GridPosition(0, 0)))
        )
        val result = PuzzleDefinitionValidator.validate(puzzle)

        assertFalse(result.isValid)
        val errors = (result as DefinitionValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == DefinitionErrorCode.INSUFFICIENT_CHECKPOINTS })
    }

    @Test
    fun validate_identicalStartAndEnd_rejected() {
        val puzzle = PuzzleDefinition(
            puzzleId = "same_start_end",
            gridDimensions = GridDimensions(4, 4),
            requiredCells = GridDimensions(4, 4).allPositions().toSet(),
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(0, 0)) // Also duplicate pos
            )
        )
        val result = PuzzleDefinitionValidator.validate(puzzle)

        assertFalse(result.isValid)
        val errors = (result as DefinitionValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == DefinitionErrorCode.IDENTICAL_START_AND_END })
    }

    // =========================================================================
    // 3. Wall and Dimension Validation Errors
    // =========================================================================

    @Test
    fun validate_nonAdjacentWall_rejected() {
        val fixture = SamplePuzzleFixtures.createNonAdjacentWallFixture()
        val result = PuzzleDefinitionValidator.validate(fixture)

        assertFalse(result.isValid)
        val errors = (result as DefinitionValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == DefinitionErrorCode.WALL_NON_ADJACENT })
    }

    @Test
    fun validate_outOfBoundsWall_rejected() {
        val fixture = SamplePuzzleFixtures.createOutOfBoundsWallFixture()
        val result = PuzzleDefinitionValidator.validate(fixture)

        assertFalse(result.isValid)
        val errors = (result as DefinitionValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == DefinitionErrorCode.WALL_OUT_OF_BOUNDS })
    }

    @Test
    fun validate_blankIdAndZeroVersion_rejected() {
        val puzzle = PuzzleDefinition(
            puzzleId = "",
            puzzleVersion = 0,
            gridDimensions = GridDimensions(4, 4),
            requiredCells = GridDimensions(4, 4).allPositions().toSet(),
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(3, 3))
            )
        )
        val result = PuzzleDefinitionValidator.validate(puzzle)

        assertFalse(result.isValid)
        val errors = (result as DefinitionValidationResult.Invalid).errors
        assertTrue(errors.any { it.code == DefinitionErrorCode.INVALID_ID })
        assertTrue(errors.any { it.code == DefinitionErrorCode.INVALID_VERSION })
    }
}
