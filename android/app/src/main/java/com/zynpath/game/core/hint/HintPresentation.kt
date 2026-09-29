package com.zynpath.game.core.hint

import com.zynpath.game.core.puzzle.hint.HintType
import com.zynpath.game.core.puzzle.model.GridPosition

/**
 * Immutable UI presentation state representing the currently active hint.
 *
 * Implements Prompt 14 Sections 17 & 18:
 * - Tied to a specific [stateRevision] to guarantee immediate invalidation upon player move.
 * - Distinctly separates next-move cell highlighting from recovery rollbacks.
 */
data class HintPresentation(
    val type: HintType,
    val targetCell: GridPosition? = null,
    val explanation: String = "",
    val stepsToRetract: Int = 0,
    val rollbackPosition: GridPosition? = null,
    val stateRevision: Long = 0L
)
