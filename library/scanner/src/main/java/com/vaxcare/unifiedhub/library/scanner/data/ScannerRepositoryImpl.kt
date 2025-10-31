package com.vaxcare.unifiedhub.library.scanner.data

import android.view.View
import com.vaxcare.unifiedhub.core.data.repository.ConfigRepository
import com.vaxcare.unifiedhub.library.scanner.domain.ScannerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScannerRepositoryImpl @Inject constructor(
    private val codeCorpDataSource: CodeCorpDataSource,
    private val configRepository: ConfigRepository
) : ScannerRepository {
    override fun initHardware(): Boolean = codeCorpDataSource.initialize()

    override fun activate(): Flow<Boolean> = codeCorpDataSource.activate()

    override fun rawResults(): Flow<RawScanResult?> = codeCorpDataSource.scanResults()

    override fun preview(): View? = codeCorpDataSource.preview()

    override fun stop() = codeCorpDataSource.stopPreview()

    override fun resumeScanner() = codeCorpDataSource.resumeScanner()

    override fun pauseScanner() = codeCorpDataSource.pauseScanner()

    override suspend fun refreshLicense() {
        configRepository.upsertSetupConfig()
    }
}
