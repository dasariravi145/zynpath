package com.zynpath.game.core.puzzle.model

/**
 * Domain representation of a single cell in the puzzle grid.
 * Decoupled completely from Compose UI, pixel rendering, or transient touch states.
 */
data class PuzzleCell(
    val position: GridPosition,
    val isRequired: Boolean = true,
    val checkpoint: NumberedCheckpoint? = null
) {
    /**
     * True if this cell contains a numbered checkpoint.
     */
    val hasCheckpoint: Boolean
        get() = checkpoint != null

    /**
     * Checkpoint number if present, or null.
     */
    val checkpointNumber: Int?
        get() = checkpoint?.number

    /**
     * True if this cell is the starting checkpoint (#1).
     */
    val isStartCell: Boolean
        get() = checkpoint?.isStart == true

    override fun toString(): String {
        return if (checkpoint != null) "Cell[$position $checkpoint]" else "Cell[$position ${if (isRequired) "Req" else "Excl"}]"
    }
}
