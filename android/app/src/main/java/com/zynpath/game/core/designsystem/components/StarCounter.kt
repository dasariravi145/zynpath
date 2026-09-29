package com.zynpath.game.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.zynpath.game.R
import com.zynpath.game.core.designsystem.theme.GameBrushes
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameShapes
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite

/**
 * Star counter badge displaying the player's earned stars in solo worlds or total progression.
 *
 * @param currentStars Number of stars collected.
 * @param maxStars Optional total stars possible in the world/game (e.g. 60).
 * @param modifier Custom modifier.
 * @param onClick Optional tap callback.
 */
@Composable
fun StarCounter(
    currentStars: Int,
    modifier: Modifier = Modifier,
    maxStars: Int? = null,
    onClick: (() -> Unit)? = null
) {
    val textDisplay = if (maxStars != null) "$currentStars / $maxStars" else "$currentStars"

    Box(
        modifier = modifier
            .semantics {
                role = Role.Button
                contentDescription = "$textDisplay stars"
            }
            .clip(GameShapes.pill)
            .background(Color(0xEE0B1736))
            .border(1.2.dp, GameGoldHighlight.copy(alpha = 0.5f), GameShapes.pill)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_game_star),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = textDisplay,
                style = GameTypography.labelMedium.copy(
                    color = GameWhite
                )
            )
        }
    }
}
