package com.vaxcare.unifiedhub.library.analytics.di

import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsSanitizer
import com.vaxcare.unifiedhub.library.analytics.sanitizers.NoopSanitizer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsSanitizerModule {
    @Binds
    abstract fun bindsNoopSanitizer(noopSanitizer: NoopSanitizer): AnalyticsSanitizer
}
