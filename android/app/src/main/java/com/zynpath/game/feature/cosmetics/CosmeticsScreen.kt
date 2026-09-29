package com.zynpath.game.feature.cosmetics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.core.cosmetics.model.CosmeticAccessDecision
import com.zynpath.game.core.cosmetics.model.CosmeticAccessStatus
import com.zynpath.game.core.cosmetics.model.CosmeticCategory
import com.zynpath.game.core.cosmetics.model.CosmeticItem
import com.zynpath.game.core.cosmetics.ui.AvatarWithFrame
import com.zynpath.game.core.designsystem.components.StatusBadge
import com.zynpath.game.core.designsystem.components.ZynpathPrimaryButton
import com.zynpath.game.core.designsystem.components.ZynpathSecondaryButton
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.PathCyanGlow
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary
import com.zynpath.game.core.designsystem.theme.ZynpathPalettes
import com.zynpath.game.core.puzzle.model.SamplePuzzles
import com.zynpath.game.core.puzzle.ui.PuzzleBoard

/**
 * Dedicated cosmetic customization screen featuring live previews,
 * free vs premium tier indicators, and authoritative equipment controls.
 *
 * Implements Prompt 28 Sections 12, 17, 26, 33, 34, 47 & 50.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CosmeticsScreen(
    onBackClick: () -> Unit,
    onNavigateToPremium: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CosmeticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Customize Appearance",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.resetToDefaults() },
                        modifier = Modifier.semantics {
                            contentDescription = "Reset appearance to defaults"
                            role = Role.Button
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        bottomBar = {
            CosmeticsBottomActionBar(
                selectedItem = uiState.selectedPreviewItem,
                isPreviewEquipped = uiState.isPreviewEquipped,
                accessDecision = uiState.selectedDecision,
                isEquipping = uiState.isEquipping,
                onEquip = { viewModel.equipSelected() },
                onUnlockPremium = onNavigateToPremium
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Live Interactive Preview Section
            CosmeticsPreviewHeader(
                uiState = uiState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Category Tab Row (Themes, Path Effects, Avatar Frames)
            CategoryTabRow(
                selectedCategory = uiState.selectedCategory,
                onSelectCategory = { viewModel.selectCategory(it) }
            )

            // Catalog Items List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = uiState.currentCategoryItems,
                    key = { it.first.id }
                ) { (item, decision) ->
                    val isEquipped = when (item.category) {
                        CosmeticCategory.THEME -> uiState.equippedCosmetics.themeId == item.id
                        CosmeticCategory.PATH_EFFECT -> uiState.equippedCosmetics.pathEffectId == item.id
                        CosmeticCategory.AVATAR_FRAME -> uiState.equippedCosmetics.avatarFrameId == item.id
                    }
                    val isPreviewing = when (item.category) {
                        CosmeticCategory.THEME -> uiState.previewCosmetics.themeId == item.id
                        CosmeticCategory.PATH_EFFECT -> uiState.previewCosmetics.pathEffectId == item.id
                        CosmeticCategory.AVATAR_FRAME -> uiState.previewCosmetics.avatarFrameId == item.id
                    }

                    CosmeticItemCard(
                        item = item,
                        decision = decision,
                        isEquipped = isEquipped,
                        isPreviewing = isPreviewing,
                        onClick = { viewModel.previewItem(item) }
                    )
                }
            }
        }
    }
}

/**
 * Preview area showing real-time feedback with fixed sample puzzle or avatar frame.
 * Section 17 & 26: fixed sample path, no competitive spoilers.
 */
@Composable
private fun CosmeticsPreviewHeader(
    uiState: CosmeticsUiState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LIVE PREVIEW",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                val badgeText = if (uiState.isPreviewEquipped) "EQUIPPED" else "PREVIEWING"
                val badgeColor = if (uiState.isPreviewEquipped) ForestMint else PathCyanGlow
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.16f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (uiState.selectedCategory) {
                CosmeticCategory.THEME,
                CosmeticCategory.PATH_EFFECT -> {
                    val previewPalette = remember(uiState.previewCosmetics.themeId) {
                        ZynpathPalettes.getById(uiState.previewCosmetics.themeId)
                    }
                    val sampleBoard = remember {
                        SamplePuzzles.Sample3x3_Base.copy(
                            path = SamplePuzzles.Sample3x3_FullSolutionPath.take(5)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(previewPalette.backgroundDark),
                        contentAlignment = Alignment.Center
                    ) {
                        PuzzleBoard(
                            boardState = sampleBoard,
                            palette = previewPalette,
                            pathEffectId = uiState.previewCosmetics.pathEffectId,
                            isReducedMotion = uiState.isReducedMotion,
                            isInputEnabled = false,
                            modifier = Modifier.size(160.dp)
                        )
                    }
                }

                CosmeticCategory.AVATAR_FRAME -> {
                    Box(
                        modifier = Modifier
                            .size(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AvatarWithFrame(
                            avatar = uiState.currentAvatar,
                            frameId = uiState.previewCosmetics.avatarFrameId,
                            size = 110.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            uiState.selectedPreviewItem?.let { item ->
                Text(
                    text = item.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = item.description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun CategoryTabRow(
    selectedCategory: CosmeticCategory,
    onSelectCategory: (CosmeticCategory) -> Unit
) {
    val categories = CosmeticCategory.entries
    val selectedIndex = categories.indexOf(selectedCategory)

    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = BackgroundDark,
        contentColor = TextPrimary,
        indicator = { tabPositions ->
            if (selectedIndex < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    color = ForestMint,
                    height = 3.dp
                )
            }
        }
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory
            Tab(
                selected = isSelected,
                onClick = { onSelectCategory(category) },
                text = {
                    Text(
                        text = category.displayName,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) ForestMint else TextSecondary
                    )
                }
            )
        }
    }
}

@Composable
private fun CosmeticItemCard(
    item: CosmeticItem,
    decision: CosmeticAccessDecision,
    isEquipped: Boolean,
    isPreviewing: Boolean,
    onClick: () -> Unit
) {
    val borderColor = when {
        isPreviewing -> PathCyanGlow
        isEquipped -> ForestMint.copy(alpha = 0.6f)
        else -> Color.Transparent
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = if (isPreviewing) 2.dp else if (isEquipped) 1.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .semantics {
                contentDescription = "${item.name}, ${item.accessStatus.name}. " +
                    (if (isEquipped) "Currently equipped. " else "") +
                    (if (isPreviewing) "Currently previewing. " else "") +
                    item.description
                role = Role.Button
            },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPreviewing) BackgroundElevated else BackgroundCard
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Visual Swatch or Thumbnail
            CosmeticThumbnail(item = item)

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = item.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Badges
                    when (item.accessStatus) {
                        CosmeticAccessStatus.FREE -> {
                            StatusBadge(text = "Free", color = TextMuted)
                        }
                        CosmeticAccessStatus.PREMIUM -> {
                            StatusBadge(text = "Premium", color = AccentGold)
                        }
                        CosmeticAccessStatus.COMING_SOON -> {
                            StatusBadge(text = "Soon", color = TextMuted)
                        }
                        CosmeticAccessStatus.UNAVAILABLE -> {
                            StatusBadge(text = "Locked", color = TextSecondary)
                        }
                    }

                    if (isEquipped) {
                        StatusBadge(text = "Equipped", color = ForestMint)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Trailing status indicator
            if (decision == CosmeticAccessDecision.LOCKED) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked with Premium",
                    tint = AccentGold,
                    modifier = Modifier.size(20.dp)
                )
            } else if (isEquipped) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Equipped",
                    tint = ForestMint,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun CosmeticThumbnail(item: CosmeticItem) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(BackgroundDark),
        contentAlignment = Alignment.Center
    ) {
        when (item.category) {
            CosmeticCategory.THEME -> {
                val palette = ZynpathPalettes.getById(item.id)
                Row(
                    modifier = Modifier.size(30.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(modifier = Modifier.weight(1f).fillMaxSize().clip(CircleShape).background(palette.primary))
                    Box(modifier = Modifier.weight(1f).fillMaxSize().clip(CircleShape).background(palette.secondary))
                    Box(modifier = Modifier.weight(1f).fillMaxSize().clip(CircleShape).background(palette.tertiary))
                }
            }
            CosmeticCategory.PATH_EFFECT -> {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(PathCyanGlow.copy(alpha = 0.25f))
                        .border(2.dp, PathCyanGlow, CircleShape)
                )
            }
            CosmeticCategory.AVATAR_FRAME -> {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .border(2.dp, if (item.accessStatus == CosmeticAccessStatus.PREMIUM) AccentGold else Color(0xFFCBD5E1), CircleShape)
                )
            }
        }
    }
}

@Composable
private fun CosmeticsBottomActionBar(
    selectedItem: CosmeticItem?,
    isPreviewEquipped: Boolean,
    accessDecision: CosmeticAccessDecision,
    isEquipping: Boolean,
    onEquip: () -> Unit,
    onUnlockPremium: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundDark)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        if (selectedItem == null) return@Box

        when {
            isPreviewEquipped -> {
                ZynpathSecondaryButton(
                    text = "Currently Equipped",
                    enabled = false,
                    onClick = {}
                )
            }
            accessDecision == CosmeticAccessDecision.AVAILABLE -> {
                ZynpathPrimaryButton(
                    text = if (isEquipping) "Equipping..." else "Equip ${selectedItem.name}",
                    enabled = !isEquipping,
                    onClick = onEquip
                )
            }
            accessDecision == CosmeticAccessDecision.LOCKED -> {
                ZynpathPrimaryButton(
                    text = "Unlock with Premium",
                    onClick = onUnlockPremium
                )
            }
            else -> {
                ZynpathSecondaryButton(
                    text = "Coming Soon",
                    enabled = false,
                    onClick = {}
                )
            }
        }
    }
}
