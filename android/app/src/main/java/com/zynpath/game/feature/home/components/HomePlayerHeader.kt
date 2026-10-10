package com.zynpath.game.feature.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.R
import com.zynpath.game.core.designsystem.components.AvatarFrameType
import com.zynpath.game.core.designsystem.components.ZynpathMasterCurrencyPill
import com.zynpath.game.core.designsystem.components.glossyTopHighlight
import com.zynpath.game.feature.home.HomeUiState

/**
 * Resolves avatar illustration resource safely from identifier.
 */
fun resolveAvatarDrawable(avatarId: String): Int {
    return when (avatarId) {
        "avatar_cyber", "cyber" -> R.drawable.ic_game_avatar_cyber
        "avatar_wizard", "wizard" -> R.drawable.ic_game_avatar_wizard
        "avatar_amit" -> R.drawable.avatar_amit
        "avatar_neha" -> R.drawable.avatar_neha
        "avatar_priya" -> R.drawable.avatar_priya
        "avatar_rahul" -> R.drawable.avatar_rahul
        "avatar_vikram" -> R.drawable.avatar_vikram
        "avatar_you", "you" -> R.drawable.avatar_you
        else -> R.drawable.avatar_you
    }
}

/**
 * Premium game header displaying player identity, avatar with circular cosmetic frame,
 * level rank, star progression, coin counter, and settings access.
 *
 * Implements Prompt UI Quality Audit & Premium Polish:
 * - Complete avatar visibility inside a circular frame with safe internal padding and ContentScale.Fit.
 * - Entire head, hair, and face 100% visible, not cropped by the circular frame.
 * - Subtle mirror/glossy top highlight rim.
 * - Dynamic avatar resolution via resolveAvatarDrawable.
 * - Player name constrained so it never collides with counters.
 * - Accessible touch targets (>=44dp) on interactive controls.
 * - Adaptive layout preventing horizontal compression on narrow screens.
 */
@Composable
fun HomePlayerHeader(
    uiState: HomeUiState,
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToLevels: () -> Unit,
    onCoinClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val playerLevel = (uiState.completedLevelsCount / 5) + 1

    // Delta animation for real-time coin earning / spending feedback
    var previousBalance by remember { mutableStateOf<Int?>(null) }
    var deltaValue by remember { mutableStateOf<Int?>(null) }
    val deltaAnim = remember { androidx.compose.animation.core.Animatable(0f) }

    LaunchedEffect(uiState.coinBalance) {
        val prev = previousBalance
        previousBalance = uiState.coinBalance
        if (prev != null && prev != uiState.coinBalance) {
            deltaValue = uiState.coinBalance - prev
            deltaAnim.snapTo(0f)
            deltaAnim.animateTo(
                targetValue = 1f,
                animationSpec = androidx.compose.animation.core.tween(
                    durationMillis = 1500,
                    easing = androidx.compose.animation.core.LinearEasing
                )
            )
            deltaValue = null
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left: Avatar with glowing blue ring, Name, and Level Pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f, fill = false)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onNavigateToProfile
                )
        ) {
            // Circular Avatar Container with safe internal framing
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF142B56),
                                Color(0xFF0A1931)
                            )
                        )
                    )
                    .border(
                        2.dp,
                        Brush.sweepGradient(
                            listOf(
                                Color(0xFF00E5FF),
                                Color(0xFF00B0FF),
                                Color(0xFF82B1FF),
                                Color(0xFF00E5FF)
                            )
                        ),
                        CircleShape
                    )
                    .glossyTopHighlight(cornerRadius = 22.dp, highlightAlpha = 0.22f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = resolveAvatarDrawable(uiState.avatarId)),
                    contentDescription = "Player avatar",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.widthIn(max = 130.dp)) {
                Text(
                    text = uiState.displayName.ifBlank { "Pathfinder" },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0C244A))
                        .border(1.dp, Color(0xFF1B4E8C), RoundedCornerShape(8.dp))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Level $playerLevel",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF82B1FF)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Right: Coin Pill, Star Pill, Settings Gear
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Coin Counter Pill with Floating Delta
            Box(contentAlignment = Alignment.Center) {
                ZynpathMasterCurrencyPill(
                    iconPainter = painterResource(id = R.drawable.ic_game_coin),
                    amount = java.text.NumberFormat.getIntegerInstance().format(uiState.coinBalance),
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onCoinClick
                    )
                )

                deltaValue?.let { delta ->
                    val isPositive = delta > 0
                    val deltaText = if (isPositive) "+$delta" else "$delta"
                    val textColor = if (isPositive) Color(0xFF00E676) else Color(0xFFFF5252)
                    val alpha = (1f - deltaAnim.value).coerceIn(0f, 1f)
                    val yOffset = (-26 * deltaAnim.value).dp

                    Text(
                        text = deltaText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor.copy(alpha = alpha),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = yOffset)
                    )
                }
            }

            // Star Counter Pill
            ZynpathMasterCurrencyPill(
                iconPainter = painterResource(id = R.drawable.ic_game_star),
                amount = "${uiState.totalStars}",
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onNavigateToLevels
                )
            )

            // Settings Gear Button with accessible touch target (>=44dp)
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 44.dp, minHeight = 44.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onNavigateToSettings
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF14244E), Color(0xFF0A1931))
                            )
                        )
                        .border(1.2.dp, Color(0xFF1E3A8A), CircleShape)
                        .glossyTopHighlight(cornerRadius = 19.dp, highlightAlpha = 0.20f),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color(0xFFB0C4DE),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
