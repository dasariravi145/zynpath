package com.zynpath.game.feature.multiplayer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.components.GamePrimaryButton
import com.zynpath.game.core.designsystem.components.glossyTopHighlight
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.core.designsystem.theme.RefCyanNeon
import com.zynpath.game.core.designsystem.theme.RefNavyBorder
import com.zynpath.game.core.designsystem.theme.RefNavyDark
import com.zynpath.game.core.designsystem.theme.RefNavySurface

/**
 * Compact Create Room Modal for Zynpath Friends Arena.
 *
 * Replaces the full-screen Create Game Room page with a compact, focused popup dialog:
 * - Scrim dimming over underlying screen.
 * - Hardware / gesture back dismissal.
 * - Player count selector: 1–5 total participants including the host.
 * - Invite Friends action opening compact friend picker.
 * - Puzzle display: Solver-verified competitive pool (11×11 & 12×12 only).
 * - CREATE ROOM primary action with debounce protection.
 * - "Have a code? Join Room" shortcut.
 */
@Composable
fun CreateRoomModal(
    onDismiss: () -> Unit,
    selectedPlayerCount: Int,
    onPlayerCountSelected: (Int) -> Unit,
    invitedFriendsCount: Int,
    onInviteFriendsClick: () -> Unit,
    onCreateRoom: () -> Unit,
    onJoinRoomClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCreating: Boolean = false,
    errorMessage: String? = null,
    puzzleDescription: String = "Random Expert"
) {
    // Intercept back button to dismiss modal
    BackHandler {
        if (!isCreating) onDismiss()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xCC040C1E))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!isCreating) onDismiss()
            },
        contentAlignment = Alignment.Center
    ) {
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
                                GameElectricCyan.copy(alpha = 0.8f),
                                GameGoldHighlight.copy(alpha = 0.6f),
                                GameElectricCyan.copy(alpha = 0.8f)
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
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header: Title and X Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CREATE ROOM",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        letterSpacing = 1.sp,
                        color = GameWhite
                    )

                    IconButton(
                        onClick = onDismiss,
                        enabled = !isCreating,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = GameSecondaryText,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // 1. Players Section (including you)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Players (including you)",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = GameWhite
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        (1..5).forEach { count ->
                            val isSelected = count == selectedPlayerCount
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) RefCyanNeon
                                        else RefNavySurface
                                    )
                                    .border(
                                        1.2.dp,
                                        if (isSelected) RefCyanNeon
                                        else RefNavyBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable(enabled = !isCreating) {
                                        onPlayerCountSelected(count)
                                    },
                                    contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$count",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) RefNavyDark else GameWhite
                                )
                            }
                        }
                    }

                    // Explanatory note about capacity
                    Text(
                        text = if (selectedPlayerCount == 1) {
                            "1 player: Wait alone in lobby (match requires 2+)"
                        } else {
                            "Match starts when 2–$selectedPlayerCount players join"
                        },
                        fontSize = 11.sp,
                        color = GameSecondaryText
                    )
                }

                // 2. Invite Friends Row
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(RefNavySurface)
                        .border(1.dp, RefNavyBorder, RoundedCornerShape(14.dp))
                        .clickable(enabled = !isCreating) { onInviteFriendsClick() }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Invite Friends",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GameWhite
                            )
                            Text(
                                text = if (invitedFriendsCount > 0) {
                                    "$invitedFriendsCount friend${if (invitedFriendsCount > 1) "s" else ""} selected"
                                } else {
                                    "Invite connected friends"
                                },
                                fontSize = 11.sp,
                                color = if (invitedFriendsCount > 0) GameElectricCyan else GameSecondaryText
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(GameRoyalBlue.copy(alpha = 0.5f))
                                .border(1.dp, GameElectricCyan.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Friends",
                                tint = GameElectricCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // 3. Puzzle Selection Row
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(RefNavySurface)
                        .border(1.dp, RefNavyBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Puzzle: $puzzleDescription",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GameWhite
                            )
                            Text(
                                text = "11×11 & 12×12 competitive pool",
                                fontSize = 11.sp,
                                color = GameSecondaryText
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = GameElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Error message if room creation failed
                if (!errorMessage.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x33FF5252))
                            .border(1.dp, Color(0x66FF5252), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFF8A80),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // 4. Primary Button: CREATE ROOM
                GamePrimaryButton(
                    text = if (isCreating) "CREATING ROOM..." else "CREATE ROOM",
                    onClick = onCreateRoom,
                    enabled = !isCreating,
                    height = 48.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                // 5. Join Room Link
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isCreating) { onJoinRoomClick() }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Have a code? ",
                        fontSize = 13.sp,
                        color = GameSecondaryText
                    )
                    Text(
                        text = "Join Room",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GameElectricCyan
                    )
                }
            }
        }
    }
}
