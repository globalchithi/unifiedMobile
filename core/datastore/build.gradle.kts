plugins {
    alias(libs.plugins.buildlogic.android.library)
    alias(libs.plugins.buildlogic.android.library.jacoco)
    alias(libs.plugins.buildlogic.hilt)
}

android {
    namespace = "com.vaxcare.unifiedhub.core.datastore"

    defaultConfig {
        buildConfigField(
            "String",
            "SCANNER_CUSTOMER_ID",
            "\"${findProperty("offlineScannerCustomerId") as String}\""
        )

        buildConfigField(
            "String",
            "SCANNER_LICENSE",
            "\"${findProperty("offlineScannerKey") as String}\""
        )
        buildConfigField(
            type = "String",
            name = "DATADOG_CLIENT_TOKEN",
            value = "\"${project.findProperty("dataDogClientToken")}\""
        )
        buildConfigField(
            type = "String",
            name = "DATADOG_APPLICATION_ID",
            value = "\"${project.findProperty("dataDogApplicationId")}\""
        )
        buildConfigField(
            type = "String",
            name = "DATADOG_SITE",
            value = "\"${project.findProperty("dataDogSite")}\""
        )
        buildConfigField(
            type = "String",
            name = "DATADOG_RUM_SAMPLING_RATE",
            value = "\"${project.findProperty("dataDogRumSampleRate")}\""
        )
        buildConfigField(
            type = "String",
            name = "DATADOG_SESSION_REPLAY_SAMPLING_RATE",
            value = "\"${project.findProperty("dataDogSessionReplaySampleRate")}\""
        )
    }
}

dependencies {
    implementation(libs.androidx.datastore)
}
