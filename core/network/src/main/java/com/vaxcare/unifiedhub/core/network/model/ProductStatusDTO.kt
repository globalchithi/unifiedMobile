package com.vaxcare.unifiedhub.core.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class ProductStatusDTO(
    val id: Int
) {
    DISABLED(0),
    VACCINE_ENABLED(1),
    FLU_ENABLED(2),
    HISTORICAL(4),
    HISTORICAL_VACCINE(5),
    HISTORICAL_FLU(6);

    companion object {
        private val map = entries.associateBy(ProductStatusDTO::id)

        fun fromInt(type: Int) = map[type] ?: DISABLED
    }

    fun isHistorical(): Boolean =
        when (this) {
            HISTORICAL, HISTORICAL_VACCINE, HISTORICAL_FLU -> true
            else -> false
        }
}
