package com.vaxcare.unifiedhub.feature.transactions.lot.functionality

import com.vaxcare.unifiedhub.core.data.repository.LotInventoryRepository
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.PackageRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.core.domain.PostNewLotUseCase
import com.vaxcare.unifiedhub.core.domain.ValidateScannedProductUseCase
import com.vaxcare.unifiedhub.core.domain.model.ScanValidationResult
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.model.lot.Lot
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.core.ui.arch.MainDispatcherRule
import com.vaxcare.unifiedhub.core.ui.arch.TestDispatcherProvider
import com.vaxcare.unifiedhub.feature.transactions.domain.model.TransactionSession
import com.vaxcare.unifiedhub.feature.transactions.model.LotState
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.LotInteractionDialog
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.LotInteractionError
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.LotInteractionEvent
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.LotInteractionIntent
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.LotInteractionState
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.LotInteractionViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.model.mapper.ProductAndLotInventoryMapper
import com.vaxcare.unifiedhub.library.scanner.domain.TwoDeeBarcode
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate

@RunWith(JUnit4::class)
class LotInteractionViewModelTest :
    BaseViewModelTest<LotInteractionState, LotInteractionEvent, LotInteractionIntent>() {
    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    override val viewModel: LotInteractionViewModel by lazy {
        LotInteractionViewModel(
            lotInventoryRepository = lotInventoryRepository,
            productRepository = productRepository,
            lotRepository = lotRepository,
            packageRepository = packageRepository,
            mapper = mapper,
            dispatcherProvider = dispatcherProvider,
            transactionSession = transactionSession,
            validateScannedProductUseCase = validateScannedProductUseCase,
            postNewLot = postNewLot
        )
    }

    private val validateScannedProductUseCase: ValidateScannedProductUseCase = mockk()
    private val postNewLot: PostNewLotUseCase = mockk()

    private val lotRepository: LotRepository = mockk(relaxUnitFun = true)
    private val packageRepository: PackageRepository = mockk(relaxUnitFun = true)
    private val lotInventoryRepository: LotInventoryRepository = mockk(relaxUnitFun = true)
    private val productRepository: ProductRepository = mockk(relaxUnitFun = true)
    private val mapper: ProductAndLotInventoryMapper = ProductAndLotInventoryMapper()
    private val dispatcherProvider = TestDispatcherProvider()
    private val transactionSession: TransactionSession = mockk(relaxed = true)

    private val addedLotsFlow = MutableStateFlow<Set<Pair<Int, String>>>(setOf())
    private val lotStatesFlow = MutableStateFlow<Map<String, LotState>>(mapOf())
    private val searchedLotFlow = MutableStateFlow<String?>(null)

    @Before
    fun setup() {
        coEvery { packageRepository.getOneByProductId(any()) } returns testPackage
        coEvery { lotRepository.getLotsByNumber(testLots.map { it.lotNumber }) } returns testLots
        every { productRepository.getProduct(any()) } returns flowOf(testProduct)
        every { transactionSession.stockType } returns StockType.PRIVATE
        every {
            lotInventoryRepository.getLotInventoryByProductAndSourceId(
                any(),
                any()
            )
        } returns flowOf(testLotInventory)
        every { transactionSession.searchedLot } returns searchedLotFlow
        every { transactionSession.productId } returns testProduct.id
        every { transactionSession.addedLots } returns addedLotsFlow.asStateFlow()
        every { transactionSession.lotState } returns lotStatesFlow.asStateFlow()
    }

    @Test
    fun `Initial Items Test`() {
        whenState {
            skipItems(1)
            thenStateShouldBe(initialState)
        }
    }

    @Test
    fun `Add New Lot Test`() {
        coEvery { lotRepository.getLotsByNumber(testLots.map { it.lotNumber } + lotToAdd.lotNumber) } returns
            testLots + lotToAdd
        coEvery { lotRepository.getLotByNumberAsync(lotToAdd.lotNumber) } returns lotToAdd
        whenState {
            emitNewLotItem(lotToAdd, LotState(0, false))
            skipItems(1)
            thenStateShouldBe(initialState.copy(lots = initialState.lots + lotUiToAdd))
        }
    }

    @Test
    fun `Bad Scan Test`() {
        coEvery {
            validateScannedProductUseCase.invoke(any(), any(), any(), any())
        } returns ScanValidationResult.InvalidBarcode

        whenState {
            viewModel.handleIntent(
                LotInteractionIntent.ScanLot(
                    TwoDeeBarcode("", "", "", "123bogus", LocalDate.now())
                )
            )
            skipItems(1)
            thenStateShouldBe(
                initialState.copy(
                    isActionRequired = false,
                    error = LotInteractionError.BadBarcodeScan,
                    isScannerActive = true
                )
            )
        }
    }

    @Test
    fun `Expired Lot Scan Test`() {
        coEvery {
            validateScannedProductUseCase.invoke(any(), any(), any(), any())
        } returns ScanValidationResult.Expired("")

        whenState {
            viewModel.handleIntent(
                LotInteractionIntent.ScanLot(
                    TwoDeeBarcode("", "", "", "123expired", LocalDate.now().minusDays(5))
                )
            )
            skipItems(1)
            thenStateShouldBe(
                initialState.copy(
                    isScannerActive = false,
                    activeDialog = LotInteractionDialog.ExpiredProductScanned
                )
            )
        }
    }

    @Test
    fun `Mismatch Product Scan Test`() {
        coEvery {
            validateScannedProductUseCase.invoke(any(), any(), any(), any())
        } returns ScanValidationResult.MismatchedProduct

        whenState {
            viewModel.handleIntent(
                LotInteractionIntent.ScanLot(
                    TwoDeeBarcode("", "", "", "456mismatch", LocalDate.now())
                )
            )
            skipItems(1)
            thenStateShouldBe(
                initialState.copy(
                    isScannerActive = false,
                    activeDialog = LotInteractionDialog.MismatchedProduct
                )
            )
        }
    }

    private suspend fun emitNewLotItem(lot: Lot, lotState: LotState?) {
        lotState?.let { lotStatesFlow.emit(mapOf(lot.lotNumber to lotState)) }
        addedLotsFlow.emit(setOf(lot.productId to lot.lotNumber))
    }
}
