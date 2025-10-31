package com.vaxcare.unifiedhub.core.network.model

enum class ProductCategoryDTO(
    val id: Int
) {
    UNKNOWN(0),
    SUPPLY(1),
    VACCINE(2),
    LARC(3);

    companion object {
        private val map = entries.associateBy(ProductCategoryDTO::id)

        fun fromInt(type: Int) = map[type] ?: UNKNOWN
    }
}
