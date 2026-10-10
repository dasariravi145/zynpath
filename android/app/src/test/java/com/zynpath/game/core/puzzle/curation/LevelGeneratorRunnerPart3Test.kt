package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.catalog.PuzzleAsset
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetMetadata
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetSerializer
import com.zynpath.game.core.puzzle.generator.PuzzleFingerprint
import com.zynpath.game.core.puzzle.model.GridPosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.util.Random

class LevelGeneratorRunnerPart3Test {

    private fun countTurns(path: List<GridPosition>): Int {
        if (path.size < 3) return 0
        var turns = 0
        for (i in 1 until path.size - 1) {
            val d1r = path[i].row - path[i - 1].row
            val d1c = path[i].column - path[i - 1].column
            val d2r = path[i + 1].row - path[i].row
            val d2c = path[i + 1].column - path[i].column
            if (d1r != d2r || d1c != d2c) turns++
        }
        return turns
    }

    private fun generateHamiltonianPath(
        rows: Int,
        cols: Int,
        rng: Random,
        steps: Int = 3_500
    ): List<GridPosition> {
        val total = rows * cols
        val path = ArrayList<GridPosition>(total)
        val posToIndex = IntArray(total)

        for (r in 0 until rows) {
            if (r % 2 == 0) {
                for (c in 0 until cols) path.add(GridPosition(r, c))
            } else {
                for (c in cols - 1 downTo 0) path.add(GridPosition(r, c))
            }
        }
        for (i in 0 until total) {
            val p = path[i]
            posToIndex[p.row * cols + p.column] = i
        }

        val dirs = listOf(GridPosition(-1, 0), GridPosition(1, 0), GridPosition(0, -1), GridPosition(0, 1))

        for (step in 0 until steps) {
            val fromTail = rng.nextBoolean()
            if (fromTail) {
                val tail = path[path.size - 1]
                val prev = path[path.size - 2]
                val candidates = dirs.map { GridPosition(tail.row + it.row, tail.column + it.column) }
                    .filter { it.row in 0 until rows && it.column in 0 until cols && it != prev }
                if (candidates.isNotEmpty()) {
                    val nbr = candidates[rng.nextInt(candidates.size)]
                    val idx = posToIndex[nbr.row * cols + nbr.column]
                    if (idx in 0 until path.size - 2) {
                        path.subList(idx + 1, path.size).reverse()
                        for (k in (idx + 1) until path.size) {
                            val p = path[k]
                            posToIndex[p.row * cols + p.column] = k
                        }
                    }
                }
            } else {
                val head = path[0]
                val next = path[1]
                val candidates = dirs.map { GridPosition(head.row + it.row, head.column + it.column) }
                    .filter { it.row in 0 until rows && it.column in 0 until cols && it != next }
                if (candidates.isNotEmpty()) {
                    val nbr = candidates[rng.nextInt(candidates.size)]
                    val idx = posToIndex[nbr.row * cols + nbr.column]
                    if (idx > 1) {
                        path.subList(0, idx).reverse()
                        for (k in 0 until idx) {
                            val p = path[k]
                            posToIndex[p.row * cols + p.column] = k
                        }
                    }
                }
            }
        }
        return path
    }

    @Test
    fun testGenerateSample11x11And12x12() {
        val rng = Random(42)
        val path11 = generateHamiltonianPath(11, 11, rng, steps = 15_000)
        assertEquals(121, path11.size)
        assertEquals(121, path11.toSet().size)
        val turns11 = countTurns(path11)
        println("Generated 11x11 path with $turns11 turns!")
        assertTrue("Turns must be >= 30, got $turns11", turns11 >= 30)

        // Verify adjacency
        for (i in 0 until path11.size - 1) {
            assertTrue(path11[i].isOrthogonalNeighbor(path11[i + 1]))
        }

        val path12 = generateHamiltonianPath(12, 12, rng, steps = 20_000)
        assertEquals(144, path12.size)
        assertEquals(144, path12.toSet().size)
        val turns12 = countTurns(path12)
        println("Generated 12x12 path with $turns12 turns!")
        assertTrue("Turns must be >= 40, got $turns12", turns12 >= 40)

        for (i in 0 until path12.size - 1) {
            assertTrue(path12[i].isOrthogonalNeighbor(path12[i + 1]))
        }
    }

    private val solver = com.zynpath.game.core.puzzle.solver.PuzzleSolver()
    private val analyzer = com.zynpath.game.core.puzzle.curation.PuzzleDifficultyAnalyzer(solver = solver)
    private val wallStrategy = com.zynpath.game.core.puzzle.generator.WallPlacementStrategy()

    private fun searchUniqueCheckpoints(
        path: List<GridPosition>,
        rows: Int,
        cols: Int,
        levelId: Int,
        worldId: Int,
        title: String,
        minCheckpoints: Int,
        maxCheckpoints: Int,
        maxWalls: Int,
        seed: Long,
        rng: Random,
        initialWalls: Set<com.zynpath.game.core.puzzle.model.BlockedEdge> = emptySet()
    ): LevelGeneratorRunnerPart2Test.VerifiedLevel? {
        val total = rows * cols
        val puzzlePath = com.zynpath.game.core.puzzle.model.PuzzlePath(path)
        val candidateIndices = mutableSetOf(0, total - 1)

        val seedCount = when {
            total <= 100 -> 18
            total <= 121 -> 24
            else -> 32
        }
        val step = (total - 1).toDouble() / (seedCount - 1)
        for (k in 1 until seedCount - 1) {
            candidateIndices.add((k * step).toInt())
        }

        val fastConfig = com.zynpath.game.core.puzzle.solver.SolverConfiguration(
            maxSolutions = 2,
            nodeLimit = 35_000L,
            timeBudgetMs = 350L
        )
        val currentWalls = initialWalls.toMutableSet()
        val dims = com.zynpath.game.core.puzzle.model.GridDimensions(rows, cols)
        val pathCellSet = path.toSet()

        val maxCandidateCount = if (total >= 144) 44 else maxCheckpoints + 6
        var iterations = 0
        while (candidateIndices.size <= maxCandidateCount && iterations < 35) {
            iterations++
            val sortedIndices = candidateIndices.sorted()
            val checkpoints = sortedIndices.mapIndexed { idx, pIdx ->
                com.zynpath.game.core.puzzle.model.NumberedCheckpoint(idx + 1, path[pIdx])
            }

            val candidateDef = com.zynpath.game.core.puzzle.model.PuzzleDefinition(
                puzzleId = "w${worldId}_lvl$levelId",
                puzzleVersion = 4,
                gridDimensions = dims,
                requiredCells = pathCellSet,
                checkpoints = checkpoints,
                blockedEdges = currentWalls,
                difficultyMetadata = "$title (seed=$seed)",
                seed = seed
            )

            val solverRes = solver.solve(candidateDef, fastConfig)
            println("searchUniqueCheckpoints iter $iterations: cpCount=${candidateIndices.size}, walls=${currentWalls.size}, status=${solverRes.status}, uniq=${solverRes.uniqueness}, sols=${solverRes.solutions.size}")
            if (solverRes.status == com.zynpath.game.core.puzzle.solver.SolverStatus.SOLVED &&
                solverRes.uniqueness == com.zynpath.game.core.puzzle.solver.UniquenessStatus.UNIQUE) {

                // Prune redundant checkpoints down while maintaining uniqueness
                val prunedIndices = candidateIndices.sorted().toMutableList()
                repeat(2) {
                    val internalCandidates = prunedIndices.subList(1, prunedIndices.size - 1).shuffled(rng)
                    for (pIdx in internalCandidates) {
                        if (prunedIndices.size <= minCheckpoints) break
                        val testIndices = prunedIndices.filter { it != pIdx }.sorted()
                        val testCps = testIndices.mapIndexed { idx, pi ->
                            com.zynpath.game.core.puzzle.model.NumberedCheckpoint(idx + 1, path[pi])
                        }
                        val testDef = candidateDef.copy(checkpoints = testCps)
                        val testRes = solver.solve(testDef, fastConfig)
                        if (testRes.status == com.zynpath.game.core.puzzle.solver.SolverStatus.SOLVED &&
                            testRes.uniqueness == com.zynpath.game.core.puzzle.solver.UniquenessStatus.UNIQUE) {
                            prunedIndices.remove(pIdx)
                        }
                    }
                }

                if (prunedIndices.size > maxCheckpoints) {
                    continue
                }

                val prunedCps = prunedIndices.sorted().mapIndexed { idx, pi ->
                    com.zynpath.game.core.puzzle.model.NumberedCheckpoint(idx + 1, path[pi])
                }
                val finalDef = candidateDef.copy(checkpoints = prunedCps)

                val fullRes = solver.solve(finalDef, com.zynpath.game.core.puzzle.solver.SolverConfiguration.CHECK_UNIQUENESS)
                if (fullRes.status == com.zynpath.game.core.puzzle.solver.SolverStatus.SOLVED &&
                    fullRes.uniqueness == com.zynpath.game.core.puzzle.solver.UniquenessStatus.UNIQUE) {

                    val completion = com.zynpath.game.core.puzzle.engine.CompletionValidator.validate(finalDef, puzzlePath)
                    if (completion is com.zynpath.game.core.puzzle.engine.CompletionCheckResult.Success) {
                        val analysisResult = analyzer.analyze(finalDef, puzzlePath, fullRes)
                        val da = analysisResult.difficultyAnalysis

                        println("DEBUG Level $levelId: decisions=${da.meaningfulDecisionPointCount}, forcedRatio=${da.forcedMoveRatio}, CPs=${finalDef.checkpoints.size}, walls=${finalDef.blockedEdges.size}")
                        if (da.meaningfulDecisionPointCount < 2 && da.forcedMoveRatio > 0.96) {
                            return null
                        }

                        return LevelGeneratorRunnerPart2Test.VerifiedLevel(
                            levelId = levelId,
                            worldId = worldId,
                            title = title,
                            definition = finalDef,
                            solution = puzzlePath,
                            analysis = da
                        )
                    }
                }
            }

            if (solverRes.solutions.size >= 2) {
                val sol1 = solverRes.solutions[0]
                val sol2 = solverRes.solutions[1]
                val altSol = if (sol1.positions == path) sol2 else sol1

                var wallAdded = false
                if (currentWalls.size < maxWalls) {
                    val protectedEdges = HashSet<com.zynpath.game.core.puzzle.model.BlockedEdge>(path.size)
                    for (i in 0 until path.size - 1) {
                        protectedEdges.add(com.zynpath.game.core.puzzle.model.BlockedEdge.between(path[i], path[i + 1]))
                    }
                    for (i in 0 until altSol.positions.size - 1) {
                        val edge = com.zynpath.game.core.puzzle.model.BlockedEdge.between(altSol.positions[i], altSol.positions[i + 1])
                        if (edge !in protectedEdges && edge !in currentWalls) {
                            if (wallStrategy.validateWalls(currentWalls + edge, dims, pathCellSet, path, 0, maxWalls)) {
                                currentWalls.add(edge)
                                wallAdded = true
                                break
                            }
                        }
                    }
                }

                if (!wallAdded) {
                    val pathPosToIndex = IntArray(rows * cols) { -1 }
                    path.forEachIndexed { i, pos -> pathPosToIndex[pos.row * cols + pos.column] = i }

                    val altIndicesInPath = altSol.positions.map { pos ->
                        pathPosToIndex[pos.row * cols + pos.column]
                    }

                    var bestIndexToAdd: Int? = null

                    // Priority 1: inversion where one cell is ALREADY a checkpoint
                    for (j in 0 until total) {
                        val idxJ = altIndicesInPath[j]
                        if (idxJ == -1) continue
                        for (k in (j + 1) until total) {
                            val idxK = altIndicesInPath[k]
                            if (idxK == -1) continue
                            if (idxJ > idxK) {
                                val jIsCp = idxJ in candidateIndices
                                val kIsCp = idxK in candidateIndices
                                if (jIsCp && !kIsCp) {
                                    bestIndexToAdd = idxK
                                    break
                                } else if (!jIsCp && kIsCp) {
                                    bestIndexToAdd = idxJ
                                    break
                                }
                            }
                        }
                        if (bestIndexToAdd != null) break
                    }

                    // Priority 2: inversion where neither is a checkpoint
                    if (bestIndexToAdd == null) {
                        for (j in 0 until total) {
                            val idxJ = altIndicesInPath[j]
                            if (idxJ == -1) continue
                            for (k in (j + 1) until total) {
                                val idxK = altIndicesInPath[k]
                                if (idxK == -1) continue
                                if (idxJ > idxK) {
                                    if (idxJ !in candidateIndices) {
                                        bestIndexToAdd = idxJ
                                        break
                                    } else if (idxK !in candidateIndices) {
                                        bestIndexToAdd = idxK
                                        break
                                    }
                                }
                            }
                            if (bestIndexToAdd != null) break
                        }
                    }

                    if (bestIndexToAdd != null && candidateIndices.size < maxCandidateCount) {
                        candidateIndices.add(bestIndexToAdd)
                    } else {
                        val gaps = sortedIndices.zipWithNext().map { (a, b) -> (b - a) to ((a + b) / 2) }
                        val largestGap = gaps.maxByOrNull { it.first }
                        if (largestGap != null && largestGap.first > 1 && candidateIndices.size < maxCandidateCount) {
                            candidateIndices.add(largestGap.second)
                        } else {
                            break
                        }
                    }
                }
            } else {
                // If first iteration is already SEARCH_LIMIT_REACHED, this initial wall/path configuration is too open; skip immediately
                if (iterations == 1) return null
                val gaps = sortedIndices.zipWithNext().map { (a, b) -> (b - a) to ((a + b) / 2) }
                val largestGap = gaps.maxByOrNull { it.first }
                if (largestGap != null && largestGap.first > 1 && candidateIndices.size < maxCandidateCount) {
                    candidateIndices.add(largestGap.second)
                } else {
                    break
                }
            }
        }
        return null
    }

    private fun generateLevelWithBackbite(
        levelId: Int,
        worldId: Int,
        title: String,
        rows: Int,
        cols: Int,
        minCheckpoints: Int,
        maxCheckpoints: Int,
        maxWalls: Int,
        seed: Long,
        minTurns: Int
    ): LevelGeneratorRunnerPart2Test.VerifiedLevel {
        val dims = com.zynpath.game.core.puzzle.model.GridDimensions(rows, cols)
        for (attempt in 0 until 500) {
            val attemptRng = Random(seed + attempt * 1013L)
            val path = generateHamiltonianPath(rows, cols, attemptRng, steps = if (rows >= 12) 20_000 else 25_000)
            val turns = countTurns(path)
            if (turns < minTurns) {
                continue
            }

            val (minInitWalls, maxInitWalls) = when {
                rows <= 10 -> 2 to 4
                rows == 11 -> 4 to 6
                else -> 5 to 7
            }
            val initialWalls = wallStrategy.placeWalls(dims, path.toSet(), path, minInitWalls, maxInitWalls, attemptRng) ?: continue
            if (initialWalls.size < minInitWalls) continue

            val verified = searchUniqueCheckpoints(
                path = path,
                rows = rows,
                cols = cols,
                levelId = levelId,
                worldId = worldId,
                title = title,
                minCheckpoints = minCheckpoints,
                maxCheckpoints = maxCheckpoints,
                maxWalls = maxWalls,
                seed = seed + attempt,
                rng = attemptRng,
                initialWalls = initialWalls
            )
            if (verified != null) {
                return verified
            }
        }
        error("Failed to generate level $levelId (${rows}x${cols}) with seed $seed")
    }

    @Test
    fun testGenerateLevel151Isolated() {
        println("Starting isolated Level 151 test with generateLevelWithBackbite...")
        val t0 = System.currentTimeMillis()
        val verified = generateLevelWithBackbite(
            levelId = 151,
            worldId = 5,
            title = "World 5 - Level 151",
            rows = 10,
            cols = 10,
            minCheckpoints = 14,
            maxCheckpoints = 20,
            maxWalls = 8,
            seed = 5151L,
            minTurns = 45
        )
        println("Generated Level 151 in ${System.currentTimeMillis() - t0}ms:")
        println("CPs=${verified.definition.checkpoints.size}, walls=${verified.definition.blockedEdges.size}, band=${verified.analysis.difficultyTier}, decisions=${verified.analysis.meaningfulDecisionPointCount}")
        assertTrue(verified.definition.checkpoints.size in 14..20)
        assertTrue(verified.definition.blockedEdges.size <= 8)
    }

    @Test
    fun testGenerateLevel181Isolated() {
        println("Starting isolated Level 181 (11x11) test with generateLevelWithBackbite...")
        val t0 = System.currentTimeMillis()
        val verified = generateLevelWithBackbite(
            levelId = 181,
            worldId = 5,
            title = "World 5 - Level 181",
            rows = 11,
            cols = 11,
            minCheckpoints = 16,
            maxCheckpoints = 24,
            maxWalls = 11,
            seed = 8181L,
            minTurns = 50
        )
        println("Generated Level 181 in ${System.currentTimeMillis() - t0}ms:")
        println("CPs=${verified.definition.checkpoints.size}, walls=${verified.definition.blockedEdges.size}, band=${verified.analysis.difficultyTier}, decisions=${verified.analysis.meaningfulDecisionPointCount}")
        assertTrue(verified.definition.checkpoints.size in 16..24)
        assertTrue(verified.definition.blockedEdges.size <= 11)
    }

    @Test
    fun testGenerateLevel300Isolated() {
        println("Starting isolated Level 300 Master Challenge (12x12) test...")
        val t0 = System.currentTimeMillis()
        val verified = generateLevelWithBackbite(
            levelId = 300,
            worldId = 6,
            title = "Grandmaster Finality",
            rows = 12,
            cols = 12,
            minCheckpoints = 18,
            maxCheckpoints = 30,
            maxWalls = 14,
            seed = 300300L,
            minTurns = 42
        )
        println("Generated Level 300 in ${System.currentTimeMillis() - t0}ms:")
        println("CPs=${verified.definition.checkpoints.size}, walls=${verified.definition.blockedEdges.size}, band=${verified.analysis.difficultyTier}, decisions=${verified.analysis.meaningfulDecisionPointCount}")
        assertTrue(verified.definition.checkpoints.size in 18..30)
        assertTrue(verified.definition.blockedEdges.size <= 15)
        assertEquals(144, verified.definition.totalRequiredCells)
    }

    @Test
    fun testGenerateLevel242Isolated() {
        println("Starting isolated Level 242 (12x12) test...")
        val t0 = System.currentTimeMillis()
        val verified = generateLevelWithBackbite(
            levelId = 242,
            worldId = 6,
            title = "World 6 - Level 242",
            rows = 12,
            cols = 12,
            minCheckpoints = 18,
            maxCheckpoints = 30,
            maxWalls = 15,
            seed = 6242L,
            minTurns = 42
        )
        println("Generated Level 242 in ${System.currentTimeMillis() - t0}ms:")
        println("CPs=${verified.definition.checkpoints.size}, walls=${verified.definition.blockedEdges.size}, band=${verified.analysis.difficultyTier}, decisions=${verified.analysis.meaningfulDecisionPointCount}")
        assertTrue(verified.definition.checkpoints.size in 18..30)
        assertTrue(verified.definition.blockedEdges.size <= 15)
        assertEquals(144, verified.definition.totalRequiredCells)
    }

    @Test
    fun generateWorld5Levels() {
        println("Generating World 5 (Levels 151–200: 10x10 & 11x11)...")
        val seenFingerprints = HashSet<String>()
        val world5Levels = ArrayList<LevelGeneratorRunnerPart2Test.VerifiedLevel>()

        for (lvlId in 151..200) {
            val is10x10 = lvlId <= 180
            val dim = if (is10x10) 10 else 11
            val minCp = if (is10x10) 14 else 16
            val maxCp = if (is10x10) 20 else 24
            val maxW = if (is10x10) 8 else 11
            val minTurns = if (is10x10) 45 else 50
            val title = "World 5 - Level $lvlId"

            val verified = generateLevelWithBackbite(
                levelId = lvlId,
                worldId = 5,
                title = title,
                rows = dim,
                cols = dim,
                minCheckpoints = minCp,
                maxCheckpoints = maxCp,
                maxWalls = maxW,
                seed = 5000L + lvlId,
                minTurns = minTurns
            )
            val fp = PuzzleFingerprint.computeSha256(verified.definition)
            seenFingerprints.add(fp)
            world5Levels.add(verified)
            println("W5 Level $lvlId (${dim}x${dim}): CPs=${verified.definition.checkpoints.size}, Walls=${verified.definition.blockedEdges.size}, Tier=${verified.analysis.difficultyTier}, Decisions=${verified.analysis.meaningfulDecisionPointCount}, Turns=${countTurns(verified.solution.positions)}")
        }

        assertEquals(50, world5Levels.size)
        writeWorldCodeFile("CuratedWorld5Levels.kt", 5, world5Levels, 151..200)
        writeJsonAssets(world5Levels)
        println("SUCCESS: World 5 generation complete!")
    }

    @Test
    fun generateWorld6PartALevels() {
        println("Generating World 6 Part A (Levels 201–250: 11x11 & 12x12)...")
        val seenFingerprints = HashSet<String>()
        val world6PartALevels = ArrayList<LevelGeneratorRunnerPart2Test.VerifiedLevel>()

        for (lvlId in 201..250) {
            val is11x11 = lvlId <= 240
            val dim = if (is11x11) 11 else 12
            val minCp = if (is11x11) 16 else 18
            val maxCp = if (is11x11) 24 else 30
            val maxW = if (is11x11) 12 else 15
            val minTurns = if (is11x11) 50 else 42
            val title = "World 6 - Level $lvlId"

            val verified = generateLevelWithBackbite(
                levelId = lvlId,
                worldId = 6,
                title = title,
                rows = dim,
                cols = dim,
                minCheckpoints = minCp,
                maxCheckpoints = maxCp,
                maxWalls = maxW,
                seed = 6000L + lvlId,
                minTurns = minTurns
            )
            val fp = PuzzleFingerprint.computeSha256(verified.definition)
            seenFingerprints.add(fp)
            world6PartALevels.add(verified)
            println("W6 Part A Level $lvlId (${dim}x${dim}): CPs=${verified.definition.checkpoints.size}, Walls=${verified.definition.blockedEdges.size}, Tier=${verified.analysis.difficultyTier}, Decisions=${verified.analysis.meaningfulDecisionPointCount}, Turns=${countTurns(verified.solution.positions)}")
        }

        assertEquals(50, world6PartALevels.size)
        val baseDir = File("d:/Zynpath/android/app/src/main/java/com/zynpath/game/core/puzzle/catalog")
        writeSingleObjectFile("CuratedWorld6PartA", 6, world6PartALevels, 201..250, baseDir)
        writeJsonAssets(world6PartALevels)
        println("SUCCESS: World 6 Part A generation complete!")
    }

    @Test
    fun generateWorld6PartBLevels() {
        println("Generating World 6 Part B (Levels 251–300: 12x12 EXTREME)...")
        val seenFingerprints = HashSet<String>()
        val world6PartBLevels = ArrayList<LevelGeneratorRunnerPart2Test.VerifiedLevel>()

        for (lvlId in 251..300) {
            val dim = 12
            val minCp = 18
            val maxCp = 30
            val maxW = if (lvlId == 300) 14 else 15
            val minTurns = 42
            val title = if (lvlId == 300) "Grandmaster Finality" else "World 6 - Level $lvlId"
            val seed = if (lvlId == 300) 300300L else (6000L + lvlId)

            val verified = generateLevelWithBackbite(
                levelId = lvlId,
                worldId = 6,
                title = title,
                rows = dim,
                cols = dim,
                minCheckpoints = minCp,
                maxCheckpoints = maxCp,
                maxWalls = maxW,
                seed = seed,
                minTurns = minTurns
            )
            val fp = PuzzleFingerprint.computeSha256(verified.definition)
            seenFingerprints.add(fp)
            world6PartBLevels.add(verified)
            println("W6 Part B Level $lvlId (${dim}x${dim}): CPs=${verified.definition.checkpoints.size}, Walls=${verified.definition.blockedEdges.size}, Tier=${verified.analysis.difficultyTier}, Decisions=${verified.analysis.meaningfulDecisionPointCount}, Turns=${countTurns(verified.solution.positions)}")
        }

        assertEquals(50, world6PartBLevels.size)
        val baseDir = File("d:/Zynpath/android/app/src/main/java/com/zynpath/game/core/puzzle/catalog")
        val partCLevels = world6PartBLevels.take(25)
        val partDLevels = world6PartBLevels.drop(25)
        writeSingleObjectFile("CuratedWorld6PartC", 6, partCLevels, 251..275, baseDir)
        writeSingleObjectFile("CuratedWorld6PartD", 6, partDLevels, 276..300, baseDir)
        writeJsonAssets(world6PartBLevels)

        // Write World 6 Aggregator object
        val aggregator = """package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath

object CuratedWorld6Levels {
    val LEVELS: Map<Int, PuzzleDefinition> by lazy {
        val map = LinkedHashMap<Int, PuzzleDefinition>()
        map.putAll(CuratedWorld6PartA.LEVELS)
        map.putAll(CuratedWorld6PartB.LEVELS)
        map.putAll(CuratedWorld6PartC.LEVELS)
        map.putAll(CuratedWorld6PartD.LEVELS)
        map
    }

    val SOLUTIONS: Map<Int, PuzzlePath> by lazy {
        val map = LinkedHashMap<Int, PuzzlePath>()
        map.putAll(CuratedWorld6PartA.SOLUTIONS)
        map.putAll(CuratedWorld6PartB.SOLUTIONS)
        map.putAll(CuratedWorld6PartC.SOLUTIONS)
        map.putAll(CuratedWorld6PartD.SOLUTIONS)
        map
    }

    fun getLevel(levelId: Int): PuzzleDefinition? = LEVELS[levelId]
    fun getSolution(levelId: Int): PuzzlePath? = SOLUTIONS[levelId]
}
"""
        File(baseDir, "CuratedWorld6Levels.kt").writeText(aggregator, Charsets.UTF_8)
        println("Written CuratedWorld6Levels.kt (aggregator)")
        println("SUCCESS: World 6 Part B generation complete!")
    }

    private fun writeWorldCodeFile(
        fileName: String,
        worldId: Int,
        levels: List<LevelGeneratorRunnerPart2Test.VerifiedLevel>,
        range: IntRange
    ) {
        val baseDir = File("d:/Zynpath/android/app/src/main/java/com/zynpath/game/core/puzzle/catalog")
        if (levels.size > 25) {
            val mid = levels.size / 2
            val partALevels = levels.take(mid)
            val partBLevels = levels.drop(mid)
            val partARange = range.first..(range.first + mid - 1)
            val partBRange = (range.first + mid)..range.last

            writeSingleObjectFile("CuratedWorld${worldId}PartA", worldId, partALevels, partARange, baseDir)
            writeSingleObjectFile("CuratedWorld${worldId}PartB", worldId, partBLevels, partBRange, baseDir)

            val aggregator = """package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath

object CuratedWorld${worldId}Levels {
    val LEVELS: Map<Int, PuzzleDefinition> by lazy {
        val map = LinkedHashMap<Int, PuzzleDefinition>()
        map.putAll(CuratedWorld${worldId}PartA.LEVELS)
        map.putAll(CuratedWorld${worldId}PartB.LEVELS)
        map
    }

    val SOLUTIONS: Map<Int, PuzzlePath> by lazy {
        val map = LinkedHashMap<Int, PuzzlePath>()
        map.putAll(CuratedWorld${worldId}PartA.SOLUTIONS)
        map.putAll(CuratedWorld${worldId}PartB.SOLUTIONS)
        map
    }

    fun getLevel(levelId: Int): PuzzleDefinition? = LEVELS[levelId]
    fun getSolution(levelId: Int): PuzzlePath? = SOLUTIONS[levelId]
}
"""
            File(baseDir, fileName).writeText(aggregator, Charsets.UTF_8)
            println("Written $fileName (aggregator)")
        } else {
            writeSingleObjectFile(fileName.removeSuffix(".kt"), worldId, levels, range, baseDir)
        }
    }

    private fun writeSingleObjectFile(
        className: String,
        worldId: Int,
        levels: List<LevelGeneratorRunnerPart2Test.VerifiedLevel>,
        range: IntRange,
        baseDir: File
    ) {
        val sb = StringBuilder()
        sb.append("package com.zynpath.game.core.puzzle.catalog\n\n")
        sb.append("import com.zynpath.game.core.puzzle.model.BlockedEdge\n")
        sb.append("import com.zynpath.game.core.puzzle.model.GridDimensions\n")
        sb.append("import com.zynpath.game.core.puzzle.model.GridPosition\n")
        sb.append("import com.zynpath.game.core.puzzle.model.NumberedCheckpoint\n")
        sb.append("import com.zynpath.game.core.puzzle.model.PuzzleDefinition\n")
        sb.append("import com.zynpath.game.core.puzzle.model.PuzzlePath\n\n")
        sb.append("object $className {\n\n")

        sb.append("    fun grid10x10Cells(): Set<GridPosition> =\n")
        sb.append("        (0..9).flatMap { r -> (0..9).map { c -> GridPosition(r, c) } }.toSet()\n\n")

        sb.append("    fun grid11x11Cells(): Set<GridPosition> =\n")
        sb.append("        (0..10).flatMap { r -> (0..10).map { c -> GridPosition(r, c) } }.toSet()\n\n")

        sb.append("    fun grid12x12Cells(): Set<GridPosition> =\n")
        sb.append("        (0..11).flatMap { r -> (0..11).map { c -> GridPosition(r, c) } }.toSet()\n\n")

        for (lvl in levels) {
            val id = lvl.levelId
            val def = lvl.definition
            val sol = lvl.solution
            val rows = def.gridDimensions.rows
            val cols = def.gridDimensions.columns

            val cellsHelper = when (rows) {
                10 -> "grid10x10Cells()"
                11 -> "grid11x11Cells()"
                12 -> "grid12x12Cells()"
                else -> "grid10x10Cells()"
            }

            sb.append("    // --- Level $id: ${lvl.title} (${rows}x${cols}) ---\n\n")
            sb.append("    fun buildLevel$id(): PuzzleDefinition = PuzzleDefinition(\n")
            sb.append("        puzzleId = \"${def.puzzleId}\",\n")
            sb.append("        puzzleVersion = ${def.puzzleVersion},\n")
            sb.append("        gridDimensions = GridDimensions($rows, $cols),\n")
            sb.append("        requiredCells = $cellsHelper,\n")
            sb.append("        checkpoints = listOf(\n")
            val cpChunks = def.checkpoints.chunked(4)
            for ((idx, chunk) in cpChunks.withIndex()) {
                val comma = if (idx == cpChunks.size - 1) "" else ","
                sb.append("            ${chunk.joinToString(", ") { "NumberedCheckpoint(${it.number}, GridPosition(${it.position.row}, ${it.position.column}))" }}$comma\n")
            }
            sb.append("        ),\n")
            val wallsStr = if (def.blockedEdges.isEmpty()) {
                "emptySet()"
            } else {
                "setOf(\n" + def.blockedEdges.joinToString(",\n") {
                    "            BlockedEdge.between(GridPosition(${it.first.row}, ${it.first.column}), GridPosition(${it.second.row}, ${it.second.column}))"
                } + "\n        )"
            }
            sb.append("        blockedEdges = $wallsStr,\n")
            sb.append("        difficultyMetadata = \"${def.difficultyMetadata}\",\n")
            sb.append("        seed = ${def.seed}L\n")
            sb.append("    )\n\n")

            sb.append("    fun buildSolution$id(): PuzzlePath = PuzzlePath(\n")
            sb.append("        listOf(\n")
            val posChunks = sol.positions.chunked(8)
            for ((idx, chunk) in posChunks.withIndex()) {
                val comma = if (idx == posChunks.size - 1) "" else ","
                sb.append("            ${chunk.joinToString(", ") { "GridPosition(${it.row}, ${it.column})" }}$comma\n")
            }
            sb.append("        )\n")
            sb.append("    )\n\n")
        }

        sb.append("    val LEVELS: Map<Int, PuzzleDefinition> by lazy {\n")
        sb.append("        val map = LinkedHashMap<Int, PuzzleDefinition>()\n")
        for (i in range) {
            sb.append("        map[$i] = buildLevel$i()\n")
        }
        sb.append("        map\n")
        sb.append("    }\n\n")

        sb.append("    val SOLUTIONS: Map<Int, PuzzlePath> by lazy {\n")
        sb.append("        val map = LinkedHashMap<Int, PuzzlePath>()\n")
        for (i in range) {
            sb.append("        map[$i] = buildSolution$i()\n")
        }
        sb.append("        map\n")
        sb.append("    }\n")
        sb.append("}\n")

        val targetFile = File(baseDir, "$className.kt")
        targetFile.writeText(sb.toString(), Charsets.UTF_8)
        println("Written $className.kt (${targetFile.length()} bytes)")
    }

    private fun writeJsonAssets(levels: List<LevelGeneratorRunnerPart2Test.VerifiedLevel>) {
        val baseAssets = File("d:/Zynpath/android/app/src/main/assets/puzzles")
        for (lvl in levels) {
            val worldDir = File(baseAssets, "w${lvl.worldId}")
            worldDir.mkdirs()
            val file = File(worldDir, "lvl${lvl.levelId}.json")

            val fp = PuzzleFingerprint.computeSha256(lvl.definition)
            val band = when {
                lvl.worldId == 5 && lvl.definition.gridDimensions.rows == 10 -> "VERY_HARD"
                lvl.worldId == 5 && lvl.definition.gridDimensions.rows == 11 -> "EXPERT"
                lvl.worldId == 6 && lvl.definition.gridDimensions.rows == 11 -> "EXPERT"
                else -> "EXTREME"
            }
            val score = if (lvl.worldId == 5) {
                0.85 + ((lvl.levelId - 150) * 0.002)
            } else {
                0.92 + ((lvl.levelId - 200) * 0.0008)
            }

            val asset = PuzzleAsset.fromPuzzleDefinition(
                def = lvl.definition,
                metadata = PuzzleAssetMetadata(
                    generatorVersion = "2.0.0",
                    generationSeed = lvl.definition.seed,
                    difficultyEstimate = score,
                    difficultyBand = band,
                    uniquenessStatus = "UNIQUE",
                    fingerprint = fp
                )
            )
            file.writeText(PuzzleAssetSerializer.serialize(asset), Charsets.UTF_8)
        }
        println("Written ${levels.size} JSON puzzle assets to assets/puzzles/w${levels.first().worldId}")
    }
}

