package com.vaxcare.unifiedhub.library.analytics.enrichers

import com.vaxcare.unifiedhub.core.data.datasource.BatteryStatusProvider
import com.vaxcare.unifiedhub.core.data.datasource.DeviceNetworkProvider
import com.vaxcare.unifiedhub.core.data.datasource.OrientationProvider
import com.vaxcare.unifiedhub.core.datastore.datasource.DevicePreferenceDataSource
import com.vaxcare.unifiedhub.core.model.BatteryStatus
import com.vaxcare.unifiedhub.core.model.ConnectionType
import com.vaxcare.unifiedhub.core.model.NetworkInfo
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DeviceAnalyticsEnricherTest {
    lateinit var deviceAnalyticsEnricher: DeviceAnalyticsEnricher
    private val batteryStatusProvider: BatteryStatusProvider = mockk(relaxUnitFun = true)
    private val networkProvider: DeviceNetworkProvider = mockk(relaxUnitFun = true)
    private val orientationProvider: OrientationProvider = mockk(relaxUnitFun = true)
    private val devicePreferenceDataSource: DevicePreferenceDataSource = mockk(relaxUnitFun = true)

    private val expectedProps: AnalyticsProps = batteryProps + networkProps + deviceProps + configurationProps

    private val networkFlow = MutableStateFlow(
        NetworkInfo(
            connectivityStatus,
            listOf(ConnectionType.WIFI),
            signalStrengthLevel,
            frequency,
            securityType
        )
    )

    private val batteryFlow = MutableStateFlow(BatteryStatus(percent, isCharging, isPowerSaveModeEnabled))
    private val orientationFlow = MutableStateFlow(180)

    @Before
    fun setUp() {
        every { networkProvider.networkInfo } returns networkFlow
        every { batteryStatusProvider.batteryStatus } returns batteryFlow
        every { orientationProvider.orientation } returns orientationFlow

        every { devicePreferenceDataSource.serialNumber } returns flowOf(serialNumber)
        every { devicePreferenceDataSource.imei } returns flowOf(imei)
        every { devicePreferenceDataSource.iccid } returns flowOf(iccid)

        deviceAnalyticsEnricher = DeviceAnalyticsEnricher(
            batteryProvider = batteryStatusProvider,
            networkProvider = networkProvider,
            orientationProvider = orientationProvider,
            devicePreferenceDataSource = devicePreferenceDataSource
        )
    }

    @Test
    fun `Props should match expected values`() =
        runTest {
            assertEquals(expectedProps, deviceAnalyticsEnricher.defaultProps())
        }
}
