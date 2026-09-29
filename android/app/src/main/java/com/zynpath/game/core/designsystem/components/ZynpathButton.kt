package com.zynpath.game.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Primary action button for Zynpath.
 * Delegates to the authoritative [GamePrimaryButton] with Gold & Orange gradients and 3D arcade bevel.
 */
@Composable
fun ZynpathPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    GamePrimaryButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon
    )
}

/**
 * Secondary action button for Zynpath.
 * Delegates to the authoritative [GameSecondaryButton] with translucent royal-blue glass and glowing border.
 */
@Composable
fun ZynpathSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    borderColor: Color? = null,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    GameSecondaryButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF07142D)
@Composable
private fun ZynpathButtonPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ZynpathPrimaryButton(text = "Play Solo (Primary)", onClick = {})
        ZynpathSecondaryButton(text = "World Select (Secondary)", onClick = {})
    }
}
