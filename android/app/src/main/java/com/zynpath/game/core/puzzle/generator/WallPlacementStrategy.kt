package com.zynpath.game.core.puzzle.generator

import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import java.util.Random

/**
 * Authoritative wall placement strategy generating blocked edges between adjacent grid cells.
 *
 * Implements Final Deep-Reasoning Puzzle Redesign Sections 3, 4, 5, 6, and 7:
 * - Walls exist ONLY on the edge between two adjacent cells (BlockedEdge).
 * - A wall does NOT consume or block a cell.
 * - STRICT INVARIANT: Never places a wall across any consecutive pair of cells in the verified route.
 * - STRICT CAGE REJECTION:
 *   - No corner cages (walls touching corner cells).
 *   - No U-shaped pockets (3-sided enclosures).
 *   - No L-shaped cages or boxed-in cells.
 *   - No tiny inaccessible pockets.
 *   - No long forced wall corridors.
 *   - Walls placed primarily in meaningful INTERNAL/MIDDLE regions.
 * - PREFERRED STYLE:
 *   - Isolated vertical and horizontal edge walls.
 *   - Separated strategic wall segments.
 *   - Avoids joining walls into maze cages.
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

        // 2. Identify candidate orthogonal edges between required cells
        val corners = getCornerPositions(dimensions)
        val candidateEdges = ArrayList<BlockedEdge>()

        for (cell in requiredCells) {
            val right = cell.right()
            if (right in requiredCells && dimensions.contains(right)) {
                val edge = BlockedEdge.between(cell, right)
                // Filter out protected route edges and edges touching corners
                if (edge !in protectedRouteEdges && edge.first !in corners && edge.second !in corners) {
                    candidateEdges.add(edge)
                }
            }
            val down = cell.down()
            if (down in requiredCells && dimensions.contains(down)) {
                val edge = BlockedEdge.between(cell, down)
                // Filter out protected route edges and edges touching corners
                if (edge !in protectedRouteEdges && edge.first !in corners && edge.second !in corners) {
                    candidateEdges.add(edge)
                }
            }
        }

        if (candidateEdges.size < minWalls) {
            return null // Candidate route cannot accommodate the required minimum wall count
        }

        // 3. Determine target wall count within [minWalls..maxWalls]
        val effectiveMax = minOf(maxWalls, candidateEdges.size)
        val targetCount = if (minWalls == effectiveMax) {
            minWalls
        } else {
            minWalls + random.nextInt(effectiveMax - minWalls + 1)
        }

        if (targetCount == 0) {
            return emptySet()
        }

        // 4. Score candidate edges to prioritize meaningful INTERNAL/MIDDLE regions
        // Internal edges (away from outer borders) receive higher weight
        val prioritizedCandidates = candidateEdges.shuffled(random).sortedByDescending { edge ->
            var score = 0
            if (isInternalPosition(edge.first, dimensions)) score += 2
            if (isInternalPosition(edge.second, dimensions)) score += 2
            score
        }

        // 5. Greedily select non-conflicting, separated strategic walls
        val selected = HashSet<BlockedEdge>(targetCount)

        for (edge in prioritizedCandidates) {
            if (selected.size >= targetCount) break

            val candidateSet = selected + edge
            // Verify that adding this edge does not introduce any forbidden cage / trap pattern
            if (!hasCornerCage(candidateSet, dimensions) &&
                !hasBoxedInCell(candidateSet, dimensions, requiredCells, route) &&
                !hasThreeSidedEnclosure(candidateSet, dimensions, requiredCells) &&
                !hasLShapedCage(candidateSet, dimensions) &&
                !hasWallCorridor(candidateSet, dimensions)
            ) {
                // Also ensure walls remain somewhat separated (avoid directly adjacent parallel walls)
                val isTooCloseToExisting = selected.any { existing ->
                    sharesVertex(edge, existing) && formsAcuteOrTightAngle(edge, existing)
                }
                if (!isTooCloseToExisting) {
                    selected.add(edge)
                }
            }
        }

        if (selected.size < minWalls) {
            return null
        }

        return if (validateWalls(selected, dimensions, requiredCells, route, minWalls, maxWalls)) {
            selected
        } else {
            null
        }
    }

    /**
     * Validates that [walls] satisfy all structural and anti-cage invariants.
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

        // Anti-cage invariants
        if (hasCornerCage(walls, dimensions)) return false
        if (hasBoxedInCell(walls, dimensions, requiredCells, route)) return false
        if (hasThreeSidedEnclosure(walls, dimensions, requiredCells)) return false
        if (hasLShapedCage(walls, dimensions)) return false
        if (hasWallCorridor(walls, dimensions)) return false

        return true
    }

    // =========================================================================
    // ANTI-CAGE AND STRATEGIC PLACEMENT HELPER FUNCTIONS
    // =========================================================================

    fun getCornerPositions(dims: GridDimensions): Set<GridPosition> = setOf(
        GridPosition(0, 0),
        GridPosition(0, dims.columns - 1),
        GridPosition(dims.rows - 1, 0),
        GridPosition(dims.rows - 1, dims.columns - 1)
    )

    fun isInternalPosition(pos: GridPosition, dims: GridDimensions): Boolean =
        pos.row in 1 until dims.rows - 1 && pos.column in 1 until dims.columns - 1

    /**
     * Rejects any wall directly touching a corner cell.
     */
    fun hasCornerCage(walls: Set<BlockedEdge>, dims: GridDimensions): Boolean {
        val corners = getCornerPositions(dims)
        return walls.any { it.first in corners || it.second in corners }
    }

    /**
     * Checks if any cell is boxed in (degree < 2, except start and end of route which can have degree 1).
     */
    fun hasBoxedInCell(
        walls: Set<BlockedEdge>,
        dims: GridDimensions,
        requiredCells: Set<GridPosition>,
        route: List<GridPosition>
    ): Boolean {
        val start = route.firstOrNull()
        val end = route.lastOrNull()

        for (cell in requiredCells) {
            val orthogonalNeighbors = listOf(cell.up(), cell.down(), cell.left(), cell.right())
                .filter { it in requiredCells && dims.contains(it) }

            val unblockedNeighbors = orthogonalNeighbors.count { nbr ->
                val edge = BlockedEdge.between(cell, nbr)
                edge !in walls
            }

            val minRequiredDegree = if (cell == start || cell == end) 1 else 2
            if (unblockedNeighbors < minRequiredDegree) {
                return true
            }
        }
        return false
    }

    /**
     * Rejects 3-sided enclosures (cells enclosed on 3 sides by walls or board borders).
     */
    fun hasThreeSidedEnclosure(
        walls: Set<BlockedEdge>,
        dims: GridDimensions,
        requiredCells: Set<GridPosition>
    ): Boolean {
        for (cell in requiredCells) {
            // Count blocked directions (boundary counts as blocked)
            var blockedSides = 0
            val directions = listOf(cell.up(), cell.down(), cell.left(), cell.right())
            for (nbr in directions) {
                if (!dims.contains(nbr) || nbr !in requiredCells) {
                    blockedSides++
                } else if (BlockedEdge.between(cell, nbr) in walls) {
                    blockedSides++
                }
            }
            if (blockedSides >= 3) {
                return true
            }
        }
        return false
    }

    /**
     * Rejects tight L-shaped wall pairs meeting at a corner cell near a boundary.
     */
    fun hasLShapedCage(walls: Set<BlockedEdge>, dims: GridDimensions): Boolean {
        // If two walls share a cell, check if they form an L-shaped cage on a boundary cell
        val wallsByCell = HashMap<GridPosition, MutableList<BlockedEdge>>()
        for (w in walls) {
            wallsByCell.getOrPut(w.first) { ArrayList() }.add(w)
            wallsByCell.getOrPut(w.second) { ArrayList() }.add(w)
        }

        for ((cell, touchingWalls) in wallsByCell) {
            if (touchingWalls.size >= 2) {
                // If cell is on the perimeter and has >= 2 internal walls touching it, it's trapped
                if (cell.row == 0 || cell.row == dims.rows - 1 || cell.column == 0 || cell.column == dims.columns - 1) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Rejects parallel walls separated by 1 cell forming a forced corridor.
     */
    fun hasWallCorridor(walls: Set<BlockedEdge>, dims: GridDimensions): Boolean {
        for (w1 in walls) {
            for (w2 in walls) {
                if (w1 == w2) continue
                // Check vertical parallel walls at same row
                if (w1.first.row == w1.second.row && w2.first.row == w2.second.row) {
                    val r = w1.first.row
                    if (w2.first.row == r) {
                        val c1 = minOf(w1.first.column, w1.second.column)
                        val c2 = minOf(w2.first.column, w2.second.column)
                        if (kotlin.math.abs(c1 - c2) == 1) return true
                    }
                }
                // Check horizontal parallel walls at same column
                if (w1.first.column == w1.second.column && w2.first.column == w2.second.column) {
                    val c = w1.first.column
                    if (w2.first.column == c) {
                        val r1 = minOf(w1.first.row, w1.second.row)
                        val r2 = minOf(w2.first.row, w2.second.row)
                        if (kotlin.math.abs(r1 - r2) == 1) return true
                    }
                }
            }
        }
        return false
    }

    private fun sharesVertex(e1: BlockedEdge, e2: BlockedEdge): Boolean =
        e1.first == e2.first || e1.first == e2.second || e1.second == e2.first || e1.second == e2.second

    private fun formsAcuteOrTightAngle(e1: BlockedEdge, e2: BlockedEdge): Boolean {
        // Find common vertex
        val common = when {
            e1.first == e2.first || e1.first == e2.second -> e1.first
            e1.second == e2.first || e1.second == e2.second -> e1.second
            else -> return false
        }
        val other1 = if (e1.first == common) e1.second else e1.first
        val other2 = if (e2.first == common) e2.second else e2.first
        // If perpendicular, they meet at a corner forming a 2-wall pocket
        return (other1.row != other2.row) && (other1.column != other2.column)
    }
}
