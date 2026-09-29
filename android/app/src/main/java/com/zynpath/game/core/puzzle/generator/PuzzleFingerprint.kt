package com.zynpath.game.core.puzzle.generator

import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import java.security.MessageDigest

/**
 * Authoritative canonical puzzle fingerprinting and duplicate detection utility.
 *
 * Adheres to Prompt 9 Sections 24 and 25:
 * Generates an order-independent canonical representation and SHA-256 hash based strictly
 * on puzzle topology, required cells, checkpoints, and normalized blocked edges.
 *
 * Two puzzles with identical gameplay elements will produce the exact same fingerprint,
 * regardless of wall order, checkpoint order in the list, or different generation seeds.
 */
object PuzzleFingerprint {

    /**
     * Produces a deterministic canonical string representation for [definition].
     *
     * Format:
     * `DIM:{rows}x{cols}|CELLS:{r1},{c1};{r2},{c2}|CP:{num1}@{r},{c};{num2}@{r},{c}|WALLS:{r1},{c1}-{r2},{c2};...`
     */
    fun canonicalRepresentation(definition: PuzzleDefinition): String {
        val dims = "${definition.gridDimensions.rows}x${definition.gridDimensions.columns}"

        val sortedCells = definition.requiredCells
            .sortedWith(compareBy({ it.row }, { it.column }))
            .joinToString(";") { "${it.row},${it.column}" }

        val sortedCheckpoints = definition.checkpoints
            .sortedBy { it.number }
            .joinToString(";") { "${it.number}@${it.position.row},${it.position.column}" }

        // BlockedEdge is already canonically ordered (first <= second), sort the collection
        val sortedWalls = definition.blockedEdges
            .sortedWith(compareBy({ it.first }, { it.second }))
            .joinToString(";") { "${it.first.row},${it.first.column}-${it.second.row},${it.second.column}" }

        return "DIM:$dims|CELLS:$sortedCells|CP:$sortedCheckpoints|WALLS:$sortedWalls"
    }

    /**
     * Computes the hex-encoded SHA-256 hash of the canonical representation.
     */
    fun computeSha256(definition: PuzzleDefinition): String {
        val canonical = canonicalRepresentation(definition)
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(canonical.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Determines whether two puzzle definitions are structurally and semantically identical.
     */
    fun areDuplicates(puzzleA: PuzzleDefinition, puzzleB: PuzzleDefinition): Boolean {
        if (puzzleA.gridDimensions != puzzleB.gridDimensions) return false
        if (puzzleA.requiredCells != puzzleB.requiredCells) return false
        if (puzzleA.checkpoints.size != puzzleB.checkpoints.size) return false
        if (puzzleA.blockedEdges.size != puzzleB.blockedEdges.size) return false
        return canonicalRepresentation(puzzleA) == canonicalRepresentation(puzzleB)
    }
}
