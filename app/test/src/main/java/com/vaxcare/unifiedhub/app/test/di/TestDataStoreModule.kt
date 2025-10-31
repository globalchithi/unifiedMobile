package com.vaxcare.unifiedhub.app.test.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.vaxcare.unifiedhub.core.datastore.DataStoreModule
import com.vaxcare.unifiedhub.core.datastore.DeviceDataStore
import com.vaxcare.unifiedhub.core.datastore.LicenseDataStore
import com.vaxcare.unifiedhub.core.datastore.LocationDataStore
import com.vaxcare.unifiedhub.core.datastore.UsageDataStore
import com.vaxcare.unifiedhub.core.datastore.UserDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

const val TEST_DEVICE_DATASTORE_NAME = "TEST_DEVICE_STORE"
const val TEST_LICENSE_DATASTORE_NAME = "TEST_LICENSE_DATASTORE"
const val TEST_LOCATION_DATASTORE_NAME = "TEST_LOCATION_STORE"
const val TEST_USER_DATASTORE_NAME = "TEST_USER_STORE"
const val TEST_USAGE_DATASTORE_NAME = "TEST_USAGE_STORE"

/**
 * For each DataStore, we provide an instance that uses a test file
 * and it is created with PreferenceDataStoreFactory to avoid
 * `by preferencesDataStore` delegate since DataStore is very strict: there can only
 * exist a single DataStore instance per file in disk (otherwise it crashes). It is
 * designed to be a Singleton.
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataStoreModule::class] // Replaces production module
)
object TestDataStoreModule {
    @DeviceDataStore
    @Provides
    @Singleton
    fun provideDeviceDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create { context.preferencesDataStoreFile(TEST_DEVICE_DATASTORE_NAME) }

    @LicenseDataStore
    @Provides
    @Singleton
    fun provideLicenseDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create { context.preferencesDataStoreFile(TEST_LICENSE_DATASTORE_NAME) }

    @LocationDataStore
    @Provides
    @Singleton
    fun provideLocationDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create { context.preferencesDataStoreFile(TEST_LOCATION_DATASTORE_NAME) }

    @UserDataStore
    @Provides
    @Singleton
    fun provideUserDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create { context.preferencesDataStoreFile(TEST_USER_DATASTORE_NAME) }

    @UsageDataStore
    @Provides
    @Singleton
    fun provideUsageDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create { context.preferencesDataStoreFile(TEST_USAGE_DATASTORE_NAME) }
}
