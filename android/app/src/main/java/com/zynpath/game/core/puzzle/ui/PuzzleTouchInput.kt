package com.zynpath.game.core.puzzle.ui

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import com.zynpath.game.core.puzzle.model.GridPosition

/**
 * Modifier handling continuous touch drawing, fast drag interpolation, discrete tap input,
 * and gesture completion for Zynpath puzzle boards.
 *
 * Implements Prompt 12 (Continuous Drag) and Prompt 15 Section 26 (Alternative Tap Input):
 * - Drag Mode: Touch down on checkpoint 1 starts path; dragging into adjacent cells extends path;
 *   dragging backwards onto predecessor cell retracts path; intermediate cells resolved sequentially.
 * - Tap Mode: Tap checkpoint 1 to start; tap adjacent cell to extend; tap previous cell to backtrack.
 * - Both modes use the exact same underlying [onCellEntered] engine dispatching without duplicating rules.
 * - Clean pointer release dispatch for durable state synchronization.
 */
fun Modifier.puzzleTouchInput(
    coordinateMapper: GridCoordinateMapper?,
    enabled: Boolean = true,
    isTapMode: Boolean = false,
    onCellEntered: (GridPosition) -> Boolean,
    onPointerReleased: (() -> Unit)? = null
): Modifier = if (!enabled || coordinateMapper == null) this else {
    if (isTapMode) {
        pointerInput(coordinateMapper, true) {
            detectTapGestures { offset ->
                val tappedCell = coordinateMapper.offsetToGridPosition(offset)
                if (tappedCell != null) {
                    onCellEntered(tappedCell)
                    onPointerReleased?.invoke()
                }
            }
        }
    } else {
        pointerInput(coordinateMapper, false) {
            awaitEachGesture {
                val down: PointerInputChange = awaitFirstDown(requireUnconsumed = false)
                var lastCell: GridPosition? = null

                val startCell = coordinateMapper.offsetToGridPosition(down.position)
                if (startCell != null) {
                    val accepted = onCellEntered(startCell)
                    if (accepted) {
                        lastCell = startCell
                    }
                }

                while (true) {
                    val event = awaitPointerEvent()
                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    if (!change.pressed) {
                        onPointerReleased?.invoke()
                        break
                    }

                    val currentCell = coordinateMapper.offsetToGridPosition(change.position)
                    if (currentCell != null && currentCell != lastCell) {
                        if (lastCell == null) {
                            val accepted = onCellEntered(currentCell)
                            if (accepted) {
                                lastCell = currentCell
                            }
                        } else {
                            val resolvedSteps = coordinateMapper.resolveIntermediatePath(
                                from = lastCell,
                                to = currentCell,
                                touchOffset = change.position
                            )
                            if (resolvedSteps != null) {
                                for (step in resolvedSteps) {
                                    val accepted = onCellEntered(step)
                                    if (accepted) {
                                        lastCell = step
                                    } else {
                                        break
                                    }
                                }
                            }
                        }
                    }
                    change.consume()
                }
                onPointerReleased?.invoke()
            }
        }
    }
}
