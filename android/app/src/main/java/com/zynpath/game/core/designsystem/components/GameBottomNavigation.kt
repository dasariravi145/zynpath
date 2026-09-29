package com.zynpath.game.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import androidx.compose.ui.res.painterResource
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.core.designsystem.theme.RefGoldGradientMid
import com.zynpath.game.core.designsystem.theme.RefNavyBorder
import com.zynpath.game.core.designsystem.theme.RefNavyDark
import com.zynpath.game.core.designsystem.theme.RefNavyNav
import com.zynpath.game.core.designsystem.theme.RefNavySurface

data class GameBottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector? = null,
    val iconRes: Int? = null,
    val badgeCount: Int = 0
)

/**
 * Arcade-styled bottom navigation bar with a glassmorphism dock, animated selection pills,
 * and glowing icons.
 *
 * @param currentRoute The active destination route.
 * @param onNavigate Callback invoked when a destination is selected.
 * @param items List of navigation destinations.
 * @param modifier Custom modifier.
 */
@Composable
fun GameBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    items: List<GameBottomNavItem>,
    modifier: Modifier = Modifier
) {
    val dockShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(dockShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        RefNavySurface,
                        RefNavyNav
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        RefNavyBorder,
                        RefNavyNav
                    )
                ),
                shape = dockShape
            )
            .navigationBarsPadding()
            .height(66.dp)
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route

                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.05f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "NavScale"
                )

                val selectedGold = Color(0xFFFED541)
                val unselectedGray = Color(0xFF8EA0B8)

                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) selectedGold else unselectedGray,
                    label = "NavColor"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(66.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onNavigate(item.route) }
                        )
                        .semantics {
                            role = Role.Tab
                            selected = isSelected
                            contentDescription = "${item.label}, ${if (isSelected) "selected" else "not selected"}"
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) selectedGold.copy(alpha = 0.12f)
                                else Color.Transparent
                            )
                            .padding(horizontal = 12.dp, vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (item.iconRes != null) {
                            Icon(
                                painter = painterResource(id = item.iconRes),
                                contentDescription = null,
                                tint = iconColor,
                                modifier = Modifier.size(24.dp)
                            )
                        } else if (item.icon != null) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = iconColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        if (item.badgeCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(GameGoldHighlight)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.label,
                        style = GameTypography.badgeText.copy(
                            color = if (isSelected) selectedGold else unselectedGray
                        )
                    )
                }
            }
        }
    }
}
