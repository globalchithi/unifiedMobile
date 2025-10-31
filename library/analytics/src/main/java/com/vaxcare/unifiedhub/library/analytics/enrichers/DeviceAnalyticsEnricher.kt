package com.vaxcare.unifiedhub.library.analytics.enrichers

import com.vaxcare.unifiedhub.core.common.ext.addIfNotEmpty
import com.vaxcare.unifiedhub.core.data.datasource.BatteryStatusProvider
import com.vaxcare.unifiedhub.core.data.datasource.DeviceNetworkProvider
import com.vaxcare.unifiedhub.core.data.datasource.OrientationProvider
import com.vaxcare.unifiedhub.core.datastore.datasource.DevicePreferenceDataSource
import com.vaxcare.unifiedhub.library.analytics.ApplicationName
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsEnricher
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.EmptyCoroutineContext.get

@Singleton
class DeviceAnalyticsEnricher @Inject constructor(
    private val batteryProvider: BatteryStatusProvider,
    private val networkProvider: DeviceNetworkProvider,
    private val orientationProvider: OrientationProvider,
    private val devicePreferenceDataSource: DevicePreferenceDataSource,
) : AnalyticsEnricher {
    override suspend fun defaultProps(): AnalyticsProps {
        val networkProps: AnalyticsProps = with(networkProvider.networkInfo.value) {
            mapOf(
                "dbmSignalLevel" to signalStrengthLevel.toString(),
                "networkFrequency" to frequency.toString(),
                "networkSecurity" to securityType.toString(),
                "networkStatus" to connectivityStatus.toString()
            )
        }

        val batteryProps: AnalyticsProps = with(batteryProvider.batteryStatus.value) {
            mapOf(
                "batteryPercentage" to percent.toString(),
                "charging" to isCharging.toString(),
                "powerSaveMode" to isPowerSaveModeEnabled.toString()
            )
        }

        val deviceProps: AnalyticsProps = with(devicePreferenceDataSource) {
            mutableMapOf(
                "modelType" to ApplicationName.MODEL_TYPE,
                "serialNumber" to serialNumber.first(),
            ).also {
                // Add optional attributes when they're not empty
                it.addIfNotEmpty("imei", imei.first())
                it.addIfNotEmpty("iccid", iccid.first())
            }.toMap()
        }

        val configurationProps: AnalyticsProps = mapOf(
            "orientation" to orientationProvider.orientation.value.let {
                if (it == -1) "null" else it.toString()
            }
        )

        return batteryProps + networkProps + deviceProps + configurationProps
    }
}
