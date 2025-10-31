package com.vaxcare.unifiedhub.feature.admin.functionality

import app.cash.turbine.test
import com.vaxcare.unifiedhub.core.data.repository.LocationRepository
import com.vaxcare.unifiedhub.core.domain.SetupVaxHub
import com.vaxcare.unifiedhub.core.model.Location
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.arch.MainDispatcherRule
import com.vaxcare.unifiedhub.core.ui.arch.TestDispatcherProvider
import com.vaxcare.unifiedhub.feature.admin.ui.details.AdminDetailsState
import com.vaxcare.unifiedhub.feature.admin.ui.details.AdminDetailsViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class AdminDetailsViewModelTest {
    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    private val locationRepository: LocationRepository = mockk(relaxUnitFun = true) {
        every { getLocation() } returns flowOf(
            Location(
                clinicName = "Initial",
                partnerName = "Initial",
                stockTypes = listOf(StockType.PRIVATE)
            )
        )
    }
    private val dispatcherProvider = TestDispatcherProvider()
    private val setupVaxHub: SetupVaxHub = mockk {
        every { this@mockk.invoke(any(), any(), any(), any()) } returns mockk {
            coEvery { join() } just runs
        }
    }
    private lateinit var viewModel: AdminDetailsViewModel

    @Before
    fun setup() {
        viewModel = AdminDetailsViewModel(
            dispatcherProvider,
            locationRepository,
            setupVaxHub
        )
    }

    @Test
    fun `Initial values are displayed`() {
        runTest(dispatcherProvider.io) {
            every { locationRepository.pidCid } returns flowOf(123L to 321L)

            viewModel.screenUiState.test {
                // trigger viewModel.loadData()
                advanceUntilIdle()

                assertEquals(
                    AdminDetailsState.ScreenUiState(
                        isLoading = false,
                        isError = false,
                        partnerID = "123",
                        clinicID = "321",
                        clinicName = "Initial",
                        showKeypad = false,
                    ),
                    expectMostRecentItem()
                )
            }
        }
    }

    @Test
    fun `Submit valid pid and cid`() {
        runTest(dispatcherProvider.io) {
            coEvery { locationRepository.getCheckData("123", "321") } returns Pair(true, "999")
            every { locationRepository.pidCid } returns flowOf(Pair(0, 0))

            viewModel.screenUiState.test {
                // trigger viewModel.loadData()
                advanceUntilIdle()
                every { locationRepository.getLocation() } returns
                    flowOf(
                        Location(
                            clinicName = "Post-sync",
                            partnerName = "Post-sync",
                            stockTypes = listOf(StockType.PRIVATE)
                        )
                    )

                enterPartnerId("123")
                advanceUntilIdle()
                assertEquals(
                    AdminDetailsState.ScreenUiState(
                        isLoading = false,
                        isError = false,
                        partnerID = "123",
                        clinicID = "",
                        clinicName = "Initial",
                        showKeypad = true,
                    ),
                    expectMostRecentItem()
                )

                enterClinicId("321")
                advanceUntilIdle()
                assertEquals(
                    AdminDetailsState.ScreenUiState(
                        isLoading = false,
                        isError = false,
                        partnerID = "123",
                        clinicID = "321",
                        clinicName = "Post-sync",
                        showKeypad = false,
                    ),
                    expectMostRecentItem()
                )
            }
        }
    }

    @Test
    fun `Submit invalid pid and cid`() {
        runTest(dispatcherProvider.io) {
            coEvery { locationRepository.getCheckData("124", "321") } returns Pair(false, "")
            every { locationRepository.pidCid } returns flowOf(Pair(0, 0))
            every { locationRepository.getLocation() } returns
                flowOf(
                    Location(
                        clinicName = "Initial",
                        partnerName = "Initial",
                        stockTypes = listOf(StockType.PRIVATE)
                    )
                )

            viewModel.screenUiState.test {
                // trigger viewModel.loadData()
                advanceUntilIdle()

                enterPartnerId("124")
                enterClinicId("321")

                advanceUntilIdle()
                assertEquals(
                    AdminDetailsState.ScreenUiState(
                        isLoading = false,
                        isError = true,
                        partnerID = "124",
                        clinicID = "321",
                        clinicName = "",
                        showKeypad = false,
                    ),
                    expectMostRecentItem()
                )
            }
        }
    }

    private fun enterClinicId(id: String) {
        viewModel.onEditClinicID()
        id.forEach(viewModel::onKeypadNumberClick)
        viewModel.onKeypadSubmit()
    }

    private fun enterPartnerId(id: String) {
        viewModel.onEditPartnerID()
        id.forEach(viewModel::onKeypadNumberClick)
        viewModel.onKeypadSubmit()
    }
}
