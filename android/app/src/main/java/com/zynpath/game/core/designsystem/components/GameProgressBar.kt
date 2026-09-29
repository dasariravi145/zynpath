package com.zynpath.game.core.designsystem.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zynpath.game.core.designsystem.theme.GameBrushes
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite

enum class ProgressBarStyle {
    CYAN,
    GOLD
}

/**
 * Arcade-quality animated progress bar featuring glowing gradients, leading edge highlights,
 * and optional integrated progress text.
 *
 * @param progress Value between 0.0f and 1.0f.
 * @param modifier Custom modifier.
 * @param height Height of the bar (defaults to 16dp).
 * @param style Color style variant (CYAN or GOLD).
 * @param label Optional text centered inside or above the bar.
 * @param isReducedMotion When true, disables progress fill interpolation.
 */
@Composable
fun GameProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 16.dp,
    style: ProgressBarStyle = ProgressBarStyle.CYAN,
    label: String? = null,
    isReducedMotion: Boolean = false
) {
    val clampedProgress = progress.coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = clampedProgress,
        animationSpec = if (isReducedMotion) tween(0) else tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "ProgressAnimation"
    )

    val shape = RoundedCornerShape(height / 2)
    val fillBrush = if (style == ProgressBarStyle.CYAN) GameBrushes.progressCyan else GameBrushes.progressGold

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(clampedProgress, 0f..1f)
            }
            .clip(shape)
            .background(Color(0xFF07142D))
            .border(1.dp, Color(0xFF152A55), shape),
        contentAlignment = Alignment.CenterStart
    ) {
        // Filled track
        if (animatedProgress > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(shape)
                    .background(fillBrush)
            )
        }

        // Centered progress label
        if (label != null) {
            Text(
                text = label,
                style = GameTypography.badgeText.copy(color = GameWhite),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 8.dp)
            )
        }
    }
}
