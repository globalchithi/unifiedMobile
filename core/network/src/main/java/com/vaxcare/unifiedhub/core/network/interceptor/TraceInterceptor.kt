package com.vaxcare.unifiedhub.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.util.Random
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TraceInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain
            .request()
            .newBuilder()
            .header("traceparent", generateTraceHeader())
            .build()
        return chain.proceed(request)
    }

    private fun generateTraceHeader(): String = String.format("00-%s-%s-01", randomHexString(32), randomHexString(16))

    private fun randomHexString(len: Int): String {
        val result = StringBuilder()
        for (i in 0 until len) {
            result.append(Integer.toHexString(Random().nextInt(16)))
        }
        return result.toString()
    }
}
