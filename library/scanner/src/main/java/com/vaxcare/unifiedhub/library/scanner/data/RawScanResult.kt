package com.vaxcare.unifiedhub.library.scanner.data

import com.codecorp.CDResult
import com.codecorp.CDSymbology

data class RawScanResult(
    val data: String,
    val symbology: CDSymbology.CDSymbologyType,
    val status: CDResult.CDDecodeStatus
)
