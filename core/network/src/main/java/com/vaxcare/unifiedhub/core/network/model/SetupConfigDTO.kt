package com.vaxcare.unifiedhub.core.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SetupConfigDTO(
    val codeCorpLicense: CodeCorpLicenseDTO?,
    val dataDogLicense: DataDogLicenseDTO?,
    val severity: UpdateSeverityDTO
)
