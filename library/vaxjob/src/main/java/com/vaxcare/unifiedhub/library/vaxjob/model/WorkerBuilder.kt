package com.vaxcare.unifiedhub.library.vaxjob.model

import android.content.Context

interface WorkerBuilder {
    fun initializeWorkers(context: Context)

    fun destroyWorkers(context: Context)
}
