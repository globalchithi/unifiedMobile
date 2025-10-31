package com.vaxcare.unifiedhub.core.data.mapper

import com.vaxcare.unifiedhub.core.database.model.enums.PresentationDTO
import com.vaxcare.unifiedhub.core.database.model.enums.ProductCategory
import com.vaxcare.unifiedhub.core.database.model.enums.ProductStatus
import com.vaxcare.unifiedhub.core.database.model.enums.RouteCode
import com.vaxcare.unifiedhub.core.database.model.inventory.product.ProductEntity
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.model.product.Product
import com.vaxcare.unifiedhub.core.network.model.ProductCategoryDTO
import com.vaxcare.unifiedhub.core.network.model.ProductDTO
import com.vaxcare.unifiedhub.core.network.model.ProductPresentationDTO
import com.vaxcare.unifiedhub.core.network.model.ProductStatusDTO
import com.vaxcare.unifiedhub.core.network.model.RouteCodeDTO
import javax.inject.Inject

class ProductMapper @Inject constructor() {
    private val fluStatuses = listOf(
        ProductStatus.DISABLED.id,
        ProductStatus.FLU_ENABLED.id,
        ProductStatus.HISTORICAL.id,
        ProductStatus.HISTORICAL_FLU.id
    )

    fun networkToEntity(data: List<ProductDTO>) =
        data.map {
            ProductEntity(
                antigen = it.antigen,
                categoryId = ProductCategory.fromInt(it.categoryId.id),
                description = it.description,
                displayName = if (it.statusId.id !in fluStatuses) {
                    it.displayName
                } else {
                    it.prettyName
                        ?: it.displayName
                },
                id = it.id,
                inventoryGroup = it.inventoryGroup,
                lossFee = it.lossFee,
                productNdc = it.productNdc,
                routeCode = RouteCode.fromInt(it.routeCode.ordinal),
                presentation = PresentationDTO.fromInt(it.presentation.ordinal),
                purchaseOrderFee = it.purchaseOrderFee,
                visDates = it.visDates,
                status = ProductStatus.fromInt(it.statusId.id),
                prettyName = it.prettyName
            )
        }

    fun entityToNetwork(data: ProductEntity) =
        with(data) {
            ProductDTO(
                ageIndications = emptyList(),
                antigen = antigen,
                routeCode = RouteCodeDTO.fromInt(routeCode.ordinal),
                inventoryGroup = inventoryGroup,
                categoryId = ProductCategoryDTO.fromInt(categoryId.id),
                cptCvxCodes = emptyList(),
                description = description,
                id = id,
                displayName = displayName,
                statusId = ProductStatusDTO.fromInt(status.id),
                productNdc = productNdc,
                packages = emptyList(),
                presentation = ProductPresentationDTO.fromInt(presentation.ordinal),
                visDates = visDates,
                lossFee = lossFee,
                purchaseOrderFee = purchaseOrderFee,
                trackableProduct = false,
                prettyName = prettyName
            )
        }

    fun entityToDomain(data: ProductEntity?): Product? {
        if (data == null) return null
        return with(data) {
            val lossFeeInDollars = (lossFee?.toFloat() ?: 0F) / 100
            Product(
                id = id,
                antigen = antigen,
                displayName = displayName,
                presentation = Presentation.entries[presentation.ordinal],
                categoryId = categoryId.id,
                prettyName = prettyName,
                lossFee = lossFeeInDollars,
                inventoryGroup = inventoryGroup,
            )
        }
    }

    fun entityToDomain(data: List<ProductEntity>) =
        data.map {
            entityToDomain(it)
        }
}
