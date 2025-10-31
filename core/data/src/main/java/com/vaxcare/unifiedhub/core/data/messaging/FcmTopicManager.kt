package com.vaxcare.unifiedhub.core.data.messaging

/**
 * Manages subscriptions to FCM topics.
 */
interface FcmTopicManager {
    /** Subscribes to the general, app-wide topic. */
    fun subscribeToGeneralTopic()

    /**
     * Updates the partner and clinic-specific topics based on the current device's location.
     * It should handle the logic of unsubscribing from old topics and subscribing to new ones.
     */
    suspend fun updateLocationSpecificTopics()
}
