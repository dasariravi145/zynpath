package com.zynpath.game.core.puzzle

import com.zynpath.game.core.puzzle.engine.GameStatus
import com.zynpath.game.core.puzzle.engine.PuzzleAction
import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.fixtures.SamplePuzzleFixtures
import com.zynpath.game.core.puzzle.model.GridPosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BacktrackingAndResetTest {

    private lateinit var engine: PuzzleEngine

    @Before
    fun setUp() {
        engine = PuzzleEngine(SamplePuzzleFixtures.puzzle4x4Valid)
    }

    // =========================================================================
    // 1. Single-Step Backtracking (Section 14)
    // =========================================================================

    @Test
    fun backtrackOne_removesLastEndpointAndRestoresPreviousState() {
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)))

        assertEquals(3, engine.currentState.coveredCellCount)
        assertEquals(GridPosition(0, 2), engine.currentState.currentEndpoint)

        // Undo one step
        val result = engine.process(PuzzleAction.BacktrackOne)
        assertTrue(result.isAccepted)
        assertEquals(2, engine.currentState.coveredCellCount)
        assertEquals(GridPosition(0, 1), engine.currentState.currentEndpoint)
        assertEquals(listOf(GridPosition(0, 0), GridPosition(0, 1)), engine.currentState.currentPath.positions)
        engine.assertInvariants(engine.currentState)
    }

    @Test
    fun dragBacktrack_movingToImmediatePredecessor_triggersIntuitiveUndo() {
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)))

        // Player drags finger backwards into (0, 1) — the immediately preceding cell!
        val backstepResult = engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))

        assertTrue("Stepping to predecessor cell must trigger intuitive backtracking", backstepResult.isAccepted)
        assertEquals(2, engine.currentState.coveredCellCount)
        assertEquals(GridPosition(0, 1), engine.currentState.currentEndpoint)
        assertEquals(listOf(GridPosition(0, 0), GridPosition(0, 1)), engine.currentState.currentPath.positions)
        engine.assertInvariants(engine.currentState)
    }

    // =========================================================================
    // 2. Multi-Cell Retraction & Checkpoint Restoration (Section 14)
    // =========================================================================

    @Test
    fun backtrackTo_acrossCheckpoint_restoresPreviousRequiredCheckpoint() {
        // 4x4 Checkpoints: #1 at (0,0), #2 at (1,3), #3 at (2,0), #4 at (3,0).
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 3)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(1, 3))) // Reached Checkpoint #2!
        engine.process(PuzzleAction.ExtendPath(GridPosition(1, 2)))

        assertEquals(3, engine.currentState.nextRequiredCheckpoint)
        assertEquals(2, engine.currentState.visitedCheckpointCount)
        assertEquals(6, engine.currentState.coveredCellCount)
        assertEquals(GridPosition(1, 2), engine.currentState.currentEndpoint)

        // Retract past checkpoint #2 back to (0, 2)
        val backtrackResult = engine.process(PuzzleAction.BacktrackTo(GridPosition(0, 2)))

        assertTrue(backtrackResult.isAccepted)
        assertEquals(GridPosition(0, 2), engine.currentState.currentEndpoint)
        assertEquals(3, engine.currentState.coveredCellCount)
        // Checkpoint #2 is no longer on path; next required checkpoint must revert to 2!
        assertEquals(2, engine.currentState.nextRequiredCheckpoint)
        assertEquals(1, engine.currentState.visitedCheckpointCount)
        engine.assertInvariants(engine.currentState)
    }

    // =========================================================================
    // 3. Start-Cell Backtracking Policy (Section 15)
    // =========================================================================

    @Test
    fun backtrackTo_checkpointOne_preservesOneCellStartingPath() {
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)))

        // Backtrack all the way back to start cell (0, 0)
        val result = engine.process(PuzzleAction.BacktrackTo(GridPosition(0, 0)))

        assertTrue(result.isAccepted)
        assertEquals(1, engine.currentState.coveredCellCount)
        assertEquals(GridPosition(0, 0), engine.currentState.currentEndpoint)
        assertEquals(GameStatus.IN_PROGRESS, engine.currentState.gameStatus)
        assertEquals(2, engine.currentState.nextRequiredCheckpoint)
        assertEquals(listOf(GridPosition(0, 0)), engine.currentState.currentPath.positions)
        engine.assertInvariants(engine.currentState)
    }

    // =========================================================================
    // 4. Reset Behavior (Section 20)
    // =========================================================================

    @Test
    fun resetPath_restoresInitialNotStartedState() {
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 3)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(1, 3))) // Checkpoint #2

        val resetResult = engine.process(PuzzleAction.ResetPath)

        assertTrue(resetResult.isAccepted)
        assertEquals(GameStatus.NOT_STARTED, engine.currentState.gameStatus)
        assertEquals(0, engine.currentState.coveredCellCount)
        assertEquals(0, engine.currentState.visitedCheckpointCount)
        assertEquals(1, engine.currentState.nextRequiredCheckpoint)
        assertNull(engine.currentState.currentEndpoint)
        assertTrue(engine.currentState.currentPath.isEmpty)
        assertEquals(0, engine.currentState.moveCount)
        engine.assertInvariants(engine.currentState)
    }
}
