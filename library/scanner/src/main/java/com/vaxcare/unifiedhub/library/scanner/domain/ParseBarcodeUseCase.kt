package com.vaxcare.unifiedhub.library.scanner.domain

import com.codecorp.CDSymbology.CDSymbologyType
import com.vaxcare.unifiedhub.library.scanner.domain.parser.DriverLicenseParser
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ParseBarcodeUseCase @Inject constructor() {
    private companion object {
        const val NDC_LENGTH = 10
        const val EXP_DATE_LENGTH = 6
        const val LOT_NUMBER_MAX_LENGTH = 10
        const val NDC_START_POSITION = 5
        const val LOT_NUMBER_START_POSITION = 26
        const val EXP_DATE_START_POSITION = 18

        const val DATA_MATRIX_SYMBOL = "]d2"
        const val SEP_THREE = '\u001D'
        const val EXP_INDICATOR = "10"
        const val LOT_INDICATOR = "17"

        val YYMMDD_FORMAT = DateTimeFormatter.ofPattern("yyMMdd")
        val YYMM_FORMAT = DateTimeFormatter.ofPattern("yyMM")
    }

    operator fun invoke(
        raw: String,
        symbology: CDSymbologyType,
        scanType: ScanType
    ): ParsedBarcode? =
        when (scanType) {
            ScanType.DOSE -> parse2D(raw, symbology)
            ScanType.DRIVER_LICENSE -> parsePdf417(raw, symbology)
        }

    private fun parse2D(barcode: String, symbology: CDSymbologyType): ParsedBarcode? {
        if (symbology != CDSymbologyType.dataMatrix) {
            return null
        }
        var expStart = EXP_DATE_START_POSITION
        var lotStart = LOT_NUMBER_START_POSITION
        var cleaned = barcode

        if (isAbnormalBarcode(cleaned)) {
            parseAbnormalBarcode(cleaned).also {
                cleaned = it.barcode
                expStart = it.expDateStartPosition
                lotStart = it.lotNumberStartPosition
            }
        }

        val productNdc = cleaned.safeSubstring(NDC_START_POSITION, NDC_LENGTH)
        val expString = cleaned.safeSubstring(expStart, EXP_DATE_LENGTH)
        val expiration = parseExpirationDate(expString)

        val lotNumber = when {
            cleaned.length in (lotStart + 1)..(lotStart + LOT_NUMBER_MAX_LENGTH) ->
                cleaned.substring(lotStart).onlyAlphaNum()

            isLotBeforeExpiration(cleaned) ->
                cleaned.substring(lotStart, expStart - 2)

            else -> ""
        }.onlyAlphaNum()

        return TwoDeeBarcode(
            raw = cleaned,
            symbologyName = symbology.name,
            vialNdc = productNdc,
            lotNumber = lotNumber,
            expiration = expiration
        )
    }

    private fun parsePdf417(barcode: String, symbology: CDSymbologyType): ParsedBarcode =
        try {
            val lic: DriverLicense = DriverLicenseParser.fromBarcode(barcode)

            DriverLicenseBarcode(
                raw = barcode,
                symbologyName = symbology.name,
                firstName = lic.firstName,
                lastName = lic.lastName,
                dob = lic.dateOfBirth
            )
        } catch (e: Exception) {
            ErrorBarcode(barcode, symbology.name, "PDF417 parse error: ${e.message}")
        }

    private fun String.safeSubstring(start: Int, length: Int): String =
        if (length >= 0 && this.length >= start + length) substring(start, start + length) else ""

    private fun String.onlyAlphaNum(): String = filter(Char::isLetterOrDigit)

    private fun parseExpirationDate(exp: String): LocalDate? =
        try {
            if (exp.endsWith("00")) {
                // Only YYMM ⇒ first date of month
                YearMonth.parse(exp.dropLast(2), YYMM_FORMAT).atDay(1)
            } else {
                LocalDate.parse(exp, YYMMDD_FORMAT)
            }
        } catch (_: Exception) {
            null
        }

    private fun isAbnormalBarcode(bc: String): Boolean =
        bc.length < LOT_NUMBER_START_POSITION ||
            bc.substring(
                LOT_NUMBER_START_POSITION - 2,
                LOT_NUMBER_START_POSITION
            ) != EXP_INDICATOR ||
            bc.contains(SEP_THREE) ||
            bc.contains(DATA_MATRIX_SYMBOL) ||
            bc.startsWith('2')

    private fun isLotBeforeExpiration(bc: String): Boolean =
        bc.length >= EXP_DATE_START_POSITION &&
            bc.substring(
                EXP_DATE_START_POSITION - 2,
                EXP_DATE_START_POSITION
            ) == EXP_INDICATOR &&
            bc.takeLast(8).startsWith(LOT_INDICATOR) &&
            parseExpirationDate(bc.takeLast(6)) != null

    private fun parseAbnormalBarcode(bc: String): TwoDBarcodeParsingInfo {
        var expStart = EXP_DATE_START_POSITION
        var lotStart = LOT_NUMBER_START_POSITION
        var cleaned = bc

        if (bc.contains(DATA_MATRIX_SYMBOL)) {
            val idx = bc.indexOf(DATA_MATRIX_SYMBOL).coerceAtLeast(0)
            if (bc.substring(idx + 4, idx + 6) == "01" && bc.length > idx + 30) {
                expStart = idx + 22
                lotStart = idx + 30
            }
        }

        if (cleaned.startsWith('2')) cleaned = cleaned.drop(1)

        if (isLotBeforeExpiration(cleaned)) {
            expStart = cleaned.length - 6
            lotStart = EXP_DATE_START_POSITION
        }

        return TwoDBarcodeParsingInfo(cleaned, expStart, lotStart)
    }

    private data class TwoDBarcodeParsingInfo(
        val barcode: String,
        val expDateStartPosition: Int,
        val lotNumberStartPosition: Int
    )
}
