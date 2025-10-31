@file:Suppress("ktlint:standard:wrapping")

package com.vaxcare.unifiedhub.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vaxcare.unifiedhub.core.database.model.inventory.lot.LotInventoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LotInventoryDao {
    @Query("DELETE FROM LotInventory")
    abstract suspend fun deleteAll()

    @Query("SELECT * FROM LotInventory WHERE productId = :productId AND inventorySource = :sourceId")
    abstract fun getLotInventoryByProductIdAndSourceId(productId: Int, sourceId: Int): Flow<List<LotInventoryEntity>>

    @Query("SELECT * FROM LotInventory WHERE inventorySource = :inventorySourceId")
    abstract fun getLotInventoryByStock(inventorySourceId: Int): Flow<List<LotInventoryEntity>>

    @Query("SELECT * FROM LotInventory WHERE lotNumber in (:lotNumbers) AND inventorySource = :inventorySourceId")
    abstract fun getLotInventory(lotNumbers: List<String>, inventorySourceId: Int): Flow<List<LotInventoryEntity>>

    @Query("SELECT * FROM LotInventory WHERE lotNumber = :lotNumber AND inventorySource = :inventorySourceId")
    abstract fun getLotInventory(lotNumber: String, inventorySourceId: Int): Flow<LotInventoryEntity?>

    @Query("""
        SELECT SUM(i.onHand * (p.lossFee / 100.0))
        FROM LotInventory i JOIN LotNumber l ON i.lotNumber = l.lotNumber JOIN Product p on p.id = l.productId
        WHERE i.inventorySource = :sourceId
    """)
    abstract fun getLotInventoryTotalValue(sourceId: Int): Float

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertLotInventory(data: List<LotInventoryEntity>)
}
