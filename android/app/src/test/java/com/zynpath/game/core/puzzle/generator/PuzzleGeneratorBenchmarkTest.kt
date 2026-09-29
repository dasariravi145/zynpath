package com.zynpath.game.core.puzzle.generator

import com.zynpath.game.core.puzzle.model.GridDimensions
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Performance benchmarks capturing representative generation metrics across 4x4 through 8x8 boards.
 *
 * Implements Prompt 9 Section 34:
 * Measures actual candidate attempts, accepted candidates, rejection reasons, solver nodes,
 * generation elapsed time, and uniqueness status.
 */
class PuzzleGeneratorBenchmarkTest {

    private lateinit var generator: PuzzleGenerator

    @Before
    fun setUp() {
        generator = PuzzleGenerator()
    }

    @Test
    fun `benchmark generation across 4x4, 5x5, 6x6, 7x7, and 8x8 boards`() {
        data class BenchmarkConfig(
            val label: String,
            val dimensions: GridDimensions,
            val checkpoints: Int,
            val minWalls: Int,
            val maxWalls: Int,
            val seed: Long,
            val style: RouteStyle
        )

        val benchmarks = listOf(
            BenchmarkConfig("4x4 World 1", GridDimensions(4, 4), 4, 0, 0, 101L, RouteStyle.SERPENTINE),
            BenchmarkConfig("5x5 World 2", GridDimensions(5, 5), 5, 0, 0, 202L, RouteStyle.SERPENTINE),
            BenchmarkConfig("5x5 World 3", GridDimensions(5, 5), 5, 2, 4, 303L, RouteStyle.SERPENTINE),
            BenchmarkConfig("6x6 World 4", GridDimensions(6, 6), 6, 2, 6, 404L, RouteStyle.SERPENTINE),
            BenchmarkConfig("7x7 World 5", GridDimensions(7, 7), 7, 4, 8, 505L, RouteStyle.SERPENTINE),
            BenchmarkConfig("8x8 World 6", GridDimensions(8, 8), 8, 6, 12, 606L, RouteStyle.SERPENTINE)
        )

        val results = StringBuilder()
        results.appendLine("\n=== ZYNPATH PUZZLE GENERATOR BENCHMARK REPORT ===")
        results.appendLine("%-15s | %-8s | %-8s | %-12s | %-10s | %-12s".format(
            "Board", "Attempts", "Accepted", "Solver Nodes", "Time (ms)", "Uniqueness"
        ))
        results.appendLine("-".repeat(75))

        for (b in benchmarks) {
            val config = GenerationConfiguration(
                dimensions = b.dimensions,
                checkpointCount = b.checkpoints,
                minWalls = b.minWalls,
                maxWalls = b.maxWalls,
                seed = b.seed,
                routeStyle = b.style,
                requireUniqueness = false,
                maxCandidateAttempts = 5
            )

            val startTime = System.currentTimeMillis()
            val result = generator.generate(config)
            val durationMs = System.currentTimeMillis() - startTime

            assertTrue("Benchmark for ${b.label} must successfully generate a puzzle", result.isSuccess)

            val stats = result.statistics
            results.appendLine("%-15s | %-8d | %-8d | %-12d | %-10d | %-12s".format(
                b.label,
                stats.totalAttempts,
                stats.acceptedCandidates,
                stats.solverNodesExplored,
                durationMs,
                result.uniquenessStatus.name
            ))
        }

        results.appendLine("=================================================")
        println(results.toString())
    }
}
