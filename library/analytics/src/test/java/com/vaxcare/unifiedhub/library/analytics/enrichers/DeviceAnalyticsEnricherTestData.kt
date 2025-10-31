package com.vaxcare.unifiedhub.library.analytics.enrichers

import com.vaxcare.unifiedhub.core.model.ConnectivityStatus
import com.vaxcare.unifiedhub.core.model.SignalStrengthLevel
import com.vaxcare.unifiedhub.library.analytics.ApplicationName
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps

val signalStrengthLevel = SignalStrengthLevel.GREAT
val frequency = 1
val securityType = "security_protocol"
val connectivityStatus = ConnectivityStatus.CONNECTED
val networkProps: AnalyticsProps = mapOf(
    "dbmSignalLevel" to signalStrengthLevel.toString(),
    "networkFrequency" to frequency.toString(),
    "networkSecurity" to securityType,
    "networkStatus" to connectivityStatus.toString()
)

val percent = 80
val isCharging = true
val isPowerSaveModeEnabled = false
val batteryProps: AnalyticsProps = mapOf(
    "batteryPercentage" to percent.toString(),
    "charging" to isCharging.toString(),
    "powerSaveMode" to isPowerSaveModeEnabled.toString()
)

val modelType = ApplicationName.MODEL_TYPE
val serialNumber = "1234"
val imei = "abc"
val iccid = "efg"
val deviceProps: AnalyticsProps = mapOf(
    "modelType" to modelType,
    "serialNumber" to serialNumber,
    "imei" to imei,
    "iccid" to iccid
)

val configurationProps: AnalyticsProps = mapOf(
    "orientation" to "180"
)
