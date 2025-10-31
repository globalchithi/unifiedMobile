package com.vaxcare.unifiedhub.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.WorkManager
import com.vaxcare.unifiedhub.library.vaxjob.model.WorkerBuilder
import timber.log.Timber

class WorkerBuilderImpl : WorkerBuilder {
    private val networkConstraints = Constraints
        .Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
    private var workersStarted = false

    override fun initializeWorkers(context: Context) {
        Timber.d("initializing workers... $workersStarted")
        if (!workersStarted) {
            WorkManager.getInstance(context).let { workManager ->
                DailyPeriodicWorker.buildPeriodicWorker(workManager, networkConstraints)
                ThreeHourPeriodicWorker.buildPeriodicWorker(workManager, networkConstraints)
                OneHourPeriodicWorker.buildPeriodicWorker(workManager, networkConstraints)
                HalfHourPeriodicWorker.buildPeriodicWorker(workManager, networkConstraints)
            }
            workersStarted = true
        }
    }

    override fun destroyWorkers(context: Context) {
        if (workersStarted) {
            val wm = WorkManager.getInstance(context)
            wm.cancelAllWork()
            workersStarted = false
        }
    }
}
