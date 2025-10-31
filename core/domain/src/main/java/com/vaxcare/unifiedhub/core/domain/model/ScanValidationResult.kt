package com.vaxcare.unifiedhub.core.domain.model

import java.time.LocalDate

/**
 * Represents the result of validating a scanned product barcode.
 * This is a domain-layer model, free of any UI-specific dependencies.
 */
sealed class ScanValidationResult(
    open val lotNumber: String? = null
) {
    data class Valid(
        override val lotNumber: String,
        val productId: Int
    ) : ScanValidationResult()

    /**
     * E.g. the scanned dose belongs to a lot that doesn't exist yet in our system.
     */
    data class NewLot(
        override val lotNumber: String,
        val expiration: LocalDate,
        val productId: Int
    ) : ScanValidationResult()

    /**
     * E.g. the scanned dose belongs to a lot that is already present on the screen.
     */
    data class DuplicateLot(override val lotNumber: String) : ScanValidationResult()

    /**
     * E.g. the scanned object is the diluent or vaccine package, rather than the dose.
     */
    data class WrongProduct(val errorMessage: String) : ScanValidationResult()

    /**
     * E.g. the scanned dose is a different product than the current filter, if any.
     */
    data object MismatchedProduct : ScanValidationResult()

    data class Expired(override val lotNumber: String) : ScanValidationResult()

    /**
     * E.g. a carton of strawberries.
     *
     * a.k.a. "Unknown Product".
     */
    data object InvalidBarcode : ScanValidationResult()
}
