package com.zynpath.game.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import com.zynpath.game.R
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameShapes
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite

enum class RewardType {
    STAR,
    COIN,
    TROPHY,
    GEM,
    CROWN,
    CHEST
}

enum class RewardTier(val color: Color) {
    BRONZE(Color(0xFFCD7F32)),
    SILVER(Color(0xFFC0C0C0)),
    GOLD(Color(0xFFFFC247)),
    DIAMOND(Color(0xFF21D4FD))
}

/**
 * Arcade reward badge component displaying collectible rewards, quest milestones,
 * or level star achievements.
 *
 * @param type Icon type (STAR, COIN, TROPHY, GEM, CROWN, CHEST).
 * @param title Short title label (e.g. "Bonus", "First Win").
 * @param value Reward amount or tier (e.g. "+250", "3 Stars").
 * @param tier Badge cosmetic tier (BRONZE, SILVER, GOLD, DIAMOND).
 * @param modifier Custom modifier.
 * @param onClick Optional tap callback.
 */
@Composable
fun GameRewardBadge(
    type: RewardType,
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    tier: RewardTier = RewardTier.GOLD,
    onClick: (() -> Unit)? = null
) {
    val iconRes = when (type) {
        RewardType.STAR -> R.drawable.ic_game_star
        RewardType.COIN -> R.drawable.ic_game_coin
        RewardType.TROPHY -> R.drawable.ic_game_trophy
        RewardType.GEM -> R.drawable.ic_game_gem
        RewardType.CROWN -> R.drawable.ic_game_crown
        RewardType.CHEST -> R.drawable.ic_game_chest
    }

    val shape = GameShapes.badge

    Box(
        modifier = modifier
            .semantics {
                role = Role.Button
                contentDescription = "$title, $value"
            }
            .clip(shape)
            .background(Color(0xEE0B1736))
            .border(1.2.dp, tier.color.copy(alpha = 0.6f), shape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(tier.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f, fill = true),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title.uppercase(),
                    style = GameTypography.badgeText.copy(
                        color = tier.color,
                        fontSize = 10.sp,
                        letterSpacing = 0.2.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false
                )
                Text(
                    text = value,
                    style = GameTypography.labelMedium.copy(
                        color = GameWhite,
                        fontSize = 13.sp,
                        letterSpacing = 0.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false
                )
            }
        }
    }
}
