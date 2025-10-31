package com.vaxcare.unifiedhub.feature.home.homeroute

import app.cash.turbine.test
import com.vaxcare.unifiedhub.core.data.repository.ClinicRepository
import com.vaxcare.unifiedhub.core.data.repository.LocationRepository
import com.vaxcare.unifiedhub.core.datastore.datasource.UsagePreferenceDataSource
import com.vaxcare.unifiedhub.core.domain.IsConnectedUseCase
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.HomeRouteEvent
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.HomeRouteIntent
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.HomeRouteState
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.HomeRouteViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class HomeRouteViewModelTest : BaseViewModelTest<HomeRouteState, HomeRouteEvent, HomeRouteIntent>() {
    private val clinicRepository: ClinicRepository = mockk()
    private val locationRepository: LocationRepository = mockk()
    private val usagePrefs: UsagePreferenceDataSource = mockk()
    private val isConnectedUseCase: IsConnectedUseCase = mockk()

    override lateinit var viewModel: HomeRouteViewModel

    @Before
    fun setup() {
        every { clinicRepository.getNoOfPermanentClinics() } returns flowOf(2)
        every { locationRepository.getStockTypes() } returns flowOf(listOf(StockType.PRIVATE))
        every { usagePrefs.lastSelectedStock } returns flowOf(StockType.PRIVATE.id)
        coEvery { isConnectedUseCase.invoke() } returns true

        viewModel =
            HomeRouteViewModel(
                clinicRepository = clinicRepository,
                locationRepository = locationRepository,
                usagePrefs = usagePrefs,
                dispatcherProvider = testDispatcherProvider,
                isConnected = isConnectedUseCase
            )
    }

    @Test
    fun `GIVEN multiple stocks and a single clinic WHEN showing the hamburger menu THEN transfers is shown`() {
        every { clinicRepository.getNoOfPermanentClinics() } returns flowOf(1)
        every { locationRepository.getStockTypes() } returns flowOf(listOf(StockType.PRIVATE, StockType.VFC))

        runTest {
            viewModel.uiState.test {
                assertEquals(
                    ExpectedMenuItems.privateWithTransfers,
                    awaitItem().manageMenuItems
                )
            }
        }
    }

    @Test
    fun `GIVEN a single stock and multiple clinics WHEN showing the hamburger menu THEN transfers is shown`() {
        every { clinicRepository.getNoOfPermanentClinics() } returns flowOf(2)
        every { locationRepository.getStockTypes() } returns flowOf(listOf(StockType.PRIVATE))

        runTest {
            viewModel.uiState.test {
                assertEquals(
                    ExpectedMenuItems.privateWithTransfers,
                    awaitItem().manageMenuItems
                )
            }
        }
    }

    @Test
    fun `GIVEN a single stock and a single clinic WHEN showing the hamburger menu THEN transfers is not shown`() {
        every { clinicRepository.getNoOfPermanentClinics() } returns flowOf(1)
        every { locationRepository.getStockTypes() } returns flowOf(StockScenarios.onlyPrivateStock)

        runTest {
            viewModel.uiState.test {
                assertEquals(
                    ExpectedMenuItems.privateNoTransfers,
                    awaitItem().manageMenuItems,
                )
            }
        }
    }

    @Test
    fun `GIVEN Private stock is selected THEN manageMenuItems should have Private only menu items`() =
        whenState {
            thenStateShouldBe(
                HomeRouteState(
                    manageMenuItems = ExpectedMenuItems.privateWithTransfers
                )
            )
        }

    @Test
    fun `GIVEN Public stock is selected THEN manageMenuItems should have Public only menu items`() {
        every { usagePrefs.lastSelectedStock } returns flowOf(StockType.VFC.id)

        whenState {
            thenStateShouldBe(
                HomeRouteState(
                    activeStock = StockType.VFC,
                    manageMenuItems = ExpectedMenuItems.publicWithTransfers
                )
            )
        }
    }

    @Test
    fun `GIVEN only one public stock is available THEN shouldConfirmStock should be FALSE`() {
        every { locationRepository.getStockTypes() } returns flowOf(StockScenarios.onlyOnePublicStock)
        every { usagePrefs.lastSelectedStock } returns flowOf(StockType.VFC.id)

        whenEvent(
            actions = { viewModel.handleIntent(HomeRouteIntent.GoToAddPublic) },
            assertions = { assertEquals(TransactionNavigationTestScenarios.onlyOnePublicStock, awaitItem()) }
        )
    }

    @Test
    fun `GIVEN multiple public stocks are available THEN shouldConfirmStock should be FALSE`() {
        every { locationRepository.getStockTypes() } returns flowOf(StockScenarios.multipleStocks)
        every { usagePrefs.lastSelectedStock } returns flowOf(StockType.VFC.id)

        whenEvent(
            actions = { viewModel.handleIntent(HomeRouteIntent.GoToAddPublic) },
            assertions = { assertEquals(TransactionNavigationTestScenarios.multiplePublicStock, awaitItem()) }
        )
    }

    @Test
    fun `GIVEN intent to navigate to seasonal on hands THEN jumpToSeasonal should be TRUE`() =
        whenEvent(
            actions = { viewModel.handleIntent(HomeRouteIntent.GoToOnHand(jumpToSeasonal = true)) },
            assertions = {
                assertEquals(HomeRouteEvent.ScrollToOnHand, awaitItem())
                thenStateShouldBe(
                    HomeRouteState(
                        jumpToSeasonal = true,
                        manageMenuItems = ExpectedMenuItems.privateWithTransfers
                    )
                )
            }
        )

    @Test
    fun `GIVEN intent to navigate to non-seasonal on hands THEN jumpToNonSeasonal should be TRUE`() =
        whenEvent(
            actions = { viewModel.handleIntent(HomeRouteIntent.GoToOnHand(jumpToNonSeasonal = true)) },
            assertions = {
                assertEquals(HomeRouteEvent.ScrollToOnHand, awaitItem())
                thenStateShouldBe(
                    HomeRouteState(
                        jumpToNonSeasonal = true,
                        manageMenuItems = ExpectedMenuItems.privateWithTransfers
                    )
                )
            }
        )

    // This test will test all private stock "GoTo" intents
    @Test
    fun `GIVEN intent to navigate to transaction and only Private stock is available THEN shouldConfirmStock should be FALSE`() =
        testWithMultipleEventScenarios(TransactionNavigationTestScenarios.onlyPrivateStock)

    // This test will test all private stock "GoTo" intents
    @Test
    fun `GIVEN intent to navigate to transaction and multiple stocks are available THEN shouldConfirmStock should be TRUE`() {
        every { locationRepository.getStockTypes() } returns flowOf(StockScenarios.multipleStocks)
        testWithMultipleEventScenarios(TransactionNavigationTestScenarios.multipleStocks)
    }

    @Test
    fun `GIVEN no internet connection WHEN navigating to a transaction THEN it shows the no internet dialog`() {
        coEvery { isConnectedUseCase.invoke() } returns false

        runTest {
            viewModel.uiState.test {
                viewModel.handleIntent(HomeRouteIntent.GoToCount)

                coVerify {
                    isConnectedUseCase.invoke()
                }

                assertEquals(
                    NoInternetScenarios.NoInternetDialog.expectedUiState,
                    expectMostRecentItem()
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GIVEN the no internet dialog is showing and 3 retries WHEN navigating to a transaction THEN the dialog shows the Network Settings button`() {
        coEvery { isConnectedUseCase.invoke() } returns false

        runTest(testDispatcherProvider.io) {
            viewModel.uiState.test {
                viewModel.handleIntent(HomeRouteIntent.GoToCount)

                repeat(3) {
                    viewModel.handleIntent(HomeRouteIntent.NoInternetTryAgain)
                    advanceUntilIdle()
                }

                coVerify(exactly = 4) {
                    isConnectedUseCase.invoke()
                }

                assertEquals(
                    NoInternetScenarios.NetworkSettingsDialog.expectedUiState,
                    expectMostRecentItem()
                )
            }
        }
    }

    private fun testWithMultipleEventScenarios(scenarios: List<EventScenario>) =
        runTest {
            viewModel.start()

            viewModel.uiEvent.test {
                scenarios.forEachIndexed { i, scenario ->
                    viewModel.handleIntent(scenario.intent)
                    assertEquals(scenario.event, awaitItem())
                    if (i == scenarios.lastIndex) {
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }
}
