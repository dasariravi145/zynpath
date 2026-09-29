package com.zynpath.game.core.puzzle.model

/**
 * Represents the four legal orthogonal movement directions in a Zynpath continuous-path puzzle.
 * Diagonal movements are strictly prohibited by game rules.
 */
enum class Direction(
    val rowDelta: Int,
    val colDelta: Int
) {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    /**
     * The reverse/opposite direction.
     */
    val opposite: Direction
        get() = when (this) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT
        }

    /**
     * Returns true if this direction is vertical (UP or DOWN).
     */
    val isVertical: Boolean
        get() = this == UP || this == DOWN

    /**
     * Returns true if this direction is horizontal (LEFT or RIGHT).
     */
    val isHorizontal: Boolean
        get() = this == LEFT || this == RIGHT
}
