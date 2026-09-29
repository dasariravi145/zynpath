package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.curation.DifficultyBand
import com.zynpath.game.core.puzzle.solver.UniquenessStatus

/**
 * Immutable domain definition of a campaign Level in the Zynpath catalog.
 *
 * Implements Prompt 11 Section 9:
 * Binds stable level identifiers to stable, versioned puzzle assets and difficulty metadata.
 */
data class LevelDefinition(
    val levelId: Int,
    val worldId: Int,
    val puzzleId: String,
    val puzzleVersion: Int = CatalogVersion.DEFAULT_PUZZLE_VERSION,
    val assetPath: String,
    val difficultyEstimate: Double,
    val difficultyBand: DifficultyBand,
    val uniquenessStatus: UniquenessStatus = UniquenessStatus.UNIQUE,
    val fingerprint: String,
    val catalogVersion: String = CatalogVersion.CATALOG_VERSION,
    val displayName: String? = null,
    val hasPackagedAsset: Boolean = true
) {
    init {
        require(levelId in 1..300) { "levelId must be between 1 and 300 (got $levelId)" }
        require(worldId in 1..6) { "worldId must be between 1 and 6 (got $worldId)" }
        require(puzzleId.isNotBlank()) { "puzzleId cannot be blank" }
        require(assetPath.isNotBlank()) { "assetPath cannot be blank" }
        require(difficultyEstimate in 0.0..1.0) { "difficultyEstimate must be in [0.0, 1.0]" }
    }
}
