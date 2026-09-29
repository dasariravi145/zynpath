package com.zynpath.game.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.R
import com.zynpath.game.core.designsystem.theme.GameBorders
import com.zynpath.game.core.designsystem.theme.GameBrushes
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameSuccessGreen
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite

enum class AvatarFrameType {
    NONE,
    BRONZE,
    CYAN_MYSTIC,
    GOLD_CHAMPION
}

enum class AvatarSize(val dp: Dp, val badgeTextSizeSp: Int) {
    SMALL(36.dp, 8),
    MEDIUM(48.dp, 9),
    LARGE(64.dp, 10),
    HERO(80.dp, 11)
}

/**
 * Premium game player avatar featuring cosmetic frames, level badges, and online status indicators.
 *
 * @param avatarDrawableId Resource ID of the avatar illustration.
 * @param modifier Custom modifier.
 * @param size Predefined size token (defaults to MEDIUM).
 * @param frame Cosmetic frame style (defaults to GOLD_CHAMPION).
 * @param badgeText Optional text displayed on bottom pill (e.g. "LV.12" or "GUEST").
 * @param showOnlineIndicator When true, renders a glowing green online presence dot.
 * @param onClick Optional tap callback.
 */
@Composable
fun PlayerAvatar(
    modifier: Modifier = Modifier,
    avatarDrawableId: Int = R.drawable.ic_game_avatar_guest,
    size: AvatarSize = AvatarSize.MEDIUM,
    frame: AvatarFrameType = AvatarFrameType.GOLD_CHAMPION,
    badgeText: String? = null,
    showOnlineIndicator: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val totalSize = size.dp

    val frameBorder = when (frame) {
        AvatarFrameType.NONE -> null
        AvatarFrameType.BRONZE -> GameBorders.cardSubtle
        AvatarFrameType.CYAN_MYSTIC -> GameBorders.avatarCyan
        AvatarFrameType.GOLD_CHAMPION -> GameBorders.avatarGold
    }

    Box(
        modifier = modifier
            .size(totalSize)
            .semantics {
                if (onClick != null) {
                    role = Role.Button
                    contentDescription = "Player avatar${badgeText?.let { ", $it" } ?: ""}"
                }
            }
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        // Outer avatar disk with frame
        Box(
            modifier = Modifier
                .size(totalSize)
                .clip(CircleShape)
                .then(if (frameBorder != null) Modifier.border(frameBorder, CircleShape) else Modifier)
                .background(GameMidnightBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = avatarDrawableId),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(totalSize * 0.90f)
                    .clip(CircleShape)
            )
        }

        // Online status dot indicator
        if (showOnlineIndicator) {
            val indicatorSize = (totalSize.value * 0.22f).dp.coerceAtLeast(8.dp)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(indicatorSize)
                    .clip(CircleShape)
                    .background(GameSuccessGreen)
                    .border(1.5.dp, GameDeepNavy, CircleShape)
            )
        }

        // Bottom level or guest badge pill
        if (badgeText != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 4.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(GameDeepNavy, GameMidnightBlue)
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = if (frame == AvatarFrameType.GOLD_CHAMPION) GameGoldHighlight else GameElectricCyan,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 1.dp)
            ) {
                Text(
                    text = badgeText.uppercase(),
                    style = GameTypography.badgeText.copy(
                        fontSize = size.badgeTextSizeSp.sp,
                        color = if (frame == AvatarFrameType.GOLD_CHAMPION) GameGoldHighlight else GameWhite
                    )
                )
            }
        }
    }
}
