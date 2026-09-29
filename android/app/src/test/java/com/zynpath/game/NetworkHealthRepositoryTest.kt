package com.zynpath.game

import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.network.api.OkHttpHealthApiService
import com.zynpath.game.core.network.repository.NetworkHealthRepositoryImpl
import com.zynpath.game.feature.diagnostics.ConnectivityDiagnosticsViewModel
import com.zynpath.game.feature.diagnostics.DiagnosticConnectionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkHealthRepositoryTest {

    private lateinit var mockWebServer: MockWebServer
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        mockWebServer.shutdown()
    }

    @Test
    fun `successful health response returns Success with parsed fields`() = runTest {
        val jsonBody = """
            {
                "status": "UP",
                "service": "Zynpath Backend",
                "version": "1.0.0-SNAPSHOT",
                "timestamp": 1727330000000,
                "environment": "dev"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(jsonBody)
        )

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.SECONDS)
            .readTimeout(2, TimeUnit.SECONDS)
            .build()

        val baseUrl = mockWebServer.url("/api/v1").toString().removeSuffix("/")
        val apiService = OkHttpHealthApiService(okHttpClient, baseUrl)
        val repository = NetworkHealthRepositoryImpl(apiService)

        val result = repository.checkHealth()

        assertTrue(result is NetworkResult.Success)
        val data = (result as NetworkResult.Success).data
        assertEquals("UP", data.status)
        assertEquals("Zynpath Backend", data.service)
        assertEquals("1.0.0-SNAPSHOT", data.version)
        assertEquals("dev", data.environment)
        assertEquals(1727330000000L, data.timestamp)
    }

    @Test
    fun `http 503 error returns Error result with status code`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(503)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"status":"DOWN","error":"Database unavailable"}""")
        )

        val okHttpClient = OkHttpClient.Builder().build()
        val baseUrl = mockWebServer.url("/api/v1").toString().removeSuffix("/")
        val apiService = OkHttpHealthApiService(okHttpClient, baseUrl)
        val repository = NetworkHealthRepositoryImpl(apiService)

        val result = repository.checkHealth()

        assertTrue(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assertEquals(503, error.code)
    }

    @Test
    fun `connection refused returns Exception result and does not crash`() = runTest {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(500, TimeUnit.MILLISECONDS)
            .build()

        // Point to an unused local port
        val unreachableUrl = "http://127.0.0.1:65534/api/v1"
        val apiService = OkHttpHealthApiService(okHttpClient, unreachableUrl)
        val repository = NetworkHealthRepositoryImpl(apiService)

        val result = repository.checkHealth()

        assertTrue(result is NetworkResult.Exception)
        val exception = (result as NetworkResult.Exception).throwable
        assertNotNull(exception)
    }

    @Test
    fun `timeout returns Exception result cleanly`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setSocketPolicy(SocketPolicy.NO_RESPONSE)
        )

        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(200, TimeUnit.MILLISECONDS)
            .build()

        val baseUrl = mockWebServer.url("/api/v1").toString().removeSuffix("/")
        val apiService = OkHttpHealthApiService(okHttpClient, baseUrl)
        val repository = NetworkHealthRepositoryImpl(apiService)

        val result = repository.checkHealth()

        assertTrue(result is NetworkResult.Exception)
    }

    @Test
    fun `diagnostics viewModel updates status to UNREACHABLE on connection failure`() = runTest {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(500, TimeUnit.MILLISECONDS)
            .build()

        val unreachableUrl = "http://127.0.0.1:65534/api/v1"
        val apiService = OkHttpHealthApiService(okHttpClient, unreachableUrl, testDispatcher)
        val repository = NetworkHealthRepositoryImpl(apiService)

        val viewModel = ConnectivityDiagnosticsViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(DiagnosticConnectionStatus.UNREACHABLE, state.status)
        assertNotNull(state.lastErrorCategory)
        assertTrue(state.lastErrorCategory!!.contains("Connection Refused") || state.lastErrorCategory!!.contains("Network Error"))
    }
}
