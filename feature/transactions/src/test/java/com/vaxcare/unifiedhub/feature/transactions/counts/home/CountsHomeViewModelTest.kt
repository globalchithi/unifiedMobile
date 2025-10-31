package com.vaxcare.unifiedhub.feature.transactions.counts.home

import app.cash.turbine.test
import com.vaxcare.unifiedhub.core.data.repository.LotInventoryRepository
import com.vaxcare.unifiedhub.core.domain.CreateProductListFromLotsUseCase
import com.vaxcare.unifiedhub.core.domain.UpdateConnectivityStatusUseCase
import com.vaxcare.unifiedhub.core.model.ConnectivityStatus
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.core.ui.arch.TestDispatcherProvider
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.home.CountsHomeDialog
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.home.CountsHomeState
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.home.CountsHomeViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.home.model.LotInventoryUi
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.home.model.ProductUi
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.session.CountSession
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.home.CountsHomeEvent as Event
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.home.CountsHomeIntent as Intent

@RunWith(JUnit4::class)
class CountsHomeViewModelTest : BaseViewModelTest<CountsHomeState, Event, Intent>() {
    private val lotInventoryRepository: LotInventoryRepository = mockk {
        coEvery { syncLotInventory() } just Runs
        every { getLotInventory(any()) } returns flowOf(emptyList())
    }
    private val dispatcherProvider = TestDispatcherProvider()
    private val createProductListFromLotsUseCase: CreateProductListFromLotsUseCase = mockk {
        coEvery {
            invoke<LotInventoryUi, ProductUi>(any(), any(), any())
        } returns emptyList()
    }
    private val updateConnectivityStatusUseCase: UpdateConnectivityStatusUseCase = mockk()
    private val session: CountSession = mockk {
        every { stockType } returns StockType.PRIVATE
    }

    override lateinit var viewModel: CountsHomeViewModel

    @Before
    fun setup() {
        viewModel = CountsHomeViewModel(
            lotInventoryRepository = lotInventoryRepository,
            dispatcherProvider = dispatcherProvider,
            createProductListFromLotsUseCase = createProductListFromLotsUseCase,
            updateConnectivityStatus = updateConnectivityStatusUseCase,
            countSession = session
        )
    }

    @Test
    fun `GIVEN there is connectivity WHEN start() THEN the sync is requested and data collection is begun`() {
        coEvery { updateConnectivityStatusUseCase.invoke() } returns ConnectivityStatus.CONNECTED
        session.apply {
            every { initializeLotState(any()) } just Runs
            every { addedLots } returns MutableStateFlow(emptySet())
            every { lotState } returns MutableStateFlow(emptyMap())
            every { confirmedIds } returns MutableStateFlow(emptyList())
        }

        runTest {
            viewModel.uiState.test {
                coVerify {
                    updateConnectivityStatusUseCase.invoke()
                    lotInventoryRepository.syncLotInventory()
                }

                expectMostRecentItem().let { state ->
                    state.equalsOther(TestData.InitWithConnectivity.finalExpectedState)
                }
            }
        }
    }

    @Test
    fun `GIVEN there is connectivity, but the sync will fail WHEN start() THEN the dialog is shown`() {
        coEvery { updateConnectivityStatusUseCase.invoke() } returns ConnectivityStatus.CONNECTED
        coEvery { lotInventoryRepository.syncLotInventory() } throws Exception()
        session.apply {
            every { initializeLotState(any()) } just Runs
            every { addedLots } returns MutableStateFlow(emptySet())
            every { lotState } returns MutableStateFlow(emptyMap())
            every { confirmedIds } returns MutableStateFlow(emptyList())
        }

        runTest {
            viewModel.uiState.test {
                coVerify {
                    updateConnectivityStatusUseCase.invoke()
                    lotInventoryRepository.syncLotInventory()
                }

                expectMostRecentItem().let { state ->
                    state.equalsOther(TestData.InitNoConnectivity.expectedState)
                }
            }
        }
    }

    @Test
    fun `GIVEN no connectivity WHEN start() THEN the dialog is shown`() {
        coEvery { updateConnectivityStatusUseCase.invoke() } returns ConnectivityStatus.DISCONNECTED

        runTest(dispatcherProvider.io) {
            viewModel.uiState.test {
                coVerify {
                    updateConnectivityStatusUseCase.invoke()
                }

                expectMostRecentItem().let { state ->
                    state.equalsOther(TestData.InitNoConnectivity.expectedState)
                }
            }
        }
    }

    private fun CountsHomeState.equalsOther(other: CountsHomeState): Boolean =
        isLoading == other.isLoading &&
            seasonalProducts.foldRightIndexed(true) { i, prod, _ ->
                other.seasonalProducts[i] == prod
            } &&
            nonSeasonalProducts.foldRightIndexed(true) { i, prod, _ ->
                other.nonSeasonalProducts[i] == prod
            } &&
            stockType == other.stockType &&
            activeDialog == other.activeDialog

    private object TestData {
        object InitNoConnectivity {
            val expectedState = CountsHomeState(
                activeDialog = CountsHomeDialog.NoInternet
            )
        }

        object InitWithConnectivity {
            val finalExpectedState = CountsHomeState(
                isLoading = false,
                seasonalProducts = emptyList(),
                nonSeasonalProducts = emptyList(),
            )
        }
    }
}
