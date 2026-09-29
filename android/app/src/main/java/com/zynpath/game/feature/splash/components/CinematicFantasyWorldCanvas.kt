package com.zynpath.game.feature.splash.components

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
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameOrangeAccent
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class MoteParticle(
    val relX: Float,
    val relY: Float,
    val radius: Float,
    val color: Color,
    val speed: Float,
    val phase: Float
)

/**
 * Procedural Compose Canvas depicting the Zynpath fantasy puzzle world.
 *
 * Visual elements:
 * - Deep navy atmospheric sky with radiant horizon bloom.
 * - Layered mountain silhouettes with glowing cyan ridge rims.
 * - Floating puzzle island with crystal roots and satellite rocks.
 * - Perspective glowing numbered path leading from foreground checkpoint 1 -> 2 -> 3 -> 4 (Golden Summit Crown).
 * - Animated traveling energy pulse along the spline.
 * - Ambient floating celestial stardust and rising sparks.
 * - Fully responsive and respectful of [isReducedMotion].
 */
@Composable
fun CinematicFantasyWorldCanvas(
    modifier: Modifier = Modifier,
    height: Dp = 260.dp,
    isReducedMotion: Boolean = false
) {
    val textMeasurer = rememberTextMeasurer()

    val infiniteTransition = rememberInfiniteTransition(label = "FantasyWorldMotion")

    // Traveling light pulse along the 4 checkpoints (0f to 1f)
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SplinePulseProgress"
    )

    // Gentle hovering / breathing motion for the floating island
    val hoverOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "IslandHover"
    )

    // Pulsing halo for the checkpoint nodes
    val nodeGlowPulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "NodeGlowPulse"
    )

    val activePulse = if (isReducedMotion) 0.5f else pulseProgress
    val activeHover = if (isReducedMotion) 0f else hoverOffset
    val activeGlow = if (isReducedMotion) 1f else nodeGlowPulse

    // Deterministic particles
    val particles = remember {
        val rand = Random(1337)
        List(24) {
            val isCyan = rand.nextFloat() > 0.35f
            MoteParticle(
                relX = rand.nextFloat(),
                relY = rand.nextFloat(),
                radius = rand.nextFloat() * 2.2f + 1.0f,
                color = if (isCyan) GameElectricCyan else GameGoldHighlight,
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

        // 1. ATMOSPHERIC HORIZON GLOW
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    GameDeepNavy,
                    Color(0xFF0D1D40),
                    GameDeepNavy
                )
            )
        )

        // Radial bloom centered behind the floating island
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    GameRoyalBlue.copy(alpha = 0.45f),
                    GameElectricCyan.copy(alpha = 0.15f),
                    Color.Transparent
                )
            ),
            radius = w * 0.45f,
            center = Offset(w * 0.50f, h * 0.40f)
        )

        // 2. CELESTIAL STARDUST PARTICLES
        particles.forEach { p ->
            val animatedY = if (isReducedMotion) {
                p.relY * h
            } else {
                ((p.relY - activePulse * 0.15f * p.speed + 1f) % 1f) * h
            }
            val alpha = (0.3f + 0.4f * sin((p.phase + activeGlow).toDouble())).toFloat().coerceIn(0.2f, 0.85f)

            drawCircle(
                color = p.color.copy(alpha = alpha),
                radius = p.radius,
                center = Offset(p.relX * w, animatedY)
            )
        }

        // 3. DISTANT MOUNTAIN SILHOUETTES
        drawDistantMountains(w, h)

        // 4. MIDGROUND MOUNTAINS WITH GLOWING CYAN RIM
        drawMidgroundMountains(w, h)

        // 5. FLOATING PUZZLE ISLAND
        drawFloatingIsland(w, h, activeHover)

        // 6. PERSPECTIVE NUMBER PATHWAY (1 -> 2 -> 3 -> 4 Summit)
        drawPerspectivePathway(
            w = w,
            h = h,
            hover = activeHover,
            pulse = activePulse,
            glow = activeGlow,
            isReducedMotion = isReducedMotion
        )

        // 7. CHECKPOINT NODES (1, 2, 3, 4 Summit Crown)
        drawCheckpoints(
            w = w,
            h = h,
            hover = activeHover,
            glow = activeGlow,
            textMeasurer = textMeasurer,
            isReducedMotion = isReducedMotion
        )
    }
}

/**
 * Draws the deep jagged background mountain range.
 */
private fun DrawScope.drawDistantMountains(w: Float, h: Float) {
    val path = Path().apply {
        moveTo(0f, h * 0.56f)
        lineTo(w * 0.12f, h * 0.46f)
        lineTo(w * 0.26f, h * 0.52f)
        lineTo(w * 0.40f, h * 0.42f)
        lineTo(w * 0.58f, h * 0.50f)
        lineTo(w * 0.74f, h * 0.38f)
        lineTo(w * 0.88f, h * 0.48f)
        lineTo(w, h * 0.44f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(
        path = path,
        color = Color(0xFF08122B)
    )
}

/**
 * Draws the midground mountain range with glowing cyan rim lighting along its peaks.
 */
private fun DrawScope.drawMidgroundMountains(w: Float, h: Float) {
    val ridgePath = Path().apply {
        moveTo(0f, h * 0.62f)
        lineTo(w * 0.18f, h * 0.54f)
        lineTo(w * 0.34f, h * 0.59f)
        lineTo(w * 0.52f, h * 0.49f)
        lineTo(w * 0.68f, h * 0.56f)
        lineTo(w * 0.84f, h * 0.48f)
        lineTo(w, h * 0.58f)
    }

    val mountainBody = Path().apply {
        addPath(ridgePath)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }

    // Mountain body
    drawPath(
        path = mountainBody,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0B1B3C),
                GameDeepNavy
            ),
            startY = h * 0.48f,
            endY = h
        )
    )

    // Glowing Cyan Ridge Rim
    drawPath(
        path = ridgePath,
        color = GameElectricCyan.copy(alpha = 0.38f),
        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

/**
 * Draws the floating puzzle island with crystalline roots and satellite rocks.
 */
private fun DrawScope.drawFloatingIsland(w: Float, h: Float, hover: Float) {
    val baseY = h * 0.40f + hover
    val islandW = w * 0.38f
    val islandLeft = (w - islandW) / 2f
    val islandRight = islandLeft + islandW

    // Island rock body tapering downwards
    val islandBody = Path().apply {
        moveTo(islandLeft, baseY)
        lineTo(islandRight, baseY)
        lineTo(islandRight - islandW * 0.15f, baseY + h * 0.08f)
        lineTo(w * 0.50f, baseY + h * 0.16f) // Deep central root point
        lineTo(islandLeft + islandW * 0.15f, baseY + h * 0.08f)
        close()
    }

    // Rocky body fill
    drawPath(
        path = islandBody,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF162952),
                Color(0xFF0D1B38),
                Color(0xFF060D1E)
            ),
            startY = baseY,
            endY = baseY + h * 0.16f
        )
    )

    // Island stone border
    drawPath(
        path = islandBody,
        color = GameRoyalBlue.copy(alpha = 0.6f),
        style = Stroke(width = 1.5.dp.toPx())
    )

    // Glowing cyan crystal seams embedded in the island root
    val crystalSeams = Path().apply {
        moveTo(w * 0.50f, baseY + 6.dp.toPx())
        lineTo(w * 0.48f, baseY + h * 0.07f)
        lineTo(w * 0.50f, baseY + h * 0.14f)

        moveTo(w * 0.42f, baseY + 8.dp.toPx())
        lineTo(w * 0.44f, baseY + h * 0.05f)

        moveTo(w * 0.58f, baseY + 8.dp.toPx())
        lineTo(w * 0.56f, baseY + h * 0.06f)
    }
    drawPath(
        path = crystalSeams,
        color = GameElectricCyan.copy(alpha = 0.75f),
        style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
    )

    // Island Plateau Top Surface (Ancient Stone Terrace)
    val terrace = Path().apply {
        moveTo(islandLeft - 4.dp.toPx(), baseY)
        lineTo(islandRight + 4.dp.toPx(), baseY)
        lineTo(islandRight, baseY + 6.dp.toPx())
        lineTo(islandLeft, baseY + 6.dp.toPx())
        close()
    }
    drawPath(
        path = terrace,
        brush = Brush.horizontalGradient(
            listOf(
                Color(0xFF1E3A75),
                Color(0xFF28509E),
                Color(0xFF1E3A75)
            )
        )
    )

    // Satellite floating stone (left)
    val satLeftX = w * 0.22f
    val satLeftY = baseY + h * 0.04f - hover * 0.8f
    drawCircle(
        color = Color(0xFF14244A),
        radius = 8.dp.toPx(),
        center = Offset(satLeftX, satLeftY)
    )
    drawCircle(
        color = GameElectricCyan.copy(alpha = 0.5f),
        radius = 8.dp.toPx(),
        center = Offset(satLeftX, satLeftY),
        style = Stroke(width = 1.dp.toPx())
    )

    // Satellite floating stone (right)
    val satRightX = w * 0.78f
    val satRightY = baseY + h * 0.02f + hover * 0.6f
    drawCircle(
        color = Color(0xFF14244A),
        radius = 6.dp.toPx(),
        center = Offset(satRightX, satRightY)
    )
    drawCircle(
        color = GameElectricCyan.copy(alpha = 0.5f),
        radius = 6.dp.toPx(),
        center = Offset(satRightX, satRightY),
        style = Stroke(width = 1.dp.toPx())
    )
}

/**
 * Calculates cubic spline points connecting the 4 checkpoints.
 */
private data class CheckpointCoords(
    val p1: Offset,
    val p2: Offset,
    val p3: Offset,
    val p4: Offset
)

private fun getCoords(w: Float, h: Float, hover: Float): CheckpointCoords {
    return CheckpointCoords(
        p1 = Offset(w * 0.18f, h * 0.84f), // Foreground Left Checkpoint 1
        p2 = Offset(w * 0.38f, h * 0.66f), // Mid-Foreground Checkpoint 2
        p3 = Offset(w * 0.72f, h * 0.50f), // Mid-Right Checkpoint 3
        p4 = Offset(w * 0.50f, h * 0.34f + hover) // Floating Island Summit Checkpoint 4
    )
}

/**
 * Draws the winding glowing 3D perspective path ribbon and traveling energy pulse.
 */
private fun DrawScope.drawPerspectivePathway(
    w: Float,
    h: Float,
    hover: Float,
    pulse: Float,
    glow: Float,
    isReducedMotion: Boolean
) {
    val coords = getCoords(w, h, hover)
    val p1 = coords.p1
    val p2 = coords.p2
    val p3 = coords.p3
    val p4 = coords.p4

    // Continuous smooth spline from 1 -> 2 -> 3 -> 4
    val splinePath = Path().apply {
        moveTo(p1.x, p1.y)
        // Curve 1 to 2
        cubicTo(
            x1 = w * 0.20f, y1 = h * 0.74f,
            x2 = w * 0.28f, y2 = h * 0.68f,
            x3 = p2.x, y3 = p2.y
        )
        // Curve 2 to 3
        cubicTo(
            x1 = w * 0.48f, y1 = h * 0.64f,
            x2 = w * 0.62f, y2 = h * 0.60f,
            x3 = p3.x, y3 = p3.y
        )
        // Curve 3 to 4 (Summit)
        cubicTo(
            x1 = w * 0.78f, y1 = h * 0.42f,
            x2 = w * 0.62f, y2 = h * 0.37f + hover,
            x3 = p4.x, y3 = p4.y
        )
    }

    // 1. Ambient Outer Path Glow Ribbon
    drawPath(
        path = splinePath,
        color = GameRoyalBlue.copy(alpha = 0.40f * glow),
        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // 2. High-energy Cyan Energy Beam
    drawPath(
        path = splinePath,
        color = GameElectricCyan.copy(alpha = 0.70f + (if (isReducedMotion) 0.1f else glow * 0.20f)),
        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // 3. Radiant Core White Laser Thread
    drawPath(
        path = splinePath,
        color = Color(0xFFF0FBFF),
        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // 4. Traveling Energy Pulse Orb (traveling 1 -> 2 -> 3 -> 4)
    if (!isReducedMotion) {
        val pulsePoint = evaluateSpline(coords, pulse)
        // Pulse outer halo
        drawCircle(
            color = GameElectricCyan.copy(alpha = 0.5f),
            radius = 12.dp.toPx(),
            center = pulsePoint
        )
        // Pulse core
        drawCircle(
            color = Color.White,
            radius = 4.5.dp.toPx(),
            center = pulsePoint
        )
    }
}

/**
 * Evaluates the 3-segment cubic spline position for normalized t in [0..1].
 */
private fun evaluateSpline(coords: CheckpointCoords, t: Float): Offset {
    val scaledT = (t.coerceIn(0f, 1f) * 3f)
    return when {
        scaledT <= 1f -> {
            // Segment 1: p1 to p2
            val localT = scaledT
            val c1 = Offset(coords.p1.x * 0.8f + coords.p2.x * 0.2f, coords.p1.y * 0.6f + coords.p2.y * 0.4f)
            val c2 = Offset(coords.p1.x * 0.3f + coords.p2.x * 0.7f, coords.p1.y * 0.4f + coords.p2.y * 0.6f)
            cubicPoint(coords.p1, c1, c2, coords.p2, localT)
        }
        scaledT <= 2f -> {
            // Segment 2: p2 to p3
            val localT = scaledT - 1f
            val c1 = Offset(coords.p2.x * 0.7f + coords.p3.x * 0.3f, coords.p2.y * 0.8f + coords.p3.y * 0.2f)
            val c2 = Offset(coords.p2.x * 0.2f + coords.p3.x * 0.8f, coords.p2.y * 0.3f + coords.p3.y * 0.7f)
            cubicPoint(coords.p2, c1, c2, coords.p3, localT)
        }
        else -> {
            // Segment 3: p3 to p4
            val localT = (scaledT - 2f).coerceIn(0f, 1f)
            val c1 = Offset(coords.p3.x * 0.6f + coords.p4.x * 0.4f, coords.p3.y * 0.7f + coords.p4.y * 0.3f)
            val c2 = Offset(coords.p3.x * 0.3f + coords.p4.x * 0.7f, coords.p3.y * 0.3f + coords.p4.y * 0.7f)
            cubicPoint(coords.p3, c1, c2, coords.p4, localT)
        }
    }
}

private fun cubicPoint(p0: Offset, p1: Offset, p2: Offset, p3: Offset, t: Float): Offset {
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
 * Draws the 4 illuminated number checkpoints along the path.
 * Checkpoint 1: Foreground electric cyan tile "1"
 * Checkpoint 2: Midground electric cyan tile "2"
 * Checkpoint 3: Mid-elevation electric cyan tile "3"
 * Checkpoint 4: Island summit radiant gold crown "4" / summit checkpoint
 */
private fun DrawScope.drawCheckpoints(
    w: Float,
    h: Float,
    hover: Float,
    glow: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    isReducedMotion: Boolean
) {
    val coords = getCoords(w, h, hover)

    // Checkpoint 1 (Foreground Start Node)
    drawSingleCyanNode(
        center = coords.p1,
        radius = 24.dp.toPx(),
        label = "1",
        glow = glow,
        textMeasurer = textMeasurer,
        isReducedMotion = isReducedMotion
    )

    // Checkpoint 2 (Mid-Left Node)
    drawSingleCyanNode(
        center = coords.p2,
        radius = 20.dp.toPx(),
        label = "2",
        glow = glow,
        textMeasurer = textMeasurer,
        isReducedMotion = isReducedMotion
    )

    // Checkpoint 3 (Mid-Right Node)
    drawSingleCyanNode(
        center = coords.p3,
        radius = 18.dp.toPx(),
        label = "3",
        glow = glow,
        textMeasurer = textMeasurer,
        isReducedMotion = isReducedMotion
    )

    // Checkpoint 4 (Summit Finish Node - Radiant Gold Crown)
    drawSummitGoldNode(
        center = coords.p4,
        radius = 22.dp.toPx(),
        glow = glow,
        isReducedMotion = isReducedMotion
    )
}

/**
 * Draws an illuminated Electric Cyan Checkpoint Tile.
 */
private fun DrawScope.drawSingleCyanNode(
    center: Offset,
    radius: Float,
    label: String,
    glow: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    isReducedMotion: Boolean
) {
    // 1. Stone pedestal foundation beneath node
    drawCircle(
        color = Color(0xFF09142E),
        radius = radius * 1.35f,
        center = Offset(center.x, center.y + 4.dp.toPx())
    )

    // 2. Outer Cyan Glow Halo
    drawCircle(
        color = GameElectricCyan.copy(alpha = if (isReducedMotion) 0.35f else 0.40f * glow),
        radius = radius * 1.55f,
        center = center
    )

    // 3. Tile Body Gradient
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

    // 4. Inner Bright Border Ring
    drawCircle(
        color = Color.White.copy(alpha = 0.85f),
        radius = radius - 1.dp.toPx(),
        center = center,
        style = Stroke(width = 1.5.dp.toPx())
    )

    // 5. Embossed Numeral Label
    val fontSize = (radius * 0.95f).toSp()
    val textLayout = textMeasurer.measure(
        text = label,
        style = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Black,
            fontSize = fontSize,
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

/**
 * Draws the Radiant Golden Summit Checkpoint with Glowing Crown Emblem.
 */
private fun DrawScope.drawSummitGoldNode(
    center: Offset,
    radius: Float,
    glow: Float,
    isReducedMotion: Boolean
) {
    // 1. Radiant Gold Corona Light Flares (8 rays)
    val rayCount = 8
    val rayLen = radius * 1.9f
    for (i in 0 until rayCount) {
        val angleRad = Math.toRadians((i * (360.0 / rayCount) + (if (isReducedMotion) 0.0 else glow * 20.0)))
        val endX = (center.x + rayLen * cos(angleRad)).toFloat()
        val endY = (center.y + rayLen * sin(angleRad)).toFloat()
        drawLine(
            color = GameGoldHighlight.copy(alpha = if (isReducedMotion) 0.3f else 0.4f * glow),
            start = center,
            end = Offset(endX, endY),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    // 2. Stone pedestal foundation
    drawCircle(
        color = Color(0xFF09142E),
        radius = radius * 1.35f,
        center = Offset(center.x, center.y + 4.dp.toPx())
    )

    // 3. Radiant Gold Glow Halo
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                GameGoldHighlight.copy(alpha = 0.6f),
                GameOrangeAccent.copy(alpha = 0.2f),
                Color.Transparent
            ),
            radius = radius * 1.7f,
            center = center
        ),
        radius = radius * 1.7f,
        center = center
    )

    // 4. Gold Tile Body
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

    // 5. White/Gold Highlight Ring
    drawCircle(
        color = Color.White.copy(alpha = 0.9f),
        radius = radius - 1.dp.toPx(),
        center = center,
        style = Stroke(width = 1.8.dp.toPx())
    )

    // 6. Summit Golden Crown Icon at Center
    val crownW = radius * 1.15f
    val crownH = radius * 0.85f
    val crownLeft = center.x - crownW / 2f
    val crownTop = center.y - crownH / 2f

    val crownPath = Path().apply {
        // Base band
        moveTo(crownLeft, crownTop + crownH)
        lineTo(crownLeft + crownW, crownTop + crownH)
        // Right side to peak
        lineTo(crownLeft + crownW * 0.9f, crownTop + crownH * 0.3f)
        lineTo(crownLeft + crownW * 0.68f, crownTop + crownH * 0.65f)
        lineTo(crownLeft + crownW * 0.5f, crownTop) // Center crown peak
        lineTo(crownLeft + crownW * 0.32f, crownTop + crownH * 0.65f)
        lineTo(crownLeft + crownW * 0.1f, crownTop + crownH * 0.3f)
        close()
    }

    // Crown fill in high-contrast Deep Navy with gold trim
    drawPath(
        path = crownPath,
        color = GameDeepNavy
    )

    // Crown jewel dots on the 3 peaks
    val peakRadius = 2.dp.toPx()
    drawCircle(color = Color.White, radius = peakRadius, center = Offset(crownLeft + crownW * 0.1f, crownTop + crownH * 0.3f))
    drawCircle(color = Color.White, radius = peakRadius * 1.2f, center = Offset(crownLeft + crownW * 0.5f, crownTop))
    drawCircle(color = Color.White, radius = peakRadius, center = Offset(crownLeft + crownW * 0.9f, crownTop + crownH * 0.3f))
}
