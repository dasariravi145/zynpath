package com.zynpath.game.core.puzzle.premium.model

import com.zynpath.game.core.puzzle.curation.DifficultyBand
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.PuzzleDefinition

/**
 * Publication status of a puzzle pack.
 *
 * Implements Prompt 27 Section 11:
 * Clearly distinguishes between accessible, premium-gated, unavailable, and future packs.
 */
enum class PackPublicationStatus {
    FREE,
    PREMIUM,
    UNAVAILABLE,
    COMING_SOON
}

/**
 * Resolved entitlement and availability state for a specific player and device.
 *
 * Implements Prompt 27 Section 19:
 * Unified access decision evaluated from pack requirements, subscription status, and offline cache.
 */
enum class PackAccessStatus {
    ALLOWED,
    LOCKED,
    NEEDS_DOWNLOAD,
    COMING_SOON,
    UNAVAILABLE,
    EXPIRED;

    val isPlayable: Boolean
        get() = this == ALLOWED
}

/**
 * Lightweight reference to a puzzle within a pack manifest.
 */
data class PremiumPuzzleRef(
    val puzzleId: String,
    val puzzleVersion: Int = 1,
    val levelIndex: Int,
    val fingerprint: String,
    val gridDimensions: GridDimensions,
    val checkpointCount: Int,
    val wallCount: Int,
    val estimatedDifficulty: Double,
    val difficultyBand: DifficultyBand
) {
    val gridSize: String get() = "${gridDimensions.rows}x${gridDimensions.columns}"
}

/**
 * Manifest describing a versioned Premium Solo puzzle pack.
 *
 * Implements Prompt 27 Section 16:
 * Versioned content manifest with integrity checksum and puzzle references.
 */
data class PremiumPackManifest(
    val packId: String,
    val version: Int,
    val displayName: String,
    val description: String,
    val difficultyBand: DifficultyBand,
    val puzzleCount: Int,
    val contentFingerprint: String,
    val requiredEntitlement: String = "PREMIUM_SOLO_PACKS",
    val publicationStatus: PackPublicationStatus = PackPublicationStatus.PREMIUM,
    val themeTag: String,
    val checksum: String,
    val puzzleReferences: List<PremiumPuzzleRef> = emptyList()
) {
    val packVersion: Int get() = version
    val difficulty: String get() = difficultyBand.name
    val puzzles: List<PremiumPuzzleRef> get() = puzzleReferences
}

/**
 * Full domain definition of a Premium Solo pack containing active puzzle definitions.
 */
data class PremiumPackDefinition(
    val packId: String,
    val version: Int,
    val displayName: String,
    val description: String,
    val difficultyBand: DifficultyBand,
    val puzzleCount: Int,
    val contentFingerprint: String,
    val requiredEntitlement: String = "PREMIUM_SOLO_PACKS",
    val publicationStatus: PackPublicationStatus = PackPublicationStatus.PREMIUM,
    val themeTag: String,
    val puzzles: List<PuzzleDefinition>
) {
    constructor(manifest: PremiumPackManifest, puzzles: List<PuzzleDefinition>) : this(
        packId = manifest.packId,
        version = manifest.version,
        displayName = manifest.displayName,
        description = manifest.description,
        difficultyBand = manifest.difficultyBand,
        puzzleCount = puzzles.size,
        contentFingerprint = manifest.contentFingerprint,
        requiredEntitlement = manifest.requiredEntitlement,
        publicationStatus = manifest.publicationStatus,
        themeTag = manifest.themeTag,
        puzzles = puzzles
    )

    val manifest: PremiumPackManifest
        get() = PremiumPackManifest(
            packId = packId,
            version = version,
            displayName = displayName,
            description = description,
            difficultyBand = difficultyBand,
            puzzleCount = puzzleCount,
            contentFingerprint = contentFingerprint,
            requiredEntitlement = requiredEntitlement,
            publicationStatus = publicationStatus,
            themeTag = themeTag,
            checksum = contentFingerprint
        )
}

/**
 * Presentation item for displaying a pack in UI.
 */
data class PremiumPackItem(
    val manifest: PremiumPackManifest,
    val accessStatus: PackAccessStatus,
    val isDownloaded: Boolean,
    val completedLevelsCount: Int,
    val totalLevelsCount: Int
) {
    constructor(
        packId: String,
        packVersion: Int,
        displayName: String,
        description: String,
        difficulty: String,
        puzzleCount: Int,
        publicationStatus: PackPublicationStatus,
        accessStatus: PackAccessStatus,
        isInstalled: Boolean,
        isDownloading: Boolean = false,
        downloadProgress: Float = 0f,
        completedPuzzlesCount: Int,
        totalPuzzlesCount: Int
    ) : this(
        manifest = PremiumPackManifest(
            packId = packId,
            version = packVersion,
            displayName = displayName,
            description = description,
            difficultyBand = try { DifficultyBand.valueOf(difficulty) } catch (e: Exception) { DifficultyBand.EXPERT },
            puzzleCount = puzzleCount,
            contentFingerprint = "",
            requiredEntitlement = "PREMIUM_SOLO_PACKS",
            publicationStatus = publicationStatus,
            themeTag = "Premium",
            checksum = ""
        ),
        accessStatus = accessStatus,
        isDownloaded = isInstalled,
        completedLevelsCount = completedPuzzlesCount,
        totalLevelsCount = totalPuzzlesCount
    )

    val packId: String get() = manifest.packId
    val packVersion: Int get() = manifest.version
    val displayName: String get() = manifest.displayName
    val description: String get() = manifest.description
    val difficulty: String get() = manifest.difficultyBand.name
    val puzzleCount: Int get() = manifest.puzzleCount
    val publicationStatus: PackPublicationStatus get() = manifest.publicationStatus
    val isInstalled: Boolean get() = isDownloaded
    val completedPuzzlesCount: Int get() = completedLevelsCount

    val completionPercentage: Int
        get() = if (totalLevelsCount > 0) (completedLevelsCount * 100) / totalLevelsCount else 0

    val isFullyCompleted: Boolean
        get() = completedLevelsCount >= totalLevelsCount && totalLevelsCount > 0
}
