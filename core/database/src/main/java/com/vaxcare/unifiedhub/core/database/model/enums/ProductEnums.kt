package com.vaxcare.unifiedhub.core.database.model.enums

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class ProductCategory(
    val id: Int
) {
    UNKNOWN(0),
    SUPPLY(1),
    VACCINE(2),
    LARC(3);

    companion object {
        private val map = entries.associateBy(ProductCategory::id)

        fun fromInt(type: Int) = map[type] ?: UNKNOWN
    }
}

@JsonClass(generateAdapter = false)
enum class ProductStatus(
    val id: Int
) {
    DISABLED(0),
    VACCINE_ENABLED(1),
    FLU_ENABLED(2),
    HISTORICAL(4),
    HISTORICAL_VACCINE(5),
    HISTORICAL_FLU(6);

    companion object {
        private val map = entries.associateBy(ProductStatus::id)

        fun fromInt(type: Int) = map[type] ?: DISABLED
    }

    fun isHistorical(): Boolean =
        when (this) {
            HISTORICAL, HISTORICAL_VACCINE, HISTORICAL_FLU -> true
            else -> false
        }
}

@JsonClass(generateAdapter = false)
enum class RouteCode(
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
        private val map = entries.associateBy(RouteCode::ordinal)

        fun fromInt(type: Int) = map[type] ?: UNKNOWN

        fun fromString(name: String?): RouteCode? =
            when (name) {
                IM.displayName -> IM
                ID.displayName -> ID
                NS.displayName -> NS
                PO.displayName -> PO
                SC.displayName -> SC
                IUD.displayName -> IUD
                IMP.displayName -> IMP
                UNKNOWN.displayName -> UNKNOWN
                else -> null
            }
    }
}

@JsonClass(generateAdapter = false)
enum class PresentationDTO(
    val longName: String,
    val displayName: String,
    val shortName: String
) {
    @Json(name = "Single Dose Vial")
    SINGLE_DOSE_VIAL(
        longName = "Single Dose Vial",
        displayName = "Single-dose Vial",
        shortName = "SDV"
    ),

    @Json(name = "Single Dose Tube")
    SINGLE_DOSE_TUBE(
        longName = "Single Dose Tube",
        displayName = "Oral",
        shortName = "SDT"
    ),

    @Json(name = "Multi Dose Vial")
    MULTI_DOSE_VIAL(
        longName = "Multi Dose Vial",
        displayName = "Multi-dose Vial",
        shortName = "MDV"
    ),

    @Json(name = "Prefilled Syringe")
    PREFILLED_SYRINGE(
        longName = "Prefilled Syringe",
        displayName = "Pre-filled Syringe",
        shortName = "PFS"
    ),

    @Json(name = "Nasal Spray")
    NASAL_SPRAY(
        longName = "Nasal Spray",
        displayName = "Nasal Spray",
        shortName = "NSP"
    ),

    @Json(name = "Nasal Syringe")
    NASAL_SYRINGE(
        longName = "Nasal Syringe",
        displayName = "Nasal Syringe",
        shortName = "NSY"
    ),

    @Json(name = "IUD")
    IUD(
        longName = "Intrauterine Device",
        displayName = "IUD",
        shortName = "IUD"
    ),

    @Json(name = "Implant")
    IMPLANT(
        longName = "Etonogestrel Single-Rod Contraceptive Implant",
        displayName = "Implant",
        shortName = "IMP"
    ),

    UNKNOWN(
        longName = "Unknown",
        displayName = "Single-dose Vial",
        shortName = "UNK"
    );

    companion object {
        private val map = entries.associateBy(PresentationDTO::ordinal)

        fun fromInt(type: Int) = map[type] ?: UNKNOWN
    }
}
