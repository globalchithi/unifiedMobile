package com.vaxcare.unifiedhub.core.data.datasource

import com.vaxcare.unifiedhub.core.model.BatteryStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BatteryStatusProvider @Inject constructor() {
    private val _batteryStatus = MutableStateFlow(BatteryStatus())
    val batteryStatus: StateFlow<BatteryStatus> = _batteryStatus

    fun update(status: BatteryStatus) {
        _batteryStatus.update { status }
    }
}
