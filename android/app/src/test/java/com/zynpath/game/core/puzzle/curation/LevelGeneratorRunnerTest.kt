package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.catalog.CatalogAssetGenerator
import com.zynpath.game.core.puzzle.catalog.PackagedPuzzles
import com.zynpath.game.core.puzzle.catalog.PuzzleAsset
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetMetadata
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetSerializer
import com.zynpath.game.core.puzzle.catalog.WorldDefinition
import com.zynpath.game.core.puzzle.engine.CompletionCheckResult
import com.zynpath.game.core.puzzle.engine.CompletionValidator
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

class LevelGeneratorRunnerTest {

    private val solver = PuzzleSolver()
    private val baselineSolver = SimpleStrategyBaselineSolver()
    private val analyzer = PuzzleDifficultyAnalyzer(solver = solver)

    data class VerifiedLevel(
        val levelId: Int,
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

    private fun findHamiltonianPath(rows: Int, cols: Int, startPos: GridPosition, rng: Random, minTurns: Int = 0): List<GridPosition>? {
        val total = rows * cols
        val visited = BooleanArray(total)
        val path = ArrayList<GridPosition>(total)

        fun posToIndex(p: GridPosition): Int = p.row * cols + p.column

        fun getUnvisitedNeighbors(p: GridPosition): List<GridPosition> {
            val list = mutableListOf<GridPosition>()
            for (dir in listOf(GridPosition(-1, 0), GridPosition(1, 0), GridPosition(0, -1), GridPosition(0, 1))) {
                val next = GridPosition(p.row + dir.row, p.column + dir.column)
                if (next.row in 0 until rows && next.column in 0 until cols && !visited[posToIndex(next)]) {
                    list.add(next)
                }
            }
            return list
        }

        fun dfs(curr: GridPosition): Boolean {
            visited[posToIndex(curr)] = true
            path.add(curr)
            if (path.size == total) {
                if (countTurns(path) >= minTurns) return true
                visited[posToIndex(curr)] = false
                path.removeAt(path.size - 1)
                return false
            }

            val neighbors = getUnvisitedNeighbors(curr)
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
        title: String,
        minCheckpoints: Int,
        maxCheckpoints: Int,
        seed: Long,
        rng: Random
    ): VerifiedLevel? {
        val total = rows * cols
        val puzzlePath = PuzzlePath(path)
        val candidateIndices = mutableSetOf(0, total - 1)

        // Seed with minCheckpoints evenly-spaced checkpoints
        val step = (total - 1).toDouble() / (minCheckpoints - 1)
        for (k in 1 until minCheckpoints - 1) {
            candidateIndices.add((k * step).toInt())
        }

        val fastConfig = SolverConfiguration(maxSolutions = 2, nodeLimit = 20000L, timeBudgetMs = 500L)

        var iterations = 0
        while (candidateIndices.size <= maxCheckpoints && iterations < 35) {
            iterations++
            val sortedIndices = candidateIndices.sorted()
            val checkpoints = sortedIndices.mapIndexed { idx, pIdx ->
                NumberedCheckpoint(idx + 1, path[pIdx])
            }

            val candidateDef = PuzzleDefinition(
                puzzleId = if (rows == 6) "w1_lvl$levelId" else "w2_lvl$levelId",
                puzzleVersion = 4,
                gridDimensions = GridDimensions(rows, cols),
                requiredCells = path.toSet(),
                checkpoints = checkpoints,
                blockedEdges = emptySet(),
                difficultyMetadata = "$title (seed=$seed)",
                seed = seed
            )

            val solverRes = solver.solve(candidateDef, fastConfig)
            if (solverRes.status == SolverStatus.SOLVED && solverRes.uniqueness == UniquenessStatus.UNIQUE) {
                // Verify with full CHECK_UNIQUENESS
                val fullRes = solver.solve(candidateDef, SolverConfiguration.CHECK_UNIQUENESS)
                if (fullRes.status == SolverStatus.SOLVED && fullRes.uniqueness == UniquenessStatus.UNIQUE) {
                    val completion = CompletionValidator.validate(candidateDef, puzzlePath)
                    if (completion is CompletionCheckResult.Success) {
                        val analysisResult = analyzer.analyze(candidateDef, puzzlePath, fullRes)
                        return VerifiedLevel(
                            levelId = levelId,
                            title = title,
                            definition = candidateDef,
                            solution = puzzlePath,
                            analysis = analysisResult.difficultyAnalysis
                        )
                    }
                }
            }

            if (solverRes.solutions.size >= 2) {
                val sol1 = solverRes.solutions[0]
                val sol2 = solverRes.solutions[1]
                val altSol = if (sol1.positions == path) sol2 else sol1
                var diffIdx = -1
                for (i in 1 until total - 1) {
                    if (i !in candidateIndices && (i >= altSol.positions.size || path[i] != altSol.positions[i])) {
                        diffIdx = i
                        break
                    }
                }
                if (diffIdx != -1) {
                    candidateIndices.add(diffIdx)
                } else {
                    val unselected = (1 until total - 1).filter { it !in candidateIndices }
                    if (unselected.isNotEmpty()) {
                        candidateIndices.add(unselected[rng.nextInt(unselected.size)])
                    } else {
                        break
                    }
                }
            } else {
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

    private fun generateLevelWithParams(
        levelId: Int,
        title: String,
        rows: Int,
        cols: Int,
        minCheckpoints: Int,
        maxCheckpoints: Int,
        seed: Long,
        startPosition: GridPosition? = null,
        minTurns: Int = 0,
        seenFingerprints: Set<String> = emptySet()
    ): VerifiedLevel {
        val total = rows * cols

        for (attempt in 0 until 500) {
            val attemptRng = Random(seed + attempt * 997L)
            val start = startPosition ?: if (total % 2 != 0) {
                val validStarts = mutableListOf<GridPosition>()
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
            val verified = searchUniqueCheckpoints(
                path = path,
                rows = rows,
                cols = cols,
                levelId = levelId,
                title = title,
                minCheckpoints = minCheckpoints,
                maxCheckpoints = maxCheckpoints,
                seed = seed + attempt,
                rng = attemptRng
            )
            if (verified != null) {
                val fp = com.zynpath.game.core.puzzle.generator.PuzzleFingerprint.computeSha256(verified.definition)
                if (fp in seenFingerprints) {
                    continue
                }
                return verified
            }
        }
        error("Failed to generate unique level $levelId with seed $seed")
    }


    @Test
    fun generateAll50Levels() {
        println("Generating all 50 verified levels (World 1: 6x6, World 2: 7x7)...")
        val world1Titles = listOf(
            "Learn the Path", "Numbered Checkpoints", "Cover Every Cell", "Plan Your Turns",
            "The Serpentine Loop", "Cross Currents", "Corner Lock", "Perimeter Spiral",
            "The Hourglass", "Pinwheel Turn", "Cul-de-Sac Weave", "Quad Quadrants",
            "Midpoint Challenge", "Double S-Curve", "Switchback Ridge", "The Fortress",
            "Squeeze Point", "Snake vs Spiral", "The Labyrinth Gate", "World 1 Climax"
        )

        val world2Titles = listOf(
            "First Steps", "Seven Seas", "Cell Reservation", "Delayed Divergence",
            "Chapter 1 Climax", "Center Pivot", "Perimeter Trench", "Double Spiral",
            "Crossroads Maze", "The Gauntlet", "Weave and Turn", "Outer Ribbon",
            "The Chasm", "Symmetric Balance", "Delayed Failure", "Snake Trap",
            "Corner Isolation", "Midpoint Threshold", "Turn Density", "The Squeeze",
            "Diagonal Flow", "Spiral Core", "Long Horizons", "The Iron Ring",
            "False Shortcut", "Dead End Decoy", "Twin Loops", "Master Weave",
            "The Grand Gate", "Smart Turns Climax"
        )

        val allLevels = mutableListOf<VerifiedLevel>()
        val seenFingerprints = mutableSetOf<String>()

        // World 1: Levels 1..20 (6x6, 6..10 checkpoints)
        for (i in 1..20) {
            val title = world1Titles[i - 1]
            val minCp = 6
            val maxCp = 10
            val startPos = if (i == 1) GridPosition(0, 0) else null
            val minTurns = when (i) {
                1 -> 0
                in 2..4 -> 4
                else -> 8
            }
            val verified = generateLevelWithParams(
                levelId = i,
                title = title,
                rows = 6,
                cols = 6,
                minCheckpoints = minCp,
                maxCheckpoints = maxCp,
                seed = 1000L + i,
                startPosition = startPos,
                minTurns = minTurns,
                seenFingerprints = seenFingerprints
            )
            allLevels.add(verified)
            val fp = com.zynpath.game.core.puzzle.generator.PuzzleFingerprint.computeSha256(verified.definition)
            seenFingerprints.add(fp)
            println("W1 Level $i: [${verified.title}] CPs=${verified.definition.checkpoints.size}, Nodes=${verified.analysis.solverSearchNodes}, Turns=${countTurns(verified.solution.positions)}")
        }

        // World 2: Levels 21..50 (7x7, 8..12 checkpoints)
        for (i in 21..50) {
            val title = world2Titles[i - 21]
            val minCp = 8
            val maxCp = 12
            val minTurns = 10
            val verified = generateLevelWithParams(
                levelId = i,
                title = title,
                rows = 7,
                cols = 7,
                minCheckpoints = minCp,
                maxCheckpoints = maxCp,
                seed = 2000L + i,
                minTurns = minTurns,
                seenFingerprints = seenFingerprints
            )
            allLevels.add(verified)
            val fp = com.zynpath.game.core.puzzle.generator.PuzzleFingerprint.computeSha256(verified.definition)
            seenFingerprints.add(fp)
            println("W2 Level $i: [${verified.title}] CPs=${verified.definition.checkpoints.size}, Nodes=${verified.analysis.solverSearchNodes}, Turns=${countTurns(verified.solution.positions)}")
        }

        assertEquals(50, allLevels.size)

        // Write to CuratedFirst50Levels.kt
        writeCuratedFirst50Levels(allLevels)

        // Write assets
        writeAssets(allLevels)

    }

    private fun writeCuratedFirst50Levels(levels: List<VerifiedLevel>) {
        val sb = StringBuilder()
        sb.append("package com.zynpath.game.core.puzzle.catalog\n\n")
        sb.append("import com.zynpath.game.core.puzzle.model.BlockedEdge\n")
        sb.append("import com.zynpath.game.core.puzzle.model.GridDimensions\n")
        sb.append("import com.zynpath.game.core.puzzle.model.GridPosition\n")
        sb.append("import com.zynpath.game.core.puzzle.model.NumberedCheckpoint\n")
        sb.append("import com.zynpath.game.core.puzzle.model.PuzzleDefinition\n")
        sb.append("import com.zynpath.game.core.puzzle.model.PuzzlePath\n\n")
        sb.append("/**\n")
        sb.append(" * Authoritative curated level definitions and verified canonical solutions for Solo Levels 1–50.\n")
        sb.append(" *\n")
        sb.append(" * Redesigned for Zynpath Master Redesign (Prompt 1 of 3):\n")
        sb.append(" * - World 1: 6x6, 36 cells, 6–10 checkpoints, 0 walls (Levels 1–20).\n")
        sb.append(" * - World 2: 7x7, 49 cells, 8–12 checkpoints, 0 walls (Levels 21–50).\n")
        sb.append(" * - All 50 levels solver-verified unique, 100% playable coverage, strictly ascending checkpoints.\n")
        sb.append(" */\n")
        sb.append("object CuratedFirst50Levels {\n\n")
        sb.append("    fun grid6x6Cells(): Set<GridPosition> =\n")
        sb.append("        (0..5).flatMap { r -> (0..5).map { c -> GridPosition(r, c) } }.toSet()\n\n")
        sb.append("    fun grid7x7Cells(): Set<GridPosition> =\n")
        sb.append("        (0..6).flatMap { r -> (0..6).map { c -> GridPosition(r, c) } }.toSet()\n\n")

        for (lvl in levels) {
            val id = lvl.levelId
            val def = lvl.definition
            val sol = lvl.solution
            val rows = def.gridDimensions.rows
            val cols = def.gridDimensions.columns

            sb.append("    // --- Level $id: ${lvl.title} (${rows}x$cols) ---\n\n")
            sb.append("    val LEVEL_$id = PuzzleDefinition(\n")
            sb.append("        puzzleId = \"${def.puzzleId}\",\n")
            sb.append("        puzzleVersion = ${def.puzzleVersion},\n")
            sb.append("        gridDimensions = GridDimensions($rows, $cols),\n")
            sb.append("        requiredCells = if ($rows == 6) grid6x6Cells() else grid7x7Cells(),\n")
            sb.append("        checkpoints = listOf(\n")
            for ((idx, cp) in def.checkpoints.withIndex()) {
                val comma = if (idx == def.checkpoints.size - 1) "" else ","
                sb.append("            NumberedCheckpoint(${cp.number}, GridPosition(${cp.position.row}, ${cp.position.col}))$comma\n")
            }
            sb.append("        ),\n")
            sb.append("        blockedEdges = emptySet(),\n")
            sb.append("        difficultyMetadata = \"${def.difficultyMetadata}\",\n")
            sb.append("        seed = ${def.seed}L\n")
            sb.append("    )\n\n")

            sb.append("    val SOLUTION_$id = PuzzlePath(\n")
            sb.append("        listOf(\n")
            val posChunks = sol.positions.chunked(6)
            for ((idx, chunk) in posChunks.withIndex()) {
                val comma = if (idx == posChunks.size - 1) "" else ","
                sb.append("            ${chunk.joinToString(", ") { "GridPosition(${it.row}, ${it.col})" }}$comma\n")
            }
            sb.append("        )\n")
            sb.append("    )\n\n")
        }

        sb.append("    val LEVELS: Map<Int, PuzzleDefinition> by lazy {\n")
        sb.append("        val map = LinkedHashMap<Int, PuzzleDefinition>()\n")
        for (i in 1..50) {
            sb.append("        map[$i] = LEVEL_$i\n")
        }
        sb.append("        map\n")
        sb.append("    }\n\n")

        sb.append("    val SOLUTIONS: Map<Int, PuzzlePath> by lazy {\n")
        sb.append("        val map = LinkedHashMap<Int, PuzzlePath>()\n")
        for (i in 1..50) {
            sb.append("        map[$i] = SOLUTION_$i\n")
        }
        sb.append("        map\n")
        sb.append("    }\n")
        sb.append("}\n")

        val targetFile = File("d:/Zynpath/android/app/src/main/java/com/zynpath/game/core/puzzle/catalog/CuratedFirst50Levels.kt")
        targetFile.writeText(sb.toString(), Charsets.UTF_8)
        println("Written CuratedFirst50Levels.kt (${targetFile.length()} bytes)")
    }

    private fun writeAssets(levels: List<VerifiedLevel>) {
        val baseAssets = File("d:/Zynpath/android/app/src/main/assets/puzzles")
        for (lvl in levels) {
            val worldId = if (lvl.levelId <= 20) 1 else 2
            val worldDir = File(baseAssets, "w$worldId")
            worldDir.mkdirs()
            val file = File(worldDir, "lvl${lvl.levelId}.json")

            val fp = com.zynpath.game.core.puzzle.generator.PuzzleFingerprint.computeSha256(lvl.definition)
            val asset = PuzzleAsset.fromPuzzleDefinition(
                def = lvl.definition,
                metadata = PuzzleAssetMetadata(
                    generatorVersion = "2.0.0",
                    generationSeed = lvl.definition.seed,
                    difficultyEstimate = if (worldId == 1) 0.35 + (lvl.levelId * 0.02) else 0.65 + ((lvl.levelId - 20) * 0.01),
                    difficultyBand = if (worldId == 1) "EASY" else "MEDIUM",
                    uniquenessStatus = "UNIQUE",
                    fingerprint = fp
                )
            )
            file.writeText(PuzzleAssetSerializer.serialize(asset), Charsets.UTF_8)
        }
        println("Written 50 JSON puzzle assets to assets/puzzles/w1 and w2")
    }
}
