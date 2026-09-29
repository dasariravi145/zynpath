package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.generator.PuzzleFingerprint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.validator.DefinitionValidationResult
import com.zynpath.game.core.puzzle.validator.PuzzleDefinitionValidator

/**
 * Result of validating a puzzle asset.
 */
sealed interface PuzzleAssetValidationResult {
    data class Valid(
        val asset: PuzzleAsset,
        val definition: PuzzleDefinition
    ) : PuzzleAssetValidationResult

    data class Invalid(
        val errors: List<String>
    ) : PuzzleAssetValidationResult {
        val errorSummary: String
            get() = errors.joinToString("; ")
    }
}

/**
 * Authoritative validator for offline puzzle assets.
 *
 * Implements Prompt 11 Section 15:
 * Validates schema, parsing, structural invariants, fingerprint integrity, and world bounds.
 */
object PuzzleAssetValidator {

    fun validate(
        json: String,
        expectedLevel: LevelDefinition? = null,
        expectedWorld: WorldDefinition? = null
    ): PuzzleAssetValidationResult {
        val errors = ArrayList<String>()

        // 1. Parse JSON
        val asset = try {
            PuzzleAssetSerializer.deserialize(json)
        } catch (e: Exception) {
            return PuzzleAssetValidationResult.Invalid(
                listOf("Asset JSON parsing failed: ${e.message ?: e.javaClass.simpleName}")
            )
        }

        // 2. Schema version check
        if (asset.schemaVersion != CatalogVersion.ASSET_SCHEMA_VERSION) {
            errors.add("Unsupported asset schema version '${asset.schemaVersion}' (expected '${CatalogVersion.ASSET_SCHEMA_VERSION}')")
        }

        // 3. Convert to domain definition
        val definition = asset.toPuzzleDefinition()

        // 4. Structural validation using authoritative PuzzleDefinitionValidator
        val structuralCheck = PuzzleDefinitionValidator.validate(definition)
        if (structuralCheck is DefinitionValidationResult.Invalid) {
            errors.add("Structural invariant violation: ${structuralCheck.errorSummary}")
        }

        // 5. Match against expected LevelDefinition if provided
        if (expectedLevel != null) {
            if (asset.puzzleId != expectedLevel.puzzleId && !expectedLevel.puzzleId.startsWith("pending_")) {
                errors.add("Puzzle ID mismatch: asset has '${asset.puzzleId}' but manifest expected '${expectedLevel.puzzleId}'")
            }
            if (asset.puzzleVersion != expectedLevel.puzzleVersion) {
                errors.add("Puzzle version mismatch: asset has ${asset.puzzleVersion} but manifest expected ${expectedLevel.puzzleVersion}")
            }
            val actualFingerprint = PuzzleFingerprint.computeSha256(definition)
            if (expectedLevel.fingerprint.isNotBlank() && expectedLevel.fingerprint != actualFingerprint) {
                errors.add("fingerprint mismatch: asset hash $actualFingerprint does not match manifest hash ${expectedLevel.fingerprint}")
            }
        }

        // 6. Match against expected WorldDefinition if provided
        if (expectedWorld != null) {
            if (asset.gridDimensions != expectedWorld.gridDimensions) {
                errors.add("Dimension mismatch: asset is ${asset.gridDimensions} but world requires ${expectedWorld.gridDimensions}")
            }
            if (asset.checkpoints.size !in expectedWorld.checkpointRange) {
                errors.add("Checkpoint count ${asset.checkpoints.size} outside world bounds ${expectedWorld.checkpointRange}")
            }
            if (asset.blockedEdges.size !in expectedWorld.wallRange) {
                errors.add("Wall count ${asset.blockedEdges.size} outside world bounds ${expectedWorld.wallRange}")
            }
        }

        return if (errors.isEmpty()) {
            PuzzleAssetValidationResult.Valid(asset, definition)
        } else {
            PuzzleAssetValidationResult.Invalid(errors)
        }
    }
}
