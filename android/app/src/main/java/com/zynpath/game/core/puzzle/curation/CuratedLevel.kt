package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.solver.UniquenessStatus

/**
 * Metadata descriptor for a published or curated level.
 *
 * Implements Prompt 10 Sections 30 and 31:
 * - Stable level identity, puzzle ID, and versioning.
 * - Does NOT store complete solution path in user-facing metadata.
 * - Records provenance (seed, generator version, analyzer version, curation version).
 * - Records objective difficulty rating and quality score.
 */
data class LevelMetadata(
    val levelId: Int,
    val worldId: Int,
    val puzzleId: String,
    val puzzleVersion: String,
    val generationSeed: Long,
    val generatorVersion: String,
    val difficultyEstimate: Double,
    val difficultyBand: DifficultyBand,
    val uniquenessStatus: UniquenessStatus,
    val softQualityScore: Double,
    val curationVersion: String = CurationConfiguration.CURATION_VERSION
)

/**
 * A curated, solver-verified, quality-evaluated level ready for catalog packaging or gameplay.
 */
data class CuratedLevel(
    val levelId: Int,
    val worldId: Int,
    val definition: PuzzleDefinition,
    val metadata: LevelMetadata
)
