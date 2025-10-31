package com.vaxcare.unifiedhub.core.data.datasource

import com.vaxcare.unifiedhub.core.data.BuildConfig
import com.vaxcare.unifiedhub.core.network.di.OkHttpQuickPing
import com.vaxcare.unifiedhub.core.network.pinger.QuickPinger
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

private const val VAXCARE_URL = "${BuildConfig.VAX_VHAPI_URL}index.txt"
private const val GOOGLE_URL = "http://gstatic.com/generate_204"

class HttpQuickPing @Inject constructor(
    @OkHttpQuickPing private val client: OkHttpClient
) : QuickPinger {
    override fun pingGoogle(): Response = ping(GOOGLE_URL)

    override fun pingVaxCare(): Response = ping(VAXCARE_URL)

    private fun ping(url: String) =
        try {
            client.newCall(url.asSimpleRequest()).execute()
        } catch (e: Exception) {
            val request = url.asSimpleRequest()
            val message = e.message ?: "ping to $url failed"
            Timber.e(e, message)
            Response
                .Builder()
                .protocol(Protocol.HTTP_2)
                .request(request)
                .message(message)
                .code(500)
                .build()
        }

    private fun String.asSimpleRequest() = Request.Builder().url(this).build()
}
