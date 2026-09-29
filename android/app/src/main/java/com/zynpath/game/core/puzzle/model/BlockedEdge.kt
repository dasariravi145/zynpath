package com.zynpath.game.core.puzzle.model

/**
 * Represents a blocked boundary/wall between two orthogonally adjacent grid cells.
 *
 * Direction-independent equality:
 * BlockedEdge(a, b) == BlockedEdge(b, a).
 *
 * Endpoints are canonically sorted upon instantiation so that the smaller coordinate
 * (in row-major order) is always [first], and the larger is [second].
 */
data class BlockedEdge(
    val first: GridPosition,
    val second: GridPosition
) {
    /**
     * Backward-compatibility alias for [first].
     */
    val cell1: GridPosition
        get() = first

    /**
     * Backward-compatibility alias for [second].
     */
    val cell2: GridPosition
        get() = second

    init {
        require(first != second) { "Wall cannot connect a cell to itself ($first)" }
        require(first <= second) { "BlockedEdge must be canonically ordered: first <= second" }
    }

    /**
     * Checks if this wall blocks traversal between [a] and [b].
     */
    fun isBetween(a: GridPosition, b: GridPosition): Boolean {
        return (first == a && second == b) || (first == b && second == a)
    }

    /**
     * True if the wall is a horizontal boundary (separating row r and row r+1 on the same column).
     */
    val isHorizontalBoundary: Boolean
        get() = first.row != second.row && first.column == second.column

    /**
     * True if the wall is a vertical boundary (separating col c and col c+1 on the same row).
     */
    val isVerticalBoundary: Boolean
        get() = first.column != second.column && first.row == second.row

    override fun toString(): String = "BlockedEdge[$first|$second]"

    companion object {
        /**
         * Creates a canonical BlockedEdge between two adjacent coordinates.
         * Enforces orthogonal adjacency and orders endpoints canonically: first <= second.
         */
        fun between(a: GridPosition, b: GridPosition): BlockedEdge {
            require(a.isOrthogonalNeighbor(b)) {
                "Blocked edge must be between orthogonally adjacent cells: $a and $b"
            }
            return if (a <= b) {
                BlockedEdge(a, b)
            } else {
                BlockedEdge(b, a)
            }
        }
    }
}

/**
 * Typealias for backward compatibility with initial presentation models.
 */
typealias WallEdge = BlockedEdge
