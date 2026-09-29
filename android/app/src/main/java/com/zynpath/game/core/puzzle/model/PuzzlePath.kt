package com.zynpath.game.core.puzzle.model

/**
 * Immutable ordered representation of a player's continuous route across the puzzle grid.
 * Preserves strict sequential traversal history while offering fast constant-time membership lookups.
 */
data class PuzzlePath(
    val positions: List<GridPosition> = emptyList()
) {
    /**
     * Set of unique visited coordinates for O(1) containment checks.
     */
    val visitedCells: Set<GridPosition> by lazy {
        positions.toSet()
    }

    /**
     * True if the path contains no coordinates.
     */
    val isEmpty: Boolean
        get() = positions.isEmpty()

    /**
     * True if the path contains at least one coordinate.
     */
    val isNotEmpty: Boolean
        get() = positions.isNotEmpty()

    /**
     * Total steps in the path.
     */
    val size: Int
        get() = positions.size

    /**
     * Backward-compatibility alias for size.
     */
    val length: Int
        get() = size

    /**
     * Starting coordinate of the path, or null if empty.
     */
    val startPosition: GridPosition?
        get() = positions.firstOrNull()

    /**
     * Current path endpoint (head), or null if empty.
     */
    val currentHead: GridPosition?
        get() = positions.lastOrNull()

    /**
     * Number of distinct grid cells visited by this path.
     */
    val coverageCount: Int
        get() = visitedCells.size

    /**
     * Returns true if any coordinate is visited more than once (self-intersection).
     */
    val hasRevisitedCells: Boolean
        get() = positions.size != visitedCells.size

    /**
     * Checks if [position] is on this path.
     */
    fun contains(position: GridPosition): Boolean = visitedCells.contains(position)

    /**
     * Derives directed orthogonal segments between consecutive coordinates.
     */
    val segments: List<PathSegment> by lazy {
        if (positions.size < 2) {
            emptyList()
        } else {
            val list = ArrayList<PathSegment>(positions.size - 1)
            for (i in 0 until positions.size - 1) {
                list.add(PathSegment(positions[i], positions[i + 1]))
            }
            list
        }
    }

    /**
     * Returns a new [PuzzlePath] with [nextPosition] appended to the end.
     */
    fun plus(nextPosition: GridPosition): PuzzlePath {
        val newPositions = ArrayList<GridPosition>(positions.size + 1)
        newPositions.addAll(positions)
        newPositions.add(nextPosition)
        return PuzzlePath(newPositions)
    }

    /**
     * Returns a new [PuzzlePath] with the last [count] steps removed (undo).
     */
    fun dropLast(count: Int = 1): PuzzlePath {
        if (count <= 0) return this
        if (count >= positions.size) return empty()
        return PuzzlePath(positions.dropLast(count))
    }

    /**
     * Retracts the path back to the specified [targetPosition], dropping all subsequent steps.
     * If [targetPosition] is not in the path, returns this unmodified.
     */
    fun retractTo(targetPosition: GridPosition): PuzzlePath {
        val idx = positions.lastIndexOf(targetPosition)
        if (idx == -1) return this
        return PuzzlePath(positions.subList(0, idx + 1))
    }

    /**
     * Index of [position] in the traversal order, or -1 if not visited.
     */
    fun indexOf(position: GridPosition): Int = positions.indexOf(position)

    override fun toString(): String {
        return if (isEmpty) "Path[]" else "Path[${positions.joinToString(" -> ")}]"
    }

    companion object {
        private val EMPTY = PuzzlePath(emptyList())

        fun empty(): PuzzlePath = EMPTY

        fun of(vararg positions: GridPosition): PuzzlePath = PuzzlePath(positions.toList())

        fun of(positions: List<GridPosition>): PuzzlePath = PuzzlePath(positions.toList())

        fun single(position: GridPosition): PuzzlePath = PuzzlePath(listOf(position))
    }
}
