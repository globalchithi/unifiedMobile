package com.vaxcare.unifiedhub.library.vaxjob.di

import com.vaxcare.unifiedhub.core.data.messaging.FcmTopicManager
import com.vaxcare.unifiedhub.library.vaxjob.JobQueuer
import com.vaxcare.unifiedhub.library.vaxjob.VaxJobQueuer
import com.vaxcare.unifiedhub.library.vaxjob.service.FirebaseTopicManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MessagingModule {
    @Binds
    @Singleton
    abstract fun bindJobQueuer(impl: VaxJobQueuer): JobQueuer

    @Binds
    @Singleton
    abstract fun bindFcmTopicManager(impl: FirebaseTopicManager): FcmTopicManager
}
