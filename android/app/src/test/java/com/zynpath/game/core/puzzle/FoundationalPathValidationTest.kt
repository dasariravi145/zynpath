package com.zynpath.game.core.puzzle

import com.zynpath.game.core.puzzle.fixtures.SamplePuzzleFixtures
import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import com.zynpath.game.core.puzzle.validator.FoundationalPathValidator
import com.zynpath.game.core.puzzle.validator.IncompleteReason
import com.zynpath.game.core.puzzle.validator.PathValidationResult
import com.zynpath.game.core.puzzle.validator.ViolationReason
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FoundationalPathValidationTest {

    // =========================================================================
    // 1. Valid Complete Victories
    // =========================================================================

    @Test
    fun pathValidation_valid4x4SerpentineRoute_achievesVictory() {
        val puzzle = SamplePuzzleFixtures.puzzle4x4Valid
        val path = SamplePuzzleFixtures.solution4x4Route

        val result = FoundationalPathValidator.validate(puzzle, path)
        assertTrue("Valid 4x4 route must be a win", result.isWin)
        assertTrue(result is PathValidationResult.ValidVictory)

        val victory = result as PathValidationResult.ValidVictory
        assertEquals(16, victory.totalCellsCovered)
        assertEquals(16, victory.pathLength)
    }

    @Test
    fun pathValidation_valid5x5SerpentineRoute_achievesVictory() {
        val puzzle = SamplePuzzleFixtures.puzzle5x5Valid
        val path = SamplePuzzleFixtures.solution5x5Route

        val result = FoundationalPathValidator.validate(puzzle, path)
        assertTrue("Valid 5x5 route must be a win", result.isWin)
        assertTrue(result is PathValidationResult.ValidVictory)

        val victory = result as PathValidationResult.ValidVictory
        assertEquals(25, victory.totalCellsCovered)
        assertEquals(25, victory.pathLength)
    }

    @Test
    fun pathValidation_valid5x5RouteWithWalls_navigatesAroundWallsAndWins() {
        val puzzle = SamplePuzzleFixtures.puzzle5x5WithWalls
        val path = SamplePuzzleFixtures.solution5x5Route

        val result = FoundationalPathValidator.validate(puzzle, path)
        assertTrue("Valid 5x5 route avoiding walls must win", result.isWin)
        assertTrue(result is PathValidationResult.ValidVictory)
    }

    // =========================================================================
    // 2. Explicit Mandatory Tests Required by Prompt 6 Specification
    // =========================================================================

    /**
     * PROMPT 6 MANDATORY TEST:
     * "VISITING ALL CHECKPOINTS WITHOUT COVERING EVERY REQUIRED CELL MUST NOT PRODUCE A WIN."
     */
    @Test
    fun visitingAllCheckpointsWithoutCoveringEveryRequiredCell_mustNotProduceAWin() {
        val puzzle = SamplePuzzleFixtures.puzzle4x4Valid
        // Checkpoints are: #1 at (0,0), #2 at (1,3), #3 at (2,0), #4 at (3,0).
        // Build a short route that visits 1 -> 2 -> 3 -> 4 directly but skips interior cells:
        val shortCutPath = PuzzlePath.of(
            GridPosition(0, 0), // #1
            GridPosition(0, 1),
            GridPosition(0, 2),
            GridPosition(0, 3),
            GridPosition(1, 3), // #2
            GridPosition(1, 2),
            GridPosition(1, 1),
            GridPosition(1, 0),
            GridPosition(2, 0), // #3
            GridPosition(3, 0)  // #4 (ends at final checkpoint #4, covering only 10 cells out of 16!)
        )

        val result = FoundationalPathValidator.validate(puzzle, shortCutPath)

        // Non-negotiable requirement: reaching final checkpoint with uncovered cells is strictly NOT victory
        assertFalse("Visiting all checkpoints with uncovered cells MUST NOT produce a win", result.isWin)
        assertTrue("Result must be Incomplete", result is PathValidationResult.Incomplete)

        val incomplete = result as PathValidationResult.Incomplete
        assertEquals(IncompleteReason.INCOMPLETE_COVERAGE_AT_FINAL_CHECKPOINT, incomplete.reason)
        assertEquals(10, incomplete.coveredCount)
        assertEquals(16, incomplete.totalRequired)
    }

    /**
     * PROMPT 6 MANDATORY TEST:
     * "COVERING ALL REQUIRED CELLS IN THE WRONG CHECKPOINT ORDER MUST NOT PRODUCE A WIN."
     */
    @Test
    fun coveringAllRequiredCellsInWrongCheckpointOrder_mustNotProduceAWin() {
        // Construct a 2x2 board where all 4 cells are required:
        // (0,0)=#1, (0,1)=#3, (1,0)=#2, (1,1) unnumbered.
        // If the player traverses: (0,0) -> (0,1) -> (1,1) -> (1,0)
        // They cover all 4 cells (100% coverage), but visit checkpoint #3 before #2!
        val puzzle = PuzzleDefinition.standard(
            puzzleId = "2x2_checkpoint_order_test",
            dimensions = GridDimensions(2, 2),
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(1, 0)),
                NumberedCheckpoint(3, GridPosition(0, 1))
            )
        )

        // Traversal covers all 4 cells, but visits #3 at step 1 before #2:
        val wrongOrderPath = PuzzlePath.of(
            GridPosition(0, 0), // #1
            GridPosition(0, 1), // Checkpoint #3 visited prematurely!
            GridPosition(1, 1),
            GridPosition(1, 0)  // Checkpoint #2
        )

        val result = FoundationalPathValidator.validate(puzzle, wrongOrderPath)

        // Non-negotiable requirement: covering all cells in wrong checkpoint order MUST NOT produce a win
        assertFalse("Covering all cells in wrong checkpoint order MUST NOT produce a win", result.isWin)
        assertTrue("Result must be Violation", result is PathValidationResult.Violation)

        val violation = result as PathValidationResult.Violation
        assertEquals(ViolationReason.WRONG_CHECKPOINT_ORDER, violation.reason)
        assertEquals(1, violation.stepIndex)
        assertEquals(GridPosition(0, 1), violation.position)
    }

    // =========================================================================
    // 3. Movement Rule Violations
    // =========================================================================

    @Test
    fun pathValidation_emptyPath_returnsViolation() {
        val puzzle = SamplePuzzleFixtures.puzzle4x4Valid
        val result = FoundationalPathValidator.validate(puzzle, PuzzlePath.empty())

        assertFalse(result.isWin)
        assertTrue(result is PathValidationResult.Violation)
        assertEquals(ViolationReason.EMPTY_PATH, (result as PathValidationResult.Violation).reason)
    }

    @Test
    fun pathValidation_invalidStartCell_returnsViolation() {
        val puzzle = SamplePuzzleFixtures.puzzle4x4Valid
        // Starts at (0, 1) instead of checkpoint #1 at (0, 0)
        val badStartPath = PuzzlePath.of(GridPosition(0, 1), GridPosition(0, 2))

        val result = FoundationalPathValidator.validate(puzzle, badStartPath)
        assertFalse(result.isWin)
        assertTrue(result is PathValidationResult.Violation)
        assertEquals(ViolationReason.INVALID_START_CELL, (result as PathValidationResult.Violation).reason)
    }

    @Test
    fun pathValidation_diagonalMovement_returnsViolation() {
        val puzzle = SamplePuzzleFixtures.puzzle4x4Valid
        // (0, 0) -> (1, 1) is diagonal!
        val diagonalPath = PuzzlePath.of(GridPosition(0, 0), GridPosition(1, 1))

        val result = FoundationalPathValidator.validate(puzzle, diagonalPath)
        assertFalse(result.isWin)
        assertTrue(result is PathValidationResult.Violation)
        assertEquals(ViolationReason.NON_ORTHOGONAL_STEP, (result as PathValidationResult.Violation).reason)
    }

    @Test
    fun pathValidation_revisitedCell_returnsViolation() {
        val puzzle = SamplePuzzleFixtures.puzzle4x4Valid
        // (0, 0) -> (0, 1) -> (0, 0) revisits (0, 0)
        val cyclePath = PuzzlePath.of(GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 0))

        val result = FoundationalPathValidator.validate(puzzle, cyclePath)
        assertFalse(result.isWin)
        assertTrue(result is PathValidationResult.Violation)
        assertEquals(ViolationReason.REVISITED_CELL, (result as PathValidationResult.Violation).reason)
    }

    @Test
    fun pathValidation_crossingWall_returnsViolation() {
        val puzzle = SamplePuzzleFixtures.puzzle5x5WithWalls
        // A wall exists between (0, 1) and (1, 1)
        val wallCrossingPath = PuzzlePath.of(
            GridPosition(0, 0),
            GridPosition(0, 1),
            GridPosition(1, 1) // Crosses wall!
        )

        val result = FoundationalPathValidator.validate(puzzle, wallCrossingPath)
        assertFalse(result.isWin)
        assertTrue(result is PathValidationResult.Violation)
        assertEquals(ViolationReason.WALL_COLLISION, (result as PathValidationResult.Violation).reason)
    }

    @Test
    fun pathValidation_prematureFinalCheckpoint_returnsViolation() {
        val puzzle = PuzzleDefinition.standard(
            puzzleId = "premature_test",
            dimensions = GridDimensions(3, 3),
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(0, 1)),
                NumberedCheckpoint(3, GridPosition(0, 2)) // Final checkpoint
            )
        )

        // Path visits final checkpoint (0, 2) at step 2, but then continues walking to (1, 2)
        val prematurePath = PuzzlePath.of(
            GridPosition(0, 0), // #1
            GridPosition(0, 1), // #2
            GridPosition(0, 2), // #3 (Final!)
            GridPosition(1, 2)  // Continued after final checkpoint
        )

        val result = FoundationalPathValidator.validate(puzzle, prematurePath)
        assertFalse(result.isWin)
        assertTrue(result is PathValidationResult.Violation)
        assertEquals(ViolationReason.PREMATURE_FINAL_CHECKPOINT, (result as PathValidationResult.Violation).reason)
    }

    // =========================================================================
    // 4. Path Mutation and Segment Tests
    // =========================================================================

    @Test
    fun puzzlePath_segmentsAndImmutability_behavesCorrectly() {
        val p = PuzzlePath.of(
            GridPosition(0, 0),
            GridPosition(0, 1),
            GridPosition(1, 1)
        )

        assertEquals(3, p.size)
        assertEquals(GridPosition(0, 0), p.startPosition)
        assertEquals(GridPosition(1, 1), p.currentHead)
        assertFalse(p.hasRevisitedCells)

        val segments = p.segments
        assertEquals(2, segments.size)
        assertEquals(GridPosition(0, 0), segments[0].from)
        assertEquals(GridPosition(0, 1), segments[0].to)
        assertTrue(segments[0].isHorizontal)

        assertEquals(GridPosition(0, 1), segments[1].from)
        assertEquals(GridPosition(1, 1), segments[1].to)
        assertTrue(segments[1].isVertical)

        // Plus
        val extended = p.plus(GridPosition(2, 1))
        assertEquals(4, extended.size)
        assertEquals(3, p.size) // Original immutable

        // DropLast (undo)
        val undid = extended.dropLast(1)
        assertEquals(p, undid)

        // RetractTo
        val retracted = extended.retractTo(GridPosition(0, 1))
        assertEquals(2, retracted.size)
        assertEquals(GridPosition(0, 1), retracted.currentHead)
    }
}
