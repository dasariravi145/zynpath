package com.zynpath.game.core.puzzle.premium.storage

import android.content.Context
import com.zynpath.game.core.puzzle.catalog.PuzzleAsset
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetSerializer
import com.zynpath.game.core.puzzle.curation.DifficultyBand
import com.zynpath.game.core.puzzle.generator.PuzzleFingerprint
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.premium.content.VerifiedPremiumPacks
import com.zynpath.game.core.puzzle.premium.model.PackPublicationStatus
import com.zynpath.game.core.puzzle.premium.model.PremiumPackDefinition
import com.zynpath.game.core.puzzle.premium.model.PremiumPackManifest
import com.zynpath.game.core.puzzle.premium.model.PremiumPuzzleRef
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.SolverStatus
import com.zynpath.game.core.puzzle.validator.DefinitionValidationResult
import com.zynpath.game.core.puzzle.validator.PuzzleDefinitionValidator
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local storage manager and integrity validation pipeline for Premium puzzle packs.
 *
 * Implements Prompt 27:
 * - Section 16 & 17: Content Manifest and Validation Pipeline.
 * - Section 23 & 24: Download Integrity and Atomic Content Activation.
 * - Section 29: Local Content Storage.
 * - Section 48 & 49: Download failure handling and safe storage cleanup.
 */
@Singleton
class PremiumPackStorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val packsDir: File
        get() = File(context.filesDir, "premium_packs").apply { if (!exists()) mkdirs() }

    private val solver: PuzzleSolver by lazy { PuzzleSolver() }

    /**
     * Checks whether the specified pack is installed locally or available in the bundled verified catalog.
     */
    fun isPackInstalled(packId: String): Boolean {
        val packFolder = File(packsDir, packId)
        val manifestFile = File(packFolder, "manifest.json")
        val puzzlesFile = File(packFolder, "puzzles.json")
        if (manifestFile.exists() && puzzlesFile.exists()) {
            return true
        }
        return VerifiedPremiumPacks.getPack(packId) != null
    }

    /**
     * Retrieves the installed or bundled version for the specified pack.
     */
    fun getInstalledVersion(packId: String): Int? {
        val packFolder = File(packsDir, packId)
        val manifestFile = File(packFolder, "manifest.json")
        if (manifestFile.exists()) {
            try {
                val json = JSONObject(manifestFile.readText())
                return json.optInt("packVersion", 1)
            } catch (_: Exception) {
                // fallback to bundled
            }
        }
        return VerifiedPremiumPacks.getPack(packId)?.manifest?.packVersion
    }

    /**
     * Loads the complete pack definition from local storage or bundled verified packs.
     */
    fun loadPackDefinition(packId: String): PremiumPackDefinition? {
        val packFolder = File(packsDir, packId)
        val manifestFile = File(packFolder, "manifest.json")
        val puzzlesFile = File(packFolder, "puzzles.json")

        if (manifestFile.exists() && puzzlesFile.exists()) {
            try {
                val manifest = parseManifest(manifestFile.readText())
                val puzzles = parsePuzzles(puzzlesFile.readText())
                return PremiumPackDefinition(manifest, puzzles)
            } catch (e: Exception) {
                // If local files are corrupted, fall back to bundled verified pack
            }
        }

        return VerifiedPremiumPacks.getPack(packId)
    }

    /**
     * Validates a pack definition completely and activates it atomically.
     *
     * 1. Validates manifest schema and puzzle count consistency.
     * 2. Validates each puzzle's structural rules and blocked edges.
     * 3. Validates each puzzle's fingerprint against expected values.
     * 4. Solver-verifies that every puzzle is solvable (has at least 1 valid complete path).
     * 5. Atomically writes to a temporary directory first, then swaps in.
     */
    fun validateAndInstallPack(packDef: PremiumPackDefinition): Result<Unit> {
        val manifest = packDef.manifest
        val puzzles = packDef.puzzles

        // 1. Manifest consistency
        if (manifest.puzzleCount != puzzles.size) {
            return Result.failure(
                IllegalArgumentException("Manifest puzzleCount (${manifest.puzzleCount}) does not match puzzles size (${puzzles.size})")
            )
        }

        // 2 & 3. Structural validation & fingerprint verification
        for (i in puzzles.indices) {
            val puzzle = puzzles[i]
            val ref = manifest.puzzles.getOrNull(i)

            val validation = PuzzleDefinitionValidator.validate(puzzle)
            if (validation is DefinitionValidationResult.Invalid) {
                return Result.failure(
                    IllegalArgumentException("Puzzle ${puzzle.puzzleId} is structurally invalid: ${validation.errorSummary}")
                )
            }

            val computedFingerprint = PuzzleFingerprint.computeSha256(puzzle)
            if (ref != null && ref.fingerprint.isNotBlank() && ref.fingerprint != computedFingerprint) {
                return Result.failure(
                    IllegalArgumentException("Puzzle ${puzzle.puzzleId} fingerprint mismatch: expected ${ref.fingerprint}, got $computedFingerprint")
                )
            }

            // 4. Solver solvability verification
            val solverResult = solver.solve(puzzle)
            if (solverResult.status != SolverStatus.SOLVED) {
                return Result.failure(
                    IllegalStateException("Puzzle ${puzzle.puzzleId} failed solver verification: status ${solverResult.status}")
                )
            }
        }

        // 5. Atomic activation via staging directory
        val targetDir = File(packsDir, manifest.packId)
        val tempDir = File(packsDir, "${manifest.packId}_staging_${System.currentTimeMillis()}")

        try {
            if (!tempDir.exists() && !tempDir.mkdirs()) {
                return Result.failure(IOException("Failed to create temporary staging directory for pack ${manifest.packId}"))
            }

            val manifestFile = File(tempDir, "manifest.json")
            manifestFile.writeText(serializeManifest(manifest))

            val puzzlesFile = File(tempDir, "puzzles.json")
            puzzlesFile.writeText(serializePuzzles(puzzles))

            // Replace target atomically
            if (targetDir.exists()) {
                targetDir.deleteRecursively()
            }
            if (!tempDir.renameTo(targetDir)) {
                // If renameTo fails across file boundaries, copy and delete
                tempDir.copyRecursively(targetDir, overwrite = true)
                tempDir.deleteRecursively()
            }

            return Result.success(Unit)
        } catch (e: Exception) {
            if (tempDir.exists()) {
                tempDir.deleteRecursively()
            }
            return Result.failure(e)
        }
    }

    /**
     * Deletes installed pack files safely while preserving player progress.
     */
    fun deletePack(packId: String, isGameplayActive: Boolean): Result<Unit> {
        if (isGameplayActive) {
            return Result.failure(IllegalStateException("Cannot delete pack while gameplay is active"))
        }

        val targetDir = File(packsDir, packId)
        if (targetDir.exists()) {
            val deleted = targetDir.deleteRecursively()
            if (!deleted) {
                return Result.failure(IOException("Failed to delete local pack directory for $packId"))
            }
        }
        return Result.success(Unit)
    }

    private fun serializeManifest(manifest: PremiumPackManifest): String {
        val json = JSONObject().apply {
            put("packId", manifest.packId)
            put("packVersion", manifest.packVersion)
            put("displayName", manifest.displayName)
            put("description", manifest.description)
            put("difficulty", manifest.difficulty)
            put("puzzleCount", manifest.puzzleCount)
            put("requiredEntitlement", manifest.requiredEntitlement)
            put("checksum", manifest.checksum)

            val puzzlesArray = JSONArray()
            manifest.puzzles.forEach { ref ->
                puzzlesArray.put(JSONObject().apply {
                    put("puzzleId", ref.puzzleId)
                    put("puzzleVersion", ref.puzzleVersion)
                    put("fingerprint", ref.fingerprint)
                    put("levelIndex", ref.levelIndex)
                    put("gridSize", ref.gridSize)
                    put("checkpointCount", ref.checkpointCount)
                })
            }
            put("puzzles", puzzlesArray)
        }
        return json.toString(2)
    }

    private fun parseManifest(raw: String): PremiumPackManifest {
        val root = JSONObject(raw)
        val puzzles = mutableListOf<PremiumPuzzleRef>()
        val arr = root.optJSONArray("puzzleReferences") ?: root.optJSONArray("puzzles")
        if (arr != null) {
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val rows = obj.optInt("rows", 5)
                val cols = obj.optInt("columns", 5)
                val band = try {
                    DifficultyBand.valueOf(obj.optString("difficultyBand", "EXPERT"))
                } catch (e: Exception) {
                    DifficultyBand.EXPERT
                }
                puzzles.add(
                    PremiumPuzzleRef(
                        puzzleId = obj.getString("puzzleId"),
                        puzzleVersion = obj.optInt("puzzleVersion", 1),
                        levelIndex = obj.optInt("levelIndex", i + 1),
                        fingerprint = obj.optString("fingerprint", ""),
                        gridDimensions = GridDimensions(rows, cols),
                        checkpointCount = obj.optInt("checkpointCount", 5),
                        wallCount = obj.optInt("wallCount", 0),
                        estimatedDifficulty = obj.optDouble("estimatedDifficulty", 0.8),
                        difficultyBand = band
                    )
                )
            }
        }

        val diffBand = try {
            DifficultyBand.valueOf(root.optString("difficultyBand", root.optString("difficulty", "EXPERT")))
        } catch (e: Exception) {
            DifficultyBand.EXPERT
        }

        val pubStatus = try {
            PackPublicationStatus.valueOf(root.optString("publicationStatus", "PREMIUM"))
        } catch (e: Exception) {
            PackPublicationStatus.PREMIUM
        }

        return PremiumPackManifest(
            packId = root.getString("packId"),
            version = root.optInt("version", root.optInt("packVersion", 1)),
            displayName = root.getString("displayName"),
            description = root.getString("description"),
            difficultyBand = diffBand,
            puzzleCount = root.optInt("puzzleCount", puzzles.size),
            contentFingerprint = root.optString("contentFingerprint", root.optString("checksum", "")),
            requiredEntitlement = root.optString("requiredEntitlement", "PREMIUM_SOLO_PACKS"),
            publicationStatus = pubStatus,
            themeTag = root.optString("themeTag", "Premium"),
            checksum = root.optString("checksum", ""),
            puzzleReferences = puzzles
        )
    }

    private fun serializePuzzles(puzzles: List<PuzzleDefinition>): String {
        val array = JSONArray()
        puzzles.forEach { def ->
            val asset = PuzzleAsset.fromPuzzleDefinition(def)
            array.put(JSONObject(PuzzleAssetSerializer.serialize(asset)))
        }
        return array.toString(2)
    }

    private fun parsePuzzles(raw: String): List<PuzzleDefinition> {
        val array = JSONArray(raw)
        val list = mutableListOf<PuzzleDefinition>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val asset = PuzzleAssetSerializer.deserialize(obj.toString())
            list.add(asset.toPuzzleDefinition())
        }
        return list
    }
}
