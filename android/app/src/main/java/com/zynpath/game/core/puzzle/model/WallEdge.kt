package com.zynpath.game.core.puzzle.model

/**
 * Represents a blocked boundary/wall between two orthogonally adjacent cells.
 * Normalized canonically so that order of cells does not affect equality:
 * WallEdge(a, b) == WallEdge(b, a).
 */
data class WallEdge private constructor(
    val cell1: GridCoordinate,
    val cell2: GridCoordinate
) {
    companion object {
        /**
         * Creates a canonical WallEdge between two adjacent coordinates.
         * Enforces canonical ordering: cell1 <= cell2.
         */
        fun between(c1: GridCoordinate, c2: GridCoordinate): WallEdge {
            require(c1.isOrthogonalNeighbor(c2)) {
                "Wall must be between orthogonally adjacent cells: $c1 and $c2"
            }
            return if (c1 <= c2) {
                WallEdge(c1, c2)
            } else {
                WallEdge(c2, c1)
            }
        }
    }

    /**
     * Checks if this wall is between the specified pair of coordinates.
     */
    fun isBetween(a: GridCoordinate, b: GridCoordinate): Boolean {
        return (cell1 == a && cell2 == b) || (cell1 == b && cell2 == a)
    }

    /**
     * True if the wall is horizontal (between row r and row r+1 on the same column).
     */
    val isHorizontalBoundary: Boolean
        get() = cell1.row != cell2.row && cell1.col == cell2.col

    /**
     * True if the wall is vertical (between col c and col c+1 on the same row).
     */
    val isVerticalBoundary: Boolean
        get() = cell1.col != cell2.col && cell1.row == cell2.row

    override fun toString(): String = "Wall[$cell1|$cell2]"
}
