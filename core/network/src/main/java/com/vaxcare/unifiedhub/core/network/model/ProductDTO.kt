package com.vaxcare.unifiedhub.core.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductDTO(
    val ageIndications: List<AgeIndicationDTO>,
    val antigen: String,
    val routeCode: RouteCodeDTO,
    val inventoryGroup: String,
    val categoryId: ProductCategoryDTO,
    val cptCvxCodes: List<CptCvxCodeDTO>,
    val description: String,
    val id: Int,
    val displayName: String,
    val statusId: ProductStatusDTO,
    val productNdc: String?,
    val packages: List<PackageDTO>,
    val presentation: ProductPresentationDTO,
    val visDates: String,
    val lossFee: Int?,
    val purchaseOrderFee: Int?,
    val trackableProduct: Boolean,
    var prettyName: String?
)
