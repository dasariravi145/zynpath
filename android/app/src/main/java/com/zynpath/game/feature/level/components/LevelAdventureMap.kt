package com.zynpath.game.feature.level.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.components.LevelCardData
import com.zynpath.game.core.designsystem.components.LevelState
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import kotlin.math.sin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import com.zynpath.game.core.designsystem.theme.ChapterTheme
import com.zynpath.game.core.designsystem.theme.ChapterThemes
import com.zynpath.game.core.designsystem.theme.DecorativePatternType
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameWhite

/**
 * Computes horizontal fraction along the smooth sinusoidal adventure trail.
 * Keeps checkpoint nodes between 18% and 82% of screen width.
 */
fun getAdventurePathXFraction(index: Int): Float {
    return 0.50f + 0.30f * sin(index * 0.85f)
}

/**
 * Vertical adventure map displaying sequential level checkpoints along a winding S-curve.
 *
 * Requirements fulfilled (Prompt 05/24 Tasks 2, 3, 8, 9, 10, Prompt 30 Tasks 4, 6):
 * - Viewport-aware lazy rendering with stable keys.
 * - Displays 7 distinct chapters with thematic milestone banners.
 * - Smooth mathematically continuous cubic bezier connecting paths across lazy rows.
 * - Clear distinction between completed, current pulsing, and locked levels.
 * - Chapter-specific floating environmental details and waypoint markers.
 * - Auto-scrolls to the player's active unlocked level on initial composition.
 * - Responsive across all phone sizes without clipping.
 */
@Composable
fun LevelAdventureMap(
    levels: List<LevelCardData>,
    onLevelClick: (LevelCardData) -> Unit,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
    isReducedMotion: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(bottom = 120.dp, top = 16.dp)
) {
    // Auto-scroll to center the player's current unlocked level
    LaunchedEffect(levels) {
        val currentIndex = levels.indexOfFirst { it.isCurrent || it.state == LevelState.UNLOCKED }
        if (currentIndex > 0) {
            lazyListState.scrollToItem((currentIndex - 1).coerceAtLeast(0))
        }
    }

    LazyColumn(
        state = lazyListState,
        contentPadding = contentPadding,
        modifier = modifier.fillMaxSize()
    ) {
        itemsIndexed(
            items = levels,
            key = { _, level -> level.levelNumber }
        ) { index, level ->
            val prevX = if (index > 0) getAdventurePathXFraction(index - 1) else getAdventurePathXFraction(index)
            val currX = getAdventurePathXFraction(index)
            val nextX = if (index < levels.size - 1) getAdventurePathXFraction(index + 1) else currX
            val chapterTheme = ChapterThemes.getThemeForLevel(level.levelNumber)

            // Chapter Transition Banner at chapter boundaries
            val isNewChapter = index == 0 ||
                chapterTheme.chapterId != ChapterThemes.getThemeForLevel(levels[index - 1].levelNumber).chapterId

            Column(modifier = Modifier.fillMaxWidth()) {
                if (isNewChapter) {
                    ChapterMapBanner(
                        chapterTheme = chapterTheme,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                    )
                }

                AdventurePathRow(
                    index = index,
                    level = level,
                    prevXFraction = prevX,
                    currXFraction = currX,
                    nextXFraction = nextX,
                    isFirst = index == 0,
                    isLast = index == levels.size - 1,
                    onClick = { onLevelClick(level) },
                    isReducedMotion = isReducedMotion,
                    chapterTheme = chapterTheme
                )
            }
        }
    }
}

/**
 * Thematic chapter transition banner displayed directly along the World Map adventure trail.
 */
@Composable
fun ChapterMapBanner(
    chapterTheme: ChapterTheme,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(chapterTheme.surfaceBrush)
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        chapterTheme.primaryAccent.copy(alpha = 0.55f),
                        chapterTheme.secondaryAccent.copy(alpha = 0.35f),
                        Color.Transparent
                    )
                ),
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                chapterTheme.primaryAccent.copy(alpha = 0.2f),
                                RoundedCornerShape(6.dp)
                            )
                            .border(
                                1.dp,
                                chapterTheme.primaryAccent.copy(alpha = 0.5f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "CHAPTER ${chapterTheme.chapterId}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = chapterTheme.primaryAccent,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Text(
                        text = "Levels ${chapterTheme.startLevel}–${chapterTheme.endLevel}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = GameSecondaryText
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = chapterTheme.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GameWhite,
                    letterSpacing = 0.4.sp
                )

                Text(
                    text = chapterTheme.visualDirection,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = chapterTheme.secondaryAccent.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
private fun AdventurePathRow(
    index: Int,
    level: LevelCardData,
    prevXFraction: Float,
    currXFraction: Float,
    nextXFraction: Float,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: () -> Unit,
    isReducedMotion: Boolean,
    chapterTheme: ChapterTheme = ChapterThemes.getThemeForLevel(level.levelNumber)
) {
    val rowHeightDp = 106.dp
    val nodeCenterYDp = 48.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeightDp)
    ) {
        // Continuous Bezier Path Connecting Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val rowH = size.height
            val nodeY = nodeCenterYDp.toPx()

            val xPrev = prevXFraction * w
            val xCurr = currXFraction * w
            val xNext = nextXFraction * w

            val isCompleted = level.state == LevelState.COMPLETED
            val isCurrent = level.isCurrent || (level.state == LevelState.UNLOCKED && !isCompleted)

            // 1. Incoming Path from previous row (top boundary to current node)
            if (!isFirst) {
                val boundaryX = (xPrev + xCurr) / 2f
                val incomingPath = Path().apply {
                    moveTo(boundaryX, 0f)
                    cubicTo(
                        boundaryX, nodeY * 0.45f,
                        xCurr, nodeY * 0.65f,
                        xCurr, nodeY
                    )
                }

                // Glow shadow
                if (isCompleted || isCurrent) {
                    drawPath(
                        path = incomingPath,
                        color = chapterTheme.mapPathGlow,
                        style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Core stroke
                drawPath(
                    path = incomingPath,
                    color = if (isCompleted) {
                        chapterTheme.nodeCompletedGlow
                    } else if (isCurrent) {
                        GameGoldHighlight
                    } else {
                        GameRoyalBlue.copy(alpha = 0.4f)
                    },
                    style = if (level.state == LevelState.LOCKED) {
                        Stroke(
                            width = 2.4.dp.toPx(),
                            cap = StrokeCap.Round,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                        )
                    } else {
                        Stroke(width = 3.2.dp.toPx(), cap = StrokeCap.Round)
                    }
                )
            }

            // 2. Outgoing Path to next row (current node to bottom boundary)
            if (!isLast) {
                val boundaryX = (xCurr + xNext) / 2f
                val outgoingPath = Path().apply {
                    moveTo(xCurr, nodeY)
                    cubicTo(
                        xCurr, nodeY + (rowH - nodeY) * 0.35f,
                        boundaryX, nodeY + (rowH - nodeY) * 0.65f,
                        boundaryX, rowH
                    )
                }

                // Glow shadow
                if (isCompleted) {
                    drawPath(
                        path = outgoingPath,
                        color = chapterTheme.mapPathGlow,
                        style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Core stroke
                drawPath(
                    path = outgoingPath,
                    color = if (isCompleted) chapterTheme.nodeCompletedGlow else GameRoyalBlue.copy(alpha = 0.4f),
                    style = if (level.state == LevelState.LOCKED || !isCompleted) {
                        Stroke(
                            width = 2.4.dp.toPx(),
                            cap = StrokeCap.Round,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                        )
                    } else {
                        Stroke(width = 3.2.dp.toPx(), cap = StrokeCap.Round)
                    }
                )

                // Waypoint Stepping Dot at the row boundary
                drawCircle(
                    color = if (isCompleted) chapterTheme.primaryAccent else GameRoyalBlue.copy(alpha = 0.5f),
                    radius = 3.5.dp.toPx(),
                    center = Offset(boundaryX, rowH)
                )
            }

            // 3. Thematic floating environmental detail based on chapter pattern
            if (index % 3 == 1) {
                val decorX = (1f - currXFraction) * w
                val decorY = nodeY + 12.dp.toPx()

                when (chapterTheme.decorativePattern) {
                    DecorativePatternType.CALM_STARS -> {
                        // 4-pointed calm star
                        val starPath = Path().apply {
                            moveTo(decorX, decorY - 8.dp.toPx())
                            lineTo(decorX + 2.dp.toPx(), decorY - 2.dp.toPx())
                            lineTo(decorX + 8.dp.toPx(), decorY)
                            lineTo(decorX + 2.dp.toPx(), decorY + 2.dp.toPx())
                            lineTo(decorX, decorY + 8.dp.toPx())
                            lineTo(decorX - 2.dp.toPx(), decorY + 2.dp.toPx())
                            lineTo(decorX - 8.dp.toPx(), decorY)
                            lineTo(decorX - 2.dp.toPx(), decorY - 2.dp.toPx())
                            close()
                        }
                        drawPath(starPath, color = chapterTheme.primaryAccent.copy(alpha = 0.25f))
                        drawPath(starPath, color = chapterTheme.secondaryAccent.copy(alpha = 0.4f), style = Stroke(width = 1.dp.toPx()))
                    }
                    DecorativePatternType.CYAN_CIRCUITS -> {
                        // Circuit trace
                        val circuitPath = Path().apply {
                            moveTo(decorX - 8.dp.toPx(), decorY)
                            lineTo(decorX, decorY)
                            lineTo(decorX + 6.dp.toPx(), decorY - 6.dp.toPx())
                        }
                        drawPath(circuitPath, color = chapterTheme.primaryAccent.copy(alpha = 0.4f), style = Stroke(width = 1.5.dp.toPx()))
                        drawCircle(color = chapterTheme.secondaryAccent, radius = 2.5.dp.toPx(), center = Offset(decorX + 6.dp.toPx(), decorY - 6.dp.toPx()))
                    }
                    DecorativePatternType.LUMINOUS_CRYSTAL -> {
                        // Diamond crystal
                        val crystalPath = Path().apply {
                            moveTo(decorX, decorY - 8.dp.toPx())
                            lineTo(decorX + 6.dp.toPx(), decorY)
                            lineTo(decorX, decorY + 8.dp.toPx())
                            lineTo(decorX - 6.dp.toPx(), decorY)
                            close()
                        }
                        drawPath(crystalPath, color = chapterTheme.primaryAccent.copy(alpha = 0.25f))
                        drawPath(crystalPath, color = chapterTheme.secondaryAccent.copy(alpha = 0.45f), style = Stroke(width = 1.dp.toPx()))
                    }
                    DecorativePatternType.GEOMETRIC_TRAILS -> {
                        // Hexagonal node
                        val hexPath = Path().apply {
                            moveTo(decorX - 4.dp.toPx(), decorY - 7.dp.toPx())
                            lineTo(decorX + 4.dp.toPx(), decorY - 7.dp.toPx())
                            lineTo(decorX + 8.dp.toPx(), decorY)
                            lineTo(decorX + 4.dp.toPx(), decorY + 7.dp.toPx())
                            lineTo(decorX - 4.dp.toPx(), decorY + 7.dp.toPx())
                            lineTo(decorX - 8.dp.toPx(), decorY)
                            close()
                        }
                        drawPath(hexPath, color = chapterTheme.primaryAccent.copy(alpha = 0.20f))
                        drawPath(hexPath, color = chapterTheme.secondaryAccent.copy(alpha = 0.40f), style = Stroke(width = 1.dp.toPx()))
                    }
                    DecorativePatternType.ELECTRIC_AURORA -> {
                        // Aurora wave arc
                        val arcPath = Path().apply {
                            moveTo(decorX - 8.dp.toPx(), decorY - 4.dp.toPx())
                            quadraticTo(decorX, decorY + 6.dp.toPx(), decorX + 8.dp.toPx(), decorY - 4.dp.toPx())
                        }
                        drawPath(arcPath, color = chapterTheme.primaryAccent.copy(alpha = 0.45f), style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
                    }
                    DecorativePatternType.ROYAL_CREST -> {
                        // Royal crest dot with gold halo
                        drawCircle(color = chapterTheme.primaryAccent.copy(alpha = 0.25f), radius = 6.dp.toPx(), center = Offset(decorX, decorY))
                        drawCircle(color = GameGoldHighlight, radius = 2.5.dp.toPx(), center = Offset(decorX, decorY))
                    }
                    DecorativePatternType.COSMIC_FINALE -> {
                        // Cosmic nova particle
                        drawCircle(color = chapterTheme.primaryAccent.copy(alpha = 0.20f), radius = 8.dp.toPx(), center = Offset(decorX, decorY))
                        drawCircle(color = GameGoldHighlight, radius = 3.dp.toPx(), center = Offset(decorX, decorY))
                    }
                }
            }
        }

        // Checkpoint Node placed at exact calculated coordinates
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeightDp)
        ) {
            val totalWidthPx = constraints.maxWidth.toFloat()
            val targetXPx = totalWidthPx * currXFraction
            val nodeSizeDp = 62.dp
            val density = LocalDensity.current
            val nodeRadiusPx = with(density) { (nodeSizeDp / 2).toPx() }
            val xOffsetDp = with(density) { (targetXPx - nodeRadiusPx).toDp() }
            val yOffsetDp = nodeCenterYDp - 31.dp

            LevelCheckpointNode(
                level = level,
                onClick = onClick,
                isReducedMotion = isReducedMotion,
                chapterTheme = chapterTheme,
                modifier = Modifier.offset(x = xOffsetDp, y = yOffsetDp)
            )
        }
    }
}

