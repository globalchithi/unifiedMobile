plugins {
    alias(libs.plugins.buildlogic.android.feature)
    alias(libs.plugins.buildlogic.android.library.compose)
    alias(libs.plugins.buildlogic.hilt)
}

android {
    namespace = "com.vaxcare.unifiedhub.feature.home"
}

dependencies {
    api(projects.library.analytics)
    api(projects.core.common)
    api(projects.core.data)
    api(projects.core.domain)

    implementation(libs.google.android.play.app.update)
}
