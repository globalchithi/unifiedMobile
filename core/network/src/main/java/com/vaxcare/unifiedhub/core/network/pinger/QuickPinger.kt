package com.vaxcare.unifiedhub.core.network.pinger

import okhttp3.Response

/**
 * An abstraction for performing simple, quick HTTP "ping" checks to determine
 * basic network connectivity to specific endpoints.
 */
interface QuickPinger {
    /**
     * Pings a generic, highly-available endpoint (like Google's) to check
     * for general internet connectivity.
     */
    fun pingGoogle(): Response

    /**
     * Pings a VaxCare-specific endpoint to check for connectivity to our services.
     */
    fun pingVaxCare(): Response
}
