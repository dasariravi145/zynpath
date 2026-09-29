package com.zynpath.game.feature.statistics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.components.ZynpathPrimaryButton
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary

/**
 * Preview and upgrade card for free users viewing the Premium Insights tab.
 * Accurately describes advanced capabilities without fabricating fake user metrics.
 * Implements Prompt 30 Sections 6, 8, 39, 40 & 41.
 */
@Composable
fun PremiumAnalyticsPreviewCard(
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .semantics(mergeDescendants = true) {
                    contentDescription = "Zynpath Premium Advanced Analytics Preview"
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.AutoGraph,
                    contentDescription = null,
                    tint = AccentGold,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Advanced Personal Analytics",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Available with Zynpath Premium",
                        fontSize = 12.sp,
                        color = AccentGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(BackgroundElevated)
                    .padding(12.dp)
            ) {
                FeatureBullet(text = "Daily and weekly puzzle completion trends")
                Spacer(modifier = Modifier.height(8.dp))
                FeatureBullet(text = "Same-puzzle time improvement & delta analysis")
                Spacer(modifier = Modifier.height(8.dp))
                FeatureBullet(text = "Detailed per-world solve velocity and average times")
                Spacer(modifier = Modifier.height(8.dp))
                FeatureBullet(text = "Chronological personal best history & replay sessions")
                Spacer(modifier = Modifier.height(8.dp))
                FeatureBullet(text = "Dedicated Premium Solo pack progression insights")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Personal analytics are strictly derived from your actual recorded gameplay. We never fabricate numbers or improvement claims.",
                fontSize = 11.sp,
                color = TextMuted,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            ZynpathPrimaryButton(
                text = "Unlock Premium Insights",
                onClick = onUpgradeClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun FeatureBullet(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = ForestMint,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}
