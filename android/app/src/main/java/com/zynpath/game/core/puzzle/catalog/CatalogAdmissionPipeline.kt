package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.engine.CompletionCheckResult
import com.zynpath.game.core.puzzle.engine.CompletionValidator
import com.zynpath.game.core.puzzle.validator.PuzzleDefinitionValidator
import com.zynpath.game.core.puzzle.generator.PuzzleFingerprint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.SolverConfiguration
import com.zynpath.game.core.puzzle.solver.UniquenessStatus

/**
 * Result of submitting a candidate puzzle to the authoritative admission pipeline.
 */
sealed class AdmissionResult {
    data class Admitted(
        val levelId: Int,
        val worldId: Int,
        val definition: PuzzleDefinition,
        val asset: PuzzleAsset,
        val json: String,
        val fingerprint: String,
        val uniquenessStatus: UniquenessStatus,
        val assetPath: String
    ) : AdmissionResult()

    data class Rejected(
        val levelId: Int,
        val worldId: Int,
        val reason: String
    ) : AdmissionResult()
}

/**
 * Authoritative 10-step puzzle admission and packaging pipeline.
 *
 * Implements Prompt 11 Section 13 and Section 23:
 * Strict admission gates ensuring no unverified or invalid puzzle is ever published to the catalog.
 */
object CatalogAdmissionPipeline {

    fun admit(
        levelId: Int,
        worldId: Int,
        candidate: PuzzleDefinition,
        expectedUniqueness: UniquenessStatus = UniquenessStatus.UNIQUE,
        difficultyEstimate: Double = 0.1,
        difficultyBand: String = "BEGINNER",
        generatorVersion: String = "1.0.0"
    ): AdmissionResult {
        // Step 1: Validate puzzle structure
        val structValidation = PuzzleDefinitionValidator.validate(candidate)
        if (!structValidation.isValid) {
            val errorMsg = (structValidation as? com.zynpath.game.core.puzzle.validator.DefinitionValidationResult.Invalid)?.errorSummary ?: "Invalid structure"
            return AdmissionResult.Rejected(
                levelId, worldId,
                "Structure invalid: $errorMsg"
            )
        }

        // Step 5 (part 1): Confirm world configuration
        val world = WorldDefinition.forWorld(worldId)
        if (candidate.gridDimensions != world.gridDimensions) {
            return AdmissionResult.Rejected(
                levelId, worldId,
                "Grid dimension mismatch: expected ${world.gridDimensions}, got ${candidate.gridDimensions}"
            )
        }
        if (candidate.checkpoints.size !in world.checkpointRange) {
            return AdmissionResult.Rejected(
                levelId, worldId,
                "Checkpoint count ${candidate.checkpoints.size} outside range ${world.checkpointRange}"
            )
        }
        if (candidate.blockedEdges.size !in world.wallRange) {
            return AdmissionResult.Rejected(
                levelId, worldId,
                "Wall count ${candidate.blockedEdges.size} outside range ${world.wallRange}"
            )
        }

        // Step 2 & 3: Run exact solver and obtain complete valid route
        val solver = PuzzleSolver()
        val solverResult = solver.solve(candidate, SolverConfiguration(maxSolutions = 2))
        if (!solverResult.isSolved || solverResult.solutions.isEmpty()) {
            return AdmissionResult.Rejected(levelId, worldId, "Solver found no valid full-coverage solution")
        }

        // Step 6: Confirm required uniqueness policy
        if (expectedUniqueness == UniquenessStatus.UNIQUE && solverResult.uniqueness != UniquenessStatus.UNIQUE) {
            return AdmissionResult.Rejected(
                levelId, worldId,
                "Uniqueness failure: expected UNIQUE but got ${solverResult.uniqueness}"
            )
        }

        // Step 4: Run CompletionValidator on primary solution
        val primaryPath = solverResult.solutions.first()
        val completionCheck = CompletionValidator.validate(
            definition = candidate,
            path = primaryPath,
            levelId = levelId,
            worldId = worldId
        )
        if (completionCheck !is CompletionCheckResult.Success) {
            val failureReason = (completionCheck as? CompletionCheckResult.Failure)?.reason?.name ?: "Unknown"
            return AdmissionResult.Rejected(levelId, worldId, "Completion validator rejected route: $failureReason")
        }

        // Step 7: Confirm puzzle fingerprint
        val fingerprint = PuzzleFingerprint.computeSha256(candidate)
        if (fingerprint.isBlank()) {
            return AdmissionResult.Rejected(levelId, worldId, "Failed to compute SHA-256 fingerprint")
        }

        // Step 8: Confirm level identity and asset path
        val assetPath = "puzzles/w$worldId/lvl$levelId.json"

        // Step 9 & 10: Serialization & Deserialization round-trip validation
        val asset = PuzzleAsset.fromPuzzleDefinition(
            def = candidate,
            metadata = PuzzleAssetMetadata(
                generatorVersion = generatorVersion,
                generationSeed = candidate.seed,
                difficultyEstimate = difficultyEstimate,
                difficultyBand = difficultyBand,
                uniquenessStatus = solverResult.uniqueness.name,
                fingerprint = fingerprint
            )
        )

        val json: String
        try {
            json = PuzzleAssetSerializer.serialize(asset)
            val deserialized = PuzzleAssetSerializer.deserialize(json)
            val roundTripDef = deserialized.toPuzzleDefinition()

            if (!PuzzleFingerprint.areDuplicates(candidate, roundTripDef)) {
                return AdmissionResult.Rejected(levelId, worldId, "Serialization round-trip mismatch: topology altered")
            }
        } catch (e: Exception) {
            return AdmissionResult.Rejected(levelId, worldId, "Serialization error: ${e.message}")
        }

        return AdmissionResult.Admitted(
            levelId = levelId,
            worldId = worldId,
            definition = candidate,
            asset = asset,
            json = json,
            fingerprint = fingerprint,
            uniquenessStatus = solverResult.uniqueness,
            assetPath = assetPath
        )
    }
}
