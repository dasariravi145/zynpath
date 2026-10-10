package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.SolutionValidationResult
import com.zynpath.game.core.puzzle.solver.SolutionValidator
import com.zynpath.game.core.puzzle.solver.SolverConfiguration
import com.zynpath.game.core.puzzle.solver.SolverResult
import com.zynpath.game.core.puzzle.validator.DefinitionValidationResult
import com.zynpath.game.core.puzzle.validator.PuzzleDefinitionValidator
import kotlin.math.log10

/**
 * Objective difficulty analysis engine for Zynpath logic puzzles.
 *
 * Implements Prompt 10 Sections 5, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, and 18:
 * - Computes objective structural, graph topology, checkpoint spacing, route traversal,
 *   wall impact, and solver search behavior metrics.
 * - Distinguishes mathematical solvability from human-perceived puzzle difficulty.
 * - Produces a deterministic, normalized composite difficulty score and [DifficultyBand].
 * - Explicitly marks difficulty ratings as provisional algorithmic estimates.
 */
class PuzzleDifficultyAnalyzer(
    private val defaultConfiguration: DifficultyAnalysisConfiguration = DifficultyAnalysisConfiguration.DEFAULT,
    private val solver: PuzzleSolver = PuzzleSolver(),
    private val advancedAnalyzer: com.zynpath.game.core.puzzle.analysis.AdvancedReasoningAnalyzer = com.zynpath.game.core.puzzle.analysis.AdvancedReasoningAnalyzer()
) {

    /**
     * Analyzes the difficulty of [definition] using optional pre-verified solution and solver results.
     *
     * @param definition The target puzzle definition to evaluate.
     * @param verifiedSolution Optional known valid solution path. If provided, it is verified against [SolutionValidator].
     * @param solverResult Optional precomputed solver result containing search statistics and uniqueness status.
     * @param config Configuration parameters and scoring weights.
     * @return Comprehensive [DifficultyAnalysisResult] containing all metrics and composite score.
     */
    fun analyze(
        definition: PuzzleDefinition,
        verifiedSolution: PuzzlePath? = null,
        solverResult: SolverResult? = null,
        config: DifficultyAnalysisConfiguration = defaultConfiguration
    ): DifficultyAnalysisResult {
        // 1. Validate puzzle definition integrity
        val validation = PuzzleDefinitionValidator.validate(definition)
        require(validation is DefinitionValidationResult.Valid) {
            val errorMsg = (validation as? DefinitionValidationResult.Invalid)?.errorSummary ?: "Invalid definition"
            "Cannot analyze structurally invalid puzzle definition: $errorMsg"
        }

        // 2. Resolve and verify solution and solver statistics
        val effectiveSolverResult: SolverResult
        val effectivePath: PuzzlePath

        if (verifiedSolution != null && solverResult != null) {
            val solutionCheck = SolutionValidator.validate(definition, verifiedSolution)
            require(solutionCheck is SolutionValidationResult.Valid) {
                "Supplied verifiedSolution failed validation: $solutionCheck"
            }
            effectivePath = verifiedSolution
            effectiveSolverResult = solverResult
        } else if (verifiedSolution != null) {
            val solutionCheck = SolutionValidator.validate(definition, verifiedSolution)
            require(solutionCheck is SolutionValidationResult.Valid) {
                "Supplied verifiedSolution failed validation: $solutionCheck"
            }
            effectivePath = verifiedSolution
            // Run solver to establish search metrics and uniqueness proof
            val solverConfig = SolverConfiguration(
                maxSolutions = 2,
                nodeLimit = config.solverNodeBudget,
                timeBudgetMs = config.solverTimeBudgetMs
            )
            effectiveSolverResult = solver.solve(definition, solverConfig)
        } else if (solverResult != null) {
            effectiveSolverResult = solverResult
            val firstSol = solverResult.firstSolution
            require(firstSol != null) {
                "Supplied solverResult does not contain any valid solution"
            }
            effectivePath = firstSol
        } else {
            // Solve the puzzle exhaustively up to 2 solutions to prove uniqueness and gather telemetry
            val solverConfig = SolverConfiguration(
                maxSolutions = 2,
                nodeLimit = config.solverNodeBudget,
                timeBudgetMs = config.solverTimeBudgetMs
            )
            effectiveSolverResult = solver.solve(definition, solverConfig)
            val firstSol = effectiveSolverResult.firstSolution
            require(firstSol != null) {
                "Cannot compute difficulty for unsolvable puzzle: ${effectiveSolverResult.diagnosticMessage}"
            }
            effectivePath = firstSol
        }

        // 3. Compute Granular Metrics
        val metrics = computeMetrics(definition, effectivePath, effectiveSolverResult)

        // 4. Calculate Normalized Component Scores
        val componentScores = computeComponentScores(metrics)

        // 5. Compute Weighted Composite Score
        val baseScore = (
            config.weightSize * componentScores.getValue("size") +
            config.weightWalls * componentScores.getValue("walls") +
            config.weightCheckpoints * componentScores.getValue("checkpoints") +
            config.weightTurns * componentScores.getValue("turns") +
            config.weightGaps * componentScores.getValue("gaps") +
            config.weightSolverNodes * componentScores.getValue("solver") +
            config.weightDecisions * componentScores.getValue("decisions") +
            config.weightLookahead * componentScores.getValue("lookahead") +
            config.weightIsolation * componentScores.getValue("isolation")
        )

        // If solved by simple naive strategy (greedy/serpentine/wall-follower), cap/discount difficulty
        val estimatedScore = if (metrics.simpleStrategySolved) {
            (baseScore * 0.70).coerceIn(0.0, 1.0)
        } else {
            (baseScore * 1.15).coerceIn(0.0, 1.0)
        }

        val band = DifficultyBand.fromScore(estimatedScore)


        return DifficultyAnalysisResult(
            puzzleId = definition.puzzleId,
            estimatedScore = estimatedScore,
            difficultyBand = band,
            metrics = metrics,
            componentScores = componentScores,
            isProvisional = true,
            configurationVersion = config.configVersion,
            analyzerVersion = config.analyzerVersion
        )
    }

    /**
     * Extracts all objective structural, graph, checkpoint, route, wall, and solver metrics.
     */
    fun computeMetrics(
        definition: PuzzleDefinition,
        path: PuzzlePath,
        solverResult: SolverResult
    ): DifficultyMetrics {
        val rows = definition.gridDimensions.rows
        val cols = definition.gridDimensions.columns
        val totalCells = definition.totalRequiredCells
        val checkpointCount = definition.checkpoints.size
        val wallCount = definition.blockedEdges.size

        // Graph Topology & Traversable Degrees
        val startCheckpoint = definition.checkpoints.find { it.number == 1 }?.position
        val finalCheckpoint = definition.checkpoints.maxByOrNull { it.number }?.position

        var totalDegreeSum = 0
        var constrainedCount = 0
        var branchCount = 0
        var deadEndCount = 0

        for (cell in definition.requiredCells) {
            val degree = definition.graph.getTraversableNeighbors(cell).size
            totalDegreeSum += degree
            when {
                degree == 2 -> constrainedCount++
                degree >= 3 -> branchCount++
                degree <= 1 -> {
                    // Start and final endpoints are natural dead ends in an open path
                    if (cell != startCheckpoint && cell != finalCheckpoint) {
                        deadEndCount++
                    }
                }
            }
        }

        val traversableEdgeCount = totalDegreeSum / 2
        val avgDegree = if (totalCells > 0) totalDegreeSum.toDouble() / totalCells else 0.0

        // Checkpoint Distribution along the verified path
        val pathPositions = path.positions
        val sortedCheckpoints = definition.checkpoints.sortedBy { it.number }
        val checkpointIndices = sortedCheckpoints.map { cp ->
            pathPositions.indexOf(cp.position)
        }

        val gaps = ArrayList<Int>()
        for (i in 1 until checkpointIndices.size) {
            val prevIdx = checkpointIndices[i - 1]
            val currIdx = checkpointIndices[i]
            if (prevIdx >= 0 && currIdx >= 0 && currIdx >= prevIdx) {
                gaps.add(currIdx - prevIdx)
            }
        }

        val minGap = gaps.minOrNull() ?: 0
        val maxGap = gaps.maxOrNull() ?: 0
        val avgGap = if (gaps.isNotEmpty()) gaps.average() else 0.0
        val gapVariance = if (gaps.isNotEmpty()) {
            gaps.map { (it - avgGap) * (it - avgGap) }.average()
        } else {
            0.0
        }
        val maxStretchRatio = if (totalCells > 0) maxGap.toDouble() / totalCells else 0.0

        // Route Complexity
        val pathLength = pathPositions.size
        var turns = 0
        val segmentLengths = ArrayList<Int>()
        var horizontalMoves = 0
        var verticalMoves = 0

        if (pathLength >= 2) {
            var currentSegmentLength = 1
            var prevDr = pathPositions[1].row - pathPositions[0].row
            var prevDc = pathPositions[1].column - pathPositions[0].column
            if (prevDc != 0) horizontalMoves++ else if (prevDr != 0) verticalMoves++

            for (i in 1 until pathLength - 1) {
                val dr = pathPositions[i + 1].row - pathPositions[i].row
                val dc = pathPositions[i + 1].column - pathPositions[i].column
                if (dc != 0) horizontalMoves++ else if (dr != 0) verticalMoves++

                if (dr == prevDr && dc == prevDc) {
                    currentSegmentLength++
                } else {
                    turns++
                    segmentLengths.add(currentSegmentLength)
                    currentSegmentLength = 1
                    prevDr = dr
                    prevDc = dc
                }
            }
            segmentLengths.add(currentSegmentLength)
        }

        val longestStraight = segmentLengths.maxOrNull() ?: 0
        val straightCount = segmentLengths.size
        val turnFrequency = if (pathLength > 2) turns.toDouble() / (pathLength - 2) else 0.0

        // Wall Complexity
        val totalInternalEdges = (rows * (cols - 1)) + ((rows - 1) * cols)
        val wallDensity = if (totalInternalEdges > 0) wallCount.toDouble() / totalInternalEdges else 0.0

        val wallConstrainedCells = definition.requiredCells.count { cell ->
            definition.blockedEdges.any { edge -> edge.first == cell || edge.second == cell }
        }

        // Advanced Reasoning, Decisions, Look-Ahead & Isolation (Section 2)
        var meaningfulDecisions = 0
        var forcedMoves = 0
        var optionalMoves = 0
        var maxLookAhead = 0
        var plausibleIncorrectPaths = 0
        var isolationRisks = 0

        val checkpointsByPos = definition.checkpoints.associateBy { it.position }
        val visitedSoFar = HashSet<GridPosition>()
        var nextCpNumber = 2
        val branchDepths = ArrayList<Int>()
        var futureCellDepCount = 0
        var cellReservationCount = 0
        var longRangeDepCount = 0

        if (pathLength >= 2) {
            for (step in 0 until pathLength - 1) {
                val current = pathPositions[step]
                visitedSoFar.add(current)

                val cpCurrent = checkpointsByPos[current]
                if (cpCurrent != null && cpCurrent.number >= nextCpNumber) {
                    nextCpNumber = cpCurrent.number + 1
                }

                val nextActual = pathPositions[step + 1]
                val neighbors = definition.graph.getTraversableNeighbors(current)
                val legalCandidates = ArrayList<GridPosition>()

                for (nbr in neighbors) {
                    if (nbr in visitedSoFar) continue
                    val cpNbr = checkpointsByPos[nbr]
                    if (cpNbr != null && cpNbr.number != nextCpNumber) continue
                    val remainingRequired = totalCells - visitedSoFar.size
                    if (nbr == finalCheckpoint && remainingRequired > 1) continue
                    legalCandidates.add(nbr)
                }

                if (legalCandidates.size > 1) {
                    meaningfulDecisions++
                    optionalMoves++

                    for (alt in legalCandidates) {
                        if (alt == nextActual) continue

                        // 1. Look-ahead depth check: simulate taking alt and see how many steps it survives
                        val branchVisited = HashSet(visitedSoFar)
                        branchVisited.add(alt)
                        var branchNextCp = nextCpNumber
                        val cpAtAlt = checkpointsByPos[alt]
                        if (cpAtAlt != null && cpAtAlt.number >= branchNextCp) {
                            branchNextCp = cpAtAlt.number + 1
                        }

                        var depth = 1
                        var currBranch = alt
                        var canAdvance = true
                        while (canAdvance && depth < 8) {
                            val nextNbrs = definition.graph.getTraversableNeighbors(currBranch)
                            val branchCands = nextNbrs.filter { bn ->
                                bn !in branchVisited &&
                                (checkpointsByPos[bn]?.number == null || checkpointsByPos[bn]?.number == branchNextCp) &&
                                (bn != finalCheckpoint || (totalCells - branchVisited.size) <= 1)
                            }
                            if (branchCands.isEmpty()) {
                                canAdvance = false
                            } else {
                                depth++
                                val nextStep = branchCands.first()
                                branchVisited.add(nextStep)
                                val cpNext = checkpointsByPos[nextStep]
                                if (cpNext != null && cpNext.number >= branchNextCp) {
                                    branchNextCp = cpNext.number + 1
                                }
                                currBranch = nextStep
                            }
                        }

                        branchDepths.add(depth)
                        if (depth > maxLookAhead) {
                            maxLookAhead = depth
                        }
                        if (depth >= 2) {
                            plausibleIncorrectPaths++
                        }
                        if (depth >= 3) {
                            longRangeDepCount++
                        }

                        // 2. Cell isolation & reservation check
                        val unvisitedAfterAlt = definition.requiredCells.filter { it !in visitedSoFar && it != alt }
                        if (unvisitedAfterAlt.isNotEmpty()) {
                            var hasIsolatedCell = false
                            for (u in unvisitedAfterAlt) {
                                if (u == finalCheckpoint) continue
                                val degreeInRemaining = definition.graph.getTraversableNeighbors(u)
                                    .count { it in unvisitedAfterAlt || it == alt }
                                if (degreeInRemaining < 2) {
                                    hasIsolatedCell = true
                                    break
                                }
                            }
                            if (hasIsolatedCell) {
                                isolationRisks++
                                futureCellDepCount++
                            }
                        }
                    }
                } else {
                    forcedMoves++
                }

                // Check cell reservation: if current position has unvisited neighbors that are NOT visited
                // until after the next checkpoint is reached
                val nextTargetCpPos = definition.checkpoints.find { it.number == nextCpNumber }?.position
                if (nextTargetCpPos != null) {
                    val rawNbrs = definition.graph.getTraversableNeighbors(current)
                    for (rn in rawNbrs) {
                        if (rn !in visitedSoFar && rn != nextActual) {
                            val nextCpIdx = pathPositions.indexOf(nextTargetCpPos)
                            val rnIdx = pathPositions.indexOf(rn)
                            if (nextCpIdx in 0..rnIdx) {
                                cellReservationCount++
                            }
                        }
                    }
                }
            }
        }

        // Run simple strategy baseline solver to detect if casual greedy / wall-follower easily solves it
        val simpleBaseline = com.zynpath.game.core.puzzle.solver.SimpleStrategyBaselineSolver()
        val simpleEval = simpleBaseline.evaluate(definition)

        // Count deceptive checkpoint pairs: Manhattan distance <= 2, but path gap >= 4
        var deceptiveCount = 0
        for (i in 1 until sortedCheckpoints.size) {
            val cp1 = sortedCheckpoints[i - 1].position
            val cp2 = sortedCheckpoints[i].position
            val manhattan = kotlin.math.abs(cp1.row - cp2.row) + kotlin.math.abs(cp1.column - cp2.column)
            val gap = gaps.getOrNull(i - 1) ?: 0
            if (manhattan <= 2 && gap >= 4) {
                deceptiveCount++
            }
        }

        val wallPositions = definition.blockedEdges.map {
            "${it.first.row},${it.first.column}-${it.second.row},${it.second.column}"
        }.sorted()

        val advResult = advancedAnalyzer.analyze(definition, path)
        val avgWrongDepth = if (branchDepths.isNotEmpty()) branchDepths.average() else 0.0
        val forcedRatio = if (pathLength > 0) forcedMoves.toDouble() / pathLength else 0.0
        val deceptionScore = if (sortedCheckpoints.size > 1) deceptiveCount.toDouble() / (sortedCheckpoints.size - 1) else 0.0
        val fingerprint = com.zynpath.game.core.puzzle.generator.PuzzleFingerprint.canonicalRepresentation(definition)

        return DifficultyMetrics(
            rows = rows,
            columns = cols,
            totalRequiredCells = totalCells,
            checkpointCount = checkpointCount,
            wallCount = wallCount,
            traversableEdgeCount = traversableEdgeCount,
            averageTraversableDegree = avgDegree,
            constrainedCellCount = constrainedCount,
            branchCellCount = branchCount,
            deadEndCellCount = deadEndCount,
            checkpointGaps = gaps,
            minCheckpointGap = minGap,
            maxCheckpointGap = maxGap,
            avgCheckpointGap = avgGap,
            checkpointGapVariance = gapVariance,
            maxUnnumberedStretchRatio = maxStretchRatio,
            pathLength = pathLength,
            turnCount = turns,
            turnFrequency = turnFrequency,
            longestStraightSegment = longestStraight,
            straightSegmentCount = straightCount,
            horizontalMoveCount = horizontalMoves,
            verticalMoveCount = verticalMoves,
            wallDensity = wallDensity,
            wallConstrainedCells = wallConstrainedCells,
            solverNodesExplored = solverResult.statistics.nodesExplored,
            solverBacktracks = solverResult.statistics.backtracks,
            solverPrunedBranches = solverResult.statistics.prunedBranches,
            uniquenessStatus = solverResult.uniqueness,
            solutionCount = solverResult.solutionCount,
            meaningfulRouteDecisions = meaningfulDecisions,
            forcedMovesCount = forcedMoves,
            optionalMovesCount = optionalMoves,
            requiredLookAheadDepth = maxLookAhead,
            plausibleIncorrectPathsCount = plausibleIncorrectPaths,
            remainingCellIsolationRisks = isolationRisks,
            maxSimilarityToPrevious = 0.0,
            simpleStrategySolved = simpleEval.solvedByAnySimpleStrategy,
            simpleStrategyName = simpleEval.successfulStrategy,
            deceptiveCheckpointDistances = deceptiveCount,
            verifiedCoveredCells = pathLength,
            wallPositions = wallPositions,
            forcedMoveRatio = forcedRatio,
            maxWrongBranchDepth = maxOf(maxLookAhead, advResult.maximumWrongBranchDepth),
            averageWrongBranchDepth = if (advResult.plausibleWrongBranchCount > 0) advResult.averageWrongBranchDepth else avgWrongDepth,
            futureCellDependency = maxOf(futureCellDepCount, advResult.futureCellDependencyScore.toInt()),
            cellReservationRequirement = maxOf(cellReservationCount, advResult.cellReservationScore.toInt()),
            checkpointDeceptionScore = maxOf(deceptionScore, advResult.checkpointDeceptionScore),
            longRangeDependencyCount = maxOf(longRangeDepCount, advResult.futureDependencyDistance.toInt()),
            backtrackingRequirement = solverResult.statistics.backtracks.toInt(),
            structuralFingerprint = fingerprint,
            simpleStrategyDetails = simpleEval.details,
            meaningfulDecisionPointCount = maxOf(meaningfulDecisions, advResult.meaningfulDecisionPointCount),
            decisionDepth = advResult.decisionDepth,
            wrongBranchCheckpointDepth = advResult.wrongBranchCheckpointDepth,
            futureDependencyDistance = advResult.futureDependencyDistance,
            criticalConnectorCount = advResult.criticalConnectorCount,
            regionDependencyCount = advResult.regionDependencyCount,
            solverDeadEnds = maxOf(solverResult.statistics.backtracks, advResult.solverDeadEnds),
            regionPlanningScore = advResult.regionPlanningScore,
            cellReservationScore = advResult.cellReservationScore,
            futureCellDependencyScore = advResult.futureCellDependencyScore,
            decisionChainDepth = advResult.decisionChainDepth,
            wrongBranchImpact = advResult.wrongBranchImpact,
            multiDependencyDecisionCount = advResult.multiDependencyDecisionCount,
            dependencyChainDepth = advResult.dependencyChainDepth,
            reservationDuration = advResult.reservationDuration,
            reservationImportance = advResult.reservationImportance,
            globalPlanningScore = advResult.globalPlanningScore,
            selfCutRisk = advResult.selfCutRisk
        )
    }


    /**
     * Normalizes metrics into sub-scores scaled between 0.0 and 1.0.
     */
    private fun computeComponentScores(m: DifficultyMetrics): Map<String, Double> {
        // Size score: 6x6 (36 cells) production minimum -> 0.0, 12x12 (144 cells) production maximum -> 1.0
        val sizeScore = ((m.totalRequiredCells - 36).coerceAtLeast(0) / (144.0 - 36.0)).coerceIn(0.0, 1.0)

        // Wall score: World 6 maximum wall limit is 18
        val wallScore = (m.wallCount / 18.0).coerceIn(0.0, 1.0)

        // Checkpoint density: Fewer checkpoints relative to grid size increases difficulty
        // Dense checkpoints (~0.35 of board) -> 0.0; Sparse checkpoints (~0.06 of board) -> 1.0
        val cpRatio = if (m.totalRequiredCells > 0) m.checkpointCount.toDouble() / m.totalRequiredCells else 0.2
        val cpScore = (1.0 - ((cpRatio - 0.06) / (0.35 - 0.06))).coerceIn(0.0, 1.0)

        // Route turn score: labyrinthine paths with frequent directional shifts are harder to trace mentally
        // Subtract baseline turns inherent to any planar snake path (~0.20)
        val turnScore = ((m.turnFrequency - 0.20).coerceAtLeast(0.0) / 0.60).coerceIn(0.0, 1.0)

        // Checkpoint gap stretch score: long unnumbered stretches require deeper lookahead
        val gapScore = m.maxUnnumberedStretchRatio.coerceIn(0.0, 1.0)

        // Solver complexity: Logarithmic scale of explored search tree nodes above trivial depth
        val solverScore = if (m.solverNodesExplored > 20L) {
            ((log10(m.solverNodesExplored.toDouble()) - 1.3).coerceAtLeast(0.0) / 2.7).coerceIn(0.0, 1.0)
        } else {
            0.0
        }

        return mapOf(
            "size" to sizeScore,
            "walls" to wallScore,
            "checkpoints" to cpScore,
            "turns" to turnScore,
            "gaps" to gapScore,
            "solver" to solverScore,
            "decisions" to (m.meaningfulRouteDecisions.toDouble() / 8.0).coerceIn(0.0, 1.0),
            "lookahead" to (m.requiredLookAheadDepth.toDouble() / 4.0).coerceIn(0.0, 1.0),
            "isolation" to (m.remainingCellIsolationRisks.toDouble() / 4.0).coerceIn(0.0, 1.0),
            "globalPlanning" to m.globalPlanningScore
        )
    }

    /**
     * Evaluates and returns the authoritative [DifficultyAnalysis] model adhering to Master Redesign Section 18.
     */
    fun analyzeDifficulty(
        definition: PuzzleDefinition,
        verifiedSolution: PuzzlePath? = null,
        solverResult: SolverResult? = null
    ): DifficultyAnalysis {
        return analyze(definition, verifiedSolution, solverResult).difficultyAnalysis
    }
}
