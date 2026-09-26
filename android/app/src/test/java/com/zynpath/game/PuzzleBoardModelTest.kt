package com.zynpath.game

import com.zynpath.game.core.puzzle.model.GridCoordinate
import com.zynpath.game.core.puzzle.model.PuzzleBoardState
import com.zynpath.game.core.puzzle.model.WallEdge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PuzzleBoardModelTest {

    @Test
    fun gridCoordinate_orthogonalAdjacency_isAccurate() {
        val center = GridCoordinate(1, 1)

        // Orthogonal neighbors
        assertTrue(center.isOrthogonalNeighbor(GridCoordinate(0, 1))) // Up
        assertTrue(center.isOrthogonalNeighbor(GridCoordinate(2, 1))) // Down
        assertTrue(center.isOrthogonalNeighbor(GridCoordinate(1, 0))) // Left
        assertTrue(center.isOrthogonalNeighbor(GridCoordinate(1, 2))) // Right

        // Non-orthogonal (diagonal or distant)
        assertFalse(center.isOrthogonalNeighbor(GridCoordinate(0, 0))) // Diagonal
        assertFalse(center.isOrthogonalNeighbor(GridCoordinate(2, 2))) // Diagonal
        assertFalse(center.isOrthogonalNeighbor(GridCoordinate(1, 3))) // 2 steps
        assertFalse(center.isOrthogonalNeighbor(center))                // Same cell
    }

    @Test
    fun gridCoordinate_manhattanDistance_computesCorrectly() {
        val a = GridCoordinate(0, 0)
        val b = GridCoordinate(2, 3)
        assertEquals(5, a.manhattanDistance(b))
        assertEquals(5, b.manhattanDistance(a))
    }

    @Test
    fun wallEdge_canonicalOrdering_enforcesEquality() {
        val c1 = GridCoordinate(1, 2)
        val c2 = GridCoordinate(1, 3)

        val wall1 = WallEdge.between(c1, c2)
        val wall2 = WallEdge.between(c2, c1)

        assertEquals(wall1, wall2)
        assertEquals(wall1.hashCode(), wall2.hashCode())
        assertTrue(wall1.isBetween(c1, c2))
        assertTrue(wall1.isBetween(c2, c1))
    }

    @Test(expected = IllegalArgumentException::class)
    fun wallEdge_nonAdjacentCells_throwsException() {
        WallEdge.between(GridCoordinate(0, 0), GridCoordinate(1, 1))
    }

    @Test
    fun wallEdge_orientation_distinguishesHorizontalAndVertical() {
        val verticalWall = WallEdge.between(GridCoordinate(1, 2), GridCoordinate(1, 3))
        assertTrue(verticalWall.isVerticalBoundary)
        assertFalse(verticalWall.isHorizontalBoundary)

        val horizontalWall = WallEdge.between(GridCoordinate(1, 2), GridCoordinate(2, 2))
        assertTrue(horizontalWall.isHorizontalBoundary)
        assertFalse(horizontalWall.isVerticalBoundary)
    }

    @Test
    fun puzzleBoardState_metadataAndQueries_returnExpectedValues() {
        val board = PuzzleBoardState(
            rowCount = 4,
            columnCount = 4,
            checkpoints = mapOf(
                GridCoordinate(0, 0) to 1,
                GridCoordinate(3, 3) to 2
            ),
            walls = setOf(
                WallEdge.between(GridCoordinate(0, 0), GridCoordinate(0, 1))
            ),
            path = listOf(GridCoordinate(0, 0), GridCoordinate(1, 0))
        )

        assertEquals(16, board.totalRequiredCells)
        assertEquals(2, board.coveredCellCount)
        assertEquals(GridCoordinate(0, 0), board.startCoordinate)
        assertEquals(2, board.maxCheckpointNumber)
        assertEquals(GridCoordinate(3, 3), board.finalCheckpointCoordinate)
        assertEquals(GridCoordinate(1, 0), board.currentHead)

        assertTrue(board.isCovered(GridCoordinate(0, 0)))
        assertTrue(board.isCovered(GridCoordinate(1, 0)))
        assertFalse(board.isCovered(GridCoordinate(2, 0)))

        assertEquals(1, board.getCheckpointAt(GridCoordinate(0, 0)))
        assertNull(board.getCheckpointAt(GridCoordinate(1, 0)))

        assertTrue(board.hasWallBetween(GridCoordinate(0, 0), GridCoordinate(0, 1)))
        assertFalse(board.hasWallBetween(GridCoordinate(0, 0), GridCoordinate(1, 0)))
    }
}
