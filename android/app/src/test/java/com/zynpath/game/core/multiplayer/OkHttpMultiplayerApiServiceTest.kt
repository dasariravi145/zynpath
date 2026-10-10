package com.zynpath.game.core.multiplayer

import com.zynpath.game.core.multiplayer.api.OkHttpMultiplayerApiService
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class OkHttpMultiplayerApiServiceTest {

    private lateinit var okHttpClient: OkHttpClient

    @Before
    fun setup() {
        okHttpClient = OkHttpClient.Builder().build()
    }

    @Test
    fun `getBaseUrl normalizes URLs ending with or without api v1`() {
        val withApiV1 = OkHttpMultiplayerApiService(okHttpClient, "http://192.168.31.164:8080/api/v1")
        assertEquals("http://192.168.31.164:8080/api/v1", withApiV1.getBaseUrl())

        val withApiV1TrailingSlash = OkHttpMultiplayerApiService(okHttpClient, "http://192.168.31.164:8080/api/v1/")
        assertEquals("http://192.168.31.164:8080/api/v1", withApiV1TrailingSlash.getBaseUrl())

        val withoutApiV1 = OkHttpMultiplayerApiService(okHttpClient, "http://192.168.31.164:8080")
        assertEquals("http://192.168.31.164:8080/api/v1", withoutApiV1.getBaseUrl())

        val releaseUrl = OkHttpMultiplayerApiService(okHttpClient, "https://api.zynpath.app/api/v1")
        assertEquals("https://api.zynpath.app/api/v1", releaseUrl.getBaseUrl())
    }

    @Test
    fun `Friends Arena endpoints do not contain duplicate api v1 prefix`() {
        val service = OkHttpMultiplayerApiService(okHttpClient, "http://192.168.31.164:8080/api/v1")
        val base = service.getBaseUrl()

        val createRoomUrl = "$base/multiplayer/friends-arena/rooms"
        val getRoomUrl = "$base/multiplayer/friends-arena/rooms/room_123"
        val joinRoomUrl = "$base/multiplayer/friends-arena/rooms/join"
        val startRoomUrl = "$base/multiplayer/friends-arena/rooms/room_123/start"

        assertEquals("http://192.168.31.164:8080/api/v1/multiplayer/friends-arena/rooms", createRoomUrl)
        assertEquals("http://192.168.31.164:8080/api/v1/multiplayer/friends-arena/rooms/room_123", getRoomUrl)
        assertEquals("http://192.168.31.164:8080/api/v1/multiplayer/friends-arena/rooms/join", joinRoomUrl)
        assertEquals("http://192.168.31.164:8080/api/v1/multiplayer/friends-arena/rooms/room_123/start", startRoomUrl)

        assertFalse(createRoomUrl.contains("/api/v1/api/v1"))
        assertFalse(getRoomUrl.contains("/api/v1/api/v1"))
        assertFalse(joinRoomUrl.contains("/api/v1/api/v1"))
        assertFalse(startRoomUrl.contains("/api/v1/api/v1"))
    }

    @Test
    fun `Mini League and Competitive endpoints do not contain duplicate api v1 prefix`() {
        val service = OkHttpMultiplayerApiService(okHttpClient, "http://192.168.31.164:8080/api/v1")
        val base = service.getBaseUrl()

        val miniLeagueRoomsUrl = "$base/multiplayer/mini-league/rooms"
        val statsUrl = "$base/multiplayer/stats"
        val historyUrl = "$base/multiplayer/history"
        val leaderboardUrl = "$base/multiplayer/leaderboard"

        assertEquals("http://192.168.31.164:8080/api/v1/multiplayer/mini-league/rooms", miniLeagueRoomsUrl)
        assertEquals("http://192.168.31.164:8080/api/v1/multiplayer/stats", statsUrl)
        assertEquals("http://192.168.31.164:8080/api/v1/multiplayer/history", historyUrl)
        assertEquals("http://192.168.31.164:8080/api/v1/multiplayer/leaderboard", leaderboardUrl)

        assertFalse(miniLeagueRoomsUrl.contains("/api/v1/api/v1"))
        assertFalse(statsUrl.contains("/api/v1/api/v1"))
        assertFalse(historyUrl.contains("/api/v1/api/v1"))
        assertFalse(leaderboardUrl.contains("/api/v1/api/v1"))
    }
}
