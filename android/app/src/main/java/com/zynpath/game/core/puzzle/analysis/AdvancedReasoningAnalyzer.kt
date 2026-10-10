package com.zynpath.game.core.puzzle.analysis

import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath

/**
 * Result data class produced by [AdvancedReasoningAnalyzer].
 *
 * Implements Master Redesign Prompt 2 Sections 8–27.
 */
data class AdvancedReasoningAnalysisResult(
    val meaningfulDecisionPointCount: Int,
    val decisionDepth: Int,
    val plausibleWrongBranchCount: Int,
    val averageWrongBranchDepth: Double,
    val maximumWrongBranchDepth: Int,
    val wrongBranchCheckpointDepth: Int,
    val futureCellDependencyScore: Double,
    val futureDependencyDistance: Double,
    val cellReservationScore: Double,
    val checkpointDeceptionScore: Double,
    val regionPlanningScore: Double,
    val criticalConnectorCount: Int,
    val regionDependencyCount: Int,
    val solverDeadEnds: Long,
    val decisionChainDepth: Int = decisionDepth,
    val wrongBranchImpact: Double = 0.0,
    val multiDependencyDecisionCount: Int = 0,
    val dependencyChainDepth: Int = 0,
    val reservationDuration: Double = 0.0,
    val reservationImportance: Double = 0.0,
    val globalPlanningScore: Double = 0.0,
    val selfCutRisk: Double = 0.0
)

/**
 * Advanced structural, topological, look-ahead, region, and multi-checkpoint reasoning analyzer.
 *
 * Implements Prompt 2 Sections 8–27:
 * - Checkpoint Interval Model ($I_1, I_2, \dots$) (Section 8)
 * - Multi-stage Future-Cell Dependency & Dependency Distance (Sections 9 & 10)
 * - Cell & Corridor Reservation Analysis (Sections 11 & 26)
 * - Critical Connector / Articulation Points Analysis (Section 12)
 * - Region Entry / Exit Planning & Premature Sealing Detection (Sections 13 & 25)
 * - Plausible Wrong Branch Depth & Wrong Branch Checkpoint Depth (Sections 15–17)
 * - Advanced Checkpoint Deception Analysis (Section 18)
 * - Meaningful Decision Points & Chained Decision Depth (Sections 21 & 22)
 * - Dead-End Classification & Solver Dead Ends (Section 24)
 */
class AdvancedReasoningAnalyzer {

    fun analyze(
        definition: PuzzleDefinition,
        verifiedPath: PuzzlePath
    ): AdvancedReasoningAnalysisResult {
        val pathPositions = verifiedPath.positions
        val pathLength = pathPositions.size
        val totalCells = definition.totalRequiredCells
        val sortedCheckpoints = definition.checkpoints.sortedBy { it.number }
        val finalCheckpoint = sortedCheckpoints.maxByOrNull { it.number }?.position
        val checkpointsByPos = definition.checkpoints.associateBy { it.position }

        if (pathLength < 2) {
            return AdvancedReasoningAnalysisResult(
                meaningfulDecisionPointCount = 0,
                decisionDepth = 1,
                plausibleWrongBranchCount = 0,
                averageWrongBranchDepth = 0.0,
                maximumWrongBranchDepth = 0,
                wrongBranchCheckpointDepth = 0,
                futureCellDependencyScore = 0.0,
                futureDependencyDistance = 0.0,
                cellReservationScore = 0.0,
                checkpointDeceptionScore = 0.0,
                regionPlanningScore = 0.0,
                criticalConnectorCount = 0,
                regionDependencyCount = 0,
                solverDeadEnds = 0L
            )
        }

        // 1. Build Checkpoint Interval Map
        // intervalOfCell[pos] = k where pos is visited between CP k and CP k+1
        val intervalOfCell = HashMap<GridPosition, Int>()
        val checkpointIndices = sortedCheckpoints.map { cp -> pathPositions.indexOf(cp.position) }
        var currentInterval = 1
        for (i in pathPositions.indices) {
            val pos = pathPositions[i]
            val cp = checkpointsByPos[pos]
            if (cp != null && cp.number > currentInterval && cp.number < sortedCheckpoints.size) {
                currentInterval = cp.number
            }
            intervalOfCell[pos] = currentInterval
        }

        // 2. Critical Connectors / Articulation Points in unvisited graph
        val criticalConnectors = findArticulationPoints(definition, definition.requiredCells)
        val criticalConnectorCount = criticalConnectors.size

        // 3. Step-by-step traversal and alternative branch analysis
        val visitedSoFar = HashSet<GridPosition>()
        var nextExpectedCp = 2

        var meaningfulDecisions = 0
        var consecutiveDecisionChain = 0
        var maxDecisionChain = 0

        val wrongBranchDepths = ArrayList<Int>()
        val allBranchEvaluations = ArrayList<WrongBranchEvaluation>()
        var maxWrongBranchDepth = 0
        var maxWrongCpDepth = 0

        var totalFutureDependencyScore = 0.0
        var maxDependencyDistance = 0.0

        var totalCellReservationScore = 0.0
        var regionDependencyCount = 0
        var regionPlanningScoreAcc = 0.0
        var deadEndCount = 0L

        for (step in 0 until pathLength - 1) {
            val currentPos = pathPositions[step]
            visitedSoFar.add(currentPos)

            val currentCp = checkpointsByPos[currentPos]
            if (currentCp != null && currentCp.number >= nextExpectedCp) {
                nextExpectedCp = currentCp.number + 1
            }

            val actualNext = pathPositions[step + 1]
            val traversableNeighbors = definition.graph.getTraversableNeighbors(currentPos)

            // Find all locally legal candidates
            val legalAlternatives = ArrayList<GridPosition>()
            for (nbr in traversableNeighbors) {
                if (nbr in visitedSoFar) continue
                val nbrCp = checkpointsByPos[nbr]
                if (nbrCp != null && nbrCp.number != nextExpectedCp) continue
                val remainingRequired = totalCells - visitedSoFar.size
                if (nbr == finalCheckpoint && remainingRequired > 1) continue
                legalAlternatives.add(nbr)
            }

            val hasBranching = legalAlternatives.size > 1
            if (hasBranching) {
                consecutiveDecisionChain++
                if (consecutiveDecisionChain > maxDecisionChain) {
                    maxDecisionChain = consecutiveDecisionChain
                }
            } else {
                consecutiveDecisionChain = 0
            }

            var stepHadMeaningfulBranch = false

            for (alt in legalAlternatives) {
                if (alt == actualNext) continue

                // Simulate forward lookahead on this locally legal wrong branch
                val branchEval = evaluateWrongBranch(
                    definition = definition,
                    startFrom = alt,
                    visitedPrefix = visitedSoFar,
                    initialNextCp = if (checkpointsByPos[alt]?.number == nextExpectedCp) nextExpectedCp + 1 else nextExpectedCp,
                    finalCheckpoint = finalCheckpoint,
                    intervalOfCell = intervalOfCell,
                    currentInterval = intervalOfCell[currentPos] ?: 1,
                    maxLookahead = minOf(30, totalCells / 2)
                )
                allBranchEvaluations.add(branchEval)

                if (branchEval.survivedSteps >= 2) {
                    wrongBranchDepths.add(branchEval.survivedSteps)
                    if (branchEval.survivedSteps > maxWrongBranchDepth) {
                        maxWrongBranchDepth = branchEval.survivedSteps
                    }
                    if (branchEval.checkpointsCrossed > maxWrongCpDepth) {
                        maxWrongCpDepth = branchEval.checkpointsCrossed
                    }
                    stepHadMeaningfulBranch = true
                }

                if (branchEval.consumedFutureCells > 0) {
                    totalFutureDependencyScore += branchEval.consumedFutureCells * (1.0 + branchEval.maxDependencyDistance * 0.5)
                    if (branchEval.maxDependencyDistance > maxDependencyDistance) {
                        maxDependencyDistance = branchEval.maxDependencyDistance.toDouble()
                    }
                }

                if (branchEval.isDeadEnd) {
                    deadEndCount++
                }

                if (branchEval.causedRegionDisconnection) {
                    regionDependencyCount++
                    regionPlanningScoreAcc += 1.5
                }
            }

            if (stepHadMeaningfulBranch) {
                meaningfulDecisions++
            }

            // Cell Reservation Check:
            // Check unvisited neighbors of currentPos that are NOT actualNext
            val currentIntIdx = intervalOfCell[currentPos] ?: 1
            for (nbr in traversableNeighbors) {
                if (nbr !in visitedSoFar && nbr != actualNext) {
                    val nbrInterval = intervalOfCell[nbr] ?: currentIntIdx
                    if (nbrInterval > currentIntIdx) {
                        val intervalDiff = nbrInterval - currentIntIdx
                        val isBridge = nbr in criticalConnectors
                        val weight = 1.0 + (intervalDiff * 0.5) + (if (isBridge) 2.0 else 0.0)
                        totalCellReservationScore += weight
                    }
                }
            }

            // Region analysis for unvisited cells:
            val unvisited = definition.requiredCells.filter { it !in visitedSoFar }
            val components = findConnectedComponents(definition, unvisited)
            if (components.size > 1) {
                regionPlanningScoreAcc += 0.5 * components.size
            }
        }

        // 4. Checkpoint Deception Score
        var deceptiveIntervals = 0
        for (i in 0 until sortedCheckpoints.size - 1) {
            val cp1 = sortedCheckpoints[i]
            val cp2 = sortedCheckpoints[i + 1]
            val idx1 = pathPositions.indexOf(cp1.position)
            val idx2 = pathPositions.indexOf(cp2.position)
            if (idx1 >= 0 && idx2 > idx1) {
                val pathDist = idx2 - idx1
                val manhattanDist = cp1.position.manhattanDistance(cp2.position)
                if (manhattanDist <= 3 && pathDist >= manhattanDist + 4) {
                    deceptiveIntervals++
                }
            }
        }
        val deceptionScore = if (sortedCheckpoints.size > 1) {
            deceptiveIntervals.toDouble() / (sortedCheckpoints.size - 1)
        } else 0.0

        val avgWrongDepth = if (wrongBranchDepths.isNotEmpty()) wrongBranchDepths.average() else 0.0
        val plausibleCount = wrongBranchDepths.size
        val decisionDepth = maxOf(1, maxDecisionChain)

        val selfCutCount = allBranchEvaluations.count { it.causedRegionDisconnection }
        val selfCutRisk = if (meaningfulDecisions > 0) (selfCutCount.toDouble() / meaningfulDecisions).coerceIn(0.0, 1.0) else 0.0
        val multiDepCount = allBranchEvaluations.count { it.consumedFutureCells > 1 }
        val depChainDepth = if (maxDependencyDistance > 0) maxOf(decisionDepth, maxDependencyDistance.toInt()) else decisionDepth
        val resDuration = maxDependencyDistance
        val resImportance = if (criticalConnectorCount > 0) (criticalConnectorCount.toDouble() * 1.5) else totalCellReservationScore * 0.2
        val wrongImpact = if (wrongBranchDepths.isNotEmpty()) {
            (maxWrongBranchDepth.toDouble() * 0.4 + maxWrongCpDepth * 0.6)
        } else 0.0

        val globalPlanning = ((regionPlanningScoreAcc / 15.0).coerceIn(0.0, 1.0) * 0.3 +
                (totalCellReservationScore / 25.0).coerceIn(0.0, 1.0) * 0.25 +
                (totalFutureDependencyScore / 20.0).coerceIn(0.0, 1.0) * 0.25 +
                (deceptionScore) * 0.2).coerceIn(0.0, 1.0)

        return AdvancedReasoningAnalysisResult(
            meaningfulDecisionPointCount = meaningfulDecisions,
            decisionDepth = decisionDepth,
            plausibleWrongBranchCount = plausibleCount,
            averageWrongBranchDepth = avgWrongDepth,
            maximumWrongBranchDepth = maxWrongBranchDepth,
            wrongBranchCheckpointDepth = maxWrongCpDepth,
            futureCellDependencyScore = totalFutureDependencyScore,
            futureDependencyDistance = maxDependencyDistance,
            cellReservationScore = totalCellReservationScore,
            checkpointDeceptionScore = deceptionScore,
            regionPlanningScore = regionPlanningScoreAcc,
            criticalConnectorCount = criticalConnectorCount,
            regionDependencyCount = regionDependencyCount,
            solverDeadEnds = deadEndCount,
            decisionChainDepth = decisionDepth,
            wrongBranchImpact = wrongImpact,
            multiDependencyDecisionCount = multiDepCount,
            dependencyChainDepth = depChainDepth,
            reservationDuration = resDuration,
            reservationImportance = resImportance,
            globalPlanningScore = globalPlanning,
            selfCutRisk = selfCutRisk
        )
    }

    private data class WrongBranchEvaluation(
        val survivedSteps: Int,
        val checkpointsCrossed: Int,
        val consumedFutureCells: Int,
        val maxDependencyDistance: Int,
        val isDeadEnd: Boolean,
        val deadEndType: DeadEndType,
        val causedRegionDisconnection: Boolean
    )

    private fun evaluateWrongBranch(
        definition: PuzzleDefinition,
        startFrom: GridPosition,
        visitedPrefix: Set<GridPosition>,
        initialNextCp: Int,
        finalCheckpoint: GridPosition?,
        intervalOfCell: Map<GridPosition, Int>,
        currentInterval: Int,
        maxLookahead: Int
    ): WrongBranchEvaluation {
        val branchVisited = HashSet(visitedPrefix)
        branchVisited.add(startFrom)

        val checkpointsByPos = definition.checkpoints.associateBy { it.position }
        var currHead = startFrom
        var nextCp = initialNextCp
        var steps = 1
        var checkpointsCrossed = if (checkpointsByPos[startFrom]?.number == initialNextCp - 1) 1 else 0

        var consumedFutureCells = 0
        var maxDepDist = 0

        val startInterval = intervalOfCell[startFrom] ?: currentInterval
        if (startInterval > currentInterval) {
            consumedFutureCells++
            val dist = startInterval - currentInterval
            if (dist > maxDepDist) maxDepDist = dist
        }

        var isDeadEnd = false
        var deadEndType = DeadEndType.IMMEDIATE_DEAD_END
        var causedDisconnection = false

        while (steps < maxLookahead) {
            val totalRemaining = definition.totalRequiredCells - branchVisited.size
            val neighbors = definition.graph.getTraversableNeighbors(currHead)

            val legalNext = ArrayList<GridPosition>()
            for (nbr in neighbors) {
                if (nbr in branchVisited) continue
                val cp = checkpointsByPos[nbr]
                if (cp != null && cp.number != nextCp) continue
                if (nbr == finalCheckpoint && totalRemaining > 1) continue
                legalNext.add(nbr)
            }

            if (legalNext.isEmpty()) {
                isDeadEnd = true
                deadEndType = if (currHead == finalCheckpoint && totalRemaining > 0) {
                    DeadEndType.FINAL_COVERAGE_FAILURE
                } else {
                    DeadEndType.IMMEDIATE_DEAD_END
                }
                break
            }

            // Check if remaining unvisited cells got disconnected
            val unvisited = definition.requiredCells.filter { it !in branchVisited }
            val comps = findConnectedComponents(definition, unvisited)
            if (comps.size > 1) {
                causedDisconnection = true
                isDeadEnd = true
                deadEndType = DeadEndType.CONNECTIVITY_DEAD_END
                break
            }

            // Pick candidate (simulate player's greedy continuation towards next target checkpoint)
            val targetPos = definition.checkpoints.find { it.number == nextCp }?.position ?: finalCheckpoint
            val chosen = if (targetPos != null) {
                legalNext.minByOrNull { it.manhattanDistance(targetPos) } ?: legalNext.first()
            } else {
                legalNext.first()
            }

            branchVisited.add(chosen)
            currHead = chosen
            steps++

            val chosenCp = checkpointsByPos[chosen]
            if (chosenCp != null && chosenCp.number == nextCp) {
                nextCp++
                checkpointsCrossed++
            }

            val cellInterval = intervalOfCell[chosen] ?: currentInterval
            if (cellInterval > currentInterval) {
                consumedFutureCells++
                val dist = cellInterval - currentInterval
                if (dist > maxDepDist) maxDepDist = dist
            }
        }

        return WrongBranchEvaluation(
            survivedSteps = steps,
            checkpointsCrossed = checkpointsCrossed,
            consumedFutureCells = consumedFutureCells,
            maxDependencyDistance = maxDepDist,
            isDeadEnd = isDeadEnd,
            deadEndType = deadEndType,
            causedRegionDisconnection = causedDisconnection
        )
    }

    /**
     * Finds articulation points (cut vertices) in the subgraph induced by [cells].
     */
    fun findArticulationPoints(
        definition: PuzzleDefinition,
        cells: Collection<GridPosition>
    ): Set<GridPosition> {
        val cellSet = cells.toSet()
        if (cellSet.size <= 2) return emptySet()

        val baseComponents = findConnectedComponents(definition, cellSet).size
        val articulationPoints = HashSet<GridPosition>()

        for (candidate in cellSet) {
            val remaining = cellSet - candidate
            val comps = findConnectedComponents(definition, remaining).size
            if (comps > baseComponents) {
                articulationPoints.add(candidate)
            }
        }

        return articulationPoints
    }

    /**
     * Finds connected components of [cells] under definition graph traversability.
     */
    fun findConnectedComponents(
        definition: PuzzleDefinition,
        cells: Collection<GridPosition>
    ): List<Set<GridPosition>> {
        val unvisited = HashSet(cells)
        val components = ArrayList<Set<GridPosition>>()

        while (unvisited.isNotEmpty()) {
            val root = unvisited.first()
            val component = HashSet<GridPosition>()
            val queue = ArrayDeque<GridPosition>()

            queue.add(root)
            unvisited.remove(root)
            component.add(root)

            while (queue.isNotEmpty()) {
                val curr = queue.removeFirst()
                val neighbors = definition.graph.getTraversableNeighbors(curr)
                for (nbr in neighbors) {
                    if (nbr in unvisited) {
                        unvisited.remove(nbr)
                        component.add(nbr)
                        queue.add(nbr)
                    }
                }
            }
            components.add(component)
        }

        return components
    }
}
