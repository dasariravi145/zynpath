package com.zynpath.game.core.designsystem.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.theme.GameBorders
import com.zynpath.game.core.designsystem.theme.GameBrushes
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameShapes
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite

enum class NumberTileState {
    UNVISITED,
    NEXT,
    VISITED,
    START,
    FINAL
}

/**
 * Authentic Number Path Puzzle number tile / checkpoint component.
 *
 * @param number Checkpoint sequence number (e.g. 1, 2, 3... N).
 * @param state Visual state of the tile.
 * @param modifier Custom modifier.
 * @param size Outer dimension of the tile (defaults to 52dp).
 * @param isReducedMotion When true, disables pulsing halo animation.
 * @param onClick Optional tap callback.
 */
@Composable
fun GameNumberTile(
    number: Int,
    state: NumberTileState,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    isReducedMotion: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "TilePulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "TilePulseScale"
    )

    val shape = GameShapes.numberTile

    val (fillBrush, borderStroke, textColor) = when (state) {
        NumberTileState.START -> Triple(
            GameBrushes.tileActive,
            GameBorders.tileActive,
            GameDeepNavy
        )
        NumberTileState.NEXT -> Triple(
            GameBrushes.tileUnvisited,
            GameBorders.tileActive,
            GameElectricCyan
        )
        NumberTileState.VISITED -> Triple(
            GameBrushes.tileActive,
            GameBorders.tileActive,
            GameDeepNavy
        )
        NumberTileState.FINAL -> Triple(
            GameBrushes.tileFinal,
            GameBorders.tileFinal,
            GameDeepNavy
        )
        NumberTileState.UNVISITED -> Triple(
            GameBrushes.tileUnvisited,
            GameBorders.tileUnvisited,
            GameWhite
        )
    }

    val stateDesc = when (state) {
        NumberTileState.START -> "Start checkpoint, number $number"
        NumberTileState.NEXT -> "Next required checkpoint, number $number"
        NumberTileState.VISITED -> "Completed checkpoint, number $number"
        NumberTileState.FINAL -> "Final goal checkpoint, number $number"
        NumberTileState.UNVISITED -> "Checkpoint, number $number"
    }

    Box(
        modifier = modifier
            .size(size)
            .then(
                if (state == NumberTileState.NEXT && !isReducedMotion) {
                    Modifier.scale(pulseScale)
                } else Modifier
            )
            .semantics {
                role = Role.Button
                contentDescription = stateDesc
            }
            .clip(shape)
            .background(fillBrush)
            .border(borderStroke, shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        // Large checkpoint number
        Text(
            text = number.toString(),
            style = GameTypography.levelNumber.copy(
                fontSize = (size.value * 0.44f).sp,
                fontWeight = FontWeight.Black,
                color = textColor
            )
        )

        // Visited mini checkmark indicator in top corner
        if (state == NumberTileState.VISITED) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(3.dp)
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(GameDeepNavy),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = GameElectricCyan,
                    modifier = Modifier.size(10.dp)
                )
            }
        }
    }
}
