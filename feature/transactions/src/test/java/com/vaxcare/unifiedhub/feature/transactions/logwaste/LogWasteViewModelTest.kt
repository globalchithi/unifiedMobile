package com.vaxcare.unifiedhub.feature.transactions.logwaste

import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.core.domain.PostNewLotUseCase
import com.vaxcare.unifiedhub.core.domain.ValidateScannedProductUseCase
import com.vaxcare.unifiedhub.core.domain.model.ScanValidationResult
import com.vaxcare.unifiedhub.core.model.lot.Lot
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.model.product.Product
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.feature.transactions.model.LotState
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.LogWasteDialog
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.LogWasteEvent
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.LogWasteIntent
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.LogWasteState
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.LogWasteViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.session.LogWasteSession
import com.vaxcare.unifiedhub.library.scanner.domain.ParsedBarcode
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate

@RunWith(JUnit4::class)
class LogWasteViewModelTest : BaseViewModelTest<LogWasteState, LogWasteEvent, LogWasteIntent>() {
    private val productRepository: ProductRepository = mockk(relaxed = true)
    private val lotRepository: LotRepository = mockk(relaxed = true)
    private val session: LogWasteSession = mockk(relaxed = true)
    private val validateScannedProductUseCase: ValidateScannedProductUseCase = mockk(relaxed = true)
    private val postNewLotUseCase: PostNewLotUseCase = mockk(relaxed = true)

    override lateinit var viewModel: LogWasteViewModel

    private val lotStateFlow = MutableStateFlow<Map<String, LotState>>(emptyMap())
    private val searchedLotFlow = MutableStateFlow<String?>(null)
    private val wasteReasonFlow = MutableStateFlow<LogWasteReason?>(null)

    @Before
    fun setUp() {
        mockkStatic("androidx.navigation.SavedStateHandleKt")

        every { session.lotState } returns lotStateFlow
        every { session.searchedLot } returns searchedLotFlow
        every { session.wasteReason } returns wasteReasonFlow

        viewModel = LogWasteViewModel(
            productRepository = productRepository,
            lotRepository = lotRepository,
            dispatcherProvider = testDispatcherProvider,
            session = session,
            validateScannedProductUseCase = validateScannedProductUseCase,
            postNewLotUseCase = postNewLotUseCase
        )
    }

    @Test
    fun `GIVEN lotState emits data WHEN start THEN wastedProductsUi and total are correctly mapped`() =
        whenState {
            // GIVEN
            val lot1 = createTestLot("LOT1", 1)
            val product1 = createTestProduct(1, "Hep A", "Havrix")
            coEvery { lotRepository.getLotsByNumber(listOf("LOT1")) } returns listOf(lot1)
            coEvery { productRepository.getProductsByIds(listOf(1)) } returns listOf(product1)
            val sessionState = mapOf("LOT1" to LotState(delta = 3, isDeleted = false))
            awaitItem()

            // WHEN
            lotStateFlow.value = sessionState

            // THEN
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(1, state.wastedProductsUi.size)
            val productUi = state.wastedProductsUi.first()
            assertEquals("LOT# LOT1", productUi.productInfoUi.subtitleLines[0].text)
            assertEquals(3, productUi.editQuantityUi.quantity)
            assertTrue(productUi.editQuantityUi.decrementEnabled)
            assertEquals(3, state.total)
        }

    @Test
    fun `GIVEN lotState has an item with quantity 1 WHEN mapped THEN decrement is disabled`() =
        whenState {
            // GIVEN
            val lot1 = createTestLot("LOT1", 1)
            val product1 = createTestProduct(1, "Test", "Test")
            coEvery { lotRepository.getLotsByNumber(any()) } returns listOf(lot1)
            coEvery { productRepository.getProductsByIds(any()) } returns listOf(product1)
            val sessionState = mapOf("LOT1" to LotState(delta = 1, isDeleted = false))
            awaitItem()

            // WHEN
            lotStateFlow.value = sessionState

            // THEN
            val state = awaitItem()
            assertFalse(
                state.wastedProductsUi
                    .first()
                    .editQuantityUi.decrementEnabled
            )
        }

    @Test
    fun `GIVEN a lot is removed WHEN lotState emits THEN total only sums non-removed items`() =
        whenState {
            // GIVEN
            val lot1 = createTestLot("LOT1", 1)
            val lot2 = createTestLot("LOT2", 1)
            val product1 = createTestProduct(1, "Hep A", "Havrix")
            coEvery { lotRepository.getLotsByNumber(any()) } returns listOf(lot1, lot2)
            coEvery { productRepository.getProductsByIds(any()) } returns listOf(product1)
            val sessionState = mapOf(
                "LOT1" to LotState(delta = 5, isDeleted = false),
                "LOT2" to LotState(delta = 2, isDeleted = true)
            )
            awaitItem() // dismiss onStart state update

            // WHEN
            lotStateFlow.value = sessionState

            // THEN
            val state = awaitItem()
            assertEquals(5, state.total)
        }

    @Test
    fun `WHEN searchedLot emits a valid lot THEN session addLotToWaste is called`() {
        // GIVEN
        val lot = createTestLot("SEARCHED_LOT", 10)
        coEvery { lotRepository.getLotByNumberAsync("SEARCHED_LOT") } returns lot

        // WHEN
        whenState {
            searchedLotFlow.value = "SEARCHED_LOT"
        }

        // THEN
        verify { session.addLotToWaste(10, "SEARCHED_LOT") }
        verify { session.clearSearchedLot() }
    }

    @Test
    fun `WHEN IncrementLotAmount is received THEN session setDelta is called with incremented value`() {
        // GIVEN
        lotStateFlow.value = mapOf("LOT1" to LotState(delta = 2))

        // WHEN
        viewModel.handleIntent(LogWasteIntent.IncrementLotAmount("LOT1"))

        // THEN
        verify { session.setDelta("LOT1", 3) }
    }

    @Test
    fun `GIVEN amount is 1 WHEN DecrementLotAmount is received THEN session setDelta is NOT called`() {
        // GIVEN
        lotStateFlow.value = mapOf("LOT1" to LotState(delta = 1))

        // WHEN
        viewModel.handleIntent(LogWasteIntent.DecrementLotAmount("LOT1"))

        // THEN
        verify(exactly = 0) { session.setDelta(any(), any()) }
    }

    @Test
    fun `WHEN BarcodeScanned with a valid lot THEN session addLotToWaste is called`() {
        // GIVEN
        val barcode = mockk<ParsedBarcode>()
        coEvery { validateScannedProductUseCase(any(), any(), any(), any()) } returns
            ScanValidationResult.Valid("VALID_LOT", 1)

        // WHEN
        viewModel.handleIntent(LogWasteIntent.BarcodeScanned(barcode))

        // THEN
        coVerify { session.addLotToWaste(1, "VALID_LOT") }
    }

    @Test
    fun `WHEN BarcodeScanned with a new lot THEN postNewLotUseCase and session addLotToWaste are called`() {
        // GIVEN
        val barcode = mockk<ParsedBarcode>()
        val expirationDate = LocalDate.now().plusYears(1)
        coEvery {
            validateScannedProductUseCase(any(), any(), any(), any())
        } returns ScanValidationResult.NewLot(
            "NEW_LOT",
            productId = 123,
            expiration = expirationDate
        )

        // WHEN
        viewModel.handleIntent(LogWasteIntent.BarcodeScanned(barcode))

        // THEN
        coVerify {
            postNewLotUseCase(
                lotNumber = "NEW_LOT",
                productId = 123,
                expiration = expirationDate,
                any()
            )
        }
        coVerify { session.addLotToWaste(123, "NEW_LOT") }
    }

    @Test
    fun `WHEN NavigateBackClicked with changes THEN DiscardChanges dialog is shown`() =
        whenState {
            // GIVEN
            every { session.containsSessionChanges() } returns true
            awaitItem() // dismiss onStart state update

            // WHEN
            viewModel.handleIntent(LogWasteIntent.NavigateBackClicked)

            // THEN
            val state = awaitItem()
            assertEquals(LogWasteDialog.DiscardChanges, state.activeDialog)
        }

    @Test
    fun `WHEN NavigateBackClicked without changes THEN GoBack event is sent`() =
        whenEvent(
            actions = {
                every { session.containsSessionChanges() } returns false
                viewModel.handleIntent(LogWasteIntent.NavigateBackClicked)
            },
            assertions = {
                assertEquals(LogWasteEvent.GoBack, awaitItem())
            }
        )

    private fun createTestLot(lotNumber: String, productId: Int) = Lot(lotNumber, productId, LocalDate.now(), productId)

    private fun createTestProduct(
        id: Int,
        antigen: String,
        name: String
    ) = Product(
        id = id,
        antigen = antigen,
        displayName = name,
        presentation = Presentation.UNKNOWN,
        categoryId = 1,
        prettyName = name,
        lossFee = 0f,
        inventoryGroup = ""
    )
}
