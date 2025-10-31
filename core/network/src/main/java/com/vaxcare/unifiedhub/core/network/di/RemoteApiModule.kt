package com.vaxcare.unifiedhub.core.network.di

import com.vaxcare.unifiedhub.core.network.api.InventoryApi
import com.vaxcare.unifiedhub.core.network.api.NdcApi
import com.vaxcare.unifiedhub.core.network.api.PatientsApi
import com.vaxcare.unifiedhub.core.network.api.ReturnApi
import com.vaxcare.unifiedhub.core.network.api.SetupApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object RemoteApiModule {
    @Provides @Singleton
    fun patientsApi(retrofit: Retrofit): PatientsApi = retrofit.create(PatientsApi::class.java)

    @Provides @Singleton
    fun setupApi(retrofit: Retrofit): SetupApi = retrofit.create(SetupApi::class.java)

    @Provides @Singleton
    fun inventoryApi(retrofit: Retrofit): InventoryApi = retrofit.create(InventoryApi::class.java)

    @Provides @Singleton
    fun ndcApi(retrofit: Retrofit): NdcApi = retrofit.create(NdcApi::class.java)

    @Provides @Singleton
    fun returnApi(retrofit: Retrofit): ReturnApi = retrofit.create(ReturnApi::class.java)
}
