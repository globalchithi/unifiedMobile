package com.vaxcare.unifiedhub.feature.home.home

import app.cash.turbine.test
import com.vaxcare.unifiedhub.core.data.datasource.AppUpdateRepository
import com.vaxcare.unifiedhub.core.data.repository.ClinicRepository
import com.vaxcare.unifiedhub.core.data.repository.CountRepository
import com.vaxcare.unifiedhub.core.data.repository.LocationRepository
import com.vaxcare.unifiedhub.core.data.repository.LotInventoryRepository
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.datastore.datasource.UsagePreferenceDataSource
import com.vaxcare.unifiedhub.core.domain.IsConnectedUseCase
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.core.ui.arch.TestDispatcherProvider
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.home.helper.HomeTestUtils.mockIsUpdateAvailable
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeEvent
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeIntent
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeState
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeViewModel
import com.vaxcare.unifiedhub.feature.home.ui.home.model.Notification
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDateTime

@RunWith(JUnit4::class)
class HomeViewModelTest : BaseViewModelTest<HomeState, HomeEvent, HomeIntent>() {
    override lateinit var viewModel: HomeViewModel

    private val locationRepository: LocationRepository = mockk()
    private val usagePrefs: UsagePreferenceDataSource = mockk()
    private val appUpdateRepository: AppUpdateRepository = mockk()
    private val clinicRepository: ClinicRepository = mockk()
    private val countRepository: CountRepository = mockk()
    private val lotRepository: LotRepository = mockk()
    private val lotInventoryRepository: LotInventoryRepository = mockk()
    private val dispatcherProvider = TestDispatcherProvider()
    private val isConnectedUseCase: IsConnectedUseCase = mockk()

    @Before
    fun setup() {
        every { appUpdateRepository.checkAppUpdateInfo() } just runs
        every { appUpdateRepository.appUpdateInfo } returns flowOf()
        every { clinicRepository.getNoOfPermanentClinics() } returns flowOf()
        every { countRepository.getLatestCountDate(any()) } returns flowOf()
        every { locationRepository.getLocation() } returns flowOf()
        every { locationRepository.getStockTypes() } returns flowOf()
        every { lotRepository.getExpiredLots() } returns flowOf()
        every { lotInventoryRepository.getLotInventory(any(), any()) } returns flowOf()
        every { usagePrefs.lastSelectedStock } returns flowOf(1)
        coEvery { isConnectedUseCase.invoke() } returns true

        viewModel = HomeViewModel(
            clinicRepository = clinicRepository,
            locationRepository = locationRepository,
            usagePrefs = usagePrefs,
            appUpdateRepository = appUpdateRepository,
            countRepository = countRepository,
            lotRepository = lotRepository,
            lotInventoryRepository = lotInventoryRepository,
            dispatcherProvider = dispatcherProvider,
            isConnected = isConnectedUseCase
        )
    }

    @Test
    fun `GIVEN multiple stocks and a single clinic WHEN showing the adjustments dialog THEN transfers is shown`() {
        every { clinicRepository.getNoOfPermanentClinics() } returns flowOf(1)
        every { locationRepository.getStockTypes() } returns flowOf(listOf(StockType.PRIVATE, StockType.VFC))

        runTest {
            viewModel.uiState.test {
                viewModel.handleIntent(HomeIntent.AdjustInventory)

                assertEquals(
                    HomeTestData.ShowTransfers.expectedUiState,
                    expectMostRecentItem(),
                )
            }
        }
    }

    @Test
    fun `GIVEN a single stock and multiple clinics WHEN showing the adjustments dialog THEN transfers is shown`() {
        every { clinicRepository.getNoOfPermanentClinics() } returns flowOf(2)
        every { locationRepository.getStockTypes() } returns flowOf(listOf(StockType.PRIVATE))

        runTest {
            viewModel.uiState.test {
                viewModel.handleIntent(HomeIntent.AdjustInventory)

                assertEquals(
                    HomeTestData.ShowTransfers.expectedUiState,
                    expectMostRecentItem()
                )
            }
        }
    }

    @Test
    fun `GIVEN a single stock and a single clinic WHEN showing the adjustments dialog THEN transfers is not shown`() {
        every { clinicRepository.getNoOfPermanentClinics() } returns flowOf(1)
        every { locationRepository.getStockTypes() } returns flowOf(listOf(StockType.PRIVATE))

        runTest {
            viewModel.uiState.test {
                viewModel.handleIntent(HomeIntent.AdjustInventory)

                assertEquals(
                    HomeTestData.HideTransfers.expectedUiState,
                    expectMostRecentItem()
                )
            }
        }
    }

    @Test
    fun `GIVEN State stock is selected WHEN that stock is disabled THEN the selected stock defaults to Private`() {
        every { locationRepository.getLocation() } returns flowOf(
            HomeTestData.AvailableStocksChanged.newLocation
        )

        runTest {
            givenState {
                HomeState(
                    activeStock = StockUi.STATE,
                    availableStocks = StockUi.entries
                )
            }

            viewModel.uiState.test {
                viewModel.start()
                thenStateShouldBe(HomeTestData.AvailableStocksChanged.expectedUiState)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN the latest count is gt 7 days old WHEN data is emitted THEN the overdue count notification displays`() {
        every {
            countRepository.getLatestCountDate(StockType.PRIVATE)
        } returns flowOf(LocalDateTime.now().minusDays(17))

        whenState {
            thenStateShouldBe(HomeTestData.OnlyCountsOverdue.expectedUiState)
        }
    }

    @Test
    fun `GIVEN there are expired doses WHEN data is emitted THEN the expired doses notification displays`() {
        every { lotRepository.getExpiredLots() } returns flowOf(listOf())
        every {
            lotInventoryRepository.getLotInventory(any(), any())
        } returns flowOf(HomeTestData.OnlyExpiredDoses.expiredInventory)

        whenState {
            thenStateShouldBe(HomeTestData.OnlyExpiredDoses.expectedUiState)
        }
    }

    @Test
    fun `GIVEN an app update is available WHEN data is emitted THEN the app update notification displays`() {
        appUpdateRepository.mockIsUpdateAvailable(true)

        whenState {
            thenStateShouldBe(HomeTestData.OnlyAppUpdate.expectedUiState)
        }
    }

    @Test
    fun `GIVEN overdue counts notification is already visible WHEN data is emitted THEN the notification disappears`() {
        every {
            countRepository.getLatestCountDate(any())
        } returns flowOf(LocalDateTime.now().minusDays(2))

        runTest {
            givenState {
                HomeState(
                    notifications = setOf(
                        Notification.OverdueCount(17)
                    )
                )
            }

            viewModel.uiState.test {
                viewModel.start()
                thenStateShouldBe(HomeTestData.EmptyNotifications.expectedUiState)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN expired doses notification is already visible WHEN data is emitted THEN the notification disappears`() {
        every { lotRepository.getExpiredLots() } returns flowOf(listOf())
        every {
            lotInventoryRepository.getLotInventory(any(), any())
        } returns flowOf(listOf())

        runTest {
            givenState {
                HomeState(
                    notifications = setOf(
                        Notification.ExpiredDoses(99)
                    )
                )
            }

            viewModel.uiState.test {
                viewModel.start()
                thenStateShouldBe(HomeTestData.EmptyNotifications.expectedUiState)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN app update notification is already visible WHEN data is emitted THEN the notification disappears`() {
        appUpdateRepository.mockIsUpdateAvailable(false)

        runTest {
            givenState {
                HomeState(notifications = setOf(Notification.AppUpdate))
            }

            viewModel.uiState.test {
                viewModel.start()
                thenStateShouldBe(HomeTestData.EmptyNotifications.expectedUiState)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN no notifications should display WHEN data is emitted THEN the empty notifications state is displayed`() {
        every {
            countRepository.getLatestCountDate(StockType.PRIVATE)
        } returns flowOf(LocalDateTime.now().minusDays(6))
        every { lotRepository.getExpiredLots() } returns flowOf(listOf())
        every {
            lotInventoryRepository.getLotInventory(any(), any())
        } returns flowOf(listOf())
        appUpdateRepository.mockIsUpdateAvailable(false)

        whenState {
            thenStateShouldBe(HomeTestData.EmptyNotifications.expectedUiState)
        }
    }

    @Test
    fun `GIVEN no internet connection WHEN navigating to a transaction THEN it shows the no internet dialog`() {
        coEvery { isConnectedUseCase.invoke() } returns false

        runTest {
            viewModel.uiState.test {
                viewModel.handleIntent(HomeIntent.GoToCount)

                coVerify {
                    isConnectedUseCase.invoke()
                }

                Assert.assertEquals(
                    HomeTestData.NoInternetDialog.expectedUiState,
                    expectMostRecentItem()
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GIVEN the no internet dialog is showing and 3 retries WHEN navigating to a transaction THEN the dialog shows the Network Settings button`() {
        coEvery { isConnectedUseCase.invoke() } returns false

        runTest(dispatcherProvider.io) {
            viewModel.uiState.test {
                viewModel.handleIntent(HomeIntent.GoToCount)

                repeat(3) {
                    viewModel.handleIntent(HomeIntent.NoInternetTryAgain)
                    advanceUntilIdle()
                }

                coVerify(exactly = 4) {
                    isConnectedUseCase.invoke()
                }

                Assert.assertEquals(
                    HomeTestData.NetworkSettingsDialog.expectedUiState,
                    expectMostRecentItem()
                )
            }
        }
    }
}
