package com.vaxcare.unifiedhub.core.datastore.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.vaxcare.unifiedhub.core.datastore.PreferenceKey
import com.vaxcare.unifiedhub.core.datastore.UserDataStore
import com.vaxcare.unifiedhub.core.datastore.clearAll
import com.vaxcare.unifiedhub.core.datastore.clearValue
import com.vaxcare.unifiedhub.core.datastore.get
import com.vaxcare.unifiedhub.core.datastore.setValue
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserSessionPreferenceDataSource @Inject constructor(
    @UserDataStore private val dataStore: DataStore<Preferences>
) {
    val userId: Flow<Long> = dataStore[PreferenceKey.USER_ID, 0L]
    val userName: Flow<String> = dataStore[PreferenceKey.USER_NAME, ""]
    val sessionId: Flow<String> = dataStore[PreferenceKey.SESSION_ID, ""]

    suspend fun clear() {
        dataStore.clearAll()
    }

    suspend fun setUserSession(userId: Long, userName: String) {
        with(dataStore) {
            setValue(PreferenceKey.USER_ID, userId)
            setValue(PreferenceKey.USER_NAME, userName)
            setValue(
                PreferenceKey.SESSION_ID,
                java.util.UUID
                    .randomUUID()
                    .toString()
            )
        }
    }

    suspend fun clearUserSession() {
        with(dataStore) {
            clearValue(PreferenceKey.USER_ID)
            clearValue(PreferenceKey.USER_NAME)
            clearValue(PreferenceKey.SESSION_ID)
        }
    }
}
