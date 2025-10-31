package com.vaxcare.unifiedhub.core.database.model.inventory.product

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import com.vaxcare.unifiedhub.core.database.model.enums.Gender

@Entity(tableName = "AgeIndication")
@JsonClass(generateAdapter = true)
data class AgeIndicationEntity(
    val gender: Gender,
    @PrimaryKey(autoGenerate = false) val id: Int,
    val maxAge: Int?,
    val minAge: Int?,
    val productId: Int,
    @Embedded(prefix = "warning_") val warning: AgeWarningEntity?
)
