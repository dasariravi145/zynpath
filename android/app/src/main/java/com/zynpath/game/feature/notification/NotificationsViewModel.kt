package com.zynpath.game.feature.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.notification.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel managing notification center state, tab filtering, and read actions.
 *
 * Implements Prompt 31 Section 7, 47, 48:
 * - Reactive state updates for unread counts and read/unread transitions.
 */
@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(NotificationTab.ALL)
    private val _isLoading = MutableStateFlow(false)
    private val _showPermissionRationale = MutableStateFlow(false)

    private data class ScreenFlags(
        val tab: NotificationTab,
        val loading: Boolean,
        val showRationale: Boolean
    )

    private val _screenFlags = combine(_selectedTab, _isLoading, _showPermissionRationale) { tab, loading, showRationale ->
        ScreenFlags(tab, loading, showRationale)
    }

    val uiState: StateFlow<NotificationsUiState> = combine(
        notificationRepository.notifications,
        notificationRepository.unreadCount,
        notificationRepository.preferences,
        _screenFlags
    ) { notifications, unreadCount, prefs, flags ->
        NotificationsUiState(
            isLoading = flags.loading,
            selectedTab = flags.tab,
            notifications = notifications,
            unreadCount = unreadCount,
            isSystemPermissionGranted = prefs.isSystemPermissionGranted,
            showPermissionRationale = flags.showRationale
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NotificationsUiState(isLoading = true)
    )

    init {
        refresh()
    }

    fun setTab(tab: NotificationTab) {
        _selectedTab.value = tab
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            notificationRepository.markAsRead(notificationId)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationRepository.markAllAsRead()
        }
    }

    fun dismissNotification(notificationId: String) {
        viewModelScope.launch {
            notificationRepository.dismissNotification(notificationId)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                notificationRepository.refreshNotifications()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun requestPermissionRationale() {
        _showPermissionRationale.value = true
    }

    fun dismissPermissionRationale() {
        _showPermissionRationale.value = false
    }
}
