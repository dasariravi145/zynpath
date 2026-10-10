package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.analysis.NumberPlacementAnalyzer
import com.zynpath.game.core.puzzle.analysis.NumberPlacementDifficulty
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.SolverConfiguration
import com.zynpath.game.core.puzzle.solver.SolverStatus
import com.zynpath.game.core.puzzle.solver.UniquenessStatus
import java.util.Random
import kotlin.math.abs

/**
 * Result of evaluating or tuning a level's number placement.
 */
data class NumberPlacementResult(
    val levelId: Int,
    val status: NumberPlacementStatus,
    val definition: PuzzleDefinition,
    val solution: PuzzlePath,
    val difficulty: NumberPlacementDifficulty,
    val targetDifficulty: Double
)

enum class NumberPlacementStatus {
    KEEP,
    TUNED,
    REPLACED
}

/**
 * Optimizer that searches for and tunes checkpoint placements to fit the Zynpath
 * 300-level Candy-Crush-style difficulty curve.
 *
 * Implements Prompt Sections 1–56:
 * - Preserves board geometry (rows, columns, cells, walls).
 * - Preserves canonical path.
 * - Modifies ONLY checkpoint count and positions along the canonical path.
 * - Guarantees player-visible solver uniqueness proof.
 */
class NumberPlacementOptimizer(
    private val solver: PuzzleSolver = PuzzleSolver(),
    private val analyzer: NumberPlacementAnalyzer = NumberPlacementAnalyzer()
) {

    /**
     * Target difficulty curve across Levels 1–300.
     * Incorporates Candy-Crush-style wave rhythm:
     * BUILD -> BUILD -> BUILD -> CHALLENGE -> RELIEF -> BUILD...
     * With milestone peaks around every ~5, ~10, and world-ending levels.
     */
    fun targetDifficulty(levelId: Int): Double {
        if (levelId == 1) return 12.0
        if (levelId == 2) return 15.0
        if (levelId == 3) return 18.0
        if (levelId == 4) return 22.0

        // World-end milestones (strongest in each world)
        if (levelId == 20) return 48.0 // WORLD 1 FROZEN
        if (levelId == 50) return 76.0 // World 2 end: VERY_HARD challenge level
        if (levelId == 100) return 82.0 // World 3 end: VERY_HARD
        if (levelId == 150) return 90.0 // World 4 end: EXPERT
        if (levelId == 200) return 96.5 // World 5 end: EXTREME
        if (levelId == 300) return 99.5 // Level 300 Grandmaster

        // Base trend line by world
        val baseTrend = when (levelId) {
            in 5..19 -> 23.0 + (levelId - 5) * 1.5 // WORLD 1 FROZEN
            in 21..49 -> 48.0 + (levelId - 21) * 0.85 // World 2: Medium -> Hard -> Very Hard
            in 51..99 -> 63.0 + (levelId - 51) * 0.35 // World 3: Hard baseline -> Very Hard
            in 101..149 -> 78.0 + (levelId - 101) * 0.25 // World 4: Very Hard baseline -> Expert
            in 151..199 -> 89.0 + (levelId - 151) * 0.15 // World 5: Expert baseline -> Extreme
            in 201..299 -> 91.0 + (levelId - 201) * 0.08 // World 6: Expert minimum -> Extreme
            else -> 90.0
        }

        // Wave modulation:
        // L % 5 == 0 -> Challenge peak
        // L % 5 == 1 -> Controlled relief (except world start)
        // L % 5 == 2 -> Build
        // L % 5 == 3 -> Build
        // L % 5 == 4 -> Pre-challenge
        val waveOffset = when (levelId % 5) {
            0 -> 3.5  // Challenge peak
            1 -> -2.5 // Controlled relief
            2 -> 0.0  // Build
            3 -> 1.5  // Build
            4 -> 2.5  // Pre-challenge
            else -> 0.0
        }

        // Additional boost for 10-level milestones (e.g. 10, 30, 40, 60, 70, 80, 90...)
        val milestoneBoost = if (levelId % 10 == 0) 1.5 else 0.0

        return (baseTrend + waveOffset + milestoneBoost).coerceIn(10.0, 99.5)
    }

    /**
     * Target checkpoint count range by grid dimensions.
     */
    fun allowedCheckpointRange(rows: Int, cols: Int): IntRange {
        return when (maxOf(rows, cols)) {
            6 -> 6..10
            7 -> 8..12
            8 -> 10..15
            9 -> 12..18
            10 -> 14..20
            11 -> 16..24
            12 -> 18..30
            else -> 8..15
        }
    }

    /**
     * Audits an existing level definition and determines if it should be KEEP, TUNE, or REPLACE.
     */
    fun evaluateLevel(
        levelId: Int,
        definition: PuzzleDefinition,
        solution: PuzzlePath
    ): NumberPlacementResult {
        val currentDiff = analyzer.analyze(levelId, definition, solution)
        val target = targetDifficulty(levelId)
        val diffDelta = abs(currentDiff.compositeScore - target)
        val allowedCps = allowedCheckpointRange(definition.gridDimensions.rows, definition.gridDimensions.columns)

        val cpCountValid = currentDiff.checkpointCount in allowedCps

        val solverBudget = when {
            definition.totalRequiredCells <= 49 -> 150L
            definition.totalRequiredCells <= 81 -> 300L
            else -> 600L
        }
        val solverRes = solver.solve(definition, SolverConfiguration(maxSolutions = 2, nodeLimit = 150_000L, timeBudgetMs = solverBudget))
        val isUnique = solverRes.status == SolverStatus.SOLVED && solverRes.uniqueness == UniquenessStatus.UNIQUE

        val stageSatisfied = when (levelId) {
            in 1..4 -> currentDiff.nearCheckpointDeception <= 0.05 && currentDiff.compositeScore <= 25.0
            in 5..10 -> currentDiff.compositeScore in 20.0..36.0
            in 11..20 -> currentDiff.compositeScore in 30.0..52.0
            in 21..35 -> currentDiff.compositeScore in 45.0..68.0
            in 36..50 -> currentDiff.compositeScore in 58.0..82.0
            300 -> currentDiff.compositeScore >= 98.0
            else -> true
        }

        val status = if (isUnique && cpCountValid && diffDelta <= 4.0 && stageSatisfied) {
            NumberPlacementStatus.KEEP
        } else if (isUnique && cpCountValid && diffDelta <= 8.5) {
            NumberPlacementStatus.TUNED
        } else {
            NumberPlacementStatus.REPLACED
        }

        return NumberPlacementResult(
            levelId = levelId,
            status = status,
            definition = definition,
            solution = solution,
            difficulty = currentDiff,
            targetDifficulty = target
        )
    }

    /**
     * Optimizes checkpoint placement on the given board geometry and canonical solution.
     * Returns an updated PuzzleDefinition if tuned/replaced, or the original if kept.
     */
    fun optimizePlacement(
        levelId: Int,
        existingDef: PuzzleDefinition,
        solution: PuzzlePath,
        randomSeed: Long = 1000L + levelId
    ): NumberPlacementResult {
        val existingResult = evaluateLevel(levelId, existingDef, solution)
        if (existingResult.status == NumberPlacementStatus.KEEP) {
            return existingResult
        }

        val target = targetDifficulty(levelId)
        val rows = existingDef.gridDimensions.rows
        val cols = existingDef.gridDimensions.columns
        val totalCells = existingDef.totalRequiredCells
        val path = solution.positions
        val allowedCps = allowedCheckpointRange(rows, cols)

        // Existing indices along the path
        val existingIndices = existingDef.checkpoints.map { path.indexOf(it.position) }.filter { it >= 0 }.sorted()

        // Find candidate turning points along the path
        val turns = ArrayList<Int>()
        for (i in 1 until path.size - 1) {
            val dr1 = path[i].row - path[i - 1].row
            val dc1 = path[i].column - path[i - 1].column
            val dr2 = path[i + 1].row - path[i].row
            val dc2 = path[i + 1].column - path[i].column
            if (dr1 != dr2 || dc1 != dc2) turns.add(i)
        }

        // Find near-deception pairs: (i, j) where manhattan distance <= 2, but path distance >= 6
        val nearPairs = ArrayList<Pair<Int, Int>>()
        for (i in 0 until path.size - 6) {
            for (j in (i + 6) until minOf(path.size, i + 35)) {
                val mDist = path[i].manhattanDistance(path[j])
                if (mDist <= 2 && (j - i) >= mDist + 4) {
                    nearPairs.add(i to j)
                }
            }
        }

        val rng = Random(randomSeed)
        val solverBudget = when {
            totalCells <= 49 -> 150L
            totalCells <= 81 -> 300L
            else -> 600L
        }
        val fastSolverConfig = SolverConfiguration(maxSolutions = 2, nodeLimit = 150_000L, timeBudgetMs = solverBudget)

        val isExistingValid = existingDef.checkpoints.size in allowedCps &&
            solver.solve(existingDef, fastSolverConfig).let { it.status == SolverStatus.SOLVED && it.uniqueness == UniquenessStatus.UNIQUE }

        var bestDef = existingDef
        var bestDiff = existingResult.difficulty
        var bestScoreDiff = if (isExistingValid) abs(existingResult.difficulty.compositeScore - target) else Double.MAX_VALUE

        fun testCandidateIndices(indices: List<Int>): Boolean {
            if (indices.first() != 0 || indices.last() != totalCells - 1) return false
            if (indices.size !in allowedCps) return false
            // Strictly ascending
            for (k in 0 until indices.size - 1) {
                if (indices[k + 1] <= indices[k]) return false
            }

            val candidateCps = indices.mapIndexed { idx, pIdx ->
                NumberedCheckpoint(idx + 1, path[pIdx])
            }

            val candidateDef = PuzzleDefinition(
                puzzleId = existingDef.puzzleId,
                puzzleVersion = existingDef.puzzleVersion + 1,
                gridDimensions = existingDef.gridDimensions,
                requiredCells = existingDef.requiredCells,
                checkpoints = candidateCps,
                blockedEdges = existingDef.blockedEdges,
                difficultyMetadata = existingDef.difficultyMetadata,
                seed = existingDef.seed
            )

            val solverRes = solver.solve(candidateDef, fastSolverConfig)
            if (solverRes.status == SolverStatus.SOLVED && solverRes.uniqueness == UniquenessStatus.UNIQUE) {
                val diff = analyzer.analyze(levelId, candidateDef, solution)
                val scoreDiff = abs(diff.compositeScore - target)

                val stageOk = when (levelId) {
                    in 1..4 -> diff.nearCheckpointDeception <= 0.05 && diff.compositeScore <= 25.0
                    in 5..10 -> diff.compositeScore in 20.0..38.0
                    in 11..20 -> diff.compositeScore in 28.0..52.0
                    300 -> diff.compositeScore >= 95.0
                    else -> true
                }

                if (stageOk && scoreDiff < bestScoreDiff) {
                    bestDef = candidateDef
                    bestDiff = diff
                    bestScoreDiff = scoreDiff
                    return scoreDiff <= 1.0 // Super close
                }
            }
            return false
        }

        // SPECIAL CASE: Instructional Levels 1..4 - Eliminate all proximity deception
        if (levelId in 1..4 && existingIndices.isNotEmpty()) {
            val nonDeceptive = existingIndices.toMutableList()
            var modified = true
            var passes = 0
            while (modified && passes < 10) {
                modified = false
                passes++
                for (i in 0 until nonDeceptive.size - 1) {
                    val p1 = path[nonDeceptive[i]]
                    val p2 = path[nonDeceptive[i + 1]]
                    val mDist = p1.manhattanDistance(p2)
                    val pDist = nonDeceptive[i + 1] - nonDeceptive[i]
                    if (mDist <= 3 && pDist >= mDist + 4) {
                        val mid = (nonDeceptive[i] + nonDeceptive[i + 1]) / 2
                        if (mid !in nonDeceptive && mid > nonDeceptive[i] && mid < nonDeceptive[i + 1]) {
                            nonDeceptive.add(mid)
                            nonDeceptive.sort()
                            modified = true
                            break
                        }
                    }
                }
            }
            while (nonDeceptive.size > allowedCps.last) {
                var minGap = 999
                var removeIdx = -1
                for (i in 1 until nonDeceptive.size - 1) {
                    val gap = nonDeceptive[i] - nonDeceptive[i - 1]
                    if (gap < minGap && gap <= 2) {
                        minGap = gap
                        removeIdx = i
                    }
                }
                if (removeIdx != -1) {
                    nonDeceptive.removeAt(removeIdx)
                } else break
            }
            testCandidateIndices(nonDeceptive)
        }

        var baseIndices = existingIndices.toMutableList()
        if (baseIndices.isNotEmpty() && baseIndices.first() == 0 && baseIndices.last() == totalCells - 1) {
            // If base has more checkpoints than allowed, greedily drop intermediate ones while keeping uniqueness
            while (baseIndices.size > allowedCps.last) {
                var reduced: List<Int>? = null
                for (i in 1 until baseIndices.size - 1) {
                    val candidate = baseIndices.filterIndexed { idx, _ -> idx != i }
                    val candidateCps = candidate.mapIndexed { idx, pIdx -> NumberedCheckpoint(idx + 1, path[pIdx]) }
                    val candidateDef = PuzzleDefinition(
                        puzzleId = existingDef.puzzleId,
                        puzzleVersion = existingDef.puzzleVersion + 1,
                        gridDimensions = existingDef.gridDimensions,
                        requiredCells = existingDef.requiredCells,
                        checkpoints = candidateCps,
                        blockedEdges = existingDef.blockedEdges,
                        difficultyMetadata = existingDef.difficultyMetadata,
                        seed = existingDef.seed
                    )
                    val sRes = solver.solve(candidateDef, fastSolverConfig)
                    if (sRes.status == SolverStatus.SOLVED && sRes.uniqueness == UniquenessStatus.UNIQUE) {
                        reduced = candidate
                        break
                    }
                }
                if (reduced != null) {
                    baseIndices = reduced.toMutableList()
                    if (baseIndices.size in allowedCps) {
                        testCandidateIndices(baseIndices)
                    }
                } else {
                    break
                }
            }

            // STRATEGY 1: Perturbation of existing known-unique checkpoints
            if (baseIndices.size in allowedCps) {
                // A. Shift individual checkpoints
                for (idxToShift in 1 until baseIndices.size - 1) {
                    val prev = baseIndices[idxToShift - 1]
                    val next = baseIndices[idxToShift + 1]
                    val current = baseIndices[idxToShift]

                    for (delta in listOf(-3, -2, -1, 1, 2, 3)) {
                        val candidatePos = current + delta
                        if (candidatePos > prev + 1 && candidatePos < next - 1) {
                            val newIndices = baseIndices.toMutableList()
                            newIndices[idxToShift] = candidatePos
                            if (testCandidateIndices(newIndices)) break
                        }
                    }
                    if (bestScoreDiff <= 1.0) break
                }

                // B. Replace an interval with near-deception pair
                if (bestScoreDiff > 1.5 && levelId > 4) {
                    for (pair in nearPairs.shuffled(rng).take(20)) {
                        val filtered = baseIndices.filter { it < pair.first - 1 || it > pair.second + 1 }
                        val newIndices = (filtered + listOf(pair.first, pair.second)).distinct().sorted()
                        if (newIndices.size in allowedCps) {
                            if (testCandidateIndices(newIndices)) break
                        }
                    }
                }

                // C. Insert/drop checkpoint if count allows
                if (bestScoreDiff > 1.5) {
                    if (baseIndices.size < allowedCps.last) {
                        // Try inserting at turns
                        for (t in turns) {
                            if (t !in baseIndices) {
                                val newIndices = (baseIndices + t).sorted()
                                if (testCandidateIndices(newIndices)) break
                            }
                        }
                    }
                    if (baseIndices.size > allowedCps.first) {
                        // Try dropping an intermediate checkpoint
                        for (i in 1 until baseIndices.size - 1) {
                            val newIndices = baseIndices.filterIndexed { index, _ -> index != i }
                            if (testCandidateIndices(newIndices)) break
                        }
                    }
                }
            }
        }

        // STRATEGY 2: Bounded Synthesis Search (if tuning didn't hit close to target)
        if (bestScoreDiff > 3.0 && levelId > 4) {
            val targetCpCount = when {
                levelId == 300 -> allowedCps.last
                target >= 85.0 -> allowedCps.last
                target >= 65.0 -> ((allowedCps.first + allowedCps.last) / 2 + 1).coerceAtMost(allowedCps.last)
                else -> ((allowedCps.first + allowedCps.last) / 2)
            }

            for (attempt in 0 until 25) {
                if (bestScoreDiff <= 1.0) break

                val selected = mutableSetOf(0, totalCells - 1)
                val numDecep = when {
                    levelId <= 10 -> 1
                    levelId <= 20 -> 2
                    levelId <= 60 -> 2
                    levelId <= 150 -> 3
                    else -> 4
                }
                var injected = 0
                for (p in nearPairs.shuffled(rng)) {
                    if (injected >= numDecep) break
                    val tooClose = selected.any { abs(it - p.first) < 2 || abs(it - p.second) < 2 }
                    if (!tooClose && selected.size + 2 <= targetCpCount) {
                        selected.add(p.first)
                        selected.add(p.second)
                        injected++
                    }
                }

                // Fill gaps
                while (selected.size < targetCpCount) {
                    val cur = selected.sorted()
                    var maxGap = 0
                    var gapA = 0
                    var gapB = 0
                    for (i in 0 until cur.size - 1) {
                        val g = cur[i + 1] - cur[i]
                        if (g > maxGap) {
                            maxGap = g
                            gapA = cur[i]
                            gapB = cur[i + 1]
                        }
                    }
                    if (maxGap < 3) break
                    val turnCandidates = turns.filter { it in (gapA + 2)..(gapB - 2) }
                    val choice = if (turnCandidates.isNotEmpty() && rng.nextBoolean()) {
                        turnCandidates[rng.nextInt(turnCandidates.size)]
                    } else {
                        (gapA + gapB) / 2
                    }
                    selected.add(choice)
                }

                testCandidateIndices(selected.sorted())
            }
        }

        val status = if (bestDef === existingDef) {
            existingResult.status
        } else if (abs(bestDiff.compositeScore - existingResult.difficulty.compositeScore) < 6.0 && bestDef.checkpoints.size == existingDef.checkpoints.size) {
            NumberPlacementStatus.TUNED
        } else {
            NumberPlacementStatus.REPLACED
        }

        return NumberPlacementResult(
            levelId = levelId,
            status = status,
            definition = bestDef,
            solution = solution,
            difficulty = bestDiff,
            targetDifficulty = target
        )
    }
}
