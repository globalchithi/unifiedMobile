plugins {
    alias(libs.plugins.buildlogic.android.library)
    alias(libs.plugins.buildlogic.android.library.jacoco)
    alias(libs.plugins.buildlogic.hilt)
    alias(libs.plugins.datadog)
    alias(libs.plugins.google.gms)
}

android {
    namespace = "com.vaxcare.unifiedhub.library.analytics"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(libs.datadog.compose)
    implementation(libs.datadog.rum)
    implementation(libs.datadog.logs)
    implementation(libs.datadog.timber)
    implementation(libs.datadog.session.replay)
    implementation(libs.datadog.session.replay.material)
    implementation(libs.datadog.session.replay.compose)
    implementation(libs.datadog.okhttp)
    implementation(libs.datadog.ndk)
    implementation(libs.mixpanel.android)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
