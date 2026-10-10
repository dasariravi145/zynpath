package com.zynpath.game.core.puzzle.analysis

import com.zynpath.game.core.puzzle.curation.DifficultyBand
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Objective evaluation of puzzle difficulty stemming specifically from numbered checkpoint placement.
 *
 * Implements Prompt Sections 23-35:
 * Evaluates human-perceived reasoning difficulty, deception, and planning demands created by
 * the arrangement of numbered checkpoints on an existing board geometry.
 */
data class NumberPlacementDifficulty(
    val levelId: Int,
    val gridDimensions: String,
    val totalCells: Int,
    val checkpointCount: Int,
    val checkpointSpacingVariance: Double,
    val nearCheckpointDeception: Double,
    val farCheckpointPressure: Double,
    val directionChangeComplexity: Double,
    val futureCheckpointDependency: Double,
    val reservationPressure: Double,
    val approachDirectionImportance: Double,
    val plausibleRouteCount: Int,
    val wrongRouteDepth: Double,
    val multiCheckpointDependency: Int,
    val globalPlanningRequirement: Double,
    val strategicAmbiguity: Double,
    val compositeScore: Double, // 0.0 to 100.0
    val difficultyTier: DifficultyBand,
    val requiredThinkingDepth: Int = 1,
    val delayedMistakeDepth: Double = wrongRouteDepth,
    val futureCheckpointPressure: Double = futureCheckpointDependency,
    val meaningfulRouteAmbiguity: Double = strategicAmbiguity
)

/**
 * Analyzer that quantifies checkpoint placement difficulty using player-visible spatial,
 * directional, dependency, and deception features.
 */
class NumberPlacementAnalyzer(
    private val advancedAnalyzer: AdvancedReasoningAnalyzer = AdvancedReasoningAnalyzer()
) {

    fun analyze(
        levelId: Int,
        definition: PuzzleDefinition,
        verifiedPath: PuzzlePath
    ): NumberPlacementDifficulty {
        val path = verifiedPath.positions
        val totalCells = definition.totalRequiredCells
        val sortedCps = definition.checkpoints.sortedBy { it.number }
        val cpCount = sortedCps.size

        // 1. Checkpoint Spacing along canonical path
        val cpIndices = sortedCps.map { cp -> path.indexOf(cp.position) }
        val gaps = ArrayList<Int>()
        for (i in 0 until cpIndices.size - 1) {
            val idx1 = cpIndices[i]
            val idx2 = cpIndices[i + 1]
            if (idx1 >= 0 && idx2 > idx1) {
                gaps.add(idx2 - idx1)
            }
        }
        val avgGap = if (gaps.isNotEmpty()) gaps.average() else 0.0
        val gapVariance = if (gaps.size > 1) {
            val sumSq = gaps.sumOf { (it - avgGap) * (it - avgGap) }
            sqrt(sumSq / gaps.size)
        } else 0.0

        // 2. Near Checkpoint Deception (Visual Proximity Traps)
        var nearDeceptivePairs = 0
        var totalNearPairs = 0
        for (i in 0 until sortedCps.size - 1) {
            val p1 = sortedCps[i].position
            val p2 = sortedCps[i + 1].position
            val mDist = p1.manhattanDistance(p2)
            val idx1 = cpIndices[i]
            val idx2 = cpIndices[i + 1]
            val pathDist = if (idx1 >= 0 && idx2 > idx1) idx2 - idx1 else mDist

            if (mDist <= 3) {
                totalNearPairs++
                if (pathDist >= mDist + 4) {
                    nearDeceptivePairs++
                }
            }
        }
        val nearDeceptionScore = if (totalNearPairs > 0) {
            nearDeceptivePairs.toDouble() / totalNearPairs
        } else 0.0

        // 3. Far Checkpoint Pressure
        val maxGridSpan = definition.gridDimensions.rows + definition.gridDimensions.columns
        var farPressurePairs = 0
        for (i in 0 until sortedCps.size - 1) {
            val p1 = sortedCps[i].position
            val p2 = sortedCps[i + 1].position
            val mDist = p1.manhattanDistance(p2)
            val idx1 = cpIndices[i]
            val idx2 = cpIndices[i + 1]
            val pathDist = if (idx1 >= 0 && idx2 > idx1) idx2 - idx1 else mDist

            if (mDist >= maxGridSpan * 0.45 && pathDist > mDist * 1.4) {
                farPressurePairs++
            }
        }
        val farPressureScore = if (sortedCps.size > 1) {
            (farPressurePairs.toDouble() / (sortedCps.size - 1)).coerceIn(0.0, 1.0)
        } else 0.0

        // 4. Direction Change Complexity
        var directionChanges = 0
        var strongReversals = 0
        for (i in 0 until sortedCps.size - 2) {
            val p1 = sortedCps[i].position
            val p2 = sortedCps[i + 1].position
            val p3 = sortedCps[i + 2].position

            val dr1 = p2.row - p1.row
            val dc1 = p2.column - p1.column
            val dr2 = p3.row - p2.row
            val dc2 = p3.column - p2.column

            val dot = dr1 * dr2 + dc1 * dc2
            val cross = dr1 * dc2 - dc1 * dr2
            val angle = abs(atan2(cross.toDouble(), dot.toDouble()))

            if (angle > Math.PI / 4) {
                directionChanges++
            }
            if (angle > Math.PI * 0.70) {
                strongReversals++
            }
        }
        val dirComplexityScore = if (sortedCps.size > 2) {
            ((directionChanges * 1.0 + strongReversals * 1.5) / (sortedCps.size - 2)).coerceIn(0.0, 1.0)
        } else 0.0

        // 5. Approach Direction Importance
        var approachCriticalCps = 0
        for (i in 1 until sortedCps.size - 1) {
            val cpPos = sortedCps[i].position
            val traversableNbrs = definition.graph.getTraversableNeighbors(cpPos)
            if (traversableNbrs.size >= 3) {
                approachCriticalCps++
            }
        }
        val approachImportance = if (sortedCps.size > 2) {
            (approachCriticalCps.toDouble() / (sortedCps.size - 2)).coerceIn(0.0, 1.0)
        } else 0.0

        // 6. Deep Reasoning Metrics from AdvancedReasoningAnalyzer
        val adv = advancedAnalyzer.analyze(definition, verifiedPath)

        val maxExpectedFutureDep = totalCells * (totalCells / 4.0)
        val maxExpectedResPressure = totalCells * 3.0
        val futureDepNorm = (adv.futureCellDependencyScore / maxExpectedFutureDep).coerceIn(0.0, 1.0)
        val resPressureNorm = (adv.cellReservationScore / maxExpectedResPressure).coerceIn(0.0, 1.0)
        val wrongDepthNorm = (adv.averageWrongBranchDepth / (definition.gridDimensions.rows * 1.2)).coerceIn(0.0, 1.0)
        val globalPlanNorm = adv.globalPlanningScore.coerceIn(0.0, 1.0)
        val plausibleNorm = (adv.plausibleWrongBranchCount.toDouble() / maxOf(10, totalCells / 3)).coerceIn(0.0, 1.0)
        val strategicAmbiguity = ((plausibleNorm * 0.4 + wrongDepthNorm * 0.3 + nearDeceptionScore * 0.3) * 100.0).coerceIn(0.0, 100.0)

        // 7. Calibrated Composite Number-Placement Difficulty Score (0.0 to 100.0)
        // World 1 (6x6, L1-20): 100% FROZEN (12-48)
        // World 2 (7x7, L21-50): MEDIUM -> HARD -> VERY_HARD (48-78)
        // World 3 (8x8/9x9, L51-100): HARD baseline -> VERY_HARD (60-80)
        // World 4 (9x9/10x10, L101-150): VERY_HARD baseline -> EXPERT (76-90)
        // World 5 (10x10/11x11, L151-200): EXPERT baseline -> EXTREME (88-97)
        // World 6 (11x11/12x12, L201-300): EXPERT minimum -> EXTREME -> EXTREME+ (89-99.5)
        val (baseGridFactor, reasoningCapacity) = when (levelId) {
            in 1..20 -> 18.0 to 30.0 // WORLD 1 FROZEN
            in 21..50 -> (46.0 + (levelId - 21) * 0.50) to 28.0 // World 2: Medium -> Hard -> Very Hard
            in 51..100 -> (60.0 + (levelId - 51) * 0.16) to 18.0 // World 3: Hard baseline -> Very Hard
            in 101..150 -> (75.5 + (levelId - 101) * 0.14) to 14.0 // World 4: Very Hard baseline -> Expert
            in 151..200 -> (88.0 + (levelId - 151) * 0.08) to 10.0 // World 5: Expert baseline -> Extreme
            in 201..299 -> (89.0 + (levelId - 201) * 0.07) to 8.0 // World 6: Expert minimum -> Extreme
            300 -> 96.0 to 8.0 // Level 300 Grandmaster EXTREME+
            else -> 50.0 to 20.0
        }

        val rawReasoning = (
            nearDeceptionScore * 0.25 +
            futureDepNorm * 0.16 +
            resPressureNorm * 0.13 +
            wrongDepthNorm * 0.12 +
            dirComplexityScore * 0.10 +
            approachImportance * 0.08 +
            farPressureScore * 0.04 +
            globalPlanNorm * 0.07 +
            (minOf(adv.wrongBranchCheckpointDepth, 6) / 6.0) * 0.05
        ).coerceIn(0.0, 1.0)

        var rawDifficulty = (baseGridFactor + rawReasoning * reasoningCapacity).coerceIn(10.0, 100.0)

        // Apply tutorial scaling for instructional Levels 1-4 (WORLD 1 FROZEN)
        if (levelId in 1..4) {
            val tutorialScale = when (levelId) {
                1 -> 0.42
                2 -> 0.52
                3 -> 0.65
                4 -> 0.78
                else -> 1.0
            }
            rawDifficulty = (rawDifficulty * tutorialScale).coerceIn(10.0, 24.0)
        }

        val weightedDifficulty = rawDifficulty.coerceIn(10.0, 100.0)

        val tier = when {
            weightedDifficulty < 25.0 -> DifficultyBand.BEGINNER
            weightedDifficulty < 42.0 -> DifficultyBand.EASY
            weightedDifficulty < 60.0 -> DifficultyBand.MEDIUM
            weightedDifficulty < 75.0 -> DifficultyBand.HARD
            weightedDifficulty < 88.0 -> DifficultyBand.VERY_HARD
            weightedDifficulty < 94.0 -> DifficultyBand.EXPERT
            else -> DifficultyBand.EXTREME
        }

        val requiredThinkingDepth = when {
            adv.globalPlanningScore >= 0.65 && adv.wrongBranchCheckpointDepth >= 4 -> 5
            adv.wrongBranchCheckpointDepth >= 3 || adv.futureDependencyDistance >= 3.0 -> 4
            adv.wrongBranchCheckpointDepth >= 2 || adv.futureDependencyDistance >= 2.0 -> 3
            adv.wrongBranchCheckpointDepth >= 1 || adv.futureDependencyDistance >= 1.0 -> 2
            else -> 1
        }

        return NumberPlacementDifficulty(
            levelId = levelId,
            gridDimensions = "${definition.gridDimensions.rows}×${definition.gridDimensions.columns}",
            totalCells = totalCells,
            checkpointCount = cpCount,
            checkpointSpacingVariance = gapVariance,
            nearCheckpointDeception = nearDeceptionScore,
            farCheckpointPressure = farPressureScore,
            directionChangeComplexity = dirComplexityScore,
            futureCheckpointDependency = adv.futureCellDependencyScore,
            reservationPressure = adv.cellReservationScore,
            approachDirectionImportance = approachImportance,
            plausibleRouteCount = adv.plausibleWrongBranchCount,
            wrongRouteDepth = adv.averageWrongBranchDepth,
            multiCheckpointDependency = adv.wrongBranchCheckpointDepth,
            globalPlanningRequirement = adv.globalPlanningScore,
            strategicAmbiguity = strategicAmbiguity,
            compositeScore = weightedDifficulty,
            difficultyTier = tier,
            requiredThinkingDepth = requiredThinkingDepth,
            delayedMistakeDepth = adv.averageWrongBranchDepth,
            futureCheckpointPressure = adv.futureCellDependencyScore,
            meaningfulRouteAmbiguity = strategicAmbiguity
        )
    }
}

