package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.generator.PuzzleFingerprint
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import com.zynpath.game.core.puzzle.solver.SolutionValidationResult
import com.zynpath.game.core.puzzle.solver.SolutionValidator
import com.zynpath.game.core.puzzle.solver.SolverResult
import com.zynpath.game.core.puzzle.solver.UniquenessStatus
import com.zynpath.game.core.puzzle.validator.DefinitionValidationResult
import com.zynpath.game.core.puzzle.validator.PuzzleDefinitionValidator
import kotlin.math.abs

/**
 * Authoritative quality evaluation gate for candidate puzzles.
 *
 * Implements Prompt 10 Sections 19, 20, and 21:
 * - Decouples mathematical solvability from gameplay progression quality.
 * - Enforces rigorous hard rejection rules (structural integrity, solvability, completion invariants,
 *   uniqueness policy, world boundary constraints, duplicate detection).
 * - Computes multi-signal soft quality metrics (route variation, checkpoint spacing, choice richness,
 *   wall relevance, progression fit) to rank and curate candidate batches.
 */
object PuzzleQualityEvaluator {

    /**
     * Criteria and bounds for evaluating puzzle candidate quality.
     */
    data class QualityCriteria(
        val expectedDimensions: GridDimensions? = null,
        val checkpointRange: IntRange? = null,
        val wallRange: IntRange? = null,
        val requireUnique: Boolean = true,
        val targetDifficultyRange: ClosedFloatingPointRange<Double>? = null,
        val targetDifficultyBand: DifficultyBand? = null,
        val maxAllowedSimilarity: Double = 0.85,
        val rejectSymmetricDuplicates: Boolean = true
    )

    /**
     * Evaluates a candidate puzzle definition against quality criteria and existing catalog state.
     *
     * @param definition The candidate puzzle definition.
     * @param solution The verified candidate solution path.
     * @param solverResult The complete solver result containing uniqueness proof and search metrics.
     * @param difficulty The difficulty analysis result for the candidate.
     * @param criteria Target quality bounds, world constraints, and uniqueness policy.
     * @param existingLevels Existing accepted puzzle definitions in the current curation batch or catalog.
     * @return [PuzzleQualityResult] with acceptance status, hard rejection diagnostics, and soft scores.
     */
    fun evaluate(
        definition: PuzzleDefinition,
        solution: PuzzlePath,
        solverResult: SolverResult,
        difficulty: DifficultyAnalysisResult,
        criteria: QualityCriteria = QualityCriteria(),
        existingLevels: List<PuzzleDefinition> = emptyList()
    ): PuzzleQualityResult {
        val rejectionReasons = ArrayList<QualityRejectionReason>()
        val rejectionDetails = ArrayList<String>()

        // 1. Structural validity check
        val validation = PuzzleDefinitionValidator.validate(definition)
        if (validation is DefinitionValidationResult.Invalid) {
            rejectionReasons.add(QualityRejectionReason.STRUCTURAL_INVALIDITY)
            rejectionDetails.add("Definition validation failed: ${validation.errorSummary}")
        }

        // 2. Solvability check
        if (!solverResult.isSolved) {
            rejectionReasons.add(QualityRejectionReason.UNSOLVABLE)
            rejectionDetails.add("Solver found no valid solutions: ${solverResult.diagnosticMessage}")
        }

        // 3. Completion invariant validation
        val completionCheck = SolutionValidator.validate(definition, solution)
        if (completionCheck !is SolutionValidationResult.Valid) {
            rejectionReasons.add(QualityRejectionReason.FAILED_COMPLETION_VALIDATION)
            rejectionDetails.add("Solution path failed CompletionValidator: $completionCheck")
        }

        // 4. Inconclusive solver verification
        if (!solverResult.isExhaustive && criteria.requireUnique) {
            rejectionReasons.add(QualityRejectionReason.INCONCLUSIVE_VERIFICATION)
            rejectionDetails.add("Solver search was not exhaustive (budget or timeout exceeded)")
        }

        // 5. Uniqueness requirement
        if (criteria.requireUnique && solverResult.uniqueness != UniquenessStatus.UNIQUE) {
            rejectionReasons.add(QualityRejectionReason.UNIQUENESS_NOT_PROVEN)
            rejectionDetails.add("Puzzle uniqueness is ${solverResult.uniqueness} (expected UNIQUE)")
        }

        // 6. World dimension constraints
        if (criteria.expectedDimensions != null && definition.gridDimensions != criteria.expectedDimensions) {
            rejectionReasons.add(QualityRejectionReason.GRID_DIMENSION_MISMATCH)
            rejectionDetails.add(
                "Grid dimensions ${definition.gridDimensions} do not match expected ${criteria.expectedDimensions}"
            )
        }

        // 7. Checkpoint count constraints
        if (criteria.checkpointRange != null && definition.checkpoints.size !in criteria.checkpointRange) {
            rejectionReasons.add(QualityRejectionReason.CHECKPOINT_COUNT_OUT_OF_RANGE)
            rejectionDetails.add(
                "Checkpoint count ${definition.checkpoints.size} outside allowed range ${criteria.checkpointRange}"
            )
        }

        // 8. Wall count constraints
        if (criteria.wallRange != null && definition.blockedEdges.size !in criteria.wallRange) {
            rejectionReasons.add(QualityRejectionReason.WALL_COUNT_OUT_OF_RANGE)
            rejectionDetails.add(
                "Wall count ${definition.blockedEdges.size} outside allowed range ${criteria.wallRange}"
            )
        }

        // 9. Exact and symmetric duplicate detection
        val candidateHash = PuzzleFingerprint.computeSha256(definition)
        for (existing in existingLevels) {
            if (PuzzleFingerprint.computeSha256(existing) == candidateHash) {
                rejectionReasons.add(QualityRejectionReason.EXACT_DUPLICATE)
                rejectionDetails.add("Identical SHA-256 fingerprint to existing level ${existing.puzzleId}")
                break
            } else if (criteria.rejectSymmetricDuplicates &&
                PuzzleSimilarityCalculator.isSymmetricDuplicate(definition, existing)) {
                rejectionReasons.add(QualityRejectionReason.EXACT_DUPLICATE)
                rejectionDetails.add("Symmetric geometric duplicate of existing level ${existing.puzzleId}")
                break
            }
        }

        // 10. Excessive similarity check against existing levels
        if (rejectionReasons.isEmpty() && existingLevels.isNotEmpty()) {
            var highestSim = 0.0
            var mostSimilarId = ""
            for (existing in existingLevels) {
                val sim = PuzzleSimilarityCalculator.calculateSimilarity(definition, existing, considerSymmetries = true)
                if (sim > highestSim) {
                    highestSim = sim
                    mostSimilarId = existing.puzzleId
                }
            }
            if (highestSim > criteria.maxAllowedSimilarity) {
                rejectionReasons.add(QualityRejectionReason.EXCESSIVE_SIMILARITY)
                rejectionDetails.add(
                    "Similarity to level $mostSimilarId is ${String.format(java.util.Locale.US, "%.2f", highestSim)} " +
                    "(exceeds threshold ${criteria.maxAllowedSimilarity})"
                )
            }
        }

        // 11. Difficulty band / range check
        if (criteria.targetDifficultyRange != null &&
            difficulty.estimatedScore !in criteria.targetDifficultyRange) {
            rejectionReasons.add(QualityRejectionReason.DIFFICULTY_OUT_OF_RANGE)
            rejectionDetails.add(
                "Estimated difficulty score ${difficulty.estimatedScore} outside target range ${criteria.targetDifficultyRange}"
            )
        } else if (criteria.targetDifficultyBand != null &&
            difficulty.difficultyBand != criteria.targetDifficultyBand) {
            rejectionReasons.add(QualityRejectionReason.DIFFICULTY_OUT_OF_RANGE)
            rejectionDetails.add(
                "Estimated difficulty band ${difficulty.difficultyBand} does not match target ${criteria.targetDifficultyBand}"
            )
        }

        // If hard rejection occurred, return failure immediately
        if (rejectionReasons.isNotEmpty()) {
            return PuzzleQualityResult.rejected(
                puzzleId = definition.puzzleId,
                reasons = rejectionReasons,
                details = rejectionDetails
            )
        }

        // Compute Soft Quality Signals (Section 21)
        val softSignals = computeSoftSignals(definition, difficulty, criteria)
        val softScore = softSignals.values.average()

        return PuzzleQualityResult.accepted(
            puzzleId = definition.puzzleId,
            softQualityScore = softScore,
            softSignals = softSignals
        )
    }

    /**
     * Evaluates soft signals: route variation, checkpoint spacing, choice richness,
     * wall relevance, and progression fit.
     */
    private fun computeSoftSignals(
        definition: PuzzleDefinition,
        difficulty: DifficultyAnalysisResult,
        criteria: QualityCriteria
    ): Map<String, Double> {
        val m = difficulty.metrics

        // 1. Route Variation: Ideal turn frequency is in 0.40 .. 0.70 (interesting labyrinth)
        val turnFreq = m.turnFrequency
        val routeVariation = when {
            turnFreq in 0.40..0.70 -> 1.0
            turnFreq < 0.40 -> (turnFreq / 0.40).coerceIn(0.2, 1.0)
            else -> (1.0 - (turnFreq - 0.70) * 2.0).coerceIn(0.2, 1.0)
        }

        // 2. Checkpoint Spacing: Penalize extreme gap variances
        val gapVariance = m.checkpointGapVariance
        val checkpointSpacing = (1.0 / (1.0 + gapVariance * 0.1)).coerceIn(0.1, 1.0)

        // 3. Choice Richness: Cells with branching options (degree >= 3) give real choices
        val branchRatio = if (m.totalRequiredCells > 0) {
            m.branchCellCount.toDouble() / m.totalRequiredCells
        } else {
            0.0
        }
        val choiceRichness = (branchRatio * 2.0).coerceIn(0.1, 1.0)

        // 4. Wall Relevance: If walls exist, ensure they constrain cells along meaningful paths
        val wallRelevance = if (definition.blockedEdges.isEmpty()) {
            1.0
        } else {
            (m.wallConstrainedCells.toDouble() / (definition.blockedEdges.size * 2)).coerceIn(0.3, 1.0)
        }

        // 5. Progression Fit: Proximity to target difficulty midpoint
        val progressionFit = if (criteria.targetDifficultyRange != null) {
            val mid = (criteria.targetDifficultyRange.start + criteria.targetDifficultyRange.endInclusive) / 2.0
            val dist = abs(difficulty.estimatedScore - mid)
            (1.0 - dist * 2.0).coerceIn(0.0, 1.0)
        } else {
            1.0
        }

        return mapOf(
            "routeVariation" to routeVariation,
            "checkpointSpacing" to checkpointSpacing,
            "choiceRichness" to choiceRichness,
            "wallRelevance" to wallRelevance,
            "progressionFit" to progressionFit
        )
    }
}
