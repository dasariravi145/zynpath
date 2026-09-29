package com.zynpath.game.core.cosmetics.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.PathCyanGlow
import com.zynpath.game.core.player.AvatarOption

/**
 * Reusable avatar rendering component adorned with the player's equipped avatar frame.
 *
 * Implements Prompt 28 Sections 27, 28, 29, 30:
 * - Clean circular avatar core visible for all players.
 * - Free default and silver frames.
 * - Curated premium frames (Royal Gold Crown, Cyber Ring, Emerald Facet).
 * - Scales cleanly across profile headers (88dp), lobby icons (56dp), and list items (40dp).
 */
@Composable
fun AvatarWithFrame(
    avatar: AvatarOption,
    frameId: String,
    size: Dp = 88.dp,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        // Inner Avatar Circle
        val avatarInnerScale = 0.82f
        Box(
            modifier = Modifier
                .size(size * avatarInnerScale)
                .clip(CircleShape)
                .background(avatar.primaryColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = avatar.icon,
                contentDescription = "Avatar: ${avatar.title}",
                tint = BackgroundDark,
                modifier = Modifier.size(size * avatarInnerScale * 0.58f)
            )
        }

        // Outer Decorative Frame Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val outerRadius = (this.size.minDimension / 2f) * 0.95f
            val innerRadius = outerRadius * 0.88f

            when (frameId) {
                // 1. Free Default Slate
                "frame_default_slate" -> {
                    drawCircle(
                        color = Color(0xFF64748B),
                        radius = outerRadius,
                        center = center,
                        style = Stroke(width = 2.5.dp.toPx())
                    )
                }

                // 2. Free Silver Crest (With 4 Cardinal Accents)
                "frame_silver_crest" -> {
                    drawCircle(
                        color = Color(0xFFCBD5E1),
                        radius = outerRadius,
                        center = center,
                        style = Stroke(width = 3.dp.toPx())
                    )
                    drawCircle(
                        color = Color(0xFF94A3B8),
                        radius = outerRadius * 0.92f,
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )
                    // 4 cardinal pips
                    val pipDist = outerRadius
                    val pipRadius = 2.2.dp.toPx()
                    drawCircle(Color(0xFFE2E8F0), pipRadius, Offset(center.x, center.y - pipDist))
                    drawCircle(Color(0xFFE2E8F0), pipRadius, Offset(center.x, center.y + pipDist))
                    drawCircle(Color(0xFFE2E8F0), pipRadius, Offset(center.x - pipDist, center.y))
                    drawCircle(Color(0xFFE2E8F0), pipRadius, Offset(center.x + pipDist, center.y))
                }

                // 3. Premium Royal Gold Crown
                "frame_gold_accent" -> {
                    // Outer gold ring
                    drawCircle(
                        color = AccentGold,
                        radius = outerRadius,
                        center = center,
                        style = Stroke(width = 3.5.dp.toPx())
                    )
                    // Inner subtle ring
                    drawCircle(
                        color = Color(0xFFFFE082),
                        radius = innerRadius,
                        center = center,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                    // Crown Diamond at top
                    val diamondSize = 4.5.dp.toPx()
                    val crownPath = Path().apply {
                        moveTo(center.x, center.y - outerRadius - diamondSize)
                        lineTo(center.x + diamondSize, center.y - outerRadius)
                        lineTo(center.x, center.y - outerRadius + diamondSize)
                        lineTo(center.x - diamondSize, center.y - outerRadius)
                        close()
                    }
                    drawPath(crownPath, AccentGold)
                }

                // 4. Premium Cyber Ring (Neon Cyan & Magenta)
                "frame_neon_ring" -> {
                    // Outer glow
                    drawCircle(
                        color = Color(0xFFFF007F).copy(alpha = 0.45f),
                        radius = outerRadius * 1.05f,
                        center = center,
                        style = Stroke(width = 4.dp.toPx())
                    )
                    // Magenta ring
                    drawCircle(
                        color = Color(0xFFFF007F),
                        radius = outerRadius,
                        center = center,
                        style = Stroke(width = 2.5.dp.toPx())
                    )
                    // Inner Cyan ring
                    drawCircle(
                        color = PathCyanGlow,
                        radius = innerRadius,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                // 5. Premium Emerald Facet
                "frame_emerald_geometric" -> {
                    drawCircle(
                        color = Color(0xFF2EC4B6),
                        radius = outerRadius,
                        center = center,
                        style = Stroke(width = 3.5.dp.toPx())
                    )
                    drawCircle(
                        color = Color(0xFF80ED99),
                        radius = innerRadius,
                        center = center,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                    // 4 corner corner gem markers
                    val nodeDist = outerRadius * 0.98f
                    val nodeR = 2.8.dp.toPx()
                    drawCircle(Color(0xFFFFB703), nodeR, Offset(center.x + nodeDist * 0.707f, center.y - nodeDist * 0.707f))
                    drawCircle(Color(0xFFFFB703), nodeR, Offset(center.x - nodeDist * 0.707f, center.y - nodeDist * 0.707f))
                    drawCircle(Color(0xFFFFB703), nodeR, Offset(center.x + nodeDist * 0.707f, center.y + nodeDist * 0.707f))
                    drawCircle(Color(0xFFFFB703), nodeR, Offset(center.x - nodeDist * 0.707f, center.y + nodeDist * 0.707f))
                }

                // Default Fallback
                else -> {
                    drawCircle(
                        color = Color(0xFF64748B),
                        radius = outerRadius,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }
    }
}
