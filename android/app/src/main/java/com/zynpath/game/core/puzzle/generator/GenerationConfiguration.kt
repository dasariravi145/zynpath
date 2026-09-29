package com.zynpath.game.core.puzzle.generator

import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition

/**
 * Authoritative input parameters governing deterministic puzzle generation.
 *
 * Implements Prompt 9 Sections 7, 8, and 23:
 * - Exact board dimensions and required cell sets.
 * - Exact checkpoint count and valid wall count ranges.
 * - Seeded reproducibility and generator versioning.
 * - Solver time and node search limits.
 * - Explicit uniqueness requirement.
 * - Authoritative world progression presets (Worlds 1 through 6).
 */
data class GenerationConfiguration(
    val dimensions: GridDimensions,
    val requiredCells: Set<GridPosition>? = null,
    val checkpointCount: Int,
    val minWalls: Int = 0,
    val maxWalls: Int = 0,
    val seed: Long = 0L,
    val maxCandidateAttempts: Int = 100,
    val solverNodeBudget: Long = 50_000L,
    val solverTimeBudgetMs: Long = 2_000L,
    val requireUniqueness: Boolean = false,
    val routeStyle: RouteStyle = RouteStyle.MIXED,
    val puzzleIdPrefix: String = "gen",
    val puzzleVersion: Int = 1,
    val generatorVersion: String = GENERATOR_VERSION,
    val worldId: Int? = null,
    val levelId: Int? = null
) {
    /**
     * Resolves the actual set of playable required cells (defaults to all positions in [dimensions]).
     */
    val effectiveRequiredCells: Set<GridPosition>
        get() = requiredCells ?: dimensions.allPositions().toSet()

    /**
     * Total count of required cells to cover.
     */
    val totalRequiredCells: Int
        get() = effectiveRequiredCells.size

    /**
     * Maximum possible internal orthogonal edges between required cells.
     */
    val maxPossibleInternalEdges: Int
        get() {
            var count = 0
            val cells = effectiveRequiredCells
            for (cell in cells) {
                val right = cell.right()
                if (right in cells) count++
                val down = cell.down()
                if (down in cells) count++
            }
            return count
        }

    /**
     * Maximum walls that could theoretically be placed without blocking a Hamiltonian path.
     * A Hamiltonian path uses exactly (N - 1) edges.
     */
    val maxTheoreticalNonPathWalls: Int
        get() = maxOf(0, maxPossibleInternalEdges - (totalRequiredCells - 1))

    /**
     * Validates this configuration against physical board constraints and Prompt 9 requirements.
     * Returns null if valid, or a descriptive error message explaining why generation is impossible.
     */
    fun validate(): String? {
        if (dimensions.rows < 2 || dimensions.columns < 2) {
            return "Grid dimensions must be at least 2x2 (got ${dimensions.rows}x${dimensions.columns})"
        }
        val cells = effectiveRequiredCells
        if (cells.size < 4) {
            return "Required cells must contain at least 4 cells for a valid puzzle (got ${cells.size})"
        }
        for (cell in cells) {
            if (!dimensions.contains(cell)) {
                return "Required cell $cell is outside grid dimensions $dimensions"
            }
        }
        if (checkpointCount < 2) {
            return "Checkpoint count must be >= 2 (got $checkpointCount)"
        }
        if (checkpointCount > cells.size) {
            return "Checkpoint count ($checkpointCount) cannot exceed total required cells (${cells.size})"
        }
        if (minWalls < 0) {
            return "Minimum wall count cannot be negative (got $minWalls)"
        }
        if (maxWalls < minWalls) {
            return "Maximum wall count ($maxWalls) cannot be less than minimum wall count ($minWalls)"
        }
        if (minWalls > maxTheoreticalNonPathWalls) {
            return "Requested minWalls ($minWalls) exceeds maximum theoretically placeable non-path walls ($maxTheoreticalNonPathWalls)"
        }
        if (maxCandidateAttempts <= 0) {
            return "Maximum candidate attempts must be positive (got $maxCandidateAttempts)"
        }
        if (solverNodeBudget <= 0) {
            return "Solver node budget must be positive (got $solverNodeBudget)"
        }
        if (solverTimeBudgetMs <= 0) {
            return "Solver time budget must be positive (got $solverTimeBudgetMs)"
        }
        return null
    }

    /**
     * True if this configuration satisfies all precondition constraints.
     */
    val isValid: Boolean
        get() = validate() == null

    companion object {
        const val GENERATOR_VERSION: String = "1.0.0"

        /**
         * Creates an authoritative generation configuration adhering to the World Progression Contract (Section 8).
         *
         * | World | Levels   | Grid | Checkpoints | Walls |
         * |---|---|---|---|---|
         * | 1     | 1–20     | 4x4  | 4–6         | 0     |
         * | 2     | 21–50    | 5x5  | 4–7         | 0     |
         * | 3     | 51–100   | 5x5  | 4–7         | 1–5   |
         * | 4     | 101–150  | 6x6  | 4–8         | 2–8   |
         * | 5     | 151–200  | 7x7  | 4–10        | 4–12  |
         * | 6     | 201–300  | 8x8  | 4–12        | 6–18  |
         */
        fun forWorld(
            worldId: Int,
            levelId: Int,
            seed: Long,
            requireUniqueness: Boolean = false,
            routeStyle: RouteStyle = RouteStyle.MIXED
        ): GenerationConfiguration {
            val dimensions: GridDimensions
            val minCp: Int
            val maxCp: Int
            val minW: Int
            val maxW: Int

            when (worldId) {
                1 -> {
                    dimensions = GridDimensions(4, 4)
                    minCp = 4; maxCp = 6
                    minW = 0; maxW = 0
                }
                2 -> {
                    dimensions = GridDimensions(5, 5)
                    minCp = 4; maxCp = 7
                    minW = 0; maxW = 0
                }
                3 -> {
                    dimensions = GridDimensions(5, 5)
                    minCp = 4; maxCp = 7
                    minW = 1; maxW = 5
                }
                4 -> {
                    dimensions = GridDimensions(6, 6)
                    minCp = 4; maxCp = 8
                    minW = 2; maxW = 8
                }
                5 -> {
                    dimensions = GridDimensions(7, 7)
                    minCp = 4; maxCp = 10
                    minW = 4; maxW = 12
                }
                6 -> {
                    dimensions = GridDimensions(8, 8)
                    minCp = 4; maxCp = 12
                    minW = 6; maxW = 18
                }
                else -> {
                    dimensions = GridDimensions(4, 4)
                    minCp = 4; maxCp = 6
                    minW = 0; maxW = 0
                }
            }

            // Distribute checkpoints and walls across levels in this world
            val levelOffset = (levelId - 1).coerceAtLeast(0)
            val cpSpan = maxCp - minCp
            val cp = if (cpSpan > 0) minCp + (levelOffset % (cpSpan + 1)) else minCp

            val puzzleId = "zyn_w${worldId}_lvl${levelId}_s${seed}"

            return GenerationConfiguration(
                dimensions = dimensions,
                checkpointCount = cp,
                minWalls = minW,
                maxWalls = maxW,
                seed = seed,
                requireUniqueness = requireUniqueness,
                routeStyle = routeStyle,
                puzzleIdPrefix = "zyn_w${worldId}_lvl${levelId}",
                worldId = worldId,
                levelId = levelId
            )
        }
    }
}
