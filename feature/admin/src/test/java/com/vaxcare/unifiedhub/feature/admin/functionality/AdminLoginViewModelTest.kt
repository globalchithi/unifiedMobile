package com.vaxcare.unifiedhub.feature.admin.functionality

import app.cash.turbine.test
import com.vaxcare.unifiedhub.core.datastore.datasource.DevicePreferenceDataSource
import com.vaxcare.unifiedhub.core.domain.ReportMetricEventUseCase
import com.vaxcare.unifiedhub.core.domain.ReportScreenEventsUseCase
import com.vaxcare.unifiedhub.core.domain.UpdateConnectivityStatusUseCase
import com.vaxcare.unifiedhub.core.model.ConnectivityStatus
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.core.ui.arch.MainDispatcherRule
import com.vaxcare.unifiedhub.core.ui.arch.TestDispatcherProvider
import com.vaxcare.unifiedhub.feature.admin.repository.AdminRepository
import com.vaxcare.unifiedhub.feature.admin.ui.login.AdminLoginError
import com.vaxcare.unifiedhub.feature.admin.ui.login.AdminLoginEvent
import com.vaxcare.unifiedhub.feature.admin.ui.login.AdminLoginIntent
import com.vaxcare.unifiedhub.feature.admin.ui.login.AdminLoginState
import com.vaxcare.unifiedhub.feature.admin.ui.login.AdminLoginViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminLoginViewModelTest :
    BaseViewModelTest<AdminLoginState, AdminLoginEvent, AdminLoginIntent>() {
    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    private val adminRepository: AdminRepository = mockk()
    private val devicePreferenceRepository: DevicePreferenceDataSource = mockk()
    private val updateConnectivityStatusUseCase: UpdateConnectivityStatusUseCase = mockk()
    private val reportMetricEventUseCase: ReportMetricEventUseCase = mockk(relaxed = true)
    private val reportScreenEventsUseCase: ReportScreenEventsUseCase = mockk(relaxed = true)
    private val dispatcherProvider = TestDispatcherProvider()
    override lateinit var viewModel: AdminLoginViewModel

    @Before
    fun setup() {
        every { devicePreferenceRepository.serialNumber } returns flowOf("12345")
        coEvery { reportScreenEventsUseCase(any()) } just Runs
        coEvery { reportMetricEventUseCase(any()) } just Runs
        viewModel = AdminLoginViewModel(
            adminRepository = adminRepository,
            dispatcherProvider = dispatcherProvider,
            updateConnectivityStatus = updateConnectivityStatusUseCase,
            trackEvent = reportMetricEventUseCase,
            trackScreen = reportScreenEventsUseCase,
            devicePreferenceDataSource = devicePreferenceRepository
        )
    }

    @Test
    fun `GIVEN the device is offline WHEN receiving LogIn intent THEN it shows the correct error text`() {
        coEvery { updateConnectivityStatusUseCase() } coAnswers {
            delay(2000)
            ConnectivityStatus.DISCONNECTED
        }
        runTest(dispatcherProvider.io) {
            var expectedUiState = AdminLoginState(serialNo = "12345")

            viewModel.uiState.test {
                assertEquals(expectedUiState, awaitItem())

                viewModel.handleIntent(AdminLoginIntent.LogIn("abc"))

                expectedUiState = expectedUiState.copy(isLoading = true).apply {
                    assertEquals(this, awaitItem())
                }

                expectedUiState = expectedUiState
                    .copy(
                        isLoading = false,
                        currentError = AdminLoginError.DeviceOffline
                    ).apply {
                        assertEquals(this, awaitItem())
                    }
            }
        }
    }

    @Test
    fun `GIVEN an incorrect password WHEN receiving LogIn intent THEN it shows the correct error text`() {
        coEvery { updateConnectivityStatusUseCase() } coAnswers {
            delay(2000)
            ConnectivityStatus.CONNECTED
        }
        coEvery { adminRepository.validatePassword(any()) } returns false

        runTest(dispatcherProvider.io) {
            var expectedUiState = AdminLoginState(serialNo = "12345")

            viewModel.uiState.test {
                assertEquals(expectedUiState, awaitItem())

                viewModel.handleIntent(AdminLoginIntent.LogIn("abc"))

                expectedUiState = expectedUiState.copy(isLoading = true).apply {
                    assertEquals(this, awaitItem())
                }

                expectedUiState = expectedUiState
                    .copy(
                        pw = "",
                        isLoading = false,
                        currentError = AdminLoginError.LoginFailure
                    ).apply {
                        assertEquals(this, awaitItem())
                    }
            }
        }
    }

    @Test
    fun `GIVEN a correct password WHEN receiving LogIn intent THEN it sends NavigateForward`() {
        coEvery { updateConnectivityStatusUseCase() } coAnswers {
            delay(2000)
            ConnectivityStatus.CONNECTED
        }
        coEvery { adminRepository.validatePassword(any()) } returns true

        runTest(dispatcherProvider.io) {
            viewModel.uiEvent.test {
                viewModel.handleIntent(AdminLoginIntent.LogIn("abc"))
                assertEquals(AdminLoginEvent.NavigateForward, awaitItem())
            }
        }
    }
}
