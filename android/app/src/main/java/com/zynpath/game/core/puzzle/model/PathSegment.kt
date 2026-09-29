package com.zynpath.game.core.puzzle.model

/**
 * Represents a single directed step connecting consecutive positions along a puzzle path.
 */
data class PathSegment(
    val from: GridPosition,
    val to: GridPosition
) {
    /**
     * Direction of movement from [from] to [to], or null if not orthogonally adjacent.
     */
    val direction: Direction? = from.directionTo(to)

    /**
     * True if this segment connects orthogonally adjacent cells.
     */
    val isOrthogonal: Boolean
        get() = direction != null

    /**
     * True if this segment is a horizontal move (LEFT or RIGHT).
     */
    val isHorizontal: Boolean
        get() = direction?.isHorizontal == true

    /**
     * True if this segment is a vertical move (UP or DOWN).
     */
    val isVertical: Boolean
        get() = direction?.isVertical == true

    override fun toString(): String = "$from -> $to"
}
