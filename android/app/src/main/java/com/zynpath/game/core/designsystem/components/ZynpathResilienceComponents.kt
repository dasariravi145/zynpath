package com.zynpath.game.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.ErrorRed
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary
import com.zynpath.game.core.designsystem.theme.WarningAmber
import com.zynpath.game.core.error.RecoveryAction
import com.zynpath.game.core.error.ZynpathError

/**
 * Non-intrusive inline error banner.
 *
 * Implements Prompt 38 Sections 10 & 70:
 * - High-contrast text with icon for non-color accessibility.
 * - Optional inline retry button.
 */
@Composable
fun ZynpathErrorBanner(
    error: ZynpathError,
    modifier: Modifier = Modifier,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(BackgroundCard)
            .padding(12.dp)
            .semantics { contentDescription = "Error notification: ${error.userMessage}" },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(ErrorRed.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Alert",
                tint = ErrorRed,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = error.userMessage,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            if (error.correlationId.isNotEmpty()) {
                Text(
                    text = "Ref: ${error.correlationId}",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        if (error.suggestedAction !is RecoveryAction.None && onActionClick != null) {
            Spacer(modifier = Modifier.width(8.dp))
            ZynpathSecondaryButton(
                text = error.suggestedAction.label,
                onClick = onActionClick,
                modifier = Modifier.height(36.dp)
            )
        }
    }
}

/**
 * Persistent or dismissible offline / connection indicator.
 *
 * Implements Prompt 38 Section 10 & 26:
 * - Displays clear offline status or reconnecting spinner.
 */
@Composable
fun ZynpathOfflineIndicator(
    isOffline: Boolean,
    modifier: Modifier = Modifier,
    isReconnecting: Boolean = false
) {
    AnimatedVisibility(
        visible = isOffline,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(BackgroundElevated.copy(alpha = 0.95f))
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .semantics { contentDescription = if (isReconnecting) "Reconnecting to server" else "Offline mode active" },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isReconnecting) {
                CircularProgressIndicator(
                    color = ForestMint,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Reconnecting...",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            } else {
                Icon(
                    imageVector = Icons.Default.CloudOff,
                    contentDescription = null,
                    tint = WarningAmber,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Offline — Local progress is saved",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Accessible full-screen error state with primary and secondary recovery actions.
 *
 * Implements Prompt 38 Sections 10, 70, 72:
 * - Does not trap the user. Provides primary recovery and return home option.
 * - Displays correlation reference ID for support inquiries without exposing sensitive data.
 */
@Composable
fun ZynpathFullscreenError(
    error: ZynpathError,
    modifier: Modifier = Modifier,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(24.dp)
            .semantics { contentDescription = "Full screen error: ${error.userMessage}" },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(ErrorRed.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error Icon",
                tint = ErrorRed,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Unable to Continue",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = error.userMessage,
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Reference: ${error.correlationId}",
            fontSize = 12.sp,
            color = TextMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        ZynpathPrimaryButton(
            text = error.suggestedAction.label.ifBlank { "Try Again" },
            onClick = onPrimaryAction,
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        if (onSecondaryAction != null) {
            Spacer(modifier = Modifier.height(12.dp))
            ZynpathSecondaryButton(
                text = "Return Home",
                onClick = onSecondaryAction,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }
    }
}

/**
 * Accessible modal error dialog for critical or user-action-required states.
 *
 * Implements Prompt 38 Sections 10, 57, 70, 72:
 * - Clear title, message, reference ID.
 * - Primary recovery button and secondary dismiss button.
 * - Does not trap the user.
 */
@Composable
fun ZynpathErrorDialog(
    error: ZynpathError,
    onDismiss: () -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(BackgroundCard)
                .padding(24.dp)
                .semantics { contentDescription = "Error alert: ${error.userMessage}" }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ErrorRed.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Alert",
                        tint = ErrorRed,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = when (error.category) {
                        com.zynpath.game.core.error.ErrorCategory.PERSISTENCE -> "Storage Alert"
                        com.zynpath.game.core.error.ErrorCategory.AUTHENTICATION -> "Authentication Required"
                        com.zynpath.game.core.error.ErrorCategory.NETWORK -> "Connection Notice"
                        com.zynpath.game.core.error.ErrorCategory.GAMEPLAY_STATE -> "Session Notice"
                        else -> "Notice"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = error.userMessage,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                if (error.correlationId.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ref: ${error.correlationId}",
                        fontSize = 11.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (error.suggestedAction !is RecoveryAction.None) {
                    ZynpathPrimaryButton(
                        text = error.suggestedAction.label.ifBlank { "OK" },
                        onClick = onActionClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                ZynpathSecondaryButton(
                    text = "Dismiss",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

