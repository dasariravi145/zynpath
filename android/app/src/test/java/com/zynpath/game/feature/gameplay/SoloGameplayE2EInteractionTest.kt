package com.zynpath.game.feature.gameplay

import com.zynpath.game.core.puzzle.catalog.PackagedPuzzles
import com.zynpath.game.core.puzzle.engine.GameStatus
import com.zynpath.game.core.puzzle.engine.MoveRejectionReason
import com.zynpath.game.core.puzzle.engine.PuzzleAction
import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.engine.PuzzleEngineResult
import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * End-to-End Verification of Solo Gameplay Interactions, Touch Movement, Wall Edge Invariants,
 * Dual Win Condition, Undo, and Reset.
 *
 * Implements Prompt 48 Requirements 23, 24, 25, 26, 27, 28, 29, 30, 31, 32.
 */
class SoloGameplayE2EInteractionTest {

    @Test
    fun testLevel1LoadsWithValidDimensionsAndCheckpoints() {
        val def = PackagedPuzzles.LEVEL_1
        assertEquals("w1_lvl1", def.puzzleId)
        assertEquals(4, def.gridDimensions.rows)
        assertEquals(4, def.gridDimensions.columns)
        assertEquals(16, def.gridDimensions.totalCells)
        assertEquals(5, def.checkpoints.size)
        // Checkpoint 1 at (0, 0)
        val cp1 = def.checkpoints.firstOrNull { it.position == GridPosition(0, 0) }
        assertNotNull(cp1)
        assertEquals(1, cp1?.number)
        assertTrue(cp1?.isStart == true)
    }

    @Test
    fun testStartingOnNonOriginCellIsRejected() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)

        // Attempt starting at (1, 1) which is not Checkpoint 1
        val res = engine.process(PuzzleAction.StartPath(GridPosition(1, 1)))
        assertFalse(res.isAccepted)
        assertEquals(MoveRejectionReason.START_MUST_BE_CHECKPOINT_ONE, (res as PuzzleEngineResult.Rejected).reason)
        assertEquals(GameStatus.NOT_STARTED, engine.currentState.gameStatus)
    }

    @Test
    fun testContinuousTouchDrawingExtendsPathOrthogonally() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)

        // Start at (0, 0)
        assertTrue(engine.process(PuzzleAction.StartPath(GridPosition(0, 0))).isAccepted)
        assertEquals(1, engine.currentState.coveredCellCount)

        // Move across top row: (0, 1) -> (0, 2) -> (0, 3)
        val steps = listOf(GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3))
        for (step in steps) {
            val res = engine.process(PuzzleAction.ExtendPath(step))
            assertTrue("Step to $step must be accepted", res.isAccepted)
        }

        assertEquals(4, engine.currentState.coveredCellCount)
        assertEquals(GridPosition(0, 3), engine.currentState.currentEndpoint)
        assertEquals(listOf(GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3)), engine.currentState.currentPath.positions)
    }

    @Test
    fun testBoundaryAndPrecisionInputValidation() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))

        // Attempting to step outside grid boundaries
        val outOfBounds = engine.process(PuzzleAction.ExtendPath(GridPosition(-1, 0)))
        assertFalse("Out-of-bounds coordinates must be rejected", outOfBounds.isAccepted)

        val outOfBoundsCol = engine.process(PuzzleAction.ExtendPath(GridPosition(0, -1)))
        assertFalse("Negative column must be rejected", outOfBoundsCol.isAccepted)
    }

    @Test
    fun testInvalidMovementRejectedWithoutPathCorruption() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))

        // 1. Diagonal move to (1, 2) from (0, 1) is rejected
        val diag = engine.process(PuzzleAction.ExtendPath(GridPosition(1, 2)))
        assertFalse(diag.isAccepted)
        assertEquals(MoveRejectionReason.NON_ADJACENT, (diag as PuzzleEngineResult.Rejected).reason)

        // Continue along valid path to reach Checkpoint 2 at (0, 3) then turn into row 1
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 3))) // CP2 visited
        engine.process(PuzzleAction.ExtendPath(GridPosition(1, 3)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(1, 2)))

        // 2. Self-intersection: move from (1, 2) up to (0, 2) which is already visited and not the immediate predecessor (1, 3)
        val loop = engine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)))
        assertFalse(loop.isAccepted)
        assertEquals(MoveRejectionReason.CELL_ALREADY_VISITED, (loop as PuzzleEngineResult.Rejected).reason)

        // Ensure path remains exactly 6 cells ending at (1, 2)
        assertEquals(6, engine.currentState.coveredCellCount)
        assertEquals(GridPosition(1, 2), engine.currentState.currentEndpoint)
    }

    @Test
    fun testWallEdgeRepresentationBlocksEdgeNotCell() {
        // Create 2x2 board with a wall between (0, 0) and (0, 1)
        val cells2x2 = (0..1).flatMap { r -> (0..1).map { c -> GridPosition(r, c) } }.toSet()
        val wallDef = PuzzleDefinition(
            puzzleId = "wall_test_2x2",
            puzzleVersion = 1,
            gridDimensions = GridDimensions(2, 2),
            requiredCells = cells2x2,
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(0, 1))
            ),
            blockedEdges = setOf(
                BlockedEdge(GridPosition(0, 0), GridPosition(0, 1))
            )
        )
        val engine = PuzzleEngine(wallDef)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))

        // Direct move to (0, 1) blocked by wall edge
        val wallMove = engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        assertFalse("Direct step through wall edge must be blocked", wallMove.isAccepted)
        assertEquals(MoveRejectionReason.WALL_COLLISION, (wallMove as PuzzleEngineResult.Rejected).reason)

        // Detour via (1, 0) -> (1, 1) -> (0, 1) must be valid and cover the entire board
        assertTrue(engine.process(PuzzleAction.ExtendPath(GridPosition(1, 0))).isAccepted)
        assertTrue(engine.process(PuzzleAction.ExtendPath(GridPosition(1, 1))).isAccepted)
        assertTrue(engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1))).isAccepted)

        assertEquals("All 4 cells covered", 4, engine.currentState.coveredCellCount)
        assertEquals(GameStatus.SOLVED, engine.currentState.gameStatus)
    }

    @Test
    fun testDualWinConditionPrematureFinalCheckpointRejected() {
        // Create 3x3 board where final checkpoint 3 is at (1, 1) and checkpoint 2 is at (1, 0)
        val cells3x3 = (0..2).flatMap { r -> (0..2).map { c -> GridPosition(r, c) } }.toSet()
        val def = PuzzleDefinition(
            puzzleId = "dual_win_test_3x3",
            puzzleVersion = 1,
            gridDimensions = GridDimensions(3, 3),
            requiredCells = cells3x3,
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(1, 0)),
                NumberedCheckpoint(3, GridPosition(1, 1))
            ),
            blockedEdges = emptySet()
        )
        val engine = PuzzleEngine(def)

        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(1, 0)))

        // Prematurely step into final checkpoint 3 at (1, 1) with only 3 of 9 cells covered!
        val premature = engine.process(PuzzleAction.ExtendPath(GridPosition(1, 1)))
        assertFalse("Stepping into final checkpoint without full-grid coverage must be rejected", premature.isAccepted)
        assertEquals(MoveRejectionReason.FINAL_CHECKPOINT_PREMATURE, (premature as PuzzleEngineResult.Rejected).reason)
        assertEquals(GameStatus.IN_PROGRESS, engine.currentState.gameStatus)
    }

    @Test
    fun testUndoUpdatesBothEngineAndVisiblePath() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)))

        assertEquals(3, engine.currentState.coveredCellCount)
        assertEquals(GridPosition(0, 2), engine.currentState.currentEndpoint)

        // Undo one step
        val undoRes = engine.process(PuzzleAction.UndoLastMove)
        assertTrue(undoRes.isAccepted)
        assertEquals(2, engine.currentState.coveredCellCount)
        assertEquals(GridPosition(0, 1), engine.currentState.currentEndpoint)
        assertFalse("Cell (0, 2) must no longer be covered after undo", engine.currentState.isCellCovered(GridPosition(0, 2)))
    }

    @Test
    fun testResetClearsEntirePathBackToOriginCheckpoint() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 3)))

        assertEquals(4, engine.currentState.coveredCellCount)

        // Reset
        val resetRes = engine.process(PuzzleAction.ResetPuzzle)
        assertTrue(resetRes.isAccepted)
        assertEquals(0, engine.currentState.coveredCellCount)
        assertEquals(null, engine.currentState.currentEndpoint)
        assertTrue(engine.currentState.currentPath.isEmpty)
        assertEquals(GameStatus.NOT_STARTED, engine.currentState.gameStatus)
    }

    @Test
    fun testFullCoverageAndOrderedCheckpointsTriggersVictory() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)
        val solution = PackagedPuzzles.SOLUTION_1_ROUTE.positions

        // Walk the canonical complete solution
        for (i in solution.indices) {
            if (i == 0) {
                engine.process(PuzzleAction.StartPath(solution[i]))
            } else {
                val step = engine.process(PuzzleAction.ExtendPath(solution[i]))
                assertTrue("Step $i to ${solution[i]} must be accepted", step.isAccepted)
            }
        }

        assertEquals(16, engine.currentState.coveredCellCount)
        assertEquals(GameStatus.SOLVED, engine.currentState.gameStatus)
        assertTrue("Board must be fully covered", engine.currentState.isFullyCovered)
        assertEquals(5, engine.currentState.highestCheckpointVisited)
    }
}
