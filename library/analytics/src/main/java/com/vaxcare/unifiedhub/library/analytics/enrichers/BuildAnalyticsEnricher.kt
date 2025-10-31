package com.vaxcare.unifiedhub.library.analytics.enrichers

import android.os.Build
import com.vaxcare.unifiedhub.library.analytics.BuildConfig
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsEnricher
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps
import javax.inject.Inject

class BuildAnalyticsEnricher @Inject constructor() : AnalyticsEnricher {
    override suspend fun defaultProps(): AnalyticsProps =
        mapOf(
            "version" to BuildConfig.VERSION_CODE.toString(),
            "versionName" to BuildConfig.VERSION_NAME.toString(),
            "androidSdk" to Build.VERSION.SDK_INT.toString(),
            // When testing Build.VERSION.RELEASE is null
            "androidVersion" to (Build.VERSION.RELEASE?.toString() ?: ""),
        )
}
