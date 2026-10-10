package com.zynpath.game.core.puzzle.generator

import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import java.util.Random
import kotlin.math.abs

/**
 * Authoritative checkpoint placement strategy distributing numbered checkpoints strictly
 * along the verified solution route in ascending traversal order.
 *
 * Implements Final Deep-Reasoning Puzzle Redesign Sections 8, 9, 10, and 18:
 * - Checkpoint #1 always occupies route index 0 (origin).
 * - Final checkpoint #N always occupies route index (route.size - 1) (terminal).
 * - Intermediate checkpoints are assigned to strictly ascending route indices.
 * - Enforces contiguous numbering (1, 2, ..., N).
 * - DECEPTIVE NUMBER PLACEMENT:
 *   Next checkpoint appears visually close (Manhattan distance <= 2), but shortest path to it
 *   is WRONG because taking it too early prevents full-board coverage.
 * - CELL-RESERVATION REASONING:
 *   Forces the player to recognize cells near the next checkpoint that must remain unused until later.
 */
class CheckpointPlacementStrategy {

    /**
     * Places [count] checkpoints along [route] using [random] for deceptive and strategic placement.
     * Evaluates multiple candidate partitions to select one with high deception and cell reservation.
     *
     * @throws IllegalArgumentException if [count] < 2 or [count] > route.size.
     */
    fun placeCheckpoints(
        route: List<GridPosition>,
        count: Int,
        random: Random
    ): List<NumberedCheckpoint> {
        require(count >= 2) { "Checkpoint count must be >= 2 (got $count)" }
        require(route.size >= count) {
            "Route length (${route.size}) must be >= checkpoint count ($count)"
        }

        val totalRouteCells = route.size
        val intermediateCount = count - 2

        if (intermediateCount == 0) {
            return listOf(
                NumberedCheckpoint(1, route.first()),
                NumberedCheckpoint(2, route.last())
            )
        }

        // Generate multiple candidate index partitions and select the one with best deception & reasoning score
        var bestIndices: List<Int>? = null
        var bestScore = -1.0

        val candidateTries = 25
        for (attempt in 0 until candidateTries) {
            val indices = sampleCandidateIndices(totalRouteCells, intermediateCount, random)
            val score = scoreCheckpointIndices(indices, route)
            if (score > bestScore) {
                bestScore = score
                bestIndices = indices
            }
        }

        val selectedIndices = bestIndices ?: sampleCandidateIndices(totalRouteCells, intermediateCount, random)

        // Construct NumberedCheckpoints strictly matching ascending traversal order
        val checkpoints = ArrayList<NumberedCheckpoint>(count)
        for (i in selectedIndices.indices) {
            val checkpointNumber = i + 1
            val routeIndex = selectedIndices[i]
            checkpoints.add(NumberedCheckpoint(checkpointNumber, route[routeIndex]))
        }

        return checkpoints
    }

    private fun sampleCandidateIndices(
        totalCells: Int,
        intermediateCount: Int,
        random: Random
    ): List<Int> {
        val indices = ArrayList<Int>(intermediateCount + 2)
        indices.add(0)

        val segmentSize = (totalCells - 1).toDouble() / (intermediateCount + 1)
        var lastIndex = 0

        for (i in 1..intermediateCount) {
            val idealIndex = (i * segmentSize).toInt()
            val minAllowed = lastIndex + 1
            val maxAllowed = totalCells - 1 - (intermediateCount - i + 1)

            val jitterWindow = maxOf(2, (segmentSize / 2.5).toInt())
            val lower = maxOf(minAllowed, idealIndex - jitterWindow)
            val upper = minOf(maxAllowed, idealIndex + jitterWindow)

            val chosen = if (lower <= upper) {
                lower + random.nextInt(upper - lower + 1)
            } else {
                minAllowed
            }

            indices.add(chosen)
            lastIndex = chosen
        }

        indices.add(totalCells - 1)
        return indices
    }

    /**
     * Evaluates a set of checkpoint indices for deceptive proximity, cell reservation, and route spacing.
     */
    private fun scoreCheckpointIndices(indices: List<Int>, route: List<GridPosition>): Double {
        var score = 0.0

        for (i in 0 until indices.size - 1) {
            val idx1 = indices[i]
            val idx2 = indices[i + 1]
            val pos1 = route[idx1]
            val pos2 = route[idx2]

            val manhattanDist = abs(pos1.row - pos2.row) + abs(pos1.column - pos2.column)
            val pathDist = idx2 - idx1

            // Deception bonus: Checkpoints are close in grid space (<= 2) but require long traversal (>= 4)
            if (manhattanDist <= 2 && pathDist >= 4) {
                score += 3.0 + (pathDist - 4) * 0.5
            } else if (manhattanDist <= 3 && pathDist >= 6) {
                score += 2.0
            }

            // Cell reservation bonus:
            // Check if pos2 has orthogonal neighbors that appear EARLIER or MUCH LATER in the route
            // A player rushing to pos2 directly would consume or cut off those neighbor cells!
            val neighborsOf2 = listOf(pos2.up(), pos2.down(), pos2.left(), pos2.right())
            for (nbr in neighborsOf2) {
                val nbrIdx = route.indexOf(nbr)
                if (nbrIdx in (idx1 + 1) until idx2) {
                    // Cell is visited in the middle of the segment between CP1 and CP2
                    score += 1.5
                } else if (nbrIdx > idx2) {
                    // Cell is reserved for after CP2
                    score += 1.0
                }
            }

            // Penalty for trivial 1-cell hops (unless total board is very small)
            if (pathDist <= 1 && route.size > 16) {
                score -= 4.0
            }
        }

        return score
    }

    /**
     * Validates that [checkpoints] strictly adhere to [route] and ascending order invariants.
     */
    fun validateCheckpoints(
        checkpoints: List<NumberedCheckpoint>,
        route: List<GridPosition>,
        expectedCount: Int
    ): Boolean {
        if (checkpoints.size != expectedCount) return false
        if (checkpoints.size < 2) return false

        val sorted = checkpoints.sortedBy { it.number }

        // Contiguous 1..N numbering
        for (i in sorted.indices) {
            if (sorted[i].number != i + 1) return false
        }

        // Start at route[0]
        if (sorted.first().position != route.first()) return false

        // End at route[last]
        if (sorted.last().position != route.last()) return false

        // Checkpoints must appear in strictly ascending route indices
        var lastRouteIndex = -1
        val distinctPositions = HashSet<GridPosition>(checkpoints.size)

        for (cp in sorted) {
            if (!distinctPositions.add(cp.position)) return false
            val indexInRoute = route.indexOf(cp.position)
            if (indexInRoute <= lastRouteIndex) return false
            lastRouteIndex = indexInRoute
        }

        return true
    }
}
