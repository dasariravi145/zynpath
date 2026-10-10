package com.zynpath.game.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zynpath.game.core.designsystem.theme.GameBorders
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue

/**
 * Draws a subtle specular reflective shine on the upper surface of a container,
 * producing a premium glossy/mirror finish without expensive runtime blur.
 *
 * @param cornerRadius The corner radius of the container in dp.
 * @param highlightAlpha Maximum opacity of the top reflection (defaults to 0.18f).
 * @param highlightHeightPercent Fraction of the container height covered by the sheen (defaults to 0.40f).
 */
fun Modifier.glossyTopHighlight(
    cornerRadius: Dp = 16.dp,
    highlightAlpha: Float = 0.18f,
    highlightHeightPercent: Float = 0.40f
): Modifier = this.drawWithContent {
    drawContent()
    val crPx = cornerRadius.toPx()
    val maxH = (size.height * highlightHeightPercent).coerceAtMost(32.dp.toPx())
    if (maxH > 0f && size.width > 0f) {
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = highlightAlpha),
                    Color.White.copy(alpha = highlightAlpha * 0.25f),
                    Color.Transparent
                ),
                startY = 0f,
                endY = maxH
            ),
            topLeft = Offset.Zero,
            size = Size(size.width, maxH),
            cornerRadius = CornerRadius(crPx, crPx)
        )
    }
}

/**
 * Applies cohesive Zynpath modal card surface:
 * - Rounded corners (default 22.dp)
 * - Deep navy/midnight gradient background
 * - Refined gold & cyan multi-stop border
 * - Reflective top-edge glossy highlight
 */
fun Modifier.modalCardSurface(
    cornerRadius: Dp = 22.dp,
    shape: Shape = RoundedCornerShape(cornerRadius)
): Modifier = this
    .clip(shape)
    .background(
        Brush.verticalGradient(
            colors = listOf(
                Color(0xF80A1635),
                GameMidnightBlue
            )
        )
    )
    .border(GameBorders.modalGoldCyan, shape)
    .glossyTopHighlight(cornerRadius = cornerRadius, highlightAlpha = 0.16f)
