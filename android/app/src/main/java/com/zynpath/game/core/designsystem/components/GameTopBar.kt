package com.zynpath.game.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite

/**
 * Cinematic game top bar with navigation, title hierarchy, and right-aligned action slots
 * (such as CurrencyCounter and StarCounter).
 *
 * @param title Screen or level header title.
 * @param modifier Custom modifier.
 * @param subtitle Optional subtitle (e.g., "World 1 • Level 4").
 * @param onBackClick Optional back button action. If null, back button is hidden.
 * @param actions Composable row slot for counters, badges, and action buttons.
 */
@Composable
fun GameTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(GameDeepNavy.copy(alpha = 0.96f))
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                if (onBackClick != null) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .semantics {
                                role = Role.Button
                                contentDescription = "Navigate back"
                            }
                            .clip(CircleShape)
                            .background(Color(0xCC101D3C))
                            .border(1.5.dp, GameRoyalBlue.copy(alpha = 0.6f), CircleShape)
                            .clickable(onClick = onBackClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = GameWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (title.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                }

                if (title.isNotEmpty()) {
                    Column {
                        Text(
                            text = title,
                            style = GameTypography.screenHeading,
                            maxLines = 1
                        )
                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                style = GameTypography.secondaryInfo,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                content = actions
            )
        }
    }
}
