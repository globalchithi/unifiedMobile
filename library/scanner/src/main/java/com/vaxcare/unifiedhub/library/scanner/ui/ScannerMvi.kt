package com.vaxcare.unifiedhub.library.scanner.ui

import android.view.View
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import com.vaxcare.unifiedhub.library.scanner.domain.ParsedBarcode
import com.vaxcare.unifiedhub.library.scanner.domain.ScanType

sealed class ScannerIntent : UiIntent {
    data class Start(val scanType: ScanType) : ScannerIntent()

    data class Refresh(val scanType: ScanType) : ScannerIntent()

    object Resume : ScannerIntent()

    object Pause : ScannerIntent()

    object Stop : ScannerIntent()
}

sealed class ScannerEvent : UiEvent {
    data class BarcodeScanned(
        val barcode: ParsedBarcode
    ) : ScannerEvent()
}

data class ScannerState(
    val status: ScannerStatus = ScannerStatus.Uninitialized,
    val previewView: View? = null,
    val retryStatus: RetryStatus? = null
) : UiState

sealed interface RetryStatus {
    /**
     * Idle - can refresh again
     */
    object Idle : RetryStatus

    /**
     * Running - currently waiting on backend call. Unable to refresh while this is the state
     */
    object Running : RetryStatus

    /**
     * Blocked - We reached the threshold of refresh attempts for this screen
     */
    object Block : RetryStatus
}

sealed interface ScannerStatus {
    object Uninitialized : ScannerStatus

    object Activating : ScannerStatus

    data class Activation(
        val ok: Boolean
    ) : ScannerStatus

    object Scanning : ScannerStatus

    object Paused : ScannerStatus

    object Stopped : ScannerStatus
}
