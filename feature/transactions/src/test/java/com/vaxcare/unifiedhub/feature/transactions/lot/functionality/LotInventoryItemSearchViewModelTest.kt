package com.vaxcare.unifiedhub.feature.transactions.lot.functionality

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.vaxcare.unifiedhub.core.data.repository.LocationRepository
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.network.model.FeatureFlagDTO
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.feature.transactions.domain.model.TransactionSession
import com.vaxcare.unifiedhub.feature.transactions.lot.data.testProducts
import com.vaxcare.unifiedhub.feature.transactions.navigation.LotSearchRoute
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.search.LotSearchDialog
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.search.LotSearchEvent
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.search.LotSearchIntent
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.search.LotSearchState
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.search.LotSearchViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.search.SelectedLot
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class LotInventoryItemSearchViewModelTest :
    BaseViewModelTest<LotSearchState, LotSearchEvent, LotSearchIntent>() {
    override val viewModel: LotSearchViewModel by lazy {
        LotSearchViewModel(
            lotRepository = lotRepository,
            dispatcherProvider = testDispatcherProvider,
            locationRepository = locationRepository,
            productRepository = productRepository,
            analyticsRepository = analyticsRepository,
            savedStateHandle = savedStateHandle
        )
    }
    private val savedStateHandle: SavedStateHandle = mockk(relaxed = true)
    private val lotRepository: LotRepository = mockk()
    private val locationRepository: LocationRepository = mockk()
    private val productRepository: ProductRepository = mockk()
    private val analyticsRepository: AnalyticsRepository = mockk()
    private val transactionSession: TransactionSession = mockk(relaxed = true)

    @Before
    fun setUp() {
        every { savedStateHandle.toRoute<LotSearchRoute>() } returns LotSearchRoute(null, 1, "counts")
        every { transactionSession.productId } returns null
        every { transactionSession.stockType } returns StockType.PRIVATE
        coEvery { locationRepository.getFeatureFlagsAsync() } returns listOf(
            FeatureFlagDTO(
                featureFlagId = 1,
                clinicId = 1,
                featureFlagName = "LARCsEnabled"
            )
        )
        every { productRepository.getAllProducts() } returns flowOf(testProducts)
        every { lotRepository.getAllLots() } returns flowOf(testLots)
    }

    @Ignore("savedStateHandle is broken")
    @Test
    fun navigateBackTest() =
        whenEvent(
            actions = { viewModel.handleIntent(LotSearchIntent.CloseScreen) },
            assertions = {
                Assert.assertEquals(
                    LotSearchEvent.NavigateBack,
                    awaitItem()
                )
            }
        )

    @Ignore("savedStateHandle is broken")
    @Test
    fun searchLotsTestEmpty() =
        whenState {
            val searchTerm = "12"
            viewModel.handleIntent(LotSearchIntent.SearchLot(TextFieldValue(searchTerm)))
            awaitItem()
            Assert.assertEquals(
                emptyList<SelectedLot>(),
                awaitItem().selectedLots
            )
        }

    @Ignore("savedStateHandle is broken")
    @Test
    fun addNewLotTest() =
        whenState {
            val searchTerm = "JAMESTEST"
            viewModel.handleIntent(LotSearchIntent.AddNewLot(searchTerm))
            awaitItem()
            Assert.assertEquals(
                LotSearchDialog.ConfirmLotNumber(searchTerm),
                awaitItem().activeDialog
            )
        }

    @Ignore("savedStateHandle is broken")
    @Test
    fun addNewLotCancel() =
        whenState {
            viewModel.handleIntent(LotSearchIntent.AddNewLot("test"))
            awaitItem()
            viewModel.handleIntent(LotSearchIntent.CancelAddNewLot)
            awaitItem()
            Assert.assertEquals(
                null,
                awaitItem().activeDialog
            )
        }

    @Ignore("savedStateHandle is broken")
    @Test
    fun searchLotsTest123() =
        whenState {
            val searchTerm = "123"
            viewModel.handleIntent(LotSearchIntent.SearchLot(TextFieldValue(searchTerm)))
            skipItems(2)
            val lots = awaitItem().selectedLots
            Assert.assertTrue(
                lots.isNotEmpty() && lots.all { it.lotNumber.text.contains(searchTerm) }
            )
        }

    @Ignore("pending add new lot ticket")
    @Test
    fun addNewLotConfirm() =
        whenEvent(
            actions = { viewModel.handleIntent(LotSearchIntent.ConfirmedAddNewLot("JAMESLOT")) },
            assertions = {
                Assert.assertEquals(
                    LotSearchEvent.NavigateWithSelectedLot("JAMESLOT", 1),
                    awaitItem()
                )
            }
        )
}
