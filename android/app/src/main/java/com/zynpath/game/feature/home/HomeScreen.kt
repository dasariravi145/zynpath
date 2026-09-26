package com.zynpath.game.feature.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.core.designsystem.components.FeatureCard
import com.zynpath.game.core.designsystem.components.PlayerAvatarBadge
import com.zynpath.game.core.designsystem.components.StatusBadge
import com.zynpath.game.core.designsystem.components.ZynpathPrimaryButton
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.AccentPurple
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.PathCyanGlow
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary

@Composable
fun HomeScreen(
    onNavigateToSoloPlay: () -> Unit,
    onNavigateToLevels: () -> Unit,
    onNavigateToQuickDuel: () -> Unit,
    onNavigateToFriendDuel: () -> Unit,
    onNavigateToMiniLeague: () -> Unit,
    onNavigateToDailyChallenge: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Header: Branding, Guest Profile, and Settings Shortcut
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayerAvatarBadge(
                displayName = "Guest Player",
                playerTag = uiState.guestTag,
                isGuest = true,
                onClick = onNavigateToProfile
            )

            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundCard)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hero Brand Title & Offline Ready Indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Zynpath",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "One path. Every number.",
                    fontSize = 13.sp,
                    color = ForestMint,
                    fontWeight = FontWeight.Medium
                )
            }

            StatusBadge(text = "Offline Solo Ready", color = ForestMint)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // PRIMARY ACTION: Solo Play Hero Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(BackgroundElevated)
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SOLO CAMPAIGN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PathCyanGlow,
                        letterSpacing = 1.sp
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Stars",
                            tint = AccentGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${uiState.totalStars} Stars",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Continuous Number Path",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "World 1 to 6 • 300 base logic puzzles without ads or timers.",
                    fontSize = 13.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                )

                ZynpathPrimaryButton(
                    text = "Play Solo",
                    onClick = onNavigateToSoloPlay,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = BackgroundDark
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Game Modes Section
        Text(
            text = "Game Modes",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Daily Challenge Card
        FeatureCard(
            title = "Daily Challenge",
            description = "A shared puzzle every 24 hours. Solve locally or sync rankings.",
            icon = Icons.Default.CalendarToday,
            onClick = onNavigateToDailyChallenge,
            accentColor = ForestMint,
            badgeText = "Phase 4"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Duel Card
        FeatureCard(
            title = "Quick Duel (1v1)",
            description = "Real-time matchmaking. Both players receive the same puzzle seed.",
            icon = Icons.Default.Timer,
            onClick = onNavigateToQuickDuel,
            accentColor = PathCyanGlow,
            badgeText = "Phase 7"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Friend Duel Card
        FeatureCard(
            title = "Friend Duel",
            description = "Invite a friend via private room code or direct shareable link.",
            icon = Icons.Default.Group,
            onClick = onNavigateToFriendDuel,
            accentColor = AccentPurple,
            badgeText = "Phase 7"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Mini League Card
        FeatureCard(
            title = "Mini League",
            description = "Multi-round party tournament for 2–5 players with cumulative standings.",
            icon = Icons.Default.WorkspacePremium,
            onClick = onNavigateToMiniLeague,
            accentColor = AccentGold,
            badgeText = "Phase 7"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Navigation Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BackgroundCard)
                    .padding(14.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = onNavigateToLevels) {
                        Icon(
                            imageVector = Icons.Outlined.List,
                            contentDescription = "Levels",
                            tint = ForestMint
                        )
                    }
                    Text("Levels", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BackgroundCard)
                    .padding(14.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = onNavigateToPremium) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "Premium",
                            tint = AccentGold
                        )
                    }
                    Text("Premium", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
