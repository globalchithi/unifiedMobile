package com.vaxcare.unifiedhub.core.network.model

data class PostDTO<T>(
    val androidSdk: Int,
    val androidVersion: String,
    val assetTag: String = "-1",
    val clinicId: Long,
    val deviceSerialNumber: String,
    val key: String,
    val payload: T,
    val version: Int,
    val versionName: String,
    val userName: String,
    val userId: Int
)
