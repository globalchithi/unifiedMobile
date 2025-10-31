package com.vaxcare.unifiedhub.core.database.model.inventory.product

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.vaxcare.unifiedhub.core.database.model.enums.PresentationDTO
import com.vaxcare.unifiedhub.core.database.model.enums.ProductCategory
import com.vaxcare.unifiedhub.core.database.model.enums.ProductStatus
import com.vaxcare.unifiedhub.core.database.model.enums.RouteCode

@Entity(tableName = "Product")
@JsonClass(generateAdapter = true)
data class ProductEntity(
    @PrimaryKey val id: Int,
    val antigen: String,
    val categoryId: ProductCategory,
    val description: String,
    var displayName: String,
    val inventoryGroup: String,
    /**
     * The loss fee for this product in cents.
     */
    val lossFee: Int?,
    val productNdc: String?,
    var routeCode: RouteCode,
    val presentation: PresentationDTO,
    val purchaseOrderFee: Int?,
    val visDates: String,
    @Json(name = "statusId") val status: ProductStatus,
    val prettyName: String?
)
