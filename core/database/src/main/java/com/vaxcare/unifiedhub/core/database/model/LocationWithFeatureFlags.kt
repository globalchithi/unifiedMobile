package com.vaxcare.unifiedhub.core.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class LocationWithFeatureFlags(
    @Embedded
    val location: LocationEntity,
    @Relation(
        parentColumn = "clinicId",
        entityColumn = "clinicId"
    )
    val featureFlag: List<FeatureFlagEntity>
)
