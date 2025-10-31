package com.vaxcare.unifiedhub.core.data.network.interceptor

import com.squareup.moshi.Moshi
import com.vaxcare.unifiedhub.core.database.model.OfflineRequestEntity
import com.vaxcare.unifiedhub.core.network.model.OfflineRequestDTO
import com.vaxcare.unifiedhub.core.network.util.IGNORE_OFFLINE_STORAGE
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.Request
import okio.Buffer
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineRequestValidator @Inject constructor(
    private val moshi: Moshi,
) {
    private val interceptOfflineRequestUrls = listOf(
        OfflineRequestEntity.LOT_CREATION
    )

    fun validateRequest(request: Request): OfflineRequestDTO? {
        val requestUri = request.url.toUri().toASCIIString()
        return when {
            interceptOfflineRequestUrls.find { requestUri.contains(Regex(it)) } == null ||
                (request.header(IGNORE_OFFLINE_STORAGE) == "true") ||
                (requestUri.contains(Regex(OfflineRequestEntity.LOT_CREATION)) && request.method != "POST")
            -> null

            else -> {
                val buffer = Buffer()
                request.body?.writeTo(buffer)

                val requestBody = buffer.buffer.readUtf8()
                OfflineRequestDTO(
                    id = (requestUri + requestBody).hashCode(),
                    requestUri = requestUri,
                    requestMethod = request.method,
                    requestHeaders = moshi.adapter(Headers::class.java).toJson(request.headers),
                    contentType = moshi
                        .adapter(MediaType::class.java)
                        .toJson(request.body?.contentType()),
                    requestBody = requestBody,
                    originalDateTime = LocalDateTime.now()
                )
            }
        }
    }
}
