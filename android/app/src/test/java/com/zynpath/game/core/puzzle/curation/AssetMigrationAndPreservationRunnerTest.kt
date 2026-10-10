package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.catalog.LevelDefinition
import com.zynpath.game.core.puzzle.catalog.PuzzleAsset
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetMetadata
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
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class AssetMigrationAndPreservationRunnerTest {

    private val baseAssetsDir = File("d:/Zynpath/android/app/src/main/assets/puzzles")
    private val solver = PuzzleSolver()

    @Test
    fun migrateAndPreserveExistingPuzzles() {
        // Worlds to migrate:
        // World 2: files lvl21..lvl50 -> lvl1..lvl30
        // World 3: files lvl51..lvl100 -> lvl1..lvl50
        // World 4: files lvl101..lvl150 -> lvl1..lvl50
        // World 5: files lvl151..lvl200 -> lvl1..lvl50
        // World 6: files lvl201..lvl300 -> lvl1..lvl100
        val migrations = listOf(
            MigrationPlan(worldId = 2, oldStart = 21, oldEnd = 50, newStart = 1),
            MigrationPlan(worldId = 3, oldStart = 51, oldEnd = 100, newStart = 1),
            MigrationPlan(worldId = 4, oldStart = 101, oldEnd = 150, newStart = 1),
            MigrationPlan(worldId = 5, oldStart = 151, oldEnd = 200, newStart = 1),
            MigrationPlan(worldId = 6, oldStart = 201, oldEnd = 300, newStart = 1)
        )

        var totalMigrated = 0

        for (plan in migrations) {
            val worldDir = File(baseAssetsDir, "w${plan.worldId}")
            assertTrue("World dir ${worldDir.absolutePath} must exist", worldDir.exists())

            val count = plan.oldEnd - plan.oldStart + 1
            for (i in 0 until count) {
                val oldLvl = plan.oldStart + i
                val newLocalLvl = plan.newStart + i
                val oldFile = File(worldDir, "lvl$oldLvl.json")
                val targetFile = File(worldDir, "lvl$newLocalLvl.json")

                if (oldFile.exists()) {
                    val rawJson = oldFile.readText(Charsets.UTF_8)
                    val asset = PuzzleAssetSerializer.deserialize(rawJson)
                    val def = asset.toPuzzleDefinition()

                    // Update puzzleId to local format: w{worldId}_lvl{newLocalLvl}
                    val updatedDef = def.copy(puzzleId = "w${plan.worldId}_lvl$newLocalLvl")
                    val fp = PuzzleFingerprint.computeSha256(updatedDef)
                    val updatedAsset = PuzzleAsset.fromPuzzleDefinition(
                        def = updatedDef,
                        metadata = asset.metadata.copy(
                            fingerprint = fp,
                            uniquenessStatus = "UNIQUE"
                        )
                    )
                    val updatedJson = PuzzleAssetSerializer.serialize(updatedAsset)

                    // Write to new target file
                    targetFile.writeText(updatedJson, Charsets.UTF_8)

                    // If old file was different name, delete old file to clean up
                    if (oldFile.name != targetFile.name) {
                        oldFile.delete()
                    }

                    // Validate with authoritative PuzzleAssetValidator
                    val worldDef = WorldDefinition.forWorld(plan.worldId)
                    val globalLevel = WorldConfiguration.toGlobalLevel(plan.worldId, newLocalLvl)
                    val expectedLevel = LevelDefinition(
                        levelId = globalLevel,
                        worldId = plan.worldId,
                        puzzleId = updatedDef.puzzleId,
                        puzzleVersion = updatedDef.puzzleVersion,
                        assetPath = "puzzles/w${plan.worldId}/lvl$newLocalLvl.json",
                        difficultyEstimate = asset.metadata.difficultyEstimate,
                        difficultyBand = com.zynpath.game.core.puzzle.curation.DifficultyBand.fromScore(asset.metadata.difficultyEstimate),
                        uniquenessStatus = UniquenessStatus.UNIQUE,
                        fingerprint = fp,
                        hasPackagedAsset = false,
                        displayName = "Level $newLocalLvl"
                    )

                    val valResult = PuzzleAssetValidator.validate(updatedJson, expectedLevel, worldDef)
                    if (valResult is PuzzleAssetValidationResult.Invalid) {
                        println("VALIDATION FAILED on $targetFile: ${valResult.errors.joinToString("; ")}")
                    }
                    assertTrue("Asset $targetFile must be valid: ${(valResult as? PuzzleAssetValidationResult.Invalid)?.errors}",
                        valResult is PuzzleAssetValidationResult.Valid)

                    totalMigrated++
                } else if (targetFile.exists()) {
                    // Already migrated in a previous run
                    totalMigrated++
                } else {
                    error("Neither $oldFile nor $targetFile exists!")
                }
            }
            println("World ${plan.worldId}: migrated and preserved $count puzzles successfully.")
        }

        println("Total preserved migrated puzzles: $totalMigrated")
        assertEquals(280, totalMigrated) // 30 + 50 + 50 + 50 + 100 = 280
    }

    private data class MigrationPlan(
        val worldId: Int,
        val oldStart: Int,
        val oldEnd: Int,
        val newStart: Int
    )
}
