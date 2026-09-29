package com.zynpath.game.core.designsystem.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameTypography
import kotlin.math.cos
import kotlin.math.sin

/**
 * Custom arcade puzzle loading animation featuring orbiting checkpoint nodes connected by glowing
 * path energy lines.
 *
 * @param modifier Custom modifier.
 * @param size Dimension of the spinner animation (defaults to 56dp).
 * @param message Optional loading caption (e.g., "Generating Path...", "Connecting...").
 */
@Composable
fun GameLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    message: String? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "GameLoadingOrbit")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbitAngle"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OrbitPulse"
    )

    Column(
        modifier = modifier.semantics {
            contentDescription = message ?: "Loading"
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val orbitRadius = this.size.width * 0.36f

            // Outer subtle glowing track ring
            drawCircle(
                color = GameRoyalBlue.copy(alpha = 0.35f),
                radius = orbitRadius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // 4 Orbiting connected nodes
            val nodeCount = 4
            val points = (0 until nodeCount).map { i ->
                val nodeAngleRad = Math.toRadians((angle + i * (360f / nodeCount)).toDouble())
                Offset(
                    x = (center.x + orbitRadius * cos(nodeAngleRad)).toFloat(),
                    y = (center.y + orbitRadius * sin(nodeAngleRad)).toFloat()
                )
            }

            // Connecting path chords
            for (i in 0 until nodeCount) {
                val nextIndex = (i + 1) % nodeCount
                drawLine(
                    color = GameElectricCyan.copy(alpha = 0.35f + pulse * 0.25f),
                    start = points[i],
                    end = points[nextIndex],
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Draw orbiting checkpoint nodes
            points.forEachIndexed { index, point ->
                val isLeader = index == 0
                val nodeColor = if (isLeader) GameGoldHighlight else GameElectricCyan
                val nodeRadius = if (isLeader) 5.dp.toPx() else 3.5.dp.toPx()

                // Glow halo
                drawCircle(
                    color = nodeColor.copy(alpha = 0.4f * pulse),
                    radius = nodeRadius * 1.8f,
                    center = point
                )
                // Core node
                drawCircle(
                    color = nodeColor,
                    radius = nodeRadius,
                    center = point
                )
            }
        }

        if (message != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = GameTypography.secondaryInfo.copy(
                    color = GameSecondaryText
                )
            )
        }
    }
}
