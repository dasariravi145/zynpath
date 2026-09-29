package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.curation.DifficultyBand
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.solver.UniquenessStatus
import java.security.MessageDigest

/**
 * Immutable top-level manifest defining the entire offline puzzle campaign catalog.
 *
 * Implements Prompt 11 Section 11:
 * Versioned schema establishing authoritative world configurations, level indices,
 * asset references, fingerprints, and catalog integrity hashes.
 */
data class CatalogManifest(
    val catalogId: String = "zynpath_solo_campaign",
    val catalogVersion: String = CatalogVersion.CATALOG_VERSION,
    val schemaVersion: String = CatalogVersion.MANIFEST_SCHEMA_VERSION,
    val totalPlannedLevels: Int = 300,
    val worlds: List<WorldDefinition> = WorldDefinition.ALL_WORLDS,
    val levels: List<LevelDefinition> = emptyList(),
    val integrityHash: String = ""
) {
    val levelMap: Map<Int, LevelDefinition> by lazy {
        levels.associateBy { it.levelId }
    }

    val worldMap: Map<Int, WorldDefinition> by lazy {
        worlds.associateBy { it.worldId }
    }

    fun getLevel(levelId: Int): LevelDefinition? = levelMap[levelId]

    fun getWorld(worldId: Int): WorldDefinition? = worldMap[worldId]

    fun getLevelsForWorld(worldId: Int): List<LevelDefinition> {
        return levels.filter { it.worldId == worldId }.sortedBy { it.levelId }
    }

    companion object {
        const val DEFAULT_MANIFEST_PATH = "catalog_manifest.json"

        fun computeIntegrityHash(levels: List<LevelDefinition>): String {
            val digest = MessageDigest.getInstance("SHA-256")
            val sortedLevels = levels.sortedBy { it.levelId }
            val input = sortedLevels.joinToString("|") {
                "${it.levelId}:${it.worldId}:${it.puzzleId}:${it.puzzleVersion}:${it.fingerprint}"
            }
            val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
            return hashBytes.joinToString("") { "%02x".format(it) }
        }

        fun createDefaultManifest(): CatalogManifest {
            val levels = (1..300).map { lvlId ->
                val world = WorldDefinition.forLevel(lvlId)
                val packagedDef = PackagedPuzzles.ALL_PACKAGED[lvlId]
                if (packagedDef != null) {
                    val fp = com.zynpath.game.core.puzzle.generator.PuzzleFingerprint.computeSha256(packagedDef)
                    LevelDefinition(
                        levelId = lvlId,
                        worldId = world.worldId,
                        puzzleId = packagedDef.puzzleId,
                        puzzleVersion = packagedDef.puzzleVersion,
                        assetPath = "puzzles/w${world.worldId}/lvl$lvlId.json",
                        difficultyEstimate = when (world.worldId) {
                            1 -> 0.10 + (lvlId - 1) * 0.01
                            2 -> 0.25 + (lvlId - 21) * 0.01
                            else -> 0.40 + (lvlId - 51) * 0.01
                        },
                        difficultyBand = when (world.worldId) {
                            1 -> DifficultyBand.BEGINNER
                            2 -> DifficultyBand.EASY
                            3 -> DifficultyBand.MEDIUM
                            4 -> DifficultyBand.MEDIUM
                            5 -> DifficultyBand.HARD
                            else -> DifficultyBand.EXPERT
                        },
                        uniquenessStatus = UniquenessStatus.UNIQUE,
                        fingerprint = fp,
                        hasPackagedAsset = true,
                        displayName = "Level $lvlId"
                    )
                } else {
                    val curatedDef = CuratedFirst50Levels.LEVELS[lvlId]
                    val anchorDef = CuratedAnchorLevels.ANCHOR_LEVELS[lvlId]
                    val puzzleId = curatedDef?.puzzleId ?: anchorDef?.puzzleId ?: "w${world.worldId}_lvl$lvlId"
                    val fp = when {
                        curatedDef != null -> com.zynpath.game.core.puzzle.generator.PuzzleFingerprint.computeSha256(curatedDef)
                        anchorDef != null -> com.zynpath.game.core.puzzle.generator.PuzzleFingerprint.computeSha256(anchorDef)
                        else -> ""
                    }
                    LevelDefinition(
                        levelId = lvlId,
                        worldId = world.worldId,
                        puzzleId = puzzleId,
                        puzzleVersion = 1,
                        assetPath = "puzzles/w${world.worldId}/lvl$lvlId.json",
                        difficultyEstimate = when (world.worldId) {
                            1 -> 0.10 + (lvlId - 1) * 0.01
                            2 -> 0.25 + (lvlId - 21) * 0.01
                            else -> 0.40 + (lvlId - 51) * 0.002
                        },
                        difficultyBand = when (world.worldId) {
                            1 -> DifficultyBand.BEGINNER
                            2 -> DifficultyBand.EASY
                            3 -> DifficultyBand.MEDIUM
                            4 -> DifficultyBand.MEDIUM
                            5 -> DifficultyBand.HARD
                            else -> DifficultyBand.EXPERT
                        },
                        uniquenessStatus = UniquenessStatus.UNIQUE,
                        fingerprint = fp,
                        hasPackagedAsset = false,
                        displayName = "Level $lvlId"
                    )
                }
            }
            val hash = computeIntegrityHash(levels)
            return CatalogManifest(
                catalogId = "zynpath_solo_campaign",
                catalogVersion = CatalogVersion.CATALOG_VERSION,
                schemaVersion = CatalogVersion.MANIFEST_SCHEMA_VERSION,
                totalPlannedLevels = 300,
                worlds = WorldDefinition.ALL_WORLDS,
                levels = levels,
                integrityHash = hash
            )
        }
    }
}

/**
 * Serializer and parser for [CatalogManifest].
 */
object CatalogManifestSerializer {

    fun serialize(manifest: CatalogManifest): String {
        val sb = StringBuilder()
        sb.append("{\n")
        sb.append("  \"catalogId\": \"${escape(manifest.catalogId)}\",\n")
        sb.append("  \"catalogVersion\": \"${escape(manifest.catalogVersion)}\",\n")
        sb.append("  \"schemaVersion\": \"${escape(manifest.schemaVersion)}\",\n")
        sb.append("  \"totalPlannedLevels\": ${manifest.totalPlannedLevels},\n")

        // Worlds
        sb.append("  \"worlds\": [\n")
        manifest.worlds.forEachIndexed { wIdx, w ->
            sb.append("    {\n")
            sb.append("      \"worldId\": ${w.worldId},\n")
            sb.append("      \"displayName\": \"${escape(w.displayName)}\",\n")
            sb.append("      \"firstLevelId\": ${w.firstLevelId},\n")
            sb.append("      \"lastLevelId\": ${w.lastLevelId},\n")
            sb.append("      \"gridDimensions\": {\"rows\": ${w.gridDimensions.rows}, \"columns\": ${w.gridDimensions.columns}},\n")
            sb.append("      \"minimumCheckpointCount\": ${w.minimumCheckpointCount},\n")
            sb.append("      \"maximumCheckpointCount\": ${w.maximumCheckpointCount},\n")
            sb.append("      \"minimumWallCount\": ${w.minimumWallCount},\n")
            sb.append("      \"maximumWallCount\": ${w.maximumWallCount},\n")
            sb.append("      \"themeIdentifier\": ${if (w.themeIdentifier != null) "\"${escape(w.themeIdentifier)}\"" else "null"}\n")
            sb.append("    }")
            if (wIdx < manifest.worlds.size - 1) sb.append(",")
            sb.append("\n")
        }
        sb.append("  ],\n")

        // Levels
        sb.append("  \"levels\": [\n")
        manifest.levels.sortedBy { it.levelId }.forEachIndexed { lIdx, lvl ->
            sb.append("    {\n")
            sb.append("      \"levelId\": ${lvl.levelId},\n")
            sb.append("      \"worldId\": ${lvl.worldId},\n")
            sb.append("      \"puzzleId\": \"${escape(lvl.puzzleId)}\",\n")
            sb.append("      \"puzzleVersion\": ${lvl.puzzleVersion},\n")
            sb.append("      \"assetPath\": \"${escape(lvl.assetPath)}\",\n")
            sb.append("      \"difficultyEstimate\": ${lvl.difficultyEstimate},\n")
            sb.append("      \"difficultyBand\": \"${escape(lvl.difficultyBand.name)}\",\n")
            sb.append("      \"uniquenessStatus\": \"${escape(lvl.uniquenessStatus.name)}\",\n")
            sb.append("      \"fingerprint\": \"${escape(lvl.fingerprint)}\",\n")
            sb.append("      \"hasPackagedAsset\": ${lvl.hasPackagedAsset},\n")
            sb.append("      \"displayName\": ${if (lvl.displayName != null) "\"${escape(lvl.displayName)}\"" else "null"}\n")
            sb.append("    }")
            if (lIdx < manifest.levels.size - 1) sb.append(",")
            sb.append("\n")
        }
        sb.append("  ],\n")

        val effectiveHash = if (manifest.integrityHash.isNotBlank()) {
            manifest.integrityHash
        } else {
            CatalogManifest.computeIntegrityHash(manifest.levels)
        }
        sb.append("  \"integrityHash\": \"${escape(effectiveHash)}\"\n")
        sb.append("}\n")
        return sb.toString()
    }

    fun deserialize(json: String): CatalogManifest {
        val root = SimpleJsonParser.parse(json) as? JsonElement.JsonObject
            ?: throw IllegalArgumentException("Root of catalog manifest JSON must be an object")

        val catalogId = root.getString("catalogId") ?: "zynpath_solo_campaign"
        val catalogVersion = root.getString("catalogVersion") ?: CatalogVersion.CATALOG_VERSION
        val schemaVersion = root.getString("schemaVersion") ?: CatalogVersion.MANIFEST_SCHEMA_VERSION
        val totalPlannedLevels = root.getInt("totalPlannedLevels") ?: 300
        val integrityHash = root.getString("integrityHash") ?: ""

        // Parse Worlds
        val worldsArray = root.getArray("worlds")
        val worlds = if (worldsArray != null) {
            worldsArray.elements.map { el ->
                val obj = el as? JsonElement.JsonObject ?: throw IllegalArgumentException("Invalid world element")
                val dims = obj.getObject("gridDimensions")!!
                WorldDefinition(
                    worldId = obj.getInt("worldId")!!,
                    displayName = obj.getString("displayName")!!,
                    firstLevelId = obj.getInt("firstLevelId")!!,
                    lastLevelId = obj.getInt("lastLevelId")!!,
                    gridDimensions = GridDimensions(dims.getInt("rows")!!, dims.getInt("columns")!!),
                    minimumCheckpointCount = obj.getInt("minimumCheckpointCount")!!,
                    maximumCheckpointCount = obj.getInt("maximumCheckpointCount")!!,
                    minimumWallCount = obj.getInt("minimumWallCount")!!,
                    maximumWallCount = obj.getInt("maximumWallCount")!!,
                    themeIdentifier = obj.getString("themeIdentifier")
                )
            }
        } else {
            WorldDefinition.ALL_WORLDS
        }

        // Parse Levels
        val levelsArray = root.getArray("levels")
        val levels = if (levelsArray != null) {
            levelsArray.elements.map { el ->
                val obj = el as? JsonElement.JsonObject ?: throw IllegalArgumentException("Invalid level element")
                val bandStr = obj.getString("difficultyBand") ?: "BEGINNER"
                val band = try { DifficultyBand.valueOf(bandStr) } catch (_: Exception) { DifficultyBand.BEGINNER }
                val uniqueStr = obj.getString("uniquenessStatus") ?: "UNIQUE"
                val unique = try { UniquenessStatus.valueOf(uniqueStr) } catch (_: Exception) { UniquenessStatus.UNIQUE }

                LevelDefinition(
                    levelId = obj.getInt("levelId")!!,
                    worldId = obj.getInt("worldId")!!,
                    puzzleId = obj.getString("puzzleId")!!,
                    puzzleVersion = obj.getInt("puzzleVersion") ?: 1,
                    assetPath = obj.getString("assetPath")!!,
                    difficultyEstimate = obj.getDouble("difficultyEstimate") ?: 0.0,
                    difficultyBand = band,
                    uniquenessStatus = unique,
                    fingerprint = obj.getString("fingerprint") ?: "",
                    hasPackagedAsset = obj.getBoolean("hasPackagedAsset") ?: true,
                    displayName = obj.getString("displayName")
                )
            }
        } else {
            emptyList()
        }

        return CatalogManifest(
            catalogId = catalogId,
            catalogVersion = catalogVersion,
            schemaVersion = schemaVersion,
            totalPlannedLevels = totalPlannedLevels,
            worlds = worlds,
            levels = levels,
            integrityHash = integrityHash
        )
    }

    private fun escape(s: String): String {
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
    }
}
