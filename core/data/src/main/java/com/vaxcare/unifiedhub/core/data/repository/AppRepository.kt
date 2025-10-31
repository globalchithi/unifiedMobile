package com.vaxcare.unifiedhub.core.data.repository

import com.vaxcare.unifiedhub.core.database.AppDatabase
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val appDatabase: AppDatabase
) {
    /**
     * Irreversibly delete all data stored on the device via Room.
     */
    fun wipeDatabase() = appDatabase.clearAllTables()
}
