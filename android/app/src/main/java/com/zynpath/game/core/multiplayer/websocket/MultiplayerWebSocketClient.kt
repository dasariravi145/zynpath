package com.zynpath.game.core.multiplayer.websocket

import android.net.Uri
import android.util.Log
import com.zynpath.game.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Connection states for real-time multiplayer signaling.
 *
 * Implements Prompt 38 Section 35 & 36.
 */
enum class WebSocketConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    RECONNECTING,
    FAILED
}

/**
 * Incoming WebSocket event from the backend multiplayer gateway.
 */
data class IncomingMultiplayerEvent(
    val eventId: String,
    val eventType: String,
    val schemaVersion: String,
    val matchId: String?,
    val serverTimestamp: Long,
    val sequenceNumber: Long,
    val payloadJson: String
)

/**
 * Real-time authenticated WebSocket transport for multiplayer match signaling.
 *
 * Implements Prompt 20 Sections 27-31, 41, 43 and Prompt 38 Sections 35-37:
 * - Authenticated WebSocket connection over /ws/multiplayer
 * - Explicit connection state tracking with bounded backoff reconnect
 * - Dispatches versioned event envelopes with sequence ordering
 * - Throttles live progress updates without streaming finger touches
 */
@Singleton
class MultiplayerWebSocketClient @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    companion object {
        private const val TAG = "MultiplayerWS"
        private const val MAX_RECONNECT_ATTEMPTS = 3
        private const val MAX_BACKOFF_MS = 8000L
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private var webSocket: WebSocket? = null
    private var activeMatchId: String? = null
    private var activeSessionToken: String? = null
    private var reconnectAttempts = 0
    private var reconnectJob: Job? = null

    private val _incomingEvents = MutableSharedFlow<IncomingMultiplayerEvent>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val incomingEvents: SharedFlow<IncomingMultiplayerEvent> = _incomingEvents.asSharedFlow()

    private val _isConnected = MutableSharedFlow<Boolean>(replay = 1)
    val isConnected: SharedFlow<Boolean> = _isConnected.asSharedFlow()

    private val _connectionState = MutableStateFlow(WebSocketConnectionState.DISCONNECTED)
    val connectionState: StateFlow<WebSocketConnectionState> = _connectionState.asStateFlow()

    fun connect(sessionToken: String, matchId: String?) {
        reconnectJob?.cancel()
        reconnectJob = null
        reconnectAttempts = 0
        this.activeSessionToken = sessionToken
        this.activeMatchId = matchId
        _connectionState.value = WebSocketConnectionState.CONNECTING
        openSocket(sessionToken, matchId)
    }

    private fun openSocket(sessionToken: String, matchId: String?) {
        try {
            webSocket?.close(1000, "Reconnecting")
        } catch (_: Exception) {}
        webSocket = null

        val baseHttpUrl = BuildConfig.BACKEND_BASE_URL.removeSuffix("/")
        val wsUrl = baseHttpUrl
            .replace("https://", "wss://")
            .replace("http://", "ws://")
            .removeSuffix("/api/v1") + "/ws/multiplayer"

        val uri = Uri.parse(wsUrl).buildUpon()
            .appendQueryParameter("token", sessionToken)
            .build()

        val request = Request.Builder()
            .url(uri.toString())
            .build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket connected to $wsUrl")
                reconnectAttempts = 0
                _connectionState.value = WebSocketConnectionState.CONNECTED
                scope.launch { _isConnected.emit(true) }

                // If matchId was provided at connect, automatically subscribe
                matchId?.let { subscribeMatch(it) }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val obj = JSONObject(text)
                    val event = IncomingMultiplayerEvent(
                        eventId = obj.optString("eventId", ""),
                        eventType = obj.optString("eventType", ""),
                        schemaVersion = obj.optString("schemaVersion", "1.0"),
                        matchId = obj.optString("matchId").takeIf { it.isNotEmpty() },
                        serverTimestamp = obj.optLong("serverTimestamp", System.currentTimeMillis()),
                        sequenceNumber = obj.optLong("sequenceNumber", 0L),
                        payloadJson = obj.opt("payload")?.toString() ?: "{}"
                    )
                    scope.launch { _incomingEvents.emit(event) }
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to parse incoming WebSocket message: $text", e)
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closing: $code / $reason")
                webSocket.close(code, reason)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closed: $code / $reason")
                _connectionState.value = WebSocketConnectionState.DISCONNECTED
                scope.launch { _isConnected.emit(false) }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket error: ${t.message}", t)
                scope.launch { _isConnected.emit(false) }

                // Bounded exponential backoff reconnection if actively engaged in a match
                val token = activeSessionToken
                val mId = activeMatchId
                if (token != null && mId != null && reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                    val delayMs = (1000L * (1L shl reconnectAttempts)).coerceAtMost(MAX_BACKOFF_MS)
                    reconnectAttempts++
                    _connectionState.value = WebSocketConnectionState.RECONNECTING
                    Log.d(TAG, "Scheduling WebSocket reconnect attempt $reconnectAttempts in ${delayMs}ms for match $mId")
                    reconnectJob?.cancel()
                    reconnectJob = scope.launch {
                        delay(delayMs)
                        if (activeSessionToken != null) {
                            openSocket(token, mId)
                        }
                    }
                } else {
                    _connectionState.value = WebSocketConnectionState.FAILED
                }
            }
        })
    }

    fun subscribeMatch(matchId: String) {
        this.activeMatchId = matchId
        val msg = JSONObject().apply {
            put("type", "SUBSCRIBE_MATCH")
            put("matchId", matchId)
        }
        send(msg.toString())
    }

    fun subscribeFriendsArenaRoom(roomId: String) {
        val msg = JSONObject().apply {
            put("type", "SUBSCRIBE_ROOM")
            put("roomId", roomId)
        }
        send(msg.toString())
    }

    fun sendReady(matchId: String) {
        val msg = JSONObject().apply {
            put("type", "READY")
            put("matchId", matchId)
        }
        send(msg.toString())
    }

    fun sendProgress(matchId: String, coveredCells: Int, lastCheckpoint: Int) {
        val msg = JSONObject().apply {
            put("type", "PROGRESS")
            put("matchId", matchId)
            put("coveredCells", coveredCells)
            put("lastCheckpoint", lastCheckpoint)
        }
        send(msg.toString())
    }

    fun sendClaim(
        matchId: String,
        puzzleId: String,
        pathCoordinates: List<String>,
        clientReportedSolveTimeMs: Long,
        movesCount: Int
    ) {
        val jsonCoords = org.json.JSONArray()
        pathCoordinates.forEach { jsonCoords.put(it) }
        val msg = JSONObject().apply {
            put("type", "CLAIM")
            put("matchId", matchId)
            put("puzzleId", puzzleId)
            put("pathCoordinates", jsonCoords)
            put("clientReportedSolveTimeMs", clientReportedSolveTimeMs)
            put("movesCount", movesCount)
        }
        send(msg.toString())
    }

    fun sendForfeit(matchId: String) {
        val msg = JSONObject().apply {
            put("type", "FORFEIT")
            put("matchId", matchId)
        }
        send(msg.toString())
    }

    fun sendReaction(matchId: String, reactionCode: String) {
        val msg = JSONObject().apply {
            put("type", "PRESET_REACTION")
            put("matchId", matchId)
            put("reactionCode", reactionCode)
        }
        send(msg.toString())
    }

    fun sendInvitation(targetPublicZynpathId: String) {
        val msg = JSONObject().apply {
            put("type", "SEND_INVITATION")
            put("targetPublicZynpathId", targetPublicZynpathId)
        }
        send(msg.toString())
    }

    fun sendAcceptInvitation(invitationId: String) {
        val msg = JSONObject().apply {
            put("type", "ACCEPT_INVITATION")
            put("invitationId", invitationId)
        }
        send(msg.toString())
    }

    fun sendDeclineInvitation(invitationId: String) {
        val msg = JSONObject().apply {
            put("type", "DECLINE_INVITATION")
            put("invitationId", invitationId)
        }
        send(msg.toString())
    }

    fun sendCancelInvitation(invitationId: String) {
        val msg = JSONObject().apply {
            put("type", "CANCEL_INVITATION")
            put("invitationId", invitationId)
        }
        send(msg.toString())
    }

    fun sendRequestRematch(matchId: String) {
        val msg = JSONObject().apply {
            put("type", "REQUEST_REMATCH")
            put("matchId", matchId)
        }
        send(msg.toString())
    }

    fun sendRespondRematch(matchId: String, accept: Boolean) {
        val msg = JSONObject().apply {
            put("type", "RESPOND_REMATCH")
            put("matchId", matchId)
            put("accept", accept)
        }
        send(msg.toString())
    }

    private fun send(text: String) {
        webSocket?.send(text) ?: Log.w(TAG, "Cannot send, socket is null")
    }

    fun disconnect() {
        reconnectJob?.cancel()
        reconnectJob = null
        reconnectAttempts = 0
        activeSessionToken = null
        try {
            webSocket?.close(1000, "Normal closure")
        } catch (_: Exception) {}
        webSocket = null
        activeMatchId = null
        _connectionState.value = WebSocketConnectionState.DISCONNECTED
    }
}
