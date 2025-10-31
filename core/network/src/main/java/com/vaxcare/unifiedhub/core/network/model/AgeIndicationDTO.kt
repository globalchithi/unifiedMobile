package com.vaxcare.unifiedhub.core.network.model

data class AgeIndicationDTO(
    val id: Int,
    val gender: GenderDTO,
    val minAge: Int?,
    val maxAge: Int?,
    val productId: Int,
    val warning: AgeWarning? = null
)

data class AgeWarning(
    val title: String,
    val message: String
)
