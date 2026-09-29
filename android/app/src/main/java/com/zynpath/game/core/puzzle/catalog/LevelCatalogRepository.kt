package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.database.repository.ProgressRepository
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Authoritative repository contract for accessing world progression, level metadata,
 * and offline puzzle assets.
 *
 * Implements Prompt 11 Section 25:
 * Exposes clean reactive Flow and suspend APIs decoupled from Android framework internals.
 */
interface LevelCatalogRepository {
    fun getWorlds(): List<WorldDefinition>
    fun getWorld(worldId: Int): WorldDefinition?
    fun getLevelsForWorld(worldId: Int): List<LevelDefinition>
    fun getLevel(levelId: Int): LevelDefinition?
    suspend fun loadPuzzle(levelId: Int): CatalogLoadResult
    fun observeLevelAvailability(levelId: Int): Flow<LevelAvailability>
    fun observeWorldCompletedCount(worldId: Int): Flow<Int>
    suspend fun isWorldUnlocked(worldId: Int): Boolean
    suspend fun isLevelUnlocked(levelId: Int): Boolean
    suspend fun getNextPlayableLevel(): Int
    fun validateCatalog(): CatalogIntegrityResult
}

@Singleton
class LevelCatalogRepositoryImpl @Inject constructor(
    private val assetLoader: PuzzleAssetLoader,
    private val progressRepository: ProgressRepository,
    private val deterministicLevelProvider: com.zynpath.game.core.puzzle.generator.DeterministicLevelProvider
) : LevelCatalogRepository {

    constructor(
        assetLoader: PuzzleAssetLoader,
        progressRepository: ProgressRepository
    ) : this(
        assetLoader = assetLoader,
        progressRepository = progressRepository,
        deterministicLevelProvider = com.zynpath.game.core.puzzle.generator.DeterministicLevelProviderImpl()
    )

    // Manifest is lazily loaded and cached in memory
    private val manifest: CatalogManifest by lazy {
        loadManifest()
    }

    // In-memory caches to prevent repeated asset reading and JSON deserialization
    private val assetValidationCache = java.util.concurrent.ConcurrentHashMap<Int, Boolean>()
    private val loadedPuzzleCache = java.util.concurrent.ConcurrentHashMap<Int, CatalogLoadResult>()

    private fun loadManifest(): CatalogManifest {
        val rawJson = assetLoader.loadAsset(CatalogManifest.DEFAULT_MANIFEST_PATH)
        return if (rawJson != null) {
            try {
                CatalogManifestSerializer.deserialize(rawJson)
            } catch (_: Exception) {
                CatalogManifest.createDefaultManifest()
            }
        } else {
            CatalogManifest.createDefaultManifest()
        }
    }

    override fun getWorlds(): List<WorldDefinition> = manifest.worlds

    override fun getWorld(worldId: Int): WorldDefinition? = manifest.getWorld(worldId)

    override fun getLevelsForWorld(worldId: Int): List<LevelDefinition> = manifest.getLevelsForWorld(worldId)

    override fun getLevel(levelId: Int): LevelDefinition? = manifest.getLevel(levelId)

    override suspend fun loadPuzzle(levelId: Int): CatalogLoadResult {
        if (levelId !in 1..300) {
            return CatalogLoadResult.LevelNotFound(levelId)
        }
        loadedPuzzleCache[levelId]?.let { return it }

        val level = manifest.getLevel(levelId)
            ?: WorldDefinition.forLevel(levelId).let { world ->
                LevelDefinition(
                    levelId = levelId,
                    worldId = world.worldId,
                    puzzleId = "w${world.worldId}_lvl$levelId",
                    puzzleVersion = 1,
                    assetPath = "puzzles/w${world.worldId}/lvl$levelId.json",
                    difficultyEstimate = 0.0,
                    difficultyBand = com.zynpath.game.core.puzzle.curation.DifficultyBand.BEGINNER,
                    uniquenessStatus = com.zynpath.game.core.puzzle.solver.UniquenessStatus.UNIQUE,
                    fingerprint = "",
                    hasPackagedAsset = false,
                    displayName = "Level $levelId"
                )
            }

        // 1. If physical asset file exists, load and validate it
        if (assetLoader.hasAsset(level.assetPath)) {
            val json = assetLoader.loadAsset(level.assetPath)
            if (json != null) {
                val world = manifest.getWorld(level.worldId)
                val validation = PuzzleAssetValidator.validate(json, level, world)
                when (validation) {
                    is PuzzleAssetValidationResult.Valid -> {
                        val result = CatalogLoadResult.Success(level, validation.definition)
                        loadedPuzzleCache[levelId] = result
                        assetValidationCache[levelId] = true
                        return result
                    }
                    is PuzzleAssetValidationResult.Invalid -> {
                        return CatalogLoadResult.AssetInvalid(levelId, level.assetPath, validation.errorSummary)
                    }
                }
            }
        }

        // 2. Seamless fallback to verified in-memory PackagedPuzzles and CuratedFirst50Levels
        val packagedDef = PackagedPuzzles.ALL_PACKAGED[levelId]
            ?: CuratedFirst50Levels.LEVELS[levelId]
            ?: CuratedAnchorLevels.ANCHOR_LEVELS[levelId]
        if (packagedDef != null) {
            val result = CatalogLoadResult.Success(level, packagedDef)
            loadedPuzzleCache[levelId] = result
            assetValidationCache[levelId] = true
            return result
        }

        // 3. Fallback to deterministic level provider for all 300 levels (1..300)
        if (levelId in 1..300) {
            val deterministicDef = deterministicLevelProvider.getLevel(levelId)
            if (deterministicDef != null) {
                val result = CatalogLoadResult.Success(level, deterministicDef)
                loadedPuzzleCache[levelId] = result
                assetValidationCache[levelId] = true
                return result
            }
        }

        return CatalogLoadResult.AssetUnavailable(
            levelId = levelId,
            assetPath = level.assetPath,
            message = "Puzzle asset for Level $levelId is unavailable"
        )
    }

    override fun observeLevelAvailability(levelId: Int): Flow<LevelAvailability> {
        return progressRepository.observeAllProgress().map { allProgress ->
            val progress = allProgress.firstOrNull { it.levelId == levelId }
            if (progress?.isCompleted == true) {
                return@map LevelAvailability.COMPLETED
            }

            val completedLevelIds = allProgress.filter { it.isCompleted }.map { it.levelId }.toSet()
            val isUnlocked = WorldConfiguration.isLevelUnlocked(levelId, completedLevelIds)
            if (!isUnlocked) {
                return@map LevelAvailability.LOCKED
            }

            // Fast path: use cached asset validation if available
            val cachedValid = assetValidationCache[levelId]
            if (cachedValid == true) {
                return@map LevelAvailability.UNLOCKED_AND_AVAILABLE
            } else if (cachedValid == false) {
                return@map LevelAvailability.ASSET_INVALID
            }

            // Check physical asset packaging readiness
            val level = manifest.getLevel(levelId)
            if (level != null && assetLoader.hasAsset(level.assetPath)) {
                val json = assetLoader.loadAsset(level.assetPath)
                if (json != null) {
                    val world = manifest.getWorld(level.worldId)
                    val validation = PuzzleAssetValidator.validate(json, level, world)
                    if (validation is PuzzleAssetValidationResult.Valid) {
                        assetValidationCache[levelId] = true
                        return@map LevelAvailability.UNLOCKED_AND_AVAILABLE
                    } else {
                        assetValidationCache[levelId] = false
                        return@map LevelAvailability.ASSET_INVALID
                    }
                }
            }

            // In-memory packaged puzzles, curated levels, anchor levels, or deterministic level provider availability
            if (PackagedPuzzles.ALL_PACKAGED.containsKey(levelId) ||
                CuratedFirst50Levels.LEVELS.containsKey(levelId) ||
                CuratedAnchorLevels.ANCHOR_LEVELS.containsKey(levelId) ||
                (levelId in 1..300 && deterministicLevelProvider.getLevel(levelId) != null)) {
                assetValidationCache[levelId] = true
                return@map LevelAvailability.UNLOCKED_AND_AVAILABLE
            }

            return@map LevelAvailability.ASSET_UNAVAILABLE
        }.distinctUntilChanged()
    }

    override fun observeWorldCompletedCount(worldId: Int): Flow<Int> {
        val world = manifest.getWorld(worldId) ?: WorldDefinition.forWorld(worldId)
        return progressRepository.observeAllProgress().map { allProgress ->
            allProgress.count { it.isCompleted && it.levelId in world.levelRange }
        }.distinctUntilChanged()
    }

    override suspend fun isWorldUnlocked(worldId: Int): Boolean {
        return progressRepository.isWorldUnlocked(worldId)
    }

    override suspend fun isLevelUnlocked(levelId: Int): Boolean {
        return progressRepository.isLevelUnlocked(levelId)
    }

    override suspend fun getNextPlayableLevel(): Int {
        return progressRepository.getNextPlayableLevel()
    }

    override fun validateCatalog(): CatalogIntegrityResult {
        return CatalogIntegrityChecker.checkIntegrity(manifest, assetLoader, verifyAssetContents = true)
    }
}
