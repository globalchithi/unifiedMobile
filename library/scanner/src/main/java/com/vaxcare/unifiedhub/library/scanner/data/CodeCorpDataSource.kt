package com.vaxcare.unifiedhub.library.scanner.data

import android.view.View
import com.codecorp.CDCamera
import com.codecorp.CDDecoder
import com.codecorp.CDDevice
import com.codecorp.CDLicense
import com.codecorp.CDLicenseResult
import com.codecorp.CDPerformanceFeatures
import com.codecorp.CDResult
import com.vaxcare.unifiedhub.library.scanner.data.settings.CameraSettings
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CodeCorpDataSource @Inject constructor(
    private val settings: CameraSettings
) {
    private var preview: View? = null

    fun initialize(): Boolean =
        try {
            with(CDCamera.shared) {
                cameraAPI = settings.cameraApi
                resolution = settings.resolution
                focus = settings.focus
                agcMode = settings.agcMode
                torch = settings.torch
            }

            with(CDDecoder.shared) {
                setDuplicateDelay(settings.duplicateDelayMs)
                setBarcodesToDecode(1, true)
                decoding = settings.decoding
            }

            CDPerformanceFeatures.shared.lowContrast = settings.enableLowContrast

            CDDevice.shared.audio = settings.enableAudio

            CDLicense.shared.setCustomerID(settings.customerId)
            true
        } catch (e: Exception) {
            Timber.e(e, "Error initializing scanner")
            false
        }

    fun activate(): Flow<Boolean> =
        callbackFlow {
            CDLicense.shared.activateLicense(settings.licenseKey) { res ->
                trySend(res.status == CDLicenseResult.CDLicenseStatus.activated)
                close()
            }
            awaitClose { /* nothing */ }
        }

    fun scanResults(): Flow<RawScanResult?> =
        callbackFlow {
            val cb: (Array<out CDResult?>?) -> Unit = { arr ->
                arr?.firstOrNull()?.let { result ->
                    trySend(RawScanResult(result.barcodeData, result.rawSymbology, result.status))
                }
            }
            startCDCameraVideoCapture(cb)

            // Certain actions (like starting the preview) do not work with CodeCorp's 4.7.1 version
            // until after the camera has started. Sending a null here let's our caller know that
            // the camera has started.
            trySend(null)

            awaitClose {
                stopCDCameraVideoCapture()
                CDDecoder.shared.decoding = false
            }
        }.buffer(Channel.UNLIMITED)

    fun preview(): View? {
        if (preview == null) {
            preview = CDCamera.shared.startPreview()
        }
        return preview
    }

    fun stopPreview() {
        stopCDCameraVideoCapture()
        CDDecoder.shared.decoding = false
        preview = null
    }

    fun resumeScanner() {
        CDDecoder.shared.decoding = true
    }

    fun pauseScanner() {
        CDDecoder.shared.decoding = false
    }

    private fun startCDCameraVideoCapture(callback: (Array<out CDResult?>?) -> Unit) {
        try {
            CDCamera.shared.startCamera(callback)
            CDCamera.shared.videoCapturing = true
        } catch (e: Exception) {
            Timber.e("Camera exception caught: ${e.message}")
        }
    }

    private fun stopCDCameraVideoCapture() {
        try {
            CDCamera.shared.stopPreview()
            CDCamera.shared.videoCapturing = false
        } catch (e: Exception) {
            Timber.e("Camera exception caught: ${e.message}")
        }
    }
}
