package com.zynpath.game.core.puzzle.session

import com.zynpath.game.core.puzzle.model.GridPosition

/**
 * Deterministic serializer and deserializer for ordered puzzle path coordinates.
 *
 * Implements Prompt 13 Section 12:
 * - Semicolon-delimited coordinate format: "row,col;row,col;..."
 * - Preserves exact traversal order and coordinate conventions.
 * - Throws on malformed input to enable clean error handling in the validator.
 */
object SessionPathSerializer {

    fun serialize(path: List<GridPosition>): String {
        if (path.isEmpty()) return ""
        return path.joinToString(";") { "${it.row},${it.column}" }
    }

    fun deserialize(snapshot: String?): List<GridPosition> {
        if (snapshot.isNullOrBlank()) return emptyList()

        val tokens = snapshot.split(";").filter { it.isNotBlank() }
        val result = mutableListOf<GridPosition>()

        for (token in tokens) {
            val parts = token.split(",")
            require(parts.size == 2) { "Malformed coordinate token '$token' in path snapshot" }
            val row = parts[0].trim().toIntOrNull()
                ?: throw IllegalArgumentException("Invalid row integer in token '$token'")
            val col = parts[1].trim().toIntOrNull()
                ?: throw IllegalArgumentException("Invalid col integer in token '$token'")
            result.add(GridPosition(row, col))
        }

        return result
    }
}
