package com.vaxcare.unifiedhub.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserNetworkDTO(
    @Json(name = "FirstName")
    val firstName: String?,
    @Json(name = "LastName")
    val lastName: String?,
    @Json(name = "Pin")
    val pin: String,
    @Json(name = "UserId")
    val userId: Int,
    @Json(name = "UserName")
    val userName: String?
)
