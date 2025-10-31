package com.vaxcare.unifiedhub.core.model

/**
 * Model class holding connectivity information for the device
 */
data class NetworkInfo(
    /**
     * Status of the device's network connectivity
     */
    val connectivityStatus: ConnectivityStatus = ConnectivityStatus.DISCONNECTED,
    /**
     * List of connection types of the device's network capabilities
     */
    val connectionTypes: List<ConnectionType> = listOf(ConnectionType.NONE),
    /**
     * Signal strength extracted from the network's dbmSignal
     */
    val signalStrengthLevel: SignalStrengthLevel = SignalStrengthLevel.NO_INTERNET,
    /**
     * Frequency of the network (i.e. "2427" and "5745" represent 2.4Ghz and 5Ghz respectively)
     */
    val frequency: Int = 0,
    /**
     * Security type of the network (i.e. WEP, PSK, etc)
     */
    val securityType: String = ""
)
