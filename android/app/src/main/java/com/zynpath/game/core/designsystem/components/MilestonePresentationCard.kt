package com.zynpath.game.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldGlow
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.core.puzzle.experience.MilestoneIndicator
import com.zynpath.game.core.puzzle.experience.MilestoneType

/**
 * Reusable card presenting milestones and pacing accomplishments.
 *
 * Implements Prompt 25 Task 8:
 * Deep navy container with royal-blue gradients, gold accents, and milestone icon.
 */
@Composable
fun MilestonePresentationCard(
    milestone: MilestoneIndicator,
    levelId: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GameRoyalBlue.copy(alpha = 0.5f),
                        GameMidnightBlue
                    )
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        GameGold,
                        GameElectricCyan.copy(alpha = 0.6f)
                    )
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Milestone Badge Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                GameGold.copy(alpha = 0.4f),
                                GameDeepNavy
                            )
                        ),
                        shape = CircleShape
                    )
                    .border(width = 1.5.dp, color = GameGold, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = milestone.milestoneTitle,
                    tint = GameGold,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Milestone Details
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(GameGold.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = when (milestone.type) {
                                MilestoneType.GRAND_FINALE -> "GRAND FINALE"
                                MilestoneType.CENTURY_MARK -> "CENTURY"
                                MilestoneType.CHAPTER_CLIMAX -> "CLIMAX"
                                else -> "MILESTONE"
                            },
                            color = GameGold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Text(
                        text = "Level $levelId",
                        color = GameElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = milestone.milestoneTitle,
                    color = GameWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = milestone.milestoneSubtitle,
                    color = GameSecondaryText,
                    fontSize = 12.sp
                )
            }
        }
    }
}
