package com.zynpath.game.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.theme.ForestMint

@Composable
fun StatusBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = ForestMint
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

/** Standard alias */
@Composable
fun ZynpathStatusBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = ForestMint
) = StatusBadge(text, modifier, color)

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun StatusBadgePreview() {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        StatusBadge("OFFLINE", color = ForestMint)
        StatusBadge("UNLOCKED", color = com.zynpath.game.core.designsystem.theme.AccentGold)
        StatusBadge("LOCKED", color = com.zynpath.game.core.designsystem.theme.TextMuted)
    }
}
