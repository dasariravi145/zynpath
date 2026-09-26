package com.zynpath.game.feature.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.components.ScreenHeader
import com.zynpath.game.core.designsystem.components.ZynpathPrimaryButton
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary

val PremiumBenefits = listOf(
    "Ad-free gameplay — Zero banner or interstitial ads",
    "Unlimited solo hints — Clear stuck numbers anytime",
    "Exclusive solo puzzle collections — Bonus master worlds",
    "Premium themes & boards — Midnight, Neon & Forest Glass",
    "Custom path effects — Cyan pulse, Gold trail & Ember glow",
    "Profile customization — Exclusive avatar badges & titles",
    "Advanced player statistics — Solve speeds & perfect coverage rates"
)

@Composable
fun PremiumScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            ScreenHeader(
                title = "Zynpath Premium",
                subtitle = "Enhance your puzzle journey",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Gold Crown Badge
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(AccentGold.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = AccentGold,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "One Path. Premium Experience.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Pure logic without interruptions. 100% cosmetic and quality-of-life benefits with zero pay-to-win mechanics.",
                fontSize = 13.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Pricing Plans (Proposed & Indicative)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Monthly Plan
                PlanCard(
                    title = "Monthly",
                    indicativePrice = "₹99",
                    period = "/ month",
                    subtitle = "Flexible monthly access",
                    isRecommended = false,
                    modifier = Modifier.weight(1f)
                )

                // 6-Month Plan
                PlanCard(
                    title = "6 Months",
                    indicativePrice = "₹499",
                    period = "/ 6 months",
                    subtitle = "Save 16% annually",
                    isRecommended = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Benefits Checklist Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(BackgroundElevated)
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "Premium Features & Benefits",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    PremiumBenefits.forEach { benefit ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(ForestMint.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = ForestMint,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = benefit,
                                fontSize = 13.sp,
                                color = TextSecondary,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Honest Architectural Disclaimer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundCard.copy(alpha = 0.7f))
                    .border(1.dp, ForestMint.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "ROADMAP NOTICE: Live Google Play Billing integration and secure token subscription verification will be deployed in Phase 8 (Prompts 41–44). Displayed pricing is indicative.",
                    fontSize = 11.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            ZynpathPrimaryButton(
                text = "Subscribe (Coming in Phase 8)",
                onClick = {},
                enabled = false
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PlanCard(
    title: String,
    indicativePrice: String,
    period: String,
    subtitle: String,
    isRecommended: Boolean,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (isRecommended) BackgroundElevated else BackgroundCard)
            .then(
                if (isRecommended) Modifier.border(1.5.dp, AccentGold, shape) else Modifier
            )
            .padding(16.dp)
    ) {
        Column {
            if (isRecommended) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AccentGold.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "POPULAR",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = indicativePrice,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isRecommended) AccentGold else ForestMint
                )
                Text(
                    text = period,
                    fontSize = 11.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun PremiumScreenPreview() {
    PremiumScreen(onBackClick = {})
}
