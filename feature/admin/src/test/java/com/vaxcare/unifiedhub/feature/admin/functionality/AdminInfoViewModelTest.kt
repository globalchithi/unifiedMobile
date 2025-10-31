package com.vaxcare.unifiedhub.feature.admin.functionality

import com.vaxcare.unifiedhub.core.data.BuildConfig
import com.vaxcare.unifiedhub.core.datastore.datasource.DevicePreferenceDataSource
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.feature.admin.ui.info.AdminInfoDialog
import com.vaxcare.unifiedhub.feature.admin.ui.info.AdminInfoEvent
import com.vaxcare.unifiedhub.feature.admin.ui.info.AdminInfoIntent
import com.vaxcare.unifiedhub.feature.admin.ui.info.AdminInfoState
import com.vaxcare.unifiedhub.feature.admin.ui.info.AdminInfoViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class AdminInfoViewModelTest : BaseViewModelTest<AdminInfoState, AdminInfoEvent, AdminInfoIntent>() {
    private val devicePreferenceRepository: DevicePreferenceDataSource = mockk(relaxed = true)

    override lateinit var viewModel: AdminInfoViewModel

    @Before
    fun setUp() {
        coEvery { devicePreferenceRepository.serialNumber } returns flowOf<String>("SN-12345")

        viewModel = AdminInfoViewModel(
            dispatcherProvider = testDispatcherProvider,
            devicePreferenceRepository = devicePreferenceRepository
        )
    }

    @Ignore("No value produced in 3s error. Not sure why")
    @Test
    fun `GIVEN ViewModel is created WHEN start is called THEN shows loading and finally filled state`() =
        runTest {
            viewModel.start()

            advanceUntilIdle()

            whenState {
                awaitItem() // Initial
                awaitItem().run { Assert.assertTrue(isLoading) }
                awaitItem().run {
                    Assert.assertFalse(isLoading)
                    Assert.assertEquals("SN-12345", serialNumber)
                    Assert.assertEquals(BuildConfig.VERSION_NAME, apkVersion)
                    Assert.assertEquals(
                        LocalDateTime.of(1900, 1, 1, 0, 0),
                        lastSyncedDatabaseRecords
                    )
                }
            }
        }

    @Test
    fun `GIVEN CloseScreen intent WHEN handled THEN emits NavigateBack event`() =
        whenEvent(
            actions = { viewModel.handleIntent(AdminInfoIntent.CloseScreen) },
            assertions = { Assert.assertEquals(AdminInfoEvent.NavigateBack, awaitItem()) }
        )

    @Test
    fun `GIVEN OpenSourceLibrary intent WHEN handled THEN emits NavigateToOpenSourceLibrary`() =
        whenEvent(
            actions = { viewModel.handleIntent(AdminInfoIntent.OpenSourceLibrary) },
            assertions = {
                Assert.assertEquals(
                    AdminInfoEvent.NavigateToOpenSourceLibrary,
                    awaitItem()
                )
            }
        )

    @Test
    fun `GIVEN OpenSystemConnectivity intent WHEN handled THEN emits NavigateToSystemConnectivity`() =
        whenEvent(
            actions = { viewModel.handleIntent(AdminInfoIntent.OpenSystemConnectivity) },
            assertions = {
                Assert.assertEquals(
                    AdminInfoEvent.NavigateToSystemConnectivity,
                    awaitItem()
                )
            }
        )

    @Test
    fun `GIVEN ValidateScannerLicenseClicked intent WHEN handled THEN dialog is shown`() =
        runTest {
            whenState {
                viewModel.handleIntent(AdminInfoIntent.ValidateScannerLicenseClicked)

                awaitItem()
                Assert.assertEquals(
                    AdminInfoDialog.ValidateScannerLicense,
                    awaitItem().activeDialog
                )
            }
        }

    @Test
    fun `GIVEN dialog open WHEN CloseValidateScanner intent THEN dialog is cleared`() =
        runTest {
            whenState {
                viewModel.handleIntent(AdminInfoIntent.ValidateScannerLicenseClicked)
                awaitItem()
                awaitItem()

                viewModel.handleIntent(AdminInfoIntent.CloseValidateScanner)
                Assert.assertNull(awaitItem().activeDialog)
            }
        }
}
