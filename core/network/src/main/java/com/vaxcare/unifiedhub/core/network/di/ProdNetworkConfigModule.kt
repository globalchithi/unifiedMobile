package com.vaxcare.unifiedhub.core.network.di

import com.vaxcare.unifiedhub.core.network.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProdNetworkConfigModule {
    @Provides
    @Named("BaseUrl")
    @Singleton
    fun provideBaseUrl() = BuildConfig.VAX_VHAPI_URL
}
