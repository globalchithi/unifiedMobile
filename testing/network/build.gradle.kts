plugins {
    alias(libs.plugins.buildlogic.android.library)
    alias(libs.plugins.buildlogic.android.library.compose)
    alias(libs.plugins.buildlogic.hilt)
}

android {
    namespace = "com.vaxcare.unifiedhub.testing.network"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.okhttp3.mockwebserver)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    api(projects.core.network)
    api(libs.google.dagger.hilt.android.testing)
}
