package com.zynpath.game.feature.rewards

import android.app.Activity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.R
import com.zynpath.game.core.designsystem.components.glossyTopHighlight
import com.zynpath.game.core.designsystem.feedback.rememberSoundFeedbackManager
import com.zynpath.game.core.designsystem.theme.RefCyanNeon
import com.zynpath.game.core.designsystem.theme.RefGoldPrimary
import com.zynpath.game.core.designsystem.theme.RefGreenClaim
import com.zynpath.game.core.designsystem.theme.RefNavyDark
import com.zynpath.game.core.designsystem.theme.RefNavySurface
import kotlinx.coroutines.delay
import java.text.NumberFormat

/**
 * ZYNPATH Dedicated Daily Rewards Modal.
 * Appears instantly over the active screen with a dark translucent scrim.
 *
 * Dedicated strictly to collecting rewards (not playing puzzles):
 * 1. Header: DAILY REWARDS
 * 2. Current coin balance
 * 3. Daily Bonus +50 Coins
 * 4. CLAIM button or CLAIMED TODAY status
 * 5. WATCH AD +50 COINS button (or CLAIMED TODAY status)
 * 6. Close (X)
 */
@Composable
fun FreeRewardsDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onPlayDailyChallenge: (() -> Unit)? = null,
    onNavigateToCosmetics: (() -> Unit)? = null,
    viewModel: RewardsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val soundManager = rememberSoundFeedbackManager(isSfxEnabled = true)
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    LaunchedEffect(uiState.rewardAnimationCoins) {
        if (uiState.rewardAnimationCoins > 0) {
            soundManager.playCoinCollected()
            delay(1200)
            viewModel.clearAnimationCoins()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        // Scrim background (#CC040C1E - 80% opacity dark navy)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xCC040C1E))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            // Modal Card Content
            Box(
                modifier = modifier
                    .fillMaxWidth(0.90f)
                    .widthIn(max = 380.dp)
                    .padding(vertical = 16.dp)
                    .shadow(20.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(RefNavyDark, RefNavySurface)
                        )
                    )
                    .border(
                        1.5.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                RefCyanNeon.copy(alpha = 0.8f),
                                RefGoldPrimary.copy(alpha = 0.9f)
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .glossyTopHighlight(cornerRadius = 24.dp, highlightAlpha = 0.16f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* prevent dismissal on clicking inside */ }
                    )
                    .padding(horizontal = 18.dp, vertical = 18.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. Header with Title, Live Coin Balance & Close Button
                    RewardsHeader(
                        walletBalance = uiState.walletBalance,
                        onDismiss = onDismiss
                    )

                    // 2. Section 1: Daily Bonus (+50 Coins)
                    DailyBonusCard(
                        isClaimed = uiState.isDailyClaimed,
                        isClaiming = uiState.isClaimingDaily,
                        rewardAmount = uiState.dailyRewardAmount,
                        onClaim = { viewModel.claimDailyLoginReward() }
                    )

                    HorizontalDivider(
                        color = Color(0x3321D4FD),
                        thickness = 1.dp
                    )

                    // 3. Section 2: Rewarded Video Ad (+50 Coins)
                    DailyAdBonusCard(
                        isClaimed = uiState.isDailyAdClaimed,
                        isWatching = uiState.isWatchingDailyAd,
                        rewardAmount = uiState.dailyAdRewardAmount,
                        onWatchAd = {
                            if (activity != null) {
                                viewModel.watchDailyAd(activity)
                            }
                        }
                    )

                    // Snackbar Host inside the card
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * Header row displaying title, current live coin balance, and close button.
 */
@Composable
private fun RewardsHeader(
    walletBalance: Int,
    onDismiss: () -> Unit
) {
    val animatedBalance = remember { Animatable(walletBalance.toFloat()) }
    LaunchedEffect(walletBalance) {
        animatedBalance.animateTo(
            targetValue = walletBalance.toFloat(),
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Title: DAILY REWARDS
        Text(
            text = "DAILY REWARDS",
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            color = RefGoldPrimary,
            letterSpacing = 1.sp
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Live Wallet Balance Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0C1935))
                    .border(1.dp, RefGoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_game_coin),
                        contentDescription = "Coins",
                        tint = RefGoldPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = NumberFormat.getIntegerInstance().format(animatedBalance.value.toInt()),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Close ✕ button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF09172A))
                    .border(1.dp, RefCyanNeon.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

/**
 * Daily Login Bonus Card showing reward amount and Claim button.
 */
@Composable
private fun DailyBonusCard(
    isClaimed: Boolean,
    isClaiming: Boolean,
    rewardAmount: Int,
    onClaim: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0B1736))
            .border(1.dp, Color(0xFF1E3A8A), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_game_coin),
                    contentDescription = null,
                    tint = RefGoldPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Daily Bonus",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "+$rewardAmount Coins",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RefGoldPrimary
                )
            }

            // CLAIM BUTTON / CLAIMED TODAY
            if (isClaimed) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0E232B))
                        .border(1.dp, RefGreenClaim.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = RefGreenClaim,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "CLAIMED TODAY",
                            color = RefGreenClaim,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFFB800), Color(0xFFFF8C00))
                            )
                        )
                        .clickable(enabled = !isClaiming, onClick = onClaim),
                    contentAlignment = Alignment.Center
                ) {
                    if (isClaiming) {
                        CircularProgressIndicator(
                            color = Color(0xFF0A1428),
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text(
                            text = "CLAIM",
                            color = Color(0xFF0A1428),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Rewarded Video Ad Card showing reward amount and WATCH AD button.
 */
@Composable
private fun DailyAdBonusCard(
    isClaimed: Boolean,
    isWatching: Boolean,
    rewardAmount: Int,
    onWatchAd: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0B1736))
            .border(1.dp, Color(0xFF1E3A8A), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_game_coin),
                    contentDescription = null,
                    tint = RefGoldPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Bonus Video Ad",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "+$rewardAmount Coins",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RefGoldPrimary
                )
            }

            // WATCH AD BUTTON / CLAIMED TODAY
            if (isClaimed) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0E232B))
                        .border(1.dp, RefGreenClaim.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = RefGreenClaim,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "CLAIMED TODAY",
                            color = RefGreenClaim,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFFB800), Color(0xFFFF8C00))
                            )
                        )
                        .clickable(enabled = !isWatching, onClick = onWatchAd),
                    contentAlignment = Alignment.Center
                ) {
                    if (isWatching) {
                        CircularProgressIndicator(
                            color = Color(0xFF0A1428),
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color(0xFF0A1428),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "WATCH AD +$rewardAmount COINS",
                                color = Color(0xFF0A1428),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
