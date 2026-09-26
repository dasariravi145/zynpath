package com.zynpath.game.core.puzzle.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.theme.BoardBackgroundLight
import com.zynpath.game.core.designsystem.theme.BoardCellBorder
import com.zynpath.game.core.designsystem.theme.CellCoveredTint
import com.zynpath.game.core.designsystem.theme.CellStartHalo
import com.zynpath.game.core.designsystem.theme.CheckpointDark
import com.zynpath.game.core.designsystem.theme.CheckpointTextWhite
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.PathCyanGlow
import com.zynpath.game.core.designsystem.theme.PathCyanSubtle
import com.zynpath.game.core.designsystem.theme.WallCrimson
import com.zynpath.game.core.puzzle.model.GridCoordinate
import com.zynpath.game.core.puzzle.model.PuzzleBoardState
import com.zynpath.game.core.puzzle.model.SamplePuzzles
import kotlin.math.min

/**
 * High-performance, data-driven Jetpack Compose puzzle board renderer.
 * Renders rectangular grids, cell checkpoints, wall barriers, and continuous glowing path segments.
 */
@Composable
fun PuzzleBoard(
    boardState: PuzzleBoardState,
    modifier: Modifier = Modifier,
    onCellClick: ((GridCoordinate) -> Unit)? = null
) {
    val textMeasurer = rememberTextMeasurer()
    val aspectRatio = boardState.columnCount.toFloat() / boardState.rowCount.toFloat()

    Box(
        modifier = modifier
            .padding(16.dp)
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(20.dp))
            .background(BoardBackgroundLight),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (onCellClick != null) {
                        Modifier.pointerInput(boardState) {
                            detectTapGestures { offset ->
                                val canvasWidth = size.width
                                val canvasHeight = size.height
                                val cellSize = min(canvasWidth / boardState.columnCount, canvasHeight / boardState.rowCount)
                                val xOffset = (canvasWidth - cellSize * boardState.columnCount) / 2f
                                val yOffset = (canvasHeight - cellSize * boardState.rowCount) / 2f

                                val col = ((offset.x - xOffset) / cellSize).toInt()
                                val row = ((offset.y - yOffset) / cellSize).toInt()

                                if (row in 0 until boardState.rowCount && col in 0 until boardState.columnCount) {
                                    onCellClick(GridCoordinate(row, col))
                                }
                            }
                        }
                    } else Modifier
                )
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val cellSize = min(canvasWidth / boardState.columnCount, canvasHeight / boardState.rowCount)
            val xOffset = (canvasWidth - cellSize * boardState.columnCount) / 2f
            val yOffset = (canvasHeight - cellSize * boardState.rowCount) / 2f

            // 1. Draw Cell Backgrounds & Grid Lines
            drawGrid(boardState, cellSize, xOffset, yOffset)

            // 2. Draw Covered Cell Highlights
            drawCoveredCells(boardState, cellSize, xOffset, yOffset)

            // 3. Draw Continuous Path
            drawContinuousPath(boardState, cellSize, xOffset, yOffset)

            // 4. Draw Checkpoints
            drawCheckpoints(boardState, cellSize, xOffset, yOffset, textMeasurer)

            // 5. Draw Walls (Blocked Connections)
            drawWalls(boardState, cellSize, xOffset, yOffset)

            // 6. Draw Path Head Marker
            drawPathHead(boardState, cellSize, xOffset, yOffset)
        }
    }
}

private fun DrawScope.drawGrid(
    board: PuzzleBoardState,
    cellSize: Float,
    xOffset: Float,
    yOffset: Float
) {
    val stroke = Stroke(width = 1.dp.toPx())
    for (r in 0 until board.rowCount) {
        for (c in 0 until board.columnCount) {
            val cellLeft = xOffset + c * cellSize
            val cellTop = yOffset + r * cellSize
            drawRoundRect(
                color = BoardCellBorder,
                topLeft = Offset(cellLeft, cellTop),
                size = Size(cellSize, cellSize),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                style = stroke
            )
        }
    }
}

private fun DrawScope.drawCoveredCells(
    board: PuzzleBoardState,
    cellSize: Float,
    xOffset: Float,
    yOffset: Float
) {
    board.path.forEach { coord ->
        val cellLeft = xOffset + coord.col * cellSize + 2.dp.toPx()
        val cellTop = yOffset + coord.row * cellSize + 2.dp.toPx()
        val cellDimension = cellSize - 4.dp.toPx()
        drawRoundRect(
            color = CellCoveredTint,
            topLeft = Offset(cellLeft, cellTop),
            size = Size(cellDimension, cellDimension),
            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
        )
    }
}

private fun DrawScope.drawContinuousPath(
    board: PuzzleBoardState,
    cellSize: Float,
    xOffset: Float,
    yOffset: Float
) {
    if (board.path.size < 2) return

    val path = Path()
    val firstCenter = getCellCenter(board.path.first(), cellSize, xOffset, yOffset)
    path.moveTo(firstCenter.x, firstCenter.y)

    for (i in 1 until board.path.size) {
        val center = getCellCenter(board.path[i], cellSize, xOffset, yOffset)
        path.lineTo(center.x, center.y)
    }

    // Outer glow
    drawPath(
        path = path,
        color = PathCyanSubtle.copy(alpha = 0.4f),
        style = Stroke(
            width = (cellSize * 0.28f),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // Inner bright core
    drawPath(
        path = path,
        color = PathCyanGlow,
        style = Stroke(
            width = (cellSize * 0.16f),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

private fun DrawScope.drawCheckpoints(
    board: PuzzleBoardState,
    cellSize: Float,
    xOffset: Float,
    yOffset: Float,
    textMeasurer: TextMeasurer
) {
    val radius = cellSize * 0.32f
    board.checkpoints.forEach { (coord, number) ->
        val center = getCellCenter(coord, cellSize, xOffset, yOffset)
        val isStart = number == 1
        val isVisited = board.isCovered(coord)

        // Halo on start cell or visited checkpoint
        if (isStart) {
            drawCircle(
                color = CellStartHalo,
                radius = radius * 1.35f,
                center = center
            )
        }

        // Checkpoint Circle Body
        drawCircle(
            color = if (isStart) ForestMint else CheckpointDark,
            radius = radius,
            center = center
        )

        // Outer border
        drawCircle(
            color = if (isVisited) PathCyanGlow else Color.White.copy(alpha = 0.6f),
            radius = radius,
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )

        // Number Text
        val textLayoutResult = textMeasurer.measure(
            text = "$number",
            style = TextStyle(
                fontSize = (cellSize * 0.28f).sp,
                fontWeight = FontWeight.Bold,
                color = if (isStart) CheckpointDark else CheckpointTextWhite
            )
        )
        val textOffset = Offset(
            x = center.x - textLayoutResult.size.width / 2f,
            y = center.y - textLayoutResult.size.height / 2f
        )
        drawText(textLayoutResult, topLeft = textOffset)
    }
}

private fun DrawScope.drawWalls(
    board: PuzzleBoardState,
    cellSize: Float,
    xOffset: Float,
    yOffset: Float
) {
    val wallThickness = 5.dp.toPx()
    val wallInset = 4.dp.toPx()

    board.walls.forEach { wall ->
        val c1 = wall.cell1
        val c2 = wall.cell2

        if (wall.isHorizontalBoundary) {
            // Horizontal wall between row min(r1, r2) and max(r1, r2)
            val topRow = minOf(c1.row, c2.row)
            val wallY = yOffset + (topRow + 1) * cellSize
            val startX = xOffset + c1.col * cellSize + wallInset
            val endX = xOffset + (c1.col + 1) * cellSize - wallInset

            drawLine(
                color = WallCrimson,
                start = Offset(startX, wallY),
                end = Offset(endX, wallY),
                strokeWidth = wallThickness,
                cap = StrokeCap.Round
            )
        } else if (wall.isVerticalBoundary) {
            // Vertical wall between col min(c1, c2) and max(c1, c2)
            val leftCol = minOf(c1.col, c2.col)
            val wallX = xOffset + (leftCol + 1) * cellSize
            val startY = yOffset + c1.row * cellSize + wallInset
            val endY = yOffset + (c1.row + 1) * cellSize - wallInset

            drawLine(
                color = WallCrimson,
                start = Offset(wallX, startY),
                end = Offset(wallX, endY),
                strokeWidth = wallThickness,
                cap = StrokeCap.Round
            )
        }
    }
}

private fun DrawScope.drawPathHead(
    board: PuzzleBoardState,
    cellSize: Float,
    xOffset: Float,
    yOffset: Float
) {
    val head = board.currentHead ?: return
    val center = getCellCenter(head, cellSize, xOffset, yOffset)

    drawCircle(
        color = PathCyanGlow,
        radius = cellSize * 0.12f,
        center = center
    )
    drawCircle(
        color = Color.White,
        radius = cellSize * 0.06f,
        center = center
    )
}

private fun getCellCenter(
    coord: GridCoordinate,
    cellSize: Float,
    xOffset: Float,
    yOffset: Float
): Offset {
    return Offset(
        x = xOffset + (coord.col + 0.5f) * cellSize,
        y = yOffset + (coord.row + 0.5f) * cellSize
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun PuzzleBoardPreview() {
    PuzzleBoard(
        boardState = SamplePuzzles.Sample3x3_Solved
    )
}
