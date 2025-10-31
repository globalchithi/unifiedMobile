package com.vaxcare.unifiedhub.library.scanner.data.di

import com.vaxcare.unifiedhub.core.datastore.datasource.LicensePreferenceDataSource
import com.vaxcare.unifiedhub.library.scanner.data.CodeCorpDataSource
import com.vaxcare.unifiedhub.library.scanner.data.ScannerRepositoryImpl
import com.vaxcare.unifiedhub.library.scanner.data.settings.CameraSettings
import com.vaxcare.unifiedhub.library.scanner.domain.ScannerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ScannerDataModule {
    @Binds
    @Singleton
    abstract fun bindScannerRepository(scannerRepositoryImpl: ScannerRepositoryImpl): ScannerRepository

    companion object {
        @Provides
        @Singleton
        fun provideCameraSettings(licensePreferenceDataSource: LicensePreferenceDataSource): CameraSettings =
            CameraSettings(licensePreferenceDataSource)

        @Provides
        @Singleton
        fun provideCodeCorpDataSource(settings: CameraSettings): CodeCorpDataSource = CodeCorpDataSource(settings)
    }
}
