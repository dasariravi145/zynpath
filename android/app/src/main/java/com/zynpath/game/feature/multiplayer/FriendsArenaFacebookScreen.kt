package com.zynpath.game.feature.multiplayer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.core.designsystem.components.GamePanel
import com.zynpath.game.core.designsystem.components.ZynpathMasterHeaderBar
import com.zynpath.game.core.designsystem.components.ZynpathMasterPillButton
import com.zynpath.game.core.designsystem.components.ZynpathPillStyle
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.core.designsystem.theme.RefCyanNeon
import com.zynpath.game.core.designsystem.theme.RefNavyBorder
import com.zynpath.game.core.designsystem.theme.RefNavyDark
import com.zynpath.game.core.designsystem.theme.RefNavySurface
import com.zynpath.game.core.social.model.InvitationLinkHelper

/**
 * Friends Arena Room Invitation Screen.
 *
 * Implements:
 * - Provider-neutral room invitation dispatch.
 * - Displays active room code with copy and share sheet actions.
 * - Displays real friends from SocialRepository.
 * - Clean empty state when no friends are connected yet (NO FRIENDS YET + INVITE FRIEND).
 * - Enforces room capacity limits (up to 4 additional guests for a 5-player room).
 * - Eliminates fake/mock friend data and Facebook platform dependencies.
 */
@Composable
fun FriendsArenaFacebookScreen(
    onBackClick: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onNavigateToRoomLobby: (String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FriendsArenaFacebookViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Alert Notice Dialog
    if (uiState.userNoticeTitle != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearNotice() },
            title = {
                Text(
                    text = uiState.userNoticeTitle ?: "Notice",
                    style = GameTypography.screenHeading.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GameWhite
                    )
                )
            },
            text = {
                Text(
                    text = uiState.userNoticeDetails ?: "",
                    style = GameTypography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = GameSecondaryText,
                        lineHeight = 20.sp
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.clearNotice() }) {
                    Text(
                        text = "UNDERSTOOD",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        color = GameGoldHighlight
                    )
                }
            },
            containerColor = GameMidnightBlue,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.border(1.dp, GameRoyalBlue.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
        )
    }

    Scaffold(
        containerColor = RefNavyDark,
        topBar = {
            ZynpathMasterHeaderBar(
                title = "Invite Friends",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            if (uiState.filteredFriends.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .navigationBarsPadding()
                ) {
                    ZynpathMasterPillButton(
                        text = "Send Invitation (${uiState.selectedCount})",
                        style = ZynpathPillStyle.GOLD,
                        height = 52.dp,
                        fontSize = 18,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState.canSubmitInvitations,
                        onClick = {
                            viewModel.onSendInvitations()
                            onNavigateToRoomLobby(uiState.roomId)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Room Code & Capacity Card
            val code = uiState.roomCode ?: uiState.roomId
            if (!code.isNullOrBlank()) {
                GamePanel(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = 14.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ROOM CODE",
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp,
                                color = GameElectricCyan
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = code.uppercase(),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                letterSpacing = 2.sp,
                                color = GameGoldHighlight
                            )
                            Text(
                                text = "${uiState.availableCapacity} invitation slots available",
                                fontSize = 10.sp,
                                color = GameSecondaryText
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Copy button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x3300E5FF))
                                    .border(1.dp, RefCyanNeon.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Zynpath Room Code", code))
                                        Toast.makeText(context, "Room code copied!", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = RefCyanNeon,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "COPY",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = RefCyanNeon
                                    )
                                }
                            }

                            // Share button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x33FFB300))
                                    .border(1.dp, GameGoldHighlight.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        val publicId = uiState.playerProfile?.publicZynpathId ?: "HOST"
                                        val shareIntent = InvitationLinkHelper.createShareIntent(
                                            context = context,
                                            publicZynpathId = publicId,
                                            displayName = uiState.playerProfile?.displayName ?: "Player"
                                        )
                                        context.startActivity(shareIntent)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = GameGoldHighlight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "SHARE",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = GameGoldHighlight
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Search Bar Capsule
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(RefNavySurface)
                    .border(1.dp, RefNavyBorder, RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF8E9BB0),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    if (uiState.searchQuery.isEmpty()) {
                        Text(
                            text = "Search Friends...",
                            color = Color(0xFF8E9BB0),
                            fontSize = 14.sp
                        )
                    }
                    BasicTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        singleLine = true,
                        cursorBrush = SolidColor(RefCyanNeon),
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Friend List or Clean Empty State
            if (uiState.filteredFriends.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(GameRoyalBlue.copy(alpha = 0.25f))
                                .border(1.dp, RefCyanNeon.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                tint = RefCyanNeon,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "NO FRIENDS YET",
                            style = GameTypography.screenHeading.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = GameWhite
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Invite a friend to play Zynpath and join your arena room.",
                            style = GameTypography.bodyMedium.copy(
                                fontSize = 13.sp,
                                color = GameSecondaryText
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        ZynpathMasterPillButton(
                            text = "INVITE FRIEND",
                            style = ZynpathPillStyle.GOLD,
                            height = 46.dp,
                            fontSize = 15,
                            modifier = Modifier.fillMaxWidth(0.7f),
                            onClick = {
                                val publicId = uiState.playerProfile?.publicZynpathId ?: "HOST"
                                val shareIntent = InvitationLinkHelper.createShareIntent(
                                    context = context,
                                    publicZynpathId = publicId,
                                    displayName = uiState.playerProfile?.displayName ?: "Player"
                                )
                                context.startActivity(shareIntent)
                            }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.filteredFriends) { friend ->
                        val isSelected = uiState.selectedFriendIds.contains(friend.appScopedId)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.toggleFriendSelection(friend.appScopedId)
                                },
                            colors = CardDefaults.cardColors(containerColor = RefNavySurface),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) RefCyanNeon.copy(alpha = 0.5f) else RefNavyBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(GameMidnightBlue)
                                        .border(1.dp, GameRoyalBlue, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = RefCyanNeon,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = friend.displayName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "ID: ${friend.appScopedId}",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = GameSecondaryText
                                    )
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(RefCyanNeon),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = GameDeepNavy,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .border(1.5.dp, Color(0xFF334B75), CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
