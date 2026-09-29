package com.zynpath.game.core.ads.network

import com.zynpath.game.BuildConfig
import com.zynpath.game.core.ads.model.RewardVerificationStatus
import com.zynpath.game.core.network.NetworkResult
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
class OkHttpAdRewardApiService(
    private val okHttpClient: OkHttpClient,
    private val customBaseUrl: String? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AdRewardApiService {

    @Inject
    constructor(okHttpClient: OkHttpClient) : this(okHttpClient, null, Dispatchers.IO)

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    override fun getBaseUrl(): String {
        return customBaseUrl?.removeSuffix("/") ?: BuildConfig.BACKEND_BASE_URL.removeSuffix("/")
    }

    override suspend fun verifyReward(
        authToken: String?,
        rewardEventId: String,
        transactionId: String?,
        adUnitId: String,
        amount: Int
    ): NetworkResult<ServerVerifyRewardResponse> = withContext(ioDispatcher) {
        val url = "${getBaseUrl()}/api/v1/ads/reward/verify"
        val payload = JSONObject().apply {
            put("rewardEventId", rewardEventId)
            put("transactionId", transactionId ?: "")
            put("adUnitId", adUnitId)
            put("amount", amount)
            put("rewardType", "SOLO_HINT")
        }

        val requestBuilder = Request.Builder()
            .url(url)
            .post(payload.toString().toRequestBody(jsonMediaType))
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")

        if (!authToken.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $authToken")
        }

        try {
            val response = okHttpClient.newCall(requestBuilder.build()).execute()
            val body = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                return@withContext NetworkResult.Error(response.code, "Reward verification rejected: ${response.message}")
            }

            val json = JSONObject(body)
            val result = ServerVerifyRewardResponse(
                status = RewardVerificationStatus.valueOf(json.optString("status", "LOCAL_CONFIRMED")),
                granted = json.optBoolean("granted", false),
                rewardEventId = json.optString("rewardEventId", rewardEventId),
                newRewardedCredits = json.optInt("newRewardedCredits", 0),
                totalAvailableHints = json.optInt("totalAvailableHints", 0),
                message = json.optString("message", "")
            )
            NetworkResult.Success(result)
        } catch (e: IOException) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getBalance(authToken: String?): NetworkResult<ServerHintBalanceResponse> = withContext(ioDispatcher) {
        val url = "${getBaseUrl()}/api/v1/ads/reward/balance"
        val requestBuilder = Request.Builder()
            .url(url)
            .get()
            .addHeader("Accept", "application/json")

        if (!authToken.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $authToken")
        }

        try {
            val response = okHttpClient.newCall(requestBuilder.build()).execute()
            val body = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                return@withContext NetworkResult.Error(response.code, "Failed to retrieve hint balance: ${response.message}")
            }

            val json = JSONObject(body)
            val result = ServerHintBalanceResponse(
                freeHintsRemaining = json.optInt("freeHintsRemaining", 3),
                rewardedCredits = json.optInt("rewardedCredits", 0),
                totalAvailable = json.optInt("totalAvailable", 3),
                isPremium = json.optBoolean("isPremium", false),
                dailyRewardedAdsRemaining = json.optInt("dailyRewardedAdsRemaining", 5)
            )
            NetworkResult.Success(result)
        } catch (e: IOException) {
            NetworkResult.Exception(e)
        }
    }
}
