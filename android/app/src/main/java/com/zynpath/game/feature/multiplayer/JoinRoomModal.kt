package com.zynpath.game.feature.multiplayer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.components.GamePrimaryButton
import com.zynpath.game.core.designsystem.components.glossyTopHighlight
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.core.designsystem.theme.RefNavyBorder
import com.zynpath.game.core.designsystem.theme.RefNavySurface

/**
 * Compact Join Room Modal for Zynpath Friends Arena.
 *
 * Implements Prompt Section 10:
 * - Scrim dimming over underlying screen.
 * - Hardware / gesture back dismissal.
 * - Clean 6-character room code text input.
 * - JOIN ROOM action with validation and authoritative backend checking.
 */
@Composable
fun JoinRoomModal(
    roomCode: String,
    onRoomCodeChange: (String) -> Unit,
    onJoinClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    isJoining: Boolean = false,
    errorMessage: String? = null
) {
    val focusManager = LocalFocusManager.current

    // Intercept back button to dismiss modal
    BackHandler {
        if (!isJoining) onDismiss()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xCC040C1E))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!isJoining) onDismiss()
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = 380.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xF80A1635),
                            GameMidnightBlue
                        )
                    )
                )
                .border(
                    BorderStroke(
                        1.4.dp,
                        Brush.horizontalGradient(
                            listOf(
                                GameElectricCyan.copy(alpha = 0.8f),
                                GameGoldHighlight.copy(alpha = 0.6f),
                                GameElectricCyan.copy(alpha = 0.8f)
                            )
                        )
                    ),
                    RoundedCornerShape(22.dp)
                )
                .glossyTopHighlight(cornerRadius = 22.dp, highlightAlpha = 0.16f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    // Prevent click-through to scrim
                }
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header: Title and X Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "JOIN ROOM",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        letterSpacing = 1.sp,
                        color = GameWhite
                    )

                    IconButton(
                        onClick = onDismiss,
                        enabled = !isJoining,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = GameSecondaryText,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Input Field: Enter Room Code
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Enter Room Code",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GameSecondaryText
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(RefNavySurface)
                            .border(
                                1.2.dp,
                                if (roomCode.isNotEmpty()) GameElectricCyan else RefNavyBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicTextField(
                            value = roomCode,
                            onValueChange = onRoomCodeChange,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(
                                color = GameWhite,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                textAlign = TextAlign.Center,
                                letterSpacing = 4.sp
                            ),
                            cursorBrush = SolidColor(GameElectricCyan),
                            singleLine = true,
                            enabled = !isJoining,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Characters,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    if (roomCode.isNotBlank() && !isJoining) {
                                        onJoinClick()
                                    }
                                }
                            ),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (roomCode.isEmpty()) {
                                        Text(
                                            text = "ROOM CODE",
                                            color = GameSecondaryText.copy(alpha = 0.5f),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            letterSpacing = 2.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }
                }

                // Error feedback if join failed
                if (!errorMessage.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x33FF5252))
                            .border(1.dp, Color(0x66FF5252), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFF8A80),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Primary Action Button: JOIN ROOM
                GamePrimaryButton(
                    text = if (isJoining) "JOINING..." else "JOIN ROOM",
                    onClick = {
                        focusManager.clearFocus()
                        onJoinClick()
                    },
                    enabled = roomCode.isNotBlank() && !isJoining,
                    height = 48.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
