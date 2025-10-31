package com.vaxcare.unifiedhub.core.database.model.enums

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class Gender {
    MF,
    M,
    F;

    companion object {
        private val map = entries.associateBy(Gender::ordinal)

        fun fromInt(type: Int) = map[type] ?: MF
    }
}
