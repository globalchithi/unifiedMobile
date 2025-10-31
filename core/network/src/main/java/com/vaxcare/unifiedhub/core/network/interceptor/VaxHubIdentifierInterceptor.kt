package com.vaxcare.unifiedhub.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class VaxHubIdentifierInterceptor @Inject constructor(
    private val identifierProvider: IdentifierProvider,
) : Interceptor {
    companion object {
        private const val IDENTIFIER_HEADER = "X-VaxHub-Identifier"
        private const val AI_TRACER_HEADER = "traceparent"
        private const val MOBILE_DATA_HEADER = "MobileData"
        private const val USER_SESSION_ID = "UserSessionId"
        private const val MESSAGE_SOURCE = "MessageSource"
        private const val UNIFIED_HUB = "UnifiedHub"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain
            .request()
            .newBuilder()
            .header(IDENTIFIER_HEADER, identifierProvider.header())
            .header(AI_TRACER_HEADER, identifierProvider.correlationId())
            .header(MOBILE_DATA_HEADER, identifierProvider.isCellular().toString())
            .header(USER_SESSION_ID, identifierProvider.sessionId())
            .header(MESSAGE_SOURCE, UNIFIED_HUB)
            .build()
        return chain.proceed(request)
    }
}
