package com.vaxcare.unifiedhub.core.database.model.inventory

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "WrongProductNdc")
data class WrongProductNdcEntity(
    @PrimaryKey val ndc: String,
    val errorMessage: String
)
