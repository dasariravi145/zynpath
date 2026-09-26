package com.zynpath.game.core.designsystem.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.ZynpathShapes

@Composable
fun ZynpathConfirmationDialog(
    title: String,
    message: String,
    confirmButtonText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dismissButtonText: String = "Cancel",
    isDestructive: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Text(
                text = message,
                fontSize = 14.sp,
                color = TextMuted,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmButtonText,
                    fontWeight = FontWeight.Bold,
                    color = if (isDestructive) com.zynpath.game.core.designsystem.theme.ErrorRed else ForestMint
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = dismissButtonText,
                    color = TextMuted
                )
            }
        },
        modifier = modifier,
        shape = ZynpathShapes.large,
        containerColor = BackgroundElevated
    )
}

@Preview
@Composable
private fun ConfirmationDialogPreview() {
    ZynpathConfirmationDialog(
        title = "Reset Puzzle",
        message = "Are you sure you want to clear your current path and start over?",
        confirmButtonText = "Reset",
        onConfirm = {},
        onDismiss = {}
    )
}
