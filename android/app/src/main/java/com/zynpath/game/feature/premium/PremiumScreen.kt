package com.zynpath.game.feature.premium

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zynpath.game.core.billing.SubscriptionConstants
import com.zynpath.game.core.billing.model.SubscriptionOffer
import com.zynpath.game.core.billing.model.SubscriptionPlanType
import com.zynpath.game.core.designsystem.components.ScreenHeader
import com.zynpath.game.core.designsystem.components.ZynpathPrimaryButton
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.CoralRed
import com.zynpath.game.core.designsystem.theme.ElectricCyan
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary
import com.zynpath.game.core.premium.model.EntitlementStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PremiumScreen(
    onBackClick: () -> Unit,
    onNavigateToAuth: () -> Unit = {},
    onNavigateToPacks: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: PremiumViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context.findActivity()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            ScreenHeader(
                title = "Zynpath Premium",
                subtitle = "Pure logic. Zero interruptions.",
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
                text = if (uiState.isPremiumActive) "You are a Premium Member" else "Upgrade to Zynpath Premium",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Enjoy an ad-free experience, unlimited solo hints, and exclusive cosmetic customizations. Never pay-to-win.",
                fontSize = 13.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Entitlement Status Banner
            EntitlementStatusBanner(
                uiState = uiState,
                onManageSubscription = {
                    openGooglePlaySubscriptions(context)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Guest Account Linking Notice
            if (!uiState.isAccountLinked && !uiState.isPremiumActive) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentGold.copy(alpha = 0.1f))
                        .border(1.dp, AccentGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .clickable { onNavigateToAuth() }
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = AccentGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Account Linking Recommended",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Sign in with Google or Facebook to link and protect your subscription across devices.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Status or Error Notification Banner
            if (uiState.statusMessage != null || uiState.errorMessage != null) {
                val isError = uiState.errorMessage != null
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isError) CoralRed.copy(alpha = 0.15f) else ForestMint.copy(alpha = 0.15f))
                        .border(
                            1.dp,
                            if (isError) CoralRed.copy(alpha = 0.4f) else ForestMint.copy(alpha = 0.4f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isError) Icons.Default.Warning else Icons.Default.Check,
                            contentDescription = null,
                            tint = if (isError) CoralRed else ForestMint,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uiState.errorMessage ?: uiState.statusMessage.orEmpty(),
                            fontSize = 12.sp,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Subscription Plan Selection Cards
            if (!uiState.isPremiumActive) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Monthly Plan
                    PlanSelectionCard(
                        title = "Monthly",
                        offer = uiState.monthlyOffer,
                        periodLabel = "/ month",
                        subtitle = "Flexible monthly pass",
                        isPopular = false,
                        isSelected = uiState.selectedPlanType == SubscriptionPlanType.MONTHLY,
                        onClick = { viewModel.selectPlan(SubscriptionPlanType.MONTHLY) },
                        modifier = Modifier.weight(1f)
                    )

                    // 6-Month Plan
                    PlanSelectionCard(
                        title = "6 Months",
                        offer = uiState.sixMonthsOffer,
                        periodLabel = "/ 6 months",
                        subtitle = "Best value (save 16%)",
                        isPopular = true,
                        isSelected = uiState.selectedPlanType == SubscriptionPlanType.SIX_MONTH,
                        onClick = { viewModel.selectPlan(SubscriptionPlanType.SIX_MONTH) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

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

                    SubscriptionConstants.BENEFIT_DESCRIPTIONS.forEach { benefit ->
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

            Spacer(modifier = Modifier.height(16.dp))

            // Competitive Fairness Guarantee Notice
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundCard)
                    .border(1.dp, ElectricCyan.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Competitive Fairness: Zynpath is 100% fair. Premium never grants hints, extra time, or advantages in Quick Duel, Friend Duel, Mini League, or competitive Daily Challenge.",
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Action Button (Subscribe or Manage)
            val isBusy = uiState.purchaseFlowState == PurchaseFlowState.PURCHASE_IN_PROGRESS ||
                    uiState.purchaseFlowState == PurchaseFlowState.VERIFYING ||
                    uiState.purchaseFlowState == PurchaseFlowState.RESTORING

            if (uiState.isPremiumActive) {
                ZynpathPrimaryButton(
                    text = "Play Premium Puzzle Packs",
                    onClick = onNavigateToPacks,
                    enabled = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { openGooglePlaySubscriptions(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Manage on Google Play", color = TextPrimary)
                }
            } else {
                val selectedOffer = if (uiState.selectedPlanType == SubscriptionPlanType.MONTHLY) {
                    uiState.monthlyOffer
                } else {
                    uiState.sixMonthsOffer
                }

                val planName = if (uiState.selectedPlanType == SubscriptionPlanType.MONTHLY) "Monthly" else "6 Months"
                val buttonText = when {
                    isBusy -> "Processing..."
                    selectedOffer != null && selectedOffer.isAvailable -> "Subscribe (${selectedOffer.formattedPrice})"
                    else -> "Subscribe ($planName)"
                }

                ZynpathPrimaryButton(
                    text = buttonText,
                    onClick = {
                        activity?.let { viewModel.buySelectedPlan(it) }
                    },
                    enabled = !isBusy && uiState.isBillingAvailable
                )
            }

            if (isBusy) {
                Spacer(modifier = Modifier.height(12.dp))
                CircularProgressIndicator(
                    color = AccentGold,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Secondary Actions: Restore Purchases & Manage Subscription
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(
                    onClick = { viewModel.restorePurchases() },
                    enabled = !isBusy
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Restore Purchases", fontSize = 12.sp, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun EntitlementStatusBanner(
    uiState: PremiumUiState,
    onManageSubscription: () -> Unit
) {
    val entitlement = uiState.entitlement
    val status = entitlement.status

    val (bgColor, borderColor, textColor) = when (status) {
        EntitlementStatus.ACTIVE -> Triple(
            AccentGold.copy(alpha = 0.15f),
            AccentGold.copy(alpha = 0.5f),
            AccentGold
        )
        EntitlementStatus.IN_GRACE_PERIOD -> Triple(
            CoralRed.copy(alpha = 0.15f),
            CoralRed.copy(alpha = 0.5f),
            CoralRed
        )
        EntitlementStatus.CANCELLED_BUT_ACTIVE -> Triple(
            ElectricCyan.copy(alpha = 0.15f),
            ElectricCyan.copy(alpha = 0.5f),
            ElectricCyan
        )
        else -> Triple(
            BackgroundElevated,
            BackgroundCard,
            TextSecondary
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Current Status: ${status.displayLabel}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                if (entitlement.currentPeriodEndMs > 0L) {
                    val dateFormatted = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        .format(Date(entitlement.currentPeriodEndMs))
                    Text(
                        text = if (status == EntitlementStatus.ACTIVE) "Renews / Valid until: $dateFormatted" else "Expires: $dateFormatted",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                } else if (status == EntitlementStatus.FREE) {
                    Text(
                        text = "Standard Free Tier",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            if (status.isEntitled) {
                OutlinedButton(
                    onClick = onManageSubscription,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Manage", fontSize = 11.sp, color = TextPrimary)
                }
            }
        }
    }
}

@Composable
private fun PlanSelectionCard(
    title: String,
    offer: SubscriptionOffer?,
    periodLabel: String,
    subtitle: String,
    isPopular: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    val displayPrice = offer?.formattedPrice ?: if (isPopular) SubscriptionConstants.PLANNED_PRICE_SIX_MONTH_INR else SubscriptionConstants.PLANNED_PRICE_MONTHLY_INR

    Box(
        modifier = modifier
            .clip(shape)
            .background(if (isSelected) BackgroundElevated else BackgroundCard)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) AccentGold else BackgroundCard,
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column {
            if (isPopular) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AccentGold.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "BEST VALUE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = displayPrice,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) AccentGold else ForestMint
                )
                Text(
                    text = periodLabel,
                    fontSize = 10.sp,
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

private fun openGooglePlaySubscriptions(context: Context) {
    val targetUrl = SubscriptionConstants.getManageSubscriptionUrl(context.packageName)
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(targetUrl)
        setPackage("com.android.vending")
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to web browser
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(SubscriptionConstants.GOOGLE_PLAY_SUBSCRIPTIONS_OVERVIEW_URL)
        )
        context.startActivity(webIntent)
    }
}

private fun Context.findActivity(): Activity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}
