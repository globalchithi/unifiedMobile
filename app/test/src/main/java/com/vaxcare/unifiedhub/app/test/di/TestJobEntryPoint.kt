package com.vaxcare.unifiedhub.app.test.di

import com.vaxcare.unifiedhub.di.DailyJobs
import com.vaxcare.unifiedhub.di.HalfHourJobs
import com.vaxcare.unifiedhub.library.vaxjob.model.JobExecutor
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * An Hilt EntryPoint to allow test-only classes (like Robots) to access
 * specific JobExecutor instances from the SingletonComponent graph for direct execution.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface TestJobEntryPoint {
    /**
     * Provides the singleton instance of the JobExecutor that is qualified
     * with @DailyJobs. This contains all the daily synchronization jobs.
     */
    @DailyJobs
    fun getDailyJobExecutor(): JobExecutor

    /** Provides the JobExecutor containing all half-hourly jobs (e.g., inventory). */
    @HalfHourJobs
    fun getHalfHourJobExecutor(): JobExecutor
}
