package com.zynpath.game.feature.auth.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.theme.GameBrightBlue
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameOrangeAccent
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class SparkleMote(
    val relX: Float,
    val relY: Float,
    val radius: Float,
    val color: Color,
    val speed: Float,
    val phase: Float
)

/**
 * Original animated puzzle-path illustration visually communicating the Number Path Puzzle concept.
 *
 * Features:
 * - 4 sequentially connected floating puzzle checkpoints: 1 -> 2 -> 3 -> 4 (Golden Summit).
 * - Layered environmental depth: floating stone pedestals with subtle levitation dynamics.
 * - Multi-layer glowing electric-cyan path connections with white energy laser cores.
 * - Traveling energy pulse navigating through the path sequence.
 * - Ambient celestial particles and radial royal-blue bloom.
 * - Fully respects [isReducedMotion].
 */
@Composable
fun AnimatedPuzzlePathIllustration(
    modifier: Modifier = Modifier,
    height: Dp = 190.dp,
    isReducedMotion: Boolean = false
) {
    val textMeasurer = rememberTextMeasurer()
    val infiniteTransition = rememberInfiniteTransition(label = "PuzzleIllustrationMotion")

    // Traveling light pulse along the spline (0f to 1f)
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseProgress"
    )

    // Gentle floating breathing oscillation
    val floatWave by infiniteTransition.animateFloat(
        initialValue = -3.5f,
        targetValue = 3.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatWave"
    )

    // Checkpoint halo glow breathing
    val haloPulse by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HaloPulse"
    )

    val activePulse = if (isReducedMotion) 0.5f else pulseProgress
    val activeFloat = if (isReducedMotion) 0f else floatWave
    val activeGlow = if (isReducedMotion) 1f else haloPulse

    val particles = remember {
        val rand = Random(2026)
        List(18) {
            val isGold = rand.nextFloat() > 0.65f
            SparkleMote(
                relX = rand.nextFloat(),
                relY = rand.nextFloat(),
                radius = rand.nextFloat() * 1.8f + 1.0f,
                color = if (isGold) GameGoldHighlight else GameElectricCyan,
                speed = rand.nextFloat() * 0.4f + 0.6f,
                phase = rand.nextFloat()
            )
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val w = size.width
        val h = size.height

        // 1. Ambient Background Glow Behind Puzzle Field
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    GameRoyalBlue.copy(alpha = 0.35f),
                    GameElectricCyan.copy(alpha = 0.10f),
                    Color.Transparent
                )
            ),
            radius = w * 0.42f,
            center = Offset(w * 0.50f, h * 0.50f)
        )

        // 2. Ambient Celestial Dust Particles
        particles.forEach { p ->
            val animatedY = if (isReducedMotion) {
                p.relY * h
            } else {
                ((p.relY - activePulse * 0.12f * p.speed + 1f) % 1f) * h
            }
            val alpha = (0.25f + 0.45f * sin((p.phase + activeGlow).toDouble())).toFloat().coerceIn(0.15f, 0.85f)
            drawCircle(
                color = p.color.copy(alpha = alpha),
                radius = p.radius,
                center = Offset(p.relX * w, animatedY)
            )
        }

        // Checkpoint Node Coordinates (winding S-curve layout across the canvas)
        val p1 = Offset(w * 0.16f, h * 0.72f + activeFloat * 0.8f) // Node 1 (Start)
        val p2 = Offset(w * 0.38f, h * 0.35f - activeFloat * 0.7f) // Node 2
        val p3 = Offset(w * 0.64f, h * 0.68f + activeFloat * 0.6f) // Node 3
        val p4 = Offset(w * 0.85f, h * 0.30f - activeFloat * 0.9f) // Node 4 (Summit Finish)

        // 3. Glowing Connecting Path Spline
        val spline = Path().apply {
            moveTo(p1.x, p1.y)
            cubicTo(
                x1 = w * 0.20f, y1 = h * 0.45f,
                x2 = w * 0.30f, y2 = h * 0.38f,
                x3 = p2.x, y3 = p2.y
            )
            cubicTo(
                x1 = w * 0.46f, y1 = h * 0.32f,
                x2 = w * 0.54f, y2 = h * 0.65f,
                x3 = p3.x, y3 = p3.y
            )
            cubicTo(
                x1 = w * 0.72f, y1 = h * 0.70f,
                x2 = w * 0.78f, y2 = h * 0.36f,
                x3 = p4.x, y3 = p4.y
            )
        }

        // Layer A: Outer Ambient Cyan/Royal Glow Ribbon
        drawPath(
            path = spline,
            color = GameRoyalBlue.copy(alpha = 0.35f * activeGlow),
            style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Layer B: Electric-Cyan Beam
        drawPath(
            path = spline,
            color = GameElectricCyan.copy(alpha = if (isReducedMotion) 0.8f else 0.75f * activeGlow),
            style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Layer C: Core White Energy Filament
        drawPath(
            path = spline,
            color = Color(0xFFF0FBFF),
            style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Layer D: Traveling Energy Pulse Comet
        if (!isReducedMotion) {
            val pulsePoint = evaluateAuthSpline(p1, p2, p3, p4, activePulse, w, h)
            // Outer radiant burst
            drawCircle(
                color = GameElectricCyan.copy(alpha = 0.55f),
                radius = 10.dp.toPx(),
                center = pulsePoint
            )
            // Inner core orb
            drawCircle(
                color = Color.White,
                radius = 4.dp.toPx(),
                center = pulsePoint
            )
        }

        // 4. Checkpoint Nodes (1 -> 2 -> 3 -> 4 Summit)
        val nodeRadius = 19.dp.toPx()

        // Node 1: Start Checkpoint (Cyan Tile)
        drawAuthTile(
            center = p1,
            radius = nodeRadius,
            label = "1",
            isFinal = false,
            glow = activeGlow,
            textMeasurer = textMeasurer,
            isReducedMotion = isReducedMotion
        )

        // Node 2: Checkpoint 2 (Cyan Tile)
        drawAuthTile(
            center = p2,
            radius = nodeRadius,
            label = "2",
            isFinal = false,
            glow = activeGlow,
            textMeasurer = textMeasurer,
            isReducedMotion = isReducedMotion
        )

        // Node 3: Checkpoint 3 (Cyan Tile)
        drawAuthTile(
            center = p3,
            radius = nodeRadius,
            label = "3",
            isFinal = false,
            glow = activeGlow,
            textMeasurer = textMeasurer,
            isReducedMotion = isReducedMotion
        )

        // Node 4: Summit Checkpoint 4 (Radiant Gold Crown Tile)
        drawAuthTile(
            center = p4,
            radius = nodeRadius * 1.12f,
            label = "4",
            isFinal = true,
            glow = activeGlow,
            textMeasurer = textMeasurer,
            isReducedMotion = isReducedMotion
        )
    }
}

/**
 * Evaluates the 3-segment spline point for t in [0..1].
 */
private fun evaluateAuthSpline(p1: Offset, p2: Offset, p3: Offset, p4: Offset, t: Float, w: Float, h: Float): Offset {
    val scaledT = (t.coerceIn(0f, 1f) * 3f)
    return when {
        scaledT <= 1f -> {
            val localT = scaledT
            val c1 = Offset(w * 0.20f, h * 0.45f)
            val c2 = Offset(w * 0.30f, h * 0.38f)
            bezierPoint(p1, c1, c2, p2, localT)
        }
        scaledT <= 2f -> {
            val localT = scaledT - 1f
            val c1 = Offset(w * 0.46f, h * 0.32f)
            val c2 = Offset(w * 0.54f, h * 0.65f)
            bezierPoint(p2, c1, c2, p3, localT)
        }
        else -> {
            val localT = (scaledT - 2f).coerceIn(0f, 1f)
            val c1 = Offset(w * 0.72f, h * 0.70f)
            val c2 = Offset(w * 0.78f, h * 0.36f)
            bezierPoint(p3, c1, c2, p4, localT)
        }
    }
}

private fun bezierPoint(p0: Offset, p1: Offset, p2: Offset, p3: Offset, t: Float): Offset {
    val u = 1f - t
    val tt = t * t
    val uu = u * u
    val uuu = uu * u
    val ttt = tt * t

    val x = uuu * p0.x + 3 * uu * t * p1.x + 3 * u * tt * p2.x + ttt * p3.x
    val y = uuu * p0.y + 3 * uu * t * p1.y + 3 * u * tt * p2.y + ttt * p3.y
    return Offset(x, y)
}

/**
 * Draws an authentic floating number checkpoint tile with foundation pedestal,
 * glowing halo, and embossed numeral or golden summit crown.
 */
private fun DrawScope.drawAuthTile(
    center: Offset,
    radius: Float,
    label: String,
    isFinal: Boolean,
    glow: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    isReducedMotion: Boolean
) {
    // 1. Stone pedestal foundation beneath node
    drawCircle(
        color = Color(0xFF071228),
        radius = radius * 1.35f,
        center = Offset(center.x, center.y + 3.5.dp.toPx())
    )

    if (isFinal) {
        // GOLD SUMMIT CHECKPOINT
        // Corona rays (6 radiant rays)
        val rayCount = 6
        val rayLen = radius * 1.7f
        for (i in 0 until rayCount) {
            val angleRad = Math.toRadians((i * (360.0 / rayCount) + (if (isReducedMotion) 0.0 else glow * 24.0)))
            val endX = (center.x + rayLen * cos(angleRad)).toFloat()
            val endY = (center.y + rayLen * sin(angleRad)).toFloat()
            drawLine(
                color = GameGoldHighlight.copy(alpha = if (isReducedMotion) 0.35f else 0.45f * glow),
                start = center,
                end = Offset(endX, endY),
                strokeWidth = 1.8.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Gold Glow Halo
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    GameGoldHighlight.copy(alpha = 0.55f),
                    GameOrangeAccent.copy(alpha = 0.20f),
                    Color.Transparent
                ),
                radius = radius * 1.6f,
                center = center
            ),
            radius = radius * 1.6f,
            center = center
        )

        // Gold Tile Body Gradient
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

        // White/Gold Bevel Ring
        drawCircle(
            color = Color.White.copy(alpha = 0.9f),
            radius = radius - 1.dp.toPx(),
            center = center,
            style = Stroke(width = 1.6.dp.toPx())
        )

        // Crown Glyph at Center
        val crownW = radius * 1.1f
        val crownH = radius * 0.8f
        val cLeft = center.x - crownW / 2f
        val cTop = center.y - crownH / 2f

        val crownPath = Path().apply {
            moveTo(cLeft, cTop + crownH)
            lineTo(cLeft + crownW, cTop + crownH)
            lineTo(cLeft + crownW * 0.9f, cTop + crownH * 0.3f)
            lineTo(cLeft + crownW * 0.68f, cTop + crownH * 0.65f)
            lineTo(cLeft + crownW * 0.5f, cTop)
            lineTo(cLeft + crownW * 0.32f, cTop + crownH * 0.65f)
            lineTo(cLeft + crownW * 0.1f, cTop + crownH * 0.3f)
            close()
        }
        drawPath(path = crownPath, color = GameDeepNavy)
    } else {
        // CYAN REGULAR CHECKPOINT
        // Outer Cyan Glow Halo
        drawCircle(
            color = GameElectricCyan.copy(alpha = if (isReducedMotion) 0.35f else 0.40f * glow),
            radius = radius * 1.55f,
            center = center
        )

        // Cyan Tile Gradient
        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(
                    GameElectricCyan,
                    GameBrightBlue
                ),
                startY = center.y - radius,
                endY = center.y + radius
            ),
            radius = radius,
            center = center
        )

        // Inner Bright Bevel Ring
        drawCircle(
            color = Color.White.copy(alpha = 0.85f),
            radius = radius - 1.dp.toPx(),
            center = center,
            style = Stroke(width = 1.4.dp.toPx())
        )

        // Embossed Numeral
        val textLayout = textMeasurer.measure(
            text = label,
            style = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Black,
                fontSize = (radius * 0.95f).toSp(),
                color = GameDeepNavy
            )
        )
        drawText(
            textLayoutResult = textLayout,
            topLeft = Offset(
                x = center.x - textLayout.size.width / 2f,
                y = center.y - textLayout.size.height / 2f
            )
        )
    }
}
