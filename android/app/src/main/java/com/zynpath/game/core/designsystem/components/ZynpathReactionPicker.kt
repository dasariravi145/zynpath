package com.zynpath.game.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary

data class PredefinedReaction(
    val id: String,
    val emoji: String,
    val phrase: String
)

val StandardReactions: List<PredefinedReaction> = listOf(
    PredefinedReaction("wow", "😲", "Wow!"),
    PredefinedReaction("nice", "👏", "Nice!"),
    PredefinedReaction("gg", "🤝", "GG!"),
    PredefinedReaction("well_played", "🎯", "Well played!"),
    PredefinedReaction("good_luck", "🍀", "Good luck!"),
    PredefinedReaction("amazing", "⚡", "Amazing!"),
    PredefinedReaction("rematch", "🔄", "Rematch!")
)

/**
 * Reusable reaction-picker UI for online duels and leagues.
 * Zero freeform text input and zero permanent chat storage.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ZynpathReactionPicker(
    onSelectReaction: (PredefinedReaction) -> Unit,
    modifier: Modifier = Modifier,
    reactions: List<PredefinedReaction> = StandardReactions,
    title: String = "Send Reaction"
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BackgroundElevated)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Preset Only",
                fontSize = 11.sp,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            reactions.forEach { reaction ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(BackgroundCard)
                        .clickable { onSelectReaction(reaction) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = reaction.emoji,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = reaction.phrase,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ForestMint
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun ReactionPickerPreview() {
    ZynpathReactionPicker(onSelectReaction = {})
}
