package com.vaxcare.unifiedhub.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebLogger @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Timber.v("Starting Intercept")
        val request = chain.request()

        val requestTime = System.nanoTime()
        Timber.v(
            "Sending request %s on %s%n%s",
            request.url,
            chain.connection(),
            request.headers
        )

        val response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            Timber.v(e, "Request fail")
            throw e
        }

        Timber.v("Response Code: ${response.code}")

        val responseTime = System.nanoTime()
        val durationMs = "%.1f".format((responseTime - requestTime) / 1e6)
        val msg =
            "Received response for ${response.request.url} in $durationMs ms\n${response.headers}"
        Timber.v(msg)

        return response
    }
}
