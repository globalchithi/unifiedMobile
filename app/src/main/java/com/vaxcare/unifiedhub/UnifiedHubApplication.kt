package com.vaxcare.unifiedhub

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import com.vaxcare.unifiedhub.core.common.di.ApplicationScope
import com.vaxcare.unifiedhub.core.data.messaging.FcmTopicManager
import com.vaxcare.unifiedhub.core.data.repository.LocationRepository
import com.vaxcare.unifiedhub.library.analytics.managers.DatadogManager
import com.vaxcare.unifiedhub.library.analytics.reporters.MixpanelReporter
import com.vaxcare.unifiedhub.library.vaxjob.model.WorkerBuilder
import com.vaxcare.unifiedhub.worker.OneTimeParams
import com.vaxcare.unifiedhub.worker.OneTimeWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import androidx.work.Configuration as WorkConfiguration

@HiltAndroidApp
class UnifiedHubApplication :
    Application(),
    WorkConfiguration.Provider {
    @Inject
    lateinit var mixpanelReporter: MixpanelReporter

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var workerBuilder: WorkerBuilder

    @Inject
    lateinit var datadogManager: DatadogManager

    @Inject
    lateinit var locationRepository: LocationRepository

    @Inject
    lateinit var fcmTopicManager: FcmTopicManager

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override val workManagerConfiguration: WorkConfiguration
        get() = WorkConfiguration
            .Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(applicationContext)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        applicationScope.launch { datadogManager.configureAndEnable() }
        configureWorkersAndFcmOnLocationSync()
    }

    override fun onTerminate() {
        super.onTerminate()

        applicationScope.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun configureWorkersAndFcmOnLocationSync() {
        applicationScope.launch(Dispatchers.IO) {
            locationRepository.pidCid
                .flatMapLatest { (pid, cid) ->
                    if (pid > 0 && cid > 0) {
                        // If we have valid location IDs, return them to proceed.
                        flowOf(pid to cid)
                    } else {
                        // If IDs are not valid, emit nothing to prevent execution.
                        emptyFlow()
                    }
                }.collectLatest { (_, _) ->
                    workerBuilder.destroyWorkers(this@UnifiedHubApplication)
                    workerBuilder.initializeWorkers(this@UnifiedHubApplication)

                    fcmTopicManager.updateLocationSpecificTopics()

                    OneTimeWorker.buildOneTimeUniqueWorker(
                        wm = WorkManager.getInstance(this@UnifiedHubApplication),
                        parameters = OneTimeParams.PingJob
                    )
                }
        }
    }
}
