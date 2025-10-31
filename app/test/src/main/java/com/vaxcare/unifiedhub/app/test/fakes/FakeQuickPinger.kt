package com.vaxcare.unifiedhub.app.test.fakes

import com.vaxcare.unifiedhub.core.network.pinger.QuickPinger
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject

/**
 * A fake implementation of [QuickPinger] for instrumented tests.
 *
 * This class avoids real network calls, providing predictable and fast responses.
 * You can configure its behavior to simulate success or failure scenarios.
 */
class FakeQuickPinger @Inject constructor() : QuickPinger {
    // Properties to control the fake's behavior from a test
    var googlePingSucceeds: Boolean = true
    var vaxCarePingSucceeds: Boolean = true

    override fun pingGoogle(): Response =
        createFakeResponse(
            url = "http://gstatic.com/generate_204",
            success = googlePingSucceeds
        )

    override fun pingVaxCare(): Response =
        createFakeResponse(
            url = "https://test.vaxcare.com/index.txt",
            success = vaxCarePingSucceeds
        )

    private fun createFakeResponse(url: String, success: Boolean): Response {
        val request = Request.Builder().url(url).build()
        return if (success) {
            Response
                .Builder()
                .request(request)
                .protocol(Protocol.HTTP_2)
                .code(200)
                .message("OK")
                .body("OK".toResponseBody("text/plain".toMediaType()))
                .build()
        } else {
            Response
                .Builder()
                .request(request)
                .protocol(Protocol.HTTP_2)
                .code(503) // Service Unavailable, a good error for a failed ping
                .message("Service Unavailable")
                .body("".toResponseBody(null))
                .build()
        }
    }
}
