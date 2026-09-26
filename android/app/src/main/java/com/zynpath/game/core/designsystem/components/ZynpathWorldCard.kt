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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary

data class WorldCardData(
    val worldId: Int,
    val name: String,
    val gridSizeDescription: String,
    val levelRangeDescription: String,
    val totalLevels: Int,
    val completedLevels: Int,
    val isLocked: Boolean,
    val hasWalls: Boolean = false,
    val accentColor: Color = ForestMint
)

@Composable
fun ZynpathWorldCard(
    world: WorldCardData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)
    val isCompleted = world.completedLevels >= world.totalLevels && world.totalLevels > 0
    val progress = if (world.totalLevels > 0) world.completedLevels.toFloat() / world.totalLevels.toFloat() else 0f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (world.isLocked) BackgroundCard.copy(alpha = 0.6f) else BackgroundElevated)
            .then(
                if (!world.isLocked && !isCompleted) Modifier.border(1.dp, world.accentColor.copy(alpha = 0.4f), shape) else Modifier
            )
            .clickable(enabled = !world.isLocked) { onClick() }
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    world.isLocked -> BackgroundCard
                                    isCompleted -> AccentGold.copy(alpha = 0.2f)
                                    else -> world.accentColor.copy(alpha = 0.2f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${world.worldId}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                world.isLocked -> TextMuted
                                isCompleted -> AccentGold
                                else -> world.accentColor
                            }
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = world.name,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (world.isLocked) TextMuted else TextPrimary
                        )
                        Text(
                            text = "${world.gridSizeDescription} • ${world.levelRangeDescription}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Status Icon or Pill
                if (world.isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = TextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                } else if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentGold.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = AccentGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "COMPLETE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentGold
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(ForestMint)
                            .size(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = BackgroundDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar and counts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (world.isLocked) "Locked" else "${world.completedLevels} / ${world.totalLevels} Completed",
                    fontSize = 12.sp,
                    color = if (world.isLocked) TextMuted else TextSecondary
                )
                if (world.hasWalls) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFE63946).copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "WALLS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE63946)
                        )
                    }
                }
            }

            if (!world.isLocked) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isCompleted) AccentGold else world.accentColor,
                    trackColor = BackgroundCard,
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun ZynpathWorldCardPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ZynpathWorldCard(
            world = WorldCardData(
                worldId = 1,
                name = "Learn the Path",
                gridSizeDescription = "4×4",
                levelRangeDescription = "Levels 1–20",
                totalLevels = 20,
                completedLevels = 14,
                isLocked = false
            ),
            onClick = {}
        )
        ZynpathWorldCard(
            world = WorldCardData(
                worldId = 3,
                name = "Wall Challenge",
                gridSizeDescription = "5×5 with walls",
                levelRangeDescription = "Levels 51–100",
                totalLevels = 50,
                completedLevels = 0,
                isLocked = true,
                hasWalls = true
            ),
            onClick = {}
        )
    }
}
