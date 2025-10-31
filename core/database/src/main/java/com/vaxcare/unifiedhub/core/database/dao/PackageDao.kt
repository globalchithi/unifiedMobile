package com.vaxcare.unifiedhub.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.vaxcare.unifiedhub.core.database.model.inventory.product.PackageEntity

@Dao
abstract class PackageDao {
    @Query("SELECT * FROM Package WHERE productId = :productId LIMIT 1")
    abstract suspend fun getOneByProductId(productId: Int): PackageEntity
}
