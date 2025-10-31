package com.vaxcare.unifiedhub.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.vaxcare.unifiedhub.core.database.model.inventory.ClinicEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ClinicDao {
    @Query("DELETE FROM Clinics")
    abstract suspend fun deleteAll()

    @Query("SELECT Count(*) FROM Clinics WHERE clinicType = 0")
    abstract fun getNoOfPermanentClinics(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(data: List<ClinicEntity>)

    @Transaction
    @Insert
    suspend fun replaceAll(data: List<ClinicEntity>) {
        deleteAll()
        insert(data)
    }
}
