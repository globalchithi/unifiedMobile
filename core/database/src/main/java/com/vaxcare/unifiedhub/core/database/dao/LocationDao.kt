package com.vaxcare.unifiedhub.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.vaxcare.unifiedhub.core.database.model.FeatureFlagEntity
import com.vaxcare.unifiedhub.core.database.model.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LocationDao {
    @Transaction
    @Query("SELECT * FROM Location")
    abstract fun get(): Flow<LocationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(location: LocationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertFeatureFlags(featureFlags: List<FeatureFlagEntity>)

    @Query("SELECT * FROM FeatureFlags")
    abstract suspend fun getFeatureFlags(): List<FeatureFlagEntity>

    @Query("DELETE FROM FeatureFlags")
    abstract suspend fun deleteAllFeatureFlags()

    @Transaction
    open suspend fun replaceAllFeatureFlags(featureFlags: List<FeatureFlagEntity>) {
        deleteAllFeatureFlags()
        insertFeatureFlags(featureFlags)
    }
}
