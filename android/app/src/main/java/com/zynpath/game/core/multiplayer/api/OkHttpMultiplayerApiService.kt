package com.zynpath.game.core.multiplayer.api

import com.zynpath.game.BuildConfig
import com.zynpath.game.core.multiplayer.model.FriendDuelInvitationDto
import com.zynpath.game.core.multiplayer.model.FriendDuelInvitationStatus
import com.zynpath.game.core.multiplayer.model.GameMode
import com.zynpath.game.core.multiplayer.model.MatchParticipantDto
import com.zynpath.game.core.multiplayer.model.MatchResultDto
import com.zynpath.game.core.multiplayer.model.MatchSessionSnapshotDto
import com.zynpath.game.core.multiplayer.model.MatchState
import com.zynpath.game.core.multiplayer.model.MatchmakingTicketStatus
import com.zynpath.game.core.multiplayer.model.FriendsArenaMemberDto
import com.zynpath.game.core.multiplayer.model.FriendsArenaRoomDto
import com.zynpath.game.core.multiplayer.model.FriendsArenaMatchParticipantDto
import com.zynpath.game.core.multiplayer.model.FriendsArenaMatchDto
import com.zynpath.game.core.multiplayer.model.PuzzleAssignmentDto
import com.zynpath.game.core.multiplayer.model.RematchState
import com.zynpath.game.core.multiplayer.model.RematchStatusDto
import com.zynpath.game.core.multiplayer.model.SolutionClaimOutcome
import com.zynpath.game.core.multiplayer.model.LeaderboardCategory
import com.zynpath.game.core.multiplayer.model.LeaderboardPeriod
import com.zynpath.game.core.multiplayer.model.LeaderboardEntry
import com.zynpath.game.core.multiplayer.model.LeaderboardResponse
import com.zynpath.game.core.multiplayer.model.MatchHistoryItem
import com.zynpath.game.core.multiplayer.model.MatchHistoryResponse
import com.zynpath.game.core.multiplayer.model.MatchDetails
import com.zynpath.game.core.multiplayer.model.MatchParticipantSummary
import com.zynpath.game.core.multiplayer.model.CompetitiveStats
import com.zynpath.game.core.multiplayer.model.PublicCompetitiveStats
import com.zynpath.game.core.multiplayer.model.PersonalBestRecord
import com.zynpath.game.core.network.NetworkResult
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
class OkHttpMultiplayerApiService(
    private val okHttpClient: OkHttpClient,
    private val customBaseUrl: String? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MultiplayerApiService {

    @Inject
    constructor(okHttpClient: OkHttpClient) : this(okHttpClient, null, Dispatchers.IO)

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    override fun getBaseUrl(): String {
        return customBaseUrl?.removeSuffix("/") ?: BuildConfig.BACKEND_BASE_URL.removeSuffix("/")
    }

    @get:JvmName("internalBaseUrl")
    private val baseUrl: String get() = getBaseUrl()

    override suspend fun enqueueQuickDuel(sessionToken: String): NetworkResult<MatchmakingTicketStatus> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/multiplayer/matchmaking/quick-duel/enqueue")
            .post("{}".toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val obj = JSONObject(bodyString)
                NetworkResult.Success(
                    MatchmakingTicketStatus(
                        ticketId = obj.optString("ticketId").takeIf { it.isNotEmpty() },
                        status = obj.optString("status", "SEARCHING"),
                        matchId = obj.optString("matchId").takeIf { it.isNotEmpty() },
                        enqueuedAt = obj.optLong("enqueuedAt", System.currentTimeMillis()),
                        currentWaitMs = obj.optLong("currentWaitMs", 0L)
                    )
                )
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun cancelQuickDuel(sessionToken: String): NetworkResult<Boolean> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/multiplayer/matchmaking/quick-duel/cancel")
            .post("{}".toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val obj = JSONObject(bodyString)
                NetworkResult.Success(obj.optBoolean("cancelled", true))
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getQuickDuelStatus(sessionToken: String): NetworkResult<MatchmakingTicketStatus> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/multiplayer/matchmaking/quick-duel/status")
            .get()
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val obj = JSONObject(bodyString)
                NetworkResult.Success(
                    MatchmakingTicketStatus(
                        ticketId = obj.optString("ticketId").takeIf { it.isNotEmpty() },
                        status = obj.optString("status", "IDLE"),
                        matchId = obj.optString("matchId").takeIf { it.isNotEmpty() },
                        enqueuedAt = obj.optLong("enqueuedAt", 0L),
                        currentWaitMs = obj.optLong("currentWaitMs", 0L)
                    )
                )
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun createFriendDuel(sessionToken: String, targetPublicZynpathId: String): NetworkResult<MatchSessionSnapshotDto> = withContext(ioDispatcher) {
        val payload = JSONObject().apply {
            put("targetPublicZynpathId", targetPublicZynpathId)
        }
        val request = Request.Builder()
            .url("${getBaseUrl()}/multiplayer/matches/friend-duel")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        executeSnapshotRequest(request)
    }

    override suspend fun createMiniLeague(sessionToken: String, roomName: String, maxParticipants: Int): NetworkResult<MatchSessionSnapshotDto> = withContext(ioDispatcher) {
        val payload = JSONObject().apply {
            put("roomName", roomName)
            put("maxParticipants", maxParticipants)
        }
        val request = Request.Builder()
            .url("${getBaseUrl()}/multiplayer/matches/mini-league")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        executeSnapshotRequest(request)
    }

    override suspend fun getMatchSnapshot(sessionToken: String, matchId: String): NetworkResult<MatchSessionSnapshotDto> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/multiplayer/matches/$matchId")
            .get()
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        executeSnapshotRequest(request)
    }

    override suspend fun markReady(sessionToken: String, matchId: String): NetworkResult<Unit> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/multiplayer/matches/$matchId/ready")
            .post("{}".toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                NetworkResult.Success(Unit)
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun submitClaim(
        sessionToken: String,
        matchId: String,
        puzzleId: String,
        pathCoordinates: List<String>,
        clientDurationMs: Long,
        movesCount: Int
    ): NetworkResult<SolutionClaimOutcome> = withContext(ioDispatcher) {
        val jsonCoords = JSONArray()
        pathCoordinates.forEach { jsonCoords.put(it) }

        val payload = JSONObject().apply {
            put("matchId", matchId)
            put("puzzleId", puzzleId)
            put("pathCoordinates", jsonCoords)
            put("clientReportedSolveTimeMs", clientDurationMs)
            put("movesCount", movesCount)
        }

        val request = Request.Builder()
            .url("${getBaseUrl()}/multiplayer/matches/$matchId/claim")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val obj = JSONObject(bodyString)
                NetworkResult.Success(
                    SolutionClaimOutcome(
                        valid = obj.optBoolean("valid", false),
                        rejectionReason = obj.optString("rejectionReason").takeIf { it.isNotEmpty() }
                    )
                )
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun reconnect(sessionToken: String, matchId: String): NetworkResult<MatchSessionSnapshotDto> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/multiplayer/matches/$matchId/reconnect")
            .post("{}".toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        executeSnapshotRequest(request)
    }

    override suspend fun forfeitMatch(sessionToken: String, matchId: String): NetworkResult<Unit> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/multiplayer/matches/$matchId/forfeit")
            .post("{}".toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                NetworkResult.Success(Unit)
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getMatchResults(sessionToken: String, matchId: String): NetworkResult<List<MatchResultDto>> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/multiplayer/matches/$matchId/results")
            .get()
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val arr = JSONArray(bodyString)
                val list = mutableListOf<MatchResultDto>()
                for (i in 0 until arr.length()) {
                    val r = arr.getJSONObject(i)
                    list.add(parseMatchResult(r))
                }
                NetworkResult.Success(list)
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun sendInvitation(sessionToken: String, targetPublicZynpathId: String): NetworkResult<FriendDuelInvitationDto> = withContext(ioDispatcher) {
        val payload = JSONObject().apply {
            put("targetPublicZynpathId", targetPublicZynpathId)
        }
        val request = Request.Builder()
            .url("${getBaseUrl()}/multiplayer/invitations")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val obj = JSONObject(bodyString)
                NetworkResult.Success(parseFriendDuelInvitation(obj))
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getIncomingInvitations(sessionToken: String): NetworkResult<List<FriendDuelInvitationDto>> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/multiplayer/invitations/incoming")
            .get()
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val arr = JSONArray(bodyString)
                val list = mutableListOf<FriendDuelInvitationDto>()
                for (i in 0 until arr.length()) {
                    list.add(parseFriendDuelInvitation(arr.getJSONObject(i)))
                }
                NetworkResult.Success(list)
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getOutgoingInvitations(sessionToken: String): NetworkResult<List<FriendDuelInvitationDto>> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/multiplayer/invitations/outgoing")
            .get()
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val arr = JSONArray(bodyString)
                val list = mutableListOf<FriendDuelInvitationDto>()
                for (i in 0 until arr.length()) {
                    list.add(parseFriendDuelInvitation(arr.getJSONObject(i)))
                }
                NetworkResult.Success(list)
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getInvitation(sessionToken: String, invitationId: String): NetworkResult<FriendDuelInvitationDto> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/multiplayer/invitations/$invitationId")
            .get()
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val obj = JSONObject(bodyString)
                NetworkResult.Success(parseFriendDuelInvitation(obj))
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun acceptInvitation(sessionToken: String, invitationId: String): NetworkResult<MatchSessionSnapshotDto> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/multiplayer/invitations/$invitationId/accept")
            .post("{}".toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        executeSnapshotRequest(request)
    }

    override suspend fun declineInvitation(sessionToken: String, invitationId: String): NetworkResult<FriendDuelInvitationDto> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/multiplayer/invitations/$invitationId/decline")
            .post("{}".toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val obj = JSONObject(bodyString)
                NetworkResult.Success(parseFriendDuelInvitation(obj))
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun cancelInvitation(sessionToken: String, invitationId: String): NetworkResult<FriendDuelInvitationDto> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/multiplayer/invitations/$invitationId/cancel")
            .post("{}".toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val obj = JSONObject(bodyString)
                NetworkResult.Success(parseFriendDuelInvitation(obj))
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun requestRematch(sessionToken: String, matchId: String): NetworkResult<RematchStatusDto> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/multiplayer/matches/$matchId/rematch")
            .post("{}".toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val obj = JSONObject(bodyString)
                NetworkResult.Success(parseRematchStatus(obj))
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun respondToRematch(sessionToken: String, matchId: String, accept: Boolean): NetworkResult<RematchStatusDto> = withContext(ioDispatcher) {
        val payload = JSONObject().apply {
            put("accept", accept)
        }
        val request = Request.Builder()
            .url("${getBaseUrl()}/multiplayer/matches/$matchId/rematch/respond")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val obj = JSONObject(bodyString)
                NetworkResult.Success(parseRematchStatus(obj))
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getRematchStatus(sessionToken: String, matchId: String): NetworkResult<RematchStatusDto> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/multiplayer/matches/$matchId/rematch")
            .get()
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val obj = JSONObject(bodyString)
                NetworkResult.Success(parseRematchStatus(obj))
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    private fun executeSnapshotRequest(request: Request): NetworkResult<MatchSessionSnapshotDto> {
        return try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val obj = JSONObject(bodyString)
                NetworkResult.Success(parseSnapshot(obj))
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    private fun parseSnapshot(obj: JSONObject): MatchSessionSnapshotDto {
        val puzzleObj = obj.optJSONObject("puzzle") ?: obj.optJSONObject("puzzleAssignment")
        val puzzleDto = puzzleObj?.let { parsePuzzleAssignment(it) }

        val partArr = obj.optJSONArray("participants") ?: JSONArray()
        val participants = mutableListOf<MatchParticipantDto>()
        for (i in 0 until partArr.length()) {
            val p = partArr.getJSONObject(i)
            val isReadyVal = p.optBoolean("ready", p.optBoolean("isReady", false))
            val isConnectedVal = p.optBoolean("connected", p.optBoolean("isConnected", true))
            val isWinnerVal = p.optBoolean("winner", p.optBoolean("isWinner", false))
            val isCompletedVal = (p.has("completedAt") && !p.isNull("completedAt")) || p.optBoolean("completed", p.optBoolean("isCompleted", false))

            participants.add(
                MatchParticipantDto(
                    playerId = p.getString("playerId"),
                    publicZynpathId = p.optString("publicZynpathId", ""),
                    displayName = p.optString("displayName", "Player"),
                    avatarId = p.optString("avatarId", "avatar_compass"),
                    isReady = isReadyVal,
                    isConnected = isConnectedVal,
                    isCompleted = isCompletedVal,
                    isWinner = isWinnerVal,
                    solveTimeMs = if (p.has("solveTimeMs") && !p.isNull("solveTimeMs")) p.getLong("solveTimeMs") else null,
                    finishOrder = if (p.has("finishOrder") && !p.isNull("finishOrder")) p.getInt("finishOrder") else null,
                    coveredCellsCount = p.optInt("coveredCells", p.optInt("coveredCellsCount", 0)),
                    lastCheckpoint = p.optInt("lastCheckpoint", 1)
                )
            )
        }

        val resultsArr = obj.optJSONArray("results")
        val resultsList = mutableListOf<MatchResultDto>()
        if (resultsArr != null) {
            for (i in 0 until resultsArr.length()) {
                resultsList.add(parseMatchResult(resultsArr.getJSONObject(i)))
            }
        }

        return MatchSessionSnapshotDto(
            matchId = obj.getString("matchId"),
            gameMode = GameMode.fromString(obj.optString("gameMode", "QUICK_DUEL")),
            matchState = MatchState.fromString(obj.optString("matchState", "CREATED")),
            hostPlayerId = obj.optString("hostPlayerId", ""),
            puzzle = puzzleDto,
            participants = participants,
            createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
            startedAt = if (obj.has("startedAt") && !obj.isNull("startedAt")) obj.getLong("startedAt") else null,
            endedAt = if (obj.has("endedAt") && !obj.isNull("endedAt")) obj.getLong("endedAt") else null,
            countdownDurationMs = obj.optLong("countdownDurationMs", 3000L),
            serverTimestamp = obj.optLong("serverTimestamp", System.currentTimeMillis()),
            results = resultsList
        )
    }

    private fun parsePuzzleAssignment(it: JSONObject): PuzzleAssignmentDto {
        val reqCellsArr = it.optJSONArray("requiredCells") ?: JSONArray()
        val reqCells = mutableListOf<String>()
        for (i in 0 until reqCellsArr.length()) {
            val item = reqCellsArr.get(i)
            if (item is JSONObject) {
                reqCells.add("${item.getInt("row")},${item.getInt("col")}")
            } else {
                reqCells.add(item.toString())
            }
        }

        val checkpoints = mutableMapOf<Int, String>()
        val cpArr = it.optJSONArray("checkpoints")
        if (cpArr != null) {
            for (i in 0 until cpArr.length()) {
                val cp = cpArr.getJSONObject(i)
                val num = cp.optInt("number", cp.optInt("order", i + 1))
                val pos = if (cp.has("row") && cp.has("col")) {
                    "${cp.getInt("row")},${cp.getInt("col")}"
                } else {
                    cp.optString("position", "")
                }
                checkpoints[num] = pos
            }
        } else {
            val cpObj = it.optJSONObject("checkpoints")
            if (cpObj != null) {
                val keys = cpObj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    checkpoints[key.toInt()] = cpObj.getString(key)
                }
            }
        }

        val edges = mutableListOf<String>()
        val edgesArr = it.optJSONArray("blockedEdges")
        if (edgesArr != null) {
            for (i in 0 until edgesArr.length()) {
                val edgeItem = edgesArr.get(i)
                if (edgeItem is JSONObject) {
                    val r1 = edgeItem.getInt("row1")
                    val c1 = edgeItem.getInt("col1")
                    val r2 = edgeItem.getInt("row2")
                    val c2 = edgeItem.getInt("col2")
                    edges.add("$r1,$c1|$r2,$c2")
                } else {
                    edges.add(edgeItem.toString())
                }
            }
        }

        val w = it.optInt("gridCols", it.optInt("width", 5))
        val h = it.optInt("gridRows", it.optInt("height", 5))

        return PuzzleAssignmentDto(
            puzzleId = it.getString("puzzleId"),
            gameMode = GameMode.fromString(it.optString("gameMode", "QUICK_DUEL")),
            width = w,
            height = h,
            requiredCells = reqCells,
            checkpoints = checkpoints,
            blockedEdges = edges,
            fingerprint = it.optString("fingerprint", "")
        )
    }

    private fun parseMatchResult(r: JSONObject): MatchResultDto {
        return MatchResultDto(
            matchId = r.optString("matchId", ""),
            playerId = r.optString("playerId", ""),
            publicZynpathId = r.optString("publicZynpathId", ""),
            displayName = r.optString("displayName", "Player"),
            completed = r.optBoolean("completed", true),
            solveTimeMs = if (r.has("solveTimeMs") && !r.isNull("solveTimeMs")) r.getLong("solveTimeMs") else null,
            finishOrder = r.optInt("finishOrder", 1),
            isWinner = r.optBoolean("isWinner", r.optBoolean("winner", false)),
            resultStatus = r.optString("resultStatus", if (r.optBoolean("isWinner", false)) "VICTORY" else "DEFEAT")
        )
    }

    private fun parseErrorMessage(bodyString: String, statusCode: Int): String {
        return try {
            val json = JSONObject(bodyString)
            json.optString("message", json.optString("error", "HTTP $statusCode"))
        } catch (_: Exception) {
            "Network error (HTTP $statusCode)"
        }
    }

    private fun parseFriendDuelInvitation(obj: JSONObject): FriendDuelInvitationDto {
        return FriendDuelInvitationDto(
            invitationId = obj.getString("invitationId"),
            inviterPlayerId = obj.getString("inviterPlayerId"),
            inviterPublicZynpathId = obj.optString("inviterPublicZynpathId", ""),
            inviterDisplayName = obj.optString("inviterDisplayName", "Player"),
            inviterAvatarId = obj.optString("inviterAvatarId").takeIf { it.isNotEmpty() },
            recipientPlayerId = obj.getString("recipientPlayerId"),
            recipientPublicZynpathId = obj.optString("recipientPublicZynpathId", ""),
            recipientDisplayName = obj.optString("recipientDisplayName", "Friend"),
            recipientAvatarId = obj.optString("recipientAvatarId").takeIf { it.isNotEmpty() },
            gameMode = GameMode.fromString(obj.optString("gameMode", "FRIEND_DUEL")),
            status = FriendDuelInvitationStatus.fromString(obj.optString("status", "PENDING")),
            createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
            expiresAt = obj.optLong("expiresAt", System.currentTimeMillis() + 60000L),
            matchId = obj.optString("matchId").takeIf { it.isNotEmpty() }
        )
    }

    private fun parseRematchStatus(obj: JSONObject): RematchStatusDto {
        return RematchStatusDto(
            matchId = obj.getString("matchId"),
            requesterPlayerId = obj.optString("requesterPlayerId").takeIf { it.isNotEmpty() },
            status = RematchState.fromString(obj.optString("status", "NOT_REQUESTED")),
            requestedAt = if (obj.has("requestedAt") && !obj.isNull("requestedAt")) obj.getLong("requestedAt") else null,
            expiresAt = if (obj.has("expiresAt") && !obj.isNull("expiresAt")) obj.getLong("expiresAt") else null,
            newMatchId = obj.optString("newMatchId").takeIf { it.isNotEmpty() }
        )
    }

    // --- Mini League REST Operations (Prompt 23) ---

    override suspend fun createMiniLeagueRoom(
        sessionToken: String,
        roomName: String,
        maxParticipants: Int
    ): NetworkResult<com.zynpath.game.core.multiplayer.model.MiniLeagueRoom> = withContext(Dispatchers.IO) {
        val payload = JSONObject().apply {
            put("roomName", roomName)
            put("maxParticipants", maxParticipants)
        }
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/mini-league/rooms")
            .header("Authorization", "Bearer $sessionToken")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()
        executeMiniLeagueRoomRequest(request)
    }

    override suspend fun getMiniLeagueRoom(
        sessionToken: String,
        roomId: String
    ): NetworkResult<com.zynpath.game.core.multiplayer.model.MiniLeagueRoom> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/mini-league/rooms/$roomId")
            .header("Authorization", "Bearer $sessionToken")
            .get()
            .build()
        executeMiniLeagueRoomRequest(request)
    }

    override suspend fun getMiniLeagueRoomByCode(
        sessionToken: String,
        roomCode: String
    ): NetworkResult<com.zynpath.game.core.multiplayer.model.MiniLeagueRoom> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/mini-league/rooms/code/$roomCode")
            .header("Authorization", "Bearer $sessionToken")
            .get()
            .build()
        executeMiniLeagueRoomRequest(request)
    }

    override suspend fun joinMiniLeagueRoom(
        sessionToken: String,
        roomCode: String
    ): NetworkResult<com.zynpath.game.core.multiplayer.model.MiniLeagueRoom> = withContext(Dispatchers.IO) {
        val payload = JSONObject().apply {
            put("roomCode", roomCode)
        }
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/mini-league/rooms/join")
            .header("Authorization", "Bearer $sessionToken")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()
        executeMiniLeagueRoomRequest(request)
    }

    override suspend fun leaveMiniLeagueRoom(
        sessionToken: String,
        roomId: String
    ): NetworkResult<Unit> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/mini-league/rooms/$roomId/leave")
            .header("Authorization", "Bearer $sessionToken")
            .post("{}".toRequestBody(jsonMediaType))
            .build()
        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                NetworkResult.Success(Unit)
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun setMiniLeagueReady(
        sessionToken: String,
        roomId: String,
        ready: Boolean
    ): NetworkResult<com.zynpath.game.core.multiplayer.model.MiniLeagueRoom> = withContext(Dispatchers.IO) {
        val payload = JSONObject().apply {
            put("ready", ready)
        }
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/mini-league/rooms/$roomId/ready")
            .header("Authorization", "Bearer $sessionToken")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()
        executeMiniLeagueRoomRequest(request)
    }

    override suspend fun startMiniLeagueMatch(
        sessionToken: String,
        roomId: String
    ): NetworkResult<MatchSessionSnapshotDto> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/mini-league/rooms/$roomId/start")
            .header("Authorization", "Bearer $sessionToken")
            .post("{}".toRequestBody(jsonMediaType))
            .build()
        executeSnapshotRequest(request)
    }

    override suspend fun inviteToMiniLeague(
        sessionToken: String,
        roomId: String,
        targetPublicZynpathId: String
    ): NetworkResult<com.zynpath.game.core.multiplayer.model.MiniLeagueInvitation> = withContext(Dispatchers.IO) {
        val payload = JSONObject().apply {
            put("targetPublicZynpathId", targetPublicZynpathId)
        }
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/mini-league/rooms/$roomId/invite")
            .header("Authorization", "Bearer $sessionToken")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()
        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val obj = JSONObject(bodyString)
                NetworkResult.Success(parseMiniLeagueInvitation(obj))
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getMiniLeagueIncomingInvitations(
        sessionToken: String
    ): NetworkResult<List<com.zynpath.game.core.multiplayer.model.MiniLeagueInvitation>> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/mini-league/invitations/incoming")
            .header("Authorization", "Bearer $sessionToken")
            .get()
            .build()
        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val arr = JSONArray(bodyString)
                val list = mutableListOf<com.zynpath.game.core.multiplayer.model.MiniLeagueInvitation>()
                for (i in 0 until arr.length()) {
                    list.add(parseMiniLeagueInvitation(arr.getJSONObject(i)))
                }
                NetworkResult.Success(list)
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun respondToMiniLeagueInvitation(
        sessionToken: String,
        invitationId: String,
        accept: Boolean
    ): NetworkResult<com.zynpath.game.core.multiplayer.model.MiniLeagueRoom?> = withContext(Dispatchers.IO) {
        val payload = JSONObject().apply {
            put("accept", accept)
        }
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/mini-league/invitations/$invitationId/respond")
            .header("Authorization", "Bearer $sessionToken")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()
        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                if (!accept || bodyString.isBlank() || bodyString == "null") {
                    NetworkResult.Success(null)
                } else {
                    val obj = JSONObject(bodyString)
                    NetworkResult.Success(parseMiniLeagueRoom(obj))
                }
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun cancelMiniLeagueInvitation(
        sessionToken: String,
        invitationId: String
    ): NetworkResult<com.zynpath.game.core.multiplayer.model.MiniLeagueInvitation> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/mini-league/invitations/$invitationId/cancel")
            .header("Authorization", "Bearer $sessionToken")
            .post("{}".toRequestBody(jsonMediaType))
            .build()
        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val obj = JSONObject(bodyString)
                NetworkResult.Success(parseMiniLeagueInvitation(obj))
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    private fun executeMiniLeagueRoomRequest(request: Request): NetworkResult<com.zynpath.game.core.multiplayer.model.MiniLeagueRoom> {
        return try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val obj = JSONObject(bodyString)
                NetworkResult.Success(parseMiniLeagueRoom(obj))
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    private fun parseMiniLeagueRoom(obj: JSONObject): com.zynpath.game.core.multiplayer.model.MiniLeagueRoom {
        val participantsArr = obj.optJSONArray("participants") ?: JSONArray()
        val participantsList = mutableListOf<com.zynpath.game.core.multiplayer.model.MiniLeagueParticipant>()
        for (i in 0 until participantsArr.length()) {
            participantsList.add(parseMiniLeagueParticipant(participantsArr.getJSONObject(i)))
        }

        return com.zynpath.game.core.multiplayer.model.MiniLeagueRoom(
            roomId = obj.getString("roomId"),
            roomCode = obj.getString("roomCode"),
            roomName = obj.optString("roomName", "Mini League"),
            hostPlayerId = obj.getString("hostPlayerId"),
            state = obj.optString("state", "WAITING_FOR_PLAYERS"),
            maxParticipants = obj.optInt("maxParticipants", 5),
            currentParticipants = obj.optInt("currentParticipants", participantsList.size),
            participants = participantsList,
            matchId = obj.optString("matchId").takeIf { it.isNotEmpty() },
            createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
            expiresAt = obj.optLong("expiresAt", System.currentTimeMillis() + 900000L)
        )
    }

    private fun parseMiniLeagueParticipant(obj: JSONObject): com.zynpath.game.core.multiplayer.model.MiniLeagueParticipant {
        return com.zynpath.game.core.multiplayer.model.MiniLeagueParticipant(
            playerId = obj.getString("playerId"),
            publicZynpathId = obj.optString("publicZynpathId", ""),
            displayName = obj.optString("displayName", "Player"),
            avatarId = obj.optString("avatarId").takeIf { it.isNotEmpty() },
            isHost = obj.optBoolean("isHost", false),
            isReady = obj.optBoolean("isReady", false),
            isConnected = obj.optBoolean("isConnected", true),
            coveredCells = obj.optInt("coveredCells", 0),
            lastCheckpoint = obj.optInt("lastCheckpoint", 1),
            completed = obj.optBoolean("completed", false),
            solveTimeMs = if (obj.has("solveTimeMs") && !obj.isNull("solveTimeMs")) obj.getLong("solveTimeMs") else null,
            finishOrder = if (obj.has("finishOrder") && !obj.isNull("finishOrder")) obj.getInt("finishOrder") else null
        )
    }

    private fun parseMiniLeagueInvitation(obj: JSONObject): com.zynpath.game.core.multiplayer.model.MiniLeagueInvitation {
        return com.zynpath.game.core.multiplayer.model.MiniLeagueInvitation(
            invitationId = obj.getString("invitationId"),
            roomId = obj.getString("roomId"),
            roomCode = obj.getString("roomCode"),
            roomName = obj.optString("roomName", "Mini League"),
            inviterPlayerId = obj.getString("inviterPlayerId"),
            inviterPublicZynpathId = obj.optString("inviterPublicId", obj.optString("inviterPublicZynpathId", "")),
            inviterDisplayName = obj.optString("inviterDisplayName", "Player"),
            recipientPlayerId = obj.getString("recipientPlayerId"),
            recipientPublicZynpathId = obj.optString("recipientPublicId", obj.optString("recipientPublicZynpathId", "")),
            recipientDisplayName = obj.optString("recipientDisplayName", "Friend"),
            status = FriendDuelInvitationStatus.fromString(obj.optString("status", "PENDING")),
            createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
            expiresAt = obj.optLong("expiresAt", System.currentTimeMillis() + 60000L)
        )
    }

    // --- Friends Arena REST Operations (Prompt 18) ---

    override suspend fun createFriendsArenaRoom(
        sessionToken: String,
        idempotencyKey: String?
    ): NetworkResult<FriendsArenaRoomDto> = withContext(ioDispatcher) {
        val payload = JSONObject().apply {
            if (idempotencyKey != null) {
                put("idempotencyKey", idempotencyKey)
            }
        }
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/friends-arena/rooms")
            .header("Authorization", "Bearer $sessionToken")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()
        executeFriendsArenaRoomRequest(request)
    }

    override suspend fun getFriendsArenaRoom(
        sessionToken: String,
        roomId: String
    ): NetworkResult<FriendsArenaRoomDto> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/friends-arena/rooms/$roomId")
            .header("Authorization", "Bearer $sessionToken")
            .get()
            .build()
        executeFriendsArenaRoomRequest(request)
    }

    override suspend fun getFriendsArenaRoomByCode(
        sessionToken: String,
        roomCode: String
    ): NetworkResult<FriendsArenaRoomDto> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/friends-arena/rooms/code/$roomCode")
            .header("Authorization", "Bearer $sessionToken")
            .get()
            .build()
        executeFriendsArenaRoomRequest(request)
    }

    override suspend fun joinFriendsArenaRoom(
        sessionToken: String,
        roomCode: String
    ): NetworkResult<FriendsArenaRoomDto> = withContext(ioDispatcher) {
        val payload = JSONObject().apply {
            put("roomCode", roomCode)
        }
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/friends-arena/rooms/join")
            .header("Authorization", "Bearer $sessionToken")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()
        executeFriendsArenaRoomRequest(request)
    }

    override suspend fun leaveFriendsArenaRoom(
        sessionToken: String,
        roomId: String
    ): NetworkResult<Unit> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/friends-arena/rooms/$roomId/leave")
            .header("Authorization", "Bearer $sessionToken")
            .post("{}".toRequestBody(jsonMediaType))
            .build()
        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                NetworkResult.Success(Unit)
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun startFriendsArenaMatch(
        sessionToken: String,
        roomId: String
    ): NetworkResult<FriendsArenaRoomDto> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/friends-arena/rooms/$roomId/start")
            .header("Authorization", "Bearer $sessionToken")
            .post("{}".toRequestBody(jsonMediaType))
            .build()
        executeFriendsArenaRoomRequest(request)
    }

    private fun executeFriendsArenaRoomRequest(request: Request): NetworkResult<FriendsArenaRoomDto> {
        return try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val obj = JSONObject(bodyString)
                NetworkResult.Success(parseFriendsArenaRoom(obj))
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    private fun parseFriendsArenaRoom(obj: JSONObject): FriendsArenaRoomDto {
        val membersArr = obj.optJSONArray("members") ?: JSONArray()
        val members = mutableListOf<FriendsArenaMemberDto>()
        for (i in 0 until membersArr.length()) {
            val m = membersArr.getJSONObject(i)
            members.add(
                FriendsArenaMemberDto(
                    playerId = m.getString("playerId"),
                    publicZynpathId = m.optString("publicZynpathId", ""),
                    displayName = m.optString("displayName", "Player"),
                    avatarId = m.optString("avatarId", "avatar_compass"),
                    isHost = m.optBoolean("isHost", false),
                    isReady = m.optBoolean("isReady", false),
                    joinedAt = m.optLong("joinedAt", 0L)
                )
            )
        }
        return FriendsArenaRoomDto(
            roomId = obj.getString("roomId"),
            roomCode = obj.getString("roomCode"),
            hostPlayerId = obj.getString("hostPlayerId"),
            status = obj.optString("status", "WAITING"),
            currentOccupancy = obj.optInt("currentOccupancy", members.size),
            maxCapacity = obj.optInt("maxCapacity", 5),
            members = members,
            activeMatchId = if (obj.has("activeMatchId") && !obj.isNull("activeMatchId")) obj.optString("activeMatchId") else null,
            createdAt = obj.optLong("createdAt", 0L),
            version = obj.optLong("version", 1L)
        )
    }

    // --- Friends Arena Match Gameplay (Prompt 19) ---

    override suspend fun getFriendsArenaMatch(
        sessionToken: String,
        matchId: String
    ): NetworkResult<FriendsArenaMatchDto> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/friends-arena/matches/$matchId")
            .header("Authorization", "Bearer $sessionToken")
            .get()
            .build()
        executeFriendsArenaMatchRequest(request)
    }

    override suspend fun getFriendsArenaMatchByRoom(
        sessionToken: String,
        roomId: String
    ): NetworkResult<FriendsArenaMatchDto> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/friends-arena/rooms/$roomId/match")
            .header("Authorization", "Bearer $sessionToken")
            .get()
            .build()
        executeFriendsArenaMatchRequest(request)
    }

    override suspend fun updateFriendsArenaProgress(
        sessionToken: String,
        matchId: String,
        coveredCells: Int,
        lastCheckpoint: Int
    ): NetworkResult<Unit> = withContext(ioDispatcher) {
        val payload = JSONObject().apply {
            put("coveredCells", coveredCells)
            put("lastCheckpoint", lastCheckpoint)
        }
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/friends-arena/matches/$matchId/progress")
            .header("Authorization", "Bearer $sessionToken")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()
        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                NetworkResult.Success(Unit)
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun submitFriendsArenaClaim(
        sessionToken: String,
        matchId: String,
        pathCoordinates: List<String>,
        clientDurationMs: Long
    ): NetworkResult<SolutionClaimOutcome> = withContext(ioDispatcher) {
        val coordsArr = JSONArray()
        pathCoordinates.forEach { coordsArr.put(it) }
        val payload = JSONObject().apply {
            put("pathCoordinates", coordsArr)
            put("clientReportedSolveTimeMs", clientDurationMs)
        }
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/friends-arena/matches/$matchId/claim")
            .header("Authorization", "Bearer $sessionToken")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()
        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val obj = JSONObject(bodyString)
                NetworkResult.Success(
                    SolutionClaimOutcome(
                        valid = obj.optBoolean("valid", false),
                        rejectionReason = obj.optString("rejectionReason").takeIf { it.isNotEmpty() }
                    )
                )
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun forfeitFriendsArenaMatch(
        sessionToken: String,
        matchId: String
    ): NetworkResult<Unit> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/friends-arena/matches/$matchId/forfeit")
            .header("Authorization", "Bearer $sessionToken")
            .post("{}".toRequestBody(jsonMediaType))
            .build()
        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                NetworkResult.Success(Unit)
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun requestFriendsArenaRematch(
        sessionToken: String,
        roomId: String
    ): NetworkResult<FriendsArenaMatchDto> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/friends-arena/rooms/$roomId/rematch")
            .header("Authorization", "Bearer $sessionToken")
            .post("{}".toRequestBody(jsonMediaType))
            .build()
        executeFriendsArenaMatchRequest(request)
    }

    private fun executeFriendsArenaMatchRequest(request: Request): NetworkResult<FriendsArenaMatchDto> {
        return try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val obj = JSONObject(bodyString)
                NetworkResult.Success(parseFriendsArenaMatch(obj))
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    private fun parseFriendsArenaMatch(obj: JSONObject): FriendsArenaMatchDto {
        val puzzleObj = obj.optJSONObject("puzzleAssignment") ?: obj.optJSONObject("puzzle")
        val puzzle = puzzleObj?.let { parsePuzzleAssignment(it) }

        val pArr = obj.optJSONArray("participants") ?: JSONArray()
        val participants = mutableListOf<FriendsArenaMatchParticipantDto>()
        for (i in 0 until pArr.length()) {
            val p = pArr.getJSONObject(i)
            participants.add(
                FriendsArenaMatchParticipantDto(
                    playerId = p.getString("playerId"),
                    publicZynpathId = p.optString("publicZynpathId", ""),
                    displayName = p.optString("displayName", "Player"),
                    avatarId = p.optString("avatarId", "avatar_compass"),
                    isHost = p.optBoolean("isHost", false),
                    isConnected = p.optBoolean("isConnected", true),
                    coveredCells = p.optInt("coveredCells", 0),
                    lastCheckpoint = p.optInt("lastCheckpoint", 1),
                    isCompleted = p.optBoolean("isCompleted", false),
                    solveTimeMs = if (p.has("solveTimeMs") && !p.isNull("solveTimeMs")) p.optLong("solveTimeMs") else null,
                    isWinner = p.optBoolean("isWinner", false),
                    finishOrder = if (p.has("finishOrder") && !p.isNull("finishOrder")) p.optInt("finishOrder") else null
                )
            )
        }

        val rArr = obj.optJSONArray("results") ?: JSONArray()
        val results = mutableListOf<MatchResultDto>()
        for (i in 0 until rArr.length()) {
            results.add(parseMatchResult(rArr.getJSONObject(i)))
        }

        return FriendsArenaMatchDto(
            matchId = obj.getString("matchId"),
            roomId = obj.optString("roomId", ""),
            hostPlayerId = obj.optString("hostPlayerId", ""),
            status = obj.optString("status", "COUNTDOWN"),
            puzzle = puzzle,
            participants = participants,
            countdownStartedAt = if (obj.has("countdownStartedAt") && !obj.isNull("countdownStartedAt")) obj.optLong("countdownStartedAt") else null,
            startedAt = if (obj.has("startedAt") && !obj.isNull("startedAt")) obj.optLong("startedAt") else null,
            endedAt = if (obj.has("endedAt") && !obj.isNull("endedAt")) obj.optLong("endedAt") else null,
            serverTime = obj.optLong("serverTime", System.currentTimeMillis()),
            version = obj.optLong("version", 1L),
            results = results
        )
    }

    // --- Competitive Progression, History, Statistics & Leaderboards (Prompt 24) ---

    override suspend fun getMatchHistory(
        sessionToken: String,
        mode: GameMode?,
        page: Int,
        pageSize: Int
    ): NetworkResult<MatchHistoryResponse> = withContext(ioDispatcher) {
        val query = buildString {
            append("page=").append(page)
            append("&pageSize=").append(pageSize)
            if (mode != null) {
                append("&mode=").append(mode.name)
            }
        }
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/history?$query")
            .header("Authorization", "Bearer $sessionToken")
            .get()
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val obj = JSONObject(bodyString)
                val itemsArr = obj.optJSONArray("items") ?: JSONArray()
                val itemsList = mutableListOf<MatchHistoryItem>()
                for (i in 0 until itemsArr.length()) {
                    itemsList.add(parseMatchHistoryItem(itemsArr.getJSONObject(i)))
                }
                NetworkResult.Success(
                    MatchHistoryResponse(
                        items = itemsList,
                        page = obj.optInt("page", page),
                        pageSize = obj.optInt("pageSize", pageSize),
                        totalItems = obj.optInt("totalItems", itemsList.size),
                        hasMore = obj.optBoolean("hasMore", false)
                    )
                )
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getMatchDetails(
        sessionToken: String,
        matchId: String
    ): NetworkResult<MatchDetails> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/history/$matchId")
            .header("Authorization", "Bearer $sessionToken")
            .get()
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val obj = JSONObject(bodyString)
                val partArr = obj.optJSONArray("participants") ?: JSONArray()
                val partList = mutableListOf<MatchParticipantSummary>()
                for (i in 0 until partArr.length()) {
                    partList.add(parseMatchParticipantSummary(partArr.getJSONObject(i)))
                }
                val myResultObj = obj.optJSONObject("myResult")
                val myResult = myResultObj?.let { parseMatchParticipantSummary(it) }

                NetworkResult.Success(
                    MatchDetails(
                        matchId = obj.getString("matchId"),
                        gameMode = GameMode.fromString(obj.optString("gameMode", "QUICK_DUEL")),
                        puzzleId = obj.optString("puzzleId", ""),
                        puzzleVersion = obj.optInt("puzzleVersion", 1),
                        puzzleFingerprint = obj.optString("puzzleFingerprint", ""),
                        gridRows = obj.optInt("gridRows", 5),
                        gridCols = obj.optInt("gridCols", 5),
                        startedAt = obj.optLong("startedAt", 0L),
                        endedAt = obj.optLong("endedAt", 0L),
                        durationMs = obj.optLong("durationMs", 0L),
                        matchStatus = obj.optString("matchStatus", "COMPLETED"),
                        participantCount = obj.optInt("participantCount", partList.size),
                        participants = partList,
                        myResult = myResult
                    )
                )
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getCompetitiveStats(
        sessionToken: String
    ): NetworkResult<CompetitiveStats> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/stats")
            .header("Authorization", "Bearer $sessionToken")
            .get()
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val obj = JSONObject(bodyString)
                NetworkResult.Success(parseCompetitiveStats(obj))
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getPublicCompetitiveStats(
        publicZynpathId: String
    ): NetworkResult<PublicCompetitiveStats> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/stats/$publicZynpathId")
            .get()
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val obj = JSONObject(bodyString)
                NetworkResult.Success(
                    PublicCompetitiveStats(
                        publicZynpathId = obj.optString("publicZynpathId", publicZynpathId),
                        displayName = obj.optString("displayName", "Player"),
                        avatarId = obj.optString("avatarId", "avatar_default"),
                        totalFinalizedMatches = obj.optInt("totalFinalizedMatches", 0),
                        quickDuelWins = obj.optInt("quickDuelWins", 0),
                        friendDuelWins = obj.optInt("friendDuelWins", 0),
                        miniLeagueWins = obj.optInt("miniLeagueWins", 0),
                        totalCompletions = obj.optInt("totalCompletions", 0)
                    )
                )
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getLeaderboard(
        sessionToken: String?,
        category: LeaderboardCategory,
        period: LeaderboardPeriod,
        page: Int,
        pageSize: Int
    ): NetworkResult<LeaderboardResponse> = withContext(ioDispatcher) {
        val query = "category=${category.name}&period=${period.name}&page=$page&pageSize=$pageSize"
        val reqBuilder = Request.Builder()
            .url("$baseUrl/api/v1/multiplayer/leaderboard?$query")
            .get()
        if (!sessionToken.isNullOrBlank()) {
            reqBuilder.header("Authorization", "Bearer $sessionToken")
        }

        try {
            okHttpClient.newCall(reqBuilder.build()).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, parseErrorMessage(bodyString, response.code))
                }
                val obj = JSONObject(bodyString)
                val entriesArr = obj.optJSONArray("entries") ?: JSONArray()
                val entriesList = mutableListOf<LeaderboardEntry>()
                for (i in 0 until entriesArr.length()) {
                    val e = entriesArr.getJSONObject(i)
                    entriesList.add(
                        LeaderboardEntry(
                            rank = e.getInt("rank"),
                            publicZynpathId = e.optString("publicZynpathId", ""),
                            displayName = e.optString("displayName", "Player"),
                            avatarId = e.optString("avatarId", "avatar_default"),
                            metricValue = e.optLong("metricValue", 0L),
                            formattedValue = e.optString("formattedValue", "")
                        )
                    )
                }
                val catStr = obj.optString("category", category.name)
                val perStr = obj.optString("period", period.name)
                val parsedCat = LeaderboardCategory.values().firstOrNull { it.name.equals(catStr, true) } ?: category
                val parsedPer = LeaderboardPeriod.values().firstOrNull { it.name.equals(perStr, true) } ?: period

                val myRankVal = if (obj.has("myRank") && !obj.isNull("myRank")) obj.getInt("myRank") else null
                val myMetricVal = if (obj.has("myMetricValue") && !obj.isNull("myMetricValue")) obj.getLong("myMetricValue") else null

                NetworkResult.Success(
                    LeaderboardResponse(
                        category = parsedCat,
                        period = parsedPer,
                        entries = entriesList,
                        myRank = myRankVal,
                        myMetricValue = myMetricVal,
                        page = obj.optInt("page", page),
                        pageSize = obj.optInt("pageSize", pageSize),
                        totalEntries = obj.optInt("totalEntries", entriesList.size),
                        hasMore = obj.optBoolean("hasMore", false)
                    )
                )
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    private fun parseMatchHistoryItem(obj: JSONObject): MatchHistoryItem {
        val partArr = obj.optJSONArray("participants") ?: JSONArray()
        val partList = mutableListOf<MatchParticipantSummary>()
        for (i in 0 until partArr.length()) {
            partList.add(parseMatchParticipantSummary(partArr.getJSONObject(i)))
        }
        val myResultObj = obj.optJSONObject("myResult")
        val myResult = myResultObj?.let { parseMatchParticipantSummary(it) }

        return MatchHistoryItem(
            matchId = obj.getString("matchId"),
            gameMode = GameMode.fromString(obj.optString("gameMode", "QUICK_DUEL")),
            puzzleId = obj.optString("puzzleId", ""),
            puzzleVersion = obj.optInt("puzzleVersion", 1),
            puzzleFingerprint = obj.optString("puzzleFingerprint", ""),
            gridRows = obj.optInt("gridRows", 5),
            gridCols = obj.optInt("gridCols", 5),
            startedAt = obj.optLong("startedAt", 0L),
            endedAt = obj.optLong("endedAt", 0L),
            matchStatus = obj.optString("matchStatus", "COMPLETED"),
            participantCount = obj.optInt("participantCount", partList.size),
            participants = partList,
            myResult = myResult
        )
    }

    private fun parseMatchParticipantSummary(obj: JSONObject): MatchParticipantSummary {
        return MatchParticipantSummary(
            playerId = obj.optString("playerId", ""),
            publicZynpathId = obj.optString("publicZynpathId", ""),
            displayName = obj.optString("displayName", "Player"),
            avatarId = obj.optString("avatarId").takeIf { it.isNotEmpty() },
            completed = obj.optBoolean("completed", false),
            solveTimeMs = if (obj.has("solveTimeMs") && !obj.isNull("solveTimeMs")) obj.getLong("solveTimeMs") else null,
            finishOrder = obj.optInt("finishOrder", 0),
            isWinner = obj.optBoolean("isWinner", false),
            outcomeStatus = obj.optString("outcomeStatus", "")
        )
    }

    private fun parseCompetitiveStats(obj: JSONObject): CompetitiveStats {
        val bestsObj = obj.optJSONObject("personalBests")
        val bestsMap = mutableMapOf<String, PersonalBestRecord>()
        if (bestsObj != null) {
            val keys = bestsObj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                val b = bestsObj.getJSONObject(k)
                bestsMap[k] = PersonalBestRecord(
                    gameMode = GameMode.fromString(b.optString("gameMode", k)),
                    puzzleId = b.optString("puzzleId", ""),
                    matchId = b.optString("matchId", ""),
                    solveTimeMs = b.optLong("solveTimeMs", 0L),
                    achievedAt = b.optLong("achievedAt", 0L)
                )
            }
        }

        return CompetitiveStats(
            playerId = obj.optString("playerId", ""),
            publicZynpathId = obj.optString("publicZynpathId", ""),
            displayName = obj.optString("displayName", "Player"),
            totalFinalizedMatches = obj.optInt("totalFinalizedMatches", 0),
            quickDuelMatches = obj.optInt("quickDuelMatches", 0),
            quickDuelWins = obj.optInt("quickDuelWins", 0),
            quickDuelLosses = obj.optInt("quickDuelLosses", 0),
            quickDuelTies = obj.optInt("quickDuelTies", 0),
            quickDuelWinRate = obj.optDouble("quickDuelWinRate", 0.0),
            friendDuelMatches = obj.optInt("friendDuelMatches", 0),
            friendDuelWins = obj.optInt("friendDuelWins", 0),
            friendDuelLosses = obj.optInt("friendDuelLosses", 0),
            friendDuelTies = obj.optInt("friendDuelTies", 0),
            friendDuelWinRate = obj.optDouble("friendDuelWinRate", 0.0),
            miniLeagueParticipations = obj.optInt("miniLeagueParticipations", 0),
            miniLeagueFirstPlaceFinishes = obj.optInt("miniLeagueFirstPlaceFinishes", 0),
            miniLeagueTopThreeFinishes = obj.optInt("miniLeagueTopThreeFinishes", 0),
            miniLeagueAverageFinishPosition = obj.optDouble("miniLeagueAverageFinishPosition", 0.0),
            totalValidatedCompletions = obj.optInt("totalValidatedCompletions", 0),
            personalBests = bestsMap
        )
    }
}

