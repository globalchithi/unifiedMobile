package com.vaxcare.unifiedhub.core.database.model.inventory

import androidx.room.Relation
import com.vaxcare.unifiedhub.core.database.model.enums.PresentationDTO
import com.vaxcare.unifiedhub.core.database.model.enums.ProductCategory
import com.vaxcare.unifiedhub.core.database.model.enums.ProductStatus
import com.vaxcare.unifiedhub.core.database.model.enums.RouteCode
import com.vaxcare.unifiedhub.core.database.model.inventory.product.AgeIndicationEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.product.CptCvxCodeEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.product.PackageEntity

data class ProductAndPackage(
    val antigen: String,
    val categoryId: ProductCategory,
    val description: String,
    var displayName: String,
    val id: Int,
    val lossFee: Int?,
    val inventoryGroup: String,
    val productNdc: String?,
    val presentation: PresentationDTO,
    val purchaseOrderFee: Int?,
    val routeCode: RouteCode,
    val status: ProductStatus,
    val visDates: String,
    @Relation(parentColumn = "id", entityColumn = "productId")
    val packages: List<PackageEntity>,
    @Relation(parentColumn = "id", entityColumn = "productId")
    val cptCvxCodes: List<CptCvxCodeEntity>,
    @Relation(parentColumn = "id", entityColumn = "productId")
    val ageIndications: List<AgeIndicationEntity>,
    val prettyName: String?
)
