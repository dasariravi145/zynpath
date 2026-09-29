package com.zynpath.game.core.premium.network

import com.zynpath.game.BuildConfig
import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.premium.model.EntitlementStatus
import com.zynpath.game.core.premium.model.PremiumEntitlement
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OkHttpSubscriptionApiService(
    private val okHttpClient: OkHttpClient,
    private val customBaseUrl: String? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SubscriptionApiService {

    @Inject
    constructor(okHttpClient: OkHttpClient) : this(okHttpClient, null, Dispatchers.IO)

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    override fun getBaseUrl(): String {
        return customBaseUrl?.removeSuffix("/") ?: BuildConfig.BACKEND_BASE_URL.removeSuffix("/")
    }

    override suspend fun getEntitlement(sessionToken: String): NetworkResult<PremiumEntitlement> = withContext(ioDispatcher) {
        val url = "${getBaseUrl()}/api/v1/subscription/entitlement"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        executeEntitlementRequest(request)
    }

    override suspend fun verifyPurchase(
        sessionToken: String,
        request: SubscriptionVerificationRequest
    ): NetworkResult<PremiumEntitlement> = withContext(ioDispatcher) {
        val url = "${getBaseUrl()}/api/v1/subscription/verify"
        val payload = JSONObject().apply {
            put("purchaseToken", request.purchaseToken)
            put("productId", request.productId)
            put("basePlanId", request.basePlanId)
            request.obfuscatedAccountId?.let { put("obfuscatedAccountId", it) }
        }.toString()

        val httpRequest = Request.Builder()
            .url(url)
            .post(payload.toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()

        executeEntitlementRequest(httpRequest)
    }

    override suspend fun refreshEntitlement(sessionToken: String): NetworkResult<PremiumEntitlement> = withContext(ioDispatcher) {
        val url = "${getBaseUrl()}/api/v1/subscription/refresh"
        val httpRequest = Request.Builder()
            .url(url)
            .post("{}".toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()

        executeEntitlementRequest(httpRequest)
    }

    override suspend fun restoreSubscriptions(
        sessionToken: String,
        request: SubscriptionRestoreRequest
    ): NetworkResult<SubscriptionRestoreResponse> = withContext(ioDispatcher) {
        val url = "${getBaseUrl()}/api/v1/subscription/restore"
        val payload = JSONObject().apply {
            val arr = JSONArray()
            request.purchaseTokens.forEach { arr.put(it) }
            put("purchaseTokens", arr)
        }.toString()

        val httpRequest = Request.Builder()
            .url(url)
            .post(payload.toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(httpRequest).execute().use { response ->
                val bodyString = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    val errorMsg = try {
                        JSONObject(bodyString).optString("message", "HTTP ${response.code}")
                    } catch (e: Exception) {
                        "HTTP ${response.code}: ${response.message}"
                    }
                    return@withContext NetworkResult.Error(code = response.code, message = errorMsg)
                }

                val json = JSONObject(bodyString)
                val activeEntitlementJson = json.optJSONObject("activeEntitlement")
                val activeEntitlement = activeEntitlementJson?.let { parseEntitlementJson(it) }
                val restoredCount = json.optInt("restoredCount", 0)
                val message = json.optString("message", "")

                NetworkResult.Success(
                    SubscriptionRestoreResponse(
                        activeEntitlement = activeEntitlement,
                        restoredCount = restoredCount,
                        message = message
                    )
                )
            }
        } catch (e: IOException) {
            NetworkResult.Exception(e)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    private fun executeEntitlementRequest(request: Request): NetworkResult<PremiumEntitlement> {
        return try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    val errorMsg = try {
                        JSONObject(bodyString).optString("message", "HTTP ${response.code}")
                    } catch (e: Exception) {
                        "HTTP ${response.code}: ${response.message}"
                    }
                    return NetworkResult.Error(code = response.code, message = errorMsg)
                }

                val json = JSONObject(bodyString)
                val entitlement = parseEntitlementJson(json)
                NetworkResult.Success(entitlement)
            }
        } catch (e: IOException) {
            NetworkResult.Exception(e)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    companion object {
        fun parseEntitlementJson(json: JSONObject): PremiumEntitlement {
            val statusStr = json.optString("status", "FREE")
            val status = try {
                EntitlementStatus.valueOf(statusStr)
            } catch (e: Exception) {
                EntitlementStatus.UNKNOWN
            }

            val featuresArr = json.optJSONArray("unlockedFeatureKeys")
            val features = mutableListOf<String>()
            if (featuresArr != null) {
                for (i in 0 until featuresArr.length()) {
                    features.add(featuresArr.getString(i))
                }
            }

            return PremiumEntitlement(
                accountId = json.optString("accountId").ifEmpty { null },
                status = status,
                productId = json.optString("productId").ifEmpty { null },
                basePlanId = json.optString("basePlanId").ifEmpty { null },
                currentPeriodEndMs = json.optLong("expiresAt", 0L),
                lastVerifiedAtMs = json.optLong("lastVerifiedAt", System.currentTimeMillis()),
                isAutoRenewing = json.optBoolean("active", false),
                isCachedOffline = false,
                unlockedFeatureKeys = features
            )
        }
    }
}
