package com.vaxcare.unifiedhub.core.domain

import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.NdcCodeRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.core.data.repository.WrongProductNdcRepository
import com.vaxcare.unifiedhub.core.domain.analytics.ValidateScanAnalytics
import com.vaxcare.unifiedhub.core.domain.model.ScanValidationResult
import com.vaxcare.unifiedhub.core.model.lot.Lot
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.model.product.Product
import com.vaxcare.unifiedhub.core.model.product.WrongProductNdc
import com.vaxcare.unifiedhub.library.scanner.domain.ErrorBarcode
import com.vaxcare.unifiedhub.library.scanner.domain.TwoDeeBarcode
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class ValidateScannedProductUseCaseTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var lotRepository: LotRepository

    @MockK
    private lateinit var ndcCodeRepository: NdcCodeRepository

    @MockK
    private lateinit var wrongProductNdcRepository: WrongProductNdcRepository

    @MockK
    private lateinit var productRepository: ProductRepository

    @MockK(relaxed = true)
    private lateinit var analytics: ValidateScanAnalytics

    private lateinit var useCase: ValidateScannedProductUseCase

    private val expectedProductId = 101
    private val screenSource = "Test Screen"

    @Before
    fun setUp() {
        useCase = ValidateScannedProductUseCase(
            lotRepository,
            ndcCodeRepository,
            wrongProductNdcRepository,
            productRepository,
            analytics
        )
    }

    @Test
    fun `GIVEN a null barcode WHEN invoked THEN returns InvalidBarcode`() =
        runTest {
            val result = useCase(
                parsedBarcode = null,
                expectedProductId = expectedProductId,
                existingLotNumbers = emptyList(),
                analyticsScreenSource = screenSource
            )
            assertTrue("Result should be InvalidBarcode", result is ScanValidationResult.InvalidBarcode)
        }

    @Test
    fun `GIVEN a non-2D barcode WHEN invoked THEN returns InvalidBarcode`() =
        runTest {
            val errorBarcode = ErrorBarcode("raw", "error", "message")

            val result = useCase(
                parsedBarcode = errorBarcode,
                expectedProductId = expectedProductId,
                existingLotNumbers = emptyList(),
                analyticsScreenSource = screenSource
            )

            assertTrue(
                "Result should be InvalidBarcode for non-2D barcodes",
                result is ScanValidationResult.InvalidBarcode
            )
        }

    @Test
    fun `GIVEN lot number already exists in session WHEN invoked THEN returns DuplicateLot`() =
        runTest {
            val barcode = createTwoDeeBarcode(lotNumber = "EXISTING_LOT")
            val existingLots = listOf("OTHER_LOT", "EXISTING_LOT")

            coEvery { ndcCodeRepository.getProductIdByNdcCode(any()) } returns expectedProductId

            val result = useCase(barcode, expectedProductId, existingLots, screenSource)

            assertEquals(ScanValidationResult.DuplicateLot("EXISTING_LOT"), result)
            coVerify(exactly = 0) {
                analytics.trackLotAdded(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any()
                )
            }
        }

    @Test
    fun `GIVEN barcode NDC maps to a different product WHEN invoked THEN returns MismatchedProduct`() =
        runTest {
            val barcode = createTwoDeeBarcode(vialNdc = "DIFFERENT_NDC")
            val otherProductId = 999
            coEvery { ndcCodeRepository.getProductIdByNdcCode("DIFFERENT_NDC") } returns otherProductId

            val result = useCase(barcode, expectedProductId, emptyList(), screenSource)

            assertTrue(
                "Result should be MismatchedProduct",
                result is ScanValidationResult.MismatchedProduct
            )
        }

    @Test
    fun `GIVEN barcode expiration is in the past WHEN invoked THEN returns Expired and tracks event`() =
        runTest {
            val expiredDate = LocalDate.now().minusDays(1)
            val barcode = createTwoDeeBarcode(lotNumber = "EXPIRED_LOT", expiration = expiredDate)
            coEvery { ndcCodeRepository.getProductIdByNdcCode(any()) } returns expectedProductId

            val result = useCase(barcode, expectedProductId, emptyList(), screenSource)

            assertTrue("Result should be Expired", result is ScanValidationResult.Expired)

            coVerify(exactly = 1) { analytics.trackExpiredProduct(eq("EXPIRED_LOT"), any()) }
        }

    @Test
    fun `GIVEN barcode NDC is blacklisted WHEN invoked THEN returns WrongProduct and tracks event`() =
        runTest {
            val barcode = createTwoDeeBarcode(vialNdc = "BLACKLISTED_NDC")
            val errorMessage = "Do not use this product"
            val wrongProduct = WrongProductNdc(ndc = "BLACKLISTED_NDC", errorMessage = errorMessage)

            coEvery { ndcCodeRepository.getProductIdByNdcCode("BLACKLISTED_NDC") } returns null
            coEvery { wrongProductNdcRepository.getWrongProductByNdc("BLACKLISTED_NDC") } returns wrongProduct
            coEvery { analytics.trackWrongProduct(any(), any()) } returns Unit

            val result = useCase(barcode, expectedProductId, emptyList(), screenSource)

            assertTrue("Result should be WrongProduct", result is ScanValidationResult.WrongProduct)
            val wrongProductResult = result as ScanValidationResult.WrongProduct
            assertEquals(errorMessage, wrongProductResult.errorMessage)
            coVerify(exactly = 1) { analytics.trackWrongProduct("BLACKLISTED_NDC", errorMessage) }
        }

    @Test
    fun `GIVEN barcode for new lot but NDC does not match WHEN invoked THEN returns InvalidBarcode`() =
        runTest {
            val barcode = createTwoDeeBarcode(vialNdc = "UNKNOWN_NDC", lotNumber = "UNKNOWN_LOT")

            coEvery { ndcCodeRepository.getProductIdByNdcCode("UNKNOWN_NDC") } returns null
            coEvery { wrongProductNdcRepository.getWrongProductByNdc("UNKNOWN_NDC") } returns null
            coEvery { lotRepository.getLotByNumberAsync("UNKNOWN_LOT") } returns null

            val result = useCase(barcode, expectedProductId, emptyList(), screenSource)

            assertTrue(
                "Result should be InvalidBarcode",
                result is ScanValidationResult.InvalidBarcode
            )
            coVerify(exactly = 0) {
                analytics.trackLotAdded(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any()
                )
            }
        }

    @Test
    fun `GIVEN repository throws exception WHEN invoked THEN returns InvalidBarcode`() =
        runTest {
            val barcode = createTwoDeeBarcode()
            val exception = RuntimeException("Database connection failed")
            coEvery { ndcCodeRepository.getProductIdByNdcCode(any()) } throws exception

            val result = useCase(barcode, expectedProductId, emptyList(), screenSource)

            assertTrue(
                "Result should be InvalidBarcode on exception",
                result is ScanValidationResult.InvalidBarcode
            )
        }

    @Test
    fun `GIVEN a valid barcode for an existing lot WHEN invoked THEN returns Valid and tracks event`() =
        runTest {
            val barcode = createTwoDeeBarcode(lotNumber = "KNOWN_LOT")
            val existingLot = Lot(
                lotNumber = "KNOWN_LOT",
                productId = expectedProductId,
                expiration = LocalDate.now().plusYears(1),
                salesProductId = expectedProductId
            )
            val product = Product(
                id = expectedProductId,
                prettyName = "Fluarix",
                antigen = "Influenza",
                displayName = "Fluarix Vaccine",
                presentation = Presentation.NASAL_SYRINGE,
                categoryId = 1,
                lossFee = 10.0f,
                inventoryGroup = "GROUP_A"
            )

            coEvery { ndcCodeRepository.getProductIdByNdcCode(barcode.vialNdc) } returns null
            coEvery { wrongProductNdcRepository.getWrongProductByNdc(barcode.vialNdc) } returns null
            coEvery { lotRepository.getLotByNumberAsync("KNOWN_LOT") } returns existingLot
            coEvery { productRepository.getProductAsync(expectedProductId) } returns product

            val result = useCase(barcode, expectedProductId, emptyList(), screenSource)

            assertEquals(ScanValidationResult.Valid("KNOWN_LOT", 101), result)
            coVerify(exactly = 1) {
                analytics.trackLotAdded(
                    screenSource = eq(screenSource),
                    symbologyScanned = any(),
                    productSource = eq("2D Scan - Existing Lot"),
                    productId = eq(expectedProductId),
                    productName = eq("Fluarix"),
                    ndc = eq(barcode.vialNdc),
                    lotNumber = eq("KNOWN_LOT"),
                    expirationDate = any(),
                    rawBarcodeData = eq(barcode.raw)
                )
            }
        }

    @Test
    fun `GIVEN a valid barcode for a new lot WHEN invoked THEN returns NewLot and tracks event`() =
        runTest {
            val barcode = createTwoDeeBarcode(lotNumber = "NEW_LOT")
            val product = Product(
                id = expectedProductId,
                prettyName = "Gardasil",
                antigen = "HPV",
                displayName = "Gardasil 9",
                presentation = Presentation.NASAL_SYRINGE,
                categoryId = 2,
                lossFee = 25.5f,
                inventoryGroup = "GROUP_B"
            )

            coEvery { ndcCodeRepository.getProductIdByNdcCode(barcode.vialNdc) } returns expectedProductId
            coEvery { wrongProductNdcRepository.getWrongProductByNdc(barcode.vialNdc) } returns null
            coEvery { lotRepository.getLotByNumberAsync("NEW_LOT") } returns null
            coEvery { productRepository.getProductAsync(expectedProductId) } returns product

            val result = useCase(barcode, expectedProductId, emptyList(), screenSource)

            assertTrue("Result should be NewLot", result is ScanValidationResult.NewLot)
            coVerify(exactly = 1) {
                analytics.trackLotAdded(
                    screenSource = eq(screenSource),
                    symbologyScanned = any(),
                    productSource = eq("2D Scan - New Lot"),
                    productId = eq(expectedProductId),
                    productName = eq("Gardasil"),
                    ndc = eq(barcode.vialNdc),
                    lotNumber = eq("NEW_LOT"),
                    expirationDate = any(),
                    rawBarcodeData = eq(barcode.raw)
                )
            }
        }

    private fun createTwoDeeBarcode(
        raw: String = "raw_barcode_data",
        symbologyName: String = "dataMatrix",
        vialNdc: String = "1234567890",
        lotNumber: String = "DEFAULT_LOT",
        expiration: LocalDate? = LocalDate.now().plusYears(1)
    ): TwoDeeBarcode = TwoDeeBarcode(raw, symbologyName, vialNdc, lotNumber, expiration)
}
