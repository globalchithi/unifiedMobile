package com.vaxcare.unifiedhub.core.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LegacyProductMappingDTO(
    val coreProductId: Int,
    val epProductName: String,
    val epPackageId: Int,
    val epProductId: Int,
    val prettyName: String?,
    val dosesInSeries: Int?
)
