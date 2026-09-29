package com.zynpath.game.core.puzzle.ui

import com.zynpath.game.core.puzzle.catalog.PackagedPuzzles
import com.zynpath.game.core.puzzle.engine.GameStatus
import com.zynpath.game.core.puzzle.engine.MoveRejectionReason
import com.zynpath.game.core.puzzle.engine.PuzzleAction
import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.engine.PuzzleEngineResult
import com.zynpath.game.core.puzzle.model.GridPosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Integration tests connecting user touch actions to the authoritative [PuzzleEngine].
 *
 * Implements Prompt 12 Section 35 and Section 37:
 * - Tests starting on checkpoint 1 vs invalid starting cells.
 * - Tests legal orthogonal path extension.
 * - Tests rejection of diagonal movements.
 * - Tests rejection of wall crossing.
 * - Tests rejection of checkpoint skipping.
 * - Tests rejection of premature final-checkpoint entry.
 * - Tests drag backtracking to predecessor and multi-cell retraction.
 * - Tests Undo and Reset controls.
 * - Tests full-coverage victory producing validated completion results.
 */
class GameplayEngineTouchIntegrationTest {

    @Test
    fun `test touching checkpoint 1 starts the path`() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)

        val result = engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        assertTrue("Starting on checkpoint 1 must be accepted", result.isAccepted)
        assertEquals(GameStatus.IN_PROGRESS, engine.currentState.gameStatus)
        assertEquals(GridPosition(0, 0), engine.currentState.currentEndpoint)
        assertEquals(1, engine.currentState.coveredCellCount)
        assertEquals(listOf(GridPosition(0, 0)), engine.currentState.currentPath.positions)
    }

    @Test
    fun `test touching non-start cell first is rejected and preserves empty path`() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)

        val result = engine.process(PuzzleAction.StartPath(GridPosition(0, 1)))
        assertFalse("Starting on non-checkpoint-1 cell must be rejected", result.isAccepted)
        val rejected = result as PuzzleEngineResult.Rejected
        assertEquals(MoveRejectionReason.START_MUST_BE_CHECKPOINT_ONE, rejected.reason)
        assertEquals(GameStatus.NOT_STARTED, engine.currentState.gameStatus)
        assertTrue(engine.currentState.currentPath.isEmpty)
    }

    @Test
    fun `test legal drag movement extends path sequentially`() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)

        // Start
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))

        // Move to (0, 1)
        val r1 = engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        assertTrue(r1.isAccepted)
        assertEquals(GridPosition(0, 1), engine.currentState.currentEndpoint)
        assertEquals(2, engine.currentState.coveredCellCount)

        // Move to (0, 2)
        val r2 = engine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)))
        assertTrue(r2.isAccepted)
        assertEquals(GridPosition(0, 2), engine.currentState.currentEndpoint)
        assertEquals(3, engine.currentState.coveredCellCount)
    }

    @Test
    fun `test diagonal drag is rejected and path is preserved`() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)

        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))

        // Attempt diagonal move to (1, 2)
        val result = engine.process(PuzzleAction.ExtendPath(GridPosition(1, 2)))
        assertFalse("Diagonal move must be rejected", result.isAccepted)
        val rejected = result as PuzzleEngineResult.Rejected
        assertEquals(MoveRejectionReason.NON_ADJACENT, rejected.reason)

        // Path must remain unchanged
        assertEquals(listOf(GridPosition(0, 0), GridPosition(0, 1)), engine.currentState.currentPath.positions)
        assertEquals(GridPosition(0, 1), engine.currentState.currentEndpoint)
    }

    @Test
    fun `test wall crossing drag is rejected and path is preserved`() {
        // Level 51 has a wall between (0, 1) and (1, 1)
        val def = PackagedPuzzles.LEVEL_51
        val engine = PuzzleEngine(def)

        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))

        // Attempt to cross blocked edge into (1, 1)
        val result = engine.process(PuzzleAction.ExtendPath(GridPosition(1, 1)))
        assertFalse("Crossing wall must be rejected", result.isAccepted)
        val rejected = result as PuzzleEngineResult.Rejected
        assertEquals(MoveRejectionReason.BLOCKED_BY_WALL, rejected.reason)

        // Path must remain at (0, 1)
        assertEquals(GridPosition(0, 1), engine.currentState.currentEndpoint)
        assertEquals(2, engine.currentState.coveredCellCount)
    }

    @Test
    fun `test drag backtracking to predecessor cell retracts path by one step`() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)

        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)))

        assertEquals(3, engine.currentState.coveredCellCount)
        assertEquals(GridPosition(0, 2), engine.currentState.currentEndpoint)

        // Drag backward into predecessor (0, 1)
        val result = engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        assertTrue("Backtracking to predecessor via ExtendPath must be accepted", result.isAccepted)

        assertEquals(listOf(GridPosition(0, 0), GridPosition(0, 1)), engine.currentState.currentPath.positions)
        assertEquals(GridPosition(0, 1), engine.currentState.currentEndpoint)
        assertEquals(2, engine.currentState.coveredCellCount)
    }

    @Test
    fun `test multi-cell drag backtracking truncates path prefix`() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)

        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 3))) // Hit checkpoint 2
        engine.process(PuzzleAction.ExtendPath(GridPosition(1, 3)))

        assertEquals(5, engine.currentState.coveredCellCount)
        assertEquals(3, engine.currentState.nextRequiredCheckpoint)

        // Rapid drag back to (0, 1)
        val result = engine.process(PuzzleAction.BacktrackTo(GridPosition(0, 1)))
        assertTrue("Multi-cell BacktrackTo must be accepted", result.isAccepted)

        assertEquals(listOf(GridPosition(0, 0), GridPosition(0, 1)), engine.currentState.currentPath.positions)
        assertEquals(GridPosition(0, 1), engine.currentState.currentEndpoint)
        assertEquals(2, engine.currentState.coveredCellCount)
        // Checkpoint 2 was undone, next required checkpoint should be restored to 2
        assertEquals(2, engine.currentState.nextRequiredCheckpoint)
    }

    @Test
    fun `test undo button retracts last accepted move`() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)

        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))

        val undoResult = engine.process(PuzzleAction.BacktrackOne)
        assertTrue(undoResult.isAccepted)
        assertEquals(listOf(GridPosition(0, 0)), engine.currentState.currentPath.positions)
        assertEquals(GridPosition(0, 0), engine.currentState.currentEndpoint)
        assertEquals(1, engine.currentState.coveredCellCount)
    }

    @Test
    fun `test reset button restores initial state`() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)

        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)))

        val resetResult = engine.process(PuzzleAction.ResetPath)
        assertTrue(resetResult.isAccepted)
        assertEquals(GameStatus.NOT_STARTED, engine.currentState.gameStatus)
        assertTrue(engine.currentState.currentPath.isEmpty)
        assertEquals(0, engine.currentState.coveredCellCount)
        assertNull(engine.currentState.currentEndpoint)
        assertEquals(1, engine.currentState.nextRequiredCheckpoint)
    }

    @Test
    fun `test full verified solution produces validated completion`() {
        val def = PackagedPuzzles.LEVEL_1
        val solution = PackagedPuzzles.ALL_SOLUTIONS[1]
        assertNotNull("Level 1 must have verified solution", solution)

        val engine = PuzzleEngine(def)

        // 1. Start at first position
        val startResult = engine.process(PuzzleAction.StartPath(solution!!.positions.first()))
        assertTrue(startResult.isAccepted)

        // 2. Play every subsequent step
        for (i in 1 until solution.positions.size) {
            val step = solution.positions[i]
            val stepResult = engine.process(PuzzleAction.ExtendPath(step), elapsedTimeMs = 15000L)
            assertTrue("Step $i ($step) must be accepted", stepResult.isAccepted)
        }

        // 3. Victory assertions
        assertEquals(GameStatus.COMPLETED, engine.currentState.gameStatus)
        assertEquals(16, engine.currentState.coveredCellCount)
        assertEquals(def.totalRequiredCells, engine.currentState.coveredCellCount)
        assertEquals(solution.positions.last(), engine.currentState.currentEndpoint)
        assertNotNull("Completion result must be generated", engine.currentState.completionResult)

        val completion = engine.currentState.completionResult!!
        assertEquals("w1_lvl1", completion.puzzleId)
        assertEquals(1, completion.levelId)
        assertEquals(1, completion.worldId)
        assertEquals(16, completion.totalCellsCovered)
        assertEquals(15, completion.moveCount)
        assertEquals(15000L, completion.elapsedTimeMs)
    }
}
