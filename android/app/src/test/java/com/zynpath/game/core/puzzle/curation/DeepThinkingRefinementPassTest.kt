package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.analysis.AdvancedReasoningAnalyzer
import com.zynpath.game.core.puzzle.analysis.NumberPlacementAnalyzer
import com.zynpath.game.core.puzzle.catalog.CuratedFirst50Levels
import com.zynpath.game.core.puzzle.catalog.CuratedWorld3Levels
import com.zynpath.game.core.puzzle.catalog.CuratedWorld4Levels
import com.zynpath.game.core.puzzle.catalog.CuratedWorld5Levels
import com.zynpath.game.core.puzzle.catalog.CuratedWorld6Levels
import com.zynpath.game.core.puzzle.catalog.CatalogAssetGenerator
import com.zynpath.game.core.puzzle.catalog.WorldDefinition
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.SimpleStrategyBaselineSolver
import com.zynpath.game.core.puzzle.solver.SolverConfiguration
import com.zynpath.game.core.puzzle.solver.SolverStatus
import com.zynpath.game.core.puzzle.solver.UniquenessStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.security.MessageDigest

/**
 * Diagnostic test to find optimal checkpoint placements for weak levels
 * (e.g. Level 67, Level 75) ensuring resistance to all greedy heuristics
 * and deep thinking depth.
 */
class DeepThinkingRefinementPassTest {

    private val solver = PuzzleSolver()
    private val analyzer = NumberPlacementAnalyzer()
    private val advAnalyzer = AdvancedReasoningAnalyzer()
    private val baselineSolver = SimpleStrategyBaselineSolver()

    @Test
    fun testWorld1FrozenAndApplyTunedAssets() {
        val assetBaseDir = File("d:/Zynpath/android/app/src/main/assets")
        val w1Dir = File(assetBaseDir, "puzzles/w1")
        
        // Assert all 20 World 1 files exist
        for (i in 1..20) {
            val f = File(w1Dir, "lvl$i.json")
            assertTrue("World 1 lvl$i.json must exist", f.exists())
            val bytes = f.readBytes()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val hash = digest.joinToString("") { "%02x".format(it) }
            println("W1 lvl$i SHA256: $hash")
        }

        // Apply updated JSON assets for 26, 47, 67, 75
        val tunedLevels = listOf(26, 47, 67, 75)
        for (lvl in tunedLevels) {
            val world = WorldDefinition.forLevel(lvl)
            val puzzleFile = File(assetBaseDir, "puzzles/w${world.worldId}/lvl$lvl.json")
            val json = CatalogAssetGenerator.generateAssetJson(lvl)
            assertNotNull("JSON for level $lvl must not be null", json)
            puzzleFile.writeText(json!!)
            println("Updated asset file: ${puzzleFile.absolutePath}")
        }

        // Write updated catalog manifest
        val manifestFile = File(assetBaseDir, "catalog_manifest.json")
        val manifestJson = CatalogAssetGenerator.generateManifestJson()
        manifestFile.writeText(manifestJson)
        println("Updated manifest file: ${manifestFile.absolutePath}")
    }

    @Test
    fun testScanAllLevels21To300() {
        val weakGreedy = mutableListOf<Int>()
        val weakLookahead = mutableListOf<Pair<Int, Int>>()
        for (lvl in 21..300) {
            val (def, sol) = getOriginalLevel(lvl)
            val sim = baselineSolver.evaluate(def)
            val diff = analyzer.analyze(lvl, def, sol)
            if (sim.solvedByAnySimpleStrategy) {
                weakGreedy.add(lvl)
                println("GREEDY VULNERABLE: Level $lvl solved by ${sim.successfulStrategy}")
            }
            val minDepth = when (lvl) {
                in 21..50 -> 2
                in 51..100 -> 3
                in 101..150 -> 3
                in 151..200 -> 4
                else -> 4
            }
            if (diff.requiredThinkingDepth < minDepth) {
                weakLookahead.add(lvl to diff.requiredThinkingDepth)
                println("LOW LOOKAHEAD: Level $lvl has depth ${diff.requiredThinkingDepth} < $minDepth")
            }
        }
        println("Total greedy vulnerable: ${weakGreedy.size}: $weakGreedy")
        println("Total low lookahead: ${weakLookahead.size}: $weakLookahead")
    }

    private fun optimizeForDeepThinking(levelId: Int, def: PuzzleDefinition, sol: PuzzlePath): PuzzleDefinition? {
        val path = sol.positions
        val targetMinDepth = if (levelId in 21..50) 2 else 3
        val existingIndices = def.checkpoints.map { path.indexOf(it.position) }.filter { it >= 0 }.sorted()

        val fastConfig = SolverConfiguration(maxSolutions = 2, nodeLimit = 150_000L, timeBudgetMs = 300L)

        // Try single shift of intermediate checkpoints
        for (i in 1 until existingIndices.size - 1) {
            for (delta in listOf(-3, -2, -1, 1, 2, 3, -4, 4)) {
                val candidateIndices = existingIndices.toMutableList()
                val newPos = candidateIndices[i] + delta
                if (newPos > candidateIndices[i - 1] + 1 && newPos < candidateIndices[i + 1] - 1) {
                    candidateIndices[i] = newPos
                    val candidateCps = candidateIndices.mapIndexed { idx, pIdx -> NumberedCheckpoint(idx + 1, path[pIdx]) }
                    val candidateDef = PuzzleDefinition(
                        puzzleId = def.puzzleId,
                        puzzleVersion = def.puzzleVersion + 1,
                        gridDimensions = def.gridDimensions,
                        requiredCells = def.requiredCells,
                        checkpoints = candidateCps,
                        blockedEdges = def.blockedEdges,
                        difficultyMetadata = def.difficultyMetadata,
                        seed = def.seed
                    )
                    val sRes = solver.solve(candidateDef, fastConfig)
                    if (sRes.status == SolverStatus.SOLVED && sRes.uniqueness == UniquenessStatus.UNIQUE) {
                        val sim = baselineSolver.evaluate(candidateDef)
                        if (!sim.solvedByAnySimpleStrategy) {
                            val diff = analyzer.analyze(levelId, candidateDef, sol)
                            if (diff.requiredThinkingDepth >= targetMinDepth) {
                                return candidateDef
                            }
                        }
                    }
                }
            }
        }

        // Try shifting two checkpoints
        for (i in 1 until existingIndices.size - 2) {
            for (delta1 in listOf(-2, -1, 1, 2)) {
                for (delta2 in listOf(-2, -1, 1, 2)) {
                    val candidateIndices = existingIndices.toMutableList()
                    val p1 = candidateIndices[i] + delta1
                    val p2 = candidateIndices[i + 1] + delta2
                    if (p1 > candidateIndices[i - 1] + 1 && p1 < p2 - 1 && p2 < candidateIndices[i + 2] - 1) {
                        candidateIndices[i] = p1
                        candidateIndices[i + 1] = p2
                        val candidateCps = candidateIndices.mapIndexed { idx, pIdx -> NumberedCheckpoint(idx + 1, path[pIdx]) }
                        val candidateDef = PuzzleDefinition(
                            puzzleId = def.puzzleId,
                            puzzleVersion = def.puzzleVersion + 1,
                            gridDimensions = def.gridDimensions,
                            requiredCells = def.requiredCells,
                            checkpoints = candidateCps,
                            blockedEdges = def.blockedEdges,
                            difficultyMetadata = def.difficultyMetadata,
                            seed = def.seed
                        )
                        val sRes = solver.solve(candidateDef, fastConfig)
                        if (sRes.status == SolverStatus.SOLVED && sRes.uniqueness == UniquenessStatus.UNIQUE) {
                            val sim = baselineSolver.evaluate(candidateDef)
                            if (!sim.solvedByAnySimpleStrategy) {
                                val diff = analyzer.analyze(levelId, candidateDef, sol)
                                if (diff.requiredThinkingDepth >= 3) {
                                    return candidateDef
                                }
                            }
                        }
                    }
                }
            }
        }

        return null
    }

    private fun optimizeWithExpandedSearch(levelId: Int, def: PuzzleDefinition, sol: PuzzlePath): PuzzleDefinition? {
        val path = sol.positions
        val targetMinDepth = if (levelId in 21..50) 2 else 3
        val existingIndices = def.checkpoints.map { path.indexOf(it.position) }.filter { it >= 0 }.sorted()
        val fastConfig = SolverConfiguration(maxSolutions = 2, nodeLimit = 200_000L, timeBudgetMs = 500L)

        // 1. Try adding 1 intermediate checkpoint
        for (insertPos in 2 until path.size - 2) {
            if (insertPos !in existingIndices) {
                val candidateIndices = (existingIndices + insertPos).sorted()
                val candidateCps = candidateIndices.mapIndexed { idx, pIdx -> NumberedCheckpoint(idx + 1, path[pIdx]) }
                val candidateDef = PuzzleDefinition(
                    puzzleId = def.puzzleId,
                    puzzleVersion = def.puzzleVersion + 1,
                    gridDimensions = def.gridDimensions,
                    requiredCells = def.requiredCells,
                    checkpoints = candidateCps,
                    blockedEdges = def.blockedEdges,
                    difficultyMetadata = def.difficultyMetadata,
                    seed = def.seed
                )
                val sRes = solver.solve(candidateDef, fastConfig)
                if (sRes.status == SolverStatus.SOLVED && sRes.uniqueness == UniquenessStatus.UNIQUE) {
                    val sim = baselineSolver.evaluate(candidateDef)
                    if (!sim.solvedByAnySimpleStrategy) {
                        val diff = analyzer.analyze(levelId, candidateDef, sol)
                        if (diff.requiredThinkingDepth >= targetMinDepth) {
                            return candidateDef
                        }
                    }
                }
            }
        }

        // 2. Try removing 1 intermediate checkpoint
        if (existingIndices.size > 4) {
            for (removeIdx in 1 until existingIndices.size - 1) {
                val candidateIndices = existingIndices.filterIndexed { idx, _ -> idx != removeIdx }
                val candidateCps = candidateIndices.mapIndexed { idx, pIdx -> NumberedCheckpoint(idx + 1, path[pIdx]) }
                val candidateDef = PuzzleDefinition(
                    puzzleId = def.puzzleId,
                    puzzleVersion = def.puzzleVersion + 1,
                    gridDimensions = def.gridDimensions,
                    requiredCells = def.requiredCells,
                    checkpoints = candidateCps,
                    blockedEdges = def.blockedEdges,
                    difficultyMetadata = def.difficultyMetadata,
                    seed = def.seed
                )
                val sRes = solver.solve(candidateDef, fastConfig)
                if (sRes.status == SolverStatus.SOLVED && sRes.uniqueness == UniquenessStatus.UNIQUE) {
                    val sim = baselineSolver.evaluate(candidateDef)
                    if (!sim.solvedByAnySimpleStrategy) {
                        val diff = analyzer.analyze(levelId, candidateDef, sol)
                        if (diff.requiredThinkingDepth >= targetMinDepth) {
                            return candidateDef
                        }
                    }
                }
            }
        }

        // 3. Try double shift with larger deltas
        for (i in 1 until existingIndices.size - 2) {
            for (delta1 in listOf(-3, -2, -1, 1, 2, 3)) {
                for (delta2 in listOf(-3, -2, -1, 1, 2, 3)) {
                    val candidateIndices = existingIndices.toMutableList()
                    val p1 = candidateIndices[i] + delta1
                    val p2 = candidateIndices[i + 1] + delta2
                    if (p1 > candidateIndices[i - 1] + 1 && p1 < p2 - 1 && p2 < candidateIndices[i + 2] - 1) {
                        candidateIndices[i] = p1
                        candidateIndices[i + 1] = p2
                        val candidateCps = candidateIndices.mapIndexed { idx, pIdx -> NumberedCheckpoint(idx + 1, path[pIdx]) }
                        val candidateDef = PuzzleDefinition(
                            puzzleId = def.puzzleId,
                            puzzleVersion = def.puzzleVersion + 1,
                            gridDimensions = def.gridDimensions,
                            requiredCells = def.requiredCells,
                            checkpoints = candidateCps,
                            blockedEdges = def.blockedEdges,
                            difficultyMetadata = def.difficultyMetadata,
                            seed = def.seed
                        )
                        val sRes = solver.solve(candidateDef, fastConfig)
                        if (sRes.status == SolverStatus.SOLVED && sRes.uniqueness == UniquenessStatus.UNIQUE) {
                            val sim = baselineSolver.evaluate(candidateDef)
                            if (!sim.solvedByAnySimpleStrategy) {
                                val diff = analyzer.analyze(levelId, candidateDef, sol)
                                if (diff.requiredThinkingDepth >= targetMinDepth) {
                                    return candidateDef
                                }
                            }
                        }
                    }
                }
            }
        }

        return null
    }

    private fun printCheckpointCode(levelId: Int, def: PuzzleDefinition) {
        val cps = def.checkpoints.joinToString(", ") { "NumberedCheckpoint(${it.number}, GridPosition(${it.position.row}, ${it.position.column}))" }
        println("checkpoints = listOf($cps)")
    }

    private fun getOriginalLevel(id: Int): Pair<PuzzleDefinition, PuzzlePath> {
        val def = when (id) {
            in 1..50 -> CuratedFirst50Levels.LEVELS.getValue(id)
            in 51..100 -> CuratedWorld3Levels.LEVELS.getValue(id)
            in 101..150 -> CuratedWorld4Levels.LEVELS.getValue(id)
            in 151..200 -> CuratedWorld5Levels.LEVELS.getValue(id)
            else -> CuratedWorld6Levels.LEVELS.getValue(id)
        }
        val sol = when (id) {
            in 1..50 -> CuratedFirst50Levels.SOLUTIONS.getValue(id)
            in 51..100 -> CuratedWorld3Levels.SOLUTIONS.getValue(id)
            in 101..150 -> CuratedWorld4Levels.SOLUTIONS.getValue(id)
            in 151..200 -> CuratedWorld5Levels.SOLUTIONS.getValue(id)
            else -> CuratedWorld6Levels.SOLUTIONS.getValue(id)
        }
        return def to sol
    }
}
