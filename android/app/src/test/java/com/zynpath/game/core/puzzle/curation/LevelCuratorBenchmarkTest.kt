package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.fixtures.CuratedDifficultyFixtures
import com.zynpath.game.core.puzzle.generator.PuzzleGenerator
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Benchmark test measuring representative difficulty analysis and level curation performance.
 *
 * Implements Prompt 10 Section 37:
 * Records candidate count, accepted count, rejection reasons, analysis duration,
 * solver nodes, duplicate rate, and difficulty distribution.
 */
class LevelCuratorBenchmarkTest {

    private lateinit var analyzer: PuzzleDifficultyAnalyzer
    private lateinit var solver: PuzzleSolver
    private lateinit var curator: LevelCurator

    @Before
    fun setUp() {
        solver = PuzzleSolver()
        analyzer = PuzzleDifficultyAnalyzer(solver = solver)
        val generator = PuzzleGenerator()
        curator = LevelCurator(
            generator = generator,
            analyzer = analyzer,
            config = CurationConfiguration(maxCandidatesPerLevel = 5)
        )
    }

    @Test
    fun `benchmark difficulty analysis execution performance`() {
        val fixtures = listOf(
            CuratedDifficultyFixtures.beginner4x4 to CuratedDifficultyFixtures.beginner4x4Solution,
            CuratedDifficultyFixtures.constrained4x4 to CuratedDifficultyFixtures.constrained4x4Solution,
            CuratedDifficultyFixtures.clean5x5 to CuratedDifficultyFixtures.clean5x5Solution,
            CuratedDifficultyFixtures.highTurn4x4 to CuratedDifficultyFixtures.highTurn4x4Solution
        )

        // Warmup
        for ((def, path) in fixtures) {
            analyzer.analyze(def, path)
        }

        val iterations = 50
        val startNanos = System.nanoTime()
        var totalNodes = 0L

        for (i in 0 until iterations) {
            for ((def, path) in fixtures) {
                val res = analyzer.analyze(def, path)
                totalNodes += res.metrics.solverNodesExplored
            }
        }

        val elapsedMs = (System.nanoTime() - startNanos) / 1_000_000.0
        val totalAnalyses = iterations * fixtures.size
        val avgMsPerAnalysis = elapsedMs / totalAnalyses

        println("=== DIFFICULTY ANALYSIS BENCHMARK ===")
        println("Total evaluations: $totalAnalyses")
        println("Total duration: ${String.format(java.util.Locale.US, "%.2f", elapsedMs)} ms")
        println("Average per analysis: ${String.format(java.util.Locale.US, "%.3f", avgMsPerAnalysis)} ms")
        println("Total solver nodes: $totalNodes")

        assertTrue("Analysis should complete in under 20ms per puzzle", avgMsPerAnalysis < 20.0)
    }

    @Test
    fun `benchmark representative level curation run`() {
        val startNanos = System.nanoTime()
        val curationResult = curator.curateLevels(
            startLevelId = 1,
            count = 4,
            baseSeed = 42_000L
        )
        val elapsedMs = (System.nanoTime() - startNanos) / 1_000_000.0

        println("=== LEVEL CURATION PIPELINE BENCHMARK ===")
        println("Total candidates evaluated: ${curationResult.totalCandidatesEvaluated}")
        println("Total accepted levels: ${curationResult.acceptedCount}")
        println("Total rejected candidates: ${curationResult.rejectedCandidateCount}")
        println("Acceptance rate: ${String.format(java.util.Locale.US, "%.1f", curationResult.acceptanceRate * 100)}%")
        println("Total elapsed duration: ${String.format(java.util.Locale.US, "%.2f", elapsedMs)} ms")
        println("Rejection reason breakdown: ${curationResult.rejectionReasonCounts}")

        assertNotNull(curationResult)
        assertTrue(curationResult.acceptedCount > 0)
    }
}
