plugins {
    alias(libs.plugins.buildlogic.android.feature)
    alias(libs.plugins.buildlogic.android.library.compose)
    alias(libs.plugins.buildlogic.android.library.jacoco)
    alias(libs.plugins.buildlogic.hilt)
}

android {
    namespace = "com.vaxcare.unifiedhub.feature.admin"
}

dependencies {
    api(projects.core.common)
    api(projects.core.data)
    api(projects.core.domain)
    api(projects.core.model)
    api(projects.core.designsystem)
    api(projects.library.vaxjob)
    api(projects.library.scanner)
    api(projects.library.analytics)

    implementation(libs.retrofit)

    implementation(libs.google.oss.licenses)
}
