package com.zynpath.game.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.theme.GameCardBorder
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.core.puzzle.experience.ChapterDefinition

/**
 * Reusable header component displaying chapter identity, subtitle, and level boundaries.
 *
 * Implements Prompt 25 Task 8:
 * Deep navy background with royal-blue gradients, electric-cyan accents, and chapter badge.
 */
@Composable
fun ChapterTitleHeader(
    chapter: ChapterDefinition,
    completedLevelsCount: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GameMidnightBlue.copy(alpha = 0.95f),
                        GameDeepNavy
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        GameElectricCyan.copy(alpha = 0.5f),
                        GameRoyalBlue.copy(alpha = 0.3f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chapter Badge
                Box(
                    modifier = Modifier
                        .background(
                            color = GameRoyalBlue.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = GameElectricCyan.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "CHAPTER ${chapter.chapterId}",
                        color = GameElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                // Range / Completion indicator
                Text(
                    text = "Levels ${chapter.startLevel}–${chapter.endLevel} • $completedLevelsCount/${chapter.totalLevels}",
                    color = GameSecondaryText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Chapter Title
            Text(
                text = chapter.title,
                color = GameWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )

            // Chapter Subtitle
            Text(
                text = chapter.subtitle,
                color = GameElectricCyan.copy(alpha = 0.9f),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            // Description
            Text(
                text = chapter.description,
                color = GameSecondaryText,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}
