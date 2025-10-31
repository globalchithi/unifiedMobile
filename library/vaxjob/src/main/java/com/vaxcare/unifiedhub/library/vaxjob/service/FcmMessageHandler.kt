package com.vaxcare.unifiedhub.library.vaxjob.service

import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.analytics.reporters.DatadogReporter
import com.vaxcare.unifiedhub.library.analytics.reporters.MixpanelReporter
import com.vaxcare.unifiedhub.library.vaxjob.JobQueuer
import com.vaxcare.unifiedhub.library.vaxjob.metric.FcmEventMetric
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class FcmMessageHandler @Inject constructor(
    private val jobQueuer: JobQueuer,
    private val analyticsRepository: AnalyticsRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    companion object {
        private const val KEY_EVENT_TYPE = "eventType"
        private const val KEY_EVENT_ID = "eventId"
        private const val KEY_PAYLOAD = "payload"
    }

    /**
     * Handles an incoming remote message payload.
     * This is the main entry point for logic execution.
     */
    suspend fun handleRemoteMessage(data: Map<String, String>) {
        Timber.i("Handling remote message data: $data")
        val eventType = data[KEY_EVENT_TYPE]
        val eventId = data[KEY_EVENT_ID]
        val payload = data[KEY_PAYLOAD]

        if (eventType == null) {
            Timber.w("Received FCM without an 'eventType'. Message ignored.")
            return
        }

        withContext(dispatcherProvider.io) {
            try {
                Timber.i("Queuing job for eventType: '$eventType' with payload: $payload")
                jobQueuer.queueJob(eventType, payload)

                analyticsRepository.track(
                    events = arrayOf(
                        FcmEventMetric(
                            fcmEventId = eventId,
                            eventType = eventType
                        )
                    )
                ) { it !is MixpanelReporter && it !is DatadogReporter }
                Timber.d("Job queued and analytics tracked successfully.")
            } catch (e: Exception) {
                Timber.e(e, "Error processing FCM event '$eventType'")
            }
        }
    }
}
