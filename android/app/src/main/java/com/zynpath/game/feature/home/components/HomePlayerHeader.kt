package com.zynpath.game.feature.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.R
import com.zynpath.game.core.designsystem.components.AvatarFrameType
import com.zynpath.game.core.designsystem.components.AvatarSize
import com.zynpath.game.core.designsystem.components.PlayerAvatar
import com.zynpath.game.core.designsystem.components.StarCounter
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.feature.home.HomeUiState

/**
 * Resolves avatar illustration resource safely from identifier.
 */
fun resolveAvatarDrawable(avatarId: String): Int {
    return when (avatarId) {
        "avatar_cyber", "cyber" -> R.drawable.ic_game_avatar_cyber
        "avatar_wizard", "wizard" -> R.drawable.ic_game_avatar_wizard
        else -> R.drawable.ic_game_avatar_guest
    }
}

/**
 * Premium game header displaying player identity, avatar with cosmetic frame,
 * level rank, star progression, and settings/notification access.
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
    val frameType = if (uiState.isPremium) {
        AvatarFrameType.GOLD_CHAMPION
    } else if (playerLevel >= 5) {
        AvatarFrameType.CYAN_MYSTIC
    } else {
        AvatarFrameType.BRONZE
    }

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
        // Left: Avatar with blue ring, Name, and Level Pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onNavigateToProfile)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0A1931))
                    .border(2.dp, Color(0xFF00B0FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.avatar_you),
                    contentDescription = "Avatar",
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = uiState.displayName.ifBlank { "Player_123" },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(2.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0C244A))
                        .border(1.dp, Color(0xFF1B4E8C), RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 1.dp)
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

        Spacer(modifier = Modifier.width(6.dp))

        // Right: Coin Pill, Star Pill, Settings Gear
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Coin Counter Pill with Floating Delta
            Box(contentAlignment = Alignment.Center) {
                com.zynpath.game.core.designsystem.components.ZynpathMasterCurrencyPill(
                    iconPainter = painterResource(id = R.drawable.ic_game_coin),
                    amount = java.text.NumberFormat.getIntegerInstance().format(uiState.coinBalance),
                    modifier = Modifier.clickable { onCoinClick() }
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
            com.zynpath.game.core.designsystem.components.ZynpathMasterCurrencyPill(
                iconPainter = painterResource(id = R.drawable.ic_game_star),
                amount = "${uiState.totalStars}",
                modifier = Modifier.clickable { onNavigateToLevels() }
            )

            // Settings Gear Button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0A1931))
                    .border(1.2.dp, Color(0xFF1A3B6E), CircleShape)
                    .clickable { onNavigateToSettings() },
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
