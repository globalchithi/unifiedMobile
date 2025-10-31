package com.vaxcare.unifiedhub.testing.testnetwork

import okhttp3.mockwebserver.MockWebServer

/**
 * Singleton holder so the same MockWebServer instance can be shared
 * between Hilt modules and TestServerRule.
 */
object MockWebServerHolder {
    val server: MockWebServer by lazy { MockWebServer() }
}
