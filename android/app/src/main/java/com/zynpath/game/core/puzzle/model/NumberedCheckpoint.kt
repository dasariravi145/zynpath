package com.zynpath.game.core.puzzle.model

/**
 * Immutable numbered checkpoint in a continuous number-path puzzle.
 * Players must begin at checkpoint 1 and visit all checkpoints in strictly ascending numerical order.
 */
data class NumberedCheckpoint(
    val number: Int,
    val position: GridPosition
) : Comparable<NumberedCheckpoint> {

    init {
        require(number >= 1) { "Checkpoint number must be positive (got $number)" }
    }

    /**
     * True if this is the puzzle starting checkpoint (#1).
     */
    val isStart: Boolean
        get() = number == 1

    override fun compareTo(other: NumberedCheckpoint): Int {
        return number.compareTo(other.number)
    }

    override fun toString(): String = "#$number at $position"
}
