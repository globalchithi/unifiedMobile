package com.vaxcare.unifiedhub.core.data.mapper

import com.vaxcare.unifiedhub.core.database.model.FeatureFlagEntity
import com.vaxcare.unifiedhub.core.network.model.FeatureFlagDTO
import javax.inject.Inject

class FeatureFlagMapper @Inject constructor() {
    fun networkToEntity(data: List<FeatureFlagDTO>) =
        data.map {
            FeatureFlagEntity(
                featureFlagId = it.featureFlagId,
                clinicId = it.clinicId,
                featureFlagName = it.featureFlagName
            )
        }

    fun entityToDomain(data: List<FeatureFlagEntity>) =
        data.map {
            FeatureFlagDTO(
                featureFlagId = it.featureFlagId,
                clinicId = it.clinicId,
                featureFlagName = it.featureFlagName
            )
        }
}
