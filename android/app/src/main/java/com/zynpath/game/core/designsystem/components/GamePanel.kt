package com.zynpath.game.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zynpath.game.core.designsystem.theme.GameBorders
import com.zynpath.game.core.designsystem.theme.GameBrushes
import com.zynpath.game.core.designsystem.theme.GameShapes
import com.zynpath.game.core.designsystem.theme.GameTypography

/**
 * High-polish rounded game panel container featuring deep translucent navy glassmorphism,
 * subtle cyan/royal glowing rim borders, and optional structured header slots.
 *
 * @param modifier Layout modifier.
 * @param title Optional title displayed in the panel header.
 * @param subtitle Optional subtitle displayed below the title.
 * @param headerAction Optional composable placed at the right side of the header.
 * @param contentPadding Padding applied to the inner content column.
 * @param border Optional custom border stroke.
 * @param content Slot for panel body content.
 */
@Composable
fun GamePanel(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    headerAction: (@Composable () -> Unit)? = null,
    contentPadding: Dp = 16.dp,
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = GameShapes.panel

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(GameBrushes.panelGlass)
            .border(border ?: GameBorders.panel, shape)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            if (title != null || headerAction != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title ?: "",
                            style = GameTypography.screenHeading
                        )
                        if (subtitle != null) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = subtitle,
                                style = GameTypography.secondaryInfo
                            )
                        }
                    }

                    headerAction?.let {
                        Spacer(modifier = Modifier.width(12.dp))
                        it()
                    }
                }
            }

            content()
        }
    }
}
