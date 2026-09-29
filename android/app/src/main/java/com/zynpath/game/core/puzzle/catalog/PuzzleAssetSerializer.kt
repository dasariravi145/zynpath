package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition

/**
 * Deterministic serializer and deserializer for [PuzzleAsset].
 *
 * Implements Prompt 11 Sections 10 and 16:
 * - Produces compact, canonical JSON output with normalized blocked edges and coordinates.
 * - Guarantees 100% lossless serialization round-trips.
 */
object PuzzleAssetSerializer {

    fun serialize(asset: PuzzleAsset): String {
        val sb = StringBuilder()
        sb.append("{\n")
        sb.append("  \"schemaVersion\": \"${escape(asset.schemaVersion)}\",\n")
        sb.append("  \"puzzleId\": \"${escape(asset.puzzleId)}\",\n")
        sb.append("  \"puzzleVersion\": ${asset.puzzleVersion},\n")

        // Grid Dimensions
        sb.append("  \"gridDimensions\": {\"rows\": ${asset.gridDimensions.rows}, \"columns\": ${asset.gridDimensions.columns}},\n")

        // Required Cells
        val sortedCells = asset.requiredCells.sortedWith(compareBy({ it.row }, { it.column }))
        sb.append("  \"requiredCells\": [\n")
        sortedCells.forEachIndexed { idx, cell ->
            sb.append("    {\"row\": ${cell.row}, \"column\": ${cell.column}}")
            if (idx < sortedCells.size - 1) sb.append(",")
            sb.append("\n")
        }
        sb.append("  ],\n")

        // Checkpoints
        val sortedCheckpoints = asset.checkpoints.sorted()
        sb.append("  \"checkpoints\": [\n")
        sortedCheckpoints.forEachIndexed { idx, cp ->
            sb.append("    {\"number\": ${cp.number}, \"row\": ${cp.position.row}, \"column\": ${cp.position.column}}")
            if (idx < sortedCheckpoints.size - 1) sb.append(",")
            sb.append("\n")
        }
        sb.append("  ],\n")

        // Blocked Edges
        val sortedWalls = asset.blockedEdges.sortedWith(compareBy({ it.first }, { it.second }))
        sb.append("  \"blockedEdges\": [\n")
        sortedWalls.forEachIndexed { idx, wall ->
            sb.append("    {\"first\": {\"row\": ${wall.first.row}, \"column\": ${wall.first.column}}, ")
            sb.append("\"second\": {\"row\": ${wall.second.row}, \"column\": ${wall.second.column}}}")
            if (idx < sortedWalls.size - 1) sb.append(",")
            sb.append("\n")
        }
        sb.append("  ],\n")

        // Metadata
        val meta = asset.metadata
        sb.append("  \"metadata\": {\n")
        sb.append("    \"generatorVersion\": \"${escape(meta.generatorVersion)}\",\n")
        if (meta.generationSeed != null) {
            sb.append("    \"generationSeed\": ${meta.generationSeed},\n")
        }
        sb.append("    \"difficultyEstimate\": ${meta.difficultyEstimate},\n")
        sb.append("    \"difficultyBand\": \"${escape(meta.difficultyBand)}\",\n")
        sb.append("    \"uniquenessStatus\": \"${escape(meta.uniquenessStatus)}\"")
        if (meta.fingerprint != null) {
            sb.append(",\n    \"fingerprint\": \"${escape(meta.fingerprint)}\"\n")
        } else {
            sb.append("\n")
        }
        sb.append("  }\n")
        sb.append("}\n")
        return sb.toString()
    }

    fun deserialize(json: String): PuzzleAsset {
        val root = SimpleJsonParser.parse(json) as? JsonElement.JsonObject
            ?: throw IllegalArgumentException("Root of puzzle asset JSON must be an object")

        val schemaVersion = root.getString("schemaVersion") ?: CatalogVersion.ASSET_SCHEMA_VERSION
        val puzzleId = root.getString("puzzleId") ?: throw IllegalArgumentException("Missing 'puzzleId'")
        val puzzleVersion = root.getInt("puzzleVersion") ?: CatalogVersion.DEFAULT_PUZZLE_VERSION

        val dimsObj = root.getObject("gridDimensions") ?: throw IllegalArgumentException("Missing 'gridDimensions'")
        val rows = dimsObj.getInt("rows") ?: throw IllegalArgumentException("Missing gridDimensions.rows")
        val cols = dimsObj.getInt("columns") ?: throw IllegalArgumentException("Missing gridDimensions.columns")
        val gridDimensions = GridDimensions(rows, cols)

        // Required Cells
        val cellsArray = root.getArray("requiredCells")
        val requiredCells = if (cellsArray != null) {
            cellsArray.elements.map { el ->
                val obj = el as? JsonElement.JsonObject ?: throw IllegalArgumentException("Invalid required cell element")
                val r = obj.getInt("row") ?: throw IllegalArgumentException("Missing cell row")
                val c = obj.getInt("column") ?: throw IllegalArgumentException("Missing cell column")
                GridPosition(r, c)
            }.toSet()
        } else {
            gridDimensions.allPositions().toSet()
        }

        // Checkpoints
        val cpArray = root.getArray("checkpoints") ?: throw IllegalArgumentException("Missing 'checkpoints'")
        val checkpoints = cpArray.elements.map { el ->
            val obj = el as? JsonElement.JsonObject ?: throw IllegalArgumentException("Invalid checkpoint element")
            val num = obj.getInt("number") ?: throw IllegalArgumentException("Missing checkpoint number")
            val r = obj.getInt("row") ?: throw IllegalArgumentException("Missing checkpoint row")
            val c = obj.getInt("column") ?: throw IllegalArgumentException("Missing checkpoint column")
            NumberedCheckpoint(num, GridPosition(r, c))
        }

        // Blocked Edges
        val wallsArray = root.getArray("blockedEdges")
        val blockedEdges = if (wallsArray != null) {
            wallsArray.elements.map { el ->
                val obj = el as? JsonElement.JsonObject ?: throw IllegalArgumentException("Invalid blocked edge element")
                val firstObj = obj.getObject("first") ?: throw IllegalArgumentException("Missing wall first")
                val secondObj = obj.getObject("second") ?: throw IllegalArgumentException("Missing wall second")
                val p1 = GridPosition(firstObj.getInt("row")!!, firstObj.getInt("column")!!)
                val p2 = GridPosition(secondObj.getInt("row")!!, secondObj.getInt("column")!!)
                BlockedEdge.between(p1, p2)
            }.toSet()
        } else {
            emptySet()
        }

        // Metadata
        val metaObj = root.getObject("metadata")
        val metadata = if (metaObj != null) {
            PuzzleAssetMetadata(
                generatorVersion = metaObj.getString("generatorVersion") ?: "1.0.0",
                generationSeed = metaObj.getLong("generationSeed"),
                difficultyEstimate = metaObj.getDouble("difficultyEstimate") ?: 0.0,
                difficultyBand = metaObj.getString("difficultyBand") ?: "BEGINNER",
                uniquenessStatus = metaObj.getString("uniquenessStatus") ?: "UNIQUE",
                fingerprint = metaObj.getString("fingerprint")
            )
        } else {
            PuzzleAssetMetadata()
        }

        return PuzzleAsset(
            schemaVersion = schemaVersion,
            puzzleId = puzzleId,
            puzzleVersion = puzzleVersion,
            gridDimensions = gridDimensions,
            requiredCells = requiredCells,
            checkpoints = checkpoints,
            blockedEdges = blockedEdges,
            metadata = metadata
        )
    }

    fun serializeDefinition(def: PuzzleDefinition, metadata: PuzzleAssetMetadata? = null): String {
        return serialize(PuzzleAsset.fromPuzzleDefinition(def, metadata))
    }

    fun deserializeToDefinition(json: String): PuzzleDefinition {
        return deserialize(json).toPuzzleDefinition()
    }

    private fun escape(s: String): String {
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
    }
}
