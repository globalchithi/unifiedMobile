package com.vaxcare.unifiedhub.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vaxcare.unifiedhub.core.database.model.inventory.WrongProductNdcEntity

@Dao
abstract class WrongProductNdcDao {
    @Query("SELECT * FROM WrongProductNdc")
    abstract suspend fun getAsync(): List<WrongProductNdcEntity>

    @Query("SELECT * FROM WrongProductNdc WHERE ndc = :ndc")
    abstract suspend fun getByNdc(ndc: String): WrongProductNdcEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(wrongProductNDCList: List<WrongProductNdcEntity>)

    @Query("DELETE FROM WrongProductNdc")
    abstract suspend fun deleteAll()
}
