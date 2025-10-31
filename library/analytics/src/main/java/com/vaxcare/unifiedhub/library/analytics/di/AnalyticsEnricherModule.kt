package com.vaxcare.unifiedhub.library.analytics.di

import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsEnricher
import com.vaxcare.unifiedhub.library.analytics.enrichers.BuildAnalyticsEnricher
import com.vaxcare.unifiedhub.library.analytics.enrichers.DeviceAnalyticsEnricher
import com.vaxcare.unifiedhub.library.analytics.enrichers.LocationAnalyticsEnricher
import com.vaxcare.unifiedhub.library.analytics.enrichers.SessionAnalyticsEnricher
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsEnricherModule {
    @Binds @IntoSet
    abstract fun bindsSessionEnricher(sessionAnalyticsEnricher: SessionAnalyticsEnricher): AnalyticsEnricher

    @Binds @IntoSet
    abstract fun bindsLocationEnricher(locationAnalyticsEnricher: LocationAnalyticsEnricher): AnalyticsEnricher

    @Binds @IntoSet
    abstract fun bindsDeviceEnricher(deviceAnalyticsEnricher: DeviceAnalyticsEnricher): AnalyticsEnricher

    @Binds @IntoSet
    abstract fun bindsBuildEnricher(buildAnalyticsEnricher: BuildAnalyticsEnricher): AnalyticsEnricher
}
