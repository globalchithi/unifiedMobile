package com.vaxcare.unifiedhub.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

private const val DEVICE_DATASTORE_NAME = "DEVICE_STORE"
private const val LICENSE_DATASTORE_NAME = "LICENSE_DATASTORE"
private const val LOCATION_DATASTORE_NAME = "LOCATION_STORE"
private const val USER_DATASTORE_NAME = "USER_STORE"
private const val USAGE_DATASTORE_NAME = "USAGE_STORE"

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DeviceDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LicenseDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocationDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UsageDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserDataStore

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {
    private val Context.deviceDataStore: DataStore<Preferences> by preferencesDataStore(
        DEVICE_DATASTORE_NAME
    )
    private val Context.licenseDataStore: DataStore<Preferences> by preferencesDataStore(
        LICENSE_DATASTORE_NAME
    )
    private val Context.locationDataStore: DataStore<Preferences> by preferencesDataStore(
        LOCATION_DATASTORE_NAME
    )
    private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(
        USER_DATASTORE_NAME
    )
    private val Context.usageDataStore: DataStore<Preferences> by preferencesDataStore(
        USAGE_DATASTORE_NAME
    )

    @DeviceDataStore
    @Provides
    @Singleton
    fun provideDeviceDataStore(
        @ApplicationContext applicationContext: Context
    ): DataStore<Preferences> = applicationContext.deviceDataStore

    @LicenseDataStore
    @Provides
    @Singleton
    fun provideLicenseDataStore(
        @ApplicationContext applicationContext: Context
    ): DataStore<Preferences> = applicationContext.licenseDataStore

    @LocationDataStore
    @Provides
    @Singleton
    fun provideLocationDataStore(
        @ApplicationContext applicationContext: Context
    ): DataStore<Preferences> = applicationContext.locationDataStore

    @UserDataStore
    @Provides
    @Singleton
    fun provideUserDataStore(
        @ApplicationContext applicationContext: Context
    ): DataStore<Preferences> = applicationContext.userDataStore

    @UsageDataStore
    @Provides
    @Singleton
    fun provideUsageDataStore(
        @ApplicationContext applicationContext: Context
    ): DataStore<Preferences> = applicationContext.usageDataStore
}
