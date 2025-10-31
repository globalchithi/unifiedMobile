package com.vaxcare.unifiedhub.core.datastore.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.vaxcare.unifiedhub.core.datastore.BuildConfig
import com.vaxcare.unifiedhub.core.datastore.LicenseDataStore
import com.vaxcare.unifiedhub.core.datastore.PreferenceKey
import com.vaxcare.unifiedhub.core.datastore.clearValue
import com.vaxcare.unifiedhub.core.datastore.get
import com.vaxcare.unifiedhub.core.datastore.getPreferenceWithDefault
import com.vaxcare.unifiedhub.core.datastore.setValue
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LicensePreferenceDataSource @Inject constructor(
    @LicenseDataStore private val dataStore: DataStore<Preferences>
) {
    val scannerCustomerId: String =
        dataStore.getPreferenceWithDefault(PreferenceKey.SCANNER_CUSTOMER_ID, BuildConfig.SCANNER_CUSTOMER_ID)

    val scannerLicense: String
        get() = dataStore.getPreferenceWithDefault(PreferenceKey.SCANNER_LICENSE, BuildConfig.SCANNER_LICENSE)

    val datadogClientToken: Flow<String> =
        dataStore[PreferenceKey.DATADOG_CLIENT_TOKEN, BuildConfig.DATADOG_CLIENT_TOKEN]

    val datadogApplicationId: Flow<String> =
        dataStore[PreferenceKey.DATADOG_APPLICATION_ID, BuildConfig.DATADOG_APPLICATION_ID]

    val datadogRumSampleRate: Flow<Float> =
        dataStore[
            PreferenceKey.DATADOG_RUM_SAMPLE_RATE,
            BuildConfig.DATADOG_RUM_SAMPLING_RATE.toFloat()
        ]

    val datadogSessionReplaySampleRate: Flow<Float> =
        dataStore[
            PreferenceKey.DATADOG_SESSION_REPLAY_SAMPLE_RATE,
            BuildConfig.DATADOG_SESSION_REPLAY_SAMPLING_RATE.toFloat()
        ]

    val datadogSite: Flow<String> = dataStore[PreferenceKey.DATADOG_SITE, BuildConfig.DATADOG_SITE]

    val datadogEnabled: Flow<Boolean> = dataStore[PreferenceKey.DATADOG_ENABLED, true]

    suspend fun setCustomerId(value: String) = dataStore.setValue(PreferenceKey.SCANNER_CUSTOMER_ID, value)

    @Suppress("unused")
    suspend fun clearCustomerId() = dataStore.clearValue(PreferenceKey.SCANNER_CUSTOMER_ID)

    suspend fun setScannerLicense(value: String) = dataStore.setValue(PreferenceKey.SCANNER_LICENSE, value)

    @Suppress("unused")
    suspend fun clearScannerLicense() = dataStore.clearValue(PreferenceKey.SCANNER_LICENSE)

    suspend fun setDatadogClientToken(value: String) = dataStore.setValue(PreferenceKey.DATADOG_CLIENT_TOKEN, value)

    @Suppress("unused")
    suspend fun clearDatadogClientToken() = dataStore.clearValue(PreferenceKey.DATADOG_CLIENT_TOKEN)

    suspend fun setDatadogApplicationId(value: String) = dataStore.setValue(PreferenceKey.DATADOG_APPLICATION_ID, value)

    @Suppress("unused")
    suspend fun clearDatadogApplicationId() = dataStore.clearValue(PreferenceKey.DATADOG_APPLICATION_ID)

    suspend fun setDatadogRUMSampleRate(value: Float) = dataStore.setValue(PreferenceKey.DATADOG_RUM_SAMPLE_RATE, value)

    @Suppress("unused")
    suspend fun clearDatadogRUMSampleRate() = dataStore.clearValue(PreferenceKey.DATADOG_RUM_SAMPLE_RATE)

    suspend fun setDatadogSessionReplaySampleRate(value: Float) =
        dataStore.setValue(PreferenceKey.DATADOG_SESSION_REPLAY_SAMPLE_RATE, value)

    @Suppress("unused")
    suspend fun clearDatadogSessionReplaySampleRate() =
        dataStore.clearValue(PreferenceKey.DATADOG_SESSION_REPLAY_SAMPLE_RATE)

    suspend fun setDatadogSite(value: String) = dataStore.setValue(PreferenceKey.DATADOG_SITE, value)

    @Suppress("unused")
    suspend fun clearDatadogSite() = dataStore.clearValue(PreferenceKey.DATADOG_SITE)

    suspend fun setDatadogEnabled(value: Boolean) = dataStore.setValue(PreferenceKey.DATADOG_ENABLED, value)

    @Suppress("unused")
    suspend fun clearDatadogEnabled() = dataStore.clearValue(PreferenceKey.DATADOG_ENABLED)
}
