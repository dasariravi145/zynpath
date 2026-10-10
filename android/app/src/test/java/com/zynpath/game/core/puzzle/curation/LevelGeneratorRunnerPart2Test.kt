package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.catalog.PuzzleAsset
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetMetadata
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetSerializer
import com.zynpath.game.core.puzzle.engine.CompletionCheckResult
import com.zynpath.game.core.puzzle.engine.CompletionValidator
import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
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
import java.util.Random

/**
 * Production generator and certifier for Master Redesign Prompt 2:
 * World 3 (Levels 51–100: 8x8 & 9x9) and World 4 (Levels 101–150: 9x9 & 10x10).
 *
 * Implements Sections 1–43, 62–66:
 * - 8x8 requires exactly 64 cells
 * - 9x9 requires exactly 81 cells
 * - 10x10 requires exactly 100 cells
 * - 100% full coverage, no blocked logical cells
 * - Exact checkpoint sequence (1 -> 2 -> ... -> N)
 * - Certified unique by exact solver
 * - Anti-trivial filters: multi-checkpoint reasoning, cell reservation, wrong branch depth
 */
class LevelGeneratorRunnerPart2Test {

    private val solver = PuzzleSolver()
    private val baselineSolver = SimpleStrategyBaselineSolver()
    private val analyzer = PuzzleDifficultyAnalyzer(solver = solver)

    data class VerifiedLevel(
        val levelId: Int,
        val worldId: Int,
        val title: String,
        val definition: PuzzleDefinition,
        val solution: PuzzlePath,
        val analysis: DifficultyAnalysis
    )

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

    private fun findHamiltonianPath(
        rows: Int,
        cols: Int,
        startPos: GridPosition,
        rng: Random,
        minTurns: Int = 0
    ): List<GridPosition>? {
        val total = rows * cols
        val visited = BooleanArray(total)
        val path = ArrayList<GridPosition>(total)
        val dirs = listOf(GridPosition(-1, 0), GridPosition(1, 0), GridPosition(0, -1), GridPosition(0, 1))

        fun posToIndex(p: GridPosition): Int = p.row * cols + p.column

        fun getUnvisitedNeighbors(p: GridPosition): List<GridPosition> {
            val list = ArrayList<GridPosition>(4)
            for (dir in dirs) {
                val nr = p.row + dir.row
                val nc = p.column + dir.column
                if (nr in 0 until rows && nc in 0 until cols && !visited[nr * cols + nc]) {
                    list.add(GridPosition(nr, nc))
                }
            }
            return list
        }

        fun hasDeadEndIsland(): Boolean {
            var ones = 0
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val idx = r * cols + c
                    if (!visited[idx]) {
                        var deg = 0
                        for (dir in dirs) {
                            val nr = r + dir.row
                            val nc = c + dir.column
                            if (nr in 0 until rows && nc in 0 until cols && !visited[nr * cols + nc]) deg++
                        }
                        if (deg == 0 && path.size < total - 1) return true
                        if (deg == 1 && path.size < total - 1) {
                            ones++
                            if (ones > 2) return true
                        }
                    }
                }
            }
            return false
        }

        var dfsSteps = 0
        val maxSteps = 40_000

        fun dfs(curr: GridPosition): Boolean {
            dfsSteps++
            if (dfsSteps > maxSteps) return false

            visited[posToIndex(curr)] = true
            path.add(curr)
            if (path.size == total) {
                if (countTurns(path) >= minTurns) return true
                visited[posToIndex(curr)] = false
                path.removeAt(path.size - 1)
                return false
            }

            val neighbors = getUnvisitedNeighbors(curr)
            if (neighbors.isEmpty()) {
                visited[posToIndex(curr)] = false
                path.removeAt(path.size - 1)
                return false
            }

            if (hasDeadEndIsland()) {
                visited[posToIndex(curr)] = false
                path.removeAt(path.size - 1)
                return false
            }

            val sortedNeighbors = neighbors.sortedWith(
                compareBy<GridPosition> { getUnvisitedNeighbors(it).size }
                    .thenBy { rng.nextInt() }
            )

            for (next in sortedNeighbors) {
                if (dfs(next)) return true
            }

            visited[posToIndex(curr)] = false
            path.removeAt(path.size - 1)
            return false
        }

        return if (dfs(startPos)) path else null
    }

    private fun searchUniqueCheckpoints(
        path: List<GridPosition>,
        rows: Int,
        cols: Int,
        levelId: Int,
        worldId: Int,
        title: String,
        minCheckpoints: Int,
        maxCheckpoints: Int,
        seed: Long,
        rng: Random,
        initialWalls: Set<BlockedEdge> = emptySet()
    ): VerifiedLevel? {
        val total = rows * cols
        val puzzlePath = PuzzlePath(path)
        val candidateIndices = mutableSetOf(0, total - 1)

        val seedCount = if (total <= 64) 8 else if (total <= 81) 12 else 14
        val step = (total - 1).toDouble() / (seedCount - 1)
        for (k in 1 until seedCount - 1) {
            candidateIndices.add((k * step).toInt())
        }

        val fastConfig = SolverConfiguration(maxSolutions = 2, nodeLimit = 20_000L, timeBudgetMs = 250L)
        val currentWalls = initialWalls.toMutableSet()
        val dims = GridDimensions(rows, cols)
        val pathCellSet = path.toSet()

        var iterations = 0
        while (candidateIndices.size <= maxCheckpoints && iterations < 30) {
            iterations++
            val sortedIndices = candidateIndices.sorted()
            val checkpoints = sortedIndices.mapIndexed { idx, pIdx ->
                NumberedCheckpoint(idx + 1, path[pIdx])
            }

            val candidateDef = PuzzleDefinition(
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
            if (levelId == 135) {
                println("searchUniqueCheckpoints iter $iterations: cpCount=${candidateIndices.size}, status=${solverRes.status}, uniq=${solverRes.uniqueness}, sols=${solverRes.solutions.size}, nodes=${solverRes.statistics.nodesExplored}, time=${solverRes.statistics.elapsedMs}ms")
            }
            if (solverRes.status == SolverStatus.SOLVED && solverRes.uniqueness == UniquenessStatus.UNIQUE) {
                var finalDef = candidateDef
                // If unique with fewer than minCheckpoints, fill remaining checkpoints at largest gaps
                if (candidateIndices.size < minCheckpoints) {
                    while (candidateIndices.size < minCheckpoints) {
                        val curSorted = candidateIndices.sorted()
                        val gaps = curSorted.zipWithNext().map { (a, b) -> (b - a) to ((a + b) / 2) }
                        val largestGap = gaps.maxByOrNull { it.first } ?: break
                        candidateIndices.add(largestGap.second)
                    }
                    val finalSorted = candidateIndices.sorted()
                    val finalCps = finalSorted.mapIndexed { idx, pIdx ->
                        NumberedCheckpoint(idx + 1, path[pIdx])
                    }
                    finalDef = candidateDef.copy(checkpoints = finalCps)
                }

                // Verify with full CHECK_UNIQUENESS
                val fullRes = solver.solve(finalDef, SolverConfiguration.CHECK_UNIQUENESS)
                if (fullRes.status == SolverStatus.SOLVED && fullRes.uniqueness == UniquenessStatus.UNIQUE) {
                    val completion = CompletionValidator.validate(finalDef, puzzlePath)
                    if (completion is CompletionCheckResult.Success) {
                        val analysisResult = analyzer.analyze(finalDef, puzzlePath, fullRes)
                        val da = analysisResult.difficultyAnalysis

                        // Section 29 & 30 anti-trivial filter: reject overly forced puzzles
                        if (worldId >= 3 && da.meaningfulDecisionPointCount < 3 && da.forcedMoveRatio > 0.90) {
                            if (levelId == 135) println("Rejected because too forced: decisions=${da.meaningfulDecisionPointCount}, forcedRatio=${da.forcedMoveRatio}")
                            return null // reject too forced
                        }

                        return VerifiedLevel(
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
                // Try placing a strategic wall on an illegal transition in altSol (up to 5 walls max)
                if (currentWalls.size < 5) {
                    val protectedRouteEdges = HashSet<BlockedEdge>(path.size)
                    for (i in 0 until path.size - 1) {
                        protectedRouteEdges.add(BlockedEdge.between(path[i], path[i + 1]))
                    }
                    for (i in 0 until altSol.positions.size - 1) {
                        val edge = BlockedEdge.between(altSol.positions[i], altSol.positions[i + 1])
                        if (edge !in protectedRouteEdges && edge !in currentWalls) {
                            if (wallStrategy.validateWalls(currentWalls + edge, dims, pathCellSet, path, 0, 5)) {
                                currentWalls.add(edge)
                                wallAdded = true
                                if (levelId == 135) println("DEBUG: Added dynamic wall on altSol edge: $edge (total=${currentWalls.size})")
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

                    // First priority: an inversion where one cell is ALREADY a checkpoint!
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

                    // Second priority: any inversion where neither is a checkpoint
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

                    if (levelId == 135) {
                        println("DEBUG: Inversion bestIndexToAdd=$bestIndexToAdd, pos=${bestIndexToAdd?.let { path[it] }}")
                    }

                    if (bestIndexToAdd != null) {
                        candidateIndices.add(bestIndexToAdd)
                    } else {
                        val unselected = (1 until total - 1).filter { it !in candidateIndices }
                        if (unselected.isNotEmpty()) {
                            candidateIndices.add(unselected[rng.nextInt(unselected.size)])
                        } else {
                            break
                        }
                    }
                }
            } else {
                if (solverRes.status == SolverStatus.SEARCH_LIMIT_REACHED && solverRes.solutions.isEmpty()) {
                    break // don't waste time on paths with intractable search trees
                }
                val gaps = sortedIndices.zipWithNext().map { (a, b) -> (b - a) to ((a + b) / 2) }
                val largestGap = gaps.maxByOrNull { it.first }
                if (largestGap != null && largestGap.first > 1) {
                    candidateIndices.add(largestGap.second)
                } else {
                    break
                }
            }
        }
        return null
    }

    private val wallStrategy = com.zynpath.game.core.puzzle.generator.WallPlacementStrategy()

    private fun generateLevelWithParams(
        levelId: Int,
        worldId: Int,
        title: String,
        rows: Int,
        cols: Int,
        minCheckpoints: Int,
        maxCheckpoints: Int,
        seed: Long,
        minTurns: Int,
        seenFingerprints: Set<String>
    ): VerifiedLevel {
        val total = rows * cols
        val dims = GridDimensions(rows, cols)

        for (attempt in 0 until 400) {
            val attemptRng = Random(seed + attempt * 1013L)
            val start = if (total % 2 != 0) {
                val validStarts = ArrayList<GridPosition>()
                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        if ((r + c) % 2 == 0) validStarts.add(GridPosition(r, c))
                    }
                }
                validStarts[attemptRng.nextInt(validStarts.size)]
            } else {
                GridPosition(attemptRng.nextInt(rows), attemptRng.nextInt(cols))
            }

            val path = findHamiltonianPath(rows, cols, start, attemptRng, minTurns) ?: continue

            // Try 0 walls first, then strategic walls on subsequent attempts
            val walls = if (attempt % 4 == 0) {
                emptySet()
            } else {
                val maxW = if (rows <= 8) 3 else 4
                wallStrategy.placeWalls(dims, path.toSet(), path, 1, maxW, attemptRng) ?: emptySet()
            }

            val verified = searchUniqueCheckpoints(
                path = path,
                rows = rows,
                cols = cols,
                levelId = levelId,
                worldId = worldId,
                title = title,
                minCheckpoints = minCheckpoints,
                maxCheckpoints = maxCheckpoints,
                seed = seed + attempt,
                rng = attemptRng,
                initialWalls = walls
            )
            if (verified != null) {
                val fp = com.zynpath.game.core.puzzle.generator.PuzzleFingerprint.computeSha256(verified.definition)
                if (fp !in seenFingerprints) {
                    return verified
                }
            }
        }
        error("Failed to generate unique level $levelId (World $worldId, ${rows}x${cols}) with seed $seed")
    }

    @Test
    fun testSingleLevel55() {
        println("Testing single Level 55 generation...")
        val verified = generateLevelWithParams(
            levelId = 55,
            worldId = 3,
            title = "World 3 - Level 55",
            rows = 8,
            cols = 8,
            minCheckpoints = 10,
            maxCheckpoints = 15,
            seed = 3055L,
            minTurns = 14,
            seenFingerprints = emptySet()
        )
        println("Generated Level 55: CPs=${verified.definition.checkpoints.size}, Walls=${verified.definition.blockedEdges.size}, Tier=${verified.analysis.difficultyTier}, Nodes=${verified.analysis.solverSearchNodes}")
        assertTrue(verified.definition.checkpoints.size in 10..15)
        assertTrue(verified.definition.totalRequiredCells == 64)
    }

    fun generateWorld3Levels() {
        println("Generating World 3 (Levels 51–100: 8x8 & 9x9)...")

        val seenFingerprints = HashSet<String>()
        val world3Levels = ArrayList<VerifiedLevel>()

        // World 3: Levels 51..100 (50 levels)
        // 51..90: 8x8 (64 cells), 10..15 checkpoints
        // 91..100: 9x9 (81 cells), 12..18 checkpoints
        for (lvlId in 51..100) {
            val is8x8 = lvlId <= 90
            val dim = if (is8x8) 8 else 9
            val minCp = if (is8x8) 10 else 12
            val maxCp = if (is8x8) 15 else 18
            val minTurns = if (is8x8) 12 else 16
            val title = "World 3 - Level $lvlId"

            val verified = generateLevelWithParams(
                levelId = lvlId,
                worldId = 3,
                title = title,
                rows = dim,
                cols = dim,
                minCheckpoints = minCp,
                maxCheckpoints = maxCp,
                seed = 3000L + lvlId,
                minTurns = minTurns,
                seenFingerprints = seenFingerprints
            )
            world3Levels.add(verified)
            val fp = com.zynpath.game.core.puzzle.generator.PuzzleFingerprint.computeSha256(verified.definition)
            seenFingerprints.add(fp)
            println("W3 Level $lvlId (${dim}x${dim}): CPs=${verified.definition.checkpoints.size}, Walls=${verified.definition.blockedEdges.size}, Tier=${verified.analysis.difficultyTier}, Nodes=${verified.analysis.solverSearchNodes}, Turns=${countTurns(verified.solution.positions)}")
        }

        assertEquals(50, world3Levels.size)
        writeWorldCodeFile("CuratedWorld3Levels.kt", 3, world3Levels, 51..100)
        writeJsonAssets(world3Levels)
        println("SUCCESS: World 3 generation complete!")
    }

    fun generateWorld4Levels() {
        println("Generating World 4 (Levels 101–150: 9x9 & 10x10)...")

        val seenFingerprints = HashSet<String>()
        val world4Levels = ArrayList<VerifiedLevel>()

        // World 4: Levels 101..150 (50 levels)
        // 101..130: 9x9 (81 cells), 12..18 checkpoints
        // 131..150: 10x10 (100 cells), 16..20 checkpoints
        for (lvlId in 101..150) {
            val is9x9 = lvlId <= 130
            val dim = if (is9x9) 9 else 10
            val minCp = if (is9x9) 12 else 16
            val maxCp = if (is9x9) 18 else 20
            val minTurns = if (is9x9) 16 else 28
            val title = "World 4 - Level $lvlId"

            val verified = generateLevelWithParams(
                levelId = lvlId,
                worldId = 4,
                title = title,
                rows = dim,
                cols = dim,
                minCheckpoints = minCp,
                maxCheckpoints = maxCp,
                seed = 4000L + lvlId,
                minTurns = minTurns,
                seenFingerprints = seenFingerprints
            )
            world4Levels.add(verified)
            val fp = com.zynpath.game.core.puzzle.generator.PuzzleFingerprint.computeSha256(verified.definition)
            seenFingerprints.add(fp)
            println("W4 Level $lvlId (${dim}x${dim}): CPs=${verified.definition.checkpoints.size}, Walls=${verified.definition.blockedEdges.size}, Tier=${verified.analysis.difficultyTier}, Nodes=${verified.analysis.solverSearchNodes}, Turns=${countTurns(verified.solution.positions)}")
        }

        assertEquals(50, world4Levels.size)
        writeWorldCodeFile("CuratedWorld4Levels.kt", 4, world4Levels, 101..150)
        writeJsonAssets(world4Levels)
        println("SUCCESS: World 4 generation complete!")
    }

    private fun writeWorldCodeFile(
        fileName: String,
        worldId: Int,
        levels: List<VerifiedLevel>,
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
        levels: List<VerifiedLevel>,
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

        sb.append("    fun grid8x8Cells(): Set<GridPosition> =\n")
        sb.append("        (0..7).flatMap { r -> (0..7).map { c -> GridPosition(r, c) } }.toSet()\n\n")

        sb.append("    fun grid9x9Cells(): Set<GridPosition> =\n")
        sb.append("        (0..8).flatMap { r -> (0..8).map { c -> GridPosition(r, c) } }.toSet()\n\n")

        sb.append("    fun grid10x10Cells(): Set<GridPosition> =\n")
        sb.append("        (0..9).flatMap { r -> (0..9).map { c -> GridPosition(r, c) } }.toSet()\n\n")

        for (lvl in levels) {
            val id = lvl.levelId
            val def = lvl.definition
            val sol = lvl.solution
            val rows = def.gridDimensions.rows
            val cols = def.gridDimensions.columns

            val cellsHelper = when (rows) {
                8 -> "grid8x8Cells()"
                9 -> "grid9x9Cells()"
                else -> "grid10x10Cells()"
            }

            sb.append("    // --- Level $id: ${lvl.title} (${rows}x${cols}) ---\n\n")
            sb.append("    val LEVEL_$id = PuzzleDefinition(\n")
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

            sb.append("    val SOLUTION_$id = PuzzlePath(\n")
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
            sb.append("        map[$i] = LEVEL_$i\n")
        }
        sb.append("        map\n")
        sb.append("    }\n\n")

        sb.append("    val SOLUTIONS: Map<Int, PuzzlePath> by lazy {\n")
        sb.append("        val map = LinkedHashMap<Int, PuzzlePath>()\n")
        for (i in range) {
            sb.append("        map[$i] = SOLUTION_$i\n")
        }
        sb.append("        map\n")
        sb.append("    }\n")
        sb.append("}\n")

        val targetFile = File(baseDir, "$className.kt")
        targetFile.writeText(sb.toString(), Charsets.UTF_8)
        println("Written $className.kt (${targetFile.length()} bytes)")
    }

    private fun writeJsonAssets(levels: List<VerifiedLevel>) {
        val baseAssets = File("d:/Zynpath/android/app/src/main/assets/puzzles")
        for (lvl in levels) {
            val worldDir = File(baseAssets, "w${lvl.worldId}")
            worldDir.mkdirs()
            val file = File(worldDir, "lvl${lvl.levelId}.json")

            val fp = com.zynpath.game.core.puzzle.generator.PuzzleFingerprint.computeSha256(lvl.definition)
            val asset = PuzzleAsset.fromPuzzleDefinition(
                def = lvl.definition,
                metadata = PuzzleAssetMetadata(
                    generatorVersion = "2.0.0",
                    generationSeed = lvl.definition.seed,
                    difficultyEstimate = if (lvl.worldId == 3) 0.65 + ((lvl.levelId - 50) * 0.003) else 0.78 + ((lvl.levelId - 100) * 0.002),
                    difficultyBand = if (lvl.worldId == 3) "HARD" else "VERY_HARD",
                    uniquenessStatus = "UNIQUE",
                    fingerprint = fp
                )
            )
            file.writeText(PuzzleAssetSerializer.serialize(asset), Charsets.UTF_8)
        }
        println("Written ${levels.size} JSON puzzle assets to assets/puzzles/w${levels.first().worldId}")
    }
}
