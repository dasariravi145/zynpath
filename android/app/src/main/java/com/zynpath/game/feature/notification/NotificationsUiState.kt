package com.zynpath.game.feature.notification

import com.zynpath.game.core.notification.model.ZynpathNotification

enum class NotificationTab {
    ALL,
    UNREAD
}

data class NotificationsUiState(
    val isLoading: Boolean = false,
    val selectedTab: NotificationTab = NotificationTab.ALL,
    val notifications: List<ZynpathNotification> = emptyList(),
    val unreadCount: Int = 0,
    val isSystemPermissionGranted: Boolean = true,
    val showPermissionRationale: Boolean = false,
    val errorMessage: String? = null
) {
    val displayedNotifications: List<ZynpathNotification>
        get() = when (selectedTab) {
            NotificationTab.ALL -> notifications
            NotificationTab.UNREAD -> notifications.filter { !it.isRead && !it.isExpired }
        }
}
