package com.zynpath.game.core.cosmetics.network

import com.zynpath.game.BuildConfig
import com.zynpath.game.core.cosmetics.model.CosmeticAccessStatus
import com.zynpath.game.core.cosmetics.model.CosmeticCategory
import com.zynpath.game.core.cosmetics.model.CosmeticItem
import com.zynpath.game.core.cosmetics.model.EquippedCosmetics
import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.premium.model.PremiumFeatureKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OkHttpCosmeticsApiService(
    private val okHttpClient: OkHttpClient,
    private val customBaseUrl: String? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CosmeticsApiService {

    @Inject
    constructor(okHttpClient: OkHttpClient) : this(okHttpClient, null, Dispatchers.IO)

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    override fun getBaseUrl(): String {
        return customBaseUrl?.removeSuffix("/") ?: BuildConfig.BACKEND_BASE_URL.removeSuffix("/")
    }

    override suspend fun getCatalog(): NetworkResult<List<CosmeticItem>> = withContext(ioDispatcher) {
        val url = "${getBaseUrl()}/api/v1/cosmetics/catalog"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Accept", "application/json")
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            val body = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                return@withContext NetworkResult.Error(response.code, "Failed to fetch cosmetic catalog: ${response.message}")
            }
            val json = JSONObject(body)
            val itemsJson = json.optJSONArray("items") ?: return@withContext NetworkResult.Success(emptyList())
            val list = mutableListOf<CosmeticItem>()
            for (i in 0 until itemsJson.length()) {
                val itemObj = itemsJson.getJSONObject(i)
                val id = itemObj.getString("id")
                val category = CosmeticCategory.valueOf(itemObj.getString("category"))
                val name = itemObj.getString("name")
                val description = itemObj.getString("description")
                val accessStatus = CosmeticAccessStatus.valueOf(itemObj.getString("accessStatus"))
                val reqKeyStr = itemObj.optString("requiredFeatureKey", "")
                val reqKey = if (reqKeyStr.isNotEmpty()) {
                    try { PremiumFeatureKey.valueOf(reqKeyStr) } catch (_: Exception) { null }
                } else null
                val isDefault = itemObj.optBoolean("isDefault", false)
                val version = itemObj.optInt("version", 1)
                list.add(
                    CosmeticItem(
                        id = id,
                        category = category,
                        name = name,
                        description = description,
                        accessStatus = accessStatus,
                        requiredFeatureKey = reqKey,
                        isDefault = isDefault,
                        version = version
                    )
                )
            }
            NetworkResult.Success(list)
        } catch (e: IOException) {
            NetworkResult.Error(-1, "Network error fetching cosmetic catalog: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Error(-2, "Catalog parsing error: ${e.message}")
        }
    }

    override suspend fun getEquippedCosmetics(sessionToken: String): NetworkResult<EquippedCosmetics> = withContext(ioDispatcher) {
        val url = "${getBaseUrl()}/api/v1/cosmetics/equipped"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            val body = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                return@withContext NetworkResult.Error(response.code, "Failed to fetch equipped cosmetics: ${response.message}")
            }
            val json = JSONObject(body)
            val equipped = EquippedCosmetics(
                themeId = json.optString("themeId", EquippedCosmetics.DEFAULT_THEME_ID),
                pathEffectId = json.optString("pathEffectId", EquippedCosmetics.DEFAULT_PATH_EFFECT_ID),
                avatarFrameId = json.optString("avatarFrameId", EquippedCosmetics.DEFAULT_AVATAR_FRAME_ID)
            )
            NetworkResult.Success(equipped)
        } catch (e: IOException) {
            NetworkResult.Error(-1, "Network error fetching equipped cosmetics: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Error(-2, "Equipped parsing error: ${e.message}")
        }
    }

    override suspend fun updateEquippedCosmetics(
        sessionToken: String,
        equipped: EquippedCosmetics
    ): NetworkResult<EquippedCosmetics> = withContext(ioDispatcher) {
        val url = "${getBaseUrl()}/api/v1/cosmetics/equipped"
        val payload = JSONObject().apply {
            put("themeId", equipped.themeId)
            put("pathEffectId", equipped.pathEffectId)
            put("avatarFrameId", equipped.avatarFrameId)
        }.toString()

        val request = Request.Builder()
            .url(url)
            .post(payload.toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            val body = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                return@withContext NetworkResult.Error(response.code, "Failed to sync equipped cosmetics: ${response.message}")
            }
            val json = JSONObject(body)
            val equippedObj = json.optJSONObject("equipped") ?: json
            val result = EquippedCosmetics(
                themeId = equippedObj.optString("themeId", equipped.themeId),
                pathEffectId = equippedObj.optString("pathEffectId", equipped.pathEffectId),
                avatarFrameId = equippedObj.optString("avatarFrameId", equipped.avatarFrameId)
            )
            NetworkResult.Success(result)
        } catch (e: IOException) {
            NetworkResult.Error(-1, "Network error syncing equipped cosmetics: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Error(-2, "Sync response parsing error: ${e.message}")
        }
    }

    override suspend fun getPublicCosmetics(playerId: String): NetworkResult<EquippedCosmetics> = withContext(ioDispatcher) {
        val url = "${getBaseUrl()}/api/v1/cosmetics/public/$playerId"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Accept", "application/json")
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            val body = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                return@withContext NetworkResult.Error(response.code, "Failed to fetch public player cosmetics: ${response.message}")
            }
            val json = JSONObject(body)
            val result = EquippedCosmetics(
                themeId = json.optString("themeId", EquippedCosmetics.DEFAULT_THEME_ID),
                pathEffectId = json.optString("pathEffectId", EquippedCosmetics.DEFAULT_PATH_EFFECT_ID),
                avatarFrameId = json.optString("avatarFrameId", EquippedCosmetics.DEFAULT_AVATAR_FRAME_ID)
            )
            NetworkResult.Success(result)
        } catch (e: IOException) {
            NetworkResult.Error(-1, "Network error fetching public cosmetics: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Error(-2, "Public cosmetics parsing error: ${e.message}")
        }
    }
}
