package com.vaxcare.unifiedhub.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class ProductPresentationDTO(
    val longName: String,
    val displayName: String,
    val shortName: String,
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
        fun fromInt(ordinal: Int) = entries[ordinal]
    }
}
