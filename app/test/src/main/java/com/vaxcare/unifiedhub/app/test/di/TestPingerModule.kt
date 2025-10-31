package com.vaxcare.unifiedhub.app.test.di

import com.vaxcare.unifiedhub.app.test.fakes.FakeQuickPinger
import com.vaxcare.unifiedhub.core.data.di.PingModule
import com.vaxcare.unifiedhub.core.network.pinger.QuickPinger
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [PingModule::class]
)
abstract class TestPingerModule {
    @Binds
    @Singleton
    abstract fun bindQuickPinger(fake: FakeQuickPinger): QuickPinger
}
