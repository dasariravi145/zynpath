package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.generator.PuzzleFingerprint
import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition

/**
 * Metadata descriptor embedded within a serialized puzzle asset.
 */
data class PuzzleAssetMetadata(
    val generatorVersion: String = "1.0.0",
    val generationSeed: Long? = null,
    val difficultyEstimate: Double = 0.0,
    val difficultyBand: String = "BEGINNER",
    val uniquenessStatus: String = "UNIQUE",
    val fingerprint: String? = null
)

/**
 * Immutable data transfer model representing a serialized puzzle asset on disk.
 *
 * Implements Prompt 11 Section 10:
 * Explicit, compact, deterministic representation decoupled from mutable runtime engine state.
 */
data class PuzzleAsset(
    val schemaVersion: String = CatalogVersion.ASSET_SCHEMA_VERSION,
    val puzzleId: String,
    val puzzleVersion: Int = CatalogVersion.DEFAULT_PUZZLE_VERSION,
    val gridDimensions: GridDimensions,
    val requiredCells: Set<GridPosition>,
    val checkpoints: List<NumberedCheckpoint>,
    val blockedEdges: Set<BlockedEdge> = emptySet(),
    val metadata: PuzzleAssetMetadata = PuzzleAssetMetadata()
) {
    /**
     * Converts this asset to an authoritative domain [PuzzleDefinition].
     */
    fun toPuzzleDefinition(): PuzzleDefinition {
        return PuzzleDefinition(
            puzzleId = puzzleId,
            puzzleVersion = puzzleVersion,
            gridDimensions = gridDimensions,
            requiredCells = requiredCells,
            checkpoints = checkpoints.sorted(),
            blockedEdges = blockedEdges,
            difficultyMetadata = metadata.difficultyBand,
            seed = metadata.generationSeed
        )
    }

    companion object {
        fun fromPuzzleDefinition(
            def: PuzzleDefinition,
            metadata: PuzzleAssetMetadata? = null
        ): PuzzleAsset {
            val effectiveMeta = metadata ?: PuzzleAssetMetadata(
                difficultyBand = def.difficultyMetadata ?: "BEGINNER",
                generationSeed = def.seed,
                fingerprint = PuzzleFingerprint.computeSha256(def)
            )
            return PuzzleAsset(
                schemaVersion = CatalogVersion.ASSET_SCHEMA_VERSION,
                puzzleId = def.puzzleId,
                puzzleVersion = def.puzzleVersion,
                gridDimensions = def.gridDimensions,
                requiredCells = def.requiredCells,
                checkpoints = def.checkpoints.sorted(),
                blockedEdges = def.blockedEdges,
                metadata = effectiveMeta
            )
        }
    }
}
