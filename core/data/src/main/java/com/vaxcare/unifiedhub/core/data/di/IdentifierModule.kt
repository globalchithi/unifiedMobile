package com.vaxcare.unifiedhub.core.data.di

import com.vaxcare.unifiedhub.core.data.repository.VaxHubIdentifierProvider
import com.vaxcare.unifiedhub.core.network.interceptor.IdentifierProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class IdentifierModule {
    @Binds
    @Singleton
    abstract fun bindIdentifierProvider(impl: VaxHubIdentifierProvider): IdentifierProvider
}
