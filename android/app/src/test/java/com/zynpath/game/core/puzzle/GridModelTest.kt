package com.zynpath.game.core.puzzle

import com.zynpath.game.core.puzzle.model.Direction
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridGraph
import com.zynpath.game.core.puzzle.model.GridPosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GridModelTest {

    // =========================================================================
    // 1. GridPosition Tests
    // =========================================================================

    @Test
    fun gridPosition_equalityAndHashing_behavesAsValueType() {
        val p1 = GridPosition(2, 3)
        val p2 = GridPosition(2, 3)
        val p3 = GridPosition(3, 2)

        assertEquals(p1, p2)
        assertEquals(p1.hashCode(), p2.hashCode())
        assertNotEquals(p1, p3)
        assertNotEquals(p1.hashCode(), p3.hashCode())
        assertEquals("(2,3)", p1.toString())
    }

    @Test
    fun gridPosition_comparison_ordersRowMajor() {
        val top = GridPosition(0, 1)
        val bottom = GridPosition(1, 0)
        val right = GridPosition(0, 2)

        assertTrue(top < bottom)
        assertTrue(top < right)
        assertTrue(right < bottom)
    }

    @Test
    fun gridPosition_orthogonalAdjacency_acceptsCardinalAndRejectsDiagonalAndSelf() {
        val center = GridPosition(2, 2)

        // 4 orthogonal directions
        assertTrue(center.isOrthogonalNeighbor(GridPosition(1, 2))) // UP
        assertTrue(center.isOrthogonalNeighbor(GridPosition(3, 2))) // DOWN
        assertTrue(center.isOrthogonalNeighbor(GridPosition(2, 1))) // LEFT
        assertTrue(center.isOrthogonalNeighbor(GridPosition(2, 3))) // RIGHT

        // Diagonal neighbors must be rejected
        assertFalse(center.isOrthogonalNeighbor(GridPosition(1, 1)))
        assertFalse(center.isOrthogonalNeighbor(GridPosition(1, 3)))
        assertFalse(center.isOrthogonalNeighbor(GridPosition(3, 1)))
        assertFalse(center.isOrthogonalNeighbor(GridPosition(3, 3)))

        // Self must be rejected
        assertFalse(center.isOrthogonalNeighbor(center))

        // Distant cells must be rejected
        assertFalse(center.isOrthogonalNeighbor(GridPosition(0, 2)))
        assertFalse(center.isOrthogonalNeighbor(GridPosition(2, 5)))
    }

    @Test
    fun gridPosition_manhattanDistance_computesExactMetric() {
        val a = GridPosition(1, 1)
        val b = GridPosition(4, 5)

        assertEquals(0, a.manhattanDistance(a))
        assertEquals(7, a.manhattanDistance(b)) // |1-4| + |1-5| = 3 + 4 = 7
        assertEquals(7, b.manhattanDistance(a))
    }

    @Test
    fun gridPosition_directionTo_returnsExpectedOrNull() {
        val pos = GridPosition(2, 2)

        assertEquals(Direction.UP, pos.directionTo(GridPosition(1, 2)))
        assertEquals(Direction.DOWN, pos.directionTo(GridPosition(3, 2)))
        assertEquals(Direction.LEFT, pos.directionTo(GridPosition(2, 1)))
        assertEquals(Direction.RIGHT, pos.directionTo(GridPosition(2, 3)))

        // Non-orthogonal returns null
        assertNull(pos.directionTo(GridPosition(1, 1)))
        assertNull(pos.directionTo(GridPosition(0, 2)))
    }

    @Test
    fun gridPosition_neighbor_shiftsCoordinatesCorrectly() {
        val origin = GridPosition(1, 1)
        assertEquals(GridPosition(0, 1), origin.neighbor(Direction.UP))
        assertEquals(GridPosition(2, 1), origin.neighbor(Direction.DOWN))
        assertEquals(GridPosition(1, 0), origin.neighbor(Direction.LEFT))
        assertEquals(GridPosition(1, 2), origin.neighbor(Direction.RIGHT))
    }

    // =========================================================================
    // 2. GridDimensions Tests
    // =========================================================================

    @Test
    fun gridDimensions_squareAndRectangular_calculatesCellCountsAndBounds() {
        val square = GridDimensions.square(4)
        assertTrue(square.isSquare)
        assertEquals(4, square.rows)
        assertEquals(4, square.columns)
        assertEquals(16, square.totalCellCount)
        assertEquals("4×4", square.toString())

        val rect = GridDimensions(3, 5)
        assertFalse(rect.isSquare)
        assertEquals(3, rect.rows)
        assertEquals(5, rect.columns)
        assertEquals(15, rect.totalCellCount)
        assertEquals("3×5", rect.toString())

        // Bounds checks
        assertTrue(rect.contains(0, 0))
        assertTrue(rect.contains(2, 4))
        assertTrue(rect.contains(GridPosition(1, 3)))

        assertFalse(rect.contains(-1, 0))
        assertFalse(rect.contains(0, -1))
        assertFalse(rect.contains(3, 4)) // Row out of bounds
        assertFalse(rect.contains(2, 5)) // Column out of bounds
        assertFalse(rect.contains(GridPosition(5, 5)))
    }

    @Test(expected = IllegalArgumentException::class)
    fun gridDimensions_zeroRows_throwsException() {
        GridDimensions(0, 4)
    }

    @Test(expected = IllegalArgumentException::class)
    fun gridDimensions_negativeColumns_throwsException() {
        GridDimensions(4, -1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun gridDimensions_exceedingMaxDimension_throwsException() {
        GridDimensions(GridDimensions.MAX_DIMENSION + 1, 10)
    }

    @Test
    fun gridDimensions_allPositions_enumeratesDeterministicallyInRowMajorOrder() {
        val dims = GridDimensions(2, 3)
        val positions = dims.allPositions()

        assertEquals(6, positions.size)
        assertEquals(
            listOf(
                GridPosition(0, 0),
                GridPosition(0, 1),
                GridPosition(0, 2),
                GridPosition(1, 0),
                GridPosition(1, 1),
                GridPosition(1, 2)
            ),
            positions
        )
    }

    // =========================================================================
    // 3. GridGraph Neighborhood Tests
    // =========================================================================

    @Test
    fun gridGraph_neighborQueries_respectCornersEdgesAndInterior() {
        val dims = GridDimensions(4, 4)
        val allCells = dims.allPositions().toSet()
        val graph = GridGraph(dims, allCells, emptySet())

        // 1. Corner cell (0, 0): exactly 2 neighbors (RIGHT and DOWN)
        val cornerNeighbors = graph.getNeighbors(GridPosition(0, 0))
        assertEquals(2, cornerNeighbors.size)
        assertTrue(cornerNeighbors.contains(GridPosition(0, 1))) // RIGHT
        assertTrue(cornerNeighbors.contains(GridPosition(1, 0))) // DOWN

        // 2. Edge cell (0, 1): exactly 3 neighbors (LEFT, RIGHT, DOWN)
        val edgeNeighbors = graph.getNeighbors(GridPosition(0, 1))
        assertEquals(3, edgeNeighbors.size)
        assertTrue(edgeNeighbors.contains(GridPosition(0, 0)))
        assertTrue(edgeNeighbors.contains(GridPosition(0, 2)))
        assertTrue(edgeNeighbors.contains(GridPosition(1, 1)))

        // 3. Interior cell (1, 1): exactly 4 neighbors (UP, RIGHT, DOWN, LEFT)
        val interiorNeighbors = graph.getNeighbors(GridPosition(1, 1))
        assertEquals(4, interiorNeighbors.size)
        assertTrue(interiorNeighbors.contains(GridPosition(0, 1)))
        assertTrue(interiorNeighbors.contains(GridPosition(1, 2)))
        assertTrue(interiorNeighbors.contains(GridPosition(2, 1)))
        assertTrue(interiorNeighbors.contains(GridPosition(1, 0)))
    }

    @Test
    fun direction_oppositeAndOrientation_behavesCorrectly() {
        assertEquals(Direction.DOWN, Direction.UP.opposite)
        assertEquals(Direction.UP, Direction.DOWN.opposite)
        assertEquals(Direction.RIGHT, Direction.LEFT.opposite)
        assertEquals(Direction.LEFT, Direction.RIGHT.opposite)

        assertTrue(Direction.UP.isVertical)
        assertTrue(Direction.DOWN.isVertical)
        assertFalse(Direction.LEFT.isVertical)
        assertFalse(Direction.RIGHT.isVertical)

        assertTrue(Direction.LEFT.isHorizontal)
        assertTrue(Direction.RIGHT.isHorizontal)
        assertFalse(Direction.UP.isHorizontal)
        assertFalse(Direction.DOWN.isHorizontal)
    }
}
