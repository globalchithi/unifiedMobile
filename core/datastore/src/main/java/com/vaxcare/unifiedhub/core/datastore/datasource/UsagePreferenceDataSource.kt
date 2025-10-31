package com.vaxcare.unifiedhub.core.datastore.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.vaxcare.unifiedhub.core.datastore.PreferenceKey
import com.vaxcare.unifiedhub.core.datastore.UsageDataStore
import com.vaxcare.unifiedhub.core.datastore.clearValue
import com.vaxcare.unifiedhub.core.datastore.get
import com.vaxcare.unifiedhub.core.datastore.setValue
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

const val PRIVATE_STOCK_ID = 1

class UsagePreferenceDataSource @Inject constructor(
    @UsageDataStore private val dataStore: DataStore<Preferences>
) {
    /**
     * The id of the stock last selected by a user on the Home screen.
     */
    val lastSelectedStock: Flow<Int> = dataStore[PreferenceKey.LAST_SELECTED_STOCK, PRIVATE_STOCK_ID]

    suspend fun setLastSelectedStock(value: Int) = dataStore.setValue(PreferenceKey.LAST_SELECTED_STOCK, value)

    suspend fun clearLastSelectedStock() {
        dataStore.clearValue(PreferenceKey.LAST_SELECTED_STOCK)
    }
}
