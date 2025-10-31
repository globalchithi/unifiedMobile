package com.vaxcare.unifiedhub.viewmodel

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.vaxcare.unifiedhub.core.datastore.datasource.DevicePreferenceDataSource
import com.vaxcare.unifiedhub.permissions.PermissionsViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class PermissionsViewModelTest {
    private lateinit var permissionViewModel: PermissionsViewModel
    private lateinit var applicationContext: Context
    private val devicePreferenceRepository: DevicePreferenceDataSource = mockk(relaxed = true)

//    @get:Rule
//    val rule = ProviderTestRule.Builder(
//        MockContentProvider::class.java,
//        "com.vaxcare.mobilebridge.t105")
//        .build()
//    val mockContentProvider = MockContentProvider()

    @Before
    fun setup() {
//        rule.resolver
//        applicationContext.contentResolver.insert()
        applicationContext = ApplicationProvider.getApplicationContext()
        permissionViewModel = PermissionsViewModel(
            devicePreferenceRepository
        )
//        mockContentProvider.populateDummyData()
        every { devicePreferenceRepository.serialNumber } returns flow { emit("fake_serial") }
        every { devicePreferenceRepository.imei } returns flow { emit("fake_imei") }
        every { devicePreferenceRepository.iccid } returns flow { emit("fake_iccid") }
    }

    @Ignore("doesn't work")
    @Test
    fun test() {
        val data = permissionViewModel.getDeviceInfoFromBridge(applicationContext.contentResolver)
        assert(data.serial == "")
    }
}
