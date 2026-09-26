package com.zynpath.game

import com.zynpath.game.core.puzzle.model.SamplePuzzles
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SamplePuzzleValidityTest {

    @Test
    fun sample3x3_solvedBoard_satisfiesAllAuthoritativeRules() {
        val board = SamplePuzzles.Sample3x3_Solved

        // 1. Correct dimensions
        assertEquals(3, board.rowCount)
        assertEquals(3, board.columnCount)
        assertEquals(9, board.totalRequiredCells)

        // 2. Starts at 1
        assertEquals(board.startCoordinate, board.path.first())
        assertEquals(1, board.checkpoints[board.path.first()])

        // 3. Exactly all cells covered once (no revisits)
        assertEquals(9, board.coveredCellCount)
        assertEquals(9, board.path.size)
        assertEquals(9, board.path.distinct().size)

        // 4. Ends at max checkpoint 4
        assertEquals(board.finalCheckpointCoordinate, board.path.last())
        assertEquals(4, board.checkpoints[board.path.last()])

        // 5. Valid orthogonal steps
        for (i in 0 until board.path.size - 1) {
            val from = board.path[i]
            val to = board.path[i + 1]
            assertTrue("Step from $from to $to must be orthogonal", from.isOrthogonalNeighbor(to))
            assertFalse("Step from $from to $to must not cross a wall", board.hasWallBetween(from, to))
        }

        // 6. Ascending checkpoints
        val visitedCheckpoints = board.path.mapNotNull { board.checkpoints[it] }
        assertEquals(listOf(1, 2, 3, 4), visitedCheckpoints)

        // 7. Authoritative solve verification
        assertTrue("Sample 3x3 completed state must be marked solved", board.isSolved)
    }

    @Test
    fun sample3x3_uncompletedStates_areNotMarkedSolved() {
        // Base empty board
        assertFalse(SamplePuzzles.Sample3x3_Base.isSolved)

        // Steps 1 to 5 of tutorial
        for (step in 1..5) {
            val stepBoard = SamplePuzzles.getTutorialStepBoard(step)
            assertFalse("Tutorial step $step must not be solved until complete", stepBoard.isSolved)
        }

        // Step 6 must be solved
        val finalStep = SamplePuzzles.getTutorialStepBoard(6)
        assertTrue("Tutorial step 6 must be solved", finalStep.isSolved)
    }

    @Test
    fun sample4x4_solvedBoard_satisfiesAllAuthoritativeRules() {
        val board = SamplePuzzles.Sample4x4_Solved

        assertEquals(4, board.rowCount)
        assertEquals(4, board.columnCount)
        assertEquals(16, board.totalRequiredCells)

        // All 16 cells covered exactly once
        assertEquals(16, board.coveredCellCount)
        assertEquals(16, board.path.size)
        assertEquals(16, board.path.distinct().size)

        // Checkpoints visited in strictly ascending order: 1 -> 2 -> 3 -> 4 -> 5
        val visitedCheckpoints = board.path.mapNotNull { board.checkpoints[it] }
        assertEquals(listOf(1, 2, 3, 4, 5), visitedCheckpoints)

        // Orthogonal and wall-free
        for (i in 0 until board.path.size - 1) {
            val from = board.path[i]
            val to = board.path[i + 1]
            assertTrue(from.isOrthogonalNeighbor(to))
            assertFalse(board.hasWallBetween(from, to))
        }

        assertTrue("Sample 4x4 completed state must be marked solved", board.isSolved)
    }
}
