package com.zynpath.game.core.puzzle.model

/**
 * Authoritative visual state of a Zynpath rectangular puzzle board.
 * Holds grid dimensions, checkpoint placements, wall obstacles, and the current continuous path.
 */
data class PuzzleBoardState(
    val rowCount: Int,
    val columnCount: Int,
    val checkpoints: Map<GridCoordinate, Int>,
    val walls: Set<WallEdge> = emptySet(),
    val path: List<GridCoordinate> = emptyList()
) {
    init {
        require(rowCount > 0 && columnCount > 0) { "Grid dimensions must be positive ($rowCount x $columnCount)" }
        checkpoints.keys.forEach { coord ->
            require(coord.row in 0 until rowCount && coord.col in 0 until columnCount) {
                "Checkpoint coordinate $coord is out of bounds for $rowCount x $columnCount grid"
            }
        }
    }

    /**
     * Start coordinate is always the checkpoint numbered 1.
     */
    val startCoordinate: GridCoordinate?
        get() = checkpoints.entries.firstOrNull { it.value == 1 }?.key

    /**
     * Maximum numbered checkpoint on this board.
     */
    val maxCheckpointNumber: Int
        get() = checkpoints.values.maxOrNull() ?: 0

    /**
     * Coordinate of the final numbered checkpoint.
     */
    val finalCheckpointCoordinate: GridCoordinate?
        get() = checkpoints.entries.firstOrNull { it.value == maxCheckpointNumber }?.key

    /**
     * Current path endpoint (head).
     */
    val currentHead: GridCoordinate?
        get() = path.lastOrNull()

    /**
     * Total number of required cells in the grid.
     */
    val totalRequiredCells: Int
        get() = rowCount * columnCount

    /**
     * Number of unique cells currently covered by the path.
     */
    val coveredCellCount: Int
        get() = path.distinct().size

    /**
     * True if the specified coordinate is on the current path.
     */
    fun isCovered(coord: GridCoordinate): Boolean = coord in path

    /**
     * Returns checkpoint number at the given coordinate, or null if unnumbered.
     */
    fun getCheckpointAt(coord: GridCoordinate): Int? = checkpoints[coord]

    /**
     * Returns true if there is a wall between c1 and c2.
     */
    fun hasWallBetween(c1: GridCoordinate, c2: GridCoordinate): Boolean {
        return walls.any { it.isBetween(c1, c2) }
    }

    /**
     * Segments between consecutive coordinates in the continuous path.
     */
    val pathSegments: List<Pair<GridCoordinate, GridCoordinate>>
        get() {
            if (path.size < 2) return emptyList()
            return path.zipWithNext()
        }

    /**
     * Authoritative check:
     * 1. Path must start at checkpoint 1.
     * 2. Path must reach the final checkpoint.
     * 3. Checkpoints must be visited in strictly ascending order without skipping.
     * 4. Each move must be an orthogonal neighbor.
     * 5. No wall may be crossed.
     * 6. Every cell must be visited exactly once (no revisits).
     * 7. Total cells covered must equal totalRequiredCells.
     */
    val isSolved: Boolean
        get() {
            if (path.isEmpty() || path.size != totalRequiredCells) return false
            if (coveredCellCount != totalRequiredCells) return false
            if (path.first() != startCoordinate) return false
            if (path.last() != finalCheckpointCoordinate) return false

            // Check orthogonal steps and wall collisions
            for (i in 0 until path.size - 1) {
                val from = path[i]
                val to = path[i + 1]
                if (!from.isOrthogonalNeighbor(to)) return false
                if (hasWallBetween(from, to)) return false
            }

            // Check strictly ascending checkpoint order
            var expectedCheckpoint = 1
            for (cell in path) {
                val cp = checkpoints[cell]
                if (cp != null) {
                    if (cp != expectedCheckpoint) return false
                    expectedCheckpoint++
                }
            }

            return expectedCheckpoint == maxCheckpointNumber + 1
        }
}
