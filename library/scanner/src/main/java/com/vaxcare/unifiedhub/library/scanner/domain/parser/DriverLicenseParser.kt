package com.vaxcare.unifiedhub.library.scanner.domain.parser

import com.vaxcare.unifiedhub.library.scanner.domain.DriverLicense
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DriverLicenseParser {
    /**
     * Converts PDF-417 into a [DriverLicense].
     * */
    fun fromBarcode(barcode: String): DriverLicense {
        val dl = DriverLicenseBuilder()
        barcode.split('\n').forEachIndexed { idx, raw ->
            val item = if (idx == 1) raw.replace(Regex("^[^\n]+DL"), "") else raw
            if (item.length <= 3) return@forEachIndexed
            val code = Code.valuesOrNull(item.substring(0, 3).trim()) ?: return@forEachIndexed
            val value = item.substring(3)

            when (code) {
                Code.DAA -> value.split(',').let {
                    dl.last = it.getOrNull(0) ?: ""
                    dl.first = it.getOrNull(1) ?: ""
                    dl.middle = it.getOrNull(2) ?: ""
                }

                Code.DAB, Code.DCS -> dl.last = value
                Code.DAC, Code.DCT -> dl.first = value
                Code.DAD -> dl.middle = value
                Code.DAG -> dl.street = value
                Code.DAI, Code.DAN -> dl.city = value
                Code.DAJ, Code.DAO -> dl.state = value
                Code.DAK, Code.DAP -> dl.zip = value
                Code.DAQ -> dl.licNo = value
                Code.DBB -> dl.dob = parseDate(value)
                Code.DBC -> dl.gender = when (value) {
                    "F", "2" -> DriverLicense.Gender.FEMALE
                    else -> DriverLicense.Gender.MALE
                }
            }
        }
        return dl.build()
    }

    private fun parseDate(raw: String): LocalDate =
        try {
            LocalDate.parse(raw, DateTimeFormatter.ofPattern("yyyyMMdd"))
        } catch (_: Exception) {
            LocalDate.parse(raw, DateTimeFormatter.ofPattern("MMddyyyy"))
        }

    private enum class Code {
        DAA,
        DAB,
        DAC,
        DAD,
        DAG,
        DAI,
        DAJ,
        DAK,
        DAN,
        DAO,
        DAP,
        DAQ,
        DBB,
        DBC,
        DCS,
        DCT;

        companion object {
            fun valuesOrNull(v: String) = entries.find { it.name == v }
        }
    }

    private class DriverLicenseBuilder {
        var first = ""
        var middle = ""
        var last = ""
        var gender = DriverLicense.Gender.MALE
        var street = ""
        var city = ""
        var state = ""
        var zip = ""
        var licNo = ""
        var dob = LocalDate.of(1900, 1, 1)

        fun build() =
            DriverLicense(
                firstName = first,
                middleName = middle,
                lastName = last,
                gender = gender,
                street = street,
                city = city,
                state = state,
                zip = zip,
                licenseNo = licNo,
                dateOfBirth = dob
            )
    }
}
