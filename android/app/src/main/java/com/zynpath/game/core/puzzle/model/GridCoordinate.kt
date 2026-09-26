package com.zynpath.game.core.puzzle.model

import kotlin.math.abs

/**
 * Stable integer grid coordinate (row, column) in a rectangular puzzle board.
 * Coordinates are 0-indexed where (0,0) is the top-left cell.
 */
data class GridCoordinate(
    val row: Int,
    val col: Int
) : Comparable<GridCoordinate> {

    /**
     * Returns true if [other] is horizontally or vertically adjacent (Manhattan distance == 1).
     */
    fun isOrthogonalNeighbor(other: GridCoordinate): Boolean {
        return (row == other.row && abs(col - other.col) == 1) ||
                (col == other.col && abs(row - other.row) == 1)
    }

    /**
     * Manhattan distance to [other].
     */
    fun manhattanDistance(other: GridCoordinate): Int {
        return abs(row - other.row) + abs(col - other.col)
    }

    override fun compareTo(other: GridCoordinate): Int {
        return if (row != other.row) row.compareTo(other.row) else col.compareTo(other.col)
    }

    override fun toString(): String = "($row,$col)"
}
