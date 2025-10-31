package com.vaxcare.unifiedhub.app.test.arch

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.vaxcare.unifiedhub.app.test.di.SystemTestEntryPoint
import com.vaxcare.unifiedhub.app.test.di.TestJobEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.runBlocking
import timber.log.Timber

/**
 * A test robot for simulating and controlling system-level events and interactions
 * that don't directly involve UI, such as receiving FCM messages.
 */
class SystemRobot {
    private val context: Context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    private val testJobEntryPoint by lazy {
        EntryPointAccessors.fromApplication(
            context,
            TestJobEntryPoint::class.java
        )
    }

    /**
     * Simulates receiving a Firebase Cloud Message (FCM) and triggers the corresponding logic.
     *
     * This method directly invokes the `FcmMessageHandler` to start the process of
     * job queuing, just as if a real FCM had arrived. Thanks to the synchronous
     * test setup for WorkManager, this call is blocking and will only return once
     * the dispatched worker has finished its execution.
     *
     * @param eventType The "eventType" value from the FCM data payload. This is used
     *                  to route to the correct job. (e.g., "SYNC_LOCATION").
     * @param payload An optional JSON string representing the data payload of the message.
     */
    fun receiveFcmMessage(eventType: String, payload: String? = null) {
        Timber.i("🤖 Simulating FCM reception: eventType='$eventType', payload='$payload'")

        // 1. Get access to the Hilt dependency graph from the Application context
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            SystemTestEntryPoint::class.java
        )

        // 2. Retrieve the handler instance
        val fcmMessageHandler = entryPoint.getFcmMessageHandler()

        // 3. Create the data map, just like FirebaseMessagingService would
        val fcmData = mutableMapOf(
            "eventType" to eventType,
            "eventId" to "test-event-id-${System.currentTimeMillis()}"
        )
        payload?.let { fcmData["payload"] = it }

        // 4. Invoke the handler and block until the suspend function completes.
        //    This kicks off the entire synchronous chain:
        //    MessageHandler -> JobQueuer -> JobSelector -> WorkManager -> OneTimeWorker -> VaxJob
        runBlocking {
            fcmMessageHandler.handleRemoteMessage(fcmData)
        }

        Timber.i("🤖 FCM simulation finished. Worker execution should be complete.")
    }

    /**
     * Executes the main data sync after a fresh clinic setup.
     * This is the primary method to use in tests to ensure the app has all
     * necessary data (users, inventory, etc.) to be usable.
     * This is a blocking, synchronous call.
     */
    fun performClinicInitialDataSync() =
        apply {
            Timber.i("🤖 Prforming clinic initial data sync...")
            executeDailyJobs()
            executeHalfHourJobs()
            Timber.i("🤖 Clinic initial data sync finished.")
        }

    private fun executeDailyJobs() {
        Timber.i("  -> Executing @DailyJobs group (Users, Config, etc.)...")

        val jobExecutor = testJobEntryPoint.getDailyJobExecutor()
        runBlocking {
            jobExecutor.executeJobs()
        }
    }

    private fun executeHalfHourJobs() {
        Timber.i("  -> Executing @HalfHourJobs group (Inventory, Lots)...")
        val jobExecutor = testJobEntryPoint.getHalfHourJobExecutor()
        runBlocking {
            jobExecutor.executeJobs()
        }
    }
}
