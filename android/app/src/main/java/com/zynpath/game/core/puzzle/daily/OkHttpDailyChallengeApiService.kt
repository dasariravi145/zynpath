package com.zynpath.game.core.puzzle.daily

import com.zynpath.game.BuildConfig
import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OkHttpDailyChallengeApiService(
    private val okHttpClient: OkHttpClient,
    private val customBaseUrl: String? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : DailyChallengeApiService {

    @Inject
    constructor(okHttpClient: OkHttpClient) : this(okHttpClient, null, Dispatchers.IO)

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private fun getBaseUrl(): String {
        return customBaseUrl?.removeSuffix("/") ?: BuildConfig.BACKEND_BASE_URL.removeSuffix("/")
    }

    override suspend fun getChallenge(dateKey: String?): NetworkResult<DailyChallengeDefinition> = withContext(ioDispatcher) {
        val url = if (dateKey != null && dateKey.isNotBlank()) {
            "${getBaseUrl()}/api/v1/daily/challenge?date=$dateKey"
        } else {
            "${getBaseUrl()}/api/v1/daily/challenge"
        }

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, "Failed to get daily challenge: HTTP ${response.code} $body")
                }
                val json = JSONObject(body)
                val def = parseDailyChallengeDefinition(json)
                NetworkResult.Success(def)
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun startOfficialAttempt(
        sessionToken: String,
        dateKey: String?
    ): NetworkResult<DailyChallengeAttempt> = withContext(ioDispatcher) {
        val payload = JSONObject().apply {
            if (dateKey != null && dateKey.isNotBlank()) {
                put("dateKey", dateKey)
            }
        }

        val request = Request.Builder()
            .url("${getBaseUrl()}/api/v1/daily/attempt/start")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, "Failed to start daily attempt: HTTP ${response.code} $body")
                }
                val json = JSONObject(body)
                val attempt = parseAttempt(json)
                NetworkResult.Success(attempt)
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getActiveAttempt(
        sessionToken: String,
        dateKey: String?
    ): NetworkResult<DailyChallengeAttempt?> = withContext(ioDispatcher) {
        val url = if (dateKey != null && dateKey.isNotBlank()) {
            "${getBaseUrl()}/api/v1/daily/attempt/active?date=$dateKey"
        } else {
            "${getBaseUrl()}/api/v1/daily/attempt/active"
        }

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $sessionToken")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (response.code == 204) {
                    return@withContext NetworkResult.Success(null)
                }
                val body = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, "Failed to get active attempt: HTTP ${response.code} $body")
                }
                val json = JSONObject(body)
                NetworkResult.Success(parseAttempt(json))
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun submitCompletion(
        sessionToken: String,
        attemptId: String,
        challengeId: String,
        puzzleFingerprint: String,
        pathCoordinates: List<String>,
        clientElapsedMs: Long?
    ): NetworkResult<DailyChallengeOnlineResult> = withContext(ioDispatcher) {
        val payload = JSONObject().apply {
            put("attemptId", attemptId)
            put("challengeId", challengeId)
            put("puzzleFingerprint", puzzleFingerprint)
            val pathArray = JSONArray()
            pathCoordinates.forEach { pathArray.put(it) }
            put("pathCoordinates", pathArray)
            if (clientElapsedMs != null) {
                put("clientElapsedMs", clientElapsedMs)
            }
        }

        val request = Request.Builder()
            .url("${getBaseUrl()}/api/v1/daily/attempt/submit")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, "Failed to submit daily completion: HTTP ${response.code} $body")
                }
                val json = JSONObject(body)
                NetworkResult.Success(parseResult(json))
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getPersonalResult(
        sessionToken: String,
        dateKey: String?
    ): NetworkResult<DailyChallengeOnlineResult?> = withContext(ioDispatcher) {
        val url = if (dateKey != null && dateKey.isNotBlank()) {
            "${getBaseUrl()}/api/v1/daily/result?date=$dateKey"
        } else {
            "${getBaseUrl()}/api/v1/daily/result"
        }

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $sessionToken")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (response.code == 204) {
                    return@withContext NetworkResult.Success(null)
                }
                val body = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, "Failed to get personal result: HTTP ${response.code} $body")
                }
                val json = JSONObject(body)
                NetworkResult.Success(parseResult(json))
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getDailyLeaderboard(
        sessionToken: String?,
        dateKey: String?,
        page: Int,
        pageSize: Int
    ): NetworkResult<DailyLeaderboardResponse> = withContext(ioDispatcher) {
        val params = mutableListOf<String>()
        if (dateKey != null && dateKey.isNotBlank()) params.add("date=$dateKey")
        params.add("page=$page")
        params.add("pageSize=$pageSize")
        val queryString = params.joinToString("&")

        val builder = Request.Builder()
            .url("${getBaseUrl()}/api/v1/daily/leaderboard?$queryString")
            .get()

        if (sessionToken != null && sessionToken.isNotBlank()) {
            builder.addHeader("Authorization", "Bearer $sessionToken")
        }

        try {
            okHttpClient.newCall(builder.build()).execute().use { response ->
                val body = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, "Failed to get daily leaderboard: HTTP ${response.code} $body")
                }
                val json = JSONObject(body)
                val entries = mutableListOf<DailyLeaderboardEntry>()
                val arr = json.optJSONArray("entries") ?: JSONArray()
                for (i in 0 until arr.length()) {
                    entries.add(parseLeaderboardEntry(arr.getJSONObject(i)))
                }

                val playerEntry = if (json.has("playerEntry") && !json.isNull("playerEntry")) {
                    parseLeaderboardEntry(json.getJSONObject("playerEntry"))
                } else null

                val resp = DailyLeaderboardResponse(
                    challengeId = json.optString("challengeId", ""),
                    dateKey = json.optString("dateKey", ""),
                    puzzleFingerprint = json.optString("puzzleFingerprint", ""),
                    entries = entries,
                    playerEntry = playerEntry,
                    totalEntries = json.optInt("totalEntries", entries.size),
                    page = json.optInt("page", page),
                    pageSize = json.optInt("pageSize", pageSize)
                )
                NetworkResult.Success(resp)
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun syncProvisional(
        sessionToken: String,
        challengeId: String,
        dateKey: String,
        fingerprint: String,
        solveTimeMs: Long,
        completedAt: Long,
        pathCoordinates: List<String>?,
        movesCount: Int
    ): NetworkResult<DailyChallengeOnlineResult> = withContext(ioDispatcher) {
        val payload = JSONObject().apply {
            put("challengeId", challengeId)
            put("dateKey", dateKey)
            put("puzzleFingerprint", fingerprint)
            put("solveTimeMs", solveTimeMs)
            put("completedAt", completedAt)
            put("movesCount", movesCount)
            if (pathCoordinates != null) {
                val pathArr = JSONArray()
                pathCoordinates.forEach { pathArr.put(it) }
                put("pathCoordinates", pathArr)
            }
        }

        val request = Request.Builder()
            .url("${getBaseUrl()}/api/v1/daily/sync-provisional")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, "Failed to sync provisional result: HTTP ${response.code} $body")
                }
                val json = JSONObject(body)
                NetworkResult.Success(parseResult(json))
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    private fun parseDailyChallengeDefinition(json: JSONObject): DailyChallengeDefinition {
        val challengeId = json.getString("challengeId")
        val dateKey = json.getString("dateKey")
        val challengeVersion = json.optInt("challengeVersion", 1)
        val scheduleVersion = json.optString("scheduleVersion", "1.0.0")
        val puzzleId = json.getString("puzzleId")
        val puzzleVersion = json.optInt("puzzleVersion", 1)
        val puzzleFingerprint = json.getString("puzzleFingerprint")
        val rows = json.getInt("gridRows")
        val cols = json.getInt("gridCols")
        val difficultyTier = json.optString("difficultyTier", "MEDIUM")
        val title = json.optString("title", "Daily Challenge")

        val requiredCells = (0 until rows).flatMap { r ->
            (0 until cols).map { c -> GridPosition(r, c) }
        }.toSet()

        val checkpoints = mutableListOf<NumberedCheckpoint>()
        val cpArray = json.optJSONArray("checkpoints") ?: JSONArray()
        for (i in 0 until cpArray.length()) {
            val cpObj = cpArray.getJSONObject(i)
            checkpoints.add(
                NumberedCheckpoint(
                    number = cpObj.getInt("number"),
                    position = GridPosition(cpObj.getInt("row"), cpObj.getInt("col"))
                )
            )
        }

        val blockedEdges = mutableSetOf<BlockedEdge>()
        val beArray = json.optJSONArray("blockedEdges") ?: JSONArray()
        for (i in 0 until beArray.length()) {
            val beObj = beArray.getJSONObject(i)
            blockedEdges.add(
                BlockedEdge.between(
                    GridPosition(beObj.getInt("row1"), beObj.getInt("col1")),
                    GridPosition(beObj.getInt("row2"), beObj.getInt("col2"))
                )
            )
        }

        val puzzleDef = PuzzleDefinition(
            puzzleId = puzzleId,
            puzzleVersion = puzzleVersion,
            gridDimensions = GridDimensions(rows, cols),
            requiredCells = requiredCells,
            checkpoints = checkpoints,
            blockedEdges = blockedEdges,
            difficultyMetadata = difficultyTier,
            seed = 1000L
        )

        return DailyChallengeDefinition(
            challengeId = challengeId,
            dateKey = dateKey,
            challengeVersion = challengeVersion,
            puzzleId = puzzleId,
            puzzleVersion = puzzleVersion,
            puzzleFingerprint = puzzleFingerprint,
            scheduleVersion = scheduleVersion,
            puzzleDefinition = puzzleDef,
            difficultyTier = difficultyTier,
            title = title
        )
    }

    private fun parseAttempt(json: JSONObject): DailyChallengeAttempt {
        return DailyChallengeAttempt(
            attemptId = json.getString("attemptId"),
            playerId = json.getString("playerId"),
            challengeId = json.getString("challengeId"),
            dateKey = json.getString("dateKey"),
            puzzleFingerprint = json.getString("puzzleFingerprint"),
            startedAt = json.getLong("startedAt"),
            expiresAt = json.getLong("expiresAt"),
            status = json.getString("status")
        )
    }

    private fun parseResult(json: JSONObject): DailyChallengeOnlineResult {
        val statusStr = json.optString("verificationStatus", "PROVISIONAL")
        val status = try {
            DailyVerificationStatus.valueOf(statusStr)
        } catch (e: Exception) {
            DailyVerificationStatus.PROVISIONAL
        }

        return DailyChallengeOnlineResult(
            resultId = json.getString("resultId"),
            attemptId = json.getString("attemptId"),
            playerId = json.getString("playerId"),
            publicZynpathId = json.optString("publicZynpathId", "ZYN-XXXX-0000"),
            displayName = json.optString("displayName", "Pathfinder"),
            avatarId = json.optString("avatarId", "avatar_compass"),
            challengeId = json.getString("challengeId"),
            dateKey = json.getString("dateKey"),
            puzzleFingerprint = json.getString("puzzleFingerprint"),
            solveTimeMs = json.getLong("solveTimeMs"),
            completedAt = json.getLong("completedAt"),
            verificationStatus = status,
            isLeaderboardEligible = json.optBoolean("isLeaderboardEligible", false),
            rank = if (json.has("rank") && !json.isNull("rank")) json.getInt("rank") else null
        )
    }

    private fun parseLeaderboardEntry(json: JSONObject): DailyLeaderboardEntry {
        val statusStr = json.optString("verificationStatus", "SERVER_VALIDATED")
        val status = try {
            DailyVerificationStatus.valueOf(statusStr)
        } catch (e: Exception) {
            DailyVerificationStatus.SERVER_VALIDATED
        }

        return DailyLeaderboardEntry(
            rank = json.getInt("rank"),
            publicZynpathId = json.getString("publicZynpathId"),
            displayName = json.getString("displayName"),
            avatarId = json.optString("avatarId", "avatar_compass"),
            solveTimeMs = json.getLong("solveTimeMs"),
            completedAt = json.getLong("completedAt"),
            verificationStatus = status
        )
    }
}
