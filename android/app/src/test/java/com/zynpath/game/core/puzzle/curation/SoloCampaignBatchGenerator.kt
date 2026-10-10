package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.catalog.LevelDefinition
import com.zynpath.game.core.puzzle.catalog.PuzzleAsset
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetMetadata
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetSerializer
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetValidationResult
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetValidator
import com.zynpath.game.core.puzzle.catalog.WorldDefinition
import com.zynpath.game.core.puzzle.generator.PuzzleFingerprint
import com.zynpath.game.core.puzzle.generator.WallPlacementStrategy
import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import com.zynpath.game.core.puzzle.policy.PuzzleConstraintPolicy
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.SolverConfiguration
import com.zynpath.game.core.puzzle.solver.SolverStatus
import com.zynpath.game.core.puzzle.solver.UniquenessStatus
import java.io.File
import java.util.Random

object SoloCampaignBatchGenerator {

    private val baseAssetsDir = File("d:/Zynpath/android/app/src/main/assets/puzzles")
    private val solver = PuzzleSolver()
    private val wallStrategy = WallPlacementStrategy()

    fun countTurns(path: List<GridPosition>): Int {
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

    fun generateHamiltonianPath(
        rows: Int,
        cols: Int,
        rng: Random,
        steps: Int
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

    fun generateUniqueLevel(
        worldId: Int,
        localLevel: Int
    ): PuzzleAsset {
        val policy = PuzzleConstraintPolicy.forWorld(worldId)
        val rows = policy.gridDimensions.rows
        val cols = policy.gridDimensions.columns
        val total = rows * cols
        val dims = policy.gridDimensions
        val pathCellSet = (0 until rows).flatMap { r -> (0 until cols).map { c -> GridPosition(r, c) } }.toSet()

        val minCps = policy.checkpointRequirements.first
        val maxCps = policy.checkpointRequirements.last
        val maxWalls = policy.allowedWallCountRange.last
        val minWalls = if (worldId == 1) 0 else minOf(1, maxWalls)

        val minTurns = when (worldId) {
            1 -> 12
            2 -> 16
            3 -> 22
            4 -> 28
            5 -> 40
            else -> 48
        }
        val backbiteSteps = when (worldId) {
            1 -> 3_000
            2 -> 4_500
            3 -> 6_500
            4 -> 9_000
            5 -> 9_000
            else -> 11_000
        }

        val baseSeed = (worldId * 100_000L) + (localLevel * 1013L) + (if (worldId == 1 && localLevel == 179) 9999L else 0L)

        val fastConfig = SolverConfiguration(
            maxSolutions = 2,
            nodeLimit = if (total >= 121) 12_000L else 25_000L,
            timeBudgetMs = if (total >= 121) 150L else 300L
        )

        for (attempt in 0 until 400) {
            val rng = Random(baseSeed + attempt * 179L)
            val path = generateHamiltonianPath(rows, cols, rng, backbiteSteps)
            if (countTurns(path) < minTurns) continue

            // Determine initial walls
            val initialWalls = if (maxWalls > 0) {
                val targetInit = when (worldId) {
                    2 -> rng.nextInt(3) + 1
                    3 -> rng.nextInt(3) + 2
                    4 -> rng.nextInt(3) + 3
                    5 -> rng.nextInt(4) + 4
                    else -> rng.nextInt(4) + 5
                }
                wallStrategy.placeWalls(dims, pathCellSet, path, minOf(minWalls, targetInit), minOf(maxWalls, targetInit + 2), rng) ?: emptySet()
            } else {
                emptySet()
            }

            val currentWalls = initialWalls.toMutableSet()
            val candidateIndices = mutableSetOf(0, total - 1)

            val seedCount = when {
                total <= 36 -> 8
                total <= 49 -> 10
                total <= 64 -> 12
                total <= 81 -> 16
                total <= 121 -> 28
                else -> 35
            }
            val step = (total - 1).toDouble() / (seedCount - 1)
            for (k in 1 until seedCount - 1) {
                candidateIndices.add((k * step).toInt())
            }

            var uniqueFound = false
            for (iter in 0 until 30) {
                if (candidateIndices.size > maxCps + 4) break

                val sorted = candidateIndices.sorted()
                val checkpoints = sorted.mapIndexed { idx, pIdx ->
                    NumberedCheckpoint(idx + 1, path[pIdx])
                }

                val candidateDef = PuzzleDefinition(
                    puzzleId = "w${worldId}_lvl$localLevel",
                    puzzleVersion = 1,
                    gridDimensions = dims,
                    requiredCells = pathCellSet,
                    checkpoints = checkpoints,
                    blockedEdges = currentWalls,
                    difficultyMetadata = "World $worldId Level $localLevel",
                    seed = baseSeed + attempt
                )

                val solverRes = solver.solve(candidateDef, fastConfig)
                if (solverRes.status == SolverStatus.SOLVED && solverRes.uniqueness == UniquenessStatus.UNIQUE) {
                    // Prune redundant checkpoints only if we have more than target
                    val pruned = sorted.toMutableList()
                    val targetMax = minOf(maxCps, (minCps + maxCps) / 2 + 3)
                    if (pruned.size > minCps) {
                        val internalCps = pruned.subList(1, pruned.size - 1).shuffled(rng)
                        for (pIdx in internalCps) {
                            if (pruned.size <= targetMax) break
                            val testIndices = pruned.filter { it != pIdx }.sorted()
                            val testCps = testIndices.mapIndexed { idx, pi -> NumberedCheckpoint(idx + 1, path[pi]) }
                            val testDef = candidateDef.copy(checkpoints = testCps)
                            val testRes = solver.solve(testDef, fastConfig)
                            if (testRes.status == SolverStatus.SOLVED && testRes.uniqueness == UniquenessStatus.UNIQUE) {
                                pruned.remove(pIdx)
                            }
                        }
                    }

                    if (pruned.size in minCps..maxCps) {
                        val finalCps = pruned.sorted().mapIndexed { idx, pi -> NumberedCheckpoint(idx + 1, path[pi]) }
                        val finalDef = candidateDef.copy(checkpoints = finalCps)

                        // Full uniqueness verification
                        val fullRes = solver.solve(finalDef, SolverConfiguration.CHECK_UNIQUENESS)
                        if (fullRes.status == SolverStatus.SOLVED && fullRes.uniqueness == UniquenessStatus.UNIQUE) {
                            val policyCheck = policy.validate(finalDef)
                            if (policyCheck.isEmpty()) {
                                val score = when (worldId) {
                                    1 -> 0.10 + (localLevel - 1) * 0.0008
                                    2 -> 0.35 + (localLevel - 1) * 0.0007
                                    3 -> 0.55 + (localLevel - 1) * 0.0005
                                    4 -> 0.70 + (localLevel - 1) * 0.0005
                                    5 -> 0.85 + (localLevel - 1) * 0.0003
                                    else -> 0.93 + (localLevel - 1) * 0.0002
                                }.coerceIn(0.0, 1.0)
                                val band = com.zynpath.game.core.puzzle.curation.DifficultyBand.fromScore(score)
                                val fp = PuzzleFingerprint.computeSha256(finalDef)

                                return PuzzleAsset.fromPuzzleDefinition(
                                    def = finalDef,
                                    metadata = PuzzleAssetMetadata(
                                        generatorVersion = "2.0.0",
                                        generationSeed = finalDef.seed,
                                        difficultyEstimate = score,
                                        difficultyBand = band.name,
                                        uniquenessStatus = "UNIQUE",
                                        fingerprint = fp
                                    )
                                )
                            }
                        }
                    }
                }

                // If multiple solutions exist, find branch and add wall or checkpoint
                if (solverRes.solutions.size >= 2) {
                    val sol1 = solverRes.solutions[0]
                    val sol2 = solverRes.solutions[1]
                    val altSol = if (sol1.positions == path) sol2 else sol1

                    var wallAdded = false
                    if (currentWalls.size < maxWalls) {
                        val protectedEdges = HashSet<BlockedEdge>(path.size)
                        for (i in 0 until path.size - 1) {
                            protectedEdges.add(BlockedEdge.between(path[i], path[i + 1]))
                        }
                        for (i in 0 until altSol.positions.size - 1) {
                            val edge = BlockedEdge.between(altSol.positions[i], altSol.positions[i + 1])
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
                        // Find first position where altSol differs from path
                        for (i in 0 until path.size) {
                            if (i >= altSol.positions.size || path[i] != altSol.positions[i]) {
                                candidateIndices.add(i)
                                val nextQuarter = minOf(path.size - 1, i + 3)
                                candidateIndices.add(nextQuarter)
                                break
                            }
                        }
                    }
                } else {
                    // Gap halving
                    val gaps = sorted.zipWithNext().map { (a, b) -> (b - a) to ((a + b) / 2) }
                    val largestGap = gaps.maxByOrNull { it.first }
                    if (largestGap != null && largestGap.first > 2) {
                        candidateIndices.add(largestGap.second)
                    } else {
                        break
                    }
                }
            }
        }

        error("Failed to generate level W$worldId Lvl$localLevel within attempt budget")
    }

    fun isLevelFileValid(worldId: Int, localLevel: Int): Boolean {
        val file = File(baseAssetsDir, "w$worldId/lvl$localLevel.json")
        if (!file.exists()) return false
        val json = file.readText(Charsets.UTF_8)
        val globalLevel = WorldConfiguration.toGlobalLevel(worldId, localLevel)
        val worldDef = WorldDefinition.forWorld(worldId)
        val expectedLevel = LevelDefinition(
            levelId = globalLevel,
            worldId = worldId,
            puzzleId = "w${worldId}_lvl$localLevel",
            assetPath = "puzzles/w$worldId/lvl$localLevel.json",
            difficultyEstimate = 0.5,
            difficultyBand = com.zynpath.game.core.puzzle.curation.DifficultyBand.MEDIUM,
            fingerprint = "",
            hasPackagedAsset = true,
            displayName = "Level $localLevel"
        )
        val res = PuzzleAssetValidator.validate(json, expectedLevel, worldDef)
        return res is PuzzleAssetValidationResult.Valid
    }

    fun generateWorldBatch(
        worldId: Int,
        startLevel: Int,
        endLevel: Int,
        threads: Int = Runtime.getRuntime().availableProcessors().coerceIn(1, 8)
    ): Int {
        val worldDir = File(baseAssetsDir, "w$worldId")
        worldDir.mkdirs()

        val pendingLevels = (startLevel..endLevel).filter { !isLevelFileValid(worldId, it) }
        if (pendingLevels.isEmpty()) {
            println("All levels in World $worldId [$startLevel..$endLevel] are already valid.")
            return 0
        }

        println("Generating ${pendingLevels.size} levels for World $worldId using $threads worker threads...")
        val generatedCount = java.util.concurrent.atomic.AtomicInteger(0)
        val executor = java.util.concurrent.Executors.newFixedThreadPool(threads)

        val futures = pendingLevels.map { lvl ->
            executor.submit {
                val t0 = System.currentTimeMillis()
                val asset = generateUniqueLevel(worldId, lvl)
                val json = PuzzleAssetSerializer.serialize(asset)
                val file = File(worldDir, "lvl$lvl.json")
                file.writeText(json, Charsets.UTF_8)
                val done = generatedCount.incrementAndGet()
                val elapsed = System.currentTimeMillis() - t0
                println("[$done/${pendingLevels.size}] W$worldId Lvl$lvl generated in ${elapsed}ms (CPs=${asset.checkpoints.size}, walls=${asset.blockedEdges.size})")
            }
        }

        for (f in futures) {
            f.get()
        }
        executor.shutdown()
        return generatedCount.get()
    }
}
