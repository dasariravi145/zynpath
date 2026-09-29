package com.zynpath.game.core.puzzle

import com.zynpath.game.core.puzzle.engine.GameStatus
import com.zynpath.game.core.puzzle.engine.MoveRejectionReason
import com.zynpath.game.core.puzzle.engine.PuzzleAction
import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.fixtures.SamplePuzzleFixtures
import com.zynpath.game.core.puzzle.model.GridPosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InteractiveMovementTest {

    private lateinit var engine: PuzzleEngine

    @Before
    fun setUp() {
        engine = PuzzleEngine(SamplePuzzleFixtures.puzzle4x4Valid)
    }

    // =========================================================================
    // 1. Starting the Path (Section 8)
    // =========================================================================

    @Test
    fun startPath_atCheckpointOne_initializesPathSuccessfully() {
        val result = engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))

        assertTrue("Start at checkpoint 1 must be accepted", result.isAccepted)
        assertEquals(GameStatus.IN_PROGRESS, engine.currentState.gameStatus)
        assertEquals(1, engine.currentState.coveredCellCount)
        assertEquals(1, engine.currentState.visitedCheckpointCount)
        assertEquals(2, engine.currentState.nextRequiredCheckpoint)
        assertEquals(GridPosition(0, 0), engine.currentState.currentEndpoint)
        assertEquals(listOf(GridPosition(0, 0)), engine.currentState.currentPath.positions)
        engine.assertInvariants(engine.currentState)
    }

    @Test
    fun startPath_atOrdinaryCell_rejectsActionAndPreservesState() {
        // (0, 1) is an unnumbered cell on the 4x4 board
        val result = engine.process(PuzzleAction.StartPath(GridPosition(0, 1)))

        assertFalse("Start at ordinary cell must be rejected", result.isAccepted)
        assertEquals(GameStatus.NOT_STARTED, engine.currentState.gameStatus)
        assertEquals(0, engine.currentState.coveredCellCount)
        assertNull(engine.currentState.currentEndpoint)
        assertTrue(engine.currentState.currentPath.isEmpty)
        engine.assertInvariants(engine.currentState)
    }

    @Test
    fun startPath_atCheckpointTwo_rejectsActionAndPreservesState() {
        // (1, 3) is checkpoint #2 on the 4x4 board
        val result = engine.process(PuzzleAction.StartPath(GridPosition(1, 3)))

        assertFalse("Start at checkpoint 2 must be rejected", result.isAccepted)
        assertEquals(GameStatus.NOT_STARTED, engine.currentState.gameStatus)
        assertEquals(0, engine.currentState.coveredCellCount)
        engine.assertInvariants(engine.currentState)
    }

    // =========================================================================
    // 2. Orthogonal Movement & Bounds (Section 9)
    // =========================================================================

    @Test
    fun extendPath_legalOrthogonalSteps_accepted() {
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))

        // Horizontal step right: (0, 0) -> (0, 1)
        val step1 = engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        assertTrue("Horizontal step right must be accepted", step1.isAccepted)
        assertEquals(GridPosition(0, 1), engine.currentState.currentEndpoint)
        assertEquals(2, engine.currentState.coveredCellCount)
        assertEquals(1, engine.currentState.moveCount)
        engine.assertInvariants(engine.currentState)

        // Horizontal step right: (0, 1) -> (0, 2)
        val step2 = engine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)))
        assertTrue("Horizontal step right must be accepted", step2.isAccepted)
        assertEquals(GridPosition(0, 2), engine.currentState.currentEndpoint)
        assertEquals(3, engine.currentState.coveredCellCount)
        engine.assertInvariants(engine.currentState)

        // Vertical step down: (0, 2) -> (1, 2)
        val step3 = engine.process(PuzzleAction.ExtendPath(GridPosition(1, 2)))
        assertTrue("Vertical step down must be accepted", step3.isAccepted)
        assertEquals(GridPosition(1, 2), engine.currentState.currentEndpoint)
        assertEquals(4, engine.currentState.coveredCellCount)
        engine.assertInvariants(engine.currentState)
    }

    @Test
    fun extendPath_diagonalStep_rejectedWithNonAdjacent() {
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))

        // Diagonal move: (0, 0) -> (1, 1)
        val result = engine.process(PuzzleAction.ExtendPath(GridPosition(1, 1)))

        assertFalse("Diagonal move must be rejected", result.isAccepted)
        assertEquals(MoveRejectionReason.NON_ADJACENT, (result as com.zynpath.game.core.puzzle.engine.PuzzleEngineResult.Rejected).reason)
        assertEquals(GridPosition(0, 0), engine.currentState.currentEndpoint)
        assertEquals(1, engine.currentState.coveredCellCount)
        assertEquals(0, engine.currentState.moveCount)
        engine.assertInvariants(engine.currentState)
    }

    @Test
    fun extendPath_jumpAcrossMultipleCells_rejectedWithNonAdjacent() {
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))

        // 2-cell jump: (0, 0) -> (0, 2)
        val result = engine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)))

        assertFalse("Non-adjacent jump must be rejected", result.isAccepted)
        assertEquals(MoveRejectionReason.NON_ADJACENT, (result as com.zynpath.game.core.puzzle.engine.PuzzleEngineResult.Rejected).reason)
        assertEquals(GridPosition(0, 0), engine.currentState.currentEndpoint)
        engine.assertInvariants(engine.currentState)
    }

    @Test
    fun extendPath_outOfBounds_rejectedWithOutOfBounds() {
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))

        // Move off top of board: (0, 0) -> (-1, 0)
        val result = engine.process(PuzzleAction.ExtendPath(GridPosition(-1, 0)))

        assertFalse("Out of bounds step must be rejected", result.isAccepted)
        assertEquals(MoveRejectionReason.OUT_OF_BOUNDS, (result as com.zynpath.game.core.puzzle.engine.PuzzleEngineResult.Rejected).reason)
        assertEquals(GridPosition(0, 0), engine.currentState.currentEndpoint)
        engine.assertInvariants(engine.currentState)
    }

    // =========================================================================
    // 3. Wall Collisions (Section 10)
    // =========================================================================

    @Test
    fun extendPath_crossingWall_rejectedWithBlockedByWall() {
        val wallEngine = PuzzleEngine(SamplePuzzleFixtures.puzzle5x5WithWalls)
        // Walls on 5x5: (0, 1) | (1, 1), (0, 3) | (1, 3), (2, 1) | (3, 1)
        wallEngine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        wallEngine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))

        // Attempt to cross wall between (0, 1) and (1, 1)
        val blockedMove = wallEngine.process(PuzzleAction.ExtendPath(GridPosition(1, 1)))

        assertFalse("Move crossing wall must be rejected", blockedMove.isAccepted)
        assertEquals(
            MoveRejectionReason.BLOCKED_BY_WALL,
            (blockedMove as com.zynpath.game.core.puzzle.engine.PuzzleEngineResult.Rejected).reason
        )
        assertEquals(GridPosition(0, 1), wallEngine.currentState.currentEndpoint)

        // Legal detour around the wall is accepted: (0, 1) -> (0, 2)
        val legalMove = wallEngine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)))
        assertTrue("Legal step bypassing wall must be accepted", legalMove.isAccepted)
        assertEquals(GridPosition(0, 2), wallEngine.currentState.currentEndpoint)
        wallEngine.assertInvariants(wallEngine.currentState)
    }

    // =========================================================================
    // 4. Revisited-Cell Validation (Section 11)
    // =========================================================================

    @Test
    fun extendPath_revisitingOccupiedCell_rejectedWithCellAlreadyVisited() {
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(1, 1)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(1, 0)))

        // Loop attempt back into start: (1, 0) -> (0, 0)
        val loopMove = engine.process(PuzzleAction.ExtendPath(GridPosition(0, 0)))

        assertFalse("Move into already visited cell must be rejected", loopMove.isAccepted)
        assertEquals(
            MoveRejectionReason.CELL_ALREADY_VISITED,
            (loopMove as com.zynpath.game.core.puzzle.engine.PuzzleEngineResult.Rejected).reason
        )
        assertEquals(GridPosition(1, 0), engine.currentState.currentEndpoint)
        assertEquals(4, engine.currentState.coveredCellCount)
        engine.assertInvariants(engine.currentState)
    }

    // =========================================================================
    // 5. Checkpoint Sequence Progression (Section 12)
    // =========================================================================

    @Test
    fun extendPath_checkpointProgression_advancesSequenceAccurately() {
        // 4x4 Checkpoints: #1 at (0,0), #2 at (1,3), #3 at (2,0), #4 at (3,0).
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0))) // #1
        assertEquals(2, engine.currentState.nextRequiredCheckpoint)

        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 3)))
        assertEquals(2, engine.currentState.nextRequiredCheckpoint)

        // Enter checkpoint #2 at (1, 3)
        val cp2Step = engine.process(PuzzleAction.ExtendPath(GridPosition(1, 3)))
        assertTrue("Entering checkpoint 2 must be accepted", cp2Step.isAccepted)
        assertEquals(3, engine.currentState.nextRequiredCheckpoint)
        assertEquals(2, engine.currentState.visitedCheckpointCount)
        engine.assertInvariants(engine.currentState)
    }

    @Test
    fun extendPath_skippingCheckpoint_rejectedWithWrongCheckpointOrder() {
        // Construct a small 3x3 board where checkpoint 3 is adjacent to checkpoint 1:
        // (0,0)=#1, (0,1)=#3, (1,0)=#2
        val customPuzzle = com.zynpath.game.core.puzzle.model.PuzzleDefinition.standard(
            puzzleId = "skip_cp_test",
            dimensions = com.zynpath.game.core.puzzle.model.GridDimensions(3, 3),
            checkpoints = listOf(
                com.zynpath.game.core.puzzle.model.NumberedCheckpoint(1, GridPosition(0, 0)),
                com.zynpath.game.core.puzzle.model.NumberedCheckpoint(2, GridPosition(1, 0)),
                com.zynpath.game.core.puzzle.model.NumberedCheckpoint(3, GridPosition(0, 1))
            )
        )
        val customEngine = PuzzleEngine(customPuzzle)
        customEngine.process(PuzzleAction.StartPath(GridPosition(0, 0)))

        // Attempt to move from (0,0) [cp 1] to (0,1) [cp 3] while cp 2 is expected next
        val skipMove = customEngine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))

        assertFalse("Skipping checkpoint 2 to enter checkpoint 3 must be rejected", skipMove.isAccepted)
        assertEquals(
            MoveRejectionReason.WRONG_CHECKPOINT_ORDER,
            (skipMove as com.zynpath.game.core.puzzle.engine.PuzzleEngineResult.Rejected).reason
        )
        assertEquals(GridPosition(0, 0), customEngine.currentState.currentEndpoint)
        assertEquals(2, customEngine.currentState.nextRequiredCheckpoint)
        customEngine.assertInvariants(customEngine.currentState)
    }

    // =========================================================================
    // 6. Pause and Resume
    // =========================================================================

    @Test
    fun pauseAndResume_freezesAndResumesGameplay() {
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        engine.process(PuzzleAction.PauseGame)

        assertEquals(GameStatus.PAUSED, engine.currentState.gameStatus)

        // Moves while paused must be rejected
        val rejectedMove = engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        assertFalse(rejectedMove.isAccepted)
        assertEquals(MoveRejectionReason.GAME_PAUSED, (rejectedMove as com.zynpath.game.core.puzzle.engine.PuzzleEngineResult.Rejected).reason)

        // Resume restores IN_PROGRESS
        engine.process(PuzzleAction.ResumeGame)
        assertEquals(GameStatus.IN_PROGRESS, engine.currentState.gameStatus)

        val acceptedMove = engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        assertTrue(acceptedMove.isAccepted)
        assertEquals(GridPosition(0, 1), engine.currentState.currentEndpoint)
    }
}
