package com.vaxcare.unifiedhub.library.analytics.core

fun interface AnalyticsEnricher {
    suspend fun defaultProps(): AnalyticsProps
}
