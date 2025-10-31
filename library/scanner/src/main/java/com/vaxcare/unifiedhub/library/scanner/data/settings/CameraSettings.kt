package com.vaxcare.unifiedhub.library.scanner.data.settings

import com.codecorp.CDCamera
import com.vaxcare.unifiedhub.core.datastore.datasource.LicensePreferenceDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CameraSettings @Inject constructor(
    private val licensePrefs: LicensePreferenceDataSource
) {
    val cameraApi = CDCamera.CDCameraAPI.camera2
    val resolution = CDCamera.CDResolution.res1920x1080
    val focus = CDCamera.CDFocus.auto
    val agcMode = CDCamera.CDAGCMode.DISABLE
    val duplicateDelayMs = 3_000
    val enableAudio = true
    val torch = CDCamera.CDTorch.off
    val enableLowContrast = true
    val decoding = true

    val customerId: String
        get() = licensePrefs.scannerCustomerId

    val licenseKey: String
        get() = licensePrefs.scannerLicense
}
