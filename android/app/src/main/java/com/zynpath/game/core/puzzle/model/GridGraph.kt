package com.zynpath.game.core.puzzle.model

/**
 * Graph representation of a puzzle grid, managing vertices (required cells) and edges (traversable orthogonal links).
 * Fast constant-time lookup for wall obstacles, boundary checks, and neighborhood queries.
 */
class GridGraph(
    val dimensions: GridDimensions,
    val requiredCells: Set<GridPosition>,
    val blockedEdges: Set<BlockedEdge>
) {
    /**
     * Checks if [position] is within the grid boundary.
     */
    fun isInsideGrid(position: GridPosition): Boolean {
        return dimensions.contains(position)
    }

    /**
     * Checks if [position] is a required/playable cell on this board.
     */
    fun isRequiredCell(position: GridPosition): Boolean {
        return isInsideGrid(position) && requiredCells.contains(position)
    }

    /**
     * Checks if [a] and [b] are orthogonally adjacent (Manhattan distance == 1).
     */
    fun areOrthogonallyAdjacent(a: GridPosition, b: GridPosition): Boolean {
        return a.isOrthogonalNeighbor(b)
    }

    /**
     * Checks if an explicit wall / blocked edge exists between [a] and [b].
     */
    fun isBlocked(a: GridPosition, b: GridPosition): Boolean {
        if (!areOrthogonallyAdjacent(a, b)) return true
        val edge = BlockedEdge.between(a, b)
        return blockedEdges.contains(edge)
    }

    /**
     * Returns true if a direct step from [from] to [to] is legal in this graph:
     * 1. Both positions are inside grid boundaries.
     * 2. Both positions are required/playable cells.
     * 3. The positions are orthogonally adjacent.
     * 4. No blocked edge (wall) exists between them.
     */
    fun canTraverse(from: GridPosition, to: GridPosition): Boolean {
        if (!isRequiredCell(from) || !isRequiredCell(to)) return false
        if (!areOrthogonallyAdjacent(from, to)) return false
        return !isBlocked(from, to)
    }

    /**
     * Returns all orthogonal neighbor positions within the grid boundary (regardless of walls or required status).
     */
    fun getNeighbors(position: GridPosition): List<GridPosition> {
        return position.orthogonalNeighbors().filter { isInsideGrid(it) }
    }

    /**
     * Returns legal, unblocked, traversable neighbor positions from [position] in deterministic order (UP, RIGHT, DOWN, LEFT).
     */
    fun getTraversableNeighbors(position: GridPosition): List<GridPosition> {
        if (!isRequiredCell(position)) return emptyList()
        return position.orthogonalNeighbors().filter { neighbor ->
            canTraverse(position, neighbor)
        }
    }
}
