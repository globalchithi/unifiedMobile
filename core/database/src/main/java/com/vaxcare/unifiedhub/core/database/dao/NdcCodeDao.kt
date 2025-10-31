package com.vaxcare.unifiedhub.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class NdcCodeDao {
    @Transaction
    @Query("SELECT productId FROM NdcCode WHERE ndcCode = :ndcCode")
    abstract suspend fun getProductIdByNdcCode(ndcCode: String): Int?
}
