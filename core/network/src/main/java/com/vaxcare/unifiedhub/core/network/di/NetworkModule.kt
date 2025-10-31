package com.vaxcare.unifiedhub.core.network.di

import com.vaxcare.unifiedhub.core.network.interceptor.DnsSelector
import com.vaxcare.unifiedhub.core.network.interceptor.IdentifierProvider
import com.vaxcare.unifiedhub.core.network.interceptor.StatusCodeInterceptor
import com.vaxcare.unifiedhub.core.network.interceptor.TraceInterceptor
import com.vaxcare.unifiedhub.core.network.interceptor.VaxHubIdentifierInterceptor
import com.vaxcare.unifiedhub.core.network.interceptor.WebLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.Duration
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OkHttpQuickPing

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun dnsSelector(): DnsSelector = DnsSelector()

    @Provides
    @Singleton
    fun vaxHubIdentifier(identifierProvider: IdentifierProvider): Interceptor =
        VaxHubIdentifierInterceptor(identifierProvider)

    @Provides
    @Singleton
    fun okHttpClient(
        identifierInterceptor: Interceptor,
        traceInterceptor: TraceInterceptor,
        webLogger: WebLogger,
        dnsSelector: DnsSelector,
        statusCodeInterceptor: StatusCodeInterceptor
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .connectTimeout(Duration.ofSeconds(30))
            .readTimeout(Duration.ofSeconds(30))
            .writeTimeout(Duration.ofSeconds(30))
            .addInterceptor(identifierInterceptor)
            .addInterceptor(webLogger)
            .addInterceptor(traceInterceptor)
            .addInterceptor(statusCodeInterceptor)
            .dns(dnsSelector)
            .build()

    @Provides
    @Singleton
    @OkHttpQuickPing
    fun okHttpQuickPing(webLogger: WebLogger, dnsSelector: DnsSelector): OkHttpClient =
        OkHttpClient
            .Builder()
            .connectTimeout(Duration.ofSeconds(30))
            .readTimeout(Duration.ofSeconds(30))
            .writeTimeout(Duration.ofSeconds(30))
            .dns(dnsSelector)
            .addInterceptor(webLogger)
            .build()

    @Provides
    @Singleton
    fun retrofit(
        okHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory,
        @Named("BaseUrl") baseUrl: String
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(moshiConverterFactory)
            .client(okHttpClient)
            .build()
}
