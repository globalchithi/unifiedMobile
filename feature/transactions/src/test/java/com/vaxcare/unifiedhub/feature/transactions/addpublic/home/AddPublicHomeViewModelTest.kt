package com.vaxcare.unifiedhub.feature.transactions.addpublic.home

import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.PackageRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.core.domain.ConvertHtmlUseCase
import com.vaxcare.unifiedhub.core.domain.CreateProductListFromLotsUseCase
import com.vaxcare.unifiedhub.core.domain.PostNewLotUseCase
import com.vaxcare.unifiedhub.core.domain.ValidateScannedProductUseCase
import com.vaxcare.unifiedhub.core.domain.model.ScanValidationResult
import com.vaxcare.unifiedhub.core.model.lot.Lot
import com.vaxcare.unifiedhub.core.model.product.Package
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.model.product.Product
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.AddPublicHomeDialog
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.AddPublicHomeEvent
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.AddPublicHomeIntent
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.AddPublicHomeState
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.AddPublicHomeViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.session.AddPublicSession
import com.vaxcare.unifiedhub.library.scanner.domain.ParsedBarcode
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate

@RunWith(JUnit4::class)
class AddPublicHomeViewModelTest : BaseViewModelTest<AddPublicHomeState, AddPublicHomeEvent, AddPublicHomeIntent>() {
    private val validateScannedProductUseCase: ValidateScannedProductUseCase = mockk(relaxed = true)
    private val postNewLotUseCase: PostNewLotUseCase = mockk(relaxed = true)
    private val convertHtmlUseCase: ConvertHtmlUseCase = mockk(relaxed = true)
    private val lotRepository: LotRepository = mockk(relaxed = true)
    private val productRepository: ProductRepository = mockk(relaxed = true)
    private val packageRepository: PackageRepository = mockk(relaxed = true)
    private val session: AddPublicSession = mockk(relaxed = true)

    private lateinit var createProductListFromLotsUseCase: CreateProductListFromLotsUseCase
    override lateinit var viewModel: AddPublicHomeViewModel

    private val lotStateFlow = MutableStateFlow<Map<String, AddPublicSession.LotState>>(emptyMap())
    private val productStateFlow =
        MutableStateFlow<Map<Int, AddPublicSession.ProductState>>(emptyMap())

    @Before
    fun setup() {
        every { session.lotState } returns lotStateFlow
        every { session.productState } returns productStateFlow

        createProductListFromLotsUseCase = CreateProductListFromLotsUseCase(
            lotRepository = lotRepository,
            productRepository = productRepository,
            packageRepository = packageRepository
        )

        viewModel = AddPublicHomeViewModel(
            dispatcherProvider = testDispatcherProvider,
            createProductListFromLotsUseCase = createProductListFromLotsUseCase,
            validateScannedProduct = validateScannedProductUseCase,
            postNewLot = postNewLotUseCase,
            convertHtml = convertHtmlUseCase,
            addPublicSession = session
        )
    }

    @Test
    fun `GIVEN a lot is removed WHEN AddPublicSession emits THEN total only sums non-removed lots`() =
        whenState {
            // GIVEN
            val lot1 = createTestLot("LOT1", 1)
            val lot2 = createTestLot("LOT2", 1)
            val product1 = createTestProduct(1, "Hep A", "Havrix")
            val package1 = createTestPackage(1, 1, 1)

            coEvery { lotRepository.getLotsByNumber(listOf("LOT1")) } returns listOf(lot1)
            coEvery { lotRepository.getLotsByNumber(listOf("LOT2")) } returns listOf(lot2)
            coEvery { productRepository.getProductsByIds(listOf(1)) } returns listOf(product1)
            coEvery { packageRepository.getOneByProductId(1) } returns package1

            val lotState = mapOf(
                "LOT1" to AddPublicSession.LotState(count = 5, isDeleted = false),
                "LOT2" to AddPublicSession.LotState(count = 2, isDeleted = true)
            )
            val productState = mapOf(
                1 to AddPublicSession.ProductState(isDeleted = false)
            )
            awaitItem() // dismiss onStart state update

            // WHEN
            lotStateFlow.value = lotState
            productStateFlow.value = productState

            // THEN
            val state = awaitItem()
            Assert.assertEquals(5, state.total)
        }

    @Test
    fun `GIVEN a product is removed WHEN AddPublicSession emits THEN total only sums non-removed products`() =
        whenState {
            // GIVEN
            val lot1 = createTestLot("LOT1", 1)
            val lot2 = createTestLot("LOT2", 2)
            val product1 = createTestProduct(1, "Hep A", "Havrix")
            val product2 = createTestProduct(2, "Hep B", "Product2")
            val package1 = createTestPackage(1, 1, 1)
            val package2 = createTestPackage(2, 2, 1)

            coEvery { lotRepository.getLotsByNumber(listOf("LOT1", "LOT2")) } returns listOf(lot1, lot2)
            coEvery { productRepository.getProductsByIds(listOf(1, 2)) } returns listOf(product1, product2)
            coEvery { packageRepository.getOneByProductId(1) } returns package1
            coEvery { packageRepository.getOneByProductId(2) } returns package2

            val lotState = mapOf(
                "LOT1" to AddPublicSession.LotState(count = 5, isDeleted = false),
                "LOT2" to AddPublicSession.LotState(count = 2, isDeleted = false)
            )
            val productState = mapOf(
                1 to AddPublicSession.ProductState(isDeleted = false),
                2 to AddPublicSession.ProductState(isDeleted = true),
            )
            awaitItem() // dismiss onStart state update

            // WHEN
            lotStateFlow.value = lotState
            awaitItem() // dismiss state update from setting lot states
            productStateFlow.value = productState

            // THEN
            val state = awaitItem()
            Assert.assertEquals(5, state.total)
        }

    @Test
    fun `WHEN BarcodeScanned with a valid lot THEN session createOrIncrementCount is called`() {
        // GIVEN
        val barcode = mockk<ParsedBarcode>()
        val validLot = createTestLot("VALID_LOT", 1)
        coEvery { validateScannedProductUseCase(any(), any(), any(), any()) } returns
            ScanValidationResult.Valid(validLot.lotNumber, validLot.productId)

        // WHEN
        viewModel.handleIntent(AddPublicHomeIntent.ScanLot(barcode))

        // THEN
        coVerify { session.createOrIncrementCount("VALID_LOT") }
    }

    @Test
    fun `WHEN BarcodeScanned with a new lot THEN postNewLotUseCase and session createOrIncrementCount are called`() {
        // GIVEN
        val barcode = mockk<ParsedBarcode>()
        val expirationDate = LocalDate.now().plusYears(1)
        coEvery {
            validateScannedProductUseCase(any(), any(), any(), any())
        } returns ScanValidationResult.NewLot("NEW_LOT", productId = 123, expiration = expirationDate)

        // WHEN
        viewModel.handleIntent(AddPublicHomeIntent.ScanLot(barcode))

        // THEN
        coVerify {
            postNewLotUseCase(
                lotNumber = "NEW_LOT",
                productId = 123,
                expiration = expirationDate,
                any()
            )
        }
        coVerify { session.createOrIncrementCount("NEW_LOT") }
    }

    @Test
    fun `WHEN NavigateBackClicked with changes THEN DiscardChanges dialog is shown`() =
        whenState {
            // GIVEN
            val lot1 = createTestLot("LOT1", 1)
            val product1 = createTestProduct(1, "Hep A", "Havrix")
            val package1 = createTestPackage(1, 1, 1)

            coEvery { lotRepository.getLotsByNumber(listOf("LOT1")) } returns listOf(lot1)
            coEvery { productRepository.getProductsByIds(listOf(1)) } returns listOf(product1)
            coEvery { packageRepository.getOneByProductId(1) } returns package1

            val lotState = mapOf(
                "LOT1" to AddPublicSession.LotState(count = 5, isDeleted = false),
            )
            val productState = mapOf(
                1 to AddPublicSession.ProductState(isDeleted = false)
            )
            awaitItem() // dismiss onStart state update
            lotStateFlow.value = lotState
            productStateFlow.value = productState
            awaitItem() // dismiss products state update

            // WHEN
            viewModel.handleIntent(AddPublicHomeIntent.CloseScreen)

            // THEN
            val state = awaitItem()
            Assert.assertEquals(AddPublicHomeDialog.DiscardChanges, state.activeDialog)
        }

    @Test
    fun `WHEN NavigateBackClicked without changes THEN GoBack event is sent`() =
        whenEvent(
            actions = {
                every { session.containsSessionChanges() } returns false
                viewModel.handleIntent(AddPublicHomeIntent.CloseScreen)
            },
            assertions = {
                Assert.assertEquals(AddPublicHomeEvent.NavigateBack, awaitItem())
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

    private fun createTestPackage(
        id: Int,
        productId: Int,
        itemCount: Int
    ) = Package(
        id = id,
        productId = productId,
        itemCount = itemCount
    )
}
