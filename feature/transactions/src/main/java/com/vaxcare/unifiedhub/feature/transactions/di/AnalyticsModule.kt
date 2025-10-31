package com.vaxcare.unifiedhub.feature.transactions.di

import com.vaxcare.unifiedhub.core.domain.analytics.ValidateScanAnalytics
import com.vaxcare.unifiedhub.feature.transactions.analytics.ValidateScanAnalyticsImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class AnalyticsModule {
    @Binds
    abstract fun bindValidateScanAnalytics(impl: ValidateScanAnalyticsImpl): ValidateScanAnalytics
}
