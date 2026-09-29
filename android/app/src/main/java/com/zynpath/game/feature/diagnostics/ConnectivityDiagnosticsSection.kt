package com.zynpath.game.feature.diagnostics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary

@Composable
fun ConnectivityDiagnosticsSection(
    viewModel: ConnectivityDiagnosticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "BACKEND CONNECTIVITY (DEBUG)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = ForestMint,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(BackgroundCard)
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header with status indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Target Backend URL",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = uiState.activeTestUrl.ifEmpty { uiState.configuredBaseUrl },
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = ForestMint
                        )
                    }

                    StatusBadge(status = uiState.status)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Presets row
                Text(
                    text = "CONNECTION PRESET:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    uiState.presets.forEach { preset ->
                        val isSelected = uiState.activeTestUrl == preset.url
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ForestMint.copy(alpha = 0.2f) else BackgroundDark)
                                .clickable { viewModel.selectPreset(preset) }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = preset.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) ForestMint else TextMuted,
                                maxLines = 1
                            )
                        }
                    }
                }

                HorizontalDivider(
                    color = BackgroundDark,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // Details based on state
                when (uiState.status) {
                    DiagnosticConnectionStatus.CONNECTED -> {
                        DiagnosticDetailRow(title = "Service", value = uiState.serviceName ?: "Zynpath")
                        DiagnosticDetailRow(title = "Version", value = uiState.serviceVersion ?: "1.0.0")
                        DiagnosticDetailRow(title = "Environment", value = uiState.environment ?: "dev")
                        DiagnosticDetailRow(title = "Latency", value = "${uiState.latencyMs ?: 0} ms")
                    }
                    DiagnosticConnectionStatus.UNREACHABLE -> {
                        Text(
                            text = "Failure Diagnostics:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF8A80)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.lastErrorCategory ?: "Unknown connection failure",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = TextPrimary
                        )
                        if (!uiState.troubleshootingTip.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = uiState.troubleshootingTip!!,
                                fontSize = 11.sp,
                                color = TextMuted,
                                lineHeight = 15.sp
                            )
                        }
                    }
                    DiagnosticConnectionStatus.CHECKING -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = ForestMint,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Probing /api/v1/health...",
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                        }
                    }
                    DiagnosticConnectionStatus.IDLE -> {
                        Text(
                            text = "Select a preset or tap Test Connection to verify backend reachability.",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Solo play runs 100% offline",
                        fontSize = 11.sp,
                        color = ForestMint
                    )

                    Button(
                        onClick = { viewModel.checkConnectivity() },
                        enabled = uiState.status != DiagnosticConnectionStatus.CHECKING,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ForestMint,
                            contentColor = BackgroundDark,
                            disabledContainerColor = BackgroundDark
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (uiState.status == DiagnosticConnectionStatus.CHECKING) "Testing..." else "Test Connection",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: DiagnosticConnectionStatus) {
    val (bgColor, textColor, label) = when (status) {
        DiagnosticConnectionStatus.CONNECTED -> Triple(Color(0xFF1B5E20), Color(0xFF81C784), "CONNECTED")
        DiagnosticConnectionStatus.UNREACHABLE -> Triple(Color(0xFF4A121A), Color(0xFFFF8A80), "OFFLINE")
        DiagnosticConnectionStatus.CHECKING -> Triple(Color(0xFF5D4037), Color(0xFFFFD54F), "CHECKING")
        DiagnosticConnectionStatus.IDLE -> Triple(BackgroundDark, TextMuted, "IDLE")
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
private fun DiagnosticDetailRow(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 13.sp, color = TextMuted)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}
