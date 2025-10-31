package com.vaxcare.unifiedhub.core.domain.analytics

/**
 * An abstraction that defines the contract for tracking analytics events
 * related to the ValidateScannedProductUseCase.
 * This allows the UseCase to remain decoupled from specific feature-level
 * metric implementations.
 */
interface ValidateScanAnalytics {
    suspend fun trackWrongProduct(ndc: String, displayedMessage: String)

    suspend fun trackExpiredProduct(lotNumber: String, expiration: String)

    suspend fun trackLotAdded(
        screenSource: String,
        symbologyScanned: String,
        productSource: String,
        productId: Int,
        productName: String,
        ndc: String,
        lotNumber: String,
        expirationDate: String,
        rawBarcodeData: String
    )
}
