package com.vaxcare.unifiedhub.core.database.model.inventory.product

import androidx.room.Entity
import com.squareup.moshi.JsonClass

@Entity(tableName = "AgeWarning")
@JsonClass(generateAdapter = true)
data class AgeWarningEntity(
    val title: String,
    val message: String
)
