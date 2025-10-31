package com.vaxcare.unifiedhub.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.vaxcare.unifiedhub.BuildConfig
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.datastore.datasource.LocationPreferenceDataSource
import com.vaxcare.unifiedhub.di.HalfHourJobs
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.vaxjob.model.JobExecutor
import com.vaxcare.unifiedhub.library.vaxjob.worker.BasePeriodicWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltWorker
class HalfHourPeriodicWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    override val analyticsRepository: AnalyticsRepository
) : BasePeriodicWorker(context, params) {
    companion object {
        private const val WORKER_NAME = "${BuildConfig.APPLICATION_ID}.WORKER.HALF_HOUR_PERIODIC"

        /**
         * Builds a periodic worker and queues it up in the work manager.
         *
         * @param wm WorkManager instance
         */
        fun buildPeriodicWorker(wm: WorkManager, networkConstraints: Constraints): PeriodicWorkRequest {
            val work = PeriodicWorkRequestBuilder<HalfHourPeriodicWorker>(30, TimeUnit.MINUTES)
                .setConstraints(networkConstraints)
                .build()
            wm.enqueueUniquePeriodicWork(
                WORKER_NAME,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                work
            )
            return work
        }
    }

    @Inject
    @HalfHourJobs
    override lateinit var executor: JobExecutor

    @Inject
    override lateinit var locationPreferenceDataSource: LocationPreferenceDataSource

    @Inject
    override lateinit var dispatcherProvider: DispatcherProvider
}
