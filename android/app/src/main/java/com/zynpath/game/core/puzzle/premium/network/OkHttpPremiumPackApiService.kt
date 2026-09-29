package com.zynpath.game.core.puzzle.premium.network

import com.zynpath.game.BuildConfig
import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.puzzle.catalog.PuzzleAssetSerializer
import com.zynpath.game.core.puzzle.curation.DifficultyBand
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.premium.model.PackPublicationStatus
import com.zynpath.game.core.puzzle.premium.model.PremiumPackDefinition
import com.zynpath.game.core.puzzle.premium.model.PremiumPackManifest
import com.zynpath.game.core.puzzle.premium.model.PremiumPuzzleRef
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OkHttpPremiumPackApiService(
    private val okHttpClient: OkHttpClient,
    private val customBaseUrl: String? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PremiumPackApiService {

    @Inject
    constructor(okHttpClient: OkHttpClient) : this(okHttpClient, null, Dispatchers.IO)

    override fun getBaseUrl(): String {
        return customBaseUrl?.removeSuffix("/") ?: BuildConfig.BACKEND_BASE_URL.removeSuffix("/")
    }

    override suspend fun getPackCatalog(sessionToken: String?): NetworkResult<List<PremiumPackManifest>> = withContext(ioDispatcher) {
        val url = "${getBaseUrl()}/api/v1/content/packs"
        val requestBuilder = Request.Builder()
            .url(url)
            .get()
            .addHeader("Accept", "application/json")

        if (!sessionToken.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $sessionToken")
        }

        try {
            val response = okHttpClient.newCall(requestBuilder.build()).execute()
            val body = response.body?.string()

            if (!response.isSuccessful || body == null) {
                return@withContext NetworkResult.Error(
                    code = response.code,
                    message = "Failed to fetch pack catalog: HTTP ${response.code}"
                )
            }

            val array = JSONArray(body)
            val list = mutableListOf<PremiumPackManifest>()
            for (i in 0 until array.length()) {
                list.add(parseManifestObject(array.getJSONObject(i)))
            }
            NetworkResult.Success(list)
        } catch (e: IOException) {
            NetworkResult.Exception(e)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getPackManifest(sessionToken: String?, packId: String): NetworkResult<PremiumPackManifest> = withContext(ioDispatcher) {
        val url = "${getBaseUrl()}/api/v1/content/packs/$packId/manifest"
        val requestBuilder = Request.Builder()
            .url(url)
            .get()
            .addHeader("Accept", "application/json")

        if (!sessionToken.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $sessionToken")
        }

        try {
            val response = okHttpClient.newCall(requestBuilder.build()).execute()
            val body = response.body?.string()

            if (!response.isSuccessful || body == null) {
                return@withContext NetworkResult.Error(
                    code = response.code,
                    message = "Failed to fetch manifest for $packId: HTTP ${response.code}"
                )
            }

            val manifest = parseManifestObject(JSONObject(body))
            NetworkResult.Success(manifest)
        } catch (e: IOException) {
            NetworkResult.Exception(e)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun downloadPack(sessionToken: String, packId: String): NetworkResult<PremiumPackDefinition> = withContext(ioDispatcher) {
        val url = "${getBaseUrl()}/api/v1/content/packs/$packId/download"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            val body = response.body?.string()

            if (response.code == 403) {
                return@withContext NetworkResult.Error(
                    code = 403,
                    message = "ENTITLEMENT_REQUIRED: Active Premium subscription required to download pack $packId"
                )
            }

            if (!response.isSuccessful || body == null) {
                return@withContext NetworkResult.Error(
                    code = response.code,
                    message = "Failed to download pack $packId: HTTP ${response.code}"
                )
            }

            val root = JSONObject(body)
            val manifestObj = root.getJSONObject("manifest")
            val manifest = parseManifestObject(manifestObj)

            val puzzlesArray = root.getJSONArray("puzzles")
            val puzzles = mutableListOf<PuzzleDefinition>()
            for (i in 0 until puzzlesArray.length()) {
                val pObj = puzzlesArray.getJSONObject(i)
                val asset = PuzzleAssetSerializer.deserialize(pObj.toString())
                puzzles.add(asset.toPuzzleDefinition())
            }

            NetworkResult.Success(PremiumPackDefinition(manifest, puzzles))
        } catch (e: IOException) {
            NetworkResult.Exception(e)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    private fun parseManifestObject(obj: JSONObject): PremiumPackManifest {
        val puzzles = mutableListOf<PremiumPuzzleRef>()
        val arr = obj.optJSONArray("puzzleReferences") ?: obj.optJSONArray("puzzles")
        if (arr != null) {
            for (i in 0 until arr.length()) {
                val p = arr.getJSONObject(i)
                val rows = p.optInt("rows", 5)
                val cols = p.optInt("columns", 5)
                val band = try {
                    DifficultyBand.valueOf(p.optString("difficultyBand", "EXPERT"))
                } catch (e: Exception) {
                    DifficultyBand.EXPERT
                }
                puzzles.add(
                    PremiumPuzzleRef(
                        puzzleId = p.getString("puzzleId"),
                        puzzleVersion = p.optInt("puzzleVersion", 1),
                        levelIndex = p.optInt("levelIndex", i + 1),
                        fingerprint = p.optString("fingerprint", ""),
                        gridDimensions = GridDimensions(rows, cols),
                        checkpointCount = p.optInt("checkpointCount", 5),
                        wallCount = p.optInt("wallCount", 0),
                        estimatedDifficulty = p.optDouble("estimatedDifficulty", 0.8),
                        difficultyBand = band
                    )
                )
            }
        }

        val diffBand = try {
            DifficultyBand.valueOf(obj.optString("difficultyBand", obj.optString("difficulty", "EXPERT")))
        } catch (e: Exception) {
            DifficultyBand.EXPERT
        }

        val pubStatus = try {
            PackPublicationStatus.valueOf(obj.optString("publicationStatus", "PREMIUM"))
        } catch (e: Exception) {
            PackPublicationStatus.PREMIUM
        }

        return PremiumPackManifest(
            packId = obj.getString("packId"),
            version = obj.optInt("version", obj.optInt("packVersion", 1)),
            displayName = obj.getString("displayName"),
            description = obj.getString("description"),
            difficultyBand = diffBand,
            puzzleCount = obj.optInt("puzzleCount", puzzles.size),
            contentFingerprint = obj.optString("contentFingerprint", obj.optString("checksum", "")),
            requiredEntitlement = obj.optString("requiredEntitlement", "PREMIUM_SOLO_PACKS"),
            publicationStatus = pubStatus,
            themeTag = obj.optString("themeTag", "Premium"),
            checksum = obj.optString("checksum", ""),
            puzzleReferences = puzzles
        )
    }
}
