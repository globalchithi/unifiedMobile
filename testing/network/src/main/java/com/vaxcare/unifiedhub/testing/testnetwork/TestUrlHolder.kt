package com.vaxcare.unifiedhub.testing.testnetwork

/**
 * Singleton holder to pass the dynamic MockWebServer URL from the JUnit Rule
 * to the Hilt test module. This avoids direct dependencies and threading issues.
 */
object TestUrlHolder {
    var baseUrl: String? = null
}
