package com.zynpath.game.core.puzzle.generator

import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import com.zynpath.game.core.puzzle.solver.UniquenessStatus

/**
 * Authoritative, immutable outcome of a puzzle generation attempt.
 *
 * Implements Prompt 9 Sections 27 and 28:
 * - Structured generation status ([GenerationStatus]).
 * - Accepted [PuzzleDefinition] when generation succeeds.
 * - Verified complete legal solution ([PuzzlePath]) verified by the solver and completion validator.
 * - Seed, generator version, and uniqueness classification.
 * - Comprehensive execution statistics and diagnostic messaging.
 * - Canonical fingerprint for duplicate detection and catalog indexing.
 */
data class GenerationResult(
    val status: GenerationStatus,
    val puzzle: PuzzleDefinition? = null,
    val verifiedSolution: PuzzlePath? = null,
    val seed: Long,
    val generatorVersion: String = GenerationConfiguration.GENERATOR_VERSION,
    val uniquenessStatus: UniquenessStatus = UniquenessStatus.UNKNOWN,
    val statistics: GenerationStatistics = GenerationStatistics.EMPTY,
    val diagnosticMessage: String? = null,
    val canonicalFingerprint: String? = null
) {
    /**
     * True if a structurally valid, solver-verified puzzle was successfully produced.
     */
    val isSuccess: Boolean
        get() = status == GenerationStatus.GENERATED && puzzle != null && verifiedSolution != null

    companion object {
        fun invalidConfiguration(
            seed: Long,
            reason: String,
            generatorVersion: String = GenerationConfiguration.GENERATOR_VERSION
        ): GenerationResult = GenerationResult(
            status = GenerationStatus.INVALID_CONFIGURATION,
            seed = seed,
            generatorVersion = generatorVersion,
            diagnosticMessage = reason
        )

        fun noValidCandidate(
            seed: Long,
            statistics: GenerationStatistics,
            reason: String,
            generatorVersion: String = GenerationConfiguration.GENERATOR_VERSION
        ): GenerationResult = GenerationResult(
            status = GenerationStatus.NO_VALID_CANDIDATE,
            seed = seed,
            generatorVersion = generatorVersion,
            statistics = statistics,
            diagnosticMessage = reason
        )

        fun cancelled(
            seed: Long,
            statistics: GenerationStatistics,
            generatorVersion: String = GenerationConfiguration.GENERATOR_VERSION
        ): GenerationResult = GenerationResult(
            status = GenerationStatus.CANCELLED,
            seed = seed,
            generatorVersion = generatorVersion,
            statistics = statistics,
            diagnosticMessage = "Generation was cooperatively cancelled"
        )

        fun resourceLimitReached(
            seed: Long,
            statistics: GenerationStatistics,
            reason: String,
            generatorVersion: String = GenerationConfiguration.GENERATOR_VERSION
        ): GenerationResult = GenerationResult(
            status = GenerationStatus.RESOURCE_LIMIT_REACHED,
            seed = seed,
            generatorVersion = generatorVersion,
            statistics = statistics,
            diagnosticMessage = reason
        )

        fun solverInconclusive(
            seed: Long,
            statistics: GenerationStatistics,
            reason: String,
            generatorVersion: String = GenerationConfiguration.GENERATOR_VERSION
        ): GenerationResult = GenerationResult(
            status = GenerationStatus.SOLVER_INCONCLUSIVE,
            seed = seed,
            generatorVersion = generatorVersion,
            statistics = statistics,
            diagnosticMessage = reason
        )

        fun internalError(
            seed: Long,
            statistics: GenerationStatistics,
            error: Throwable,
            generatorVersion: String = GenerationConfiguration.GENERATOR_VERSION
        ): GenerationResult = GenerationResult(
            status = GenerationStatus.INTERNAL_ERROR,
            seed = seed,
            generatorVersion = generatorVersion,
            statistics = statistics,
            diagnosticMessage = "Internal error: ${error.message ?: error.javaClass.simpleName}"
        )
    }
}
