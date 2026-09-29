package com.zynpath.game.feature.settings

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.core.account.model.BlockedPlayerItem
import com.zynpath.game.core.account.model.ProfileVisibility
import com.zynpath.game.core.designsystem.components.ScreenHeader
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary

@Composable
fun PrivacyScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val preferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val blockedPlayers by viewModel.blockedPlayers.collectAsStateWithLifecycle()
    val uiMessage by viewModel.uiMessage.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var playerToUnblock by remember { mutableStateOf<BlockedPlayerItem?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        ScreenHeader(
            title = "Privacy & Discovery",
            subtitle = "Control your public presence and relationships",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Profile Visibility Tier
            SectionHeader(title = "PROFILE VISIBILITY")
            SettingsCard {
                VisibilityOptionRow(
                    title = "Public",
                    description = "Anyone can view your public profile, rank & badges",
                    isSelected = preferences.profileVisibility == ProfileVisibility.PUBLIC.name,
                    onClick = { viewModel.setProfileVisibility(ProfileVisibility.PUBLIC) }
                )
                HorizontalDivider(color = BackgroundDark, thickness = 1.dp)
                VisibilityOptionRow(
                    title = "Friends Only",
                    description = "Only mutual friends can view your public profile & stats",
                    isSelected = preferences.profileVisibility == ProfileVisibility.FRIENDS_ONLY.name,
                    onClick = { viewModel.setProfileVisibility(ProfileVisibility.FRIENDS_ONLY) }
                )
                HorizontalDivider(color = BackgroundDark, thickness = 1.dp)
                VisibilityOptionRow(
                    title = "Private",
                    description = "Hidden from public lookup and social discovery",
                    isSelected = preferences.profileVisibility == ProfileVisibility.PRIVATE.name,
                    onClick = { viewModel.setProfileVisibility(ProfileVisibility.PRIVATE) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Social Discovery & Direct Invites
            SectionHeader(title = "SOCIAL DISCOVERY")
            SettingsCard {
                SettingsToggleRow(
                    title = "Allow Zynpath ID Search",
                    subtitle = "Other players can look up your profile by entering your public Zynpath ID",
                    checked = preferences.allowZynpathIdSearch,
                    onCheckedChange = { viewModel.toggleAllowZynpathIdSearch() }
                )
                HorizontalDivider(color = BackgroundDark, thickness = 1.dp)
                SettingsToggleRow(
                    title = "Allow Friend Requests",
                    subtitle = "Receive incoming friend requests and duel invitations",
                    checked = preferences.allowFriendRequests,
                    onCheckedChange = { viewModel.toggleAllowFriendRequests() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Blocked Players List & Unblock Actions
            SectionHeader(title = "BLOCKED PLAYERS (${blockedPlayers.size})")
            SettingsCard {
                if (blockedPlayers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No blocked players. Blocked users cannot send requests or duel invites.",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    }
                } else {
                    blockedPlayers.forEachIndexed { index, blockedItem ->
                        if (index > 0) {
                            HorizontalDivider(color = BackgroundDark, thickness = 1.dp)
                        }
                        BlockedPlayerRow(
                            item = blockedItem,
                            onUnblockClick = { playerToUnblock = blockedItem }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Privacy & Data Guarantees
            SectionHeader(title = "DATA MINIMIZATION & COMPLIANCE")
            SettingsCard {
                InfoRow(title = "Solo Progress", value = "Stored offline on device first")
                HorizontalDivider(color = BackgroundDark, thickness = 1.dp)
                InfoRow(title = "Analytics & Ads", value = "Zero personal identifiers shared")
                HorizontalDivider(color = BackgroundDark, thickness = 1.dp)
                InfoRow(title = "Data Deletion", value = "Self-service via Account settings")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Unblock Confirmation Dialog
    playerToUnblock?.let { item ->
        AlertDialog(
            onDismissRequest = { playerToUnblock = null },
            title = { Text(text = "Unblock Player?", color = TextPrimary) },
            text = {
                Text(
                    text = "Are you sure you want to unblock ${item.displayName} (${item.publicZynpathId})? They will be able to discover your profile and send requests if discovery is enabled.",
                    color = TextMuted
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.unblockPlayer(item.playerId)
                        playerToUnblock = null
                    }
                ) {
                    Text("Unblock", color = ForestMint, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { playerToUnblock = null }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = BackgroundCard
        )
    }

    // Toast-like UI feedback
    uiMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissMessage() },
            title = { Text(text = "Privacy Settings", color = TextPrimary) },
            text = { Text(text = msg, color = TextMuted) },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissMessage() }) {
                    Text("OK", color = ForestMint)
                }
            },
            containerColor = BackgroundCard
        )
    }
}

@Composable
private fun VisibilityOptionRow(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) ForestMint else TextPrimary
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = TextMuted
            )
        }
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (isSelected) ForestMint else BackgroundDark),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(BackgroundDark)
                )
            }
        }
    }
}

@Composable
private fun BlockedPlayerRow(
    item: BlockedPlayerItem,
    onUnblockClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.displayName,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = "ID: ${item.publicZynpathId}",
                fontSize = 12.sp,
                color = TextMuted
            )
        }
        OutlinedButton(
            onClick = onUnblockClick,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ForestMint),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Unblock", fontSize = 12.sp)
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = ForestMint,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BackgroundCard)
            .padding(vertical = 4.dp)
    ) {
        Column { content() }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(text = subtitle, fontSize = 12.sp, color = TextMuted)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TextPrimary,
                checkedTrackColor = ForestMint,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = BackgroundDark
            )
        )
    }
}

@Composable
private fun InfoRow(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 15.sp, color = TextPrimary)
        Text(text = value, fontSize = 14.sp, color = TextMuted)
    }
}
