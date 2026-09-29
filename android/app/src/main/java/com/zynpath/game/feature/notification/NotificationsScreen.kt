package com.zynpath.game.feature.notification

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.core.designsystem.components.ZynpathPrimaryButton
import com.zynpath.game.core.designsystem.components.ZynpathTopBar
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.AccentPurple
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.BorderSubtle
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary
import com.zynpath.game.core.notification.model.NotificationEventType
import com.zynpath.game.core.notification.model.ZynpathNotification

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDestination: (String) -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.refresh()
    }

    if (uiState.showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissPermissionRationale() },
            title = {
                Text(
                    text = "Stay Updated with Zynpath",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Enabling notifications allows you to receive incoming Friend Duel challenges, Mini League invitations, and optional Daily Challenge reminders in real-time.\n\nNotifications are never required to play Zynpath.",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                ZynpathPrimaryButton(
                    text = "Enable Notifications",
                    onClick = {
                        viewModel.dismissPermissionRationale()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissPermissionRationale() }
                ) {
                    Text(text = "Not Now", color = TextMuted)
                }
            },
            containerColor = BackgroundCard,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        topBar = {
            ZynpathTopBar(
                title = "Notifications",
                onBackClick = onNavigateBack,
                actions = {
                    if (uiState.unreadCount > 0) {
                        IconButton(
                            onClick = { viewModel.markAllAsRead() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Mark all notifications as read",
                                tint = ForestMint
                            )
                        }
                    }
                }
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs: All vs Unread
            PrimaryTabRow(
                selectedTabIndex = uiState.selectedTab.ordinal,
                containerColor = BackgroundElevated,
                contentColor = TextPrimary
            ) {
                Tab(
                    selected = uiState.selectedTab == NotificationTab.ALL,
                    onClick = { viewModel.setTab(NotificationTab.ALL) },
                    text = {
                        Text(
                            text = "All (${uiState.notifications.size})",
                            fontWeight = if (uiState.selectedTab == NotificationTab.ALL) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = uiState.selectedTab == NotificationTab.UNREAD,
                    onClick = { viewModel.setTab(NotificationTab.UNREAD) },
                    text = {
                        Text(
                            text = "Unread (${uiState.unreadCount})",
                            color = if (uiState.unreadCount > 0) AccentGold else TextSecondary,
                            fontWeight = if (uiState.selectedTab == NotificationTab.UNREAD) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            // Optional System Permission Banner (if disabled on Android 13+)
            if (!uiState.isSystemPermissionGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { viewModel.requestPermissionRationale() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BackgroundCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AccentPurple))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsOff,
                            contentDescription = null,
                            tint = AccentPurple,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "System notifications are disabled",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Tap to enable real-time duel alerts and reminders.",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            if (uiState.isLoading && uiState.notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ForestMint)
                }
            } else if (uiState.displayedNotifications.isEmpty()) {
                EmptyNotificationsView(selectedTab = uiState.selectedTab)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = uiState.displayedNotifications,
                        key = { it.id }
                    ) { notification ->
                        NotificationItemCard(
                            notification = notification,
                            onItemClick = {
                                viewModel.markAsRead(notification.id)
                                notification.actionDestination?.let { dest ->
                                    onNavigateToDestination(dest)
                                }
                            },
                            onActionClick = {
                                viewModel.markAsRead(notification.id)
                                notification.actionDestination?.let { dest ->
                                    onNavigateToDestination(dest)
                                }
                            },
                            onDismissClick = {
                                viewModel.dismissNotification(notification.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItemCard(
    notification: ZynpathNotification,
    onItemClick: () -> Unit,
    onActionClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    val icon = getNotificationIcon(notification.eventType)
    val accentColor = getNotificationAccentColor(notification.eventType)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
            .semantics {
                contentDescription = "${notification.title}, ${notification.message}, ${if (notification.isRead) "read" else "unread"}"
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead) BackgroundCard else BackgroundElevated
        ),
        border = if (!notification.isRead) {
            CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Category Icon Badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Body
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 14.sp,
                        fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Medium,
                        color = TextPrimary
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = notification.formattedTime,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        if (!notification.isRead) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(AccentGold)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )

                if (notification.actionDestination != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = onActionClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = accentColor),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(accentColor))
                    ) {
                        Text(
                            text = notification.actionLabel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Dismiss Button
            IconButton(
                onClick = onDismissClick,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss notification",
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyNotificationsView(selectedTab: NotificationTab) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (selectedTab == NotificationTab.UNREAD) Icons.Default.NotificationsActive else Icons.Default.Notifications,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (selectedTab == NotificationTab.UNREAD) "All caught up!" else "No notifications yet",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (selectedTab == NotificationTab.UNREAD)
                "You have no unread notifications or duel challenges."
            else
                "Friend requests, multiplayer duel invitations, and daily challenge alerts will appear here.",
            fontSize = 13.sp,
            color = TextMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

private fun getNotificationIcon(type: NotificationEventType): ImageVector {
    return when (type) {
        NotificationEventType.FRIEND_REQUEST,
        NotificationEventType.FRIEND_REQUEST_ACCEPTED -> Icons.Default.Group

        NotificationEventType.FRIEND_DUEL_INVITATION,
        NotificationEventType.FRIEND_DUEL_INVITATION_ACCEPTED,
        NotificationEventType.FRIEND_DUEL_INVITATION_DECLINED -> Icons.Default.SportsEsports

        NotificationEventType.MINI_LEAGUE_INVITATION,
        NotificationEventType.MINI_LEAGUE_READY -> Icons.Default.EmojiEvents

        NotificationEventType.MATCH_STARTING -> Icons.Default.SportsEsports
        NotificationEventType.MATCH_RESULT -> Icons.Default.EmojiEvents
        NotificationEventType.DAILY_CHALLENGE_REMINDER -> Icons.Default.CalendarToday
    }
}

private fun getNotificationAccentColor(type: NotificationEventType): androidx.compose.ui.graphics.Color {
    return when (type) {
        NotificationEventType.FRIEND_REQUEST,
        NotificationEventType.FRIEND_REQUEST_ACCEPTED -> AccentGold

        NotificationEventType.FRIEND_DUEL_INVITATION,
        NotificationEventType.FRIEND_DUEL_INVITATION_ACCEPTED,
        NotificationEventType.FRIEND_DUEL_INVITATION_DECLINED -> AccentPurple

        NotificationEventType.MINI_LEAGUE_INVITATION,
        NotificationEventType.MINI_LEAGUE_READY -> ForestMint

        NotificationEventType.MATCH_STARTING -> AccentPurple
        NotificationEventType.MATCH_RESULT -> AccentGold
        NotificationEventType.DAILY_CHALLENGE_REMINDER -> ForestMint
    }
}
