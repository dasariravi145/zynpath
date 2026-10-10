package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.catalog.CuratedFirst50Levels
import com.zynpath.game.core.puzzle.catalog.CuratedWorld3Levels
import com.zynpath.game.core.puzzle.catalog.CuratedWorld4Levels
import com.zynpath.game.core.puzzle.catalog.CuratedWorld5Levels
import com.zynpath.game.core.puzzle.catalog.CuratedWorld6Levels
import com.zynpath.game.core.puzzle.catalog.LevelDefinition
import com.zynpath.game.core.puzzle.catalog.PuzzleAsset
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetMetadata
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetSerializer
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetValidationResult
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetValidator
import com.zynpath.game.core.puzzle.catalog.WorldDefinition
import com.zynpath.game.core.puzzle.generator.PuzzleFingerprint
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import com.zynpath.game.core.puzzle.solver.UniquenessStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class SoloCampaignPreservationTest {

    private val baseAssetsDir = File("d:/Zynpath/android/app/src/main/assets/puzzles")

    @Test
    fun writeAndValidateAll300PreservedLevels() {
        // Step 1: Clean up any old non-canonical files in w2..w6 (e.g. lvl21.json..lvl300.json)
        for (w in 2..6) {
            val dir = File(baseAssetsDir, "w$w")
            if (dir.exists()) {
                val oldOffsetThreshold = when (w) {
                    2 -> 31 // old files were 21..50
                    3 -> 51 // old files were 51..100
                    4 -> 51 // old files were 101..150
                    5 -> 51 // old files were 151..200
                    6 -> 101 // old files were 201..300
                    else -> 301
                }
                dir.listFiles()?.forEach { file ->
                    val num = file.name.removePrefix("lvl").removeSuffix(".json").toIntOrNull()
                    if (num != null && num >= oldOffsetThreshold) {
                        file.delete()
                    }
                }
            }
        }

        // Map of (WorldId, LocalLevel) -> Curated PuzzleDefinition
        val preservedMap = LinkedHashMap<Pair<Int, Int>, com.zynpath.game.core.puzzle.model.PuzzleDefinition>()

        // World 1: 1..20
        for (i in 1..20) {
            preservedMap[1 to i] = CuratedFirst50Levels.LEVELS.getValue(i)
        }
        // World 2: 1..30 (from curated 21..50)
        for (i in 1..30) {
            preservedMap[2 to i] = CuratedFirst50Levels.LEVELS.getValue(20 + i)
        }
        // World 3: 1..50 (from curated 51..100)
        for (i in 1..50) {
            preservedMap[3 to i] = CuratedWorld3Levels.LEVELS.getValue(50 + i)
        }
        // World 4: 1..50 (from curated 101..150)
        for (i in 1..50) {
            preservedMap[4 to i] = CuratedWorld4Levels.LEVELS.getValue(100 + i)
        }
        // World 5: 1..50 (from curated 151..200)
        for (i in 1..50) {
            preservedMap[5 to i] = CuratedWorld5Levels.LEVELS.getValue(150 + i)
        }
        // World 6: 1..100 (from curated 201..300)
        for (i in 1..100) {
            preservedMap[6 to i] = CuratedWorld6Levels.LEVELS.getValue(200 + i)
        }

        assertEquals(300, preservedMap.size)

        var totalValidated = 0

        for ((coord, def) in preservedMap) {
            val (worldId, localLevel) = coord
            val worldDir = File(baseAssetsDir, "w$worldId")
            worldDir.mkdirs()
            val targetFile = File(worldDir, "lvl$localLevel.json")

            val targetPuzzleId = "w${worldId}_lvl$localLevel"
            val updatedDef = def.copy(puzzleId = targetPuzzleId)
            val fp = PuzzleFingerprint.computeSha256(updatedDef)

            val score = when (worldId) {
                1 -> 0.10 + (localLevel - 1) * 0.015
                2 -> 0.35 + (localLevel - 1) * 0.010
                3 -> 0.55 + (localLevel - 1) * 0.005
                4 -> 0.70 + (localLevel - 1) * 0.004
                5 -> 0.85 + (localLevel - 1) * 0.002
                else -> 0.92 + (localLevel - 1) * 0.0007
            }.coerceIn(0.0, 1.0)
            val band = com.zynpath.game.core.puzzle.curation.DifficultyBand.fromScore(score)

            val asset = PuzzleAsset.fromPuzzleDefinition(
                def = updatedDef,
                metadata = PuzzleAssetMetadata(
                    generatorVersion = "2.0.0",
                    generationSeed = updatedDef.seed,
                    difficultyEstimate = score,
                    difficultyBand = band.name,
                    uniquenessStatus = "UNIQUE",
                    fingerprint = fp
                )
            )

            val json = PuzzleAssetSerializer.serialize(asset)
            targetFile.writeText(json, Charsets.UTF_8)

            // Validate with PuzzleAssetValidator
            val worldDef = WorldDefinition.forWorld(worldId)
            val globalLevel = WorldConfiguration.toGlobalLevel(worldId, localLevel)
            val expectedLevel = LevelDefinition(
                levelId = globalLevel,
                worldId = worldId,
                puzzleId = targetPuzzleId,
                puzzleVersion = updatedDef.puzzleVersion,
                assetPath = "puzzles/w$worldId/lvl$localLevel.json",
                difficultyEstimate = score,
                difficultyBand = band,
                uniquenessStatus = UniquenessStatus.UNIQUE,
                fingerprint = fp,
                hasPackagedAsset = true,
                displayName = "Level $localLevel"
            )

            val valResult = PuzzleAssetValidator.validate(json, expectedLevel, worldDef)
            if (valResult is PuzzleAssetValidationResult.Invalid) {
                println(">>> FAILED ON W$worldId Lvl$localLevel: ${valResult.errors.joinToString("; ")}")
                org.junit.Assert.fail("Level W$worldId Lvl$localLevel failed: ${valResult.errors}")
            }
            totalValidated++
        }

        println("Successfully wrote and validated all $totalValidated preserved level assets!")
        assertEquals(300, totalValidated)
    }
}
