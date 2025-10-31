package com.vaxcare.unifiedhub.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.vaxcare.unifiedhub.core.database.model.enums.InventorySource
import com.vaxcare.unifiedhub.core.database.model.inventory.count.CountEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.count.CountEntryEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
abstract class CountDao {
    @Query("DELETE FROM Count")
    abstract suspend fun deleteCounts()

    @Query("DELETE FROM CountEntry")
    abstract suspend fun deleteCountEntries()

    @Query("SELECT createdOn FROM Count WHERE stock = :inventorySource ORDER BY createdOn DESC LIMIT 1")
    abstract fun getLatestCountDate(inventorySource: InventorySource): Flow<LocalDateTime?>

    @Insert
    abstract suspend fun insertCounts(counts: List<CountEntity>)

    @Insert
    abstract suspend fun insertCountEntries(counts: List<CountEntryEntity>)
}
