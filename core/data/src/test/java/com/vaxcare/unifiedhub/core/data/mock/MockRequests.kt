package com.vaxcare.unifiedhub.core.data.mock

import com.vaxcare.unifiedhub.core.network.util.IGNORE_OFFLINE_STORAGE
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object MockRequests {
    val postLotNumberIgnoreHeader = Request
        .Builder()
        .header(IGNORE_OFFLINE_STORAGE, "true")
        .method("POST", lotNumberPost.toRequestBody("application/json".toMediaType()))
        .url("http://vhapi.vaxcare.com/api/inventory/lotnumbers")
        .build()
    val postLotNumber = Request
        .Builder()
        .method("POST", lotNumberPost.toRequestBody("application/json".toMediaType()))
        .url("http://vhapi.vaxcare.com/api/inventory/lotnumbers")
        .build()
}

private val lotNumberPost = """
    { 
        "id": 1, 
        "qualifiedLotNumber": "TESTLOT123", 
        "epProductId": 1, 
        "salesLotNumberId": -1, 
        "salesProductId": 1, 
        "source": 1 
    }
""".trimIndent()
