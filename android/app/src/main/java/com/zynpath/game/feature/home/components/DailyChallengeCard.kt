package com.zynpath.game.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.R
import com.zynpath.game.core.designsystem.components.GamePanel
import com.zynpath.game.core.designsystem.components.GamePrimaryButton
import com.zynpath.game.core.designsystem.components.GameSecondaryButton
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.feature.home.HomeUiState

/**
 * Premium Daily Challenge Card on the Home Screen.
 *
 * Requirements fulfilled (Prompt 04/24 Task 7):
 * - Displays challenge status, grid dimensions, and completion state.
 * - Displays current streak and reset countdown.
 * - Dynamic action: "PLAY TODAY'S PUZZLE" or "VIEW LEADERBOARD" if already solved.
 * - Fully backed by real repository data via [HomeUiState].
 */
@Composable
fun DailyChallengeCard(
    uiState: HomeUiState,
    onDailyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isReducedMotion = uiState.isReducedMotion
    val isCompleted = uiState.isDailyCompleted

    GamePanel(
        modifier = modifier.fillMaxWidth(),
        contentPadding = 16.dp
    ) {
        // Top Header
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
                            .background(Color(0x3320D76B))
                            .border(1.dp, Color(0xFF20D76B).copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "DAILY PUZZLE",
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp,
                            color = Color(0xFF20D76B)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "DAILY CHALLENGE",
                        style = GameTypography.screenHeading.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GameWhite
                        )
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${uiState.dailyChallengeDate} • ${uiState.dailyChallengeGrid} Grid",
                    style = GameTypography.secondaryInfo.copy(color = GameSecondaryText)
                )
            }

            // Streak Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x991A1428))
                    .border(1.dp, GameGoldHighlight.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_game_fire_streak),
                    contentDescription = null,
                    tint = GameGoldHighlight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${uiState.dailyStreak} Streak",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = GameGoldHighlight
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Info Banner: Completion state & countdown
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x660B1736))
                .border(1.dp, GameRoyalBlue.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = GameGoldHighlight,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Solved in ${uiState.dailySolveTime ?: "--:--"}",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = GameGoldHighlight
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF22C55E))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Ready to Play",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF22C55E)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = GameSecondaryText,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Resets in ${uiState.dailyResetCountdown}",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    color = GameSecondaryText
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Button
        if (isCompleted) {
            GameSecondaryButton(
                text = "VIEW LEADERBOARD",
                onClick = onDailyClick,
                isReducedMotion = isReducedMotion,
                height = 48.dp,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Leaderboard,
                        contentDescription = null,
                        tint = GameElectricCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
        } else {
            GamePrimaryButton(
                text = "PLAY DAILY PUZZLE",
                onClick = onDailyClick,
                isReducedMotion = isReducedMotion,
                height = 50.dp,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = GameDeepNavy,
                        modifier = Modifier.size(22.dp)
                    )
                }
            )
        }
    }
}
