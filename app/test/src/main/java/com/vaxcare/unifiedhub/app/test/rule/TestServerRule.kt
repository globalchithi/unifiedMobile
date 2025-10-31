package com.vaxcare.unifiedhub.app.test.rule

import com.vaxcare.unifiedhub.testing.testdata.TestDataLoader
import com.vaxcare.unifiedhub.testing.testnetwork.MockWebServerHolder
import com.vaxcare.unifiedhub.testing.testnetwork.TestUrlHolder
import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import timber.log.Timber

/**
 * JUnit4 rule that spins up a **MockWebServer** on a random port and provides
 * a tiny DSL to stub endpoints.
 *
 * Usage in a test:
 *
 * ```kotlin
 * @get:Rule val server = TestServerRule() // VIL: Very Important Line ⭐️
 *
 * server.get("/api/setup/LocationData") {
 *     bodyJson("setup/location_data_ok.json")
 * }
 * ```
 */
class TestServerRule(
    private val schemaValidator: ((path: String, body: String) -> Unit)? = null
) : TestWatcher() {
    val server: MockWebServer = MockWebServerHolder.server

    private val stubs = mutableMapOf<Pair<String, String>, MockResponse>()

    lateinit var baseUrl: HttpUrl
        private set

    // ---------- Lifecycle ----------

    override fun starting(description: Description) {
        stubs.clear()
        server.dispatcher = dispatcher
        server.start(0)

        val resolvedUrl = server.url("/")

        TestUrlHolder.baseUrl = resolvedUrl.toString()
        baseUrl = resolvedUrl

        Timber.i("🟢 TestServer started at: $baseUrl")
    }

    override fun finished(description: Description) {
        server.shutdown()
        TestUrlHolder.baseUrl = null
        Timber.i("🔴 TestServer shutting down")
    }

    enum class HTTPMethod(val id: String) {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE"),
        PATCH("PATCH")
    }

    // ---------- Centralized Dispatcher ----------

    /**
     * Stubs [method] + [path] with a custom [code], [headers] and [bodyProvider].
     * The [path] should be **path only**, e.g. `/api/setup/LocationData`.
     */
    private val dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val reqPath = request.requestUrl?.encodedPath ?: request.path!!
            val method = request.method!!
            // Ignore query params
            val key = method to reqPath.substringBefore("?")

            Timber.i(
                "➡️ Request received: %s %s\nHeaders: %s\nBody: %s",
                method,
                reqPath,
                request.headers,
                request.body.readUtf8()
            )

            return stubs[key]?.let { response ->
                Timber.i(
                    "✅ Matched stub for: %s %s, responding with %s\nResponse Body: %s",
                    method,
                    reqPath,
                    response,
                    response.getBody()?.readUtf8() ?: "[no body]"
                )
                response
            } ?: run {
                Timber.e(
                    "⚠️ No match found for request: %s %s. Known stubs: %s",
                    method,
                    reqPath,
                    stubs.keys
                )
                MockResponse().setResponseCode(404).setBody("No stub found for $method $reqPath")
            }
        }
    }

    // ---------- DSL to add stubs ----------

    private fun stub(
        method: HTTPMethod,
        path: String,
        code: Int = 200,
        headers: Map<String, String> = emptyMap(),
        bodyProvider: () -> String
    ) {
        val body = bodyProvider()
        schemaValidator?.invoke(path, body)

        val response = MockResponse()
            .setResponseCode(code)
            .setBody(body)
            .addHeader("Content-Type", "application/json")
            .apply { headers.forEach { (k, v) -> addHeader(k, v) } }

        val key = method.id to path.substringBefore("?")
        stubs[key] = response

        Timber.i(
            "📝 Stubbed %s %s with code %d\nBody has %d chars",
            method,
            path,
            code,
            body.length
        )
    }

    fun get(
        path: String,
        code: Int = 200,
        headers: Map<String, String> = emptyMap(),
        body: () -> String
    ) = stub(method = HTTPMethod.GET, path = path, code = code, headers = headers, bodyProvider = body)

    fun post(
        path: String,
        code: Int = 200,
        headers: Map<String, String> = emptyMap(),
        body: () -> String
    ) = stub(method = HTTPMethod.POST, path = path, code = code, headers = headers, bodyProvider = body)

    fun put(
        path: String,
        code: Int = 200,
        headers: Map<String, String> = emptyMap(),
        body: () -> String
    ) = stub(method = HTTPMethod.PUT, path = path, code = code, headers = headers, bodyProvider = body)

    fun delete(
        path: String,
        code: Int = 200,
        headers: Map<String, String> = emptyMap(),
        body: () -> String
    ) = stub(method = HTTPMethod.DELETE, path = path, code = code, headers = headers, bodyProvider = body)

    fun patch(
        path: String,
        code: Int = 200,
        headers: Map<String, String> = emptyMap(),
        body: () -> String
    ) = stub(method = HTTPMethod.PATCH, path = path, code = code, headers = headers, bodyProvider = body)

    // ---------- Helpers ----------

    /** Quickly load a JSON asset from `core:testdata` resources. */
    fun bodyJson(assetPath: String): String {
        Timber.d("📄 Loading JSON asset: %s", assetPath)
        return TestDataLoader.loadJson(assetPath)
    }
}
