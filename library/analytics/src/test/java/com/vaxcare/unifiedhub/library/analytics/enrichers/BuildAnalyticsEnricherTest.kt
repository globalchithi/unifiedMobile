package com.vaxcare.unifiedhub.library.analytics.enrichers

import android.os.Build
import com.vaxcare.unifiedhub.library.analytics.BuildConfig
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class BuildAnalyticsEnricherTest {
    private val buildAnalyticsEnricher: BuildAnalyticsEnricher = BuildAnalyticsEnricher()

    private val expectedProps = mapOf(
        "version" to BuildConfig.VERSION_CODE.toString(),
        "versionName" to BuildConfig.VERSION_NAME.toString(),
        "androidSdk" to Build.VERSION.SDK_INT.toString(),
        "androidVersion" to (Build.VERSION.RELEASE?.toString() ?: ""),
    )

    @Test
    fun `Props should match expected values`() =
        runTest {
            assertEquals(expectedProps, buildAnalyticsEnricher.defaultProps())
        }
}
