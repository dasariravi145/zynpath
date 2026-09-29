package com.zynpath.game.core.puzzle.catalog

import android.content.Context
import android.content.res.AssetManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Android platform implementation of [PuzzleAssetLoader] reading offline puzzle assets
 * from the Android application package assets directory.
 *
 * Implements Prompt 11 Section 6 & 12:
 * Decouples platform Android Context / AssetManager access from pure Kotlin catalog domain.
 */
@Singleton
class AndroidAssetLoader @Inject constructor(
    @ApplicationContext private val context: Context
) : PuzzleAssetLoader {

    private val assetManager: AssetManager = context.assets

    override fun loadAsset(path: String): String? {
        return try {
            assetManager.open(path).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).use { reader ->
                    reader.readText()
                }
            }
        } catch (_: Exception) {
            null
        }
    }

    override fun hasAsset(path: String): Boolean {
        return try {
            assetManager.open(path).use { true }
        } catch (_: Exception) {
            false
        }
    }

    override fun listAssets(directory: String): List<String> {
        return try {
            assetManager.list(directory)?.toList() ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }
}
