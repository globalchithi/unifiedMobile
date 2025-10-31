package com.vaxcare.unifiedhub.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vaxcare.unifiedhub.core.database.model.OfflineRequestEntity
import com.vaxcare.unifiedhub.core.database.model.OfflineRequestInfo

@Dao
abstract class OfflineRequestDao {
    @Query("DELETE FROM OfflineRequest WHERE id IN (:ids)")
    abstract suspend fun deleteOfflineRequestsByIds(ids: List<Int>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(offlineRequests: List<OfflineRequestEntity>)

    @Query("SELECT * FROM OfflineRequest")
    abstract suspend fun getAllAsync(): List<OfflineRequestEntity>

    @Query("SELECT id, requestUri, length(requestBody) as bodySize, originalDateTime FROM OfflineRequest")
    abstract suspend fun getOfflineRequestList(): List<OfflineRequestInfo>
}
