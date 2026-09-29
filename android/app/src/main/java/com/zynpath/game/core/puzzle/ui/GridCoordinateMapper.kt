package com.zynpath.game.core.puzzle.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.zynpath.game.core.puzzle.model.GridPosition
import kotlin.math.min

/**
 * Deterministic geometry mapper converting between screen Canvas pixel coordinates
 * and 2D discrete [GridPosition] cells on a Zynpath puzzle board.
 *
 * Accounts for board width/height, row/column counts, padding, centered aspect ratio,
 * and handles safe intermediate cell interpolation during fast finger movement.
 */
data class GridCoordinateMapper(
    val canvasWidth: Float,
    val canvasHeight: Float,
    val rowCount: Int,
    val columnCount: Int,
    val padding: Float = 0f
) {
    init {
        require(rowCount > 0 && columnCount > 0) { "Grid dimensions must be positive ($rowCount x $columnCount)" }
    }

    val availableWidth: Float = (canvasWidth - 2 * padding).coerceAtLeast(0f)
    val availableHeight: Float = (canvasHeight - 2 * padding).coerceAtLeast(0f)

    val cellSize: Float = if (rowCount > 0 && columnCount > 0 && availableWidth > 0 && availableHeight > 0) {
        min(availableWidth / columnCount, availableHeight / rowCount)
    } else {
        0f
    }

    val boardWidth: Float = cellSize * columnCount
    val boardHeight: Float = cellSize * rowCount

    val originX: Float = padding + (availableWidth - boardWidth) / 2f
    val originY: Float = padding + (availableHeight - boardHeight) / 2f

    /**
     * Converts a Canvas [Offset] in pixels to a discrete [GridPosition], or null if outside the board bounds.
     */
    fun offsetToGridPosition(offset: Offset): GridPosition? {
        if (cellSize <= 0f) return null
        val x = offset.x
        val y = offset.y

        if (x < originX || x >= originX + boardWidth || y < originY || y >= originY + boardHeight) {
            return null
        }

        val col = ((x - originX) / cellSize).toInt().coerceIn(0, columnCount - 1)
        val row = ((y - originY) / cellSize).toInt().coerceIn(0, rowCount - 1)
        return GridPosition(row, col)
    }

    /**
     * Returns the pixel center of the cell at [position].
     */
    fun getCellCenter(position: GridPosition): Offset {
        val cx = originX + (position.column + 0.5f) * cellSize
        val cy = originY + (position.row + 0.5f) * cellSize
        return Offset(cx, cy)
    }

    /**
     * Returns the bounding rectangle of the cell at [position] in pixels.
     */
    fun getCellBounds(position: GridPosition): Rect {
        val left = originX + position.column * cellSize
        val top = originY + position.row * cellSize
        return Rect(left, top, left + cellSize, top + cellSize)
    }

    /**
     * Fast finger movement handling (Prompt 12 Section 13 & Prompt 37 Section 20).
     *
     * When a drag event skips intermediate cells between [from] and [to],
     * resolves the movement into an ordered sequence of strictly orthogonal adjacent cells.
     *
     * Rules:
     * 1. If [from] == [to], returns listOf(to).
     * 2. If [from] and [to] are orthogonal neighbors, returns listOf(to).
     * 3. Straight lines along row or column return all intermediate cells in sequence.
     * 4. Multi-axis/diagonal jumps decompose into discrete orthogonal steps along the dominant axis
     *    up to a bounded distance (max 5 cells), preventing skipped cells during rapid swipes while
     *    retaining strict orthogonal adjacency for each step.
     */
    fun resolveIntermediatePath(
        from: GridPosition,
        to: GridPosition,
        touchOffset: Offset? = null
    ): List<GridPosition>? {
        if (from == to) return listOf(to)

        if (from.isOrthogonalNeighbor(to)) {
            return listOf(to)
        }

        val rowDiff = to.row - from.row
        val colDiff = to.column - from.column
        val manhattanDist = kotlin.math.abs(rowDiff) + kotlin.math.abs(colDiff)
        if (manhattanDist > 6) {
            // Unusually large jump (teleport/glitch), reject to preserve gameplay integrity
            return null
        }

        // Horizontal straight line
        if (from.row == to.row) {
            val step = if (to.column > from.column) 1 else -1
            val path = mutableListOf<GridPosition>()
            var c = from.column + step
            while (if (step > 0) c <= to.column else c >= to.column) {
                path.add(GridPosition(from.row, c))
                c += step
            }
            return path
        }

        // Vertical straight line
        if (from.column == to.column) {
            val step = if (to.row > from.row) 1 else -1
            val path = mutableListOf<GridPosition>()
            var r = from.row + step
            while (if (step > 0) r <= to.row else r >= to.row) {
                path.add(GridPosition(r, from.column))
                r += step
            }
            return path
        }

        // Corner diagonal step (|dr| == 1 && |dc| == 1):
        // Resolves rapid cornering without skipping cells or creating illegal diagonal moves.
        // Decomposes into two strictly orthogonal steps ordered by touch vector dominance.
        if (touchOffset != null && kotlin.math.abs(rowDiff) == 1 && kotlin.math.abs(colDiff) == 1) {
            val fromCenter = getCellCenter(from)
            val horizontalDominant = kotlin.math.abs(touchOffset.x - fromCenter.x) > kotlin.math.abs(touchOffset.y - fromCenter.y)

            return if (horizontalDominant) {
                listOf(GridPosition(from.row, to.column), to)
            } else {
                listOf(GridPosition(to.row, from.column), to)
            }
        }

        // Unresolved multi-axis or disconnected jumps return null
        return null
    }
}
