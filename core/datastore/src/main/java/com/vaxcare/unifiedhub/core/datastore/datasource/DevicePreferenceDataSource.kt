package com.vaxcare.unifiedhub.core.datastore.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.vaxcare.unifiedhub.core.datastore.DeviceDataStore
import com.vaxcare.unifiedhub.core.datastore.PreferenceKey
import com.vaxcare.unifiedhub.core.datastore.clearValue
import com.vaxcare.unifiedhub.core.datastore.get
import com.vaxcare.unifiedhub.core.datastore.setValue
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DevicePreferenceDataSource @Inject constructor(
    @DeviceDataStore private val dataStore: DataStore<Preferences>
) {
    val iccid: Flow<String> = dataStore[PreferenceKey.ICCID, ""]
    val imei: Flow<String> = dataStore[PreferenceKey.IMEI, ""]
    val serialNumber: Flow<String> = dataStore[PreferenceKey.SERIAL_NUMBER, ""]

    suspend fun setIccid(value: String) = dataStore.setValue(PreferenceKey.ICCID, value)

    suspend fun setImei(value: String) = dataStore.setValue(PreferenceKey.IMEI, value)

    suspend fun setSerialNumber(value: String) = dataStore.setValue(PreferenceKey.SERIAL_NUMBER, value)

    @Suppress("unused")
    suspend fun clearSerialNumber() = dataStore.clearValue(PreferenceKey.SERIAL_NUMBER)

    @Suppress("unused")
    suspend fun clearIccid() = dataStore.clearValue(PreferenceKey.ICCID)

    @Suppress("unused")
    suspend fun clearImei() = dataStore.clearValue(PreferenceKey.IMEI)
}
