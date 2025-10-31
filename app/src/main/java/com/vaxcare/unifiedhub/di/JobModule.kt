package com.vaxcare.unifiedhub.di

import android.content.Context
import androidx.work.WorkManager
import com.squareup.moshi.Moshi
import com.vaxcare.unifiedhub.core.datastore.datasource.LocationPreferenceDataSource
import com.vaxcare.unifiedhub.jobs.ClinicJob
import com.vaxcare.unifiedhub.jobs.ConfigJob
import com.vaxcare.unifiedhub.jobs.LocationJob
import com.vaxcare.unifiedhub.jobs.LotInventoryJob
import com.vaxcare.unifiedhub.jobs.LotNumbersJob
import com.vaxcare.unifiedhub.jobs.ProductJob
import com.vaxcare.unifiedhub.jobs.UserJob
import com.vaxcare.unifiedhub.jobs.WrongProductNdcJob
import com.vaxcare.unifiedhub.jobs.provider.UnifiedVaxJobProvider
import com.vaxcare.unifiedhub.library.vaxjob.model.JobExecutor
import com.vaxcare.unifiedhub.library.vaxjob.model.JobExecutorImpl
import com.vaxcare.unifiedhub.library.vaxjob.model.WorkerBuilder
import com.vaxcare.unifiedhub.library.vaxjob.provider.VaxJobProvider
import com.vaxcare.unifiedhub.library.vaxjob.service.JobSelector
import com.vaxcare.unifiedhub.worker.JobSelectorImpl
import com.vaxcare.unifiedhub.worker.WorkerBuilderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DailyJobs

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ThreeHourJobs

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HourlyJobs

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HalfHourJobs

@Module
@InstallIn(SingletonComponent::class)
object JobModule {
    @Provides
    @DailyJobs
    @Singleton
    fun provideDailyJobs(
        userJob: UserJob,
        locationJob: LocationJob,
        configJob: ConfigJob,
        productJob: ProductJob,
        wrongProductJob: WrongProductNdcJob,
        clinicJob: ClinicJob,
    ): JobExecutor =
        JobExecutorImpl(
            listOf(
                userJob,
                locationJob,
                configJob,
                productJob,
                wrongProductJob,
                clinicJob
            )
        )

    @Provides
    @ThreeHourJobs
    @Singleton
    fun provideThreeHourJobs(): JobExecutor = JobExecutorImpl(listOf())

    @Provides
    @HourlyJobs
    @Singleton
    fun provideHourlyJobs(): JobExecutor = JobExecutorImpl(listOf())

    @Provides
    @HalfHourJobs
    @Singleton
    fun provideHalfHourJobs(lotInventoryJob: LotInventoryJob, lotNumbersJob: LotNumbersJob): JobExecutor =
        JobExecutorImpl(listOf(lotInventoryJob, lotNumbersJob))

    @Provides
    @Singleton
    fun provideJobSelector(
        @ApplicationContext context: Context,
        moshi: Moshi,
        locationPreferenceRepository: LocationPreferenceDataSource
    ): JobSelector =
        JobSelectorImpl(
            context = context,
            moshi = moshi,
            locationPrefs = locationPreferenceRepository
        )

    @Provides
    @Singleton
    fun provideWorkerBuilder(): WorkerBuilder = WorkerBuilderImpl()

    @Provides
    @Singleton
    fun provideUnifiedVaxJobProvider(
        @ApplicationContext context: Context
    ): VaxJobProvider = UnifiedVaxJobProvider(WorkManager.getInstance(context))
}
