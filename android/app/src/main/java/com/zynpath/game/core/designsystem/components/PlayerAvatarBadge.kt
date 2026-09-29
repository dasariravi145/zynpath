package com.zynpath.game.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zynpath.game.R
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite

@Composable
fun PlayerAvatarBadge(
    displayName: String,
    playerTag: String,
    modifier: Modifier = Modifier,
    isGuest: Boolean = true,
    avatarDrawableId: Int = R.drawable.ic_game_avatar_guest,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xEE101D3C))
            .border(1.2.dp, GameRoyalBlue.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(GameDeepNavy)
                .border(1.5.dp, if (isGuest) GameElectricCyan else GameGoldHighlight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = avatarDrawableId),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = displayName,
                style = GameTypography.labelMedium
            )
            Text(
                text = if (isGuest) "Guest • $playerTag" else playerTag,
                style = GameTypography.secondaryInfo
            )
        }
    }
}

/**
 * Standalone avatar circle with customizable size and guest/online indicator.
 */
@Composable
fun ZynpathPlayerAvatar(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 40.dp,
    isGuest: Boolean = true,
    avatarBackgroundColor: Color = GameDeepNavy,
    avatarDrawableId: Int = R.drawable.ic_game_avatar_guest,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(avatarBackgroundColor)
            .border(1.5.dp, if (isGuest) GameElectricCyan else GameGoldHighlight, CircleShape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = avatarDrawableId),
            contentDescription = if (isGuest) "Guest Avatar" else "Player Avatar",
            tint = Color.Unspecified,
            modifier = Modifier.size(size * 0.88f)
        )
    }
}
