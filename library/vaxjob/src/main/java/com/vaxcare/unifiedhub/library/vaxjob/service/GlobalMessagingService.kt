package com.vaxcare.unifiedhub.library.vaxjob.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vaxcare.unifiedhub.core.data.messaging.FcmTopicManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class GlobalMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var fcmMessageHandler: FcmMessageHandler

    @Inject
    lateinit var fcmTopicManager: FcmTopicManager

    private val serviceScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        fcmTopicManager.subscribeToGeneralTopic()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("FCM token refreshed: $token")
        serviceScope.launch {
            fcmTopicManager.updateLocationSpecificTopics()
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Timber.i("New remote message received. Delegating to FcmMessageHandler.")

        serviceScope.launch {
            fcmMessageHandler.handleRemoteMessage(remoteMessage.data)
        }
    }
}
