package com.vaxcare.unifiedhub.core.domain

import com.vaxcare.unifiedhub.core.common.ext.toLocalDateString
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.NdcCodeRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.core.data.repository.WrongProductNdcRepository
import com.vaxcare.unifiedhub.core.domain.analytics.ValidateScanAnalytics
import com.vaxcare.unifiedhub.core.domain.model.ScanValidationResult
import com.vaxcare.unifiedhub.library.scanner.domain.ParsedBarcode
import com.vaxcare.unifiedhub.library.scanner.domain.TwoDeeBarcode
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

private const val CDATA_PREFIX = "<![CDATA["
private const val CDATA_SUFFIX = "]]>"

class ValidateScannedProductUseCase @Inject constructor(
    private val lotRepository: LotRepository,
    private val ndcCodeRepository: NdcCodeRepository,
    private val wrongProductNdcRepository: WrongProductNdcRepository,
    private val productRepository: ProductRepository,
    private val analytics: ValidateScanAnalytics
) {
    suspend operator fun invoke(
        parsedBarcode: ParsedBarcode?,
        expectedProductId: Int? = null,
        existingLotNumbers: List<String>,
        analyticsScreenSource: String,
    ): ScanValidationResult {
        if (parsedBarcode == null) return ScanValidationResult.InvalidBarcode

        // We are only interested in 2D barcodes for product validation.
        val barcode = parsedBarcode as? TwoDeeBarcode
            ?: return ScanValidationResult.InvalidBarcode

        return try {
            val lotNumber = barcode.lotNumber
            val isExpired = barcode.expiration?.isBefore(LocalDate.now()) == true
            val vialNdc = barcode.vialNdc
            val ndcProductId = ndcCodeRepository.getProductIdByNdcCode(vialNdc)

            if (existingLotNumbers.any { it == lotNumber }) {
                return ScanValidationResult.DuplicateLot(lotNumber)
            }
            if (ndcProductId != null && expectedProductId != null && ndcProductId != expectedProductId) {
                return ScanValidationResult.MismatchedProduct
            }
            if (isExpired) {
                analytics.trackExpiredProduct(
                    lotNumber = lotNumber,
                    expiration = barcode.expiration!!.toLocalDateString()
                )
                return ScanValidationResult.Expired(lotNumber)
            }

            val blacklisted = wrongProductNdcRepository.getWrongProductByNdc(vialNdc)
            if (blacklisted != null) {
                analytics.trackWrongProduct(blacklisted.ndc, blacklisted.errorMessage)
                return ScanValidationResult.WrongProduct(blacklisted.errorMessage.cleanCData())
            }

            lotRepository.getLotByNumberAsync(lotNumber)?.let { foundLot ->
                sendLotAddMetric(
                    analyticsScreenSource = analyticsScreenSource,
                    parsedBarcode = parsedBarcode,
                    productSource = "2D Scan - Existing Lot",
                    productId = foundLot.productId,
                    ndc = barcode.vialNdc,
                    lotNumber = foundLot.lotNumber,
                    expirationDate = foundLot.expiration?.toLocalDateString() ?: "",
                )
                return ScanValidationResult.Valid(
                    foundLot.lotNumber,
                    foundLot.productId
                )
            }

            if ((expectedProductId == null && ndcProductId != null) ||
                (expectedProductId != null && ndcProductId == expectedProductId)
            ) {
                sendLotAddMetric(
                    analyticsScreenSource = analyticsScreenSource,
                    parsedBarcode = parsedBarcode,
                    productSource = "2D Scan - New Lot",
                    productId = ndcProductId,
                    ndc = barcode.vialNdc,
                    lotNumber = barcode.lotNumber,
                    expirationDate = barcode.expiration?.toLocalDateString() ?: ""
                )
                return ScanValidationResult.NewLot(
                    lotNumber = lotNumber,
                    expiration = barcode.expiration ?: LocalDate.of(1900, 1, 1),
                    productId = ndcProductId
                )
            } else {
                return ScanValidationResult.InvalidBarcode
            }
        } catch (e: Exception) {
            Timber.e(e, "Error validating scanned product.")
            ScanValidationResult.InvalidBarcode
        }
    }

    private suspend fun sendLotAddMetric(
        analyticsScreenSource: String,
        parsedBarcode: ParsedBarcode,
        productSource: String,
        productId: Int?,
        ndc: String,
        lotNumber: String,
        expirationDate: String
    ) {
        if (productId == null) return
        val productName = productRepository.getProductAsync(productId)?.prettyName ?: ""
        analytics.trackLotAdded(
            screenSource = analyticsScreenSource,
            symbologyScanned = parsedBarcode.symbologyName,
            productSource = productSource,
            productId = productId,
            productName = productName,
            ndc = ndc,
            lotNumber = lotNumber,
            expirationDate = expirationDate,
            rawBarcodeData = parsedBarcode.raw
        )
    }

    private fun String.cleanCData() = replace(CDATA_PREFIX, "").replace(CDATA_SUFFIX, "")
}
