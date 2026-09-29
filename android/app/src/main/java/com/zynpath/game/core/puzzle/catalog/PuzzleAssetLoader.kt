package com.zynpath.game.core.puzzle.catalog

/**
 * Platform-independent abstraction for loading raw puzzle asset files.
 *
 * Implements Prompt 11 Section 6:
 * Keeps the pure domain catalog completely decoupled from Android Context and UI frameworks.
 */
interface PuzzleAssetLoader {
    /**
     * Reads the UTF-8 text content of the asset at [path], or returns null if not found.
     */
    fun loadAsset(path: String): String?

    /**
     * Checks if an asset exists at [path].
     */
    fun hasAsset(path: String): Boolean

    /**
     * Lists assets inside [directory], or returns empty list if not supported.
     */
    fun listAssets(directory: String): List<String> = emptyList()
}

/**
 * In-memory asset loader primarily used for fast JVM unit tests and programmatic fixtures.
 */
class InMemoryAssetLoader(
    initialAssets: Map<String, String> = emptyMap()
) : PuzzleAssetLoader {
    private val assets: MutableMap<String, String> = initialAssets.toMutableMap()

    override fun loadAsset(path: String): String? = assets[path]
    override fun hasAsset(path: String): Boolean = assets.containsKey(path)
    override fun listAssets(directory: String): List<String> =
        assets.keys.filter { it.startsWith(directory) }

    fun putAsset(path: String, content: String) {
        assets[path] = content
    }

    fun removeAsset(path: String) {
        assets.remove(path)
    }
}
