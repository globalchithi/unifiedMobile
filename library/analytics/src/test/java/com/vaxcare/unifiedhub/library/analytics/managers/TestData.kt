package com.vaxcare.unifiedhub.library.analytics.managers

import com.datadog.android.DatadogSite
import com.vaxcare.unifiedhub.core.network.model.FeatureFlagDTO

val testAttributes: DatadogManager.Attributes = DatadogManager.Attributes(
    serialNumber = "1234",
    imei = "abc",
    iccid = "efg",
    featureFlags = mapOf("ff1" to true),
    userId = "1",
    userName = "testUser",
    pid = "1",
    cid = "1"
)

val testSettings: DatadogManager.DatadogSettings = DatadogManager.DatadogSettings(
    clientToken = "token",
    applicationId = "appId",
    rumSampleRate = 100F,
    sessionReplaySampleRate = 100F,
    site = DatadogSite.US3,
    enabled = true,
    haveDatadogSettingsChanged = true
)

val featureFlags = listOf(
    FeatureFlagDTO(
        featureFlagId = 1,
        clinicId = 1,
        featureFlagName = "ff1"
    )
)
