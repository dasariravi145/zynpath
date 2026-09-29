package com.zynpath.game.core.puzzle.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.BoardBackgroundLight
import com.zynpath.game.core.designsystem.theme.BoardCellBorder
import com.zynpath.game.core.designsystem.theme.CellCoveredTint
import com.zynpath.game.core.designsystem.theme.CellStartHalo
import com.zynpath.game.core.designsystem.theme.CheckpointDark
import com.zynpath.game.core.designsystem.theme.CheckpointTextWhite
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.core.designsystem.theme.PathCyanGlow
import com.zynpath.game.core.designsystem.theme.PathCyanSubtle
import com.zynpath.game.core.designsystem.theme.WallCrimson
import com.zynpath.game.core.designsystem.theme.LocalPathEffectId
import com.zynpath.game.core.designsystem.theme.LocalZynpathPalette
import com.zynpath.game.core.designsystem.theme.ZynpathColorPalette
import com.zynpath.game.core.puzzle.engine.MoveRejectionReason
import com.zynpath.game.core.puzzle.model.GridCoordinate
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleBoardState
import com.zynpath.game.core.puzzle.model.SamplePuzzles
import kotlin.math.max

/**
 * High-performance, data-driven Jetpack Compose puzzle board renderer with real-time touch drawing,
 * polished visual feedback, accessible screen reader semantics, and responsive layout across 4x4 to 8x8 grids.
 *
 * Implements Prompts 12, 13, 14, 15, 28, and 39:
 * - Responsive geometry for 4x4, 5x5, 6x6, 7x7, and 8x8 boards.
 * - Dynamic theme color palettes and customizable path effects (Prompt 28).
 * - Continuous path drawing connecting centers of visited cells with smooth rounded joints.
 * - Distinct, non-color-only styling for Start (#1), Final (#N), Next Required, and Visited checkpoints.
 * - Exact wall edge alignment on shared cell boundaries without obscuring cell centers.
 * - Continuous touch-drag gesture handling, alternative discrete tap mode, and accessible cell-by-cell selection (Prompt 39).
 * - Hardware keyboard and D-pad arrow key navigation (Prompt 39).
 * - Virtual cell grid overlay exposing TalkBack descriptions, wall barriers, and interactive double-tap actions (Prompt 39).
 * - Subtle animations respecting reduced-motion user preferences.
 * - Screen reader semantics providing descriptive state without spamming pointer movements.
 */
@Composable
fun PuzzleBoard(
    boardState: PuzzleBoardState,
    modifier: Modifier = Modifier,
    isInputEnabled: Boolean = true,
    isTapMode: Boolean = false,
    isReducedMotion: Boolean = false,
    lastRejectionReason: MoveRejectionReason? = null,
    palette: ZynpathColorPalette = LocalZynpathPalette.current,
    pathEffectId: String = LocalPathEffectId.current,
    onCellEntered: ((GridPosition) -> Boolean)? = null,
    onCellClick: ((GridCoordinate) -> Unit)? = null,
    onPointerReleased: (() -> Unit)? = null
) {
    val textMeasurer = rememberTextMeasurer()
    val aspectRatio = boardState.columnCount.toFloat() / boardState.rowCount.toFloat()

    // Rejection flash feedback animation
    val rejectionAnim = remember { Animatable(0f) }
    LaunchedEffect(lastRejectionReason) {
        if (lastRejectionReason != null && !isReducedMotion) {
            rejectionAnim.snapTo(1f)
            rejectionAnim.animateTo(0f, animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing))
        }
    }

    // Restrained animations for halo and head pulsing (Prompt 15 Section 14)
    // PERFORMANCE OPTIMIZATION (Prompt 37): Retain State<Float> and read ONLY in DrawScope to eliminate 60/120Hz recomposition of the Composable
    val infiniteTransition = rememberInfiniteTransition(label = "BoardAnimations")
    val pulseAlphaState = if (isReducedMotion) {
        remember { mutableFloatStateOf(0.35f) }
    } else {
        infiniteTransition.animateFloat(
            initialValue = 0.22f,
            targetValue = 0.52f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseAlpha"
        )
    }

    val headPulseScaleState = if (isReducedMotion) {
        remember { mutableFloatStateOf(1f) }
    } else {
        infiniteTransition.animateFloat(
            initialValue = 0.94f,
            targetValue = 1.12f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "headPulseScale"
        )
    }

    // Reusable graphics structures to avoid per-frame allocations during animations (Prompt 37)
    val sharedPath = remember { Path() }
    val sharedDiamondPath = remember { Path() }
    val checkpointTextCache = remember { CheckpointTextCache() }

    // Accessible board overview for screen readers (Prompt 15 Section 27 & Prompt 39)
    val accessibilityDescription = remember(
        boardState.rowCount,
        boardState.columnCount,
        boardState.coveredCellCount,
        boardState.totalRequiredCells,
        boardState.nextRequiredCheckpoint,
        boardState.isCompleted
    ) {
        buildString {
            append("Puzzle grid, ${boardState.rowCount} by ${boardState.columnCount}. ")
            append("Coverage: ${boardState.coveredCellCount} of ${boardState.totalRequiredCells} cells. ")
            if (boardState.isCompleted) {
                append("Victory achieved! All cells covered and final checkpoint reached.")
            } else {
                append("Next required checkpoint: ${boardState.nextRequiredCheckpoint} of ${boardState.maxCheckpointNumber}.")
            }
        }
    }

    val currentRejection = rejectionAnim.value
    val borderBrush = if (currentRejection > 0f) {
        Brush.verticalGradient(
            listOf(
                Color(0xFFFF5252).copy(alpha = (0.65f + 0.35f * currentRejection).coerceAtMost(1f)),
                Color(0xFFFF7A00).copy(alpha = (0.45f + 0.30f * currentRejection).coerceAtMost(1f)),
                GameElectricCyan.copy(alpha = 0.20f)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                GameElectricCyan.copy(alpha = 0.55f),
                GameRoyalBlue.copy(alpha = 0.35f),
                GameElectricCyan.copy(alpha = 0.20f)
            )
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .padding(6.dp)
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xF008132B))
            .border(
                width = if (currentRejection > 0f) (1.5 + currentRejection * 0.8).dp else 1.5.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(22.dp)
            )
            .onKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
                val head = boardState.currentHead
                val startCoord = boardState.startCoordinate
                val target = when (event.key) {
                    Key.DirectionUp -> head?.let { GridPosition(it.row - 1, it.col) }
                    Key.DirectionDown -> head?.let { GridPosition(it.row + 1, it.col) }
                    Key.DirectionLeft -> head?.let { GridPosition(it.row, it.col - 1) }
                    Key.DirectionRight -> head?.let { GridPosition(it.row, it.col + 1) }
                    Key.Spacebar, Key.Enter, Key.NumPadEnter -> head ?: startCoord?.let { GridPosition(it.row, it.col) }
                    else -> null
                }
                if (target != null && isInputEnabled && onCellEntered != null) {
                    onCellEntered(target)
                    onPointerReleased?.invoke()
                    true
                } else {
                    false
                }
            }
            .focusable(),
        contentAlignment = Alignment.Center
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        val coordinateMapper = remember(widthPx, heightPx, boardState.rowCount, boardState.columnCount) {
            GridCoordinateMapper(
                canvasWidth = widthPx,
                canvasHeight = heightPx,
                rowCount = boardState.rowCount,
                columnCount = boardState.columnCount
            )
        }

        val gridStroke = remember(density) { Stroke(width = with(density) { 1.dp.toPx() }) }
        val cellCornerRadius = remember(density) { CornerRadius(with(density) { 6.dp.toPx() }, with(density) { 6.dp.toPx() }) }
        val coveredInset = remember(density) { with(density) { 2.dp.toPx() } }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .semantics { contentDescription = accessibilityDescription }
                .then(
                    if (onCellEntered != null) {
                        Modifier.puzzleTouchInput(
                            coordinateMapper = coordinateMapper,
                            enabled = isInputEnabled,
                            isTapMode = isTapMode,
                            onCellEntered = onCellEntered,
                            onPointerReleased = onPointerReleased
                        )
                    } else if (onCellClick != null) {
                        Modifier.pointerInput(boardState, coordinateMapper) {
                            detectTapGestures { offset ->
                                coordinateMapper.offsetToGridPosition(offset)?.let(onCellClick)
                            }
                        }
                    } else {
                        Modifier
                    }
                )
        ) {
            val currentPulseAlpha = pulseAlphaState.value
            val currentHeadPulseScale = headPulseScaleState.value

            // 1. Draw Cell Backgrounds & Subtle Grid Outlines
            drawGrid(boardState, coordinateMapper, palette, gridStroke, cellCornerRadius)

            // 2. Draw Covered Cell Background Highlights
            drawCoveredCells(boardState, coordinateMapper, palette, coveredInset, cellCornerRadius)

            // 3. Draw Continuous Glowing Path Segments
            drawContinuousPath(boardState, coordinateMapper, palette, pathEffectId, isReducedMotion, currentPulseAlpha, sharedPath)

            // 4. Draw Numbered Checkpoints with non-color-only distinct cues
            drawCheckpoints(boardState, coordinateMapper, textMeasurer, currentPulseAlpha, palette, checkpointTextCache, sharedDiamondPath)

            // 5. Draw Blocked Edge Walls
            drawWalls(boardState, coordinateMapper, palette)

            // 6. Draw Path Head Marker with animated pulse and rejection flash
            drawPathHead(boardState, coordinateMapper, currentHeadPulseScale, palette, currentRejection)

            // 7. Draw Hinted Cell Highlight (Prompt 14 & 15)
            drawHintedCell(boardState, coordinateMapper, currentPulseAlpha)
        }

        // 8. Accessible Virtual Cell Grid Overlay for TalkBack, Switch Access, and Keyboard Navigation (Prompt 39)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .semantics {
                    isTraversalGroup = true
                }
        ) {
            for (r in 0 until boardState.rowCount) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    for (c in 0 until boardState.columnCount) {
                        val pos = GridPosition(r, c)
                        val cellDesc = remember(boardState, r, c, isInputEnabled) {
                            buildCellAccessibilityDescription(boardState, pos, isInputEnabled)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .semantics {
                                    contentDescription = cellDesc
                                    if (isInputEnabled) {
                                        if (onCellEntered != null) {
                                            onClick(label = "Move path to cell") {
                                                onCellEntered(pos)
                                                onPointerReleased?.invoke()
                                                true
                                            }
                                        } else if (onCellClick != null) {
                                            onClick(label = "Select cell") {
                                                onCellClick(pos)
                                                true
                                            }
                                        }
                                    }
                                }
                                .focusable()
                        )
                    }
                }
            }
        }
    }
}

/**
 * Builds rich, accessible descriptions for individual grid cells in TalkBack and Switch Access (Prompt 39).
 * Describes position, checkpoint status, path progress, wall edges, and accessible action cues.
 */
fun buildCellAccessibilityDescription(
    boardState: PuzzleBoardState,
    pos: GridPosition,
    isInputEnabled: Boolean
): String {
    val r = pos.row
    val c = pos.col
    val checkpoint = boardState.getCheckpointAt(pos)
    val isStart = checkpoint == 1
    val isFinal = checkpoint == boardState.maxCheckpointNumber && checkpoint != null
    val isHead = pos == boardState.currentHead
    val isCovered = boardState.isCovered(pos)
    val isHinted = pos == boardState.hintedCoordinate

    val hasWallUp = r > 0 && boardState.hasWallBetween(pos, GridPosition(r - 1, c))
    val hasWallDown = r < boardState.rowCount - 1 && boardState.hasWallBetween(pos, GridPosition(r + 1, c))
    val hasWallLeft = c > 0 && boardState.hasWallBetween(pos, GridPosition(r, c - 1))
    val hasWallRight = c < boardState.columnCount - 1 && boardState.hasWallBetween(pos, GridPosition(r, c + 1))

    return buildString {
        append("Row ${r + 1}, column ${c + 1}. ")
        when {
            isStart -> append("Checkpoint 1, start. ")
            isFinal -> append("Checkpoint $checkpoint, final goal. ")
            checkpoint != null -> append("Checkpoint $checkpoint. ")
            else -> append("Empty cell. ")
        }
        when {
            isHead -> append("Current path endpoint. ")
            isCovered -> append("Visited on path. ")
            else -> append("Unvisited cell. ")
        }
        if (isHinted) {
            append("Hinted next step. ")
        }
        val walls = mutableListOf<String>()
        if (hasWallUp) walls.add("above")
        if (hasWallDown) walls.add("below")
        if (hasWallLeft) walls.add("to the left")
        if (hasWallRight) walls.add("to the right")
        if (walls.isNotEmpty()) {
            append("Blocked wall edge ${walls.joinToString(", ")}. ")
        }
        if (isInputEnabled) {
            val head = boardState.currentHead
            val isAdjacentToHead = head?.let { h ->
                val dr = kotlin.math.abs(h.row - r)
                val dc = kotlin.math.abs(h.col - c)
                (dr == 1 && dc == 0) || (dr == 0 && dc == 1)
            } ?: false

            if (head == null && isStart) {
                append("Double tap to start path.")
            } else if (isAdjacentToHead && !isCovered) {
                append("Adjacent valid move. Double tap to move path here.")
            } else if (isHead && boardState.path.size > 1) {
                append("Double tap to backtrack one step.")
            }
        }
    }
}

private fun DrawScope.drawGrid(
    board: PuzzleBoardState,
    mapper: GridCoordinateMapper,
    palette: ZynpathColorPalette,
    stroke: Stroke,
    cornerRadius: CornerRadius
) {
    val tileInset = 1.5.dp.toPx()
    for (r in 0 until board.rowCount) {
        for (c in 0 until board.columnCount) {
            val bounds = mapper.getCellBounds(GridPosition(r, c))
            val tileLeft = bounds.left + tileInset
            val tileTop = bounds.top + tileInset
            val tileSize = Size(bounds.width - tileInset * 2, bounds.height - tileInset * 2)

            // 1. Physical unvisited tile body
            drawRoundRect(
                color = Color(0x35122146),
                topLeft = Offset(tileLeft, tileTop),
                size = tileSize,
                cornerRadius = cornerRadius
            )
            // 2. Tile border outline
            drawRoundRect(
                color = palette.boardCellBorder.copy(alpha = 0.65f),
                topLeft = Offset(tileLeft, tileTop),
                size = tileSize,
                cornerRadius = cornerRadius,
                style = stroke
            )
        }
    }
}

private fun DrawScope.drawCoveredCells(
    board: PuzzleBoardState,
    mapper: GridCoordinateMapper,
    palette: ZynpathColorPalette,
    inset: Float,
    cornerRadius: CornerRadius
) {
    val isCompleted = board.isCompleted
    val fillColor = if (isCompleted) {
        Color(0x35FFC247) // Warm celebratory gold tint when puzzle is solved
    } else {
        Color(0x2D21D4FD) // Illuminated electric cyan tint
    }
    val rimColor = if (isCompleted) {
        Color(0x77FFC247)
    } else {
        Color(0x5521D4FD)
    }

    board.path.forEach { coord ->
        val bounds = mapper.getCellBounds(coord)
        val cellDimension = mapper.cellSize - (inset * 2)
        val topLeft = Offset(bounds.left + inset, bounds.top + inset)
        val size = Size(cellDimension, cellDimension)

        // Filled illuminated tile body
        drawRoundRect(
            color = fillColor,
            topLeft = topLeft,
            size = size,
            cornerRadius = cornerRadius
        )
        // Delicate glowing inner rim
        drawRoundRect(
            color = rimColor,
            topLeft = topLeft,
            size = size,
            cornerRadius = cornerRadius,
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

private class CheckpointTextCache {
    private var lastCellSize: Float = -1f
    private val cache = mutableMapOf<Triple<Int, Color, Float>, androidx.compose.ui.text.TextLayoutResult>()

    fun get(
        number: Int,
        color: Color,
        cellSize: Float,
        textMeasurer: TextMeasurer
    ): androidx.compose.ui.text.TextLayoutResult {
        if (lastCellSize != cellSize) {
            cache.clear()
            lastCellSize = cellSize
        }
        return cache.getOrPut(Triple(number, color, cellSize)) {
            textMeasurer.measure(
                text = "$number",
                style = TextStyle(
                    fontSize = (cellSize * 0.30f).coerceAtLeast(10f).sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
        }
    }
}

private fun DrawScope.drawContinuousPath(
    board: PuzzleBoardState,
    mapper: GridCoordinateMapper,
    palette: ZynpathColorPalette,
    pathEffectId: String,
    isReducedMotion: Boolean,
    pulseAlpha: Float,
    reusablePath: Path
) {
    if (board.path.size < 2) return

    if (board.isCompleted) {
        // Celebratory victory gold finishing path across all connected cells
        reusablePath.reset()
        val firstCenter = mapper.getCellCenter(board.path.first())
        reusablePath.moveTo(firstCenter.x, firstCenter.y)

        for (i in 1 until board.path.size) {
            val center = mapper.getCellCenter(board.path[i])
            reusablePath.lineTo(center.x, center.y)
        }

        val victoryPulse = if (isReducedMotion) 1f else (0.90f + 0.25f * pulseAlpha)
        // Outer glowing gold aura
        drawPath(
            path = reusablePath,
            color = Color(0xFFFFC247).copy(alpha = if (isReducedMotion) 0.50f else 0.40f + 0.25f * pulseAlpha),
            style = Stroke(
                width = mapper.cellSize * 0.36f * victoryPulse,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
        // Radiant golden core
        drawPath(
            path = reusablePath,
            color = Color(0xFFFFE27A),
            style = Stroke(
                width = mapper.cellSize * 0.18f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
        // Pure white starlight central beam
        drawPath(
            path = reusablePath,
            color = Color.White.copy(alpha = 0.90f),
            style = Stroke(
                width = mapper.cellSize * 0.07f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
        return
    }

    // 1. Identify clue waypoint indices along the single continuous path
    val clueIndices = mutableListOf<Int>()
    board.path.forEachIndexed { idx, coord ->
        if (board.getCheckpointAt(coord) != null) {
            clueIndices.add(idx)
        }
    }

    // 2. Draw completed clue-to-clue segments with confirmed crystalline sheen
    if (clueIndices.size > 1) {
        for (s in 0 until clueIndices.size - 1) {
            val startIdx = clueIndices[s]
            val endIdx = clueIndices[s + 1]
            if (endIdx > startIdx) {
                reusablePath.reset()
                val startCenter = mapper.getCellCenter(board.path[startIdx])
                reusablePath.moveTo(startCenter.x, startCenter.y)
                for (i in startIdx + 1..endIdx) {
                    val c = mapper.getCellCenter(board.path[i])
                    reusablePath.lineTo(c.x, c.y)
                }

                // Completed segment aura: luminous confirmed crystalline cyan
                drawPath(
                    path = reusablePath,
                    color = palette.pathGlowColor.copy(alpha = 0.30f),
                    style = Stroke(
                        width = mapper.cellSize * 0.28f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
                // Solid core
                drawPath(
                    path = reusablePath,
                    color = palette.pathCoreColor,
                    style = Stroke(
                        width = mapper.cellSize * 0.15f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
                // White laser spine
                drawPath(
                    path = reusablePath,
                    color = Color.White.copy(alpha = 0.75f),
                    style = Stroke(
                        width = mapper.cellSize * 0.045f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }

    // 3. Draw active in-progress segment: from last reached checkpoint to current path endpoint
    val activeStartIdx = if (clueIndices.isNotEmpty()) clueIndices.last() else 0
    if (board.path.size - 1 > activeStartIdx) {
        reusablePath.reset()
        val startCenter = mapper.getCellCenter(board.path[activeStartIdx])
        reusablePath.moveTo(startCenter.x, startCenter.y)
        for (i in activeStartIdx + 1 until board.path.size) {
            val c = mapper.getCellCenter(board.path[i])
            reusablePath.lineTo(c.x, c.y)
        }

        when (pathEffectId) {
            "path_gold_shimmer" -> {
                drawPath(
                    path = reusablePath,
                    color = Color(0xFFFFB703).copy(alpha = 0.40f),
                    style = Stroke(
                        width = mapper.cellSize * 0.30f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
                drawPath(
                    path = reusablePath,
                    color = Color(0xFFFFE082),
                    style = Stroke(
                        width = mapper.cellSize * 0.16f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
                drawPath(
                    path = reusablePath,
                    color = Color.White.copy(alpha = 0.85f),
                    style = Stroke(
                        width = mapper.cellSize * 0.06f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
            "path_ember_trail" -> {
                drawPath(
                    path = reusablePath,
                    color = Color(0xFFFF5722).copy(alpha = 0.45f),
                    style = Stroke(
                        width = mapper.cellSize * 0.32f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
                drawPath(
                    path = reusablePath,
                    color = Color(0xFFFFD166),
                    style = Stroke(
                        width = mapper.cellSize * 0.17f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
            else -> {
                // Default & path_cyan_pulse: High-energy electric cyan with live pulsing aura
                val pulseMultiplier = if (isReducedMotion) 1f else (0.88f + 0.32f * pulseAlpha)
                val glowAlpha = if (isReducedMotion) 0.40f else (0.26f + 0.30f * pulseAlpha)

                drawPath(
                    path = reusablePath,
                    color = palette.pathGlowColor.copy(alpha = glowAlpha),
                    style = Stroke(
                        width = mapper.cellSize * 0.32f * pulseMultiplier,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
                drawPath(
                    path = reusablePath,
                    color = palette.pathCoreColor,
                    style = Stroke(
                        width = mapper.cellSize * 0.17f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
                drawPath(
                    path = reusablePath,
                    color = Color.White.copy(alpha = 0.90f),
                    style = Stroke(
                        width = mapper.cellSize * 0.055f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }

    // 4. Subtle directional flow chevrons along path edges
    val chevronSize = mapper.cellSize * 0.065f
    val chevronPath = reusablePath
    for (i in 0 until board.path.size - 1) {
        val c1 = mapper.getCellCenter(board.path[i])
        val c2 = mapper.getCellCenter(board.path[i + 1])
        val midX = (c1.x + c2.x) / 2f
        val midY = (c1.y + c2.y) / 2f

        val dx = c2.x - c1.x
        val dy = c2.y - c1.y
        val len = kotlin.math.sqrt(dx * dx + dy * dy)
        if (len > 0f) {
            val ux = dx / len
            val uy = dy / len
            val nx = -uy
            val ny = ux

            chevronPath.reset()
            chevronPath.moveTo(midX + ux * chevronSize, midY + uy * chevronSize)
            chevronPath.lineTo(midX - ux * chevronSize + nx * chevronSize, midY - uy * chevronSize + ny * chevronSize)
            chevronPath.moveTo(midX + ux * chevronSize, midY + uy * chevronSize)
            chevronPath.lineTo(midX - ux * chevronSize - nx * chevronSize, midY - uy * chevronSize - ny * chevronSize)

            drawPath(
                path = chevronPath,
                color = Color.White.copy(alpha = 0.55f),
                style = Stroke(width = 1.2.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

/**
 * Draws numbered checkpoints with distinct visual cues not relying on color alone (Prompt 15 Section 9).
 */
private fun DrawScope.drawCheckpoints(
    board: PuzzleBoardState,
    mapper: GridCoordinateMapper,
    textMeasurer: TextMeasurer,
    pulseAlpha: Float,
    palette: ZynpathColorPalette,
    textCache: CheckpointTextCache,
    reusableDiamondPath: Path
) {
    val radius = mapper.cellSize * 0.34f

    board.checkpoints.forEach { (coord, number) ->
        val center = mapper.getCellCenter(coord)
        val isStart = number == 1
        val isFinal = number == board.maxCheckpointNumber
        val isVisited = board.isCovered(coord)
        val isNext = !isVisited && (number == board.nextRequiredCheckpoint)

        // 1. Outer Halos
        if (board.isCompleted) {
            drawCircle(
                color = AccentGold.copy(alpha = pulseAlpha * 0.75f),
                radius = radius * 1.36f,
                center = center
            )
        } else if (isStart && !isVisited) {
            drawCircle(
                color = CellStartHalo.copy(alpha = pulseAlpha),
                radius = radius * 1.38f,
                center = center
            )
        } else if (isNext) {
            drawCircle(
                color = PathCyanSubtle.copy(alpha = pulseAlpha),
                radius = radius * 1.35f,
                center = center
            )
        } else if (isFinal && !isVisited) {
            drawCircle(
                color = AccentGold.copy(alpha = pulseAlpha * 0.8f),
                radius = radius * 1.35f,
                center = center
            )
        }

        // 2. Main Checkpoint Circle Body
        val bodyColor = when {
            board.isCompleted -> AccentGold
            isStart -> ForestMint
            isVisited && isFinal -> AccentGold
            isVisited -> PathCyanGlow
            isFinal && !isVisited -> Color(0xFF261D10)
            else -> CheckpointDark
        }
        drawCircle(
            color = bodyColor,
            radius = radius,
            center = center
        )

        // 3. Distinct Checkpoint Borders & Non-Color Identifiers
        when {
            board.isCompleted -> {
                // Celebratory gold completion border
                drawCircle(
                    color = AccentGold,
                    radius = radius,
                    center = center,
                    style = Stroke(width = 2.5.dp.toPx())
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.85f),
                    radius = radius * 1.15f,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
            isVisited -> {
                // High-visibility visited node: clean white-cyan outer ring + inner concentric ring
                drawCircle(
                    color = Color.White.copy(alpha = 0.85f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.40f),
                    radius = radius * 0.65f,
                    center = center,
                    style = Stroke(width = 1.2.dp.toPx())
                )
            }
            isNext -> {
                // Double concentric outer ring for Next Required (non-color indicator)
                drawCircle(
                    color = PathCyanGlow,
                    radius = radius,
                    center = center,
                    style = Stroke(width = 2.5.dp.toPx())
                )
                drawCircle(
                    color = PathCyanGlow.copy(alpha = 0.7f),
                    radius = radius * 1.15f,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
                // 4 cardinal directional accent pips around Next Required
                val pipDist = radius * 1.25f
                val pipRadius = 2.dp.toPx()
                drawCircle(PathCyanGlow, pipRadius, Offset(center.x, center.y - pipDist))
                drawCircle(PathCyanGlow, pipRadius, Offset(center.x, center.y + pipDist))
                drawCircle(PathCyanGlow, pipRadius, Offset(center.x - pipDist, center.y))
                drawCircle(PathCyanGlow, pipRadius, Offset(center.x + pipDist, center.y))
            }
            isFinal -> {
                // Gold outer border + distinct outer gold ring
                drawCircle(
                    color = AccentGold,
                    radius = radius,
                    center = center,
                    style = Stroke(width = 2.5.dp.toPx())
                )
                drawCircle(
                    color = AccentGold.copy(alpha = 0.6f),
                    radius = radius * 1.16f,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
                // Distinct crown diamond at top of final checkpoint (reusable path)
                val diamondSize = 3.5.dp.toPx()
                reusableDiamondPath.reset()
                reusableDiamondPath.moveTo(center.x, center.y - radius * 1.30f - diamondSize)
                reusableDiamondPath.lineTo(center.x + diamondSize, center.y - radius * 1.30f)
                reusableDiamondPath.lineTo(center.x, center.y - radius * 1.30f + diamondSize)
                reusableDiamondPath.lineTo(center.x - diamondSize, center.y - radius * 1.30f)
                reusableDiamondPath.close()
                drawPath(reusableDiamondPath, AccentGold)
            }
            isStart -> {
                // Forest mint solid border
                drawCircle(
                    color = ForestMint,
                    radius = radius,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            else -> {
                // Upcoming / not reached: clean white border
                drawCircle(
                    color = Color.White.copy(alpha = 0.65f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
        }

        // 4. Checkpoint Number Text (dynamically scaled, cached via CheckpointTextCache and high contrast)
        val textColor = when {
            board.isCompleted -> CheckpointDark
            isStart -> CheckpointDark
            isVisited && isFinal -> CheckpointDark
            isVisited -> CheckpointDark
            isNext -> PathCyanGlow
            isFinal && !isVisited -> AccentGold
            else -> CheckpointTextWhite
        }

        val textLayoutResult = textCache.get(number, textColor, mapper.cellSize, textMeasurer)
        val textOffset = Offset(
            x = center.x - textLayoutResult.size.width / 2f,
            y = center.y - textLayoutResult.size.height / 2f
        )
        drawText(textLayoutResult, topLeft = textOffset)
    }
}

/**
 * Draws blocked edge walls precisely between adjacent cells (Prompt 15 Section 10).
 */
private fun DrawScope.drawWalls(
    board: PuzzleBoardState,
    mapper: GridCoordinateMapper,
    palette: ZynpathColorPalette
) {
    val wallThickness = max(3.5.dp.toPx(), mapper.cellSize * 0.08f)
    val wallInset = mapper.cellSize * 0.06f

    board.walls.forEach { wall ->
        val c1 = wall.cell1
        val c2 = wall.cell2

        if (wall.isHorizontalBoundary) {
            val topRow = minOf(c1.row, c2.row)
            val wallY = mapper.originY + (topRow + 1) * mapper.cellSize
            val startX = mapper.originX + c1.column * mapper.cellSize + wallInset
            val endX = mapper.originX + (c1.column + 1) * mapper.cellSize - wallInset

            // Glowing aura
            drawLine(
                color = palette.wallGlow,
                start = Offset(startX, wallY),
                end = Offset(endX, wallY),
                strokeWidth = wallThickness * 1.8f,
                cap = StrokeCap.Round
            )
            // Solid wall line
            drawLine(
                color = palette.wallColor,
                start = Offset(startX, wallY),
                end = Offset(endX, wallY),
                strokeWidth = wallThickness,
                cap = StrokeCap.Round
            )
        } else if (wall.isVerticalBoundary) {
            val leftCol = minOf(c1.column, c2.column)
            val wallX = mapper.originX + (leftCol + 1) * mapper.cellSize
            val startY = mapper.originY + c1.row * mapper.cellSize + wallInset
            val endY = mapper.originY + (c1.row + 1) * mapper.cellSize - wallInset

            // Glowing aura
            drawLine(
                color = palette.wallGlow,
                start = Offset(wallX, startY),
                end = Offset(wallX, endY),
                strokeWidth = wallThickness * 1.8f,
                cap = StrokeCap.Round
            )
            // Solid wall line
            drawLine(
                color = palette.wallColor,
                start = Offset(wallX, startY),
                end = Offset(wallX, endY),
                strokeWidth = wallThickness,
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * Draws animated glowing path head marker at current endpoint (Prompt 15 Section 8).
 */
private fun DrawScope.drawPathHead(
    board: PuzzleBoardState,
    mapper: GridCoordinateMapper,
    headScale: Float,
    palette: ZynpathColorPalette,
    rejectionAlpha: Float = 0f
) {
    val head = board.currentHead ?: return
    val center = mapper.getCellCenter(head)

    drawCircle(
        color = palette.pathHeadHalo,
        radius = mapper.cellSize * 0.24f * headScale,
        center = center
    )
    drawCircle(
        color = if (rejectionAlpha > 0f) Color(0xFFFF5252) else palette.pathCoreColor,
        radius = mapper.cellSize * 0.14f,
        center = center
    )
    drawCircle(
        color = Color.White,
        radius = mapper.cellSize * 0.07f,
        center = center
    )
    if (rejectionAlpha > 0f) {
        drawCircle(
            color = Color(0xFFFF5252).copy(alpha = (rejectionAlpha * 0.85f).coerceAtMost(1f)),
            radius = mapper.cellSize * 0.30f * (1f + 0.25f * rejectionAlpha),
            center = center,
            style = Stroke(width = 2.5.dp.toPx())
        )
    }
}

/**
 * Draws subtle visual indication of recommended next step (Prompt 14 & 15).
 */
private fun DrawScope.drawHintedCell(
    board: PuzzleBoardState,
    mapper: GridCoordinateMapper,
    pulseAlpha: Float
) {
    val hint = board.hintedCoordinate ?: return
    val center = mapper.getCellCenter(hint)
    val radius = mapper.cellSize * 0.36f

    // Soft glowing halo with animated pulse
    drawCircle(
        color = AccentGold.copy(alpha = pulseAlpha * 0.8f),
        radius = radius * 1.3f,
        center = center
    )
    // Distinct stroked guidance ring
    drawCircle(
        color = AccentGold,
        radius = radius,
        center = center,
        style = Stroke(
            width = 2.5.dp.toPx(),
            cap = StrokeCap.Round
        )
    )
    // Focal guidance center pip
    drawCircle(
        color = AccentGold.copy(alpha = 0.90f),
        radius = radius * 0.22f,
        center = center
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun PuzzleBoardPreview() {
    PuzzleBoard(
        boardState = SamplePuzzles.Sample3x3_Solved
    )
}
