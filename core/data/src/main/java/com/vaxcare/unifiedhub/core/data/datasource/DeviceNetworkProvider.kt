package com.vaxcare.unifiedhub.core.data.datasource

import com.vaxcare.unifiedhub.core.model.ConnectivityStatus
import com.vaxcare.unifiedhub.core.model.NetworkInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceNetworkProvider @Inject constructor() {
    private val _networkInfo = MutableStateFlow(NetworkInfo())
    val networkInfo: StateFlow<NetworkInfo> = _networkInfo

    fun update(value: NetworkInfo) {
        _networkInfo.update { value }
    }

    fun updateNetworkStatus(value: ConnectivityStatus) {
        _networkInfo.update { it.copy(connectivityStatus = value) }
    }
}
