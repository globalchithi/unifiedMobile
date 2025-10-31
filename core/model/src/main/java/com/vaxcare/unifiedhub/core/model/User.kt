package com.vaxcare.unifiedhub.core.model

data class User(
    val firstName: String,
    val lastName: String,
    val pin: String,
    val userId: Int,
    val userName: String
) {
    val fullName: String
        get() = "$firstName $lastName".trim()
}
