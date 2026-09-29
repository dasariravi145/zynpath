package com.zynpath.game.core.sync.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface for observing device network connectivity state.
 *
 * Implements Prompt 35 Section 14:
 * - Detects active network availability with internet capability.
 * - Connectivity indicates transport availability, not guaranteed server reachability.
 */
interface NetworkConnectivityMonitor {
    val isConnected: StateFlow<Boolean>
}

@Singleton
class NetworkConnectivityMonitorImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NetworkConnectivityMonitor {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    private val _isConnected = MutableStateFlow(checkInitialConnectivity())
    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    init {
        registerNetworkCallback()
    }

    private fun checkInitialConnectivity(): Boolean {
        val cm = connectivityManager ?: return false
        val activeNetwork = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun registerNetworkCallback() {
        val cm = connectivityManager ?: return
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        try {
            cm.registerNetworkCallback(
                request,
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        _isConnected.value = true
                    }

                    override fun onLost(network: Network) {
                        _isConnected.value = checkInitialConnectivity()
                    }

                    override fun onCapabilitiesChanged(
                        network: Network,
                        networkCapabilities: NetworkCapabilities
                    ) {
                        val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        _isConnected.value = hasInternet
                    }
                }
            )
        } catch (_: Exception) {
            // Fallback: leave initial state intact if registration is restricted
        }
    }
}
