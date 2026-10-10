package com.zynpath.game.core.puzzle.policy

import com.zynpath.game.core.puzzle.curation.DifficultyBand
import com.zynpath.game.core.puzzle.generator.WallPlacementStrategy
import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.WorldConfiguration

/**
 * Game mode classification for constraint policy derivation.
 */
enum class PuzzleMode {
    SOLO,
    QUICK_DUEL,
    FRIENDS_ARENA
}

/**
 * Authoritative single source of truth for puzzle structural constraints, wall budgets,
 * checkpoint counts, and board topology rules across generation, catalog metadata,
 * runtime asset integrity validation, and build-time gates.
 *
 * Implements Systemic 300-Level Asset Integrity & Wall Policy Fix:
 * - Levels 1–4: Tutorial / beginner clarity (strict 0..0 walls).
 * - Level 5+: Strategic internal edge walls allowed and required.
 * - Non-negotiable full cell coverage: every visible cell is playable (requiredCells == totalCells).
 * - Zero blocked cells, zero boxes occupying cells.
 * - Strict rejection of prohibited wall cages (corner cages, U-shaped pockets, L-shaped cages, corridors).
 */
data class PuzzleConstraintPolicy(
    val levelId: Int?,
    val worldId: Int,
    val difficultyBand: DifficultyBand,
    val gridDimensions: GridDimensions,
    val allowedWallCountRange: IntRange,
    val checkpointRequirements: IntRange,
    val fullCoverageRequired: Boolean = true,
    val blockedCellsAllowed: Boolean = false,
    val mode: PuzzleMode = PuzzleMode.SOLO
) {

    /**
     * Validates [definition] against this policy's dimension, cell coverage, checkpoint,
     * wall count, and wall topology invariants.
     * Returns a list of human-readable error descriptions, or an empty list if valid.
     */
    fun validate(definition: PuzzleDefinition): List<String> {
        val errors = mutableListOf<String>()

        // 1. Grid Dimensions
        if (definition.gridDimensions != gridDimensions) {
            errors.add(
                "Dimension mismatch: definition is ${definition.gridDimensions} but policy requires $gridDimensions for ${targetLabel()}"
            )
        }

        // 2. Full Cell Coverage & No Blocked Cells
        val expectedCellCount = gridDimensions.totalCells
        if (fullCoverageRequired) {
            if (definition.requiredCells.size != expectedCellCount) {
                errors.add(
                    "Cell coverage violation: definition has ${definition.requiredCells.size} playable cells but full board requires $expectedCellCount for ${targetLabel()}"
                )
            }
            if (!blockedCellsAllowed) {
                val missingCells = mutableListOf<GridPosition>()
                for (r in 0 until gridDimensions.rows) {
                    for (c in 0 until gridDimensions.columns) {
                        val pos = GridPosition(r, c)
                        if (pos !in definition.requiredCells) {
                            missingCells.add(pos)
                        }
                    }
                }
                if (missingCells.isNotEmpty()) {
                    errors.add(
                        "Blocked cells detected: cells $missingCells are excluded from playable board for ${targetLabel()}"
                    )
                }
            }
        }

        // 3. Checkpoint Requirements
        val checkpointCount = definition.checkpoints.size
        if (checkpointCount !in checkpointRequirements) {
            errors.add(
                "Checkpoint count $checkpointCount outside allowed bounds $checkpointRequirements for ${targetLabel()}"
            )
        }

        // 4. Wall Count Policy
        val wallCount = definition.blockedEdges.size
        if (wallCount !in allowedWallCountRange) {
            errors.add(
                "Wall count $wallCount outside allowed bounds $allowedWallCountRange for ${targetLabel()}"
            )
        }

        // 5. Wall Topology & Anti-Cage Invariants
        if (definition.blockedEdges.isNotEmpty()) {
            val topologyErrors = validateWallTopology(definition)
            errors.addAll(topologyErrors)
        }

        return errors
    }

    /**
     * Validates wall placement topology:
     * - Every wall is strictly an edge between two adjacent required cells.
     * - No walls on grid boundaries or connecting to out-of-bounds cells.
     * - Rejects prohibited corner cages, 3-sided enclosures, boxed-in cells, L-shaped perimeter cages,
     *   and narrow corridors.
     */
    fun validateWallTopology(definition: PuzzleDefinition): List<String> {
        val errors = mutableListOf<String>()
        val dims = definition.gridDimensions
        val required = definition.requiredCells
        val walls = definition.blockedEdges

        // Edge legitimacy
        for (wall in walls) {
            if (!dims.contains(wall.first) || !dims.contains(wall.second)) {
                errors.add("Wall $wall connects to an out-of-bounds cell")
            } else if (wall.first !in required || wall.second !in required) {
                errors.add("Wall $wall connects to a non-required cell")
            } else if (!wall.first.isOrthogonalNeighbor(wall.second)) {
                errors.add("Wall $wall connects non-adjacent cells")
            }
        }

        val wallStrategy = WallPlacementStrategy()

        if (wallStrategy.hasCornerCage(walls, dims)) {
            errors.add("Prohibited corner cage detected: wall touches board corner cell in ${targetLabel()}")
        }

        if (wallStrategy.hasThreeSidedEnclosure(walls, dims, required)) {
            errors.add("Prohibited 3-sided enclosure / U-shaped cage detected in ${targetLabel()}")
        }

        if (wallStrategy.hasLShapedCage(walls, dims)) {
            errors.add("Prohibited L-shaped perimeter cage detected in ${targetLabel()}")
        }

        if (wallStrategy.hasWallCorridor(walls, dims)) {
            errors.add("Prohibited parallel wall corridor detected in ${targetLabel()}")
        }

        return errors
    }

    private fun targetLabel(): String = when {
        levelId != null -> "Level $levelId (World $worldId)"
        mode != PuzzleMode.SOLO -> "$mode (${gridDimensions.rows}x${gridDimensions.columns} $difficultyBand)"
        else -> "World $worldId"
    }

    companion object {

        /**
         * Resolves the authoritative constraint policy for a specific Solo campaign level [levelId].
         */
        fun forSoloLevel(levelId: Int): PuzzleConstraintPolicy {
            require(levelId in 1..WorldConfiguration.TOTAL_LEVELS) {
                "Solo levelId must be in 1..${WorldConfiguration.TOTAL_LEVELS} (got $levelId)"
            }

            val worldId = when (levelId) {
                in 1..300 -> 1
                in 301..600 -> 2
                in 601..900 -> 3
                in 901..1200 -> 4
                in 1201..1500 -> 5
                else -> 6
            }

            val gridDimensions = when (worldId) {
                1 -> GridDimensions(6, 6)
                2 -> GridDimensions(7, 7)
                3 -> GridDimensions(8, 8)
                4 -> GridDimensions(9, 9)
                5 -> GridDimensions(11, 11)
                else -> GridDimensions(12, 12)
            }

            val checkpointRange = when (worldId) {
                1 -> 6..14
                2 -> 8..16
                3 -> 10..22
                4 -> 12..28
                5 -> 14..35
                else -> 16..40
            }

            // Authoritative Level-Driven Wall Budget (walls secondary, non-maze)
            val wallRange = when (worldId) {
                // World 1 (Levels 1–300): Introductory world (strictly 0..0 walls)
                1 -> 0..0
                // World 2: Strategic edge lines (0..6 walls)
                2 -> 0..6
                // World 3: 8x8 (0..8 walls)
                3 -> 0..8
                // World 4: 9x9 (0..10 walls)
                4 -> 0..10
                // World 5: 11x11 (0..12 walls)
                5 -> 0..12
                // World 6: 12x12 (0..15 walls)
                else -> 0..15
            }

            val band = when (worldId) {
                1 -> if (levelId <= 4) DifficultyBand.TUTORIAL else DifficultyBand.EASY
                2 -> DifficultyBand.MEDIUM
                3 -> DifficultyBand.HARD
                4 -> DifficultyBand.VERY_HARD
                5 -> DifficultyBand.EXPERT
                else -> DifficultyBand.EXTREME
            }

            return PuzzleConstraintPolicy(
                levelId = levelId,
                worldId = worldId,
                difficultyBand = band,
                gridDimensions = gridDimensions,
                allowedWallCountRange = wallRange,
                checkpointRequirements = checkpointRange,
                fullCoverageRequired = true,
                blockedCellsAllowed = false,
                mode = PuzzleMode.SOLO
            )
        }

        /**
         * Resolves the overarching world constraint policy for [worldId].
         * Represents the union of all level allowances within that world.
         */
        fun forWorld(worldId: Int): PuzzleConstraintPolicy {
            require(worldId in 1..6) { "worldId must be in 1..6 (got $worldId)" }

            val dims = when (worldId) {
                1 -> GridDimensions(6, 6)
                2 -> GridDimensions(7, 7)
                3 -> GridDimensions(8, 8)
                4 -> GridDimensions(9, 9)
                5 -> GridDimensions(11, 11)
                else -> GridDimensions(12, 12)
            }

            val checkpoints = when (worldId) {
                1 -> 6..14
                2 -> 8..16
                3 -> 10..22
                4 -> 12..28
                5 -> 14..35
                else -> 16..40
            }

            // Union wall range for each world
            val wallRange = when (worldId) {
                1 -> 0..0 // World 1: Learn the Path (strictly 0..0 walls)
                2 -> 0..6 // World 2: 0..6 strategic lines
                3 -> 0..8
                4 -> 0..10
                5 -> 0..12
                else -> 0..15
            }

            val band = when (worldId) {
                1 -> DifficultyBand.TUTORIAL
                2 -> DifficultyBand.EASY
                3 -> DifficultyBand.HARD
                4 -> DifficultyBand.VERY_HARD
                5 -> DifficultyBand.EXPERT
                else -> DifficultyBand.EXTREME
            }

            return PuzzleConstraintPolicy(
                levelId = null,
                worldId = worldId,
                difficultyBand = band,
                gridDimensions = dims,
                allowedWallCountRange = wallRange,
                checkpointRequirements = checkpoints,
                fullCoverageRequired = true,
                blockedCellsAllowed = false,
                mode = PuzzleMode.SOLO
            )
        }

        /**
         * Resolves the constraint policy for competitive multiplayer puzzles (Quick Duel, Friends Arena).
         */
        fun forCompetitive(
            mode: PuzzleMode,
            gridDimensions: GridDimensions,
            isExtreme: Boolean = false
        ): PuzzleConstraintPolicy {
            require(mode != PuzzleMode.SOLO) { "Must specify a competitive mode" }

            val (checkpointRange, wallRange, band) = when {
                gridDimensions.rows == 5 -> Triple(5..7, 3..6, DifficultyBand.HARD)
                gridDimensions.rows == 6 && !isExtreme -> Triple(6..8, 4..8, DifficultyBand.HARD)
                gridDimensions.rows == 6 && isExtreme -> Triple(6..8, 5..8, DifficultyBand.EXPERT)
                else -> Triple(5..8, 3..8, DifficultyBand.HARD)
            }

            return PuzzleConstraintPolicy(
                levelId = null,
                worldId = 0,
                difficultyBand = band,
                gridDimensions = gridDimensions,
                allowedWallCountRange = wallRange,
                checkpointRequirements = checkpointRange,
                fullCoverageRequired = true,
                blockedCellsAllowed = false,
                mode = mode
            )
        }
    }
}
