package com.vaxcare.unifiedhub.library.analytics.di

import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.analytics.repository.AnalyticsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindAnalyticRepository(analyticsRepositoryImpl: AnalyticsRepositoryImpl): AnalyticsRepository
}
