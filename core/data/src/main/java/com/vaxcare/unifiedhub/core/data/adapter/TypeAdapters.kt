package com.vaxcare.unifiedhub.core.data.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.vaxcare.unifiedhub.core.common.ext.toLocalDateTime
import com.vaxcare.unifiedhub.core.database.model.enums.ProductCategory
import com.vaxcare.unifiedhub.core.database.model.enums.ProductStatus
import com.vaxcare.unifiedhub.core.network.model.ProductCategoryDTO
import com.vaxcare.unifiedhub.core.network.model.ProductStatusDTO
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class TypeAdapters {
    @ToJson
    fun uuidToString(uuid: UUID) = uuid.toString()

    @FromJson
    fun stringToUuid(string: String): UUID = UUID.fromString(string)

    @ToJson
    fun instantToString(value: Instant): String = value.toLocalDateTime()

    @FromJson
    fun stringToInstant(string: String): Instant = Instant.parse(string)

    @FromJson
    fun stringToBigDecimal(string: String): BigDecimal = BigDecimal(string)

    @ToJson
    fun bigDecimalToString(value: BigDecimal) = value.toString()

    @ToJson
    fun productCategoryToInt(cat: ProductCategory) = cat.id

    @FromJson
    fun intToProductCategory(int: Int) = ProductCategory.fromInt(int)

    @ToJson
    fun productCategoryDTOToInt(cat: ProductCategoryDTO) = cat.id

    @FromJson
    fun intToProductCategoryDTO(int: Int) = ProductCategoryDTO.fromInt(int)

    @ToJson
    fun productStatusToInt(status: ProductStatus) = status.id

    @FromJson
    fun intToProductStatus(int: Int) = ProductStatus.fromInt(int)

    @ToJson
    fun productStatusDTOToInt(status: ProductStatusDTO) = status.id

    @FromJson
    fun intToProductStatusDTO(int: Int) = ProductStatusDTO.fromInt(int)
}
