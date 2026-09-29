package com.zynpath.game.core.puzzle.model

/**
 * Immutable grid dimensions (rows, columns) for rectangular and square boards.
 * Enforces positive dimensions and bounded allocation limits.
 */
data class GridDimensions(
    val rows: Int,
    val columns: Int
) {
    init {
        require(rows > 0) { "Rows must be strictly positive (got $rows)" }
        require(columns > 0) { "Columns must be strictly positive (got $columns)" }
        require(rows <= MAX_DIMENSION && columns <= MAX_DIMENSION) {
            "Dimensions ($rows x $columns) exceed maximum permitted dimension ($MAX_DIMENSION)"
        }
    }

    /**
     * Total number of cells on this board with overflow-safe multiplication.
     */
    val totalCellCount: Int
        get() = rows * columns

    val totalCells: Int
        get() = totalCellCount

    /**
     * Backward-compatible and solver-friendly dimension aliases.
     */
    val rowCount: Int
        get() = rows

    val columnCount: Int
        get() = columns

    val width: Int
        get() = columns

    val height: Int
        get() = rows

    /**
     * Returns true if the board is square (equal rows and columns).
     */
    val isSquare: Boolean
        get() = rows == columns

    /**
     * Checks if the specified (row, column) is within this grid.
     */
    fun contains(row: Int, column: Int): Boolean {
        return row in 0 until rows && column in 0 until columns
    }

    /**
     * Checks if the specified [position] is within this grid.
     */
    fun contains(position: GridPosition): Boolean {
        return contains(position.row, position.column)
    }

    /**
     * Returns all grid positions in deterministic row-major order:
     * (0,0), (0,1), ... (0, cols-1), (1,0), ... (rows-1, cols-1).
     */
    fun allPositions(): List<GridPosition> {
        val list = ArrayList<GridPosition>(totalCellCount)
        for (r in 0 until rows) {
            for (c in 0 until columns) {
                list.add(GridPosition(r, c))
            }
        }
        return list
    }

    override fun toString(): String = "${rows}×${columns}"

    companion object {
        /**
         * Upper limit to prevent unbounded memory allocation and arithmetic overflow.
         */
        const val MAX_DIMENSION = 50

        /**
         * Factory for square grid dimensions.
         */
        fun square(size: Int): GridDimensions = GridDimensions(size, size)
    }
}
