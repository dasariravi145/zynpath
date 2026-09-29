package com.zynpath.game.feature.diagnostics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.network.repository.NetworkHealthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class DiagnosticConnectionStatus {
    IDLE,
    CHECKING,
    CONNECTED,
    UNREACHABLE
}

data class ConnectionPreset(
    val id: String,
    val label: String,
    val url: String,
    val description: String
)

data class DiagnosticsUiState(
    val configuredBaseUrl: String = "",
    val activeTestUrl: String = "",
    val status: DiagnosticConnectionStatus = DiagnosticConnectionStatus.IDLE,
    val serviceName: String? = null,
    val serviceVersion: String? = null,
    val environment: String? = null,
    val latencyMs: Long? = null,
    val lastErrorCategory: String? = null,
    val troubleshootingTip: String? = null,
    val presets: List<ConnectionPreset> = listOf(
        ConnectionPreset(
            id = "lan",
            label = "Wi-Fi LAN",
            url = "http://192.168.31.164:8080/api/v1",
            description = "For physical device on same Wi-Fi router (192.168.31.164:8080)"
        ),
        ConnectionPreset(
            id = "usb",
            label = "USB Reverse",
            url = "http://127.0.0.1:8080/api/v1",
            description = "For physical device over USB ('adb reverse tcp:8080 tcp:8080')"
        ),
        ConnectionPreset(
            id = "emulator",
            label = "Emulator",
            url = "http://10.0.2.2:8080/api/v1",
            description = "For Android Studio AVD emulator loopback"
        )
    )
)

@HiltViewModel
class ConnectivityDiagnosticsViewModel @Inject constructor(
    private val networkHealthRepository: NetworkHealthRepository
) : ViewModel() {

    private val defaultBaseUrl = networkHealthRepository.getBaseUrl()

    private val _uiState = MutableStateFlow(
        DiagnosticsUiState(
            configuredBaseUrl = defaultBaseUrl,
            activeTestUrl = defaultBaseUrl
        )
    )
    val uiState: StateFlow<DiagnosticsUiState> = _uiState.asStateFlow()

    init {
        checkConnectivity()
    }

    fun selectPreset(preset: ConnectionPreset) {
        _uiState.value = _uiState.value.copy(activeTestUrl = preset.url)
        networkHealthRepository.setCustomBaseUrl(preset.url)
        checkConnectivity(preset.url)
    }

    fun setCustomUrl(url: String) {
        val trimmed = url.trim()
        _uiState.value = _uiState.value.copy(activeTestUrl = trimmed)
        networkHealthRepository.setCustomBaseUrl(trimmed)
    }

    fun checkConnectivity(targetUrl: String? = null) {
        val testUrl = (targetUrl ?: _uiState.value.activeTestUrl.ifEmpty { _uiState.value.configuredBaseUrl }).trim()

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                status = DiagnosticConnectionStatus.CHECKING,
                activeTestUrl = testUrl,
                lastErrorCategory = null,
                troubleshootingTip = null
            )

            val startTime = System.currentTimeMillis()
            when (val result = networkHealthRepository.checkHealth(testUrl)) {
                is NetworkResult.Success -> {
                    val elapsed = System.currentTimeMillis() - startTime
                    _uiState.value = _uiState.value.copy(
                        status = DiagnosticConnectionStatus.CONNECTED,
                        serviceName = result.data.service,
                        serviceVersion = result.data.version,
                        environment = result.data.environment,
                        latencyMs = elapsed,
                        lastErrorCategory = null,
                        troubleshootingTip = null
                    )
                }
                is NetworkResult.Error -> {
                    val tip = when (result.code) {
                        404 -> "Endpoint /api/v1/health not found on server. Verify Spring Boot context-path or controller route."
                        500, 502, 503 -> "Server error (${result.code}). Check backend application terminal logs."
                        else -> "Unexpected HTTP response from server."
                    }
                    _uiState.value = _uiState.value.copy(
                        status = DiagnosticConnectionStatus.UNREACHABLE,
                        lastErrorCategory = "HTTP ${result.code}: ${result.message}",
                        troubleshootingTip = tip
                    )
                }
                is NetworkResult.Exception -> {
                    val throwable = result.throwable
                    val msg = throwable.localizedMessage ?: throwable.message ?: ""
                    val isCleartextBlocked = msg.contains("Cleartext", ignoreCase = true) ||
                            msg.contains("CLEARTEXT_COMMUNICATION_NOT_PERMITTED", ignoreCase = true)

                    val (category, tip) = when {
                        isCleartextBlocked -> Pair(
                            "Cleartext Blocked by Android Security",
                            "Ensure network_security_config.xml permits cleartext HTTP in debug builds."
                        )
                        throwable is java.net.ConnectException -> Pair(
                            "Connection Refused: Target server not listening or blocked",
                            "1) Ensure backend is running ('mvn spring-boot:run').\n2) Check Windows firewall permits port 8080.\n3) For USB testing, run 'adb reverse tcp:8080 tcp:8080' and select USB Reverse."
                        )
                        throwable is java.net.SocketTimeoutException -> Pair(
                            "Connection Timeout (> 5000ms)",
                            "Packets dropped. Verify phone and laptop are on the same 2.4/5GHz Wi-Fi and AP isolation is off, or use USB Reverse."
                        )
                        throwable is java.net.UnknownHostException -> Pair(
                            "DNS / Unknown Host (${throwable.message})",
                            "Host cannot be resolved. Check IP address spelling."
                        )
                        else -> Pair(
                            "Network Error: ${msg.ifEmpty { throwable.javaClass.simpleName }}",
                            "Check network connection and server state."
                        )
                    }

                    _uiState.value = _uiState.value.copy(
                        status = DiagnosticConnectionStatus.UNREACHABLE,
                        lastErrorCategory = category,
                        troubleshootingTip = tip
                    )
                }
            }
        }
    }
}

