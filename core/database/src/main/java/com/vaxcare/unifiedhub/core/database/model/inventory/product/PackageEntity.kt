package com.vaxcare.unifiedhub.core.database.model.inventory.product

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "Package")
@JsonClass(generateAdapter = true)
data class PackageEntity(
    @PrimaryKey val id: Int,
    val description: String,
    val itemCount: Int,
    val productId: Int,
    val salesProductId: Int
) {
    @Ignore
    var packageNdcs: List<String> = listOf()
}
