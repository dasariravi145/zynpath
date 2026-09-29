package com.zynpath.game.core.puzzle.generator

import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import java.util.Random

/**
 * Authoritative checkpoint placement strategy distributing numbered checkpoints strictly
 * along the verified solution route in ascending traversal order.
 *
 * Implements Prompt 9 Sections 12, 13, and 14:
 * - Checkpoint #1 always occupies route index 0 (origin).
 * - Final checkpoint #N always occupies route index (route.size - 1) (terminal).
 * - Intermediate checkpoints are assigned to strictly ascending route indices.
 * - Enforces contiguous numbering (1, 2, ..., N).
 * - Spreading heuristic maximizes meaningful unnumbered path segments between checkpoints.
 */
class CheckpointPlacementStrategy {

    /**
     * Places [count] checkpoints along [route] using [random] for deterministic spacing variations.
     * Returns the sorted list of [NumberedCheckpoint] instances.
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

        val selectedIndices = ArrayList<Int>(count)
        selectedIndices.add(0) // Checkpoint #1 at route start

        if (intermediateCount > 0) {
            // Partition intermediate space into (intermediateCount + 1) segments
            val segmentSize = (totalRouteCells - 1).toDouble() / (intermediateCount + 1)

            var lastIndex = 0
            for (i in 1..intermediateCount) {
                val idealIndex = (i * segmentSize).toInt()
                val minAllowed = lastIndex + 1
                val maxAllowed = totalRouteCells - 1 - (intermediateCount - i + 1)

                // Add deterministic jitter within bounds around idealIndex
                val jitterWindow = maxOf(1, (segmentSize / 4.0).toInt())
                val lower = maxOf(minAllowed, idealIndex - jitterWindow)
                val upper = minOf(maxAllowed, idealIndex + jitterWindow)

                val chosen = if (lower <= upper) {
                    lower + random.nextInt(upper - lower + 1)
                } else {
                    minAllowed
                }

                selectedIndices.add(chosen)
                lastIndex = chosen
            }
        }

        selectedIndices.add(totalRouteCells - 1) // Final checkpoint #N at route end

        // Construct NumberedCheckpoints strictly matching ascending traversal order
        val checkpoints = ArrayList<NumberedCheckpoint>(count)
        for (i in selectedIndices.indices) {
            val checkpointNumber = i + 1
            val routeIndex = selectedIndices[i]
            checkpoints.add(NumberedCheckpoint(checkpointNumber, route[routeIndex]))
        }

        return checkpoints
    }

    /**
     * Validates that [checkpoints] strictly adhere to [route] and Sections 12-14 invariants.
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
