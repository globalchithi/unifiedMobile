package com.vaxcare.unifiedhub.testing.testnetwork

import com.vaxcare.unifiedhub.core.network.di.NetworkModule
import com.vaxcare.unifiedhub.core.network.di.OkHttpQuickPing
import com.vaxcare.unifiedhub.core.network.di.ProdNetworkConfigModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkModule::class, ProdNetworkConfigModule::class]
)
object TestNetworkModule {
    @Provides @Singleton
    fun mockWebServer(): MockWebServer = MockWebServerHolder.server

    @Provides @Singleton
    fun baseUrl(server: MockWebServer): String =
        requireNotNull(TestUrlHolder.baseUrl) {
            "TestUrlHolder.baseUrl was not initialized, Is TestServerRule running?"
        }

    @Provides @Singleton
    fun okHttp(server: MockWebServer): OkHttpClient =
        OkHttpClient
            .Builder()
            .dns(Dns.SYSTEM)
            .build()

    @Provides
    @Singleton
    @OkHttpQuickPing
    fun okHttpQuickPing(mainTestClient: OkHttpClient): OkHttpClient = mainTestClient

    @Provides @Singleton
    fun retrofit(
        okHttp: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory,
        baseUrl: String
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(moshiConverterFactory)
            .client(okHttp)
            .build()
}
