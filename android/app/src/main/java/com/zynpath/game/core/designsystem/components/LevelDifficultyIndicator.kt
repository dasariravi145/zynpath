package com.zynpath.game.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.puzzle.experience.ExperienceDifficultyCategory

/**
 * Reusable visual indicator for level difficulty categories.
 *
 * Implements Prompt 25 Task 8:
 * Compact badge with color-coded dot and category label.
 */
@Composable
fun LevelDifficultyIndicator(
    difficulty: ExperienceDifficultyCategory,
    modifier: Modifier = Modifier
) {
    val categoryColor = difficulty.color

    Row(
        modifier = modifier
            .background(
                color = categoryColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp)
            )
            .border(
                width = 1.dp,
                color = categoryColor.copy(alpha = 0.4f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color = categoryColor, shape = CircleShape)
        )
        Text(
            text = difficulty.displayName.uppercase(),
            color = categoryColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}
