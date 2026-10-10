package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.generator.PuzzleFingerprint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.policy.PuzzleConstraintPolicy
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
 * Implements Prompt 11 Section 15 and Systemic Puzzle Asset Integrity Fix:
 * Validates schema, parsing, structural invariants, fingerprint integrity,
 * authoritative [PuzzleConstraintPolicy] bounds (wall counts, checkpoint ranges, grid dimensions),
 * full cell coverage (zero blocked cells), and wall topology (anti-cage invariants).
 */
object PuzzleAssetValidator {

    fun validate(
        json: String,
        expectedLevel: LevelDefinition? = null,
        expectedWorld: WorldDefinition? = null,
        policyOverride: PuzzleConstraintPolicy? = null
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

        // 6. Authoritative Policy Verification (Dimensions, Checkpoints, Walls, Anti-Cage Topology, Full Coverage)
        val inferredLevelId = expectedLevel?.levelId ?: run {
            val match = Regex("""(?:w(\d+)_)?lvl(\d+)""").find(asset.puzzleId)
            val w = match?.groupValues?.get(1)?.toIntOrNull()
            val num = match?.groupValues?.get(2)?.toIntOrNull()
            if (w != null && num != null) {
                com.zynpath.game.core.puzzle.model.WorldConfiguration.toGlobalLevel(w, num)
            } else {
                num
            }
        }
        val resolvedWorld = expectedWorld
            ?: inferredLevelId?.let { WorldDefinition.forLevel(it) }

        val policy = policyOverride
            ?: inferredLevelId?.let { PuzzleConstraintPolicy.forSoloLevel(it) }
            ?: resolvedWorld?.let { PuzzleConstraintPolicy.forWorld(it.worldId) }

        if (policy != null) {
            val policyErrors = policy.validate(definition)
            errors.addAll(policyErrors)
        } else if (resolvedWorld != null) {
            val effectiveWallRange = if (resolvedWorld.hasWalls && resolvedWorld.wallRange == 0..0) {
                // Never use default 0..0 wall range when a level belongs to a world that permits walls
                PuzzleConstraintPolicy.forWorld(resolvedWorld.worldId).allowedWallCountRange
            } else {
                resolvedWorld.wallRange
            }
            if (asset.gridDimensions != resolvedWorld.gridDimensions) {
                errors.add("Dimension mismatch: asset is ${asset.gridDimensions} but world requires ${resolvedWorld.gridDimensions}")
            }
            if (asset.checkpoints.size !in resolvedWorld.checkpointRange) {
                errors.add("Checkpoint count ${asset.checkpoints.size} outside world bounds ${resolvedWorld.checkpointRange}")
            }
            if (asset.blockedEdges.size !in effectiveWallRange) {
                errors.add("Wall count ${asset.blockedEdges.size} outside world bounds $effectiveWallRange")
            }
        }

        return if (errors.isEmpty()) {
            PuzzleAssetValidationResult.Valid(asset, definition)
        } else {
            PuzzleAssetValidationResult.Invalid(errors)
        }
    }
}
