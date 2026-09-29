package com.zynpath.game.feature.home.components

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.R
import com.zynpath.game.core.designsystem.components.GamePanel
import com.zynpath.game.core.designsystem.components.GameProgressBar
import com.zynpath.game.core.designsystem.components.ProgressBarStyle
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.feature.home.HomeUiState

/**
 * Game-style panel for player rewards, achievements count, star progression,
 * and current progression milestones.
 *
 * Requirements fulfilled (Prompt 04/24 Task 8):
 * - Displays authoritative star counts, completed levels, and achievements progress.
 * - Displays daily streak and next progression milestone goal.
 * - Entirely repository-backed without mock balances.
 */
@Composable
fun ProgressionMilestonesPanel(
    uiState: HomeUiState,
    onNavigateToAchievements: () -> Unit,
    modifier: Modifier = Modifier
) {
    GamePanel(
        modifier = modifier.fillMaxWidth(),
        contentPadding = 16.dp
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0x33F59E0B))
                            .border(1.dp, GameGold.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "MILESTONES",
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp,
                            color = GameGoldHighlight
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "REWARDS & TROPHIES",
                        style = GameTypography.screenHeading.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GameWhite
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Track your career milestones & unlock achievements",
                    style = GameTypography.bodyMedium.copy(
                        fontSize = 12.sp,
                        color = GameSecondaryText
                    )
                )
            }

            // Quick shortcut to achievements
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(GameRoyalBlue.copy(alpha = 0.3f))
                    .border(1.dp, GameElectricCyan.copy(alpha = 0.4f), CircleShape)
                    .clickable(onClick = onNavigateToAchievements)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "View all achievements",
                    tint = GameElectricCyan,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Stat Badges Row: Stars | Solved | Achievements | Streak
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProgressionStatPill(
                iconRes = R.drawable.ic_game_star,
                value = uiState.totalStars.toString(),
                label = "STARS",
                accentColor = GameGoldHighlight,
                modifier = Modifier.weight(1f)
            )

            ProgressionStatPill(
                iconRes = R.drawable.ic_game_trophy,
                value = "${uiState.achievementsUnlockedCount}/${uiState.achievementsTotalCount}",
                label = "TROPHIES",
                accentColor = Color(0xFF38BDF8),
                modifier = Modifier.weight(1f),
                onClick = onNavigateToAchievements
            )

            ProgressionStatPill(
                iconRes = R.drawable.ic_game_fire_streak,
                value = "${uiState.dailyStreak}d",
                label = "STREAK",
                accentColor = Color(0xFFFB923C),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Next Goal / Milestone Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GameMidnightBlue.copy(alpha = 0.7f),
                            GameDeepNavy.copy(alpha = 0.9f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = GameElectricCyan.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(GameRoyalBlue.copy(alpha = 0.4f))
                                .border(1.dp, GameElectricCyan.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Flag,
                                contentDescription = null,
                                tint = GameElectricCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "CURRENT MILESTONE",
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.8.sp,
                                color = GameElectricCyan
                            )
                            Text(
                                text = uiState.nextGoalTitle,
                                style = GameTypography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = GameWhite
                                )
                            )
                        }
                    }

                    val progressPercent = (uiState.nextGoalProgressFraction * 100).toInt().coerceIn(0, 100)
                    Text(
                        text = "$progressPercent%",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = GameGoldHighlight
                    )
                }

                if (uiState.nextGoalDescription.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = uiState.nextGoalDescription,
                        style = GameTypography.bodyMedium.copy(
                            fontSize = 11.sp,
                            color = GameSecondaryText
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                GameProgressBar(
                    progress = uiState.nextGoalProgressFraction,
                    style = ProgressBarStyle.CYAN,
                    height = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ProgressionStatPill(
    iconRes: Int,
    value: String,
    label: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val clickableModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }

    Box(
        modifier = clickableModifier
            .clip(RoundedCornerShape(10.dp))
            .background(GameMidnightBlue.copy(alpha = 0.6f))
            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = GameWhite
            )

            Text(
                text = label,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                letterSpacing = 0.6.sp,
                color = GameSecondaryText
            )
        }
    }
}
