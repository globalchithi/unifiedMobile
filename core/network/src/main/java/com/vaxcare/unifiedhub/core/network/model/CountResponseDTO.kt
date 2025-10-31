package com.vaxcare.unifiedhub.core.network.model

import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class CountResponseDTO(
    val clinicId: Long,
    val createdOn: LocalDateTime,
    val guid: String,
    val stock: Int,
    val userId: Int,
    val countList: List<CountEntryResponseDTO>
)
