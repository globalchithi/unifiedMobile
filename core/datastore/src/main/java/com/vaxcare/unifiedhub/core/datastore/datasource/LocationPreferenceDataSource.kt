package com.vaxcare.unifiedhub.core.datastore.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.vaxcare.unifiedhub.core.datastore.LocationDataStore
import com.vaxcare.unifiedhub.core.datastore.PreferenceKey
import com.vaxcare.unifiedhub.core.datastore.clearAll
import com.vaxcare.unifiedhub.core.datastore.clearValue
import com.vaxcare.unifiedhub.core.datastore.get
import com.vaxcare.unifiedhub.core.datastore.setValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class LocationPreferenceDataSource @Inject constructor(
    @LocationDataStore private val dataStore: DataStore<Preferences>
) {
    val partnerId: Flow<Long> = dataStore[PreferenceKey.PARTNER_ID, 0L]
    val parentClinicId: Flow<Long> = dataStore[PreferenceKey.PARENT_CLINIC_ID, 0L]
    val clinicId: Flow<Long> = dataStore[PreferenceKey.CLINIC_ID, 0L]
    val tabletId: Flow<String> = dataStore[PreferenceKey.TABLET_ID, ""]
    val isLocationSynced: Flow<Boolean> = dataStore[PreferenceKey.IS_LOCATION_SYNCED, false]
    val lastUsersSyncDate: Flow<String> = dataStore[PreferenceKey.LAST_USER_SYNC_DATE, ""]

    suspend fun getCurrentClinicId(): Long {
        val clinicId = clinicId.firstOrNull()
        return if (clinicId == 0L || clinicId == null) {
            parentClinicId.firstOrNull() ?: 0L
        } else {
            clinicId
        }
    }

    suspend fun clear() {
        dataStore.clearAll()
    }

    suspend fun setPartnerId(value: Long) =
        dataStore.setValue(
            key = PreferenceKey.PARTNER_ID,
            value = value
        )

    suspend fun setParentClinicId(value: Long) =
        dataStore.setValue(
            key = PreferenceKey.PARENT_CLINIC_ID,
            value = value
        )

    suspend fun setClinicId(value: Long) =
        dataStore.setValue(
            key = PreferenceKey.CLINIC_ID,
            value = value
        )

    suspend fun setTabletId(value: String) =
        dataStore.setValue(
            key = PreferenceKey.TABLET_ID,
            value = value
        )

    suspend fun setLastUserSyncDate(value: String) =
        dataStore.setValue(
            key = PreferenceKey.LAST_USER_SYNC_DATE,
            value = value
        )

    suspend fun setIsLocationSynced(value: Boolean) =
        dataStore.setValue(
            key = PreferenceKey.IS_LOCATION_SYNCED,
            value = value
        )

    suspend fun clearPartnerId() = dataStore.clearValue(PreferenceKey.PARTNER_ID)

    suspend fun clearParentClinicId() = dataStore.clearValue(PreferenceKey.PARENT_CLINIC_ID)

    suspend fun clearClinicId() = dataStore.clearValue(PreferenceKey.CLINIC_ID)

    suspend fun clearLastUserSyncDate() = dataStore.clearValue(PreferenceKey.LAST_USER_SYNC_DATE)
}
