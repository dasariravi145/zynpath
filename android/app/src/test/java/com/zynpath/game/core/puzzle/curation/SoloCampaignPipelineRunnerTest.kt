package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.catalog.CatalogManifest
import com.zynpath.game.core.puzzle.catalog.CatalogManifestSerializer
import com.zynpath.game.core.puzzle.catalog.CatalogVersion
import com.zynpath.game.core.puzzle.catalog.LevelDefinition
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetSerializer
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetValidationResult
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetValidator
import com.zynpath.game.core.puzzle.catalog.WorldDefinition
import com.zynpath.game.core.puzzle.generator.PuzzleFingerprint
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.SolverConfiguration
import com.zynpath.game.core.puzzle.solver.SolverStatus
import com.zynpath.game.core.puzzle.solver.UniquenessStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class SoloCampaignPipelineRunnerTest {

    private val baseAssetsDir = File("d:/Zynpath/android/app/src/main/assets/puzzles")
    private val manifestFile = File("d:/Zynpath/android/app/src/main/assets/catalog_manifest.json")

    @Test
    fun batch01_generateWorld1() {
        println("=== Starting Bounded Generation for World 1 (6x6, 0..0 walls, 300 levels) ===")
        val count = SoloCampaignBatchGenerator.generateWorldBatch(worldId = 1, startLevel = 1, endLevel = 300)
        println("=== World 1 Complete! Newly generated: $count levels ===")
        verifyWorldFilesExistAndValid(1, 1..300)
    }

    @Test
    fun batch02_generateWorld2() {
        println("=== Starting Bounded Generation for World 2 (7x7, 0..6 walls, 300 levels) ===")
        val count = SoloCampaignBatchGenerator.generateWorldBatch(worldId = 2, startLevel = 1, endLevel = 300)
        println("=== World 2 Complete! Newly generated: $count levels ===")
        verifyWorldFilesExistAndValid(2, 1..300)
    }

    @Test
    fun batch03_generateWorld3() {
        println("=== Starting Bounded Generation for World 3 (8x8, 0..8 walls, 300 levels) ===")
        val count = SoloCampaignBatchGenerator.generateWorldBatch(worldId = 3, startLevel = 1, endLevel = 300)
        println("=== World 3 Complete! Newly generated: $count levels ===")
        verifyWorldFilesExistAndValid(3, 1..300)
    }

    @Test
    fun batch04_generateWorld4() {
        println("=== Starting Bounded Generation for World 4 (9x9, 0..10 walls, 300 levels) ===")
        val count = SoloCampaignBatchGenerator.generateWorldBatch(worldId = 4, startLevel = 1, endLevel = 300)
        println("=== World 4 Complete! Newly generated: $count levels ===")
        verifyWorldFilesExistAndValid(4, 1..300)
    }

    @Test
    fun batch05_generateWorld5() {
        println("=== Starting Bounded Generation for World 5 (11x11, 0..12 walls, 300 levels) ===")
        val count = SoloCampaignBatchGenerator.generateWorldBatch(worldId = 5, startLevel = 1, endLevel = 300)
        println("=== World 5 Complete! Newly generated: $count levels ===")
        verifyWorldFilesExistAndValid(5, 1..300)
    }

    @Test
    fun batch06a_generateWorld6_part1() {
        println("=== Starting Bounded Generation for World 6 Part 1 (181..220) ===")
        val count = SoloCampaignBatchGenerator.generateWorldBatch(worldId = 6, startLevel = 181, endLevel = 220)
        println("=== World 6 Part 1 Complete! Newly generated: $count levels ===")
        verifyWorldFilesExistAndValid(6, 181..220)
    }

    @Test
    fun batch06b_generateWorld6_part2() {
        println("=== Starting Bounded Generation for World 6 Part 2 (221..260) ===")
        val count = SoloCampaignBatchGenerator.generateWorldBatch(worldId = 6, startLevel = 221, endLevel = 260)
        println("=== World 6 Part 2 Complete! Newly generated: $count levels ===")
        verifyWorldFilesExistAndValid(6, 221..260)
    }

    @Test
    fun batch06c_generateWorld6_part3() {
        println("=== Starting Bounded Generation for World 6 Part 3 (261..300) ===")
        val count = SoloCampaignBatchGenerator.generateWorldBatch(worldId = 6, startLevel = 261, endLevel = 300)
        println("=== World 6 Part 3 Complete! Newly generated: $count levels ===")
        verifyWorldFilesExistAndValid(6, 261..300)
    }

    @Test
    fun batch06_generateWorld6() {
        println("=== Starting Bounded Generation for World 6 (12x12, 0..15 walls, 300 levels) ===")
        val count = SoloCampaignBatchGenerator.generateWorldBatch(worldId = 6, startLevel = 1, endLevel = 300)
        println("=== World 6 Complete! Newly generated: $count levels ===")
        verifyWorldFilesExistAndValid(6, 1..300)
    }

    @Test
    fun buildCatalogManifestAndAudit1800Levels() {
        println("=== Compiling Catalog Manifest for all 1,800 levels ===")
        val allLevels = ArrayList<LevelDefinition>(1800)

        for (w in 1..6) {
            val worldDef = WorldDefinition.forWorld(w)
            for (lvl in 1..300) {
                val file = File(baseAssetsDir, "w$w/lvl$lvl.json")
                assertTrue("File $file must exist", file.exists())
                val json = file.readText(Charsets.UTF_8)
                val asset = PuzzleAssetSerializer.deserialize(json)
                val globalLevel = WorldConfiguration.toGlobalLevel(w, lvl)

                val fp = PuzzleFingerprint.computeSha256(asset.toPuzzleDefinition())
                val levelDef = LevelDefinition(
                    levelId = globalLevel,
                    worldId = w,
                    puzzleId = asset.puzzleId,
                    puzzleVersion = asset.puzzleVersion,
                    assetPath = "puzzles/w$w/lvl$lvl.json",
                    difficultyEstimate = asset.metadata.difficultyEstimate,
                    difficultyBand = DifficultyBand.fromScore(asset.metadata.difficultyEstimate),
                    uniquenessStatus = UniquenessStatus.UNIQUE,
                    fingerprint = fp,
                    hasPackagedAsset = true,
                    displayName = "Level $lvl"
                )
                allLevels.add(levelDef)

                // Validate with PuzzleAssetValidator
                val valRes = PuzzleAssetValidator.validate(json, levelDef, worldDef)
                assertTrue("Asset $file failed validation: ${(valRes as? PuzzleAssetValidationResult.Invalid)?.errors}",
                    valRes is PuzzleAssetValidationResult.Valid)
            }
        }

        assertEquals(1800, allLevels.size)

        // Compute integrity hash across all 1800 level fingerprints
        val integrityBuilder = StringBuilder()
        for (lvl in allLevels) {
            integrityBuilder.append("${lvl.levelId}:${lvl.puzzleId}:${lvl.fingerprint}\n")
        }
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val integrityHash = digest.digest(integrityBuilder.toString().toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }

        val manifest = CatalogManifest(
            catalogId = "zynpath_solo_campaign",
            catalogVersion = "2.0.0",
            schemaVersion = "1.0.0",
            totalPlannedLevels = 1800,
            worlds = WorldDefinition.ALL_WORLDS,
            levels = allLevels,
            integrityHash = integrityHash
        )

        val manifestJson = CatalogManifestSerializer.serialize(manifest)
        manifestFile.writeText(manifestJson, Charsets.UTF_8)
        println("=== Successfully wrote catalog_manifest.json (${manifestFile.length()} bytes, integrityHash=$integrityHash) ===")

        // Prompt 2 Section 12 Verification checkpoints:
        // World 1 Levels 1, 4, 5, 100, 299, 300
        // World 2 Levels 1, 100, 300
        // World 3 Levels 1, 300
        // World 4 Levels 1, 300
        // World 5 Levels 1, 300
        // World 6 Levels 1, 300
        val checkLevels = listOf(
            1 to 1, 1 to 4, 1 to 5, 1 to 100, 1 to 299, 1 to 300,
            2 to 1, 2 to 100, 2 to 300,
            3 to 1, 3 to 300,
            4 to 1, 4 to 300,
            5 to 1, 5 to 300,
            6 to 1, 6 to 300
        )

        val solver = PuzzleSolver()
        for ((w, localLvl) in checkLevels) {
            val file = File(baseAssetsDir, "w$w/lvl$localLvl.json")
            assertTrue("Check level file $file must exist", file.exists())
            val asset = PuzzleAssetSerializer.deserialize(file.readText(Charsets.UTF_8))
            val def = asset.toPuzzleDefinition()
            val solveRes = solver.solve(def, SolverConfiguration.CHECK_UNIQUENESS)
            assertEquals("Check level W$w Lvl$localLvl must be SOLVED", SolverStatus.SOLVED, solveRes.status)
            assertEquals("Check level W$w Lvl$localLvl must be UNIQUE", UniquenessStatus.UNIQUE, solveRes.uniqueness)
            println("Verified Prompt 2 Milestone Level W$w Lvl$localLvl: UNIQUE, CPs=${def.checkpoints.size}, walls=${def.blockedEdges.size}")
        }
        println("=== All Section 12 Milestones Verified 100% ===")
    }

    private fun verifyWorldFilesExistAndValid(worldId: Int, range: IntRange) {
        val worldDef = WorldDefinition.forWorld(worldId)
        for (lvl in range) {
            val file = File(baseAssetsDir, "w$worldId/lvl$lvl.json")
            assertTrue("File $file must exist", file.exists())
            val json = file.readText(Charsets.UTF_8)
            val globalLvl = WorldConfiguration.toGlobalLevel(worldId, lvl)
            val expectedLevel = LevelDefinition(
                levelId = globalLvl,
                worldId = worldId,
                puzzleId = "w${worldId}_lvl$lvl",
                assetPath = "puzzles/w$worldId/lvl$lvl.json",
                difficultyEstimate = 0.5,
                difficultyBand = com.zynpath.game.core.puzzle.curation.DifficultyBand.MEDIUM,
                fingerprint = "",
                hasPackagedAsset = true,
                displayName = "Level $lvl"
            )
            val valRes = PuzzleAssetValidator.validate(json, expectedLevel, worldDef)
            assertTrue("Validation failed on $file: ${(valRes as? PuzzleAssetValidationResult.Invalid)?.errors}",
                valRes is PuzzleAssetValidationResult.Valid)
        }
        println("Verified all ${range.count()} files in World $worldId exist and pass validation.")
    }
}
