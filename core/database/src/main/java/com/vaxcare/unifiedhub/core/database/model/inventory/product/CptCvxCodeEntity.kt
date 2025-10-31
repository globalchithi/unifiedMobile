package com.vaxcare.unifiedhub.core.database.model.inventory.product

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "CptCvxCode")
@JsonClass(generateAdapter = true)
data class CptCvxCodeEntity(
    @PrimaryKey val cptCode: String,
    val cvxCode: String?,
    val isMedicare: Boolean,
    val productId: Int
)
