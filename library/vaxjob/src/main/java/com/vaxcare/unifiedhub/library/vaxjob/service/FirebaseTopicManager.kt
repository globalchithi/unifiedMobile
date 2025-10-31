package com.vaxcare.unifiedhub.library.vaxjob.service

import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.vaxcare.unifiedhub.core.data.messaging.FcmTopicManager
import com.vaxcare.unifiedhub.core.datastore.datasource.LocationPreferenceDataSource
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseTopicManager @Inject constructor(
    private val locationPrefs: LocationPreferenceDataSource
) : FcmTopicManager {
    private var lastKnownClinicId: Long = 0L
    private var lastKnownPartnerId: Long = 0L

    companion object {
        private const val GENERAL_TOPIC = "vaxcare"
    }

    override fun subscribeToGeneralTopic() {
        Timber.d("Subscribing to general topic: $GENERAL_TOPIC")
        Firebase.messaging
            .subscribeToTopic(GENERAL_TOPIC)
            .addOnSuccessListener { Timber.i("Successfully subscribed to general topic.") }
            .addOnFailureListener { e -> Timber.e(e, "Failed to subscribe to general topic.") }
    }

    override suspend fun updateLocationSpecificTopics() {
        val newClinicId = locationPrefs.parentClinicId.firstOrNull() ?: 0L
        val newPartnerId = locationPrefs.partnerId.firstOrNull() ?: 0L

        handleTopicUpdate(oldTopicId = lastKnownClinicId, newTopicId = newClinicId, topicName = "clinic")
        handleTopicUpdate(oldTopicId = lastKnownPartnerId, newTopicId = newPartnerId, topicName = "partner")

        lastKnownClinicId = newClinicId
        lastKnownPartnerId = newPartnerId
    }

    private suspend fun handleTopicUpdate(
        oldTopicId: Long,
        newTopicId: Long,
        topicName: String
    ) {
        if (oldTopicId == newTopicId) {
            Timber.d("No change in $topicName topic ID ($newTopicId). Skipping update.")
            return
        }

        Timber.d("Updating $topicName topic from $oldTopicId to $newTopicId")
        if (oldTopicId != 0L) {
            unsubscribeFromTopic(oldTopicId, topicName)
        }
        if (newTopicId != 0L) {
            subscribeToTopic(newTopicId, topicName)
        }
    }

    private suspend fun subscribeToTopic(topicId: Long, topicName: String) {
        try {
            Firebase.messaging.subscribeToTopic(topicId.toString()).await()
            Timber.i("Successfully subscribed to $topicName topic: $topicId")
        } catch (e: Exception) {
            Timber.e(e, "Failure to subscribe to $topicName topic: $topicId")
        }
    }

    private suspend fun unsubscribeFromTopic(topicId: Long, topicName: String) {
        try {
            Firebase.messaging.unsubscribeFromTopic(topicId.toString()).await()
            Timber.i("Successfully unsubscribed from $topicName topic: $topicId")
        } catch (e: Exception) {
            Timber.e(e, "Failure to unsubscribe from $topicName topic: $topicId")
        }
    }
}
