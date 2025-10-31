package com.vaxcare.unifiedhub.core.network.model

import com.squareup.moshi.JsonClass
import java.time.LocalDate

data class ClinicDTO(
    val id: Long,
    val name: String,
    val state: String,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val type: ClinicType,
    val locationId: Long,
    val locationNodeId: String,
    val isIntegrated: Boolean,
    val parentClinicId: Long,
    val isSchoolCaresEnabled: Boolean,
    val temporaryClinicType: String?,
) {
    @JsonClass(generateAdapter = false)
    enum class ClinicType {
        Permanent,
        Temporary
    }
}
