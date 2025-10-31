package com.vaxcare.unifiedhub.core.network.model

enum class GenderDTO {
    MF,
    M,
    F;

    companion object {
        private val map = entries.associateBy(GenderDTO::ordinal)

        fun fromInt(type: Int) = map[type]
    }
}
