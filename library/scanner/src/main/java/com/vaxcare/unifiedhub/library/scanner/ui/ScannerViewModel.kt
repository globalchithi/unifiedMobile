package com.vaxcare.unifiedhub.library.scanner.ui

import androidx.lifecycle.viewModelScope
import com.codecorp.CDResult
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.scanner.data.metric.BarcodeScanMetric
import com.vaxcare.unifiedhub.library.scanner.data.metric.ScannerRetryMetric
import com.vaxcare.unifiedhub.library.scanner.domain.ParseBarcodeUseCase
import com.vaxcare.unifiedhub.library.scanner.domain.ScanType
import com.vaxcare.unifiedhub.library.scanner.domain.ScannerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val REFRESH_ATTEMPTS_MAX = 3

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val scannerRepository: ScannerRepository,
    private val parseBarcode: ParseBarcodeUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val analyticsRepository: AnalyticsRepository
) : BaseViewModel<ScannerState, ScannerEvent, ScannerIntent>(ScannerState()) {
    private var scanJob: Job? = null
    private var refreshAttempts = 0

    override fun handleIntent(intent: ScannerIntent) {
        Timber.d("Handling intent: $intent")
        when (intent) {
            is ScannerIntent.Start -> start(intent.scanType)
            is ScannerIntent.Refresh -> refreshLicense(intent.scanType)
            ScannerIntent.Pause -> pauseScanner()
            ScannerIntent.Resume -> resumeScanner()
            ScannerIntent.Stop -> stop()
        }
    }

    private fun refreshLicense(type: ScanType) {
        if (refreshAttempts++ < REFRESH_ATTEMPTS_MAX) {
            viewModelScope.launch(dispatcherProvider.io) {
                setState { copy(retryStatus = RetryStatus.Running) }
                try {
                    scannerRepository.refreshLicense()
                } catch (e: Exception) {
                    Timber.e(e)
                }

                start(type)
            }
        }
    }

    private fun start(type: ScanType) {
        if (!scannerRepository.initHardware()) {
            setState {
                copy(
                    status = ScannerStatus.Stopped,
                    retryStatus = RetryStatus.Idle
                )
            }
            return
        }

        setState { copy(status = ScannerStatus.Activating) }

        scannerRepository
            .activate()
            .onEach { ok ->
                viewModelScope.launch(dispatcherProvider.io) {
                    when {
                        !ok && refreshAttempts == REFRESH_ATTEMPTS_MAX -> saveScannerRetryMetric(
                            scannerValid = false,
                            reason = "Refresh Attempts Reached"
                        )

                        !ok && refreshAttempts == 0 -> saveScannerRetryMetric(
                            scannerValid = false,
                            reason = "Invalid License"
                        )

                        ok && refreshAttempts > 1 -> saveScannerRetryMetric(
                            scannerValid = true,
                            reason = "Valid after $refreshAttempts attempts"
                        )

                        else -> Unit
                    }
                }

                setState {
                    copy(
                        status = ScannerStatus.Activation(ok),
                        retryStatus = when {
                            ok -> null
                            refreshAttempts >= REFRESH_ATTEMPTS_MAX -> {
                                RetryStatus.Block
                            }

                            else -> RetryStatus.Idle
                        }
                    )
                }
                if (ok) enterScanning(type)
            }.flowOn(dispatcherProvider.io)
            .launchIn(viewModelScope)
    }

    private fun pauseScanner() {
        scannerRepository.pauseScanner()
        updateStatus<ScannerStatus.Scanning> { ScannerStatus.Paused }
    }

    private fun resumeScanner() {
        scannerRepository.resumeScanner()
        updateStatus<ScannerStatus.Paused> { ScannerStatus.Scanning }
    }

    @OptIn(FlowPreview::class)
    private fun enterScanning(type: ScanType) {
        scanJob = scannerRepository
            .rawResults()
            .onEach {
                // We should only ever get one null response. This signifies that the camera has
                // started and it's safe to start the preview.
                if (it == null) {
                    val preview = scannerRepository.preview()

                    setState {
                        copy(
                            status = ScannerStatus.Scanning,
                            previewView = preview
                        )
                    }
                }
            }.filterNotNull()
            .filter { it.status != CDResult.CDDecodeStatus.noDecode } // Filter these meaningless results
            .map { raw -> parseBarcode(raw.data, raw.symbology, type) }
            .onEach { parsed ->
                parsed?.let {
                    sendScanMetric(parsed.raw, parsed.symbologyName)
                    sendEvent(ScannerEvent.BarcodeScanned(parsed))
                }
            }.flowOn(dispatcherProvider.io)
            .launchIn(viewModelScope)
    }

    private fun stop() {
        scanJob?.cancel()

        viewModelScope.launch(dispatcherProvider.io) {
            scannerRepository.stop()
        }

        setState {
            copy(
                status = ScannerStatus.Stopped,
                previewView = null
            )
        }
    }

    private suspend fun saveScannerRetryMetric(scannerValid: Boolean, reason: String) {
        analyticsRepository.track(ScannerRetryMetric(scannerValid, reason))
    }

    private suspend fun sendScanMetric(rawBarcode: String, symbology: String) {
        analyticsRepository.track(
            BarcodeScanMetric(rawBarcode, symbology)
        )
    }

    private inline fun <reified T : ScannerStatus> updateStatus(crossinline transform: (T) -> ScannerStatus) {
        val current = uiState.value.status
        if (current is T) setState { copy(status = transform(current)) }
    }
}
