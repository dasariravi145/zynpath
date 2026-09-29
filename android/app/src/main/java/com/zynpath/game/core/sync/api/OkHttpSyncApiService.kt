package com.zynpath.game.core.sync.api

import com.zynpath.game.BuildConfig
import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.sync.model.BatchSyncRequest
import com.zynpath.game.core.sync.model.BatchSyncResponse
import com.zynpath.game.core.sync.model.LevelProgressDto
import com.zynpath.game.core.sync.model.SyncOperationResultDto
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
class OkHttpSyncApiService(
    private val okHttpClient: OkHttpClient,
    private val customBaseUrl: String? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SyncApiService {

    @Inject
    constructor(okHttpClient: OkHttpClient) : this(okHttpClient, null, Dispatchers.IO)

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    override fun getBaseUrl(): String {
        return customBaseUrl?.removeSuffix("/") ?: BuildConfig.BACKEND_BASE_URL.removeSuffix("/")
    }

    override suspend fun submitBatch(
        sessionToken: String?,
        request: BatchSyncRequest
    ): NetworkResult<BatchSyncResponse> = withContext(ioDispatcher) {
        val rootJson = JSONObject().apply {
            put("playerId", request.playerId)
            val opsArray = JSONArray()
            request.operations.forEach { op ->
                val opJson = JSONObject().apply {
                    put("operationId", op.operationId)
                    put("operationType", op.operationType)
                    put("resourceIdentity", op.resourceIdentity)
                    put("payloadVersion", op.payloadVersion)
                    put("payloadJson", op.payloadJson)
                    put("clientTimestamp", op.clientTimestamp)
                }
                opsArray.put(opJson)
            }
            put("operations", opsArray)
        }

        val requestBuilder = Request.Builder()
            .url("${getBaseUrl()}/sync/batch")
            .post(rootJson.toString().toRequestBody(jsonMediaType))
            .addHeader("Accept", "application/json")

        if (!sessionToken.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $sessionToken")
        }

        try {
            okHttpClient.newCall(requestBuilder.build()).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }

                val json = JSONObject(bodyString)
                val resultsArray = json.optJSONArray("results") ?: JSONArray()
                val resultsList = mutableListOf<SyncOperationResultDto>()
                for (i in 0 until resultsArray.length()) {
                    val r = resultsArray.getJSONObject(i)
                    resultsList.add(
                        SyncOperationResultDto(
                            operationId = r.getString("operationId"),
                            status = r.optString("status", "SUCCESS"),
                            message = if (r.has("message") && !r.isNull("message")) r.getString("message") else null,
                            serverTimestamp = r.optLong("serverTimestamp", System.currentTimeMillis())
                        )
                    )
                }

                val progressArray = json.optJSONArray("latestProgress") ?: JSONArray()
                val progressList = mutableListOf<LevelProgressDto>()
                for (i in 0 until progressArray.length()) {
                    val p = progressArray.getJSONObject(i)
                    progressList.add(
                        LevelProgressDto(
                            levelId = p.getInt("levelId"),
                            worldId = p.getInt("worldId"),
                            stars = p.getInt("stars"),
                            bestTimeMs = p.getLong("bestTimeMs"),
                            movesCount = p.getInt("movesCount"),
                            isCompleted = p.getBoolean("isCompleted"),
                            bestHintCount = p.optInt("bestHintCount", 0),
                            completedAt = if (p.has("completedAt") && !p.isNull("completedAt")) p.getLong("completedAt") else null
                        )
                    )
                }

                val serverTimestamp = json.optLong("serverTimestamp", System.currentTimeMillis())
                NetworkResult.Success(
                    BatchSyncResponse(
                        results = resultsList,
                        latestProgress = progressList,
                        serverTimestamp = serverTimestamp
                    )
                )
            }
        } catch (e: IOException) {
            NetworkResult.Exception(e)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun fetchRemoteProgress(
        sessionToken: String?,
        playerId: String
    ): NetworkResult<List<LevelProgressDto>> = withContext(ioDispatcher) {
        val requestBuilder = Request.Builder()
            .url("${getBaseUrl()}/sync/progress?playerId=$playerId")
            .get()
            .addHeader("Accept", "application/json")

        if (!sessionToken.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $sessionToken")
        }

        try {
            okHttpClient.newCall(requestBuilder.build()).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }

                val json = JSONObject(bodyString)
                val progressArray = json.optJSONArray("progress") ?: JSONArray()
                val progressList = mutableListOf<LevelProgressDto>()
                for (i in 0 until progressArray.length()) {
                    val p = progressArray.getJSONObject(i)
                    progressList.add(
                        LevelProgressDto(
                            levelId = p.getInt("levelId"),
                            worldId = p.getInt("worldId"),
                            stars = p.getInt("stars"),
                            bestTimeMs = p.getLong("bestTimeMs"),
                            movesCount = p.getInt("movesCount"),
                            isCompleted = p.getBoolean("isCompleted"),
                            bestHintCount = p.optInt("bestHintCount", 0),
                            completedAt = if (p.has("completedAt") && !p.isNull("completedAt")) p.getLong("completedAt") else null
                        )
                    )
                }
                NetworkResult.Success(progressList)
            }
        } catch (e: IOException) {
            NetworkResult.Exception(e)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    private fun parseErrorMessage(body: String, code: Int): String {
        return try {
            val json = JSONObject(body)
            json.optString("message", json.optString("error", "HTTP $code"))
        } catch (_: Exception) {
            if (body.isNotBlank()) body else "HTTP $code"
        }
    }
}
