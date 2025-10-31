package com.vaxcare.unifiedhub.core.model

/**
 * ConnectionType enum for NetworkData. This is determined from the ConnectionManager capabilities
 */
enum class ConnectionType {
    ETHERNET,
    WIFI,
    CELLULAR,
    NONE
}
