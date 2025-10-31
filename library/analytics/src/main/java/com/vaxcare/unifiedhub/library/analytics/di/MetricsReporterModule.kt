package com.vaxcare.unifiedhub.library.analytics.di

import android.content.Context
import com.datadog.android.log.Logger
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.vaxcare.unifiedhub.library.analytics.core.MetricsReporter
import com.vaxcare.unifiedhub.library.analytics.reporters.DatadogReporter
import com.vaxcare.unifiedhub.library.analytics.reporters.MixpanelReporter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MetricsReporterModule {
    @Provides
    @IntoSet
    fun provideDatadogReporter(): MetricsReporter = DatadogReporter()

    @Provides
    fun provideMixpanelApi(
        @ApplicationContext context: Context
    ): MixpanelAPI =
        MixpanelAPI
            .getInstance(
                context,
                "e342b1f207482db06ba2deba8731af4c",
                true
            ).also { it.setEnableLogging(true) }

    @Provides
    @IntoSet
    fun provideMixpanelReporter(mixpanelAPI: MixpanelAPI): MetricsReporter = MixpanelReporter(mixpanelAPI)

    @Module
    @InstallIn(SingletonComponent::class)
    object LoggingModule {
        @Provides
        @Singleton
        fun provideDatadogLogger(): Logger =
            Logger
                .Builder()
                .setNetworkInfoEnabled(true)
                .setLogcatLogsEnabled(true)
                .setRemoteSampleRate(100f)
                .setBundleWithTraceEnabled(true)
                .build()
    }
}
