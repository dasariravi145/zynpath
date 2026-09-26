package com.zynpath.game.feature.placeholder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.components.ScreenHeader
import com.zynpath.game.core.designsystem.components.ZynpathPrimaryButton
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary

@Composable
fun DevStateScreen(
    featureTitle: String,
    scheduledPhase: String,
    description: String,
    onBackClick: () -> Unit,
    accentColor: Color = ForestMint
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        ScreenHeader(
            title = featureTitle,
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Construction,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(BackgroundCard)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = scheduledPhase,
                    color = AccentGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = featureTitle,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                fontSize = 14.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            ZynpathPrimaryButton(
                text = "Back to Home",
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth(0.85f)
            )
        }
    }
}
