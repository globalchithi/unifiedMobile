package com.vaxcare.unifiedhub.library.scanner.domain

import android.view.View
import com.vaxcare.unifiedhub.library.scanner.data.RawScanResult
import kotlinx.coroutines.flow.Flow

interface ScannerRepository {
    fun initHardware(): Boolean

    fun activate(): Flow<Boolean>

    fun rawResults(): Flow<RawScanResult?>

    fun preview(): View?

    fun stop()

    fun pauseScanner()

    fun resumeScanner()

    suspend fun refreshLicense()
}
