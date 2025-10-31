package com.vaxcare.unifiedhub.library.analytics.managers

import android.app.Application
import app.cash.turbine.test
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.repository.LocationRepository
import com.vaxcare.unifiedhub.core.datastore.datasource.DevicePreferenceDataSource
import com.vaxcare.unifiedhub.core.datastore.datasource.LicensePreferenceDataSource
import com.vaxcare.unifiedhub.core.datastore.datasource.LocationPreferenceDataSource
import com.vaxcare.unifiedhub.core.datastore.datasource.UserSessionPreferenceDataSource
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class DatadogManagerTest {
    lateinit var dataDogManager: DatadogManager

    private val licensePreferenceDataSource: LicensePreferenceDataSource = mockk(relaxUnitFun = true)
    private val devicePreferenceDataSource: DevicePreferenceDataSource = mockk(relaxUnitFun = true)
    private val locationRepository: LocationRepository = mockk(relaxUnitFun = true)
    private val locationPreferenceDataSource: LocationPreferenceDataSource = mockk(relaxUnitFun = true)
    private val userSessionPreferenceDataSource: UserSessionPreferenceDataSource = mockk(relaxUnitFun = true)
    private val dispatcherProvider: DispatcherProvider = mockk(relaxUnitFun = true)
    private val application: Application = mockk(relaxUnitFun = true)

    @Before
    fun setUp() {
        every { dispatcherProvider.io } returns UnconfinedTestDispatcher()
        configureSettingsForTesting()
        configureAttributesForTesting()

        dataDogManager = DatadogManager(
            licensePreferenceDataSource = licensePreferenceDataSource,
            devicePreferenceDataSource = devicePreferenceDataSource,
            locationRepository = locationRepository,
            locationPreferenceDataSource = locationPreferenceDataSource,
            userSessionPreferenceDataSource = userSessionPreferenceDataSource,
            dispatcherProvider = dispatcherProvider,
            context = application
        )
    }

    @Test
    fun `Settings Flow should be right values and types`() {
        runTest {
            dataDogManager.buildSettingsFlow().test {
                assertEquals(testSettings, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `Attributes Flow should be right values and types`() {
        runTest {
            dataDogManager.buildAttributesFlow().test {
                assertEquals(testAttributes, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    private fun configureAttributesForTesting() {
        with(devicePreferenceDataSource) {
            every { imei } returns flowOf(testAttributes.imei)
            every { iccid } returns flowOf(testAttributes.iccid)
            every { serialNumber } returns flowOf(testAttributes.serialNumber)
        }
        with(locationPreferenceDataSource) {
            every { parentClinicId } returns flowOf(testAttributes.cid.toLong())
            every { partnerId } returns flowOf(testAttributes.pid.toLong())
        }
        with(userSessionPreferenceDataSource) {
            every { userId } returns flowOf(testAttributes.userId.toLong())
            every { userName } returns flowOf(testAttributes.userName)
        }
        with(locationPreferenceDataSource) {
            every { isLocationSynced } returns flowOf(true)
        }
        coEvery { locationRepository.getFeatureFlagsAsync() } returns featureFlags
    }

    private fun configureSettingsForTesting() {
        with(licensePreferenceDataSource) {
            every { datadogClientToken } returns flowOf(testSettings.clientToken)
            every { datadogApplicationId } returns flowOf(testSettings.applicationId)
            every { datadogRumSampleRate } returns flowOf(testSettings.rumSampleRate)
            every { datadogSessionReplaySampleRate } returns flowOf(testSettings.sessionReplaySampleRate)
            every { datadogSite } returns flowOf(testSettings.site.toString())
            every { datadogEnabled } returns flowOf(testSettings.enabled)
        }
    }
}
