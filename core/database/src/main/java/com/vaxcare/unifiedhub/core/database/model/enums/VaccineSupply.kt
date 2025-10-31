package com.vaxcare.unifiedhub.core.database.model.enums

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
sealed class VaccineSupply(
    val id: Int,
    val name: String,
    val displayName: String
) {
    data object Private : VaccineSupply(1, "Private", "Private")

    data object Vfc : VaccineSupply(2, "Vfc", "VFC")

    data object State : VaccineSupply(3, "State", "State")

    data object Section317 : VaccineSupply(4, "Section317", "317")

    companion object {
        fun fromInt(id: Int) =
            when (id) {
                2 -> Vfc
                3 -> State
                4 -> Section317
                else -> Private
            }

        fun fromName(name: String) =
            when (name.uppercase()) {
                "VFC" -> Vfc
                "STATE" -> State
                "SECTION317" -> Section317
                else -> Private
            }
    }
}
