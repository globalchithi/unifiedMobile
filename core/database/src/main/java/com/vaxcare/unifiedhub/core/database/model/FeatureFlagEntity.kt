package com.vaxcare.unifiedhub.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FeatureFlags")
data class FeatureFlagEntity(
    @PrimaryKey
    val featureFlagId: Int,
    val clinicId: Int = 0,
    val featureFlagName: String
)
