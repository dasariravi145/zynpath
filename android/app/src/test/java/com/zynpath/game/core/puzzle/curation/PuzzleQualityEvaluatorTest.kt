package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.fixtures.CuratedDifficultyFixtures
import com.zynpath.game.core.puzzle.generator.PuzzleFingerprint
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.SolverConfiguration
import com.zynpath.game.core.puzzle.solver.SolverResult
import com.zynpath.game.core.puzzle.solver.SolverStatistics
import com.zynpath.game.core.puzzle.solver.SolverStatus
import com.zynpath.game.core.puzzle.solver.UniquenessStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Quality evaluation test suite validating hard rejection rules and soft quality scoring.
 *
 * Implements Prompt 10 Section 34:
 * - Invalid puzzle rejection.
 * - Unsolvable puzzle rejection.
 * - Inconclusive uniqueness handling.
 * - Multiple-solution handling.
 * - Wrong world configuration rejection.
 * - Exact duplicate rejection.
 * - Similarity calculation.
 * - Difficulty-band assignment.
 * - Quality metadata reproducibility.
 */
class PuzzleQualityEvaluatorTest {

    private lateinit var solver: PuzzleSolver
    private lateinit var analyzer: PuzzleDifficultyAnalyzer

    @Before
    fun setUp() {
        solver = PuzzleSolver()
        analyzer = PuzzleDifficultyAnalyzer(solver = solver)
    }

    @Test
    fun `test hard rejection - structurally invalid puzzle`() {
        val invalidDef = PuzzleDefinition(
            puzzleId = "invalid_def",
            gridDimensions = GridDimensions(4, 4),
            requiredCells = emptySet(), // Violates invariant: empty required cells
            checkpoints = listOf(NumberedCheckpoint(1, GridPosition(0, 0))),
            blockedEdges = emptySet()
        )
        val dummyPath = PuzzlePath.of(GridPosition(0, 0))
        val dummySolver = SolverResult(
            status = SolverStatus.INVALID_PUZZLE,
            solutions = emptyList(),
            isExhaustive = true,
            statistics = SolverStatistics.EMPTY,
            diagnosticMessage = "Empty required cells"
        )
        val dummyDiff = DifficultyAnalysisResult(
            puzzleId = invalidDef.puzzleId,
            estimatedScore = 0.1,
            difficultyBand = DifficultyBand.BEGINNER,
            metrics = analyzer.computeMetrics(CuratedDifficultyFixtures.beginner4x4, CuratedDifficultyFixtures.beginner4x4Solution, dummySolver),
            componentScores = emptyMap()
        )

        val result = PuzzleQualityEvaluator.evaluate(
            definition = invalidDef,
            solution = dummyPath,
            solverResult = dummySolver,
            difficulty = dummyDiff
        )

        assertFalse("Must be rejected", result.isAccepted)
        assertTrue(result.rejectionReasons.contains(QualityRejectionReason.STRUCTURAL_INVALIDITY))
    }

    @Test
    fun `test hard rejection - unsolvable puzzle`() {
        val def = CuratedDifficultyFixtures.beginner4x4
        val dummyPath = CuratedDifficultyFixtures.beginner4x4Solution
        val unsolvableResult = SolverResult(
            status = SolverStatus.UNSOLVABLE,
            solutions = emptyList(),
            isExhaustive = true,
            statistics = SolverStatistics.EMPTY,
            diagnosticMessage = "No Hamiltonian path exists"
        )
        val dummyDiff = DifficultyAnalysisResult(
            puzzleId = def.puzzleId,
            estimatedScore = 0.1,
            difficultyBand = DifficultyBand.BEGINNER,
            metrics = analyzer.computeMetrics(def, dummyPath, unsolvableResult),
            componentScores = emptyMap()
        )

        val result = PuzzleQualityEvaluator.evaluate(
            definition = def,
            solution = dummyPath,
            solverResult = unsolvableResult,
            difficulty = dummyDiff
        )

        assertFalse(result.isAccepted)
        assertTrue(result.rejectionReasons.contains(QualityRejectionReason.UNSOLVABLE))
    }

    @Test
    fun `test hard rejection - multiple solutions when uniqueness is required`() {
        val def = CuratedDifficultyFixtures.multiSolution4x4
        val solverRes = solver.solve(def, SolverConfiguration(maxSolutions = 2))
        assertTrue(solverRes.isSolved)
        assertEquals(UniquenessStatus.NON_UNIQUE, solverRes.uniqueness)

        val diff = analyzer.analyze(def, solverRes.firstSolution, solverRes)
        val criteria = PuzzleQualityEvaluator.QualityCriteria(requireUnique = true)

        val result = PuzzleQualityEvaluator.evaluate(
            definition = def,
            solution = solverRes.firstSolution!!,
            solverResult = solverRes,
            difficulty = diff,
            criteria = criteria
        )

        assertFalse(result.isAccepted)
        assertTrue(result.rejectionReasons.contains(QualityRejectionReason.UNIQUENESS_NOT_PROVEN))
    }

    @Test
    fun `test hard rejection - wrong world configuration`() {
        val def4x4 = CuratedDifficultyFixtures.beginner4x4
        val path = CuratedDifficultyFixtures.beginner4x4Solution
        val solverRes = solver.solve(def4x4)
        val diff = analyzer.analyze(def4x4, path, solverRes)

        // Expect 5x5 but got 4x4
        val criteria = PuzzleQualityEvaluator.QualityCriteria(
            expectedDimensions = GridDimensions(5, 5),
            checkpointRange = 4..6,
            wallRange = 0..0
        )

        val result = PuzzleQualityEvaluator.evaluate(
            definition = def4x4,
            solution = path,
            solverResult = solverRes,
            difficulty = diff,
            criteria = criteria
        )

        assertFalse(result.isAccepted)
        assertTrue(result.rejectionReasons.contains(QualityRejectionReason.GRID_DIMENSION_MISMATCH))
    }

    @Test
    fun `test hard rejection - exact and symmetric duplicate detection`() {
        val def = CuratedDifficultyFixtures.constrained4x4
        val path = CuratedDifficultyFixtures.constrained4x4Solution
        val solverRes = solver.solve(def)
        val diff = analyzer.analyze(def, path, solverRes)

        // Same puzzle already in existing levels
        val criteria = PuzzleQualityEvaluator.QualityCriteria(
            rejectSymmetricDuplicates = true,
            requireUnique = true
        )

        val resultExact = PuzzleQualityEvaluator.evaluate(
            definition = def,
            solution = path,
            solverResult = solverRes,
            difficulty = diff,
            criteria = criteria,
            existingLevels = listOf(def)
        )

        assertFalse(resultExact.isAccepted)
        assertTrue(resultExact.rejectionReasons.contains(QualityRejectionReason.EXACT_DUPLICATE))

        // Symmetric rotated duplicate
        val rotated = PuzzleSimilarityCalculator.applyTransformation(def, SymmetryTransformation.ROTATE_90)
        val resultSymmetric = PuzzleQualityEvaluator.evaluate(
            definition = rotated,
            solution = path,
            solverResult = solverRes,
            difficulty = diff,
            criteria = criteria,
            existingLevels = listOf(def)
        )
        assertFalse(resultSymmetric.isAccepted)
        assertTrue(resultSymmetric.rejectionReasons.contains(QualityRejectionReason.EXACT_DUPLICATE))
    }

    @Test
    fun `test similarity calculation between distinct and identical puzzles`() {
        val puzzleA = CuratedDifficultyFixtures.beginner4x4
        val puzzleB = CuratedDifficultyFixtures.constrained4x4

        // Exact match similarity = 1.0
        val simIdentical = PuzzleSimilarityCalculator.calculateSimilarity(puzzleA, puzzleA)
        assertEquals(1.0, simIdentical, 0.001)

        // Partial match
        val simPartial = PuzzleSimilarityCalculator.calculateSimilarity(puzzleA, puzzleB)
        assertTrue("Partial similarity should be between 0.3 and 0.9", simPartial in 0.3..0.9)

        // Mismatched dimensions = 0.0
        val puzzle5x5 = CuratedDifficultyFixtures.clean5x5
        val simDifferentDims = PuzzleSimilarityCalculator.calculateSimilarity(puzzleA, puzzle5x5)
        assertEquals(0.0, simDifferentDims, 0.001)
    }

    @Test
    fun `test soft quality scoring and reproducibility`() {
        val def = CuratedDifficultyFixtures.constrained4x4
        val path = CuratedDifficultyFixtures.constrained4x4Solution
        val solverRes = solver.solve(def, SolverConfiguration.CHECK_UNIQUENESS)
        val diff = analyzer.analyze(def, path, solverRes)

        val result1 = PuzzleQualityEvaluator.evaluate(
            definition = def,
            solution = path,
            solverResult = solverRes,
            difficulty = diff
        )

        val result2 = PuzzleQualityEvaluator.evaluate(
            definition = def,
            solution = path,
            solverResult = solverRes,
            difficulty = diff
        )

        assertTrue(result1.isAccepted)
        assertTrue(result1.softQualityScore > 0.0)
        assertEquals(result1.softQualityScore, result2.softQualityScore, 0.0001)
        assertEquals(result1.softSignals, result2.softSignals)
    }
}
