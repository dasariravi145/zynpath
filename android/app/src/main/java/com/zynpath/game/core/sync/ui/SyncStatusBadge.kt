package com.zynpath.game.core.sync.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zynpath.game.core.sync.model.SyncState
import com.zynpath.game.core.sync.model.SyncStatus

/**
 * Transparent synchronization status badge adhering to the 5 official states.
 *
 * Implements Prompt 35 Sections 18, 19 & 64:
 * - SAVED_LOCALLY: "Saved on this device"
 * - WAITING_TO_SYNC: "Waiting to sync (X)"
 * - SYNCING: "Syncing..."
 * - SYNCED: "Synced"
 * - ACTION_REQUIRED: "Action required"
 *
 * Designed to be unobtrusive, providing user confidence without disrupting gameplay.
 */
@Composable
fun SyncStatusBadge(
    syncStatus: SyncStatus,
    onRetryClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val (label, icon, color, showSpinner) = when (syncStatus.state) {
        SyncState.SAVED_LOCALLY -> Quadruple(
            "Saved on this device",
            Icons.Default.PhoneAndroid,
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            false
        )
        SyncState.WAITING_TO_SYNC -> {
            val countText = if (syncStatus.pendingOperationsCount > 0) " (${syncStatus.pendingOperationsCount})" else ""
            Quadruple(
                "Waiting to sync$countText",
                Icons.Default.CloudQueue,
                MaterialTheme.colorScheme.secondary,
                false
            )
        }
        SyncState.SYNCING -> Quadruple(
            "Syncing...",
            Icons.Default.CloudSync,
            MaterialTheme.colorScheme.primary,
            true
        )
        SyncState.SYNCED -> Quadruple(
            "Synced",
            Icons.Default.CloudDone,
            Color(0xFF2E7D32), // Forest Green
            false
        )
        SyncState.ACTION_REQUIRED -> Quadruple(
            syncStatus.message?.takeIf { it.isNotBlank() } ?: "Action required",
            Icons.Default.ErrorOutline,
            MaterialTheme.colorScheme.error,
            false
        )
    }

    Surface(
        modifier = modifier
            .then(
                if (syncStatus.state == SyncState.ACTION_REQUIRED && onRetryClick != null) {
                    Modifier.clickable(onClick = onRetryClick)
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.12f),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (showSpinner) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    strokeWidth = 2.dp,
                    color = color
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(14.dp)
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
