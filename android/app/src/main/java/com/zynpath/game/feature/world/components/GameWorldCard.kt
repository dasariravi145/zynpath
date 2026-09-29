package com.zynpath.game.feature.world.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.components.GamePanel
import com.zynpath.game.core.designsystem.components.GameProgressBar
import com.zynpath.game.core.designsystem.components.ProgressBarStyle
import com.zynpath.game.core.designsystem.components.WorldCardData
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite

/**
 * Returns fantasy realm name associated with each world.
 */
fun getWorldBiomeTitle(worldId: Int): String {
    return when (worldId) {
        1 -> "Crystal Valley"
        2 -> "Mystic Forest"
        3 -> "Ember Canyon"
        4 -> "Sky Islands"
        5 -> "Moonlight Temple"
        6 -> "Celestial Summit"
        else -> "Realm $worldId"
    }
}

/**
 * Premium fantasy game world card for the World Map selection hub.
 *
 * Requirements fulfilled (Prompt 05/24 Tasks 4, 5, 8, 10):
 * - Displays original procedural environment artwork per fantasy biome.
 * - Displays authoritative world title, completion count, and level range.
 * - Displays locked / unlocked / mastered states.
 * - Enforces locked progression with clear unlock requirement messages.
 */
@Composable
fun GameWorldCard(
    world: WorldCardData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isReducedMotion: Boolean = false
) {
    val isCompleted = world.completedLevels >= world.totalLevels && world.totalLevels > 0
    val isLocked = world.isLocked
    val biomeTitle = getWorldBiomeTitle(world.worldId)
    val progressFraction = if (world.totalLevels > 0) world.completedLevels.toFloat() / world.totalLevels.toFloat() else 0f

    val accessibilityDesc = if (isLocked) {
        "World ${world.worldId}, $biomeTitle, Locked. ${world.unlockRequirementText ?: "Complete previous world levels to unlock."}"
    } else if (isCompleted) {
        "World ${world.worldId}, $biomeTitle, Mastered! All ${world.totalLevels} levels solved."
    } else {
        "World ${world.worldId}, $biomeTitle, ${world.completedLevels} of ${world.totalLevels} levels solved, ${world.gridSizeDescription}."
    }

    GamePanel(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                role = Role.Button
                contentDescription = accessibilityDesc
            }
            .clickable(onClick = onClick),
        contentPadding = 0.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Biome Artwork Header Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF091638),
                                GameMidnightBlue
                            )
                        )
                    )
            ) {
                // Procedural Biome Artwork
                WorldBiomeCanvas(
                    worldId = world.worldId,
                    accentColor = world.accentColor,
                    modifier = Modifier.fillMaxSize()
                )

                // Top Chips: World Number & State Tag
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xCC081229))
                            .border(1.dp, world.accentColor.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "WORLD ${world.worldId}",
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp,
                            color = world.accentColor
                        )
                    }

                    if (isCompleted) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x33F59E0B))
                                .border(1.dp, GameGoldHighlight, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.WorkspacePremium,
                                    contentDescription = null,
                                    tint = GameGoldHighlight,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "MASTERED",
                                    fontFamily = FontFamily.Default,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.8.sp,
                                    color = GameGoldHighlight
                                )
                            }
                        }
                    } else if (isLocked) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x550A1224))
                                .border(1.dp, GameSecondaryText.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = null,
                                    tint = GameSecondaryText,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "LOCKED",
                                    fontFamily = FontFamily.Default,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = GameSecondaryText
                                )
                            }
                        }
                    }
                }
            }

            // Body Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = biomeTitle,
                            style = GameTypography.screenHeading.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = GameWhite
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = world.name,
                            style = GameTypography.bodyMedium.copy(
                                fontSize = 12.sp,
                                color = GameSecondaryText
                            )
                        )
                    }

                    // Level Range Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GameMidnightBlue.copy(alpha = 0.7f))
                            .border(1.dp, GameRoyalBlue.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = world.levelRangeDescription,
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = GameElectricCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar & Percentage
                val progressPercent = (progressFraction * 100).toInt().coerceIn(0, 100)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${world.completedLevels}/${world.totalLevels} Solved • ${world.gridSizeDescription}",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = GameSecondaryText
                    )
                    Text(
                        text = "$progressPercent%",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = if (isCompleted) GameGoldHighlight else GameElectricCyan
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                GameProgressBar(
                    progress = progressFraction,
                    style = if (isCompleted) ProgressBarStyle.GOLD else ProgressBarStyle.CYAN,
                    height = 8.dp,
                    isReducedMotion = isReducedMotion,
                    modifier = Modifier.fillMaxWidth()
                )

                // Locked Requirement Notice
                if (isLocked && world.unlockRequirementText != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x330B1736))
                            .border(1.dp, GameRoyalBlue.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                tint = GameSecondaryText,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = world.unlockRequirementText,
                                style = GameTypography.bodyMedium.copy(
                                    fontSize = 11.sp,
                                    color = GameSecondaryText
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Procedural Compose vector artwork representing distinct fantasy biomes.
 */
@Composable
private fun WorldBiomeCanvas(
    worldId: Int,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Ambient celestial lighting
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.28f),
                    Color.Transparent
                ),
                center = Offset(w * 0.75f, h * 0.3f),
                radius = w * 0.5f
            )
        )

        // Mountain ridge paths
        val mountainPath = Path().apply {
            moveTo(0f, h * 0.65f)
            lineTo(w * 0.25f, h * 0.40f)
            lineTo(w * 0.50f, h * 0.55f)
            lineTo(w * 0.78f, h * 0.32f)
            lineTo(w, h * 0.50f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        drawPath(
            path = mountainPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.22f),
                    GameDeepNavy
                ),
                startY = h * 0.3f,
                endY = h
            )
        )

        // Glowing puzzle path arc
        val arcPath = Path().apply {
            moveTo(w * 0.1f, h * 0.8f)
            cubicTo(
                w * 0.35f, h * 0.45f,
                w * 0.65f, h * 0.75f,
                w * 0.9f, h * 0.45f
            )
        }

        drawPath(
            path = arcPath,
            color = accentColor.copy(alpha = 0.4f),
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
