package com.zynpath.game.core.puzzle

import com.zynpath.game.core.puzzle.engine.CompletionCheckResult
import com.zynpath.game.core.puzzle.engine.CompletionFailureReason
import com.zynpath.game.core.puzzle.engine.CompletionValidator
import com.zynpath.game.core.puzzle.engine.GameStatus
import com.zynpath.game.core.puzzle.engine.MoveRejectionReason
import com.zynpath.game.core.puzzle.engine.PuzzleAction
import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.engine.PuzzleEngineResult
import com.zynpath.game.core.puzzle.fixtures.SamplePuzzleFixtures
import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InteractiveCompletionTest {

    // =========================================================================
    // MANDATORY TESTS REQUIRED BY PROMPT 7 SECTION 26
    // =========================================================================

    /**
     * TEST A: All checkpoints visited, but some required cells uncovered.
     * Expected: NOT COMPLETED.
     */
    @Test
    fun testA_allCheckpointsVisitedWithUncoveredCells_notCompleted() {
        val puzzle = SamplePuzzleFixtures.puzzle4x4Valid
        // Checkpoints: #1 at (0,0), #2 at (1,3), #3 at (2,0), #4 at (3,0).
        // 10-step shortcut path that visits 1, 2, 3, 4 but leaves 6 cells uncovered:
        val shortPath = PuzzlePath.of(
            GridPosition(0, 0), // #1
            GridPosition(0, 1),
            GridPosition(0, 2),
            GridPosition(0, 3),
            GridPosition(1, 3), // #2
            GridPosition(1, 2),
            GridPosition(1, 1),
            GridPosition(1, 0),
            GridPosition(2, 0), // #3
            GridPosition(3, 0)  // #4
        )

        val completionCheck = CompletionValidator.validate(puzzle, shortPath)
        assertFalse("Path with uncovered cells must fail completion validation", completionCheck.isSuccess)
        assertTrue(completionCheck is CompletionCheckResult.Failure)
        assertEquals(
            CompletionFailureReason.INCOMPLETE_CELL_COVERAGE,
            (completionCheck as CompletionCheckResult.Failure).reason
        )
    }

    /**
     * TEST B: All required cells covered, but checkpoints visited in the wrong order.
     * Expected: NOT COMPLETED.
     */
    @Test
    fun testB_allCellsCoveredInWrongCheckpointOrder_notCompleted() {
        // 2x2 board: (0,0)=#1, (0,1)=#3, (1,0)=#2
        val puzzle = PuzzleDefinition.standard(
            puzzleId = "test_b_puzzle",
            dimensions = GridDimensions(2, 2),
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(1, 0)),
                NumberedCheckpoint(3, GridPosition(0, 1))
            )
        )
        // Traversal covers all 4 cells (0,0) -> (0,1) -> (1,1) -> (1,0),
        // but enters checkpoint #3 at step 1 before checkpoint #2!
        val wrongOrderPath = PuzzlePath.of(
            GridPosition(0, 0),
            GridPosition(0, 1),
            GridPosition(1, 1),
            GridPosition(1, 0)
        )

        val completionCheck = CompletionValidator.validate(puzzle, wrongOrderPath)
        assertFalse("Wrong checkpoint order must fail completion validation", completionCheck.isSuccess)
        assertTrue(completionCheck is CompletionCheckResult.Failure)
        assertEquals(
            CompletionFailureReason.WRONG_CHECKPOINT_ORDER,
            (completionCheck as CompletionCheckResult.Failure).reason
        )
    }

    /**
     * TEST C: All required cells covered, but a wall was crossed.
     * Expected: NOT COMPLETED.
     */
    @Test
    fun testC_allCellsCoveredCrossingWall_notCompleted() {
        val dims = GridDimensions(2, 2)
        val wall = BlockedEdge.between(GridPosition(0, 0), GridPosition(0, 1))
        val puzzle = PuzzleDefinition.standard(
            puzzleId = "test_c_puzzle",
            dimensions = dims,
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(1, 0))
            ),
            blockedEdges = setOf(wall)
        )
        // Path crosses wall between (0, 0) and (0, 1):
        val wallCrossingPath = PuzzlePath.of(
            GridPosition(0, 0),
            GridPosition(0, 1), // Crosses wall!
            GridPosition(1, 1),
            GridPosition(1, 0)
        )

        val completionCheck = CompletionValidator.validate(puzzle, wallCrossingPath)
        assertFalse("Crossing a wall must fail completion validation", completionCheck.isSuccess)
        assertTrue(completionCheck is CompletionCheckResult.Failure)
        assertEquals(
            CompletionFailureReason.WALL_COLLISION,
            (completionCheck as CompletionCheckResult.Failure).reason
        )
    }

    /**
     * TEST D: All required cells covered, but a cell was revisited.
     * Expected: NOT COMPLETED.
     */
    @Test
    fun testD_allCellsCoveredWithCellRevisited_notCompleted() {
        val puzzle = SamplePuzzleFixtures.puzzle4x4Valid
        // 17-step path on 16-cell board where (0,1) is revisited:
        val cyclePath = PuzzlePath.of(
            GridPosition(0, 0),
            GridPosition(0, 1),
            GridPosition(0, 2),
            GridPosition(0, 3),
            GridPosition(1, 3),
            GridPosition(1, 2),
            GridPosition(1, 1),
            GridPosition(0, 1), // Revisited!
            GridPosition(1, 0),
            GridPosition(2, 0),
            GridPosition(2, 1),
            GridPosition(2, 2),
            GridPosition(2, 3),
            GridPosition(3, 3),
            GridPosition(3, 2),
            GridPosition(3, 1),
            GridPosition(3, 0)
        )

        val completionCheck = CompletionValidator.validate(puzzle, cyclePath)
        assertFalse("Revisited cell must fail completion validation", completionCheck.isSuccess)
        assertTrue(completionCheck is CompletionCheckResult.Failure)
        assertEquals(
            CompletionFailureReason.REVISITED_CELL,
            (completionCheck as CompletionCheckResult.Failure).reason
        )
    }

    /**
     * TEST E: The final checkpoint is entered prematurely during interactive gameplay.
     * Expected: Move rejected with PREMATURE_FINAL_CHECKPOINT.
     */
    @Test
    fun testE_finalCheckpointEnteredPrematurely_moveRejected() {
        val engine = PuzzleEngine(SamplePuzzleFixtures.puzzle4x4Valid)
        // Checkpoints: #1 at (0,0), #2 at (1,3), #3 at (2,0), #4 at (3,0).
        // Move along path until (2, 0) [checkpoint #3]:
        val stepsToCp3 = listOf(
            GridPosition(0, 0),
            GridPosition(0, 1),
            GridPosition(0, 2),
            GridPosition(0, 3),
            GridPosition(1, 3), // #2
            GridPosition(1, 2),
            GridPosition(1, 1),
            GridPosition(1, 0),
            GridPosition(2, 0)  // #3
        )
        engine.process(PuzzleAction.StartPath(stepsToCp3.first()))
        for (step in stepsToCp3.drop(1)) {
            engine.process(PuzzleAction.ExtendPath(step))
        }

        assertEquals(GridPosition(2, 0), engine.currentState.currentEndpoint)
        assertEquals(4, engine.currentState.nextRequiredCheckpoint)
        assertEquals(9, engine.currentState.coveredCellCount)

        // (3, 0) is orthogonally adjacent to (2, 0) and is checkpoint #4 (the FINAL checkpoint)!
        // But only 9 out of 16 required cells are covered. Entering it now would be premature!
        val prematureMove = engine.process(PuzzleAction.ExtendPath(GridPosition(3, 0)))

        assertFalse("Premature final checkpoint move must be rejected", prematureMove.isAccepted)
        assertTrue(prematureMove is PuzzleEngineResult.Rejected)
        val rejected = prematureMove as PuzzleEngineResult.Rejected
        assertEquals(MoveRejectionReason.PREMATURE_FINAL_CHECKPOINT, rejected.reason)

        // Engine state is preserved: player remains at (2, 0), game remains IN_PROGRESS
        assertEquals(GridPosition(2, 0), engine.currentState.currentEndpoint)
        assertEquals(9, engine.currentState.coveredCellCount)
        assertEquals(GameStatus.IN_PROGRESS, engine.currentState.gameStatus)
        assertNull(engine.currentState.completionResult)
        engine.assertInvariants(engine.currentState)
    }

    /**
     * TEST F: Every required cell is covered exactly once, all checkpoints are visited in order,
     * all movements are legal, and the path ends at the final checkpoint.
     * Expected: COMPLETED with ValidatedCompletionResult.
     */
    @Test
    fun testF_validFullCoverageRoute_completesPuzzleWithAuthoritativeEvent() {
        val engine = PuzzleEngine(SamplePuzzleFixtures.puzzle4x4Valid)
        val verifiedRoute = SamplePuzzleFixtures.solution4x4Route.positions

        // Execute full 16-cell verified route step by step:
        engine.process(PuzzleAction.StartPath(verifiedRoute.first()))
        for (pos in verifiedRoute.drop(1)) {
            val result = engine.process(PuzzleAction.ExtendPath(pos))
            assertTrue("Step to $pos must be accepted", result.isAccepted)
        }

        // Final state verification
        assertEquals(GameStatus.COMPLETED, engine.currentState.gameStatus)
        assertTrue(engine.currentState.isCompleted)
        assertEquals(16, engine.currentState.coveredCellCount)
        assertEquals(15, engine.currentState.moveCount)
        assertEquals(GridPosition(3, 0), engine.currentState.currentEndpoint)
        assertEquals(verifiedRoute, engine.currentState.currentPath.positions)

        // Authoritative completion result
        val completion = engine.currentState.completionResult
        assertNotNull("Completion result must be generated", completion)
        assertTrue(completion!!.isValidated)
        assertEquals("fixture_4x4_clean", completion.puzzleId)
        assertEquals(15, completion.moveCount)

        // Forward moves must now be frozen because game is COMPLETED
        val moveAfterWin = engine.process(PuzzleAction.ExtendPath(GridPosition(3, 1)))
        assertFalse(moveAfterWin.isAccepted)
        assertEquals(
            MoveRejectionReason.GAME_ALREADY_COMPLETED,
            (moveAfterWin as PuzzleEngineResult.Rejected).reason
        )

        // UI Presentation mapping works seamlessly
        val boardState = engine.currentState.toBoardState()
        assertEquals(4, boardState.rowCount)
        assertEquals(4, boardState.columnCount)
        assertEquals(16, boardState.coveredCellCount)
        assertTrue("Mapped boardState must report isSolved == true", boardState.isSolved)

        engine.assertInvariants(engine.currentState)
    }

    @Test
    fun valid5x5WithWallsRoute_completesSuccessfully() {
        val engine = PuzzleEngine(SamplePuzzleFixtures.puzzle5x5WithWalls)
        val verifiedRoute = SamplePuzzleFixtures.solution5x5Route.positions

        engine.process(PuzzleAction.StartPath(verifiedRoute.first()))
        for (pos in verifiedRoute.drop(1)) {
            val res = engine.process(PuzzleAction.ExtendPath(pos))
            assertTrue("Step to $pos must be accepted", res.isAccepted)
        }

        assertEquals(GameStatus.COMPLETED, engine.currentState.gameStatus)
        assertEquals(25, engine.currentState.coveredCellCount)
        assertNotNull(engine.currentState.completionResult)
        engine.assertInvariants(engine.currentState)
    }
}
