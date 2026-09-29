package com.zynpath.game.core.puzzle.model

import kotlin.math.abs

/**
 * Immutable grid coordinate (row, column) in a 2D puzzle board.
 * Coordinates are 0-indexed where (0, 0) represents the top-left cell.
 */
data class GridPosition(
    val row: Int,
    val column: Int
) : Comparable<GridPosition> {

    /**
     * Backward-compatibility alias for column.
     */
    val col: Int
        get() = column

    /**
     * Returns true if [other] is horizontally or vertically adjacent (Manhattan distance == 1).
     * Diagonal neighbors return false.
     */
    fun isOrthogonalNeighbor(other: GridPosition): Boolean {
        return (row == other.row && abs(column - other.column) == 1) ||
                (column == other.column && abs(row - other.row) == 1)
    }

    /**
     * Backward-compatibility alias for [isOrthogonalNeighbor].
     */
    fun isOrthogonallyAdjacentTo(other: GridPosition): Boolean = isOrthogonalNeighbor(other)

    /**
     * Calculates the Manhattan distance (|r1 - r2| + |c1 - c2|) to [other].
     */
    fun manhattanDistance(other: GridPosition): Int {
        return abs(row - other.row) + abs(column - other.column)
    }

    fun manhattanDistanceTo(other: GridPosition): Int = manhattanDistance(other)

    fun toCellIndex(columnCount: Int): Int = row * columnCount + column

    /**
     * Computes the neighbor position in the specified [direction].
     */
    fun neighbor(direction: Direction): GridPosition {
        return GridPosition(row + direction.rowDelta, column + direction.colDelta)
    }

    /**
     * Returns the 4 orthogonal neighbor positions in canonical order: UP, RIGHT, DOWN, LEFT.
     */
    fun orthogonalNeighbors(): List<GridPosition> {
        return listOf(
            neighbor(Direction.UP),
            neighbor(Direction.RIGHT),
            neighbor(Direction.DOWN),
            neighbor(Direction.LEFT)
        )
    }

    /**
     * Returns the orthogonal [Direction] from this position to [other], or null if they are not orthogonal neighbors.
     */
    fun directionTo(other: GridPosition): Direction? {
        val rowDiff = other.row - row
        val colDiff = other.column - column
        return when {
            rowDiff == -1 && colDiff == 0 -> Direction.UP
            rowDiff == 1 && colDiff == 0 -> Direction.DOWN
            rowDiff == 0 && colDiff == -1 -> Direction.LEFT
            rowDiff == 0 && colDiff == 1 -> Direction.RIGHT
            else -> null
        }
    }

    /**
     * Checks if this position is within the given [dimensions].
     */
    fun isWithin(dimensions: GridDimensions): Boolean {
        return dimensions.contains(this)
    }

    /**
     * Computes the neighbor in each cardinal direction.
     */
    fun up(): GridPosition = neighbor(Direction.UP)
    fun down(): GridPosition = neighbor(Direction.DOWN)
    fun left(): GridPosition = neighbor(Direction.LEFT)
    fun right(): GridPosition = neighbor(Direction.RIGHT)

    override fun compareTo(other: GridPosition): Int {
        return if (row != other.row) row.compareTo(other.row) else column.compareTo(other.column)
    }

    override fun toString(): String = "($row,$column)"

    companion object {
        fun fromCellIndex(index: Int, columnCount: Int): GridPosition = GridPosition(index / columnCount, index % columnCount)
    }
}

/**
 * Typealias for backward compatibility with initial presentation models.
 */
typealias GridCoordinate = GridPosition
