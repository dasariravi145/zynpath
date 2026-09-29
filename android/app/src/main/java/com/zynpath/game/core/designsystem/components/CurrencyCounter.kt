package com.zynpath.game.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameOrangeAccent
import com.zynpath.game.core.designsystem.theme.GameShapes
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite
import java.text.NumberFormat
import java.util.Locale

/**
 * Arcade currency pill counter displaying player gold coins with an embossed coin icon
 * and optional "+" action button to access the reward store.
 *
 * @param count Current player coin balance.
 * @param modifier Custom modifier.
 * @param showAddButton If true, displays a golden "+" button on the right edge.
 * @param onAddClick Callback triggered when the "+" button or counter is tapped.
 */
@Composable
fun CurrencyCounter(
    count: Int,
    modifier: Modifier = Modifier,
    showAddButton: Boolean = true,
    onAddClick: (() -> Unit)? = null
) {
    val formattedCount = NumberFormat.getNumberInstance(Locale.US).format(count)

    Box(
        modifier = modifier
            .semantics {
                role = Role.Button
                contentDescription = "$formattedCount coins"
            }
            .clip(GameShapes.pill)
            .background(Color(0xEE0B1736))
            .border(1.2.dp, GameGold.copy(alpha = 0.6f), GameShapes.pill)
            .then(if (onAddClick != null) Modifier.clickable { onAddClick() } else Modifier)
            .padding(start = 6.dp, end = if (showAddButton) 4.dp else 12.dp, top = 4.dp, bottom = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_game_coin),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = formattedCount,
                style = GameTypography.labelMedium.copy(
                    color = GameWhite
                )
            )

            if (showAddButton) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(GameGold)
                        .border(1.dp, GameGoldHighlight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add coins",
                        tint = GameDeepNavy,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
