package com.zynpath.game.core.puzzle

import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridGraph
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckpointAndWallTest {

    // =========================================================================
    // 1. NumberedCheckpoint Tests
    // =========================================================================

    @Test
    fun numberedCheckpoint_validNumber_createsInstance() {
        val cp1 = NumberedCheckpoint(1, GridPosition(0, 0))
        val cp2 = NumberedCheckpoint(2, GridPosition(1, 1))

        assertTrue(cp1.isStart)
        assertFalse(cp2.isStart)
        assertEquals(1, cp1.number)
        assertEquals(GridPosition(0, 0), cp1.position)
        assertEquals("#1 at (0,0)", cp1.toString())
    }

    @Test(expected = IllegalArgumentException::class)
    fun numberedCheckpoint_zeroOrNegativeNumber_throwsException() {
        NumberedCheckpoint(0, GridPosition(0, 0))
    }

    @Test
    fun numberedCheckpoint_sorting_ordersAscendingByNumber() {
        val cp3 = NumberedCheckpoint(3, GridPosition(2, 2))
        val cp1 = NumberedCheckpoint(1, GridPosition(0, 0))
        val cp2 = NumberedCheckpoint(2, GridPosition(1, 1))

        val sorted = listOf(cp3, cp1, cp2).sorted()
        assertEquals(listOf(cp1, cp2, cp3), sorted)
    }

    // =========================================================================
    // 2. BlockedEdge (Wall) Tests
    // =========================================================================

    @Test
    fun blockedEdge_directionIndependentEquality_enforcesSymmetry() {
        val a = GridPosition(1, 1)
        val b = GridPosition(1, 2)

        val wall1 = BlockedEdge.between(a, b)
        val wall2 = BlockedEdge.between(b, a)

        assertEquals("Walls between same cells in reverse order must be equal", wall1, wall2)
        assertEquals("HashCodes must be identical", wall1.hashCode(), wall2.hashCode())

        // In a set, duplicate reverse order must collapse to a single element
        val set = setOf(wall1, wall2)
        assertEquals(1, set.size)

        // isBetween must return true regardless of argument order
        assertTrue(wall1.isBetween(a, b))
        assertTrue(wall1.isBetween(b, a))
        assertFalse(wall1.isBetween(a, GridPosition(1, 0)))
    }

    @Test
    fun blockedEdge_orientation_identifiesHorizontalAndVertical() {
        // Vertical wall separates columns (same row, adjacent columns)
        val verticalWall = BlockedEdge.between(GridPosition(2, 1), GridPosition(2, 2))
        assertTrue(verticalWall.isVerticalBoundary)
        assertFalse(verticalWall.isHorizontalBoundary)

        // Horizontal wall separates rows (same column, adjacent rows)
        val horizontalWall = BlockedEdge.between(GridPosition(1, 2), GridPosition(2, 2))
        assertTrue(horizontalWall.isHorizontalBoundary)
        assertFalse(horizontalWall.isVerticalBoundary)
    }

    @Test(expected = IllegalArgumentException::class)
    fun blockedEdge_diagonalCells_throwsException() {
        BlockedEdge.between(GridPosition(0, 0), GridPosition(1, 1))
    }

    @Test(expected = IllegalArgumentException::class)
    fun blockedEdge_nonAdjacentCells_throwsException() {
        BlockedEdge.between(GridPosition(0, 0), GridPosition(0, 2))
    }

    @Test(expected = IllegalArgumentException::class)
    fun blockedEdge_sameCell_throwsException() {
        BlockedEdge.between(GridPosition(1, 1), GridPosition(1, 1))
    }

    @Test
    fun blockedEdge_graphTraversal_rejectsBlockedStepAndAllowsLegalDetour() {
        val dims = GridDimensions(3, 3)
        val requiredCells = dims.allPositions().toSet()
        val c00 = GridPosition(0, 0)
        val c01 = GridPosition(0, 1)
        val c10 = GridPosition(1, 0)
        val c11 = GridPosition(1, 1)

        // Block edge between (0,0) and (0,1)
        val wall = BlockedEdge.between(c00, c01)
        val graph = GridGraph(dims, requiredCells, setOf(wall))

        // Direct traversal across the wall must be blocked
        assertTrue(graph.isBlocked(c00, c01))
        assertTrue(graph.isBlocked(c01, c00))
        assertFalse(graph.canTraverse(c00, c01))
        assertFalse(graph.canTraverse(c01, c00))

        // Legal traversal to (1,0) remains unblocked
        assertFalse(graph.isBlocked(c00, c10))
        assertTrue(graph.canTraverse(c00, c10))

        // Legal detour: (0,0) -> (1,0) -> (1,1) -> (0,1)
        assertTrue(graph.canTraverse(c00, c10))
        assertTrue(graph.canTraverse(c10, c11))
        assertTrue(graph.canTraverse(c11, c01))

        // Neighbors query from (0,0) must not include (0,1) in traversable neighbors
        val traversable = graph.getTraversableNeighbors(c00)
        assertFalse(traversable.contains(c01))
        assertTrue(traversable.contains(c10))
    }
}
