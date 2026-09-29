package com.zynpath.game.feature.splash.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.R
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameOrangeAccent
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue

/**
 * Premium gold cinematic logo treatment for Zynpath.
 *
 * Features:
 * - Glowing hexagonal game emblem badge with orthogonal path ribbon.
 * - Bold embossed gold gradient title: "ZYNPATH".
 * - Subtitle: "NUMBER PATH PUZZLE" with cyan glow and flanking decorative lines.
 * - Respects [isReducedMotion].
 */
@Composable
fun ZynpathCinematicLogo(
    modifier: Modifier = Modifier,
    isReducedMotion: Boolean = false,
    showEmblem: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "LogoGlowPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "EmblemHaloPulse"
    )

    val haloScale = if (isReducedMotion) 1f else pulse

    val goldTitleBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFBE8),
            Color(0xFFFED541),
            Color(0xFFFDB92E),
            Color(0xFFFE9D1C),
            Color(0xFFD67104)
        )
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showEmblem) {
            // Glowing Emblem Badge
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .scale(haloScale),
                contentAlignment = Alignment.Center
            ) {
                // Ambient Radial Cyan/Gold Glow Aura
                Canvas(modifier = Modifier.size(100.dp)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                GameElectricCyan.copy(alpha = 0.35f),
                                GameRoyalBlue.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        ),
                        radius = size.width * 0.48f,
                        center = center
                    )
                }

                // Emblem Frame Container
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF142652),
                                    GameDeepNavy
                                )
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            brush = Brush.verticalGradient(
                                listOf(
                                    GameElectricCyan,
                                    GameRoyalBlue
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_zynpath_emblem),
                        contentDescription = null,
                        modifier = Modifier.size(46.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Large ZYNPATH Logo Title with Gold Gradient & Deep Shadow
        Box(contentAlignment = Alignment.Center) {
            // 3D Shadow Under-layer for tactile depth
            Text(
                text = "ZYNPATH",
                style = TextStyle(
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Black,
                    fontSize = 44.sp,
                    letterSpacing = 4.sp,
                    color = Color(0x99020817),
                    shadow = Shadow(
                        color = Color(0xFF000000),
                        offset = Offset(0f, 6f),
                        blurRadius = 14f
                    )
                )
            )

            // Foreground Gold Metallic Embossed Text
            Text(
                text = "ZYNPATH",
                style = TextStyle(
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Black,
                    fontSize = 44.sp,
                    letterSpacing = 4.sp,
                    brush = goldTitleBrush,
                    shadow = Shadow(
                        color = GameGoldHighlight.copy(alpha = 0.4f),
                        offset = Offset(0f, 0f),
                        blurRadius = 10f
                    )
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Subtitle: NUMBER PATH PUZZLE with Cyan Glow & Geometric Rule Dividers
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            // Left decorative glowing line
            Canvas(modifier = Modifier.size(width = 32.dp, height = 4.dp)) {
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, GameElectricCyan.copy(alpha = 0.8f))
                    ),
                    start = Offset(0f, size.height / 2f),
                    end = Offset(size.width, size.height / 2f),
                    strokeWidth = 1.5.dp.toPx()
                )
            }

            Text(
                text = "NUMBER PATH PUZZLE",
                style = TextStyle(
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 2.5.sp,
                    color = Color.White,
                    shadow = Shadow(
                        color = Color(0xB3000000),
                        offset = Offset(0f, 2f),
                        blurRadius = 6f
                    )
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // Right decorative glowing line
            Canvas(modifier = Modifier.size(width = 32.dp, height = 4.dp)) {
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(GameElectricCyan.copy(alpha = 0.8f), Color.Transparent)
                    ),
                    start = Offset(0f, size.height / 2f),
                    end = Offset(size.width, size.height / 2f),
                    strokeWidth = 1.5.dp.toPx()
                )
            }
        }
    }
}
