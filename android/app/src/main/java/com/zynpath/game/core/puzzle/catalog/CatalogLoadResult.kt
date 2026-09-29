package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.model.PuzzleDefinition

/**
 * Structured outcome of attempting to load a level's puzzle asset from the catalog.
 *
 * Implements Prompt 11 Sections 6 and 28:
 * Provides explicit, non-throwing diagnostics when assets are missing or corrupted.
 */
sealed interface CatalogLoadResult {

    /**
     * Successfully loaded and validated the puzzle definition.
     */
    data class Success(
        val level: LevelDefinition,
        val definition: PuzzleDefinition
    ) : CatalogLoadResult

    /**
     * The requested level ID does not exist in the catalog manifest.
     */
    data class LevelNotFound(
        val levelId: Int
    ) : CatalogLoadResult

    /**
     * The level exists in progression, but its offline asset file is not yet packaged on disk.
     */
    data class AssetUnavailable(
        val levelId: Int,
        val assetPath: String,
        val message: String
    ) : CatalogLoadResult

    /**
     * The asset was found on disk, but failed JSON parsing, structural invariants, or fingerprint verification.
     */
    data class AssetInvalid(
        val levelId: Int,
        val assetPath: String,
        val error: String
    ) : CatalogLoadResult {
        val errorSummary: String get() = error
    }
}
