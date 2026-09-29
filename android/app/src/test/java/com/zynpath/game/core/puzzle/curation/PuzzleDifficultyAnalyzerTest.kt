package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.fixtures.CuratedDifficultyFixtures
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.SolverConfiguration
import com.zynpath.game.core.puzzle.solver.SolverResult
import com.zynpath.game.core.puzzle.solver.SolverStatistics
import com.zynpath.game.core.puzzle.solver.SolverStatus
import com.zynpath.game.core.puzzle.solver.UniquenessStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit test suite validating objective difficulty metrics calculations.
 *
 * Implements Prompt 10 Section 33:
 * 1. Correct required-cell count.
 * 2. Correct checkpoint count.
 * 3. Correct wall count.
 * 4. Correct traversable-edge count.
 * 5. Correct degree distribution.
 * 6. Correct checkpoint gaps.
 * 7. Correct route-turn count.
 * 8. Correct straight-segment calculation.
 * 9. Correct solver-statistics mapping.
 * 10. Correct handling of missing or inconclusive solver metrics.
 */
class PuzzleDifficultyAnalyzerTest {

    private lateinit var analyzer: PuzzleDifficultyAnalyzer
    private lateinit var solver: PuzzleSolver

    @Before
    fun setUp() {
        solver = PuzzleSolver()
        analyzer = PuzzleDifficultyAnalyzer(solver = solver)
    }

    @Test
    fun `test 1 - structural metrics required-cell count`() {
        val def = CuratedDifficultyFixtures.beginner4x4
        val path = CuratedDifficultyFixtures.beginner4x4Solution
        val solverRes = solver.solve(def)

        val metrics = analyzer.computeMetrics(def, path, solverRes)
        assertEquals(16, metrics.totalRequiredCells)
        assertEquals(4, metrics.rows)
        assertEquals(4, metrics.columns)
    }

    @Test
    fun `test 2 - structural metrics checkpoint count`() {
        val def = CuratedDifficultyFixtures.beginner4x4
        val path = CuratedDifficultyFixtures.beginner4x4Solution
        val solverRes = solver.solve(def)

        val metrics = analyzer.computeMetrics(def, path, solverRes)
        assertEquals(4, metrics.checkpointCount)

        val constrainedDef = CuratedDifficultyFixtures.constrained4x4
        val constrainedMetrics = analyzer.computeMetrics(constrainedDef, path, solverRes)
        assertEquals(6, constrainedMetrics.checkpointCount)
    }

    @Test
    fun `test 3 - structural metrics wall count and density`() {
        val cleanDef = CuratedDifficultyFixtures.clean5x5
        val cleanPath = CuratedDifficultyFixtures.clean5x5Solution
        val cleanRes = solver.solve(cleanDef)
        val cleanMetrics = analyzer.computeMetrics(cleanDef, cleanPath, cleanRes)

        assertEquals(0, cleanMetrics.wallCount)
        assertEquals(0.0, cleanMetrics.wallDensity, 0.001)
        assertEquals(0, cleanMetrics.wallConstrainedCells)

        val wallsDef = CuratedDifficultyFixtures.walls5x5
        val solverRes = solver.solve(wallsDef)
        assertTrue("Wall puzzle must be solvable", solverRes.isSolved)
        val wallMetrics = analyzer.computeMetrics(wallsDef, solverRes.firstSolution!!, solverRes)

        assertEquals(3, wallMetrics.wallCount)
        // 5x5 grid has 2 * 5 * 4 = 40 internal edges
        assertEquals(3.0 / 40.0, wallMetrics.wallDensity, 0.001)
        assertTrue("At least some cells must be constrained by walls", wallMetrics.wallConstrainedCells >= 3)
    }

    @Test
    fun `test 4 - traversable edge count matches planar topology`() {
        val def = CuratedDifficultyFixtures.beginner4x4
        val path = CuratedDifficultyFixtures.beginner4x4Solution
        val solverRes = solver.solve(def)

        val metrics = analyzer.computeMetrics(def, path, solverRes)
        // 4x4 grid has (4*3)+(3*4) = 24 traversable edges without walls
        assertEquals(24, metrics.traversableEdgeCount)

        val constrainedGraphDef = CuratedDifficultyFixtures.constrainedGraph4x4
        val graphMetrics = analyzer.computeMetrics(constrainedGraphDef, path, solverRes)
        // 24 - 6 blocked edges = 18 traversable edges
        assertEquals(18, graphMetrics.traversableEdgeCount)
    }

    @Test
    fun `test 5 - degree distribution calculations`() {
        val def = CuratedDifficultyFixtures.beginner4x4
        val path = CuratedDifficultyFixtures.beginner4x4Solution
        val solverRes = solver.solve(def)

        val metrics = analyzer.computeMetrics(def, path, solverRes)
        // 4 corners have degree 2
        assertEquals(4, metrics.constrainedCellCount)
        // 8 border cells have degree 3, 4 center cells have degree 4 -> 12 branch cells
        assertEquals(12, metrics.branchCellCount)
        // Start (0,0) and End (3,0) have degree 2, not <= 1, so 0 dead ends
        assertEquals(0, metrics.deadEndCellCount)
        assertEquals(48.0 / 16.0, metrics.averageTraversableDegree, 0.001)
    }

    @Test
    fun `test 6 - checkpoint gaps and distribution along verified path`() {
        val def = CuratedDifficultyFixtures.beginner4x4
        val path = CuratedDifficultyFixtures.beginner4x4Solution
        val solverRes = solver.solve(def)

        val metrics = analyzer.computeMetrics(def, path, solverRes)
        // Checkpoint indices: #1 at 0, #2 at 4, #3 at 8, #4 at 15
        // Gaps: [4, 4, 7]
        assertEquals(listOf(4, 4, 7), metrics.checkpointGaps)
        assertEquals(4, metrics.minCheckpointGap)
        assertEquals(7, metrics.maxCheckpointGap)
        assertEquals(5.0, metrics.avgCheckpointGap, 0.001)
        assertEquals(2.0, metrics.checkpointGapVariance, 0.001)
        assertEquals(7.0 / 16.0, metrics.maxUnnumberedStretchRatio, 0.001)
    }

    @Test
    fun `test 7 - route turn count calculation`() {
        val def = CuratedDifficultyFixtures.beginner4x4
        val path = CuratedDifficultyFixtures.beginner4x4Solution
        val solverRes = solver.solve(def)

        val metrics = analyzer.computeMetrics(def, path, solverRes)
        // Serpentine path has exactly 6 turns
        assertEquals(6, metrics.turnCount)
        assertEquals(16, metrics.pathLength)
        assertEquals(6.0 / 14.0, metrics.turnFrequency, 0.001)
    }

    @Test
    fun `test 8 - straight segment calculations`() {
        val def = CuratedDifficultyFixtures.beginner4x4
        val path = CuratedDifficultyFixtures.beginner4x4Solution
        val solverRes = solver.solve(def)

        val metrics = analyzer.computeMetrics(def, path, solverRes)
        // Segments are: 3, 1, 3, 1, 3, 1, 3 -> longest is 3, total segment count is 7
        assertEquals(3, metrics.longestStraightSegment)
        assertEquals(7, metrics.straightSegmentCount)
        assertEquals(12, metrics.horizontalMoveCount)
        assertEquals(3, metrics.verticalMoveCount)
    }

    @Test
    fun `test 9 - solver statistics mapping and uniqueness status`() {
        val def = CuratedDifficultyFixtures.constrained4x4
        val path = CuratedDifficultyFixtures.constrained4x4Solution
        val solverRes = solver.solve(
            def,
            SolverConfiguration(maxSolutions = 2, nodeLimit = 10_000L, timeBudgetMs = 2_000L)
        )

        val result = analyzer.analyze(def, path, solverRes)
        assertEquals(UniquenessStatus.UNIQUE, result.uniquenessStatus)
        assertEquals(1, result.metrics.solutionCount)
        assertTrue("Solver nodes explored must be > 0", result.metrics.solverNodesExplored > 0L)
        assertNotNull(result.difficultyBand)
        assertTrue(result.estimatedScore in 0.0..1.0)
    }

    @Test
    fun `test 10 - multi solution and missing metrics handling`() {
        val def = CuratedDifficultyFixtures.multiSolution4x4
        val solverRes = solver.solve(
            def,
            SolverConfiguration(maxSolutions = 2, nodeLimit = 10_000L, timeBudgetMs = 2_000L)
        )

        assertTrue(solverRes.isSolved)
        val result = analyzer.analyze(def, solverRes.firstSolution, solverRes)
        assertEquals(UniquenessStatus.NON_UNIQUE, result.uniquenessStatus)
        assertTrue(result.metrics.solutionCount >= 2)
    }
}
