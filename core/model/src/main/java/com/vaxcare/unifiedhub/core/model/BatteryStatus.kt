package com.vaxcare.unifiedhub.core.model

data class BatteryStatus(
    val percent: Int = -1,
    val isCharging: Boolean = false,
    val isPowerSaveModeEnabled: Boolean = false,
)
