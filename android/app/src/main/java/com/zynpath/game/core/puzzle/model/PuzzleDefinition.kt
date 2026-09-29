package com.zynpath.game.core.puzzle.model

/**
 * Immutable authoritative definition of a Zynpath continuous number-path puzzle.
 * Holds immutable board topology, required playable cells, numbered checkpoints, and wall boundaries.
 * Decoupled from transient player paths, timers, and UI rendering objects.
 */
data class PuzzleDefinition(
    val puzzleId: String,
    val puzzleVersion: Int = 1,
    val gridDimensions: GridDimensions,
    val requiredCells: Set<GridPosition>,
    val checkpoints: List<NumberedCheckpoint>,
    val blockedEdges: Set<BlockedEdge> = emptySet(),
    val difficultyMetadata: String? = null,
    val seed: Long? = null
) {
    /**
     * Map of checkpoint positions to their checkpoint number.
     */
    val checkpointMap: Map<GridPosition, Int> by lazy {
        checkpoints.associate { it.position to it.number }
    }

    /**
     * Start checkpoint (#1), or null if definition is malformed.
     */
    val startCheckpoint: NumberedCheckpoint?
        get() = checkpoints.firstOrNull { it.number == 1 }

    /**
     * Maximum checkpoint number present in this puzzle.
     */
    val maxCheckpointNumber: Int
        get() = checkpoints.maxOfOrNull { it.number } ?: 0

    /**
     * Final checkpoint (with maximum number), or null if definition is malformed.
     */
    val finalCheckpoint: NumberedCheckpoint?
        get() = checkpoints.firstOrNull { it.number == maxCheckpointNumber }

    /**
     * Total number of required cells that must be covered for victory.
     */
    val totalRequiredCells: Int
        get() = requiredCells.size

    /**
     * True if this puzzle includes wall obstacles between cells.
     */
    val hasWalls: Boolean
        get() = blockedEdges.isNotEmpty()

    /**
     * Precomputed graph representation for fast traversal checks.
     */
    val graph: GridGraph by lazy {
        GridGraph(gridDimensions, requiredCells, blockedEdges)
    }

    /**
     * Canonical SHA-256 fingerprint of the puzzle definition.
     */
    val fingerprint: String
        get() = com.zynpath.game.core.puzzle.generator.PuzzleFingerprint.computeSha256(this)

    /**
     * Alias for gridDimensions.
     */
    val gridSize: GridDimensions
        get() = gridDimensions


    /**
     * Returns true if [position] is a required/playable cell.
     */
    fun isRequired(position: GridPosition): Boolean = graph.isRequiredCell(position)

    /**
     * Returns the checkpoint number at [position], or null.
     */
    fun getCheckpointAt(position: GridPosition): Int? = checkpointMap[position]

    companion object {
        /**
         * Creates a standard rectangular puzzle where every cell in the grid is required.
         */
        fun standard(
            puzzleId: String,
            dimensions: GridDimensions,
            checkpoints: List<NumberedCheckpoint>,
            blockedEdges: Set<BlockedEdge> = emptySet(),
            version: Int = 1,
            difficultyMetadata: String? = null,
            seed: Long? = null
        ): PuzzleDefinition {
            return PuzzleDefinition(
                puzzleId = puzzleId,
                puzzleVersion = version,
                gridDimensions = dimensions,
                requiredCells = dimensions.allPositions().toSet(),
                checkpoints = checkpoints.sorted(),
                blockedEdges = blockedEdges,
                difficultyMetadata = difficultyMetadata,
                seed = seed
            )
        }
    }
}
