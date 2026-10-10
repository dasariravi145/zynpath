package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.catalog.CuratedFirst50Levels
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.SolverConfiguration
import com.zynpath.game.core.puzzle.solver.UniquenessStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class World1ProgressionTest {

    private lateinit var solver: PuzzleSolver
    private lateinit var analyzer: PuzzleDifficultyAnalyzer

    @Before
    fun setUp() {
        solver = PuzzleSolver()
        analyzer = PuzzleDifficultyAnalyzer(solver = solver)
    }

    data class LevelCandidate(
        val levelId: Int,
        val title: String,
        val path: List<GridPosition>,
        val checkpointIndices: List<Int> // 0-based indices into path
    )

    private fun testCandidate(candidate: LevelCandidate): Pair<PuzzleDefinition, DifficultyAnalysisResult>? {
        val cps = candidate.checkpointIndices.mapIndexed { idx, pathIdx ->
            NumberedCheckpoint(idx + 1, candidate.path[pathIdx])
        }
        val def = PuzzleDefinition.standard(
            puzzleId = "w1_lvl${candidate.levelId}",
            dimensions = GridDimensions(4, 4),
            checkpoints = cps,
            difficultyMetadata = candidate.title
        )
        val solverRes = solver.solve(def, SolverConfiguration.CHECK_UNIQUENESS)
        if (!solverRes.isSolved || solverRes.uniqueness != UniquenessStatus.UNIQUE) {
            println("L${candidate.levelId} NOT UNIQUE: ${solverRes.solutions.size} solutions")
            solverRes.solutions.forEachIndexed { idx, s ->
                println("   Sol $idx: ${s.positions.joinToString(" -> ") { "(${it.row},${it.col})" }}")
            }
            return null
        }
        val puzzlePath = PuzzlePath(candidate.path)
        val diff = analyzer.analyze(def, puzzlePath, solverRes)
        return def to diff
    }

    private fun findValidCheckpoints(path: List<GridPosition>): List<Int>? {
        // Try 5 checkpoints: 0, a, b, c, 15
        for (a in 1..12) {
            for (b in (a + 1)..13) {
                for (c in (b + 1)..14) {
                    val indices = listOf(0, a, b, c, 15)
                    val cps = indices.mapIndexed { idx, pIdx -> NumberedCheckpoint(idx + 1, path[pIdx]) }
                    val def = PuzzleDefinition.standard(
                        puzzleId = "test",
                        dimensions = GridDimensions(4, 4),
                        checkpoints = cps
                    )
                    val res = solver.solve(def, SolverConfiguration.CHECK_UNIQUENESS)
                    if (res.isSolved && res.uniqueness == UniquenessStatus.UNIQUE) {
                        return indices
                    }
                }
            }
        }
        // Try 6 checkpoints: 0, a, b, c, d, 15
        for (a in 1..11) {
            for (b in (a + 1)..12) {
                for (c in (b + 1)..13) {
                    for (d in (c + 1)..14) {
                        val indices = listOf(0, a, b, c, d, 15)
                        val cps = indices.mapIndexed { idx, pIdx -> NumberedCheckpoint(idx + 1, path[pIdx]) }
                        val def = PuzzleDefinition.standard(
                            puzzleId = "test",
                            dimensions = GridDimensions(4, 4),
                            checkpoints = cps
                        )
                        val res = solver.solve(def, SolverConfiguration.CHECK_UNIQUENESS)
                        if (res.isSolved && res.uniqueness == UniquenessStatus.UNIQUE) {
                            return indices
                        }
                    }
                }
            }
        }
        return null
    }

    @Test
    fun `test Curate and Validate 20 World 1 Levels`() {
        val levelPairs = listOf(
            CuratedFirst50Levels.LEVEL_1 to CuratedFirst50Levels.SOLUTION_1,
            CuratedFirst50Levels.LEVEL_2 to CuratedFirst50Levels.SOLUTION_2,
            CuratedFirst50Levels.LEVEL_3 to CuratedFirst50Levels.SOLUTION_3,
            CuratedFirst50Levels.LEVEL_4 to CuratedFirst50Levels.SOLUTION_4,
            CuratedFirst50Levels.LEVEL_5 to CuratedFirst50Levels.SOLUTION_5,
            CuratedFirst50Levels.LEVEL_6 to CuratedFirst50Levels.SOLUTION_6,
            CuratedFirst50Levels.LEVEL_7 to CuratedFirst50Levels.SOLUTION_7,
            CuratedFirst50Levels.LEVEL_8 to CuratedFirst50Levels.SOLUTION_8,
            CuratedFirst50Levels.LEVEL_9 to CuratedFirst50Levels.SOLUTION_9,
            CuratedFirst50Levels.LEVEL_10 to CuratedFirst50Levels.SOLUTION_10,
            CuratedFirst50Levels.LEVEL_11 to CuratedFirst50Levels.SOLUTION_11,
            CuratedFirst50Levels.LEVEL_12 to CuratedFirst50Levels.SOLUTION_12,
            CuratedFirst50Levels.LEVEL_13 to CuratedFirst50Levels.SOLUTION_13,
            CuratedFirst50Levels.LEVEL_14 to CuratedFirst50Levels.SOLUTION_14,
            CuratedFirst50Levels.LEVEL_15 to CuratedFirst50Levels.SOLUTION_15,
            CuratedFirst50Levels.LEVEL_16 to CuratedFirst50Levels.SOLUTION_16,
            CuratedFirst50Levels.LEVEL_17 to CuratedFirst50Levels.SOLUTION_17,
            CuratedFirst50Levels.LEVEL_18 to CuratedFirst50Levels.SOLUTION_18,
            CuratedFirst50Levels.LEVEL_19 to CuratedFirst50Levels.SOLUTION_19,
            CuratedFirst50Levels.LEVEL_20 to CuratedFirst50Levels.SOLUTION_20
        )

        val verified = mutableListOf<Pair<PuzzleDefinition, DifficultyAnalysisResult>>()
        for ((def, sol) in levelPairs) {
            val solverRes = solver.solve(def, SolverConfiguration.CHECK_UNIQUENESS)
            assertTrue("${def.puzzleId} must be solved", solverRes.isSolved)
            assertEquals("${def.puzzleId} must be unique", UniquenessStatus.UNIQUE, solverRes.uniqueness)
            val diff = analyzer.analyze(def, sol, solverRes)
            verified.add(def to diff)
            println("${def.puzzleId} [${def.difficultyMetadata}]: Score=${String.format(java.util.Locale.US, "%.3f", diff.estimatedScore)}, Decisions=${diff.metrics.meaningfulRouteDecisions}, Lookahead=${diff.metrics.requiredLookAheadDepth}, IsolationRisks=${diff.metrics.remainingCellIsolationRisks}, PlausibleDecoys=${diff.metrics.plausibleIncorrectPathsCount}")
        }

        println("Verified count: ${verified.size} / 20")
        assertEquals(20, verified.size)

        // Verify upward trend: second half avg > first half avg
        val firstHalfAvg = verified.subList(0, 10).map { it.second.estimatedScore }.average()
        val secondHalfAvg = verified.subList(10, 20).map { it.second.estimatedScore }.average()
        println("First half avg: $firstHalfAvg, Second half avg: $secondHalfAvg")
        assertTrue("Difficulty must trend upward", secondHalfAvg > firstHalfAvg)

        // Level 1 must require >= 2 meaningful decisions
        assertTrue("Level 1 must require thinking", verified[0].second.metrics.meaningfulRouteDecisions >= 2)

        // Level 20 Finale must have significant decisions and lookahead
        assertTrue("Level 20 must have significant decisions and lookahead", 
            verified[19].second.metrics.meaningfulRouteDecisions >= 3 && 
            verified[19].second.metrics.requiredLookAheadDepth >= 4)
    }

    @Test
    fun `test find 5-6 checkpoint configuration for all candidates`() {
        val candidates = listOf(
            LevelCandidate(6, "The Spiral Lock", listOf(
                GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
                GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 2),
                GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0),
                GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 1)
            ), emptyList()),
            LevelCandidate(7, "Pinwheel Turn", listOf(
                GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
                GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3),
                GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
                GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2), GridPosition(1, 2)
            ), emptyList()),
            LevelCandidate(8, "Reverse Sweep", listOf(
                GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3),
                GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2),
                GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1),
                GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0)
            ), emptyList()),
            LevelCandidate(9, "Corner Bypass", listOf(
                GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
                GridPosition(1, 3), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 3),
                GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
                GridPosition(2, 0), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 0)
            ), emptyList()),
            LevelCandidate(10, "The Knot", listOf(
                GridPosition(1, 1), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 1),
                GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3),
                GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
                GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0)
            ), emptyList()),
            LevelCandidate(11, "Quad Boxes", listOf(
                GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 1), GridPosition(1, 0),
                GridPosition(2, 0), GridPosition(3, 0), GridPosition(3, 1), GridPosition(2, 1),
                GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3),
                GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(1, 2)
            ), emptyList()),
            LevelCandidate(12, "The Hourglass", listOf(
                GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
                GridPosition(1, 3), GridPosition(1, 2), GridPosition(2, 2), GridPosition(2, 3),
                GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
                GridPosition(2, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(2, 1)
            ), emptyList()),
            LevelCandidate(13, "The Fork", listOf(
                GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0),
                GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(0, 1),
                GridPosition(0, 2), GridPosition(1, 2), GridPosition(2, 2), GridPosition(3, 2),
                GridPosition(3, 3), GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3)
            ), emptyList()),
            LevelCandidate(14, "Center Island", listOf(
                GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2), GridPosition(0, 3),
                GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 2),
                GridPosition(2, 2), GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1),
                GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0)
            ), emptyList()),
            LevelCandidate(15, "Perimeter Weave", listOf(
                GridPosition(3, 0), GridPosition(2, 0), GridPosition(1, 0), GridPosition(0, 0),
                GridPosition(0, 1), GridPosition(1, 1), GridPosition(2, 1), GridPosition(3, 1),
                GridPosition(3, 2), GridPosition(2, 2), GridPosition(1, 2), GridPosition(0, 2),
                GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3), GridPosition(3, 3)
            ), emptyList()),
            LevelCandidate(16, "Cul-de-Sac Maze", listOf(
                GridPosition(0, 0), GridPosition(1, 0), GridPosition(1, 1), GridPosition(0, 1),
                GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 2),
                GridPosition(2, 2), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 2),
                GridPosition(3, 1), GridPosition(2, 1), GridPosition(2, 0), GridPosition(3, 0)
            ), emptyList()),
            LevelCandidate(17, "The Crossweave", listOf(
                GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1),
                GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(2, 3),
                GridPosition(3, 3), GridPosition(3, 2), GridPosition(3, 1), GridPosition(3, 0),
                GridPosition(2, 0), GridPosition(2, 1), GridPosition(2, 2), GridPosition(1, 2)
            ), emptyList()),
            LevelCandidate(18, "The Squeeze", listOf(
                GridPosition(0, 2), GridPosition(0, 3), GridPosition(1, 3), GridPosition(1, 2),
                GridPosition(2, 2), GridPosition(2, 3), GridPosition(3, 3), GridPosition(3, 2),
                GridPosition(3, 1), GridPosition(3, 0), GridPosition(2, 0), GridPosition(2, 1),
                GridPosition(1, 1), GridPosition(1, 0), GridPosition(0, 0), GridPosition(0, 1)
            ), emptyList()),
            LevelCandidate(19, "The Gauntlet", listOf(
                GridPosition(3, 1), GridPosition(2, 1), GridPosition(1, 1), GridPosition(1, 2),
                GridPosition(2, 2), GridPosition(3, 2), GridPosition(3, 3), GridPosition(2, 3),
                GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2), GridPosition(0, 1),
                GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0), GridPosition(3, 0)
            ), emptyList()),
            LevelCandidate(20, "Master of the Path", listOf(
                GridPosition(1, 2), GridPosition(1, 1), GridPosition(2, 1), GridPosition(2, 2),
                GridPosition(2, 3), GridPosition(1, 3), GridPosition(0, 3), GridPosition(0, 2),
                GridPosition(0, 1), GridPosition(0, 0), GridPosition(1, 0), GridPosition(2, 0),
                GridPosition(3, 0), GridPosition(3, 1), GridPosition(3, 2), GridPosition(3, 3)
            ), emptyList())
        )

        for (c in candidates) {
            val valid = findValidCheckpoints(c.path)
            println("OPTIMIZED L${c.levelId}: $valid (count=${valid?.size})")
            assertTrue("Candidate ${c.levelId} must have a valid 5-6 checkpoint configuration", valid != null)
        }
    }
}
