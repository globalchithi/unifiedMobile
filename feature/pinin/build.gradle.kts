plugins {
    alias(libs.plugins.buildlogic.android.feature)
    alias(libs.plugins.buildlogic.android.library.compose)
    alias(libs.plugins.buildlogic.hilt)
}

android {
    namespace = "com.vaxcare.unifiedhub.feature.pinin"
}

dependencies {
    api(projects.core.data)
    api(projects.core.model)
    api(projects.library.analytics)
}
