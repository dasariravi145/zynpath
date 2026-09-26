package com.zynpath.game.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.core.designsystem.components.ScreenHeader
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val preferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        ScreenHeader(
            title = "Settings",
            subtitle = "Audio, Feedback & Preferences",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Audio Section
            SectionHeader(title = "AUDIO")
            SettingsCard {
                SettingsToggleRow(
                    title = "Sound Effects",
                    subtitle = "Touch clicks, path drawing & checkpoint chime",
                    checked = preferences.isSfxEnabled,
                    onCheckedChange = { viewModel.toggleSfx() }
                )
                HorizontalDivider(color = BackgroundDark, thickness = 1.dp)
                SettingsToggleRow(
                    title = "Background Music",
                    subtitle = "Subtle ambient forest soundscape",
                    checked = preferences.isMusicEnabled,
                    onCheckedChange = { viewModel.toggleMusic() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Haptics & Accessibility
            SectionHeader(title = "TACTILE & ACCESSIBILITY")
            SettingsCard {
                SettingsToggleRow(
                    title = "Haptic Feedback",
                    subtitle = "Vibration pulse on cell advance and undo",
                    checked = preferences.isHapticsEnabled,
                    onCheckedChange = { viewModel.toggleHaptics() }
                )
                HorizontalDivider(color = BackgroundDark, thickness = 1.dp)
                SettingsToggleRow(
                    title = "Reduced Motion",
                    subtitle = "Minimize particle bursts and pulse animations",
                    checked = preferences.isReducedMotion,
                    onCheckedChange = { viewModel.toggleReducedMotion() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Appearance & Themes
            SectionHeader(title = "APPEARANCE")
            SettingsCard {
                ThemeSelectorRow(
                    currentTheme = preferences.themePreference,
                    onThemeSelected = { viewModel.setThemePreference(it) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Information & Legal
            SectionHeader(title = "ABOUT ZYNPATH")
            SettingsCard {
                InfoRow(title = "Version", value = "1.0.0 (Build 1)")
                HorizontalDivider(color = BackgroundDark, thickness = 1.dp)
                InfoRow(title = "Puzzle Engine", value = "Continuous Number Path v1.0")
                HorizontalDivider(color = BackgroundDark, thickness = 1.dp)
                ClickableInfoRow(title = "Privacy Policy", detail = "Local-first data policy")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = ForestMint,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BackgroundCard)
            .padding(vertical = 4.dp)
    ) {
        Column { content() }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(text = subtitle, fontSize = 12.sp, color = TextMuted)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TextPrimary,
                checkedTrackColor = ForestMint,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = BackgroundDark
            )
        )
    }
}

@Composable
private fun ThemeSelectorRow(
    currentTheme: String,
    onThemeSelected: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Selected Theme", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThemeChip(name = "Forest Navy", isSelected = currentTheme == "FOREST_NAVY") {
                onThemeSelected("FOREST_NAVY")
            }
            ThemeChip(name = "Neon Cyan", isSelected = currentTheme == "NEON_CYAN") {
                onThemeSelected("NEON_CYAN")
            }
            ThemeChip(name = "Minimalist", isSelected = currentTheme == "MINIMALIST") {
                onThemeSelected("MINIMALIST")
            }
        }
    }
}

@Composable
private fun ThemeChip(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) ForestMint else BackgroundDark)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) BackgroundDark else TextPrimary
        )
    }
}

@Composable
private fun InfoRow(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 15.sp, color = TextPrimary)
        Text(text = value, fontSize = 14.sp, color = TextMuted)
    }
}

@Composable
private fun ClickableInfoRow(title: String, detail: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 15.sp, color = TextPrimary)
        Text(text = detail, fontSize = 13.sp, color = ForestMint)
    }
}
