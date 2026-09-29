package com.zynpath.game.core.puzzle.generator

import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import java.util.Random

/**
 * Authoritative wall placement strategy generating blocked edges between adjacent grid cells.
 *
 * Implements Prompt 9 Sections 15, 16, and 17:
 * - Generates direction-independent [BlockedEdge] obstacles only between orthogonally adjacent required cells.
 * - STRICT INVARIANT: Never places a wall across any consecutive pair of cells in the verified route.
 * - Respects the configured world-specific [minWalls, maxWalls] range.
 * - Employs seeded selection for varied, non-decorative movement constraints.
 */
class WallPlacementStrategy {

    /**
     * Generates a valid set of [BlockedEdge] walls for [route] within [minWalls]..[maxWalls].
     * Returns null if the requested wall count cannot be satisfied for this route topology.
     */
    fun placeWalls(
        dimensions: GridDimensions,
        requiredCells: Set<GridPosition>,
        route: List<GridPosition>,
        minWalls: Int,
        maxWalls: Int,
        random: Random
    ): Set<BlockedEdge>? {
        require(minWalls >= 0) { "minWalls must be >= 0 (got $minWalls)" }
        require(maxWalls >= minWalls) { "maxWalls ($maxWalls) must be >= minWalls ($minWalls)" }

        if (maxWalls == 0) {
            return emptySet()
        }

        // 1. Identify all protected route edges that must NEVER be blocked
        val protectedRouteEdges = HashSet<BlockedEdge>(route.size)
        for (i in 0 until route.size - 1) {
            protectedRouteEdges.add(BlockedEdge.between(route[i], route[i + 1]))
        }

        // 2. Identify all candidate orthogonal edges between required cells
        val candidateEdges = ArrayList<BlockedEdge>()
        for (cell in requiredCells) {
            val right = cell.right()
            if (right in requiredCells && dimensions.contains(right)) {
                val edge = BlockedEdge.between(cell, right)
                if (edge !in protectedRouteEdges) {
                    candidateEdges.add(edge)
                }
            }
            val down = cell.down()
            if (down in requiredCells && dimensions.contains(down)) {
                val edge = BlockedEdge.between(cell, down)
                if (edge !in protectedRouteEdges) {
                    candidateEdges.add(edge)
                }
            }
        }

        // 3. Verify that candidate pool is sufficient to satisfy minWalls
        if (candidateEdges.size < minWalls) {
            return null // Candidate route cannot accommodate the required minimum wall count
        }

        // 4. Determine target wall count within [minWalls..maxWalls]
        val effectiveMax = minOf(maxWalls, candidateEdges.size)
        val targetCount = if (minWalls == effectiveMax) {
            minWalls
        } else {
            minWalls + random.nextInt(effectiveMax - minWalls + 1)
        }

        if (targetCount == 0) {
            return emptySet()
        }

        // 5. Seeded selection from candidate edges
        // Shuffle candidate edges deterministically
        val selected = HashSet<BlockedEdge>(targetCount)
        val shuffled = candidateEdges.shuffled(random)

        for (i in 0 until targetCount) {
            selected.add(shuffled[i])
        }

        return if (validateWalls(selected, dimensions, requiredCells, route, minWalls, maxWalls)) {
            selected
        } else {
            null
        }
    }

    /**
     * Validates that [walls] satisfy all structural and route-protection invariants.
     */
    fun validateWalls(
        walls: Set<BlockedEdge>,
        dimensions: GridDimensions,
        requiredCells: Set<GridPosition>,
        route: List<GridPosition>,
        minWalls: Int,
        maxWalls: Int
    ): Boolean {
        if (walls.size < minWalls || walls.size > maxWalls) return false

        // Check each wall: inside dimensions, connects required cells, orthogonally adjacent
        for (wall in walls) {
            if (!dimensions.contains(wall.first) || !dimensions.contains(wall.second)) {
                return false
            }
            if (wall.first !in requiredCells || wall.second !in requiredCells) {
                return false
            }
            if (!wall.first.isOrthogonalNeighbor(wall.second)) {
                return false
            }
        }

        // Crucial invariant: No wall can block any step along the verified route
        for (i in 0 until route.size - 1) {
            val step = BlockedEdge.between(route[i], route[i + 1])
            if (step in walls) {
                return false
            }
        }

        return true
    }
}
