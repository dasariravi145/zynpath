package com.zynpath.game.feature.level.components

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.zynpath.game.core.designsystem.components.GamePrimaryButton
import com.zynpath.game.core.designsystem.components.GameSecondaryButton
import com.zynpath.game.core.designsystem.components.LevelCardData
import com.zynpath.game.core.designsystem.components.LevelState
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite

import com.zynpath.game.core.designsystem.theme.ChapterTheme
import com.zynpath.game.core.designsystem.theme.ChapterThemes

/**
 * Polished game-style details modal panel shown when a level checkpoint is tapped.
 *
 * Requirements fulfilled (Prompt 05/24 Task 6, Prompt 30 Tasks 4, 5, 8):
 * - Displays exact repository data: Level number, Chapter title, Completion state, Stars earned,
 *   best time (if solved), and difficulty category.
 * - Chapter theme styling with custom border brush and visual direction indicator.
 * - Action buttons: PLAY, REPLAY, and CLOSE.
 * - Locked levels show clear lock explanation without bypassing progression.
 * - Fast entry with no artificial delays or fake currencies.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelDetailsDialog(
    level: LevelCardData,
    worldId: Int,
    worldName: String,
    onPlayClick: () -> Unit,
    onDismiss: () -> Unit,
    isReducedMotion: Boolean = false,
    chapterTheme: ChapterTheme = ChapterThemes.getThemeForLevel(level.levelNumber)
) {
    val isCompleted = level.state == LevelState.COMPLETED
    val isUnlocked = level.state == LevelState.UNLOCKED
    val isLocked = level.state == LevelState.LOCKED
    val isCurrent = level.isCurrent || (isUnlocked && !isCompleted)
    val isMilestone = level.isMilestone || level.levelNumber in setOf(25, 50, 100, 150, 200, 250, 300)
    val isFinale = level.levelNumber == 300

    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(chapterTheme.surfaceBrush)
                .border(
                    width = 1.4.dp,
                    brush = if (isMilestone) {
                        chapterTheme.milestoneBadgeBrush
                    } else if (isCurrent || isCompleted) {
                        chapterTheme.nodeBorderBrush
                    } else {
                        Brush.verticalGradient(
                            listOf(GameRoyalBlue.copy(alpha = 0.5f), GameRoyalBlue.copy(alpha = 0.2f))
                        )
                    },
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(22.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header: Chapter Badge & World / Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(chapterTheme.primaryAccent.copy(alpha = 0.2f))
                                    .border(1.dp, chapterTheme.primaryAccent.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "CHAPTER ${chapterTheme.chapterId}",
                                    fontFamily = FontFamily.Default,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    letterSpacing = 0.8.sp,
                                    color = chapterTheme.primaryAccent
                                )
                            }

                            Text(
                                text = "WORLD $worldId",
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp,
                                color = GameSecondaryText
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "${chapterTheme.title} • $worldName",
                            style = GameTypography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = GameSecondaryText
                            )
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(GameRoyalBlue.copy(alpha = 0.25f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close level details",
                            tint = GameSecondaryText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Level Number Title
                Text(
                    text = "LEVEL ${level.localLevelNumber}",
                    style = GameTypography.gameTitle.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp,
                        color = GameWhite
                    )
                )

                // Chapter Subtitle & Visual Direction
                Text(
                    text = "${chapterTheme.subtitle} • ${chapterTheme.visualDirection}",
                    style = GameTypography.bodyMedium.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = chapterTheme.primaryAccent
                    )
                )

                // Milestone Badge
                if (isMilestone) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        chapterTheme.primaryAccent.copy(alpha = 0.25f),
                                        chapterTheme.secondaryAccent.copy(alpha = 0.25f)
                                    )
                                )
                            )
                            .border(1.dp, chapterTheme.milestoneGlow, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(
                                    id = if (isFinale || level.levelNumber in setOf(100, 200)) {
                                        R.drawable.ic_game_crown
                                    } else {
                                        R.drawable.ic_game_trophy
                                    }
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isFinale) {
                                    "GRAND FINALE • ALL 300 LEVELS"
                                } else {
                                    level.milestoneTitle ?: "CHAPTER MILESTONE"
                                },
                                style = GameTypography.bodyMedium.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GameGoldHighlight,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Status & Difficulty Badges Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status Pill
                    val statusText = when {
                        isCompleted -> "COMPLETED"
                        isCurrent -> "CURRENT"
                        isUnlocked -> "READY"
                        else -> "LOCKED"
                    }
                    val statusColor = when {
                        isCompleted -> GameGoldHighlight
                        isCurrent -> GameElectricCyan
                        isUnlocked -> Color(0xFF38BDF8)
                        else -> GameSecondaryText
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = statusText,
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            letterSpacing = 0.8.sp,
                            color = statusColor
                        )
                    }

                    // Difficulty Indicator
                    val diffLabel = level.difficultyCategory ?: level.difficultyBand ?: "STANDARD"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(chapterTheme.secondaryAccent.copy(alpha = 0.15f))
                            .border(1.dp, chapterTheme.secondaryAccent.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = diffLabel.uppercase(),
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.8.sp,
                            color = chapterTheme.secondaryAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Star Rating Display (3 Star Slots)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val earnedStars = level.stars.coerceIn(0, 3)
                    for (s in 1..3) {
                        val isEarned = s <= earnedStars
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isEarned) Color(0x33F59E0B) else Color(0x22111E3D)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isEarned) GameGold.copy(alpha = 0.8f) else GameRoyalBlue.copy(alpha = 0.3f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_game_star),
                                contentDescription = if (isEarned) "Star earned" else "Star not earned",
                                modifier = Modifier.size(24.dp),
                                alpha = if (isEarned) 1f else 0.35f
                            )
                        }
                    }
                }

                // Additional Repository Information: Best Time
                if (level.bestTimeSeconds != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x550B1633))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val minutes = level.bestTimeSeconds / 60
                        val seconds = level.bestTimeSeconds % 60
                        val timeStr = String.format("%02d:%02d", minutes, seconds)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Timer,
                                contentDescription = null,
                                tint = GameGoldHighlight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Personal Best: $timeStr",
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = GameWhite
                            )
                        }
                    }
                }

                // Locked Explanation Notice
                if (isLocked) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x33EF4444))
                            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                tint = Color(0xFFFCA5A5),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Solve Level ${level.levelNumber - 1} to unlock this path.",
                                style = GameTypography.bodyMedium.copy(
                                    fontSize = 12.sp,
                                    color = Color(0xFFFCA5A5)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Primary Action Button
                if (isCompleted) {
                    GamePrimaryButton(
                        text = "REPLAY LEVEL",
                        onClick = {
                            onDismiss()
                            onPlayClick()
                        },
                        isReducedMotion = isReducedMotion,
                        height = 50.dp,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Restore,
                                contentDescription = null,
                                tint = GameDeepNavy,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )
                } else if (isUnlocked || isCurrent) {
                    GamePrimaryButton(
                        text = if (isFinale) "CONQUER LEVEL 300" else "PLAY LEVEL",
                        onClick = {
                            onDismiss()
                            onPlayClick()
                        },
                        isReducedMotion = isReducedMotion,
                        height = 50.dp,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = GameDeepNavy,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    )
                } else {
                    GameSecondaryButton(
                        text = "LOCKED",
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

