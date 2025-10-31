package com.vaxcare.unifiedhub.core.data.network.interceptor

import com.vaxcare.unifiedhub.core.network.interceptor.StatusCodeInterceptor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkInterceptorModule {
    @Binds
    @Singleton
    abstract fun bindStatusCodeInterceptor(statusCodeInterceptorImpl: StatusCodeInterceptorImpl): StatusCodeInterceptor
}
