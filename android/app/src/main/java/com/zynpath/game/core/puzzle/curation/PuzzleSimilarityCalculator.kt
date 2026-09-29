package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.generator.PuzzleFingerprint
import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import kotlin.math.max

/**
 * Geometric transformation representation for planar puzzle grids.
 */
enum class SymmetryTransformation {
    IDENTITY,
    ROTATE_90,
    ROTATE_180,
    ROTATE_270,
    REFLECT_HORIZONTAL,
    REFLECT_VERTICAL,
    REFLECT_MAIN_DIAGONAL,
    REFLECT_ANTI_DIAGONAL
}

/**
 * Authoritative similarity, duplicate, and symmetry analysis for Zynpath puzzles.
 *
 * Implements Prompt 10 Sections 22 and 23:
 * - Exact duplicate detection via [PuzzleFingerprint].
 * - Symmetry-aware isomorphism detection across square (D4) and rectangular transformations.
 * - Normalized similarity scoring [0.0, 1.0] across checkpoint layout, wall configuration,
 *   and grid topology.
 */
object PuzzleSimilarityCalculator {

    /**
     * Checks if [puzzleA] and [puzzleB] are exact duplicates without transformation.
     */
    fun isExactDuplicate(puzzleA: PuzzleDefinition, puzzleB: PuzzleDefinition): Boolean {
        return PuzzleFingerprint.areDuplicates(puzzleA, puzzleB)
    }

    /**
     * Checks if [puzzleA] and [puzzleB] are duplicates under any valid geometric symmetry transformation.
     */
    fun isSymmetricDuplicate(puzzleA: PuzzleDefinition, puzzleB: PuzzleDefinition): Boolean {
        if (puzzleA.gridDimensions.rows * puzzleA.gridDimensions.columns !=
            puzzleB.gridDimensions.rows * puzzleB.gridDimensions.columns) {
            return false
        }
        if (puzzleA.checkpoints.size != puzzleB.checkpoints.size) return false
        if (puzzleA.blockedEdges.size != puzzleB.blockedEdges.size) return false

        val transformations = getApplicableTransformations(puzzleA.gridDimensions)
        val canonicalB = PuzzleFingerprint.canonicalRepresentation(puzzleB)

        for (t in transformations) {
            val transformedA = applyTransformation(puzzleA, t)
            if (transformedA.gridDimensions == puzzleB.gridDimensions) {
                if (PuzzleFingerprint.canonicalRepresentation(transformedA) == canonicalB) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Calculates a normalized similarity score [0.0, 1.0] between two puzzle definitions.
     * 1.0 means identical, 0.0 means completely distinct.
     */
    fun calculateSimilarity(
        puzzleA: PuzzleDefinition,
        puzzleB: PuzzleDefinition,
        considerSymmetries: Boolean = true
    ): Double {
        if (puzzleA.gridDimensions.rows * puzzleA.gridDimensions.columns !=
            puzzleB.gridDimensions.rows * puzzleB.gridDimensions.columns) {
            return 0.0
        }

        if (!considerSymmetries) {
            return directSimilarity(puzzleA, puzzleB)
        }

        val transformations = getApplicableTransformations(puzzleA.gridDimensions)
        var maxSim = 0.0
        for (t in transformations) {
            val transformedA = applyTransformation(puzzleA, t)
            if (transformedA.gridDimensions == puzzleB.gridDimensions) {
                val sim = directSimilarity(transformedA, puzzleB)
                if (sim > maxSim) maxSim = sim
            }
        }
        return maxSim
    }

    /**
     * Direct comparison of checkpoint and wall overlap between two aligned puzzle definitions.
     */
    private fun directSimilarity(a: PuzzleDefinition, b: PuzzleDefinition): Double {
        // 1. Checkpoint position similarity
        val cpMapA = a.checkpoints.associate { it.number to it.position }
        val cpMapB = b.checkpoints.associate { it.number to it.position }
        val allNumbers = cpMapA.keys.union(cpMapB.keys)
        var matchingCheckpoints = 0
        for (num in allNumbers) {
            if (cpMapA[num] == cpMapB[num]) {
                matchingCheckpoints++
            }
        }
        val cpSim = if (allNumbers.isNotEmpty()) {
            matchingCheckpoints.toDouble() / allNumbers.size
        } else {
            1.0
        }

        // 2. Wall configuration similarity (Jaccard coefficient)
        val wallsA = a.blockedEdges
        val wallsB = b.blockedEdges
        val wallSim = when {
            wallsA.isEmpty() && wallsB.isEmpty() -> 1.0
            wallsA.isEmpty() || wallsB.isEmpty() -> 0.0
            else -> {
                val intersectionSize = wallsA.intersect(wallsB).size
                val unionSize = wallsA.union(wallsB).size
                if (unionSize > 0) intersectionSize.toDouble() / unionSize else 1.0
            }
        }

        // Weight checkpoint overlap higher as it dictates the core progression shape
        return (0.65 * cpSim) + (0.35 * wallSim)
    }

    /**
     * Determines applicable transformations based on whether grid is square or rectangular.
     */
    fun getApplicableTransformations(dims: GridDimensions): List<SymmetryTransformation> {
        return if (dims.isSquare) {
            SymmetryTransformation.entries
        } else {
            listOf(
                SymmetryTransformation.IDENTITY,
                SymmetryTransformation.ROTATE_180,
                SymmetryTransformation.REFLECT_HORIZONTAL,
                SymmetryTransformation.REFLECT_VERTICAL
            )
        }
    }

    /**
     * Transforms a puzzle definition according to a geometric symmetry transformation.
     */
    fun applyTransformation(
        def: PuzzleDefinition,
        transformation: SymmetryTransformation
    ): PuzzleDefinition {
        if (transformation == SymmetryTransformation.IDENTITY) return def

        val rows = def.gridDimensions.rows
        val cols = def.gridDimensions.columns

        // New dimensions and coordinate mapping
        val (newDims, mapPos) = when (transformation) {
            SymmetryTransformation.IDENTITY -> Pair(def.gridDimensions, { p: GridPosition -> p })
            SymmetryTransformation.ROTATE_90 -> {
                // Clockwise 90: (r, c) -> (c, rows - 1 - r)
                Pair(GridDimensions(cols, rows), { p: GridPosition ->
                    GridPosition(p.column, rows - 1 - p.row)
                })
            }
            SymmetryTransformation.ROTATE_180 -> {
                // 180: (r, c) -> (rows - 1 - r, cols - 1 - c)
                Pair(def.gridDimensions, { p: GridPosition ->
                    GridPosition(rows - 1 - p.row, cols - 1 - p.column)
                })
            }
            SymmetryTransformation.ROTATE_270 -> {
                // Clockwise 270 (Counter-clockwise 90): (r, c) -> (cols - 1 - c, r)
                Pair(GridDimensions(cols, rows), { p: GridPosition ->
                    GridPosition(cols - 1 - p.column, p.row)
                })
            }
            SymmetryTransformation.REFLECT_HORIZONTAL -> {
                // Flip top-to-bottom: (r, c) -> (rows - 1 - r, c)
                Pair(def.gridDimensions, { p: GridPosition ->
                    GridPosition(rows - 1 - p.row, p.column)
                })
            }
            SymmetryTransformation.REFLECT_VERTICAL -> {
                // Flip left-to-right: (r, c) -> (r, cols - 1 - c)
                Pair(def.gridDimensions, { p: GridPosition ->
                    GridPosition(p.row, cols - 1 - p.column)
                })
            }
            SymmetryTransformation.REFLECT_MAIN_DIAGONAL -> {
                // Transpose: (r, c) -> (c, r)
                Pair(GridDimensions(cols, rows), { p: GridPosition ->
                    GridPosition(p.column, p.row)
                })
            }
            SymmetryTransformation.REFLECT_ANTI_DIAGONAL -> {
                // Anti-diagonal: (r, c) -> (cols - 1 - c, rows - 1 - r)
                Pair(GridDimensions(cols, rows), { p: GridPosition ->
                    GridPosition(cols - 1 - p.column, rows - 1 - p.row)
                })
            }
        }

        val newRequired = def.requiredCells.map(mapPos).toSet()
        val newCheckpoints = def.checkpoints.map { cp ->
            NumberedCheckpoint(cp.number, mapPos(cp.position))
        }
        val newBlockedEdges = def.blockedEdges.map { edge ->
            BlockedEdge.between(mapPos(edge.first), mapPos(edge.second))
        }.toSet()

        return PuzzleDefinition(
            puzzleId = "${def.puzzleId}_$transformation",
            gridDimensions = newDims,
            requiredCells = newRequired,
            checkpoints = newCheckpoints,
            blockedEdges = newBlockedEdges
        )
    }
}
