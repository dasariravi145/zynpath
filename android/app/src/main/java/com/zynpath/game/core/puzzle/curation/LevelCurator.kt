package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.generator.GenerationConfiguration
import com.zynpath.game.core.puzzle.generator.GenerationResult
import com.zynpath.game.core.puzzle.generator.GenerationStatus
import com.zynpath.game.core.puzzle.generator.PuzzleFingerprint
import com.zynpath.game.core.puzzle.generator.PuzzleGenerator
import com.zynpath.game.core.puzzle.generator.RouteStyle
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.SolverConfiguration
import com.zynpath.game.core.puzzle.solver.UniquenessStatus

/**
 * Authoritative level curation engine for Zynpath: Number Path Puzzle.
 *
 * Implements Prompt 10 Sections 24, 25, 26, 27, 28, 29, 30, and 31:
 * - Coordinates deterministic candidate generation, solver verification, difficulty analysis,
 *   and multi-signal quality evaluation.
 * - Strictly enforces the World Progression Contract (Worlds 1–6).
 * - Implements smooth progression curves with periodic recovery levels.
 * - Selects the highest quality candidate from bounded candidate batches.
 * - Protects published puzzle identity and produces structured, reproducible [CurationResult] telemetry.
 */
class LevelCurator(
    private val generator: PuzzleGenerator = PuzzleGenerator(),
    private val analyzer: PuzzleDifficultyAnalyzer = PuzzleDifficultyAnalyzer(),
    private val solver: PuzzleSolver = PuzzleSolver(),
    private val config: CurationConfiguration = CurationConfiguration.DEFAULT
) {

    /**
     * Curates a contiguous sequence of levels starting from [startLevelId] up to [count] levels.
     *
     * @param startLevelId The 1-based initial level number.
     * @param count Total number of levels to curate.
     * @param baseSeed Deterministic base random seed for candidate generation.
     * @param existingCatalog Existing published or accepted levels to avoid duplicates against.
     * @return [CurationResult] containing curated levels and detailed diagnostics.
     */
    fun curateLevels(
        startLevelId: Int,
        count: Int,
        baseSeed: Long = 10_000L,
        existingCatalog: List<CuratedLevel> = emptyList()
    ): CurationResult {
        require(startLevelId >= 1) { "startLevelId must be >= 1" }
        require(count >= 1) { "count must be >= 1" }

        val acceptedLevels = ArrayList<CuratedLevel>()
        val existingDefs = ArrayList<PuzzleDefinition>(existingCatalog.map { it.definition })

        var totalCandidates = 0
        var rejectedCount = 0
        val rejectionReasonCounts = mutableMapOf<QualityRejectionReason, Int>()
        val rejectionDiagnostics = ArrayList<String>()

        val difficultyMetricsMap = mutableMapOf<Int, DifficultyMetrics>()
        val qualityScoresMap = mutableMapOf<Int, Double>()
        val uniquenessStatusMap = mutableMapOf<Int, UniquenessStatus>()
        val fingerprintsMap = mutableMapOf<Int, String>()

        for (levelOffset in 0 until count) {
            val levelId = startLevelId + levelOffset
            val worldSpec = WorldProgressionSpec.forLevel(levelId)

            // Determine target difficulty and bounds for this level in progression curve
            val targetRange = computeTargetDifficultyRange(worldSpec, levelId)

            val criteria = PuzzleQualityEvaluator.QualityCriteria(
                expectedDimensions = worldSpec.gridDimensions,
                checkpointRange = worldSpec.checkpointRange,
                wallRange = resolveWallRangeForLevel(worldSpec, levelId),
                requireUnique = config.requireUnique,
                targetDifficultyRange = targetRange,
                targetDifficultyBand = worldSpec.targetDifficultyBand,
                maxAllowedSimilarity = config.maxAllowedSimilarity,
                rejectSymmetricDuplicates = config.rejectSymmetricDuplicates
            )

            // Evaluate a bounded candidate batch and select the best candidate
            var bestCandidateLevel: CuratedLevel? = null
            var bestCandidateScore = -1.0
            var bestCandidateDiff: DifficultyAnalysisResult? = null

            for (attempt in 0 until config.maxCandidatesPerLevel) {
                totalCandidates++
                val candidateSeed = baseSeed + (levelId * 1000L) + attempt

                val genConfig = buildGenerationConfig(worldSpec, levelId, candidateSeed, attempt)
                val genResult = generator.generate(genConfig)

                if (!genResult.isSuccess ||
                    genResult.puzzle == null ||
                    genResult.verifiedSolution == null) {
                    rejectedCount++
                    val reason = when (genResult.status) {
                        GenerationStatus.SOLVER_INCONCLUSIVE -> QualityRejectionReason.INCONCLUSIVE_VERIFICATION
                        GenerationStatus.INVALID_CONFIGURATION -> QualityRejectionReason.STRUCTURAL_INVALIDITY
                        else -> QualityRejectionReason.UNSOLVABLE
                    }
                    rejectionReasonCounts[reason] = (rejectionReasonCounts[reason] ?: 0) + 1
                    rejectionDiagnostics.add(
                        "Lvl $levelId attempt $attempt failed generation: ${genResult.diagnosticMessage}"
                    )
                    continue
                }

                val def = genResult.puzzle
                val path = genResult.verifiedSolution

                // Exhaustive solver verification to establish search metrics and uniqueness proof
                val solverConfig = SolverConfiguration(
                    maxSolutions = 2,
                    nodeLimit = config.difficultyConfig.solverNodeBudget,
                    timeBudgetMs = config.difficultyConfig.solverTimeBudgetMs
                )
                val solverRes = solver.solve(def, solverConfig)

                // Analyze difficulty
                val diffResult = analyzer.analyze(def, path, solverRes, config.difficultyConfig)

                // Quality evaluation
                val qualityResult = PuzzleQualityEvaluator.evaluate(
                    definition = def,
                    solution = path,
                    solverResult = solverRes,
                    difficulty = diffResult,
                    criteria = criteria,
                    existingLevels = existingDefs
                )

                if (qualityResult.isAccepted) {
                    // Soft ranking: pick candidate with highest soft quality score
                    if (qualityResult.softQualityScore > bestCandidateScore) {
                        bestCandidateScore = qualityResult.softQualityScore
                        bestCandidateDiff = diffResult

                        val metadata = LevelMetadata(
                            levelId = levelId,
                            worldId = worldSpec.worldId,
                            puzzleId = def.puzzleId,
                            puzzleVersion = "1.0.0",
                            generationSeed = candidateSeed,
                            generatorVersion = genConfig.generatorVersion,
                            difficultyEstimate = diffResult.estimatedScore,
                            difficultyBand = diffResult.difficultyBand,
                            uniquenessStatus = solverRes.uniqueness,
                            softQualityScore = qualityResult.softQualityScore,
                            curationVersion = config.curationVersion
                        )
                        bestCandidateLevel = CuratedLevel(levelId, worldSpec.worldId, def, metadata)
                    }
                } else {
                    rejectedCount++
                    for (r in qualityResult.rejectionReasons) {
                        rejectionReasonCounts[r] = (rejectionReasonCounts[r] ?: 0) + 1
                    }
                    if (qualityResult.rejectionDetails.isNotEmpty()) {
                        rejectionDiagnostics.add(
                            "Lvl $levelId attempt $attempt rejected: ${qualityResult.rejectionDetails.joinToString("; ")}"
                        )
                    }
                }
            }

            if (bestCandidateLevel != null) {
                acceptedLevels.add(bestCandidateLevel)
                existingDefs.add(bestCandidateLevel.definition)

                bestCandidateDiff?.let { diff ->
                    difficultyMetricsMap[levelId] = diff.metrics
                    qualityScoresMap[levelId] = bestCandidateLevel.metadata.softQualityScore
                    uniquenessStatusMap[levelId] = diff.uniquenessStatus
                }
                fingerprintsMap[levelId] = PuzzleFingerprint.computeSha256(bestCandidateLevel.definition)
            } else {
                rejectionDiagnostics.add(
                    "Lvl $levelId exhausted ${config.maxCandidatesPerLevel} candidate attempts without an accepted puzzle"
                )
            }
        }

        return CurationResult(
            acceptedLevels = acceptedLevels,
            totalCandidatesEvaluated = totalCandidates,
            rejectedCandidateCount = rejectedCount,
            rejectionReasonCounts = rejectionReasonCounts,
            rejectionDiagnostics = rejectionDiagnostics,
            difficultyMetrics = difficultyMetricsMap,
            qualityScores = qualityScoresMap,
            uniquenessStatuses = uniquenessStatusMap,
            fingerprints = fingerprintsMap,
            generatorVersion = GenerationConfiguration.GENERATOR_VERSION,
            analyzerVersion = DifficultyAnalysisConfiguration.ANALYZER_VERSION,
            solverVersion = "1.0.0",
            curationConfigVersion = config.curationVersion
        )
    }

    /**
     * Resolves the target difficulty range for a level, incorporating progression slope
     * and periodic recovery levels (Prompt 10 Section 26).
     */
    fun computeTargetDifficultyRange(
        worldSpec: WorldProgressionSpec,
        levelId: Int
    ): ClosedFloatingPointRange<Double> {
        val startLvl = worldSpec.levelRange.first
        val endLvl = worldSpec.levelRange.last
        val span = (endLvl - startLvl).coerceAtLeast(1)
        val offset = (levelId - startLvl).coerceAtLeast(0)

        val t = offset.toDouble() / span
        val targetMin = worldSpec.targetDifficultyRange.start
        val targetMax = worldSpec.targetDifficultyRange.endInclusive

        var baseTarget = targetMin + t * (targetMax - targetMin)

        // Recovery level reduction: periodic easier levels to relieve cognitive fatigue
        if (config.allowRecoveryLevels && offset > 0 && offset % config.recoveryLevelFrequency == 0) {
            baseTarget = (baseTarget - config.recoveryDifficultyReduction).coerceAtLeast(targetMin)
        }

        val rangeRadius = 0.20
        val low = (baseTarget - rangeRadius).coerceAtLeast(0.0)
        val high = (baseTarget + rangeRadius).coerceAtMost(1.0)
        return low..high
    }

    /**
     * Resolves valid wall count bounds for a level, enforcing gentle introductory wall rules
     * in early World 3 (Prompt 10 Section 28).
     */
    fun resolveWallRangeForLevel(worldSpec: WorldProgressionSpec, levelId: Int): IntRange {
        if (worldSpec.wallRange.last == 0) {
            return 0..0
        }

        // World 3 Introductory Wall Levels (Levels 51..60)
        if (worldSpec.worldId == 3 && levelId in 51..60) {
            return 1..2 // Start with 1-2 walls before scaling up to 5
        }

        return worldSpec.wallRange
    }

    /**
     * Builds a [GenerationConfiguration] tailored to the world progression contract.
     */
    private fun buildGenerationConfig(
        worldSpec: WorldProgressionSpec,
        levelId: Int,
        seed: Long,
        attempt: Int = 0
    ): GenerationConfiguration {
        val startLvl = worldSpec.levelRange.first
        val endLvl = worldSpec.levelRange.last
        val span = (endLvl - startLvl).coerceAtLeast(1)
        val offset = (levelId - startLvl).coerceAtLeast(0)
        val t = offset.toDouble() / span

        val cpMin = worldSpec.checkpointRange.first
        val cpMax = worldSpec.checkpointRange.last
        val cpSpan = (cpMax - cpMin).coerceAtLeast(0)
        val baseCp = cpMin + (t * cpSpan).toInt().coerceIn(0, cpSpan)
        // If initial candidate attempts fail to produce unique solution, dynamically vary checkpoint count within world bounds
        val cpVariation = if (cpSpan > 0) attempt % (cpSpan + 1) else 0
        val checkpointCount = (baseCp + cpVariation).coerceIn(cpMin, cpMax)

        val wallRange = resolveWallRangeForLevel(worldSpec, levelId)

        return GenerationConfiguration(
            dimensions = worldSpec.gridDimensions,
            checkpointCount = checkpointCount,
            minWalls = wallRange.first,
            maxWalls = wallRange.last,
            seed = seed,
            requireUniqueness = config.requireUnique,
            puzzleIdPrefix = "zyn_w${worldSpec.worldId}_lvl${levelId}",
            worldId = worldSpec.worldId,
            levelId = levelId,
            routeStyle = RouteStyle.MIXED
        )
    }
}
