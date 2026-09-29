package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.generator.PuzzleFingerprint

/**
 * Generator and provider for packaged offline puzzle assets and catalog manifest.
 *
 * Implements Prompt 11 Section 10, 11, 22, and 23:
 * Provides deterministic serialization of pre-admitted, solver-verified puzzle definitions
 * for runtime offline loading and automated testing.
 */
object CatalogAssetGenerator {

    fun generateAssetJson(levelId: Int): String? {
        val def = PackagedPuzzles.ALL_PACKAGED[levelId] ?: return null
        val fp = PuzzleFingerprint.computeSha256(def)
        val asset = PuzzleAsset.fromPuzzleDefinition(
            def = def,
            metadata = PuzzleAssetMetadata(
                generatorVersion = "1.0.0",
                generationSeed = def.seed,
                difficultyEstimate = when (def.gridDimensions.rows) {
                    4 -> 0.10 + (levelId - 1) * 0.01
                    5 -> if (def.hasWalls) 0.40 + (levelId - 51) * 0.01 else 0.25 + (levelId - 21) * 0.01
                    else -> 0.50
                },
                difficultyBand = def.difficultyMetadata ?: "BEGINNER",
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
        for ((levelId, _) in PackagedPuzzles.ALL_PACKAGED) {
            val world = WorldDefinition.forLevel(levelId)
            val path = "puzzles/w${world.worldId}/lvl$levelId.json"
            val json = generateAssetJson(levelId)
            if (json != null) {
                loader.putAsset(path, json)
            }
        }
    }
}
