package com.vaxcare.unifiedhub.library.scanner.domain

import java.time.LocalDate

sealed interface ParsedBarcode {
    val raw: String
    val symbologyName: String
}

data class TwoDeeBarcode(
    override val raw: String,
    override val symbologyName: String,
    val vialNdc: String,
    val lotNumber: String,
    val expiration: LocalDate?
) : ParsedBarcode

data class DriverLicenseBarcode(
    override val raw: String,
    override val symbologyName: String,
    val firstName: String,
    val lastName: String,
    val dob: LocalDate
) : ParsedBarcode

data class ErrorBarcode(
    override val raw: String,
    override val symbologyName: String,
    val message: String
) : ParsedBarcode
