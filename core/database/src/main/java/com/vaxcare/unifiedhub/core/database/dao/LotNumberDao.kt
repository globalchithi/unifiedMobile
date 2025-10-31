package com.vaxcare.unifiedhub.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vaxcare.unifiedhub.core.database.model.inventory.lot.LotNumberEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
abstract class LotNumberDao {
    @Query("delete from LotNumber")
    abstract fun deleteAll()

    @Query("SELECT * FROM LotNumber WHERE expirationDate < :date")
    abstract fun getLotsExpiringBefore(date: LocalDate): Flow<List<LotNumberEntity>>

    @Query("SELECT * FROM LotNumber WHERE lotNumber = :lotNumber")
    abstract fun getLotByNumber(lotNumber: String): Flow<LotNumberEntity?>

    @Query("SELECT * FROM LotNumber WHERE lotNumber in (:lotNumbers)")
    abstract fun getLotsByNumber(lotNumbers: List<String>): List<LotNumberEntity>

    @Query("SELECT productId FROM LotNumber WHERE lotNumber = :lotNumber ")
    abstract suspend fun getProductIdByLotNumber(lotNumber: String): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(lots: List<LotNumberEntity>)

    @Query("SELECT * FROM LotNumber")
    abstract fun getAllLots(): Flow<List<LotNumberEntity>>

    @Query("SELECT * FROM LotNumber WHERE productId = :productId")
    abstract fun getLotsByProductId(productId: Int): Flow<List<LotNumberEntity>>

    @Query("DELETE FROM LotNumber WHERE lotNumber in (:lotNumbers)")
    abstract suspend fun deleteLotsByLotNumber(lotNumbers: List<String>)
}
