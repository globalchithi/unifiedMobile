package com.vaxcare.unifiedhub.core.database.model.inventory.product

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "LegacyProductMapping")
@JsonClass(generateAdapter = true)
data class LegacyProductMappingEntity(
    @Json(name = "coreProductId")
    @PrimaryKey
    val id: Int,
    val epProductName: String,
    val epPackageId: Int,
    val epProductId: Int,
    val prettyName: String?,
    val dosesInSeries: Int?
)
