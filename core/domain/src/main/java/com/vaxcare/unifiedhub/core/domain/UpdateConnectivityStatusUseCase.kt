package com.vaxcare.unifiedhub.core.domain

import com.vaxcare.unifiedhub.core.data.device.NetworkMonitor
import com.vaxcare.unifiedhub.core.model.ConnectivityStatus
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This updates and returns the current connectivity status of the device by launching two pings.
 * One to google.com, and one to VaxCare's server.
 */
@Singleton
class UpdateConnectivityStatusUseCase @Inject constructor(
    private val networkMonitor: NetworkMonitor
) {
    suspend operator fun invoke(): ConnectivityStatus = networkMonitor.updateConnectivityStatus()
}
