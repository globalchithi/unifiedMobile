package com.vaxcare.unifiedhub.core.domain

import com.vaxcare.unifiedhub.core.data.datasource.DeviceNetworkProvider
import com.vaxcare.unifiedhub.core.model.ConnectivityStatus
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Returns a [Boolean] indicating whether the device can reach VaxCare servers.
 */
class IsConnectedUseCase @Inject constructor(
    private val deviceNetworkProvider: DeviceNetworkProvider
) {
    suspend operator fun invoke(): Boolean =
        deviceNetworkProvider
            .networkInfo
            .first()
            .connectivityStatus == ConnectivityStatus.CONNECTED
}
