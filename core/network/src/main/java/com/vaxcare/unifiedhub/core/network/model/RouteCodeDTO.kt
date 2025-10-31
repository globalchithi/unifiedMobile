package com.vaxcare.unifiedhub.core.network.model

enum class RouteCodeDTO(
    val displayName: String
) {
    IM("Intramuscular"),
    ID("Intradermal"),
    NS("Nasal"),
    PO("Periodontal"),
    SC("Subcutaneous"),
    IUD("Intrauterine"),
    IMP("Implant"),
    UNKNOWN("Unknown");

    companion object {
        private val map = entries.associateBy(RouteCodeDTO::ordinal)

        fun fromInt(type: Int) = map[type] ?: UNKNOWN
    }
}
