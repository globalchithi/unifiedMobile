package com.vaxcare.unifiedhub.library.analytics.repository

import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsEnricher
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsSanitizer
import com.vaxcare.unifiedhub.library.analytics.core.MetricsReporter
import com.vaxcare.unifiedhub.library.analytics.core.TrackableEvent
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class AnalyticsRepositoryImpl @Inject constructor(
    private val reporters: Set<@JvmSuppressWildcards MetricsReporter>,
    private val enrichers: Set<@JvmSuppressWildcards AnalyticsEnricher>,
    private val sanitizer: AnalyticsSanitizer,
    private val dispatcherProvider: DispatcherProvider
) : AnalyticsRepository {
    override suspend fun track(vararg events: TrackableEvent, filter: (MetricsReporter) -> Boolean) {
        withContext(dispatcherProvider.io) {
            val defaults: AnalyticsProps = enrichers
                .flatMap { it.defaultProps().entries }
                .associate { it.toPair() }

            val enrichedEvents = events.map { original ->
                val safe = sanitizer.sanitize(original)
                object : TrackableEvent {
                    override val name = safe.name
                    override val props = defaults + safe.props
                }
            }

            reporters.filter(filter).forEach { reporter ->
                enrichedEvents.forEach { event ->
                    runCatching { reporter.track(event) }
                        .onFailure { Timber.e(it, "Reporter ${reporter::class.simpleName}") }
                        .also { Timber.d("${reporter::class.simpleName} <${it.isSuccess}>: ${event.name}") }
                }
            }
        }
    }
}
