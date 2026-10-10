package com.zynpath.game.feature.multiplayer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.R
import com.zynpath.game.core.designsystem.components.GamePrimaryButton
import com.zynpath.game.core.designsystem.components.GameSecondaryButton
import com.zynpath.game.core.designsystem.components.glossyTopHighlight
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameSuccessGreen
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.core.multiplayer.model.ClientMatchState
import com.zynpath.game.feature.home.components.resolveAvatarDrawable
import kotlinx.coroutines.delay

/**
 * Compact, premium game-style Quick Duel matchmaking popup.
 *
 * Displayed directly over the Home screen:
 * - Dims the underlying Home screen without full-screen navigation.
 * - Displays FINDING RIVAL with animated indicator and avatars.
 * - Transitions to RIVAL FOUND state upon server confirmation.
 * - Displays synchronized 3-2-1-GO countdown in the same popup.
 * - Dismisses and triggers gameplay navigation only after server confirms ACTIVE state.
 * - Cleanly handles CANCEL and hardware back button dismissal.
 */
@Composable
fun QuickDuelMatchmakingModal(
    onDismiss: () -> Unit,
    onMatchConfirmed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QuickDuelViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Start matchmaking immediately when popup is opened in IDLE state
    LaunchedEffect(Unit) {
        if (state.clientMatchState == ClientMatchState.IDLE) {
            viewModel.startSearching()
        }
    }

    // Dismiss popup and navigate to gameplay when match start is confirmed by server
    LaunchedEffect(state.clientMatchState) {
        if (state.isActiveGameplay) {
            onMatchConfirmed()
        }
    }

    // Handle back button: safely cancel matchmaking and dismiss popup
    BackHandler {
        viewModel.cancelMatchmakingFlow()
        onDismiss()
    }

    // Full-screen overlay dimming the Home screen
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xCC040C1E))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Tapping outside cancels if searching or in error
                if (state.isSearching || state.isError || state.isCancelled) {
                    viewModel.cancelMatchmakingFlow()
                    onDismiss()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Centered compact modal card
        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = 380.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xF80A1635),
                            GameMidnightBlue
                        )
                    )
                )
                .border(
                    BorderStroke(
                        1.4.dp,
                        Brush.horizontalGradient(
                            listOf(
                                GameElectricCyan.copy(alpha = 0.85f),
                                GameGoldHighlight.copy(alpha = 0.7f),
                                GameElectricCyan.copy(alpha = 0.85f)
                            )
                        )
                    ),
                    RoundedCornerShape(22.dp)
                )
                .glossyTopHighlight(cornerRadius = 22.dp, highlightAlpha = 0.16f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    // Prevent click-through to scrim
                }
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            when {
                // Error or Timeout State
                state.isError || state.isCancelled -> {
                    QuickDuelModalErrorContent(
                        errorMessage = state.errorMessage ?: "Matchmaking was cancelled.",
                        onRetry = { viewModel.retrySearching() },
                        onCancel = {
                            viewModel.cancelMatchmakingFlow()
                            onDismiss()
                        }
                    )
                }

                // Countdown State (3, 2, 1, GO!)
                state.isCountdown -> {
                    QuickDuelModalCountdownContent(
                        seconds = state.countdownSeconds ?: 3
                    )
                }

                // Rival Found State (VS presentation)
                state.isMatchFound -> {
                    QuickDuelModalRivalFoundContent(
                        state = state,
                        onAutoReady = { viewModel.markReady() },
                        onCancel = {
                            viewModel.cancelMatchmakingFlow()
                            onDismiss()
                        }
                    )
                }

                // Default: Finding Rival State
                else -> {
                    QuickDuelModalFindingRivalContent(
                        state = state,
                        onCancel = {
                            viewModel.cancelMatchmakingFlow()
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

/**
 * 1. Finding Rival Content:
 * - FINDING RIVAL... header
 * - Small animated matchmaking indicator
 * - Player avatar and name
 * - Searching rival avatar
 * - CANCEL button
 */
@Composable
private fun QuickDuelModalFindingRivalContent(
    state: QuickDuelUiState,
    onCancel: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "portalPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val localName = state.authSession?.displayName ?: "Guest"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "FINDING RIVAL...",
                style = GameTypography.screenHeading.copy(
                    fontSize = 18.sp,
                    letterSpacing = 1.8.sp
                ),
                fontWeight = FontWeight.Black,
                color = GameElectricCyan,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "11×11 / 12×12 • Expert Puzzle",
                style = GameTypography.labelMedium.copy(
                    fontSize = 12.sp
                ),
                color = GameGoldHighlight,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }

        // Avatars & Indicator Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Local Player
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .scale(pulseScale)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    GameElectricCyan.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                        .border(BorderStroke(2.dp, GameElectricCyan), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = resolveAvatarDrawable(state.authSession?.playerId ?: "you")),
                        contentDescription = "Your Avatar",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = localName,
                    style = GameTypography.bodyEmphasized.copy(fontSize = 13.sp),
                    fontWeight = FontWeight.Bold,
                    color = GameWhite,
                    maxLines = 1
                )
                Text(
                    text = "YOU",
                    style = GameTypography.badgeText.copy(fontSize = 10.sp),
                    color = GameElectricCyan,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // Central Animated Matchmaking Indicator
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(GameMidnightBlue.copy(alpha = 0.9f), CircleShape)
                    .border(BorderStroke(1.2.dp, GameGoldHighlight.copy(alpha = 0.6f)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = GameElectricCyan,
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 2.5.dp
                )
            }

            // Searching Rival
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .scale(pulseScale)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    GameGold.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                        .border(
                            BorderStroke(
                                2.dp,
                                Brush.sweepGradient(listOf(GameGold, GameRoyalBlue, GameGold))
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = GameGold.copy(alpha = 0.45f),
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Searching...",
                    style = GameTypography.bodyMedium.copy(fontSize = 13.sp),
                    fontWeight = FontWeight.Medium,
                    color = GameSecondaryText,
                    maxLines = 1
                )
                Text(
                    text = "RIVAL",
                    style = GameTypography.badgeText.copy(fontSize = 10.sp),
                    color = GameGoldHighlight,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Cancel Button
        GameSecondaryButton(
            text = "CANCEL",
            onClick = onCancel,
            modifier = Modifier
                .fillMaxWidth(0.68f)
                .height(44.dp)
        )
    }
}

/**
 * 2. Rival Found Content:
 * - RIVAL FOUND!
 * - [Player Avatar]  VS  [Rival Avatar]
 * - [Player Name]       [Rival Name]
 * - Short premium transition before countdown
 */
@Composable
private fun QuickDuelModalRivalFoundContent(
    state: QuickDuelUiState,
    onAutoReady: () -> Unit,
    onCancel: () -> Unit
) {
    LaunchedEffect(state.currentSession?.matchId) {
        delay(1200L)
        onAutoReady()
    }

    val local = state.localParticipant
    val opponent = state.opponentParticipant

    val infiniteTransition = rememberInfiniteTransition(label = "vsPulse")
    val vsScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "vsScale"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "RIVAL FOUND!",
                style = GameTypography.screenHeading.copy(
                    fontSize = 20.sp,
                    letterSpacing = 2.sp
                ),
                fontWeight = FontWeight.Black,
                color = GameSuccessGreen,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "11×11 / 12×12 • Expert Puzzle",
                style = GameTypography.labelMedium.copy(fontSize = 12.sp),
                color = GameGoldHighlight,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }

        // [Player] VS [Rival]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Local Player
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    GameElectricCyan.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                        .border(BorderStroke(2.dp, GameElectricCyan), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = resolveAvatarDrawable(local?.playerId ?: "you")),
                        contentDescription = "Your Avatar",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = local?.displayName ?: "You",
                    style = GameTypography.bodyEmphasized.copy(fontSize = 13.sp),
                    fontWeight = FontWeight.Bold,
                    color = GameWhite,
                    maxLines = 1
                )
                Text(
                    text = "YOU",
                    style = GameTypography.badgeText.copy(fontSize = 10.sp),
                    color = GameElectricCyan,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // VS Emblem
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .scale(vsScale)
                    .background(
                        Brush.verticalGradient(
                            listOf(GameMidnightBlue, Color(0xFF070E1A))
                        ),
                        CircleShape
                    )
                    .border(BorderStroke(1.5.dp, GameGoldHighlight), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "VS",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = GameGoldHighlight
                )
            }

            // Rival Player
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    GameGoldHighlight.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                        .border(BorderStroke(2.dp, GameGoldHighlight), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = resolveAvatarDrawable(opponent?.playerId ?: "avatar_priya")),
                        contentDescription = "Rival Avatar",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = opponent?.displayName ?: "Rival",
                    style = GameTypography.bodyEmphasized.copy(fontSize = 13.sp),
                    fontWeight = FontWeight.Bold,
                    color = GameWhite,
                    maxLines = 1
                )
                Text(
                    text = "RIVAL",
                    style = GameTypography.badgeText.copy(fontSize = 10.sp),
                    color = GameGoldHighlight,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Subtitle status
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = GameElectricCyan,
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Preparing puzzle race...",
                style = GameTypography.secondaryInfo.copy(fontSize = 12.sp),
                color = GameSecondaryText
            )
        }

        // Cancel Button
        GameSecondaryButton(
            text = "CANCEL",
            onClick = onCancel,
            modifier = Modifier
                .fillMaxWidth(0.68f)
                .height(44.dp)
        )
    }
}

/**
 * 3. Countdown Content:
 * Displays 3, 2, 1, GO! in the same popup with smooth animation.
 */
@Composable
private fun QuickDuelModalCountdownContent(seconds: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "countdownPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "countdownScale"
    )

    val displayText = if (seconds > 0) "$seconds" else "GO!"
    val displayColor = if (seconds > 0) GameGoldHighlight else GameSuccessGreen

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "GET READY",
            style = GameTypography.screenHeading.copy(
                fontSize = 18.sp,
                letterSpacing = 2.sp
            ),
            fontWeight = FontWeight.Black,
            color = GameElectricCyan,
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .size(100.dp)
                .scale(pulseScale)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            displayColor.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
                .border(BorderStroke(2.dp, displayColor.copy(alpha = 0.7f)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayText,
                fontSize = 46.sp,
                fontWeight = FontWeight.Black,
                color = displayColor,
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = "Starting race...",
            style = GameTypography.secondaryInfo.copy(fontSize = 12.sp),
            color = GameSecondaryText,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 4. Error and Timeout Content:
 * Shows actionable error with Retry and Cancel options.
 */
@Composable
private fun QuickDuelModalErrorContent(
    errorMessage: String,
    onRetry: () -> Unit,
    onCancel: () -> Unit
) {
    val isTimeout = errorMessage.contains("timeout", ignoreCase = true) ||
            errorMessage.contains("no rival", ignoreCase = true) ||
            errorMessage.contains("no opponent", ignoreCase = true) ||
            errorMessage.contains("timed out", ignoreCase = true)

    val title = if (isTimeout) "NO RIVAL FOUND" else "MATCH NOTICE"
    val displayDesc = if (isTimeout) {
        "No rival was found within the search window. Try searching again?"
    } else {
        errorMessage
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0x26EF4444))
                .border(1.5.dp, Color(0xFFEF4444), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(26.dp)
            )
        }

        Text(
            text = title,
            style = GameTypography.screenHeading.copy(fontSize = 17.sp),
            fontWeight = FontWeight.Bold,
            color = GameWhite,
            textAlign = TextAlign.Center
        )

        Text(
            text = displayDesc,
            style = GameTypography.bodyMedium.copy(fontSize = 13.sp),
            color = GameSecondaryText,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GameSecondaryButton(
                text = "CANCEL",
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
            )

            GamePrimaryButton(
                text = "RETRY",
                onClick = onRetry,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
            )
        }
    }
}
