package com.zynpath.game.feature.home.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.components.GamePanel
import com.zynpath.game.core.designsystem.components.GamePrimaryButton
import com.zynpath.game.core.designsystem.components.GameProgressBar
import com.zynpath.game.core.designsystem.components.ProgressBarStyle
import com.zynpath.game.core.designsystem.theme.GameBrightBlue
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameOrangeAccent
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.feature.home.HomeUiState
import com.zynpath.game.core.puzzle.model.WorldConfiguration

/**
 * Primary centerpiece card on the Zynpath Home Screen highlighting Solo Campaign gameplay.
 * Features:
 * - Current unlocked level & world header.
 * - Mini illustrated number-path environment showing sequential checkpoints and traveling pulse.
 * - Current world progress bar and star collection summary.
 * - Prominent Gold PLAY NOW / RESUME action button.
 * - Level Selection shortcut.
 */
@Composable
fun SoloCampaignCard(
    uiState: HomeUiState,
    onPlayClick: () -> Unit,
    onWorldMapClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isReducedMotion = uiState.isReducedMotion
    val resumable = uiState.resumableSession

    GamePanel(
        modifier = modifier.fillMaxWidth(),
        contentPadding = 18.dp
    ) {
        // Mode Header & World Name
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(GameElectricCyan.copy(alpha = 0.2f))
                            .border(1.dp, GameElectricCyan.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "CAMPAIGN",
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp,
                            color = GameElectricCyan
                        )
                    }

                    if (resumable != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GameGold.copy(alpha = 0.2f))
                                .border(1.dp, GameGoldHighlight.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "IN PROGRESS",
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp,
                                color = GameGoldHighlight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = uiState.currentWorldName,
                    style = GameTypography.screenHeading.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GameWhite
                    )
                )
            }

            // World Progress Count Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x99101D3C))
                    .border(1.dp, GameRoyalBlue.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = uiState.currentWorldProgressText,
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = GameElectricCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Mini Illustrated Number-Path Preview Environment
        SoloPathPreviewCanvas(
            currentLevel = uiState.nextPlayableLevelId,
            isReducedMotion = isReducedMotion,
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF091636),
                            GameDeepNavy
                        )
                    )
                )
                .border(1.dp, GameRoyalBlue.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(14.dp))

        // World Completion Progress Bar
        val fraction = uiState.nextGoalProgressFraction
        GameProgressBar(
            progress = fraction,
            height = 10.dp,
            style = ProgressBarStyle.CYAN,
            isReducedMotion = isReducedMotion
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Actions: Prominent Gold Primary Button + Level Selection Shortcut
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val playButtonText = if (resumable != null) {
                "RESUME LEVEL ${WorldConfiguration.toLocalLevel(resumable.levelId)}"
            } else {
                "PLAY LEVEL ${WorldConfiguration.toLocalLevel(uiState.nextPlayableLevelId)}"
            }

            GamePrimaryButton(
                text = playButtonText,
                onClick = onPlayClick,
                isReducedMotion = isReducedMotion,
                height = 52.dp,
                modifier = Modifier.weight(1f),
                leadingIcon = {
                    Icon(
                        imageVector = if (resumable != null) Icons.Default.Restore else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = GameDeepNavy,
                        modifier = Modifier.size(24.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.width(10.dp))

            // World Map / Level Selection Shortcut
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xEE14244E))
                    .border(1.2.dp, GameElectricCyan.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                    .clickable(onClick = onWorldMapClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = "World map",
                    tint = GameElectricCyan,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Animated mini-canvas depicting a 3-node sequential puzzle pathway (Previous -> Current Level -> Next Level)
 * with glowing connecting energy beam and moving pulse comet.
 */
@Composable
private fun SoloPathPreviewCanvas(
    currentLevel: Int,
    isReducedMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val infiniteTransition = rememberInfiniteTransition(label = "SoloPathMotion")

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SoloPathPulse"
    )

    val haloBreathing by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HaloBreathing"
    )

    val activePulse = if (isReducedMotion) 0.5f else pulse
    val activeHalo = if (isReducedMotion) 1f else haloBreathing

    val prevLevel = (currentLevel - 1).coerceAtLeast(1)
    val nextLevel = currentLevel + 1

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val p1 = Offset(w * 0.18f, h * 0.55f) // Node 1 (Previous)
        val p2 = Offset(w * 0.50f, h * 0.45f) // Node 2 (Current Level - Hero Node)
        val p3 = Offset(w * 0.82f, h * 0.55f) // Node 3 (Next Level)

        // Connecting Spline
        val spline = Path().apply {
            moveTo(p1.x, p1.y)
            cubicTo(
                w * 0.30f, h * 0.35f,
                w * 0.38f, h * 0.40f,
                p2.x, p2.y
            )
            cubicTo(
                w * 0.62f, h * 0.50f,
                w * 0.70f, h * 0.65f,
                p3.x, p3.y
            )
        }

        // Ambient Glow Ribbon
        drawPath(
            path = spline,
            color = GameRoyalBlue.copy(alpha = 0.4f * activeHalo),
            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Electric Cyan Energy Beam
        drawPath(
            path = spline,
            color = GameElectricCyan.copy(alpha = 0.75f),
            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // White Laser Filament
        drawPath(
            path = spline,
            color = Color.White.copy(alpha = 0.9f),
            style = Stroke(width = 1.2.dp.toPx(), cap = StrokeCap.Round)
        )

        // Traveling Pulse Comet
        if (!isReducedMotion) {
            val pulsePoint = if (activePulse <= 0.5f) {
                val t = activePulse * 2f
                Offset(p1.x + (p2.x - p1.x) * t, p1.y + (p2.y - p1.y) * t)
            } else {
                val t = (activePulse - 0.5f) * 2f
                Offset(p2.x + (p3.x - p2.x) * t, p2.y + (p3.y - p2.y) * t)
            }
            drawCircle(
                color = GameElectricCyan.copy(alpha = 0.6f),
                radius = 7.dp.toPx(),
                center = pulsePoint
            )
            drawCircle(
                color = Color.White,
                radius = 2.5.dp.toPx(),
                center = pulsePoint
            )
        }

        // Draw Node 1: Completed / Previous Level
        drawMiniNode(
            center = p1,
            radius = 16.dp.toPx(),
            text = "$prevLevel",
            isCurrent = false,
            textMeasurer = textMeasurer
        )

        // Draw Node 2: Current Active Level (Hero Checkpoint)
        drawHeroNode(
            center = p2,
            radius = 22.dp.toPx(),
            text = "$currentLevel",
            halo = activeHalo,
            textMeasurer = textMeasurer
        )

        // Draw Node 3: Next Level
        drawMiniNode(
            center = p3,
            radius = 16.dp.toPx(),
            text = "$nextLevel",
            isCurrent = false,
            textMeasurer = textMeasurer
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawMiniNode(
    center: Offset,
    radius: Float,
    text: String,
    isCurrent: Boolean,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    // Pedestal
    drawCircle(
        color = Color(0xFF071228),
        radius = radius * 1.3f,
        center = Offset(center.x, center.y + 2.dp.toPx())
    )

    // Body
    drawCircle(
        color = Color(0xFF14244E),
        radius = radius,
        center = center
    )

    // Border
    drawCircle(
        color = GameRoyalBlue.copy(alpha = 0.7f),
        radius = radius,
        center = center,
        style = Stroke(width = 1.5.dp.toPx())
    )

    val layout = textMeasurer.measure(
        text = text,
        style = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = GameSecondaryText
        )
    )
    drawText(
        textLayoutResult = layout,
        topLeft = Offset(center.x - layout.size.width / 2f, center.y - layout.size.height / 2f)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHeroNode(
    center: Offset,
    radius: Float,
    text: String,
    halo: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    // Pedestal shadow
    drawCircle(
        color = Color(0xFF071228),
        radius = radius * 1.35f,
        center = Offset(center.x, center.y + 3.dp.toPx())
    )

    // Glowing Halo
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                GameGoldHighlight.copy(alpha = 0.5f * halo),
                GameElectricCyan.copy(alpha = 0.2f),
                Color.Transparent
            ),
            radius = radius * 1.7f,
            center = center
        ),
        radius = radius * 1.7f,
        center = center
    )

    // Gold/Orange Gradient Body
    drawCircle(
        brush = Brush.verticalGradient(
            colors = listOf(
                GameGoldHighlight,
                GameGold,
                GameOrangeAccent
            ),
            startY = center.y - radius,
            endY = center.y + radius
        ),
        radius = radius,
        center = center
    )

    // Bevel Rim
    drawCircle(
        color = Color.White.copy(alpha = 0.9f),
        radius = radius - 1.dp.toPx(),
        center = center,
        style = Stroke(width = 1.6.dp.toPx())
    )

    val layout = textMeasurer.measure(
        text = text,
        style = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Black,
            fontSize = 15.sp,
            color = GameDeepNavy
        )
    )
    drawText(
        textLayoutResult = layout,
        topLeft = Offset(center.x - layout.size.width / 2f, center.y - layout.size.height / 2f)
    )
}
