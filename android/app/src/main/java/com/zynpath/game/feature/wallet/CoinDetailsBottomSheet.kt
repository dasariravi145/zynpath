package com.zynpath.game.feature.wallet

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.R
import com.zynpath.game.core.ads.CoinRewardedAdManager
import com.zynpath.game.core.database.entity.WalletTransactionEntity
import com.zynpath.game.core.designsystem.components.ZynpathMasterPillButton
import com.zynpath.game.core.designsystem.components.ZynpathPillStyle
import com.zynpath.game.core.designsystem.theme.RefCyanNeon
import com.zynpath.game.core.designsystem.theme.RefGoldBorder
import com.zynpath.game.core.designsystem.theme.RefGoldGradientEnd
import com.zynpath.game.core.designsystem.theme.RefGoldGradientStart
import com.zynpath.game.core.designsystem.theme.RefGoldPrimary
import com.zynpath.game.core.designsystem.theme.RefGreenClaim
import com.zynpath.game.core.designsystem.theme.RefNavyBorder
import com.zynpath.game.core.designsystem.theme.RefNavyDark
import com.zynpath.game.core.designsystem.theme.RefNavySurface
import com.zynpath.game.core.designsystem.theme.RefRedBadge
import com.zynpath.game.core.designsystem.theme.RefTextSecondary
import com.zynpath.game.core.economy.WalletRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Reference-derived Coin Details / Wallet Bottom Sheet.
 * Inherits visual styling directly from the approved Rewards screen (Panel 07):
 * Midnight navy base, gold metallic accents, green Claim CTA, and electric cyan highlights.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinDetailsBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    currentBalance: Int,
    isDailyClaimed: Boolean,
    isDailyAdBonusClaimed: Boolean,
    remainingCoinAds: Int,
    recentTransactions: List<WalletTransactionEntity>,
    walletRepository: WalletRepository,
    coinRewardedAdManager: CoinRewardedAdManager,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as? Activity
    val todayUtc = LocalDate.now(ZoneOffset.UTC).toString()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = RefNavyDark,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .size(width = 38.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF2A4365))
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "COIN VAULT",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RefGoldPrimary,
                    letterSpacing = 1.sp
                )
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = RefTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Current Balance Hero Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0D254C), RefNavySurface)
                        )
                    )
                    .border(1.2.dp, RefNavyBorder, RoundedCornerShape(16.dp))
                    .padding(vertical = 16.dp, horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_game_coin),
                        contentDescription = "Coin",
                        modifier = Modifier.size(44.dp)
                    )
                    Column {
                        Text(
                            text = "AVAILABLE BALANCE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RefTextSecondary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "%,d Coins".format(currentBalance),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Daily Free & Ad Bonus Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "DAILY EARNINGS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = RefTextSecondary,
                    letterSpacing = 1.sp
                )

                // 1. Daily Free Login Claim (+20 Coins)
                DailyRewardActionRow(
                    title = "Daily Free Gift",
                    amount = "+20",
                    isClaimed = isDailyClaimed,
                    buttonText = if (isDailyClaimed) "CLAIMED" else "CLAIM",
                    style = ZynpathPillStyle.GREEN,
                    onClick = {
                        coroutineScope.launch {
                            walletRepository.claimDailyLoginReward(todayUtc)
                        }
                    }
                )

                // 2. Daily Login Ad Bonus (+20 Coins)
                DailyRewardActionRow(
                    title = "Daily Video Bonus",
                    amount = "+20",
                    isClaimed = isDailyAdBonusClaimed,
                    buttonText = if (isDailyAdBonusClaimed) "CLAIMED" else "WATCH AD",
                    style = ZynpathPillStyle.GOLD,
                    onClick = {
                        activity?.let { act ->
                            coinRewardedAdManager.showCoinRewardedAd(
                                activity = act,
                                onRewarded = {
                                    coroutineScope.launch {
                                        walletRepository.claimDailyLoginAdBonus(todayUtc)
                                    }
                                },
                                onFailed = {}
                            )
                        }
                    }
                )

                // 3. Rewarded Video Ad Claim (+15 Coins, max 2/day)
                val adsExhausted = remainingCoinAds <= 0
                DailyRewardActionRow(
                    title = "Bonus Video Ad ($remainingCoinAds/2 left)",
                    amount = "+15",
                    isClaimed = adsExhausted,
                    buttonText = if (adsExhausted) "LIMIT REACHED" else "WATCH AD",
                    style = ZynpathPillStyle.GOLD,
                    onClick = {
                        activity?.let { act ->
                            coinRewardedAdManager.showCoinRewardedAd(
                                activity = act,
                                onRewarded = {
                                    coroutineScope.launch {
                                        walletRepository.claimCoinAdReward(todayUtc)
                                    }
                                },
                                onFailed = {}
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Recent Ledger Transactions
            Text(
                text = "RECENT TRANSACTIONS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = RefTextSecondary,
                letterSpacing = 1.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (recentTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No recent transactions yet.",
                        fontSize = 13.sp,
                        color = RefTextSecondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recentTransactions) { tx ->
                        TransactionRowItem(tx = tx)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DailyRewardActionRow(
    title: String,
    amount: String,
    isClaimed: Boolean,
    buttonText: String,
    style: ZynpathPillStyle,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(RefNavySurface)
            .border(1.dp, RefNavyBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = "$amount Coins",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = RefGoldPrimary
                )
            }

            ZynpathMasterPillButton(
                text = buttonText,
                onClick = onClick,
                style = if (isClaimed) ZynpathPillStyle.SILVER else style,
                enabled = !isClaimed,
                height = 36.dp,
                modifier = Modifier.width(116.dp)
            )
        }
    }
}

@Composable
private fun TransactionRowItem(tx: WalletTransactionEntity) {
    val isCredit = tx.amount > 0
    val formattedTime = formatTimestamp(tx.createdAt)

    val displayTitle = when (tx.type) {
        "WELCOME_REWARD" -> "Welcome Gift"
        "SOLO_ENTRY" -> "Reward Run Entry"
        "SOLO_FIRST_CLEAR" -> "Level First Clear"
        "DAILY_LOGIN" -> "Daily Login Reward"
        "DAILY_AD_BONUS" -> "Daily Ad Bonus"
        "COIN_AD_REWARD" -> "Video Ad Reward"
        "DAILY_CHALLENGE" -> "Daily Challenge Clear"
        "DAILY_CHALLENGE_AD_BONUS" -> "Daily Challenge Bonus"
        "WORLD_COMPLETION" -> "World Milestone Clear"
        "WORLD_AD_BONUS" -> "World Bonus Reward"
        "DUEL_ENTRY" -> "Quick Duel Entry"
        "DUEL_SETTLEMENT" -> "Quick Duel Victory"
        "ARENA_ENTRY" -> "Friends Arena Entry"
        "ARENA_SETTLEMENT" -> "Friends Arena Reward"
        "MATCH_REFUND" -> "Match Entry Refund"
        else -> tx.type
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF091426))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = displayTitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Text(
                text = formattedTime,
                fontSize = 10.sp,
                color = Color(0xFF64748B)
            )
        }

        Text(
            text = if (isCredit) "+${tx.amount}" else "${tx.amount}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isCredit) Color(0xFF10B981) else Color(0xFFFF5252)
        )
    }
}

private fun formatTimestamp(timestampMs: Long): String {
    return try {
        val dt = Instant.ofEpochMilli(timestampMs)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        dt.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"))
    } catch (e: Exception) {
        ""
    }
}
