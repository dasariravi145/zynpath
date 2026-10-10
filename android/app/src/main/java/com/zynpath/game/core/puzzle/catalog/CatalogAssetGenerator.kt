package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.generator.PuzzleFingerprint
import com.zynpath.game.core.puzzle.model.WorldConfiguration

/**
 * Generator and provider for packaged offline puzzle assets and catalog manifest.
 *
 * Implements Prompt 11 Section 10, 11, 22, and 23:
 * Provides deterministic serialization of pre-admitted, solver-verified puzzle definitions
 * for runtime offline loading and automated testing.
 */
object CatalogAssetGenerator {
    fun generateAssetJson(levelId: Int): String? {
        val def = PackagedPuzzles.ALL_PACKAGED[levelId]
            ?: CuratedFirst50Levels.LEVELS[levelId]
            ?: CuratedWorld3Levels.LEVELS[levelId]
            ?: CuratedWorld4Levels.LEVELS[levelId]
            ?: CuratedWorld5Levels.LEVELS[levelId]
            ?: CuratedWorld6Levels.LEVELS[levelId]
            ?: CuratedAnchorLevels.ANCHOR_LEVELS[levelId]
            ?: return null
        val fp = PuzzleFingerprint.computeSha256(def)
        val world = WorldDefinition.forLevel(levelId)
        val parsedScore = def.difficultyMetadata?.let { Regex("score=([0-9.]+)").find(it)?.groupValues?.get(1)?.toDoubleOrNull() }
        val score = (parsedScore ?: when {
            levelId <= 4 -> 0.10 + (levelId - 1) * 0.01
            def.gridDimensions.rows == 4 -> 0.45
            def.gridDimensions.rows == 5 && !def.hasWalls -> 0.68
            def.gridDimensions.rows == 5 && def.hasWalls -> 0.72
            def.gridDimensions.rows == 6 -> 0.78
            else -> 0.85
        }).coerceIn(0.0, 1.0)
        val band = com.zynpath.game.core.puzzle.curation.DifficultyBand.fromScore(score).name
        val asset = PuzzleAsset.fromPuzzleDefinition(
            def = def,
            metadata = PuzzleAssetMetadata(
                generatorVersion = "1.0.0",
                generationSeed = def.seed,
                difficultyEstimate = score,
                difficultyBand = band,
                uniquenessStatus = "UNIQUE",
                fingerprint = fp
            )
        )
        return PuzzleAssetSerializer.serialize(asset)
    }

    fun generateManifestJson(): String {
        return CatalogManifestSerializer.serialize(CatalogManifest.createDefaultManifest())
    }

    fun populateLoader(loader: InMemoryAssetLoader) {
        loader.putAsset(CatalogManifest.DEFAULT_MANIFEST_PATH, generateManifestJson())
        for (levelId in 1..WorldConfiguration.TOTAL_LEVELS) {
            val json = generateAssetJson(levelId)
            if (json != null) {
                val world = WorldDefinition.forLevel(levelId)
                val localLvl = com.zynpath.game.core.puzzle.model.WorldConfiguration.toLocalLevel(levelId)
                val path = "puzzles/w${world.worldId}/lvl$localLvl.json"
                loader.putAsset(path, json)
            }
        }
    }
}
