package com.zynpath.game.core.puzzle.ui

import androidx.compose.ui.geometry.Offset
import com.zynpath.game.core.puzzle.model.GridPosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Deterministic unit tests for [GridCoordinateMapper].
 *
 * Implements Prompt 12 Section 36:
 * - Tests corner cells (top-left, top-right, bottom-left, bottom-right).
 * - Tests cell centers and cell boundaries.
 * - Tests outside-board touch rejection.
 * - Tests 4x4, 5x5, 6x6, 7x7, and 8x8 grid dimensions.
 * - Tests square, tall, and wide viewport dimensions.
 * - Tests fast-finger movement path resolution.
 */
class GridCoordinateMapperTest {

    @Test
    fun `test 4x4 square viewport corner mapping`() {
        val mapper = GridCoordinateMapper(
            canvasWidth = 400f,
            canvasHeight = 400f,
            rowCount = 4,
            columnCount = 4,
            padding = 0f
        )

        assertEquals(100f, mapper.cellSize, 0.001f)
        assertEquals(0f, mapper.originX, 0.001f)
        assertEquals(0f, mapper.originY, 0.001f)

        // Top-left cell (0, 0)
        assertEquals(GridPosition(0, 0), mapper.offsetToGridPosition(Offset(10f, 10f)))
        assertEquals(Offset(50f, 50f), mapper.getCellCenter(GridPosition(0, 0)))

        // Top-right cell (0, 3)
        assertEquals(GridPosition(0, 3), mapper.offsetToGridPosition(Offset(390f, 10f)))
        assertEquals(Offset(350f, 50f), mapper.getCellCenter(GridPosition(0, 3)))

        // Bottom-left cell (3, 0)
        assertEquals(GridPosition(3, 0), mapper.offsetToGridPosition(Offset(10f, 390f)))
        assertEquals(Offset(50f, 350f), mapper.getCellCenter(GridPosition(3, 0)))

        // Bottom-right cell (3, 3)
        assertEquals(GridPosition(3, 3), mapper.offsetToGridPosition(Offset(390f, 390f)))
        assertEquals(Offset(350f, 350f), mapper.getCellCenter(GridPosition(3, 3)))
    }

    @Test
    fun `test outside-board touches return null`() {
        val mapper = GridCoordinateMapper(
            canvasWidth = 500f,
            canvasHeight = 800f,
            rowCount = 5,
            columnCount = 5,
            padding = 20f
        )

        // Above board
        assertNull(mapper.offsetToGridPosition(Offset(250f, 10f)))
        // Below board
        assertNull(mapper.offsetToGridPosition(Offset(250f, 790f)))
        // Left of board
        assertNull(mapper.offsetToGridPosition(Offset(10f, 400f)))
        // Right of board
        assertNull(mapper.offsetToGridPosition(Offset(490f, 400f)))
        // Negative coordinates
        assertNull(mapper.offsetToGridPosition(Offset(-50f, -50f)))
    }

    @Test
    fun `test cell center mapping for all grid positions`() {
        val mapper = GridCoordinateMapper(
            canvasWidth = 300f,
            canvasHeight = 300f,
            rowCount = 3,
            columnCount = 3,
            padding = 0f
        )

        for (r in 0 until 3) {
            for (c in 0 until 3) {
                val pos = GridPosition(r, c)
                val center = mapper.getCellCenter(pos)
                val mappedBack = mapper.offsetToGridPosition(center)
                assertEquals("Center offset $center must map back to $pos", pos, mappedBack)
            }
        }
    }

    @Test
    fun `test cell boundary mapping near inner borders`() {
        val mapper = GridCoordinateMapper(
            canvasWidth = 400f,
            canvasHeight = 400f,
            rowCount = 4,
            columnCount = 4,
            padding = 0f
        )

        // Boundary between (0, 0) and (0, 1) is at x = 100
        assertEquals(GridPosition(0, 0), mapper.offsetToGridPosition(Offset(99.9f, 50f)))
        assertEquals(GridPosition(0, 1), mapper.offsetToGridPosition(Offset(100.1f, 50f)))

        // Boundary between (1, 1) and (2, 1) is at y = 200
        assertEquals(GridPosition(1, 1), mapper.offsetToGridPosition(Offset(150f, 199.9f)))
        assertEquals(GridPosition(2, 1), mapper.offsetToGridPosition(Offset(150f, 200.1f)))
    }

    @Test
    fun `test various board dimensions from 4x4 to 8x8`() {
        val dimensions = listOf(4, 5, 6, 7, 8)
        for (dim in dimensions) {
            val mapper = GridCoordinateMapper(
                canvasWidth = 600f,
                canvasHeight = 600f,
                rowCount = dim,
                columnCount = dim,
                padding = 10f
            )

            assertTrue("Cell size must be positive for ${dim}x${dim}", mapper.cellSize > 0f)

            // Top-left
            val tl = mapper.offsetToGridPosition(mapper.getCellCenter(GridPosition(0, 0)))
            assertEquals(GridPosition(0, 0), tl)

            // Bottom-right
            val br = mapper.offsetToGridPosition(mapper.getCellCenter(GridPosition(dim - 1, dim - 1)))
            assertEquals(GridPosition(dim - 1, dim - 1), br)
        }
    }

    @Test
    fun `test tall and wide viewport aspect ratios`() {
        // Tall screen: 400 wide by 800 tall
        val tallMapper = GridCoordinateMapper(
            canvasWidth = 400f,
            canvasHeight = 800f,
            rowCount = 4,
            columnCount = 4,
            padding = 0f
        )
        assertEquals(100f, tallMapper.cellSize, 0.001f)
        assertEquals(0f, tallMapper.originX, 0.001f)
        assertEquals(200f, tallMapper.originY, 0.001f) // Vertically centered

        // Wide screen: 900 wide by 400 tall
        val wideMapper = GridCoordinateMapper(
            canvasWidth = 900f,
            canvasHeight = 400f,
            rowCount = 4,
            columnCount = 4,
            padding = 0f
        )
        assertEquals(100f, wideMapper.cellSize, 0.001f)
        assertEquals(250f, wideMapper.originX, 0.001f) // Horizontally centered
        assertEquals(0f, wideMapper.originY, 0.001f)
    }

    @Test
    fun `test fast finger movement intermediate path resolution`() {
        val mapper = GridCoordinateMapper(
            canvasWidth = 400f,
            canvasHeight = 400f,
            rowCount = 6,
            columnCount = 6
        )

        // 1. Same cell
        val same = mapper.resolveIntermediatePath(GridPosition(2, 2), GridPosition(2, 2))
        assertEquals(listOf(GridPosition(2, 2)), same)

        // 2. Orthogonal neighbor
        val neighbor = mapper.resolveIntermediatePath(GridPosition(2, 2), GridPosition(2, 3))
        assertEquals(listOf(GridPosition(2, 3)), neighbor)

        // 3. Same row rightward multi-step jump: (2, 1) -> (2, 4)
        val rightJump = mapper.resolveIntermediatePath(GridPosition(2, 1), GridPosition(2, 4))
        assertEquals(
            listOf(GridPosition(2, 2), GridPosition(2, 3), GridPosition(2, 4)),
            rightJump
        )

        // 4. Same row leftward multi-step jump: (2, 4) -> (2, 1)
        val leftJump = mapper.resolveIntermediatePath(GridPosition(2, 4), GridPosition(2, 1))
        assertEquals(
            listOf(GridPosition(2, 3), GridPosition(2, 2), GridPosition(2, 1)),
            leftJump
        )

        // 5. Same column downward multi-step jump: (1, 3) -> (4, 3)
        val downJump = mapper.resolveIntermediatePath(GridPosition(1, 3), GridPosition(4, 3))
        assertEquals(
            listOf(GridPosition(2, 3), GridPosition(3, 3), GridPosition(4, 3)),
            downJump
        )

        // 6. Same column upward multi-step jump: (4, 3) -> (1, 3)
        val upJump = mapper.resolveIntermediatePath(GridPosition(4, 3), GridPosition(1, 3))
        assertEquals(
            listOf(GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3)),
            upJump
        )

        // 7. Diagonal jump: (1, 1) -> (2, 2) must return null to prevent illegal diagonal shortcuts
        val diagonalJump = mapper.resolveIntermediatePath(GridPosition(1, 1), GridPosition(2, 2))
        assertNull("Diagonal jump must return null and be safely ignored", diagonalJump)

        // 8. Arbitrary non-straight jump: (1, 1) -> (3, 4) must return null
        val arbitraryJump = mapper.resolveIntermediatePath(GridPosition(1, 1), GridPosition(3, 4))
        assertNull("Non-straight jump must return null", arbitraryJump)
    }
}
