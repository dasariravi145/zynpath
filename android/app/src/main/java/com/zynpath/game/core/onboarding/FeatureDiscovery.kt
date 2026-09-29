package com.zynpath.game.core.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.components.ZynpathPrimaryButton
import com.zynpath.game.core.designsystem.components.ZynpathSecondaryButton
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.PathCyanGlow
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary
import com.zynpath.game.core.designsystem.theme.WallCrimson

data class FeatureTip(
    val tipId: String,
    val title: String,
    val description: String,
    val badgeText: String? = null,
    val accentColor: Color = ForestMint
)

object FeatureDiscoveryTips {
    val WORLD_2_INTRO = FeatureTip(
        tipId = "world_2_intro",
        title = "World 2: Expanding Grid (5×5)",
        description = "Grid size expands from 4×4 to 5×5! Puzzles require longer continuous routes while still demanding 100% cell coverage.",
        badgeText = "World 2",
        accentColor = ForestMint
    )

    val WORLD_3_WALLS = FeatureTip(
        tipId = "world_3_walls",
        title = "World 3: Wall Obstacles Introduced",
        description = "Crimson walls are now active! Remember: walls are blocked edges between cells, not blocked tiles. You may use cells on both sides, but cannot cross the edge.",
        badgeText = "Walls Active",
        accentColor = WallCrimson
    )

    val WORLD_4_INTRO = FeatureTip(
        tipId = "world_4_intro",
        title = "World 4: 6×6 Labyrinth",
        description = "Welcome to 6×6 grids with up to 8 wall barriers. Look ahead to ensure your path does not trap open corners.",
        badgeText = "World 4",
        accentColor = PathCyanGlow
    )

    val WORLD_5_INTRO = FeatureTip(
        tipId = "world_5_intro",
        title = "World 5: 7×7 Grand Maze",
        description = "Mastery level puzzles with 4 to 12 walls. Carefully plan your route between numbered checkpoints.",
        badgeText = "World 5",
        accentColor = AccentGold
    )

    val WORLD_6_INTRO = FeatureTip(
        tipId = "world_6_intro",
        title = "World 6: 8×8 Zenith",
        description = "The ultimate 64-cell continuous path challenge. 6 to 18 walls with complex multi-room topologies.",
        badgeText = "Zenith",
        accentColor = WallCrimson
    )

    val DAILY_CHALLENGE = FeatureTip(
        tipId = "discovery_daily_challenge",
        title = "Daily Challenge",
        description = "One shared puzzle issued every 24 hours. Solvable offline for personal stats, or submitted online for verified leaderboard standing.",
        badgeText = "Daily",
        accentColor = ForestMint
    )

    val FRIENDS = FeatureTip(
        tipId = "discovery_friends",
        title = "Friends & Social",
        description = "Add friends using your unique Zynpath ID. Send invitations to compare stats and challenge friends in direct 1v1 duels.",
        badgeText = "Social",
        accentColor = PathCyanGlow
    )

    val QUICK_DUEL = FeatureTip(
        tipId = "discovery_quick_duel",
        title = "Quick Duel (1v1)",
        description = "Fast real-time matchmaking. Both players receive the identical puzzle seed. Server-authoritative validation with zero competitive hints.",
        badgeText = "1v1 Matchmaking",
        accentColor = PathCyanGlow
    )

    val FRIEND_DUEL = FeatureTip(
        tipId = "discovery_friend_duel",
        title = "Friend Duel",
        description = "Create a private room code or share an invitation link. Challenge a friend to race through the exact same puzzle.",
        badgeText = "Private Duel",
        accentColor = AccentGold
    )

    val MINI_LEAGUE = FeatureTip(
        tipId = "discovery_mini_league",
        title = "Mini League",
        description = "Multi-round party tournament for 2 to 5 players. Compete over consecutive rounds with cumulative leaderboard standings.",
        badgeText = "Tournament",
        accentColor = AccentGold
    )

    val PREMIUM = FeatureTip(
        tipId = "discovery_premium",
        title = "Zynpath Premium",
        description = "Ad-free experience, unlimited Solo hints, exclusive theme cosmetics, and advanced personal analytics. Note: Premium provides zero competitive advantage in duels.",
        badgeText = "Supporter",
        accentColor = AccentGold
    )

    val REWARDED_HINTS = FeatureTip(
        tipId = "discovery_rewarded_hints",
        title = "Earn Solo Hints",
        description = "Free players can watch an optional rewarded sponsor video to earn additional Solo hints. Completely optional with zero forced ads.",
        badgeText = "Optional",
        accentColor = ForestMint
    )

    fun getWorldIntro(worldId: Int): FeatureTip? {
        return when (worldId) {
            2 -> WORLD_2_INTRO
            3 -> WORLD_3_WALLS
            4 -> WORLD_4_INTRO
            5 -> WORLD_5_INTRO
            6 -> WORLD_6_INTRO
            else -> null
        }
    }
}

@Composable
fun WorldIntroductionDialog(
    tip: FeatureTip,
    onDismiss: () -> Unit,
    onReplayWallTutorial: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = tip.accentColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = tip.title,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column {
                tip.badgeText?.let { badge ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(tip.accentColor.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = tip.accentColor
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = tip.description,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )

                if (onReplayWallTutorial != null && tip.tipId == FeatureDiscoveryTips.WORLD_3_WALLS.tipId) {
                    Spacer(modifier = Modifier.height(14.dp))
                    ZynpathSecondaryButton(
                        text = "Replay Wall Tutorial",
                        onClick = {
                            onDismiss()
                            onReplayWallTutorial()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = ForestMint
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            ZynpathPrimaryButton(
                text = "Got It",
                onClick = onDismiss
            )
        },
        containerColor = BackgroundCard,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun FeatureDiscoveryCard(
    tip: FeatureTip,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BackgroundElevated)
            .border(1.dp, tip.accentColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = tip.accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = tip.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                }

                TextButton(onClick = onDismiss) {
                    Text("Dismiss", color = TextMuted, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = tip.description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 17.sp
            )
        }
    }
}
