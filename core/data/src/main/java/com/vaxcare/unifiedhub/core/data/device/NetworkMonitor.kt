package com.vaxcare.unifiedhub.core.data.device

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import androidx.core.content.getSystemService
import com.vaxcare.unifiedhub.core.common.di.ApplicationScope
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.datasource.DeviceNetworkProvider
import com.vaxcare.unifiedhub.core.data.repository.ConnectionStatusRepository
import com.vaxcare.unifiedhub.core.model.ConnectionType
import com.vaxcare.unifiedhub.core.model.ConnectivityStatus
import com.vaxcare.unifiedhub.core.model.NetworkInfo
import com.vaxcare.unifiedhub.core.model.SignalStrengthLevel
import com.vaxcare.unifiedhub.core.network.pinger.QuickPinger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private val ValidTransportTypes = listOf(
    NetworkCapabilities.TRANSPORT_ETHERNET,
    NetworkCapabilities.TRANSPORT_WIFI,
    NetworkCapabilities.TRANSPORT_CELLULAR,
)

class NetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val dispatcherProvider: DispatcherProvider,
    private val quickPinger: QuickPinger,
    private val deviceNetworkProvider: DeviceNetworkProvider,
) : ConnectionStatusRepository {
    val connectivityManager = context.getSystemService<ConnectivityManager>()?.apply {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                updateNetworkInfo(network)
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                updateNetworkInfo(networkCapabilities)
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                updateNetworkInfo(network)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                updateNetworkInfo(network)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                updateNetworkInfo(null)
            }
        }

        registerDefaultNetworkCallback(callback)
    }

    suspend fun updateConnectivityStatus(): ConnectivityStatus =
        getConnectivityStatus().also {
            deviceNetworkProvider.updateNetworkStatus(it)
        }

    /**
     * Sends a GET request to http://gstatic.com/generate_204
     *
     * @return true if a response was received, false if not
     */
    override suspend fun isDeviceOnline() = quickPinger.pingGoogle().isSuccessful

    /**
     * Sends a GET request to https://vhapi.vaxcare.com/index.txt
     *
     * @return true if a response was received, false if not
     */
    override suspend fun isVaxcareReachable() = quickPinger.pingVaxCare().isSuccessful

    fun handleVaxcareResponseCode(code: Int) {
        if (code.isReachableResponseCode()) {
            deviceNetworkProvider.updateNetworkStatus(ConnectivityStatus.CONNECTED)
        } else {
            deviceNetworkProvider.updateNetworkStatus(ConnectivityStatus.CONNECTED_VAXCARE_UNREACHABLE)
        }
    }

    private fun Int.isReachableResponseCode() = (this in 200..299 || this == 401 || this == 403 || this == 423)

    private fun updateNetworkInfo(network: Network?) {
        connectivityManager?.getNetworkCapabilities(network)?.let {
            updateNetworkInfo(it)
        } ?: {
            Timber.e("Attempt to get network capabilities returned `null`.")
            deviceNetworkProvider.update(NetworkInfo())
        }
    }

    private fun updateNetworkInfo(capabilities: NetworkCapabilities) {
        applicationScope.launch(dispatcherProvider.io) {
            parseNetworkInfo(capabilities).let {
                Timber.d("networkData: $it")
                deviceNetworkProvider.update(it)
            }
        }
    }

    private fun fromTransportID(id: Int): ConnectionType =
        when (id) {
            NetworkCapabilities.TRANSPORT_ETHERNET -> ConnectionType.ETHERNET
            NetworkCapabilities.TRANSPORT_WIFI -> ConnectionType.WIFI
            NetworkCapabilities.TRANSPORT_CELLULAR -> ConnectionType.CELLULAR
            else -> ConnectionType.NONE
        }

    private suspend fun getConnectivityStatus(): ConnectivityStatus {
        val (isOnline, isVaxcareReachable) = isDeviceOnline() to isVaxcareReachable()
        return when {
            isOnline && isVaxcareReachable -> ConnectivityStatus.CONNECTED
            isOnline -> ConnectivityStatus.CONNECTED_VAXCARE_UNREACHABLE
            isVaxcareReachable -> ConnectivityStatus.CONNECTED_NO_INTERNET
            else -> ConnectivityStatus.DISCONNECTED
        }
    }

    private suspend fun parseNetworkInfo(capabilities: NetworkCapabilities): NetworkInfo {
        val (securityType, signalStrengthLevel, frequency) =
            // TODO handle cellular "transport info" maybe mock with emulator
            (capabilities.transportInfo as? WifiInfo)?.let { info ->
                val securityType = decodeSecurityType(info)
                val signalStrength = SignalStrengthLevel.fromDbmSignal(info.rssi)
                Triple(securityType, signalStrength, info.frequency)
            } ?: Triple("", SignalStrengthLevel.NO_INTERNET, 0)
        val connectionTypes: List<ConnectionType> = ValidTransportTypes
            .filter(capabilities::hasTransport)
            .map(::fromTransportID)
            .ifEmpty {
                listOf(ConnectionType.NONE)
            }
        return NetworkInfo(
            connectivityStatus = getConnectivityStatus(),
            connectionTypes = connectionTypes,
            signalStrengthLevel = signalStrengthLevel,
            frequency = frequency,
            securityType = securityType
        )
    }

    private fun decodeSecurityType(wifiInfo: WifiInfo): String =
        when (wifiInfo.currentSecurityType) {
            WifiInfo.SECURITY_TYPE_DPP -> "DPP"
            WifiInfo.SECURITY_TYPE_EAP -> "EAP"
            WifiInfo.SECURITY_TYPE_EAP_WPA3_ENTERPRISE -> "WPA3_ENTERPRISE"
            WifiInfo.SECURITY_TYPE_EAP_WPA3_ENTERPRISE_192_BIT -> "WPA3_ENTERPRISE_192_BIT"
            WifiInfo.SECURITY_TYPE_OPEN -> "OPEN"
            WifiInfo.SECURITY_TYPE_OSEN -> "OSEN"
            WifiInfo.SECURITY_TYPE_OWE -> "OWE"
            WifiInfo.SECURITY_TYPE_PASSPOINT_R1_R2 -> "PASSPOINT_R1_R2"
            WifiInfo.SECURITY_TYPE_PASSPOINT_R3 -> "PASSPOINT_R3"
            WifiInfo.SECURITY_TYPE_PSK -> "PSK"
            WifiInfo.SECURITY_TYPE_SAE -> "SAE"
            WifiInfo.SECURITY_TYPE_UNKNOWN -> "UNKNOWN"
            WifiInfo.SECURITY_TYPE_WAPI_CERT -> "WAPI_CERT"
            WifiInfo.SECURITY_TYPE_WAPI_PSK -> "WAPI_PSK"
            WifiInfo.SECURITY_TYPE_WEP -> "WEP"
            else -> wifiInfo.currentSecurityType.toString()
        }
}
