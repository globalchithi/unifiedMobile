package com.vaxcare.unifiedhub.core.data.di

import com.vaxcare.unifiedhub.core.data.datasource.HttpQuickPing
import com.vaxcare.unifiedhub.core.network.pinger.QuickPinger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PingModule {
    @Binds
    @Singleton
    abstract fun bindQuickPinger(impl: HttpQuickPing): QuickPinger
}
